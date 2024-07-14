package com.rai.movieapi.auth.utils;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
