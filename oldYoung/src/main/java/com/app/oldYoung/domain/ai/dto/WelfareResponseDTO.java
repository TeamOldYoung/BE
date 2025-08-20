package com.app.oldYoung.domain.ai.dto;

import java.util.List;
import java.util.Map;

public record WelfareResponseDTO(
        List<Map<String, Object>> info
) {
}