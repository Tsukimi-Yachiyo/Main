package com.yachiyo.controller;

import com.yachiyo.dto.CommentRequest;
import com.yachiyo.dto.CommentResponse;
import com.yachiyo.result.Result;
import com.yachiyo.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     *
     * @param commentRequest 评论请求
     * @return 添加结果
     */
    @PostMapping("/add-comment")
    public Result<Boolean> addComment(@RequestBody @Valid CommentRequest commentRequest) {
        return commentService.addComment(commentRequest);
    }

    /**
     * 获取评论列表
     *
     * @param postingId 帖子ID
     * @return 评论列表
     */
    @PostMapping("/get-comment-list")
    public Result<List<CommentResponse>> getCommentList(@RequestBody Long postingId) {
        return commentService.getCommentList(postingId);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 删除结果
     */
    @PostMapping("/delete-comment")
    public Result<Boolean> deleteComment(@RequestBody Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
