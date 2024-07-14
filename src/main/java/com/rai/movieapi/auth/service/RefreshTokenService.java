package com.rai.movieapi.auth.service;

import com.rai.movieapi.auth.entities.RefreshToken;
import com.rai.movieapi.auth.entities.User;
import com.rai.movieapi.auth.repositories.RefreshTokenRepository;
import com.rai.movieapi.auth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + username));

        RefreshToken refreshToken = new RefreshToken();

        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(5 * 60 * 60 * 10000))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new RuntimeException("Refresh token is not found!"));

        if(refToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh Token is expired !");
        }
        return refToken;
    }
}
