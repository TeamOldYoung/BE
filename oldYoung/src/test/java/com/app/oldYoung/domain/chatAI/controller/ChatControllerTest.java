package com.app.oldYoung.domain.chatAI.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.app.oldYoung.domain.chatAI.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
class ChatControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean ChatService chatService;

  @Test
  void start_ShouldReturnWelcomeMessage() throws Exception {
    mockMvc.perform(get("/harume/chat/start/{userId}", 1L)) // pathVariable 전달
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(
            "안녕하세요, 하루미에요!😉\n어르신을 위한 건강·복지 정보를 쉽게 알려드릴게요.\n\n원하는 정보를 물어봐주세요!"
        ));
  }

  @Test
  void chat_ShouldReturnAiResponse() throws Exception {
    ChatMessage.Req req = new ChatMessage.Req("혈압 관리하는 방법 알려줘");

    mockMvc.perform(post("/harume/chat/chat/{userId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").exists()); // 응답이 존재하는지만 체크
  }
}