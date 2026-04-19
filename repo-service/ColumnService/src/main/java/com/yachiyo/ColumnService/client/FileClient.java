package com.yachiyo.ColumnService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", path = "/internal/file")
public interface FileClient {

    @GetMapping("/getUrl")
    String getUrl(@RequestParam("url") String url,
                  @RequestParam("time") long time,
                  @RequestParam(value = "prefix", defaultValue = "save") String prefix) ;

    @PutMapping("/save")
    boolean save(
            @RequestParam("fileName") String fileName,
            @RequestParam(required = false) MultipartFile file);
}
