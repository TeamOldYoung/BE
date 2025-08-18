package com.app.oldYoung.domain.chatAI.repository;

import com.app.oldYoung.domain.chatAI.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatHistoryRepository {

  private final RedisTemplate<String, ChatMessage> redisTemplate;
  private final ObjectMapper objectMapper;

  public ChatHistoryRepository(RedisTemplate<String, ChatMessage> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = new ObjectMapper();
  }

  public void appendMessage(Long userId, ChatMessage message) {
    String key = "chat:" + userId;
    redisTemplate.opsForList().rightPush(key, message);
  }

  public List<ChatMessage> getHistory(Long userId) {
    String key = "chat:" + userId;
    List<ChatMessage> list = redisTemplate.opsForList().range(key, 0, -1);
    return list == null ? List.of() : list;
  }

}
