package com.azaaza.habitpet.dto.response;

import com.azaaza.habitpet.domain.user.User;

public record SignupResponse(Long id, String email, String nickname) {

    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
