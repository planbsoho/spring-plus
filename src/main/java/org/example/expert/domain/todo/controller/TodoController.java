package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequestDto;
import org.example.expert.domain.todo.dto.request.TodoSearchWithPagingRequestDto;
import org.example.expert.domain.todo.dto.response.*;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;


    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(todoService.getTodos(page, size));
    }

    /**
     * QueryDsl 사용한 todo 조회 8번과제
     * @param todoId
     * @return todoWithUser
     */
    @GetMapping("/todos/{todoId}/with-user")
    public ResponseEntity<TodoResponse> getTodoWithUser(@PathVariable Long todoId) {
        return ResponseEntity.ok(todoService.findTodoWithUser(todoId));
    }


    /**
     * 날씨, 수정일 기준으로 찾기 3번과제
     * @param weather
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/todos/search")
    public ResponseEntity<Page<TodoResponse>> searchTodosWithCondition(
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(todoService.searchTodosWithCondition(weather,startDate,endDate,page,size));
    }

    /**
     *   부분제목
     * @param title
     * @return
     */
//    @GetMapping("/todos/contain-titles")
//    public ResponseEntity<List<TodoContainsTitleDto>> findTodoContainsTitle(@RequestParam String title) {
//        return ResponseEntity.ok(todoService.findTodoContainsTitle(title));
//    }
//    /**
//     *  부분제목, 부분별명, null 조건추가
//     * @param title, nickname
//     * @return
//     */
//    @GetMapping("/todos/contain-titles-nicknames")
//    public ResponseEntity<List<TodoContainsTitleDto>> findTodoContainsTitle(@RequestParam(required = false) String title,@RequestParam(required = false) String nickname) {
//        return ResponseEntity.ok(todoService.findTodoContainsTitle(title,nickname));
//    }

    /**
     * tuple로 조회, 직렬화오류발생
     * @param title
     * @param nickname
     * @return
     */
    @GetMapping("/todos/tuple/containers")
    public ResponseEntity<List<TodoContainsDataDto>> findTodoContainsTitleAndNickname(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String nickname
    ) {
        return ResponseEntity.ok(todoService.findTodoContainsTitleAndNickname(title, nickname));
    }

    /**
     * 조회 기간추가, 타이틀일부, 닉네임일부
     * @param request
     * @return
     */
    @GetMapping("/todos/search-details")
    public ResponseEntity<List<TodoContainsDataDto>> searchTodosByConditions(@ModelAttribute TodoSearchRequestDto request) {
        return ResponseEntity.ok(todoService.searchTodosByConditions(request));
    }

    /**
     *   기간, 닉네임, 제목일부등으로 검색결과를 페이징하여 조회
     * @param request
     * @param pageable
     * @return
     */
    @GetMapping("/todos/search-page")
    public ResponseEntity<Page<TodoSearchWithPagingResponseDto>> searchWithPaging(
            @ModelAttribute TodoSearchWithPagingRequestDto request,
            @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(todoService.searchWithPaging(request, pageable));
    }

}
