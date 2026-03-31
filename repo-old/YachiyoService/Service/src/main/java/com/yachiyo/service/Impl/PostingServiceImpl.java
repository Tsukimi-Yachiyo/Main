package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yachiyo.Config.IOFileConfig;
import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.UploadPostingRequest;
import com.yachiyo.entity.*;
import com.yachiyo.mapper.*;
import com.yachiyo.result.Result;
import com.yachiyo.service.PostingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service @Getter
public class PostingServiceImpl implements PostingService {

    private final PostingMapper postingMapper;
    private final PostDetailMapper postDetailMapper;
    private final LinkLikeMapper linkLikeMapper;
    private final LinkCollectionMapper linkCollectionMapper;
    private final IOFileConfig ioFileConfig;

    @Autowired
    public PostingServiceImpl(PostingMapper postingMapper, PostDetailMapper postDetailMapper, LinkLikeMapper linkLikeMapper, LinkCollectionMapper linkCollectionMapper, IOFileConfig ioFileConfig) {
        this.postingMapper = postingMapper;
        this.postDetailMapper = postDetailMapper;
        this.linkLikeMapper = linkLikeMapper;
        this.linkCollectionMapper = linkCollectionMapper;
        this.ioFileConfig = ioFileConfig;
    }

    @Override
    public Result<List<Integer>> searchPosting(String keyword) {
        try {
            LambdaQueryWrapper<Posting> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(keyword) && keyword.length() <= 10) {
                queryWrapper.like(Posting::getTitle, "%" + keyword + "%");
                queryWrapper.like(Posting::getContent, "%" + keyword + "%");
            } else {
                return Result.error("关键词长度不能超过10个字符");
            }
            List<Integer> postingIds = postingMapper.selectList(queryWrapper).stream().map(Posting::getId).collect(Collectors.toList());
            return Result.success(postingIds);
        } catch (Exception e) {
            return Result.error("500","搜索帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<List<Integer>> getLikePosting() {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            return Result.success(linkLikeMapper.selectList(new LambdaQueryWrapper<LinkLike>().eq(LinkLike::getUserId, UserId)).stream().map(LinkLike::getPostingId).toList());
        } catch (Exception e) {
            return Result.error("500","获取点赞帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<List<Integer>> getCollectionPosting() {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            return Result.success(linkCollectionMapper.selectList(new LambdaQueryWrapper<LinkCollection>().eq(LinkCollection::getUserId, UserId)).stream().map(LinkCollection::getUserId).toList());
        } catch (Exception e) {
            return Result.error("500","获取收藏帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> likePosting(int postingId) {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            if (linkLikeMapper.selectByMap(Map.of("user_id", UserId, "posting_id", postingId)) != null) {
                return Result.error("您已点赞该帖子");
            }
            LinkLike linkLike = new LinkLike();
            linkLike.setUserId(UserId);
            ;
            return Result.success(
                    linkLikeMapper.insert(linkLike) > 0);
        } catch (Exception e) {
            return Result.error("500","点赞帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> collectionPosting(int postingId) {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            if (linkCollectionMapper.selectByMap(Map.of("user_id", UserId, "posting_id", postingId)) != null) {
                return Result.error("您已收藏该帖子");
            }

            LinkCollection linkCollection = new LinkCollection();
            linkCollection.setUserId(UserId);
            linkCollection.setPostingId(postingId);
            return Result.success(
                    linkCollectionMapper.insert(linkCollection) > 0);
        } catch (Exception e) {
            return Result.error("500","收藏帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> cancelLikePosting(int postingId) {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            return Result.success(
                    linkLikeMapper.deleteByMap(Map.of("user_id", UserId, "posting_id", postingId)) > 0);
        } catch (Exception e) {
            return Result.error("500","取消点赞帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> cancelCollectionPosting(int postingId) {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            return Result.success(
                    linkCollectionMapper.deleteByMap(Map.of("user_id", UserId, "posting_id", postingId)) > 0);
        } catch (Exception e) {
            return Result.error("500","取消收藏帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> uploadPosting(UploadPostingRequest posting) {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            for (int i = 0; i < posting.getFiles().size(); i++) {
                MultipartFile file = posting.getFiles().get(i);
                if (ioFileConfig.checkDirExist(UserId + "/" + posting.getTitle())) {
                    ioFileConfig.createDir(UserId + "/" + posting.getTitle());
                }
                ioFileConfig.createDir(UserId + "/" + posting.getTitle() + "/");
                if (!ioFileConfig.uploadFile(UserId + "/" + posting.getTitle() + "/" + i + "_" + file.getOriginalFilename(), file)) {
                    return Result.error("文件上传失败");
                }
            }
            Posting postingEntity = new Posting();
            postingEntity.setUserId(UserId);
            postingEntity.setTitle(posting.getTitle());
            postingEntity.setContent(posting.getContent());
            postingEntity.setType(posting.getType());
            Boolean isUploadSuccess = postingMapper.insert(postingEntity) > 0;
            PostDetail postDetail = new PostDetail();
            postDetail.setId(postingEntity.getId());
            Boolean isDetailSuccess = postDetailMapper.insert(postDetail) > 0;
            return Result.success(isUploadSuccess && isDetailSuccess);
        } catch (Exception e) {
            return Result.error("500","上传帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<GetPostingResponse> getPosting(int postingId) {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            Posting postingEntity = postingMapper.selectById(postingId);
            if (postingEntity == null) {
                return Result.error("帖子不存在");
            }
            GetPostingResponse getPostingResponse = new GetPostingResponse();
            getPostingResponse.setContent(postingEntity.getContent());
            List<byte[]> files = new ArrayList<>();
            for (String fileName : ioFileConfig.getFileNames(UserId + "/" + postingEntity.getTitle())) {
                files.add(ioFileConfig.readFile(UserId + "/" + postingEntity.getTitle() + "/" + fileName));
            }
            getPostingResponse.setFiles(files);
            return Result.success(getPostingResponse);
        } catch (Exception e) {
            return Result.error("500", "获取帖子失败：", e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deletePosting(int postingId) {
        try {
            int UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            Posting postingEntity = postingMapper.selectById(postingId);
            if (postingEntity == null) {
                return Result.error("帖子不存在");
            }
            ioFileConfig.deleteFile(UserId + "/" + postingEntity.getTitle());
            return Result.success(
                    postingMapper.deleteById(postingId) > 0);
        } catch (Exception e) {
            return Result.error("500","删除帖子失败：",e.getMessage());
        }
    }
}
