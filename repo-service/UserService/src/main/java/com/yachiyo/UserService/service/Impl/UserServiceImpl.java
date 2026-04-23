package com.yachiyo.UserService.service.Impl;

import com.yachiyo.UserService.client.FileClient;
import com.yachiyo.UserService.dto.PosterDetailResponse;
import com.yachiyo.UserService.dto.SelfUserDetailResponse;
import com.yachiyo.UserService.repository.UserDetailRepository;
import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDetailRepository userDetailRepository;
    private final FileClient fileClient;

    private static final String AVATAR_PATH_FORMAT = "%d/avatar.jpg";

    // 通用方法：将阻塞的 Feign 调用包装为响应式 Mono，并自动解包 Result
    private <T> Mono<T> callFileClient(Supplier<Result<T>> feignCall, String errorMsg) {
        return Mono.fromCallable(feignCall::get)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(result -> {
                    if (String.valueOf(HttpStatus.OK.value()).equals(result.getCode()) && result.getData() != null) {
                        return Mono.just(result.getData());
                    } else {
                        log.error("{}: code={}, message={}", errorMsg, result.getCode(), result.getMessage());
                        return Mono.error(new RuntimeException(errorMsg + ": " + result.getMessage()));
                    }
                });
    }

    private Mono<MultipartFile> filePartToMultipartFile(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new MultipartFile() {
                        @Override
                        public String getName() {
                            return filePart.name();
                        }

                        @Override
                        public String getOriginalFilename() {
                            return filePart.filename();
                        }

                        @Override
                        public String getContentType() {
                            return filePart.headers().getContentType().toString();
                        }

                        @Override
                        public boolean isEmpty() {
                            return bytes.length == 0;
                        }

                        @Override
                        public long getSize() {
                            return bytes.length;
                        }

                        @Override
                        public byte @NonNull [] getBytes() {
                            return bytes;
                        }

                        @Override
                        public @NonNull InputStream getInputStream() {
                            return new ByteArrayInputStream(bytes);
                        }

                        @Override
                        public void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
                            Files.write(dest.toPath(), bytes);
                        }
                    };
                });
    }

    @Override
    public Mono<Result<SelfUserDetailResponse>> getUserDetail(Long userId) {
        return userDetailRepository.findById(userId)
                .map(userDetail -> {
                    SelfUserDetailResponse resp = new SelfUserDetailResponse();
                    resp.setUserIntroduction(userDetail.getUserIntroduction());
                    resp.setUserName(userDetail.getUserName());
                    resp.setUserCity(userDetail.getUserCity());
                    resp.setUserGender(userDetail.getUserGender());
                    resp.setUserBirthday(userDetail.getUserBirthday());
                    return Result.success(resp);
                })
                .defaultIfEmpty(Result.error("404", "用户不存在"));
    }

    @Override
    public Mono<Result<Boolean>> updateUserDetail(Long userId, SelfUserDetailResponse selfUserDetailResponse) {
        return userDetailRepository.findById(userId)
                .flatMap(existUser -> {
                    if (selfUserDetailResponse.getUserName() != null) {
                        existUser.setUserName(selfUserDetailResponse.getUserName());
                    }
                    if (selfUserDetailResponse.getUserIntroduction() != null) {
                        existUser.setUserIntroduction(selfUserDetailResponse.getUserIntroduction());
                    }
                    if (selfUserDetailResponse.getUserCity() != null) {
                        existUser.setUserCity(selfUserDetailResponse.getUserCity());
                    }
                    if (selfUserDetailResponse.getUserGender() != null) {
                        existUser.setUserGender(selfUserDetailResponse.getUserGender());
                    }
                    if (selfUserDetailResponse.getUserBirthday() != null) {
                        existUser.setUserBirthday(selfUserDetailResponse.getUserBirthday());
                    }
                    return userDetailRepository.save(existUser);
                })
                .map(saved -> Result.success(true))
                .defaultIfEmpty(Result.error("404", "用户不存在"));
    }

    @Override
    public Mono<Result<Boolean>> updateUserAvatar(Long userId, FilePart userAvatar) {
        String filePath = String.format(AVATAR_PATH_FORMAT, userId);
        return filePartToMultipartFile(userAvatar)
                .flatMap(multipartFile -> callFileClient(
                        () -> Result.success(fileClient.uploadFile(filePath, multipartFile)),
                        "上传头像失败"
                ))
                .map(success -> Result.success(true))
                .onErrorResume(e -> Mono.just(Result.error("500", e.getMessage())));
    }

    @Override
    public Mono<Result<String>> getUserAvatar(Long userId) {
        String filePath = String.format(AVATAR_PATH_FORMAT, userId);
        return callFileClient(
                () -> Result.success(fileClient.getUrl(filePath, 1000L)),
                "获取头像URL失败"
        )
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error("500", e.getMessage())));
    }

    @Override
    public Mono<Result<PosterDetailResponse>> getPosterDetail(Long userId) {
        String filePath = String.format(AVATAR_PATH_FORMAT, userId);
        return userDetailRepository.findById(userId)
                .flatMap(userDetail ->
                        callFileClient(
                                () -> Result.success(fileClient.getUrl(filePath, System.currentTimeMillis())),
                                "获取头像URL失败"
                        )
                                .map(url -> {
                                    PosterDetailResponse response = new PosterDetailResponse();
                                    response.setUserName(userDetail.getUserName());
                                    response.setUserAvatar(url);
                                    return Result.success(response);
                                })
                                .onErrorResume(e -> {
                                    log.warn("获取头像URL失败，userId={}, error={}", userId, e.getMessage());
                                    PosterDetailResponse response = new PosterDetailResponse();
                                    response.setUserName(userDetail.getUserName());
                                    response.setUserAvatar(null);
                                    return Mono.just(Result.success(response));
                                })
                )
                .defaultIfEmpty(Result.error("404", "用户不存在：" + userId));
    }
}