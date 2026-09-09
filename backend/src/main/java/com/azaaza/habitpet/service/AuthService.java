package com.azaaza.habitpet.service;

import com.azaaza.habitpet.domain.user.User;
import com.azaaza.habitpet.dto.request.LoginRequest;
import com.azaaza.habitpet.dto.request.SignupRequest;
import com.azaaza.habitpet.dto.response.LoginResponse;
import com.azaaza.habitpet.dto.response.SignupResponse;
import com.azaaza.habitpet.global.exception.BusinessException;
import com.azaaza.habitpet.global.exception.ErrorCode;
import com.azaaza.habitpet.global.security.JwtTokenProvider;
import com.azaaza.habitpet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        return SignupResponse.from(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return LoginResponse.of(token, jwtTokenProvider.getExpirationSeconds());
    }
}
