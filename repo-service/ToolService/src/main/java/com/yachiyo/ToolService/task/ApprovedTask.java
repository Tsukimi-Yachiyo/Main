package com.yachiyo.ToolService.task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ApprovedTask {

    @Scheduled(cron = "${custom.config.approved.interval}")
    public void scheduledTask() {
        approvedTask();
    }

    public void approvedTask() {

    }
}
