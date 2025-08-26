package com.app.oldYoung.domain.chatAI.controller;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.app.oldYoung.domain.chatAI.service.ChatService;
import com.app.oldYoung.global.common.apiResponse.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/harume")
public class ChatController {

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  // 시작 메시지
  @GetMapping("/start")
  public ResponseEntity<ApiResponse<ChatMessage.Res>> start(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = Long.valueOf(userDetails.getUsername());
    chatService.startNewSession(userId);
    ChatMessage.Res response = new ChatMessage.Res(
        "안녕하세요, 하루미에요!😉\n" +
            "어르신을 위한 건강·복지 정보를 쉽게 알려드릴게요.\n\n" +
            "원하는 정보를 물어봐주세요!"
    );
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  // 채팅
  @PostMapping("/chat")
  public ResponseEntity<ApiResponse<ChatMessage.Res>> chat(@AuthenticationPrincipal UserDetails userDetails,
      @RequestBody ChatMessage.Req req) {
    Long userId = Long.valueOf(userDetails.getUsername());
    String answer = chatService.reply(userId, req.message());
    ChatMessage.Res response = new ChatMessage.Res(answer);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

}
