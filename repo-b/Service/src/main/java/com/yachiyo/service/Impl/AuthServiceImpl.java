package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yachiyo.Config.FastMethodConfig;
import com.yachiyo.Config.SecuritySafeToolConfig;
import com.yachiyo.Utils.IOFileUtils;
import com.yachiyo.Utils.JwtUtils;
import com.yachiyo.Utils.MailUtils;
import com.yachiyo.dto.LoginRequest;
import com.yachiyo.dto.MailLoginRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
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
    private IOFileUtils ioFileUtils;

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

    @Override
    public Result<Boolean> ChangePassword(RegisterRequest registerRequest) {
        try{
            if (!verifyCode(registerRequest.getEmail(), registerRequest.getCode())) {
                return Result.error("400","验证码错误",null);
            }
            User user = new User();
            user.setPassword(securitySafeToolConfig.md5(registerRequest.getPassword()));
            userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getName, user.getName()));
            return Result.success(true, "密码更改成功",null);
        } catch (Exception e) {
            return Result.error("500","密码更改失败",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> Logout() {
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        redisTemplate.delete("user:" + userId);
        SecurityContextHolder.clearContext();
        return Result.success(true, "退出登录成功",null);
    }

    @Override
    public Result<String> LoginByEmail(MailLoginRequest mailLoginRequest) {
        try {
            User user = new User();
            user.setEmail(mailLoginRequest.getEmail());
            user.setPassword(securitySafeToolConfig.md5(mailLoginRequest.getPassword()));
            User selectUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail())
                    .eq(User::getPassword, user.getPassword()));
            if (selectUser == null) {
                boolean IsExistUser = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail())) == 0;
                if (IsExistUser) {
                    return Result.error("400.1","邮箱不存在",null);
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
    public Result<String> RefreshToken(String token, Long userId) {
        try{
            // 查看 redis 中是否存在用户
            if (!redisTemplate.hasKey("user:" + userId)) {
                return Result.error("400","用户不存在",null);
            }
            redisTemplate.delete("user:" + userId);
            SecurityContextHolder.clearContext();
            User user = userMapper.selectById(new LambdaQueryWrapper<User>().eq(User::getId, userId));
            String newToken = userEntrySystem(user);
            return Result.success(newToken, "刷新令牌成功",null);
        } catch (Exception e) {
            return Result.error("500","刷新令牌失败",e.getMessage());
        }
    }

    private String userEntrySystem(User user) throws IOException {
        Long userId = user.getId();        //检查用户文件夹是否存在
        if (ioFileUtils.checkDirExist(String.valueOf(userId))) {
            ioFileUtils.createDir(String.valueOf(userId));
        }
        String token = jwtUtils.generateToken(userId, user.getName(), securitySafeToolConfig.getUnique(userId));
        boolean isBirthday = fastMethodConfig.getBirthday(user);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        hash.put("user:" + userId, "birthday", String.valueOf(isBirthday));
        return token;
    }

    private boolean verifyCode(String email, String code) {
        String storedCode = (String) redisTemplate.opsForValue().get("code:" + email);
        return code != null && code.equals(storedCode);
    }

}
