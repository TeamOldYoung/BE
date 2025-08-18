package com.app.oldYoung.domain.chatAI.controller;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/harume")
public class ChatController {

  private final ChatClient chatClient;

  public ChatController(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  // 시작 메시지
  @GetMapping("/start")
  public ChatMessage.Res start() {
    return new ChatMessage.Res(
        "안녕하세요, 하루미에요!😉\n" +
            "어르신을 위한 건강·복지 정보를 쉽게 알려드릴게요.\n\n" +
            "원하는 정보를 물어봐주세요!"
    );
  }

  // 채팅
  @PostMapping("/chat/{userId}")
  public ChatMessage.Res chat(@PathVariable Long userId,
      @RequestBody ChatMessage.Req req) {

    String answer = chatClient
        .prompt()
        .system("너는 노인들의 복지 도우미 챗봇이다. 대답은 존댓말을 사용하고, 진실되게 대답해야한다.")
        .user(req.message())
        .call()
        .content();

    return new ChatMessage.Res(answer);
  }

}
