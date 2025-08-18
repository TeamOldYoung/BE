package com.app.oldYoung.domain.chatAI.controller;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.app.oldYoung.domain.chatAI.service.ChatService;
import com.app.oldYoung.domain.user.entity.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  @GetMapping("/start/{userId}")
  public ResponseEntity<ChatMessage.Res> start(@PathVariable Long userId) {
    chatService.startNewSession(userId);
    ChatMessage.Res response = new ChatMessage.Res(
        "안녕하세요, 하루미에요!😉\n" +
            "어르신을 위한 건강·복지 정보를 쉽게 알려드릴게요.\n\n" +
            "원하는 정보를 물어봐주세요!"
    );
    return ResponseEntity.ok(response);
  }

  // 채팅
  @PostMapping("/chat/{userId}")
  public ResponseEntity<ChatMessage.Res> chat(@PathVariable Long userId,
      @RequestBody ChatMessage.Req req) {
    String answer = chatService.reply(userId, req.message());
    ChatMessage.Res response = new ChatMessage.Res(answer);
    return ResponseEntity.ok(response);
  }

}
