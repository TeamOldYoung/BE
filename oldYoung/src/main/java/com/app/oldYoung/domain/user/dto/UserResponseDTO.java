package com.app.oldYoung.domain.user.dto;

public class UserResponseDTO {

    public record JoinResultDTO(
        Long userId,
        String email,
        String membername
    ) {}
}