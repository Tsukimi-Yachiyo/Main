package com.yachiyo.UserService.service.Impl;

import com.yachiyo.UserService.entity.UserDetail;
import com.yachiyo.UserService.repository.UserDetailRepository;
import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserDetailUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserDetailUpdateServiceImpl implements UserDetailUpdateService {

    // 1. 注入 R2DBC 异步 Repository（替代 MyBatis Mapper）
    @Autowired
    private UserDetailRepository userDetailRepository;

    // 2. 注入 响应式 RedisTemplate（替代同步 RedisTemplate）
    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    // 响应式 Hash 操作对象
    private ReactiveHashOperations<String, String, Object> hashOps() {
        return reactiveRedisTemplate.opsForHash();
    }

    /**
     * 异步初始化用户详情
     */
    @Override
    public Mono<Result<Boolean>> initUserDetail(Long id) {
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(id);

        return userDetailRepository.insert(userDetail)
                .then(Mono.just(Result.success(Boolean.TRUE)))
                .onErrorResume(e -> Mono.just(Result.error(
                        "500",
                        "初始化用户详情失败",
                        "数据库保存异常：" + e.getMessage()
                )));
    }

    /**
     * 异步登录校验（生日判断）
     */
    @Override
    public Mono<Result<Boolean>> login(Long id) {
        return getBirthday(id)
                .map(Result::success)
                .onErrorReturn(Result.error("500", null, "登录校验异常"));
    }

    /**
     * 异步：从Redis读取日期 + 从R2DBC查询用户生日 + 比较
     */
    private Mono<Boolean> getBirthday(Long userId) {
        // 1. 异步读取 Redis Hash
        Mono<Object> dayMono = hashOps().get("public:date", "day");
        Mono<Object> monthMono = hashOps().get("public:date", "month");

        // 2. 组合 Redis 结果 + 异步查询 DB
        return Mono.zip(dayMono, monthMono)
                .flatMap(tuple -> {
                    Object dayObj = tuple.getT1();
                    Object monthObj = tuple.getT2();

                    // 3. 异步 R2DBC 查询用户
                    return userDetailRepository.findById(userId)
                            .flatMap(user -> {
                                Date birthday = user.getUserBirthday();
                                if (birthday == null) {
                                    return Mono.just(true);
                                }

                                // 4. 日期比较
                                LocalDate localDate = birthday.toLocalDate();
                                int day = localDate.getDayOfMonth();
                                int month = localDate.getMonthValue();

                                boolean result = day == Integer.parseInt(dayObj.toString())
                                        && month == Integer.parseInt(monthObj.toString());
                                String redisMainKey = "user:" + userId;
                                ReactiveHashOperations<String, String, Boolean> hashOps = reactiveRedisTemplate.opsForHash();
                                hashOps.put(redisMainKey, "birthday", result)
                                        .flatMap(success -> reactiveRedisTemplate.expire(redisMainKey,  Duration.ofHours(24)));

                                return Mono.just(true);
                            })
                            .defaultIfEmpty(false);
                })
                .defaultIfEmpty(false)
                .onErrorReturn(false);
    }
}