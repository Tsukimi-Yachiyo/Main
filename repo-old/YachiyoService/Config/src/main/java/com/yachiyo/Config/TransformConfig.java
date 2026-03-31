package com.yachiyo.Config;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.java.com.baidu.translate.TransUtil;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransformConfig {



    /**
     * 文本翻译方法
     * @param text 待翻译文本
     * @param from 源语言（如zh=中文，en=英文，auto=自动识别）
     * @param to 目标语言（如en=英文，ja=日语）
     * @return 翻译结果
     */
    public String translate(String text, String from, String to) {
        String japaneseText = TransUtil.getTransResult(text, from, to);
        JSONObject jsonObject = JSONObject.parseObject(japaneseText);
        JSONArray transResult = jsonObject.getJSONArray("trans_result");
        return transResult.getJSONObject(0).getString("dst");
    }


}
