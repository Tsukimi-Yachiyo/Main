package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yachiyo.dto.CommentRequest;
import com.yachiyo.dto.CommentResponse;
import com.yachiyo.entity.Comment;
import com.yachiyo.entity.User;
import com.yachiyo.mapper.CommentMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Result<Boolean> addComment(CommentRequest commentRequest) {
        try {
            Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            Comment comment = new Comment();
            comment.setUserId(UserId);
            comment.setPostingId(commentRequest.getPostingId());
            comment.setContent(commentRequest.getContent());
            if (commentMapper.insert(comment) > 0) {
                return Result.success(true);
            }
            return Result.error("400","添加评论失败");
        } catch (Exception e) {
            return Result.error("500","添加评论失败"+e.getMessage());
        }
    }

    @Override
    public Result<List<CommentResponse>> getCommentList(Long postingId) {
        try {
            List<Comment> comments = commentMapper.selectList(new QueryWrapper<Comment>().eq("posting_id", postingId));
            List<CommentResponse> commentResponses = new ArrayList<>();
            for (Comment comment : comments) {
                CommentResponse commentResponse = new CommentResponse();
                commentResponse.setId(comment.getId());
                commentResponse.setContent(comment.getContent());
                commentResponse.setUserId(comment.getUserId());
                commentResponses.add(commentResponse);
            }
            commentResponses.reversed();
            return Result.success(commentResponses);
        } catch (Exception e) {
            return Result.error("500","获取评论列表失败"+e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteComment(Long commentId) {
        try {
            commentMapper.deleteById(commentId);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("500","删除评论失败"+e.getMessage());
        }
    }
}
