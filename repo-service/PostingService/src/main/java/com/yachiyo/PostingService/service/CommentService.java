package com.yachiyo.PostingService.service;

import com.yachiyo.PostingService.dto.CommentRequest;
import com.yachiyo.PostingService.dto.CommentResponse;
import com.yachiyo.PostingService.result.Result;

import java.util.List;

public interface CommentService {

    /**
     * 添加评论
     */
    Result<Boolean> addComment(CommentRequest commentRequest);

    /**
     * 获取评论列表
     */
    Result<List<CommentResponse>> getCommentList(Long postingId);

    /**
     * 删除评论
     */
    Result<Boolean> deleteComment(Long commentId);
}
