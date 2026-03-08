package com.yachiyo.service;

import com.yachiyo.result.Result;

public interface ToolService {

     /**
      * 获取 Live2d 模型的 json 字符串
      * @param prompt 语句
      * @return Live2d 模型的 json 字符串
      */
    Result<String> getLive2dJson(String prompt);
}
