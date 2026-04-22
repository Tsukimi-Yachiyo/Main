package com.yachiyo.UserService.repository;

import com.yachiyo.UserService.entity.UserDetail;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepository extends ReactiveCrudRepository<UserDetail, Long> {
}
