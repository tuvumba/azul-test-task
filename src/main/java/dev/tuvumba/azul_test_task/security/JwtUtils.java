package dev.tuvumba.azul_test_task.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 *  An utilities class for JWT token creation/verification
 */
@Component
@Getter
public class JwtUtils {
    private final SecretKey secretKey;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, List<String> roles) {
        logger.debug("Generating token, username: {}, roles: {}", username, roles);
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            logger.debug("Validating token: {}", token);
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            logger.error("Invalid token: {}", token);
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        try
        {
            return claims.get("roles", List.class);
        } catch (Exception e) {
            throw new JwtException("Invalid token roles: " + e.getMessage());
        }
    }
}
