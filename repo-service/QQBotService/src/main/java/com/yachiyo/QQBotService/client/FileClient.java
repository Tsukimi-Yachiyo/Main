package com.yachiyo.QQBotService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@FeignClient(name = "file-service", path = "/internal/file")
public interface FileClient {
    @GetMapping("/getUrl")
    String getUrl(
            @RequestParam("url") String url,
            @RequestParam("time") long time,
            @RequestParam(
                    value = "prefix",
                    required = false,
                    defaultValue = "upload"
            ) String prefix
    );

    @PutMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    boolean upload(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "file", required = false) MultipartFile file
    );

    @PutMapping(
            value = "/save",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    boolean save(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "file", required = false) MultipartFile file
    );

    @DeleteMapping("/delete")
    boolean delete(@RequestParam("fileName") String fileName);

    @GetMapping("/read")
    byte[] read(@RequestParam("fileName") String fileName);

    @GetMapping("/checkExist")
    boolean checkExist(@RequestParam("fileName") String fileName);

    @GetMapping("/getNames")
    List<String> getNames(@RequestParam("dirName") String dirName) throws IOException;
}
