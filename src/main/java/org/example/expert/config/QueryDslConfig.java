package org.example.expert.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * jpa와 queryDSL을 함께 사용하기 위한 빈등록
 */
@RequiredArgsConstructor
@Configuration
public class QueryDslConfig {
    private final EntityManager entityManager;


    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
