package com.app.oldYoung.domain.user.dto;

public class UserResponseDTO {

    public record JoinResultDTO(
        Long userId,
        String email,
        String membername
    ) {}

    public record UserMyPageResponseDTO(
        String membername,
        Long incomeBracket,
        Long expBracket,
        String birthDate,
        String email
    ) {}
}