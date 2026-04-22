package com.yachiyo.FileService.controller;

import com.yachiyo.FileService.utils.FileUrlUtil;
import io.minio.GetObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import jakarta.servlet.http.HttpServletRequest;
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

        return downloadFileFromPublic(fileName, "upload");
    }

    @GetMapping("/download/robot")
    public ResponseEntity<Resource> downloadFileFromRobot(@RequestParam String fileName,
                                                 @RequestParam long expire,
                                                 @RequestParam String sign){
        fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        if (!fileUrlUtil.verify(fileName, expire, sign)){
            return ResponseEntity.status(403).body(null);
        }

        return downloadFileFromPublic(fileName, "robot");
    }

    @GetMapping("/public")
    public ResponseEntity<Resource> downloadFileFromPublic(@RequestParam String fileName, @RequestParam(defaultValue = "public") String bucket){
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build());

            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
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

    @GetMapping("/download/save")
    public ResponseEntity<StreamingResponseBody> bigFileDownload(
            HttpServletRequest request,
            @RequestParam String fileName,
            @RequestParam long expire,
            @RequestParam String sign) {

        try {
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

            // 1. 签名验证
            if (!fileUrlUtil.verify(fileName, expire, sign)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 2. 获取文件元数据（仅一次请求）
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket("save").object(fileName).build()
            );
            long fileSize = stat.size();

            // 3. 解析 Range 请求（核心：支持边加载边看）
            long start = 0;
            long end = fileSize - 1;
            boolean isRangeRequest = false;

            String rangeHeader = request.getHeader("Range");
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                try {
                    String[] ranges = rangeHeader.substring(6).split("-");
                    if (ranges.length > 0 && !ranges[0].isEmpty()) {
                        start = Long.parseLong(ranges[0]);
                    }
                    if (ranges.length > 1 && !ranges[1].isEmpty()) {
                        end = Long.parseLong(ranges[1]);
                    }
                    // 修正范围
                    if (start >= fileSize) start = fileSize - 1;
                    if (end >= fileSize) end = fileSize - 1;
                    if (start > end) start = end;
                    isRangeRequest = true;
                } catch (NumberFormatException e) {
                    // 解析失败则返回全文件
                }
            }
            long contentLength = end - start + 1;

            // 4. 设置正确的 Content-Type（支持浏览器预览）
            MediaType mediaType = getMediaType(fileName);

            // 5. 处理文件名编码（兼容中文）
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDisposition = "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName;
            // 注意：这里用 inline 表示浏览器优先预览，若需强制下载改为 attachment

            // 6. 构建 StreamingResponseBody（支持 Range 读取）
            final long finalStart = start;
            final long finalEnd = end;
            String finalFileName = fileName;
            StreamingResponseBody streamBody = outputStream -> {
                try (InputStream minioStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket("save")
                                .object(finalFileName)
                                .offset(finalStart)       // 关键：从指定位置开始读取
                                .length(finalEnd - finalStart + 1) // 关键：读取指定长度
                                .build())) {
                    byte[] buffer = new byte[1024 * 256]; // 256KB 缓冲区
                    int bytesRead;
                    while ((bytesRead = minioStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        outputStream.flush(); // 及时刷新，避免内存积压
                    }
                } catch (Exception e) {
                    throw new RuntimeException("文件下载失败", e);
                }
            };

            // 7. 构建响应
            ResponseEntity.BodyBuilder responseBuilder = isRangeRequest
                    ? ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    : ResponseEntity.ok();

            responseBuilder
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .header("Accept-Ranges", "bytes") // 声明支持 Range 请求
                    .contentLength(contentLength);

            if (isRangeRequest) {
                responseBuilder.header("Content-Range",
                        "bytes " + start + "-" + end + "/" + fileSize);
            }

            return responseBuilder.body(streamBody);

        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据文件扩展名获取 Content-Type（支持浏览器预览）
     */
    private MediaType getMediaType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (lowerName.endsWith(".txt")) return MediaType.TEXT_PLAIN;
        if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) return MediaType.TEXT_HTML;
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lowerName.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lowerName.endsWith(".gif")) return MediaType.IMAGE_GIF;
        // 其他格式默认返回流
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}