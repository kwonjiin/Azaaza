package com.azaaza.habitpet.dto.response;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {

    public static LoginResponse of(String accessToken, long expiresInSeconds) {
        return new LoginResponse(accessToken, "Bearer", expiresInSeconds);
    }
}
