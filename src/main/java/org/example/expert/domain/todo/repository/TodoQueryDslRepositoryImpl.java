package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoQueryDslRepositoryImpl implements TodoQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin() // N+1 문제 해결
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public Page<TodoSearchResponse> searchTodos(TodoSearchRequest request, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;

        List<TodoSearchResponse> results = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        queryFactory.select(manager.countDistinct()) // 중복 방지를 위해 countDistinct 사용
                                        .from(manager)
                                        .where(todo.id.eq(todo.id)),
                        todo.comments.size().longValue()
                ))
                .from(todo)
                .leftJoin(todo.user, manager.user).fetchJoin() // N+1 문제 예방
                .where(
                        titleContains(request.getKeyword()),
                        createdBetween(request.getStartDateTime(), request.getEndDateTime()),
                        nicknameContains(request.getNickname())
                )
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(todo.countDistinct()) // 중복 방지를 위해 countDistinct 사용
                        .from(todo)
                        .where(
                                titleContains(request.getKeyword()),
                                createdBetween(request.getStartDateTime(), request.getEndDateTime()),
                                nicknameContains(request.getNickname())
                        )
                        .fetchOne()
                )
                .orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression titleContains(String keyword) {
        return keyword != null ? QTodo.todo.title.contains(keyword) : null;
    }

    private BooleanExpression createdBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return startDateTime != null && endDateTime != null ? QTodo.todo.createdAt.between(startDateTime, endDateTime) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null ? QManager.manager.user.nickname.contains(nickname) : null;
    }
}
