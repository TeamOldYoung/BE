package com.app.oldYoung.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

public class UserResponseDTO {

    @Getter
    @Builder
    public static class JoinResultDTO {

        private Long userId;
        
        private String email;
        
        private String membername;
    }
}