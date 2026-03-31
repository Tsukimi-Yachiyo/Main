package com.yachiyo.service;

import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.PostEncapsulateResponse;
import com.yachiyo.dto.SelfPostResponse;
import com.yachiyo.dto.UploadPostingRequest;
import com.yachiyo.result.Result;

import java.util.List;

public interface PostingService {

    /**
     * 搜索帖子
     */
    Result<List<Long>> searchPosting(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 点赞的帖子
     */
    Result<List<Long>> getLikePosting();

    /**
     * 收藏的帖子
     */
    Result<List<Long>> getCollectionPosting();

    /**
     * 上传帖子
     */
    Result<Boolean> uploadPosting(UploadPostingRequest posting);

    /**
     * 获取帖子详情
     */
    Result<GetPostingResponse> getPosting(Long postingId);

    /**
     * 获取帖子简述
     */
    Result<PostEncapsulateResponse> getPostingEncapsulate(Long postingId);


    /**
     * 获取帖子的金币数
     */
    Result<Long> getCoinCount(Long postingId);

    /**
     * 删除帖子
     */
    Result<Boolean> deletePosting(Long postingId);

    /**
     * 处理帖子互动（点赞/收藏）
     * @param request 互动请求
     * @return 操作结果
     */
    Result<Boolean> handleInteraction(com.yachiyo.dto.InteractionRequest request);

    /**
     * 获取帖子统计信息
     * @param postingId 帖子ID
     * @return 帖子统计信息
     */
    Result<com.yachiyo.dto.PostStatsResponse> getPostingStats(Long postingId);

    /**
     * 获取自己的帖子
     */
    Result<List<SelfPostResponse>> getMyPosting();
}
