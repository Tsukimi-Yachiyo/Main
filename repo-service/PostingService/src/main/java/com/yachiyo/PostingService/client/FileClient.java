package com.yachiyo.PostingService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@FeignClient(name = "file-service", path = "/internal/file")
public interface FileClient{

    @GetMapping("/getUrl")
    String getUrl(@RequestParam("url") String url,
                  @RequestParam("time") long time);

    @PutMapping(path = "/upload", consumes = "multipart/form-data")
    boolean upload(
            @RequestParam("fileName") String fileName,
            @RequestParam(required = false) MultipartFile file);

    @GetMapping("/getNames")
    List<String> getNames(@RequestParam("dirName") String dirName) throws IOException;

    @DeleteMapping("/delete")
    boolean delete(
            @RequestParam("fileName") String fileName);
}
