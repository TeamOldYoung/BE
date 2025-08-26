package com.app.oldYoung.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WelfareRequestDTO(
        @JsonProperty("age(bool)")
        int age,
        
        @JsonProperty("city")
        String region
) {
}