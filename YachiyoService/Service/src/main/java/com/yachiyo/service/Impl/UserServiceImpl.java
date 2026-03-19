package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yachiyo.Config.IOFileConfig;
import com.yachiyo.dto.UserDetailResponse;
import com.yachiyo.entity.User;
import com.yachiyo.entity.UserDetail;
import com.yachiyo.mapper.UserDetailMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Autowired
    private IOFileConfig ioFileConfig;

    @Override
    public Result<UserDetailResponse> getUserDetail() {
        // 从安全上下文获取当前用户id
        int userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        QueryWrapper<UserDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        UserDetail userDetail = userDetailMapper.selectOne(queryWrapper);
        if (userDetail == null) {
            return Result.error("404", "用户不存在");
        }
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        userDetailResponse.setUserIntroduction(userDetail.getUserIntroduction());
        userDetailResponse.setUserName(userDetail.getUserName());
        userDetailResponse.setUserCity(userDetail.getUserCity());
        userDetailResponse.setUserGender(userDetail.getUserGender());
        userDetailResponse.setUserBirthday(userDetail.getUserBirthday());
        return Result.success(userDetailResponse);
    }

    @Override
    public Result<Boolean> updateUserDetail( UserDetailResponse userDetailResponse) {
        // 从安全上下文获取当前用户id
        UserDetail userDetail = getUserDetail(userDetailResponse);
        if (userDetailResponse.getUserBirthday() != null
                    || userDetailResponse.getUserCity() != null
                    || userDetailResponse.getUserGender() != null
                    || userDetailResponse.getUserName() != null
                    || userDetailResponse.getUserIntroduction() != null) {
                userDetailMapper.updateById(userDetail);
                return Result.success(true);
            }
            return Result.success(false);
        }

    @Override
    public Result<Boolean> updateUserAvatar(MultipartFile userAvatar) {
        // 从安全上下文获取当前用户id
        int userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        if (!ioFileConfig.uploadFile(userId + "/avatar.jpg", userAvatar)) {
            return Result.error("500", "上传用户头像失败");
        }
        return Result.success(true);
    }

    @Override
    public Result<byte[]> getUserAvatar() {
        // 从安全上下文获取当前用户id
        int userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        byte[] avatar = ioFileConfig.readFile(userId + "/avatar.jpg");
        if (avatar == null) {
            return Result.error("404", "用户头像不存在");
        }
        return Result.success(avatar);
    }

    /**
     * 从UserDetailResponse获取UserDetail
     */
    private static @NonNull UserDetail getUserDetail(UserDetailResponse userDetailResponse) {
        int userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(userId);

        userDetail.setUserName(userDetailResponse.getUserName());
        userDetail.setUserCity(userDetailResponse.getUserCity());
        userDetail.setUserIntroduction(userDetailResponse.getUserIntroduction());
        userDetail.setUserGender(userDetailResponse.getUserGender());
        userDetail.setUserBirthday(userDetailResponse.getUserBirthday());
        return userDetail;
    }
}
