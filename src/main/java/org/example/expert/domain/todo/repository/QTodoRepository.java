package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchRequestDto;
import org.example.expert.domain.todo.dto.request.TodoSearchWithPagingRequestDto;
import org.example.expert.domain.todo.dto.response.TodoContainsDataDto;
import org.example.expert.domain.todo.dto.response.TodoSearchWithPagingResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QTodoRepository {

    Optional<Todo> findByIdWithUser(Long todoId);

    List<Todo> findTodoContainsTitle(String title);

    List<Todo> findTodoContainsTitle(String title,String nickname);

    List<TodoContainsDataDto> findTodoContainsTitleAndNickname(String title, String nickname);

    List<TodoContainsDataDto> searchTodosByConditions(TodoSearchRequestDto requestDto);

    Page<TodoSearchWithPagingResponseDto> searchWithPaging(TodoSearchWithPagingRequestDto request, Pageable pageable);


}
