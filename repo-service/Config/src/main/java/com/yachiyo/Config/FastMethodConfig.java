package com.yachiyo.Config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.github.kwfilter.util.KeyWordFilter;
import com.yachiyo.entity.User;
import com.yachiyo.entity.UserDetail;
import com.yachiyo.mapper.UserDetailMapper;
import lombok.Data;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Configuration
public class FastMethodConfig {

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String getHoliday() {
        // 从redis 中获取当前节假日
        String holiday = Objects.requireNonNull(redisTemplate.opsForHash().get("public:date", "holiday")).toString();
        if (!holiday.equals("非节假日")) {
            return holiday;
        }
        return null;
    }

    public String generateCode(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int randomNum = (int) (Math.random() * (max - min + 1)) + min;
        return String.valueOf(randomNum);
    }

    public boolean getBirthday(User user) {
        // 从redis 中获取当前日期
        String dayT = Objects.requireNonNull(redisTemplate.opsForHash().get("public:date", "day")).toString();
        String monthT = Objects.requireNonNull(redisTemplate.opsForHash().get("public:date", "month")).toString();
        if (!Objects.isNull(dayT) && !Objects.isNull(monthT)) {
            if (user != null) {
                UserDetail userDetail = userDetailMapper.selectById(user.getId());
                if (userDetail != null) {
                    Date birthday = userDetail.getUserBirthday();
                    if (Objects.nonNull(birthday)) {
                        // 提取日期中的日和月
                        LocalDate localDate = birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int day = localDate.getDayOfMonth();
                        int month = localDate.getMonthValue();
                        // 比较日期是否相等
                        return day == Integer.parseInt(dayT) && month == Integer.parseInt(monthT);
                    }
                }
            }
        }
        else  {
            return false;
        }
        return false;
    }

    @Bean
    public KeyWordFilter keyWordFilter() {
        return KeyWordFilter.getInstance();
    }
}
