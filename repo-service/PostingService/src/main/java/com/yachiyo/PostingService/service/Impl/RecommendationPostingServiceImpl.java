package com.yachiyo.PostingService.service.Impl;

import com.yachiyo.PostingService.entity.PostDetail;
import com.yachiyo.PostingService.entity.Posting;
import com.yachiyo.PostingService.mapper.PostDetailMapper;
import com.yachiyo.PostingService.mapper.PostingMapper;
import com.yachiyo.PostingService.service.RecommendationPostingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.log;
import static java.lang.Math.min;

@Service @AllArgsConstructor
public class RecommendationPostingServiceImpl implements RecommendationPostingService {

    private final PostingMapper postingMapper;

    private final PostDetailMapper postDetailMapper;


    @Override
    public void recommendPosting() {
        // 推荐任务
        List<Posting> postings = postingMapper.selectList(null);
        for (Posting posting : postings) {
            PostDetail postDetail = postDetailMapper.selectById(posting.getId());
            if (postDetail != null) {
                Long love = postDetail.getLove();
                Long collection = postDetail.getCollection();
                Long reading = postDetail.getReading();
                double hourPoor = Duration.between(posting.getCreateTime(), LocalDateTime.now()).toMillis() / 3600000.0;

                // 计算推荐分数
                love = min(love, reading);

                double likeRate = (double) love / (reading +1);
                double collectionRate = (double) collection / (reading +1);

                double scoreActivity = log(reading +1 )* (1 + likeRate + collectionRate);

                double score = scoreActivity * (1/(1 + hourPoor)) * 100000;
                posting.setScore((long) score);
                postingMapper.updateById(posting);
            }
        }
    }
}
