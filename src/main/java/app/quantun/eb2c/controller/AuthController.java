package app.quantun.eb2c.controller;


import app.quantun.eb2c.config.security.JwtTokenProvider;
import app.quantun.eb2c.model.contract.LoginRequest;
import app.quantun.eb2c.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication management.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Operations for user authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Operation(summary = "Authenticate user and generate JWT token", 
              description = "Validates user credentials and returns a JWT token for API authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", 
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "Login credentials", required = true) 
            @Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);

        return ResponseEntity.ok(response);
    }
}