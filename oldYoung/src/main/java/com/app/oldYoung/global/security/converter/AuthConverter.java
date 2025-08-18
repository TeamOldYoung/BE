package com.app.oldYoung.global.security.converter;

import com.app.oldYoung.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthConverter {

    public static User toUser(String email, String membername, String password, String providerId,
        PasswordEncoder passwordEncoder) {
        return User.builder()
            .email(email)
            .membername(membername)
            .password(password != null ? passwordEncoder.encode(password) : null)
            .provider("kakao")
            .providerId(providerId)
            .build();
    }
}
