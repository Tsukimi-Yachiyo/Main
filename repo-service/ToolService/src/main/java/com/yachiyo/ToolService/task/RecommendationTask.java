package com.yachiyo.ToolService.task;

import com.yachiyo.ToolService.client.PostingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private PostingClient postingClient;

    @Value("${custom.config.recommendation.enable}")
    private boolean enable;

    public void recommendationTask()
    {
        if (enable) {
            postingClient.recommendPosting();
        }
    }

}
