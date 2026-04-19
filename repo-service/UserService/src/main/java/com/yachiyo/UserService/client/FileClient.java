package com.yachiyo.UserService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", path = "/internal/file")
public interface FileClient {

    @PutMapping(path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    boolean uploadFile(@RequestParam("fileName") String fileName,
                       @RequestParam(required = false) MultipartFile file);

    @GetMapping("/getUrl")
    String getUrl(@RequestParam("url") String url, @RequestParam("time") long time);

}