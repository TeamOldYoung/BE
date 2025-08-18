package com.app.oldYoung.domain.user.converter;

import com.app.oldYoung.domain.user.dto.UserResponseDTO;
import com.app.oldYoung.domain.user.entity.User;

public class UserConverter {

    public static UserResponseDTO.JoinResultDTO toJoinResultDTO(User user) {
        return UserResponseDTO.JoinResultDTO.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .membername(user.getMembername())
            .build();
    }
}