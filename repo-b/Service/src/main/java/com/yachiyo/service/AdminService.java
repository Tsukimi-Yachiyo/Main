package com.yachiyo.service;

import com.yachiyo.entity.Posting;
import com.yachiyo.entity.User;
import com.yachiyo.result.Result;

import java.util.List;

public interface AdminService {

    /**
     * 登录管理员
     *
     * @param user 管理员用户
     * @return 管理员用户
     */
    Result<String> Login(User user);

    /**
     * 获取剩余 token
     *
     * @return 剩余 token
     */
    Result<Long> GetRemainingToken();

    /**
     * 更换 api key
     *
     * @param apiKey 新 api key
     */
    Result<Void> ChangeApiKey(String apiKey, String model);

    /**
     * 执行命令
     *
     * @param command 命令
     * @return 命令执行结果
     */
    Result<String> RunCommand(String command);

    /**
     * 审核帖子
     *
     * @param postingId 帖子 id
     */
    Result<Boolean> ApprovePosting(Long postingId);

    /**
     * 拒绝帖子
     *
     * @param postingId 帖子 id
     */
    Result<Boolean> RejectPosting(Long postingId);

    /**
     * 获取所有帖子
     *
     * @return 所有帖子
     * @throws Exception 异常
     */
    Result<List<Posting>> GetAllPosting();

    /**
     * 获取未审核帖子
     *
     * @return 未审核帖子
     * @throws Exception 异常
     */
    Result<List<Posting>> GetUnapprovedPosting();
}
