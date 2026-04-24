package com.yachiyo.UserService.service.Impl;

import com.yachiyo.UserService.client.FileClient;
import com.yachiyo.UserService.dto.PublicUserDetailResponse;
import com.yachiyo.UserService.dto.SearchDetailResponse;
import com.yachiyo.UserService.entity.FollowLink;
import com.yachiyo.UserService.repository.FollowLinkRepository;
import com.yachiyo.UserService.repository.UserDetailRepository;
import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserInteractService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserInteractServiceImpl implements UserInteractService {

    private final UserDetailRepository userDetailRepository;

    private final FileClient fileClient;

    private static final String AVATAR_PATH_FORMAT = "%d/avatar.jpg";

    private final FollowLinkRepository followLinkRepository;

    @Override
    public Mono<Result<Boolean>> follow(Long userId, Long followeeId) {

        FollowLink followLink = new FollowLink();
        followLink.setFollowerId(userId);
        followLink.setFolloweeId(followeeId);

        return followLinkRepository.existsFollowLinkByFolloweeIdAndFollowerId(userId, followeeId)
                .flatMap(exists -> exists ? followLinkRepository.deleteByFolloweeIdAndFollowerId(userId, followeeId) : followLinkRepository.insert(followLink))
                .map(_ -> Result.success(true))
                .onErrorResume(error -> Mono.just(Result.error("500", error.getMessage(), "关注失败")));
    }

    @Override
    public Flux<Result<Long>> getFolloweeList(Long userId) {
        return followLinkRepository.findByFollowerId(userId)
                .map(FollowLink::getFolloweeId)
                .map(Result::success)
                .onErrorResume(error -> Flux.just(Result.error("500", error.getMessage(), "获取关注列表失败")));
    }

    @Override
    public Flux<Result<Long>> getFollowerList(Long userId) {
        return followLinkRepository.findByFolloweeId(userId)
                .map(FollowLink::getFollowerId)
                .map(Result::success)
                .onErrorResume(error -> Flux.just(Result.error("500", error.getMessage(), "获取粉丝列表失败")));
    }

    @Override
    public Mono<Result<PublicUserDetailResponse>> getOnesDetail(Long userId) {
        return userDetailRepository.findById(userId)
                .map(userDetail -> {
                    PublicUserDetailResponse publicUserDetailResponse = new PublicUserDetailResponse();
                    publicUserDetailResponse.setUserIntroduction(userDetail.getUserIntroduction());
                    publicUserDetailResponse.setUserCity(userDetail.getUserCity());
                    publicUserDetailResponse.setUserGender(userDetail.getUserGender());
                    return Result.success(publicUserDetailResponse);
                })
                .onErrorResume(error -> Mono.just(Result.error("500", error.getMessage(), "获取用户详情失败")));
    }

    @Override
    public Mono<Result<Boolean>> getFollowStatus(Long userId, Long followeeId) {
        return followLinkRepository.existsFollowLinkByFolloweeIdAndFollowerId(userId, followeeId)
                .map(Result::success)
                .onErrorResume(error -> Mono.just(Result.error("500", error.getMessage(), "获取关注状态失败")));
    }

    @Override
    public Flux<Result<SearchDetailResponse>> searchUser(Long currentUserId, String userName, int pageNum, int pageSize) {

        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 20;

        return userDetailRepository.findByUserNameContains(userName, PageRequest.of(pageNum - 1, pageSize))
                .flatMap(userDetail -> {
                    // 并发查询
                    Mono<Boolean> isFollowing = followLinkRepository.existsFollowLinkByFolloweeIdAndFollowerId(userDetail.getUserId(), currentUserId);
                    Mono<Boolean> isFollowed = followLinkRepository.existsFollowLinkByFolloweeIdAndFollowerId(currentUserId, userDetail.getUserId());
                    Mono<Long> followerCount = followLinkRepository.countFollowLinkByFolloweeId(userDetail.getUserId());

                    // 组合三个 Mono
                    return Mono.zip(isFollowing, isFollowed, followerCount)
                            .flatMap(tuple3 -> Mono.fromCallable(() -> {
                                String avatarPath = String.format(AVATAR_PATH_FORMAT, userDetail.getUserId());
                                SearchDetailResponse resp = new SearchDetailResponse();
                                resp.setUserName(userDetail.getUserName());
                                resp.setUserAvatar(fileClient.getUrl(avatarPath, 1000L));
                                resp.setIsFollowing(tuple3.getT1());
                                resp.setIsFollowed(tuple3.getT2());
                                resp.setFollowerCount(tuple3.getT3());
                                return resp;
                            }).subscribeOn(Schedulers.boundedElastic()));
                })
                .map(Result::success) // 每条结果包装成 Result
                .onErrorResume(error ->
                        Flux.just(Result.error("500", error.getMessage(), "搜索用户失败"))
                );
    }

    @Override
    public Mono<Result<Boolean>> isFriend(Long currentUserId, Long followeeId) {
        Mono<Boolean> isFollowingMono = followLinkRepository.existsFollowLinkByFolloweeIdAndFollowerId(currentUserId, followeeId);
        Mono<Boolean> isFollowedMono = followLinkRepository.existsFollowLinkByFolloweeIdAndFollowerId(followeeId, currentUserId);

        return Mono.zip(isFollowingMono, isFollowedMono)
                .map(tuple -> {
                    boolean following = tuple.getT1();
                    boolean followed = tuple.getT2();
                    return Result.success(following && followed);
                });
    }

    @Override
    public Flux<Result<Long>> friends(Long currentUserId) {

        return getFolloweeList(currentUserId)
                .map(Result::getData)
                .flatMap(
                followeeId -> followLinkRepository.existsFollowLinkByFolloweeIdAndFollowerId(currentUserId, followeeId)
                        .filter(Boolean::booleanValue)
                        .thenReturn(Result.success(followeeId))
                        .onErrorResume(error -> Mono.just(Result.error("500", error.getMessage(), "获取关注状态失败")))
                );

    }
}
