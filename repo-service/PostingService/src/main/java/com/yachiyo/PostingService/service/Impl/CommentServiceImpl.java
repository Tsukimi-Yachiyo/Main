package com.yachiyo.PostingService.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yachiyo.PostingService.dto.CommentRequest;
import com.yachiyo.PostingService.dto.CommentResponse;
import com.yachiyo.PostingService.entity.Comment;
import com.yachiyo.PostingService.mapper.CommentMapper;
import com.yachiyo.PostingService.result.Result;
import com.yachiyo.PostingService.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CacheManager cacheManager;

    private static final String COMMENT_CACHE_NAME = "public:posting:comment";

    @Override @CacheEvict(value = COMMENT_CACHE_NAME, key = "#commentRequest.postingId")
    public Result<Boolean> addComment(CommentRequest commentRequest) {
        Long UserId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Comment comment = new Comment();
        comment.setUserId(UserId);
        comment.setPostingId(commentRequest.getPostingId());
        comment.setContent(commentRequest.getContent());
        if (commentMapper.insert(comment) > 0) {
            return Result.success(true);
        }
        return Result.error("400","添加评论失败");
    }

    @Override @Cacheable(value = COMMENT_CACHE_NAME, key = "#postingId")
    public Result<List<CommentResponse>> getCommentList(Long postingId) {
        List<Comment> comments = commentMapper.selectList(new QueryWrapper<Comment>().eq("posting_id", postingId));
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setId(comment.getId());
            commentResponse.setContent(comment.getContent());
            commentResponse.setUserId(comment.getUserId());
            commentResponses.add(commentResponse);
        }
        return Result.success(commentResponses.reversed());
    }

    @Override
    public Result<Boolean> deleteComment(Long commentId) {
        try {
            Long postingId = commentMapper.selectById(commentId).getPostingId();
            if (commentMapper.deleteById(commentId) > 0) {
                Cache cache = cacheManager.getCache(COMMENT_CACHE_NAME);
                if (cache != null) {
                    cache.evict(postingId);
                }
                return Result.success(true);
            }
            return Result.error("400","删除评论失败");

        } catch (Exception e) {
            return Result.error("500","删除评论失败"+e.getMessage());
        }
    }
}
