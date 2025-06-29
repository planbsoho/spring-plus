package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Setter//@Modelattribute 활성화를 위해 필요
@Getter
public class TodoSearchRequestDto {
    private String title;
    private String nickName;
    private LocalDate startDate;
    private LocalDate endDate;
}
