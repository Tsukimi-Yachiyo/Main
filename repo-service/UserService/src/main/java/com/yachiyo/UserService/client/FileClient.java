package com.yachiyo.UserService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", path = "/internal/file")
public interface FileClient {

    @PostMapping(path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    boolean uploadFile(@RequestParam("fileName") String fileName,
                       @RequestPart(required = false) MultipartFile file);

    @GetMapping("/getUrl")
    String getUrl(@RequestParam("url") String url, @RequestParam("time") long time);

}