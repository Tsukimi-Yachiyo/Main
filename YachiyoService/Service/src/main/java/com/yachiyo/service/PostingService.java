package com.yachiyo.service;

import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.UploadPostingRequest;
import com.yachiyo.result.Result;

import java.util.List;

public interface PostingService {

    /**
     * 搜索帖子
     */
    Result<List<Integer>> searchPosting(String keyword);

    /**
     * 点赞的帖子
     */
    Result<List<Integer>> getLikePosting();

    /**
     * 收藏的帖子
     */
    Result<List<Integer>> getCollectionPosting();

    /**
     * 点赞
     */
    Result<Boolean> likePosting(int postingId);

    /**
     * 收藏
     */
    Result<Boolean> collectionPosting(int postingId);

    /**
     * 取消点赞
     */
    Result<Boolean> cancelLikePosting(int postingId);

    /**
     * 取消收藏
     */
    Result<Boolean> cancelCollectionPosting(int postingId);

    /**
     * 上传帖子
     */
    Result<Boolean> uploadPosting(UploadPostingRequest posting);

    /**
     * 获取帖子详情
     */
    Result<GetPostingResponse> getPosting(int postingId);

    /**
     * 删除帖子
     */
    Result<Boolean> deletePosting(int postingId);
}
