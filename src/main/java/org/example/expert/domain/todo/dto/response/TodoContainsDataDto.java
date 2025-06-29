package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TodoContainsDataDto {

    private Long id;
    private String title;
    private String  contents;
    private String nickName;
    private Long managerCount;
    private Long commentCount;


}
