package com.yachiyo.FileService.controller;

import com.yachiyo.FileService.utils.FileUrlUtil;
import io.minio.GetObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileUrlUtil fileUrlUtil;

    private final MinioClient minioClient;

    @GetMapping("/download/upload")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName,
                                                    @RequestParam long expire,
                                                    @RequestParam String sign) {
        fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        if (!fileUrlUtil.verify(fileName, expire, sign)){
            return ResponseEntity.status(403).body(null);
        }

        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket("upload")
                    .object(fileName)
                    .build());

            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket("upload")
                        .object(fileName)
                        .build() );

                InputStreamResource resource = new InputStreamResource(inputStream);

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .header("Access-Control-Expose-Headers", "Content-Disposition")
                        .body(resource);


        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/download/robot")
    public ResponseEntity<Resource> downloadFileFromRobot(@RequestParam String fileName,
                                                 @RequestParam long expire,
                                                 @RequestParam String sign){
        return downloadFile(fileName, expire, sign);
    }

    @GetMapping("/download/save")
    public ResponseEntity<StreamingResponseBody> bigFileDownload(@RequestParam String fileName,
                                                    @RequestParam long expire,
                                                    @RequestParam String sign) {
        fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        if (!fileUrlUtil.verify(fileName, expire, sign)) {
            return ResponseEntity.status(403).body(null);
        }

        String objectName = fileName;
        try {
            // 提前获取文件元数据（可选，用于设置 Content-Length）
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket("save").object(objectName).build()
            );

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName;

            StreamingResponseBody streamBody = outputStream -> {
                try (InputStream minioStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket("save")
                                .object(objectName)
                                .build())) {
                    byte[] buffer = new byte[1024 * 64];
                    int bytesRead;
                    while ((bytesRead = minioStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentLength(stat.size())
                    .body(streamBody);

        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(500).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}