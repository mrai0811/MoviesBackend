package com.rai.movieapi.auth.repositories;

import com.rai.movieapi.auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
