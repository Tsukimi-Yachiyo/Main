package com.yachiyo.UserService.repository;

import com.yachiyo.UserService.entity.UserDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface UserDetailRepository extends R2dbcRepository<UserDetail, Long> {

    @Query("""
        insert into user_detail (
            id
        ) values (
            :userId
        )
    """)
    Mono<Integer> insert(UserDetail userDetail);

    Flux<UserDetail> findByUserNameContains(String userName, Pageable pageable);
}
