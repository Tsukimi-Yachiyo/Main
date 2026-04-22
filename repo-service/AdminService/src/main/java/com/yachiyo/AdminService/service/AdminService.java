package com.yachiyo.AdminService.service;

import com.yachiyo.AdminService.dto.PostingQueryRequest;
import com.yachiyo.AdminService.dto.ReviewRequest;
import com.yachiyo.AdminService.dto.PostingResponse;
import com.yachiyo.AdminService.dto.UserResponse;
import com.yachiyo.AdminService.result.Result;

import java.util.List;

public interface AdminService {

    /**
     * 登录管理员
     *
     * @param user 管理员用户
     * @return 管理员用户
     */
    Result<String> Login(UserResponse user);

    /**
     * 执行命令
     *
     * @param command 命令
     * @return 命令执行结果
     */
    Result<String> RunCommand(String command);

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
    Result<List<PostingResponse>> queryPostings(PostingQueryRequest request);
}
