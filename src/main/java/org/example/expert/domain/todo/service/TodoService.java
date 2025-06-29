package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequestDto;
import org.example.expert.domain.todo.dto.request.TodoSearchWithPagingRequestDto;
import org.example.expert.domain.todo.dto.response.*;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.QTodoRepository;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final QTodoRepository qTodoRepository;

    /**
     * 클래스에 readOnly=true로 일괄적용되어 발생하는 오류를 Transactional 기본값인 readOnly = false 로 수정
     * @param authUser
     * @param todoSaveRequest
     * @return
     */
    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    /**
     * JPQL 3번과제
     * 날씨 수정일 기준으로 찾기
     * @param weather
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    public Page<TodoResponse> searchTodosWithCondition(
            String weather,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findTodoByWeatherOrDate(weather, startDate, endDate, pageable);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(),todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public Page<TodoResponse> getTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    /**
     * QueryDsl을 사용한 todo 조회 8번과제
     * @param todoId
     * @return todoWithUser
     */
    public TodoResponse findTodoWithUser(Long todoId) {
        Todo todo = qTodoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
    /**
     * 부분제목으로검색 연습
     * queryDSL
     */
//    public List<TodoContainsTitleDto> findTodoContainsTitle(String title) {
//        List<Todo> todos = qTodoRepository.findTodoContainsTitle(title);
//        return todos.stream()
//                .map(TodoContainsTitleDto::new)
//                .toList();
//    }
//    /**
//     * 부분제목, 부분별명 연습
//     * queryDSL
//     */
//    public List<TodoContainsTitleDto> findTodoContainsTitle(String title,String nickname) {
//        List<Todo> todos = qTodoRepository.findTodoContainsTitle(title, nickname);
//        return todos.stream()
//                .map(TodoContainsTitleDto::new)
//                .toList();
//    }
    /**
     * projection 사용 연습
     */
    public List<TodoContainsDataDto> findTodoContainsTitleAndNickname (String title, String nickname) {
        return qTodoRepository.findTodoContainsTitleAndNickname(title, nickname);
    }
    /**
     * 기한 조건 추가 10번과제
     */
    public List<TodoContainsDataDto> searchTodosByConditions(TodoSearchRequestDto request) {
        return qTodoRepository.searchTodosByConditions(request);
    }
    public Page<TodoSearchWithPagingResponseDto> searchWithPaging(TodoSearchWithPagingRequestDto request, Pageable pageable) {
        return qTodoRepository.searchWithPaging(request, pageable);
    }
}
