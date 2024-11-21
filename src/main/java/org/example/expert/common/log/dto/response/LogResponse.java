package org.example.expert.common.log.dto.response;

import java.time.LocalDateTime;

public record LogResponse(Long id, String action, String details, LocalDateTime createdAt) {
}
