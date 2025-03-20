package dev.tuvumba.azul_test_task.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Autowired
    public JwtFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();


        // allow requests for login and docs.
        if (requestUri.startsWith("/auth/login") || requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3")) {
            filterChain.doFilter(request, response);
            return;
        }

        // can't get the correct Bearer Auth header.
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Missing or malformed Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        // get the token itself
        String token = extractToken(authHeader);

        try {
            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsername(token);
                List<String> roles = jwtUtils.extractRoles(token);
                logger.debug("JWT valid. Username:" + username + ", Roles: " + roles);


                // construct needed authorities from the roles we got from the token
                // note to self: spring treats hasRole('ADMIN') as hasAuthority('ROLE_ADMIN')

                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("User " + username + " authenticated successfully");
            } else {
                logger.warn("JWT validation failed");
            }
        } catch (Exception e) {
            logger.error("Authentication failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or expired token");
            return;
        }
        filterChain.doFilter(request, response);
    }

    // 'Bearer ' + token
    private String extractToken(String authHeader) {
        if (authHeader == null || authHeader.length() < 8) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        // get rid of 'Bearer '
        String token = authHeader.substring(7);

        // if token is wrapped in {}, assume it's a JSON-like format
        // {"token":"actualtoken"}
        if (token.startsWith("{") && token.endsWith("}")) {
            int start = token.indexOf("\"token\":\"") + 9; // get rid of "token":"
            int end = token.indexOf("\"", start);
            if (start < 9 || end == -1) {
                throw new IllegalArgumentException("Invalid token format");
            }
            return token.substring(start, end);
        }
        return token;
    }

}
