package com.app.oldYoung.domain.user.dto;

public class UserRequestDTO {

    public record LoginRequestDTO(
        String email,
        String password
    ) {}
}