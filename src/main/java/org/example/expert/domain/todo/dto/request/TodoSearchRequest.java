package org.example.expert.domain.todo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TodoSearchRequest {

    private String keyword;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String nickname;
    private int page;
    private int size;
}
