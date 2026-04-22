package com.yachiyo.AdminService.service.Impl;

import com.yachiyo.AdminService.client.PostingClient;
import com.yachiyo.AdminService.dto.UserResponse;
import com.yachiyo.AdminService.config.SecuritySafeToolConfig;
import com.yachiyo.AdminService.utils.JwtUtils;
import com.yachiyo.AdminService.dto.PostingQueryRequest;
import com.yachiyo.AdminService.dto.ReviewRequest;
import com.yachiyo.AdminService.dto.PostingResponse;
import com.yachiyo.AdminService.result.Result;
import com.yachiyo.AdminService.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SecuritySafeToolConfig securitySafeToolConfig;

    @Autowired
    private PostingClient postingClient;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public Result<String> Login(UserResponse user) {
        try {
            if (user.getName().equals("admin")) {
                if (user.getPassword().equals(adminPassword)) {
                    return Result.success(jwtUtils.generateToken(0L, "admin", securitySafeToolConfig.getUnique(0L)));
                }
            }
        } catch (Exception e) {
            return Result.error("500", "登录失败", e.getMessage());
        }
        return Result.error("400", "登录失败", "登录失败");
    }


    @Override @SuppressWarnings("all")
    public Result<String> RunCommand(String command) {
        try {
            // 执行命令
            Process process = Runtime.getRuntime().exec(command);

            // 读取命令执行结果（标准输出）
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            System.out.println("=== 命令执行结果 ===");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 读取错误输出（重要！排查问题用）
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            System.out.println("\n=== 错误信息（如有）===");
            while ((line = errorReader.readLine()) != null) {
                return Result.error("400", "执行命令失败", line);
            }

            // 等待命令执行完成，获取退出码 0=成功
            int exitCode = process.waitFor();
            return Result.success(String.valueOf(exitCode));

        } catch (Exception e) {
            return Result.error("400", "执行命令失败", e.getMessage());
        }
    }

    @Override
    public Result<Boolean> reviewPosting(ReviewRequest request) {
        try {
            return postingClient.reviewPosting(request);
        } catch (Exception e) {
            return Result.error("400", "审核帖子失败", e.getMessage());
        }
    }

    @Override
    public Result<List<PostingResponse>> queryPostings(PostingQueryRequest request) {
        try {
            return postingClient.queryPostings(request);
        } catch (Exception e) {
            return Result.error("400", "查询帖子失败", e.getMessage());
        }
    }
}
