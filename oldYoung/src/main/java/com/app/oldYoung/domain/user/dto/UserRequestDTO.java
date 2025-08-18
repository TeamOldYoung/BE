package com.app.oldYoung.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class LoginRequestDTO {

        private String email;
        
        private String password;
    }
}