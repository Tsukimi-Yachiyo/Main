package com.yachiyo.ToolService.service;

import java.util.Date;

public interface DateService {

     /**
      * 获取节日
      * @param date 日期
      * @return 节日
      */
     String getHoliday(Date date);
}
