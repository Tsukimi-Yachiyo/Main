package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yachiyo.Utils.FileUrlUtil;
import com.yachiyo.dto.CoinChangeRequest;
import com.yachiyo.dto.PosterDetailResponse;
import com.yachiyo.dto.UserDetailResponse;
import com.yachiyo.entity.CoinLog;
import com.yachiyo.entity.User;
import com.yachiyo.entity.UserDetail;
import com.yachiyo.entity.UserWallet;
import com.yachiyo.enumeration.TradeType;
import com.yachiyo.mapper.CoinLogMapper;
import com.yachiyo.mapper.UserDetailMapper;
import com.yachiyo.mapper.UserMapper;
import com.yachiyo.mapper.UserWalletMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.CoinChangeService;
import com.yachiyo.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.yachiyo.Utils.IOFileUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Autowired
    private FileUrlUtil fileUrlUtil;

    @Autowired
    private IOFileUtils ioFileUtils;

    @Autowired
    private CoinLogMapper coinLogMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private CoinChangeService coinChangeService;

    @Override
    public Result<UserDetailResponse> getUserDetail() {
        // 从安全上下文获取当前用户id
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
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
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        if (!ioFileUtils.uploadFile(userId + "/avatar.jpg", userAvatar)) {
            return Result.error("500", "上传用户头像失败");
        }
        return Result.success(true);
    }

     @Override
    public Result<String> getUserAvatar() {
        // 从安全上下文获取当前用户id
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        String avatar = fileUrlUtil.generateFileUrl(userId + "/avatar.jpg", 60 * 5);
        if (avatar == null) {
            return Result.error("404", "用户头像不存在");
        }
        return Result.success(avatar);
    }

     @Override
    public Result<PosterDetailResponse> getPosterDetail(Long userId) {
        try {
            // 从数据库中获取用户详情
            UserDetail userDetail = userDetailMapper.selectById(userId);
            if (userDetail == null) {
                 return Result.error("404", "用户不存在"+userId);
            }
            PosterDetailResponse posterDetailResponse = new PosterDetailResponse();
            posterDetailResponse.setUserName(userDetail.getUserName());
            posterDetailResponse.setUserAvatar(fileUrlUtil.generateFileUrl(userId + "/avatar.jpg", 60 * 5));
            return Result.success(posterDetailResponse);
        } catch (Exception e) {
                return Result.error("500", "获取用户详情失败");
        }
    }

    @Override
    public Result<Boolean> sign() {
        // 从安全上下文获取当前用户id
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        // 从数据库中获取用户详情
        UserDetail userDetail = userDetailMapper.selectById(userId);
        if (userDetail == null) {
            return Result.error("404", "用户不存在"+userId);
        }
        QueryWrapper<CoinLog> queryWrapper = new QueryWrapper<CoinLog>().eq("user_id", userId).eq("business_type", TradeType.CHECKIN);
        queryWrapper.orderByDesc("create_time");
        // 从交易记录中查询用户是否已签到
        CoinLog coinLog = coinLogMapper.selectOne(queryWrapper);
        if (coinLog != null) {
            if (LocalDateTime.now().isAfter(coinLog.getCreateTime().plusDays(1))) {
                return Result.error("400", "用户已签到");
            }
        }
        // 签到成功
        CoinChangeRequest coinChangeRequest = new CoinChangeRequest();
        coinChangeRequest.setToUserId(userId);
        coinChangeRequest.setType(TradeType.CHECKIN);
        coinChangeRequest.setAmount(8.0);
        return coinChangeService.changeCoin(coinChangeRequest);
    }

    @Override
    public Result<Boolean> openWallet() {
        // 从安全上下文获取当前用户id
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        // 从数据库中获取用户详情
        UserDetail userDetail = userDetailMapper.selectById(userId);
        if (userDetail == null) {
            return Result.error("404", "用户不存在"+userId);
        }
        UserWallet userWallet = userWalletMapper.selectById(userId);
        if (userWallet != null) {
            return Result.error("400", "用户已开启钱包");
        }
        UserWallet newUserWallet = new UserWallet();
        newUserWallet.setId(userId);
        newUserWallet.setBalance(0.0);
        newUserWallet.setVersion(0);
        userWalletMapper.insert(newUserWallet);
        // 开启钱包成功
        return Result.success(true);
    }

    /**
     * 从UserDetailResponse获取UserDetail
     */
    private static @NonNull UserDetail getUserDetail(UserDetailResponse userDetailResponse) {
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
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
