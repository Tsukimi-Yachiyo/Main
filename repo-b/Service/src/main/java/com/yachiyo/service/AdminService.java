package com.yachiyo.service;

import com.yachiyo.dto.PostingQueryRequest;
import com.yachiyo.dto.ReviewRequest;
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
    @Deprecated
    Result<Boolean> ApprovePosting(Long postingId);

    /**
     * 拒绝帖子
     *
     * @param postingId 帖子 id
     */
    @Deprecated
    Result<Boolean> RejectPosting(Long postingId);

    /**
     * 获取所有帖子
     *
     * @return 所有帖子
     */
    @Deprecated
    Result<List<Posting>> GetAllPosting();

    /**
     * 获取未审核帖子
     *
     * @return 未审核帖子
     */
    @Deprecated
    Result<List<Posting>> GetUnapprovedPosting();

    /**
     * 删除帖子
     *
     * @param postingId 帖子 id
     * @deprecated 使用 {@link #reviewPosting(ReviewRequest)} 替代，action 设置为 DELETE
     */
    @Deprecated
    Result<Boolean> DeletePosting(Long postingId);

    /**
     * 审核帖子（通过/拒绝/删除）
     *
     * @param request 审核请求
     * @return 操作结果
     */
    Result<Boolean> reviewPosting(ReviewRequest request);

    /**
     * 查询帖子（支持状态筛选和关键词搜索）
     *
     * @param request 查询请求
     * @return 帖子列表
     */
    Result<List<Posting>> queryPostings(PostingQueryRequest request);

    /**
     * 获取拒绝帖子
     *
     * @return 拒绝帖子
     */
    @Deprecated
    Result<List<Posting>> GetRejectedPosting();
}
