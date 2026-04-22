package com.yachiyo.QQBotService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yachiyo.QQBotService.entity.GroupMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<GroupMessage> {
}
