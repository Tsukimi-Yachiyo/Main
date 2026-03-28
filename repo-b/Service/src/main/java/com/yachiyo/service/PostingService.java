package com.yachiyo.service;

import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.PostEncapsulateResponse;
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
     * 点赞
     */
    Result<Boolean> likePosting(Long postingId);

    /**
     * 收藏
     */
    Result<Boolean> collectionPosting(Long postingId);

    /**
     * 取消点赞
     */
    Result<Boolean> cancelLikePosting(Long postingId);

    /**
     * 取消收藏
     */
    Result<Boolean> cancelCollectionPosting(Long postingId);

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
     * 获取帖子的收藏数
     */
    Result<Long> getCollectionCount(Long postingId);

    /**
     * 获取帖子的点赞数
     */
    Result<Long> getLikeCount(Long postingId);

    /**
     * 判断是否点赞帖子
     */
    Result<Boolean> isLiked(Long postingId);

    /**
     * 判断是否收藏帖子
     */
    Result<Boolean> isCollected(Long postingId);

    /**
     * 获取帖子的阅读数
     */
    Result<Long> getReadingCount(Long postingId);

    /**
     * 获取帖子的金币数
     */
    Result<Long> getCoinCount(Long postingId);

    /**
     * 删除帖子
     */
    Result<Boolean> deletePosting(Long postingId);

    /**
     * 获取自己的帖子
     */
    Result<List<Long>> getMyPosting();
}
