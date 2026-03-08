package com.yachiyo.controller;

import com.yachiyo.result.Result;
import com.yachiyo.service.ToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/tools")
@RequiredArgsConstructor
@Validated
public class AIToolController {

    @Autowired
    private final ToolService toolService;

     /**
      * 获取 Live2d 模型的 json 字符串
      * @param prompt 语句
      * @return Live2d 模型的 json 字符串
      */
    @PostMapping("/live2d")
    public Result<String> getLive2dJson(@RequestBody String prompt){
        return toolService.getLive2dJson(prompt);
    }
}
