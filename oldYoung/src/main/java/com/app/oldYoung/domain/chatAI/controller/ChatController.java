package com.app.oldYoung.domain.chatAI.controller;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.app.oldYoung.domain.chatAI.service.ChatService;
import com.app.oldYoung.global.common.apiResponse.response.ApiResponse;
import com.app.oldYoung.global.security.dto.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/harume")
@Tag(name = "Chat API", description = "AI 채팅 서비스 API")
public class ChatController {

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @GetMapping("/start")
  @Operation(summary = "채팅 세션 시작", description = "새로운 채팅 세션을 시작하고 초기 메시지를 반환합니다")
  public ResponseEntity<ApiResponse<ChatMessage.Res>> start(@AuthenticationPrincipal UserPrincipal userPrincipal) {
    Long userId = userPrincipal.getId();
    chatService.startNewSession(userId);
    ChatMessage.Res response = new ChatMessage.Res(
        "안녕하세요, 하루미에요!😉\n" +
            "어르신을 위한 건강·복지 정보를 쉽게 알려드릴게요.\n\n" +
            "원하는 정보를 물어봐주세요!"
    );
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/chat")
  @Operation(summary = "AI 채팅", description = "사용자 메시지를 받아 AI 응답을 반환합니다")
  public ResponseEntity<ApiResponse<ChatMessage.Res>> chat(@AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody ChatMessage.Req req) {
    Long userId = userPrincipal.getId();
    String answer = chatService.reply(userId, req.message());
    ChatMessage.Res response = new ChatMessage.Res(answer);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

}
