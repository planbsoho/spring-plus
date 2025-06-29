package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchRequestDto;
import org.example.expert.domain.todo.dto.request.TodoSearchWithPagingRequestDto;
import org.example.expert.domain.todo.dto.response.TodoContainsDataDto;
import org.example.expert.domain.todo.dto.response.TodoSearchWithPagingResponseDto;
import org.example.expert.domain.todo.entity.Todo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class QTodoRepositoryImpl implements QTodoRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * N+1해결을 위한 fetchJoin()
     *
     * @param todoId
     * @return
     */
    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user, user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }


    /** 연습
     * 일치하는title로 todo검색
     * @param title
     * @return
     */
    @Override
    public List<Todo> findTodoContainsTitle(String title) {
        return jpaQueryFactory.selectFrom(todo)
                .innerJoin(todo.user, user).fetchJoin()
                .where(todo.title.contains(title))
                .fetch();
    }

    /** 연습
     * booleanBilder 를 사용 포함한다면조회
     *
     */
    @Override
    public List<Todo> findTodoContainsTitle(String title, String nickname) {
        BooleanBuilder builder = new BooleanBuilder();

        if(title != null && !title.trim().isEmpty()) {
            builder.and(todo.title.contains(title));
        }
        if(nickname != null && !nickname.trim().isEmpty()) {
            builder.and(user.nickName.contains(nickname));
        }
        return  jpaQueryFactory.selectFrom(todo)
                .innerJoin(todo.user, user).fetchJoin()
                .where(builder)
                .fetch();
    }
    /** 연습
     * projection 사용 각필드를 조합하기때문에 fetch join은 불필요하다 포함한다면조회
     */
    @Override
    public List<TodoContainsDataDto> findTodoContainsTitleAndNickname(String title, String nickname) {
        BooleanBuilder builder = new BooleanBuilder();

        if (title != null && !title.trim().isEmpty()) {
            builder.and(todo.title.contains(title));
        }

        if (nickname != null && !nickname.trim().isEmpty()) {
            builder.and(user.nickName.contains(nickname));
        }
        return jpaQueryFactory
                .select(Projections.constructor(TodoContainsDataDto.class,
                        todo.id,
                        todo.title,
                        todo.contents,
                        user.nickName
                ))
                .from(todo)
                .innerJoin(todo.user, user)
                .where(builder)
                .fetch();
    }

    /** 연습
     * 일정 검색 추가 , 제목, 닉네임으로 조회 정렬 내림차순
     * @param request
     * @return
     */
    @Override
    public List<TodoContainsDataDto> searchTodosByConditions(TodoSearchRequestDto request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            builder.and(todo.title.containsIgnoreCase(request.getTitle()));
        }
        if (request.getNickName() != null && !request.getNickName().trim().isEmpty()) {
            builder.and(user.nickName.contains(request.getNickName()));
        }
        if (request.getStartDate() != null) {
            builder.and(todo.createdAt.goe(request.getStartDate().atStartOfDay()));

        }
        if (request.getEndDate() != null) {
            builder.and(todo.createdAt.loe(request.getEndDate().atTime(23, 59, 59)));
        }
        return jpaQueryFactory
                .select(Projections.constructor(TodoContainsDataDto.class,
                        todo.id,
                        todo.title,
                        todo.contents,
                        user.nickName,
                        manager.countDistinct(), //
                        comment.countDistinct() //
                ))
                .from(todo)
                .innerJoin(todo.user, user)
                .leftJoin(todo.managers, manager) //todo manager : 0되게 leftjoin
                .leftJoin(todo.comments, comment) // todo comment : 0 되게 leftjoin
                .where(builder)
                .groupBy(todo.id)//countDistinct()사용시 groupBy 필수
                .orderBy(todo.createdAt.desc())
                .fetch();
    }
    /**
     * 페이징추가
     * 일정 검색 추가 , 제목, 닉네임으로 조회 정렬 내림차순
     * @param request
     * @return
     */
    @Override
    public Page<TodoSearchWithPagingResponseDto> searchWithPaging(TodoSearchWithPagingRequestDto request, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            builder.and(todo.title.containsIgnoreCase(request.getTitle()));
        }
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            builder.and(user.nickName.contains(request.getNickname()));
        }
        if (request.getStartDate() != null) {
            builder.and(todo.createdAt.goe(request.getStartDate().atStartOfDay()));

        }
        if (request.getEndDate() != null) {
            builder.and(todo.createdAt.loe(request.getEndDate().atTime(23, 59, 59)));
        }
        List<TodoSearchWithPagingResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(TodoSearchWithPagingResponseDto.class,
                        todo.id,
                        todo.title,
                        manager.countDistinct(),
                        comment.countDistinct()
                ))
                .from(todo)
                .innerJoin(todo.user, user)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(builder)
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(todo.id.countDistinct())
                .from(todo)
                .innerJoin(todo.user,user)
                .where(builder);
        // getPage의 세번째인자 람다표현식필요. 필요할때 fetchOne으로 쿼리실행지연

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
