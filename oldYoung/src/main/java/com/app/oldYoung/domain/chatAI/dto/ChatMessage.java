package com.app.oldYoung.domain.chatAI.dto;

public record ChatMessage(String role, String content) {

  public static ChatMessage user(String content) {
    return new ChatMessage("user", content);
  }

  // assistant : 챗봇 모델
  public static ChatMessage assistant(String content) {
    return new ChatMessage("assistant", content);
  }

  // 요청 DTO
  public record Req(String message) {}

  // 응답 DTO
  public record Res(String message) {}
}

