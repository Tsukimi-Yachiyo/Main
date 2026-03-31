package com.yachiyo.service.Impl;

import cn.hutool.http.HttpUtil;
import com.yachiyo.service.DateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DateServiceImpl implements DateService {

    private final JsonMapper jsonMapper;
    @Value("${custom.config.holiday.url}")
    private String holidayUrl;

    public DateServiceImpl(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public String getHoliday(Date date) {
        // 将日期转换为 YYYY-MM-DD 格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);
        try {
            String url = holidayUrl.replace("{date}", formattedDate);
            String response = HttpUtil.get(url);
            int type = jsonMapper.readTree(response).get("type").get("type").asInt();
            if (type == 1) {
                return jsonMapper.readTree(response).get("type").get("name").asString();
            }
            return "非节假日";
        } catch (Exception e) {
            e.printStackTrace();
            return "获取节假日信息失败";
        }
    }
}
