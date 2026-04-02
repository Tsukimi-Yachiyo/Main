package com.yachiyo.Common;

import com.yachiyo.entity.PostDetail;
import com.yachiyo.entity.Posting;
import com.yachiyo.mapper.PostDetailMapper;
import com.yachiyo.mapper.PostingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.log;
import static java.lang.Math.min;

@Component
@EnableScheduling
public class RecommendationTask {

    /**
     * 推荐任务
     */
    @Scheduled(cron = "${custom.config.recommendation.interval}")
    public void scheduledTask() {
        recommendationTask();
    }

    @Autowired
    private PostingMapper postingMapper;

    @Autowired
    private PostDetailMapper postDetailMapper;

    @Value("${custom.config.recommendation.enable}")
    private boolean enable;

    public void recommendationTask()
    {
        if (enable) {
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

}
