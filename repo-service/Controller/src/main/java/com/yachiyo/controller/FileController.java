package com.yachiyo.controller;


import com.yachiyo.Utils.FileUrlUtil;
import com.yachiyo.result.Result;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.HttpHeaders;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileUrlUtil fileUrlUtil;

    private final String UPLOAD_FILE_PATH = com.yachiyo.Utils.IOFileUtils.UPLOAD_FILE_PATH;

    @GetMapping("/generate")
    public ResponseEntity<Resource> generateFileUrl(@RequestParam String fileName,
                                                    @RequestParam long expire,
                                                    @RequestParam String sign) {
        fileName = URLDecoder.decode(
                fileName,
                StandardCharsets.UTF_8
        );
        if (!fileUrlUtil.verify(fileName, expire, sign)){
            return ResponseEntity.status(403).body(null);
        }

        File file = new File(UPLOAD_FILE_PATH + "/" + fileName);

        if (!file.exists()){
            return ResponseEntity.status(404).body(null);
        }

        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + fileName + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .body(new FileSystemResource(file));
    }
}
