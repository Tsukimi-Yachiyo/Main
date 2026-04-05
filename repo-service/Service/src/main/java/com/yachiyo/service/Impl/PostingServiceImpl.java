package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yachiyo.Utils.FileUrlUtil;
import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.InteractionRequest;
import com.yachiyo.dto.PostEncapsulateResponse;
import com.yachiyo.dto.PostStatsResponse;
import com.yachiyo.dto.SelfPostResponse;
import com.yachiyo.dto.UploadPostingRequest;
import com.yachiyo.enumeration.InteractionAction;
import com.yachiyo.enumeration.InteractionType;
import com.yachiyo.entity.*;
import com.yachiyo.mapper.*;
import com.yachiyo.result.Result;
import com.yachiyo.service.PostingService;
import com.yachiyo.Utils.IOFileUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final IOFileUtils ioFileUtils;
    private final FileUrlUtil fileUrlUtil;
    private final LinkCoinMapper linkCoinMapper;

    @Autowired
    public PostingServiceImpl(PostingMapper postingMapper, PostDetailMapper postDetailMapper, LinkLikeMapper linkLikeMapper, LinkCollectionMapper linkCollectionMapper, com.yachiyo.Utils.IOFileUtils ioFileUtils, FileUrlUtil fileUrlUtil, LinkCoinMapper linkCoinMapper) {
        this.postingMapper = postingMapper;
        this.postDetailMapper = postDetailMapper;
        this.linkLikeMapper = linkLikeMapper;
        this.linkCollectionMapper = linkCollectionMapper;
        this.linkCoinMapper = linkCoinMapper;
        this.ioFileUtils = ioFileUtils;
        this.fileUrlUtil = fileUrlUtil;
    }

    @Override
    public Result<List<Long>> searchPosting(String keyword, Integer pageNum, Integer pageSize) {
        try {
            Page<Posting> page = new Page<>(pageNum, pageSize);

            LambdaQueryWrapper<Posting> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(Posting::getId)
                    .like(Posting::getTitle, "%" + keyword + "%")
                    .or()
                    .like(Posting::getContent, "%" + keyword + "%")
                    .eq(Posting::getIsApproved, true)
                    .orderByDesc(Posting::getScore);

            postingMapper.selectPage(page, queryWrapper);

            List<Long> postingIds = page.getRecords()
                    .stream()
                    .map(Posting::getId)
                    .collect(Collectors.toList());
            return Result.success(postingIds);
        } catch (Exception e) {
            return Result.error("500","搜索帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getLikePosting() {
        try {
            Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            return Result.success(linkLikeMapper.selectList(new LambdaQueryWrapper<LinkLike>().eq(LinkLike::getUserId, UserId)).stream().map(LinkLike::getPostingId).toList());
        } catch (Exception e) {
            return Result.error("500","获取点赞帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getCollectionPosting() {
        try {
            Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            return Result.success(linkCollectionMapper.selectList(new LambdaQueryWrapper<LinkCollection>().eq(LinkCollection::getUserId, UserId)).stream().map(LinkCollection::getPostingId).toList());
        } catch (Exception e) {
            return Result.error("500","获取收藏帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> uploadPosting(UploadPostingRequest posting) {
        try {
            Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            if (ioFileUtils.checkDirExist(UserId + "/" + posting.getTitle())) {
                ioFileUtils.createDir(UserId + "/" + posting.getTitle());
            }
            if (posting.getCoverImage() != null && !ioFileUtils.uploadFile(UserId + "/" + posting.getTitle() + "/" + "cover.jpg", posting.getCoverImage())) {
                return Result.error("封面图片上传失败");
            }
            if (posting.getFiles() != null && !posting.getFiles().isEmpty()) {
                for (int i = 0; i < posting.getFiles().size(); i++) {
                    MultipartFile file = posting.getFiles().get(i);
                    if (!ioFileUtils.uploadFile(UserId + "/" + posting.getTitle() + "/" + i + "_" + file.getOriginalFilename(), file)) {
                        return Result.error("文件上传失败");
                    }
                }
            }
            Posting postingEntity = new Posting();
            postingEntity.setUserId(UserId);
            postingEntity.setTitle(posting.getTitle());
            postingEntity.setContent(posting.getContent());
            postingEntity.setType(posting.getType());
            System.out.println(postingEntity);
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
    public Result<GetPostingResponse> getPosting(Long postingId) {
        try {
            Posting postingEntity = postingMapper.selectById(postingId);
            if (postingEntity == null) {
                return Result.error("帖子不存在");
            }
            if (!postingEntity.getIsApproved()) {
                return Result.error("帖子未审核");
            }
            GetPostingResponse getPostingResponse = new GetPostingResponse();
            getPostingResponse.setContent(postingEntity.getContent());
            Long userId = postingEntity.getUserId();
            List<String> files = new ArrayList<>();
            for (String fileName : ioFileUtils.getFileNames(userId + "/" + postingEntity.getTitle())) {
                if (fileName.startsWith("\\d+")) {
                    files.add(fileUrlUtil.generateFileUrl(userId + "/" + postingEntity.getTitle() + "/" + fileName, 60 * 5));
                }
            }
            PostDetail postDetail = postDetailMapper.selectById(postingId);
            postDetail.setReading(postDetail.getReading() + 1);
            postDetailMapper.updateById(postDetail);
            getPostingResponse.setFiles(files);
            return Result.success(getPostingResponse);
        } catch (Exception e) {
            return Result.error("500", "获取帖子失败：", e.getMessage());
        }
    }

    @Override
    public Result<PostEncapsulateResponse> getPostingEncapsulate(Long postingId) {
        try{
            Posting postingEntity = postingMapper.selectById(postingId);
            if (postingEntity == null) {
                return Result.error("帖子不存在");
            }
            // 获取当前登录用户
            Long currentUserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            // 如果不是帖子作者且帖子未审核，则拒绝访问
            if (!postingEntity.getIsApproved() && !postingEntity.getUserId().equals(currentUserId)) {
                return Result.error("帖子未审核");
            }
            PostEncapsulateResponse postEncapsulateResponse = new PostEncapsulateResponse();
            postEncapsulateResponse.setTitle(postingEntity.getTitle());
            postEncapsulateResponse.setPosterId(postingEntity.getUserId());
            postEncapsulateResponse.setCoverImage(fileUrlUtil.generateFileUrl(postingEntity.getUserId() + "/" + postingEntity.getTitle() + "/" + "cover.jpg", 60 * 5));
            return Result.success(postEncapsulateResponse);
        } catch (Exception e) {
            return Result.error("500","获取帖子简述失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deletePosting(Long postingId) {
        try {
            Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            Posting postingEntity = postingMapper.selectById(postingId);
            if (postingEntity == null) {
                return Result.error("帖子不存在");
            }
            ioFileUtils.deleteFile(UserId + "/" + postingEntity.getTitle());
            return Result.success(
                    postingMapper.deleteById(postingId) > 0);
        } catch (Exception e) {
            return Result.error("500","删除帖子失败：",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> handleInteraction(InteractionRequest request) {
        try {
            Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            Long postingId = request.getPostingId();
            InteractionType type = request.getType();
            InteractionAction action = request.getAction();

            // 获取帖子详情
            PostDetail postDetail = postDetailMapper.selectById(postingId);
            if (postDetail == null) {
                return Result.error("帖子不存在");
            }

            boolean isLike = type == InteractionType.LIKE;
            // 检查当前互动状态
            boolean alreadyInteracted;
            if (isLike) {
                alreadyInteracted = !linkLikeMapper.selectByMap(Map.of("user_id", userId, "posting_id", postingId)).isEmpty();
            } else {
                alreadyInteracted = !linkCollectionMapper.selectByMap(Map.of("user_id", userId, "posting_id", postingId)).isEmpty();
            }

            // 根据action决定操作
            boolean shouldAdd;
            switch (action) {
                case ADD:
                    shouldAdd = true;
                    break;
                case REMOVE:
                    shouldAdd = false;
                    break;
                case TOGGLE:
                    shouldAdd = !alreadyInteracted;
                    break;
                default:
                    return Result.error("无效的操作类型");
            }

            // 执行操作
            if (shouldAdd && !alreadyInteracted) {
                // 添加互动
                if (isLike) {
                    LinkLike linkLike = new LinkLike();
                    linkLike.setUserId(userId);
                    linkLike.setPostingId(postingId);
                    linkLikeMapper.insert(linkLike);
                    postDetail.setLove(postDetail.getLove() + 1);
                } else if (type == InteractionType.COLLECTION) {
                    LinkCollection linkCollection = new LinkCollection();
                    linkCollection.setUserId(userId);
                    linkCollection.setPostingId(postingId);
                    linkCollectionMapper.insert(linkCollection);
                    postDetail.setCollection(postDetail.getCollection() + 1);
                } else {
                    LinkCoin linkCoin = new LinkCoin();
                    linkCoin.setUserId(userId);
                    linkCoin.setPostingId(postingId);
                    linkCoinMapper.insert(linkCoin);
                    postDetail.setCoin(postDetail.getCoin() + 1);
                }
                postDetailMapper.updateById(postDetail);
                return Result.success(true);
            } else if (!shouldAdd && alreadyInteracted) {
                // 移除互动
                if (isLike) {
                    linkLikeMapper.deleteByMap(Map.of("user_id", userId, "posting_id", postingId));
                    postDetail.setLove(postDetail.getLove() - 1);
                } else {
                    linkCollectionMapper.deleteByMap(Map.of("user_id", userId, "posting_id", postingId));
                    postDetail.setCollection(postDetail.getCollection() - 1);
                }
                postDetailMapper.updateById(postDetail);
                return Result.success(true);
            } else {
                // 状态未改变，返回当前状态
                return Result.success(true);
            }
        } catch (Exception e) {
            return Result.error("500", "处理互动失败", e.getMessage());
        }
    }

    @Override
    public Result<PostStatsResponse> getPostingStats(Long postingId) {
        try {
            Long userId = null;
            try {
                userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            } catch (Exception e) {
                // 用户未登录，userId保持为null
            }

            PostDetail postDetail = postDetailMapper.selectById(postingId);
            if (postDetail == null) {
                return Result.error("帖子不存在");
            }

            PostStatsResponse stats = new PostStatsResponse();
            stats.setLikeCount(postDetail.getLove());
            stats.setCollectionCount(postDetail.getCollection());
            stats.setReadingCount(postDetail.getReading());
            stats.setCoinCount(postDetail.getCoin());

            // 如果用户已登录，检查点赞和收藏状态
            if (userId != null) {
                boolean liked = !linkLikeMapper.selectByMap(Map.of("user_id", userId, "posting_id", postingId)).isEmpty();
                boolean collected = !linkCollectionMapper.selectByMap(Map.of("user_id", userId, "posting_id", postingId)).isEmpty();
                Long coinCount = linkCoinMapper.selectCount(new QueryWrapper<LinkCoin>().eq("user_id", userId).eq("posting_id", postingId));

                stats.setLiked(liked);
                stats.setCollected(collected);
                stats.setCoined(coinCount);
            } else {
                stats.setLiked(false);
                stats.setCollected(false);
                stats.setCoined(0L);
            }

            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("500", "获取帖子统计失败", e.getMessage());
        }
    }

    @Override
    public Result<List<SelfPostResponse>> getMyPosting() {
        try {
            Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            List<Long> postingIds = postingMapper.selectList(new QueryWrapper<Posting>().eq("user_id", UserId).orderByDesc("id"))
                    .stream()
                    .map(Posting::getId)
                    .toList();
            List<com.yachiyo.dto.SelfPostResponse> selfPostResponses = new ArrayList<>();
            for (Long postingId : postingIds) {
                SelfPostResponse selfPostResponse = new SelfPostResponse();
                selfPostResponse.setPostingId(postingId);
                selfPostResponse.setApproved(postingMapper.selectById(postingId).getIsApproved());
                selfPostResponses.add(selfPostResponse);
            }
            return Result.success(selfPostResponses);
        } catch (Exception e) {
            return Result.error("500","获取自己的帖子失败：",e.getMessage());
        }
    }
}
