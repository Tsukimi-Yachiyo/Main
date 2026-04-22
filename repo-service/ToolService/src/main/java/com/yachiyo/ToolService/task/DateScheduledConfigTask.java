package com.yachiyo.ToolService.task;

import com.yachiyo.ToolService.service.DateService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

@Component
@EnableScheduling
public class DateScheduledConfigTask {


    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DateService dateService;

    @Value("${custom.config.holiday.enable}")
    private boolean enable;

    @Scheduled(cron = "${custom.config.holiday.interval}")
    public void scheduledTask() {
        dateScheduledConfigTask();
    }

    public void dateScheduledConfigTask() {
        if (enable) {
            LocalDate localDate = LocalDate.now();
            int day = localDate.getDayOfMonth();
            int month = localDate.getMonthValue();

            String holiday = dateService.getHoliday(new Date());

            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            hashOps.put("public:date", "day", String.valueOf(day));
            hashOps.put("public:date", "month", String.valueOf(month));
            hashOps.put("public:date", "holiday", holiday);
        }
    }
}
