package dev.tuvumba.azul_test_task.controllers;

import dev.tuvumba.azul_test_task.config.exceptions.ApiErrorResponse;
import dev.tuvumba.azul_test_task.domain.users.User;
import dev.tuvumba.azul_test_task.domain.users.UserRole;
import dev.tuvumba.azul_test_task.repository.UserRepository;
import dev.tuvumba.azul_test_task.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "API for managing login")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Login Okay", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content())})
    @Operation(summary = "Login to receive a token.",
            description = "If the username and password are right, the user will receive their JWT Bearer token. Available to all.")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest)
    {
        Optional<User> user = userRepository.findById(loginRequest.getUsername());
        if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {

            String token = jwtUtils.generateToken(loginRequest.username,
                    List.of(user.get().getRole().toString()));

            logger.info("Login successful for user {}", loginRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(loginRequest.username, token, user.get().getRole().toString()));
        }
        else
        {
            throw new RuntimeException("Invalid username or password.");
        }
    }


    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details. Available only to ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered the user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest)
    {
        try {
            User newUser = new User(registerRequest.username,
                    registerRequest.firstName,
                    registerRequest.lastName,
                    registerRequest.email,
                    registerRequest.password,
                    UserRole.valueOf(registerRequest.role));
            userRepository.save(newUser);
            logger.info("User {} registered successfully with role {}", registerRequest.username, registerRequest.role);
            return ResponseEntity.ok(new RegisterResponse(registerRequest.username, registerRequest.role));
        }
        catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            throw new RuntimeException("Registration failed.", e);
        }
    }


    /*
        In a more sophisticated rendition on this project, we may expand those to their separate classes.
        For simplicity, I decided to keep them as static classes directly inside the AuthController.
     */

    @Setter
    @Getter
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class RegisterRequest {
        @Schema(example = "johndeer")
        private String username;
        @Schema(example = "John")
        private String firstName;
        @Schema(example = "Deer")
        private String lastName;
        @Schema(example = "atotallyrealemail@gmail.com")
        private String email;
        @Schema(example = "averygoodpassword")
        private String password;
        @Schema(example = "USER")
        private String role;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class RegisterResponse {
        private String username;
        private String role;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class AuthResponse {
        private String username;
        private String token;
        private String role;
    }
}
