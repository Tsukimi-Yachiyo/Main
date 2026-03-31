package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yachiyo.Config.FastMethodConfig;
import com.yachiyo.Config.IOFileConfig;
import com.yachiyo.Config.SecuritySafeToolConfig;
import com.yachiyo.Utils.JwtUtils;
import com.yachiyo.Utils.MailUtils;
import com.yachiyo.dto.LoginRequest;
import com.yachiyo.dto.RegisterRequest;
import com.yachiyo.entity.User;
import com.yachiyo.entity.UserDetail;
import com.yachiyo.mapper.UserDetailMapper;
import com.yachiyo.mapper.UserMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SecuritySafeToolConfig securitySafeToolConfig;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Autowired
    private FastMethodConfig fastMethodConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private IOFileConfig ioFileConfig;

    @Override
    public Result<String> Login(LoginRequest loginRequest) {
        try {
            User user = new User();
            user.setName(loginRequest.getUsername());
            user.setPassword(securitySafeToolConfig.md5(loginRequest.getPassword()));
            User selectUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getName, user.getName())
                    .eq(User::getPassword, user.getPassword()));
            if (selectUser == null) {
                boolean IsExistUser = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getName, user.getName())) == 0;
                if (IsExistUser) {
                    return Result.error("400.1","用户名不存在",null);
                }
                return Result.error("400.2","密码错误",null);
            }
            String token = userEntrySystem(selectUser);
            return Result.success(token, "登录成功",null);
        } catch (Exception e) {
            return Result.error("500","登录失败",e.getMessage());
        }
    }

    @Override
    public Result<String> Register(RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.setName(registerRequest.getUsername());
            user.setPassword(securitySafeToolConfig.md5(registerRequest.getPassword()));
            if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getName, user.getName())) != null) {
                return Result.error("400","用户名已存在",null);
            }
            if (!verifyCode(registerRequest.getEmail(), registerRequest.getCode())) {
                return Result.error("400","验证码错误",null);
            }
            user.setEmail(registerRequest.getEmail());
            userMapper.insert(user);
            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(user.getId());
            userDetailMapper.insert(userDetail);
            String token = userEntrySystem(user);
            return Result.success(token, "注册成功",null);
        } catch (Exception e) {
            return Result.error("500","注册失败",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> SendCode(String email) {
        try {
            String code = fastMethodConfig.generateCode(6);
            if (redisTemplate.opsForValue().get("code:" + email) != null) {
                return Result.error("400","验证码已发送，请稍后再试",null);
            }
            redisTemplate.opsForValue().set("code:" + email, code, 10, TimeUnit.MINUTES);
            mailUtils.sendMail(email, "验证码", code);
            return Result.success(true, "验证码发送成功",null);
        } catch (Exception e) {
            return Result.error("500","验证码发送失败",e.getMessage());
        }
    }

    private boolean verifyCode(String email, String code) {
        String redisCode = (String) redisTemplate.opsForValue().get("code:" + email);
        return redisCode != null && redisCode.equals(code);
    }

    private String userEntrySystem(User user) throws IOException {
        int userId = user.getId();        //检查用户文件夹是否存在
        if (ioFileConfig.checkDirExist(String.valueOf(userId))) {
            ioFileConfig.createDir(String.valueOf(userId));
        }
        String token = jwtUtils.generateToken((long) userId, user.getName(), securitySafeToolConfig.getUnique(userId));
        boolean isBirthday = fastMethodConfig.getBirthday(user);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        hash.put("user:" + userId, "birthday", String.valueOf(isBirthday));
        return token;
    }
}
