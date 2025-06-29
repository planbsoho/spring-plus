package org.example.expert.domain.todo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TodoSearchWithPagingRequestDto {
    private String title;
    private String nickname;
    private LocalDate startDate;
    private LocalDate endDate;
}
