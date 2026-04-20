package com.payable.demo.controller;

import com.payable.demo.dto.ApiErrorResponse;
import com.payable.demo.dto.AuthResponse;
import com.payable.demo.dto.LoginRequest;
import com.payable.demo.dto.RegisterRequest;
import com.payable.demo.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthenticationController {

    private final IAuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new user and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Successfully registered",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "409", description = "User already exists",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
