package com.rai.movieapi.controllers;

import com.rai.movieapi.auth.entities.RefreshToken;
import com.rai.movieapi.auth.entities.User;
import com.rai.movieapi.auth.service.JwtService;
import com.rai.movieapi.auth.service.RefreshTokenService;
import com.rai.movieapi.auth.utils.AuthResponse;
import com.rai.movieapi.auth.utils.LoginRequest;
import com.rai.movieapi.auth.utils.RefreshTokenRequest;
import com.rai.movieapi.auth.utils.RegisterRequest;
import com.rai.movieapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
       RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
       User user = refreshToken.getUser();

       String accessToken = jwtService.generateToken(user);

       return ResponseEntity.ok(AuthResponse.builder()
               .accessToken(accessToken)
               .refreshToken(refreshTokenRequest.getRefreshToken())
               .build());
    }
}
