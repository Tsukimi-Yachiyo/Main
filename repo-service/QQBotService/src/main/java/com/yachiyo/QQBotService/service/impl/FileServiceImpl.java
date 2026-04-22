package com.yachiyo.QQBotService.service.impl;

import com.yachiyo.QQBotService.client.FileClient;
import com.yachiyo.QQBotService.dto.UploadFileRequest;
import com.yachiyo.QQBotService.result.Result;
import com.yachiyo.QQBotService.service.FileService;
import com.yachiyo.QQBotService.utils.ByteArrayMultipartFile;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private static final long URL_EXPIRE_SECONDS = 3600L;

    @Autowired
    private FileClient fileClient;

    @Override
    public Result<String> uploadFile(UploadFileRequest request) {
        try {
            HttpResponse<byte[]> response = Unirest.get(request.getDownloadUrl()).asBytes();
            int status = response.getStatus();
            if (status < 200 || status >= 300) {
                return Result.error("502", "下载文件失败", "status=" + status);
            }

            byte[] body = response.getBody();
            if (body == null || body.length == 0) {
                return Result.error("500", "文件状态异常", "文件内容为空");
            }

            String contentType = response.getHeaders().getFirst("Content-Type");
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            String fileName = UUID.randomUUID() + findExtension(request.getFileName());
            ByteArrayMultipartFile multipartFile = new ByteArrayMultipartFile(
                    fileName, contentType, body
            );

            boolean uploaded = fileClient.upload(fileName, multipartFile);
            if (uploaded) {
                return Result.success(fileName);
            } else {
                return Result.error("500", "上传到FileService失败", fileName);
            }
        } catch (Exception e) {
            return Result.error("500", "上传文件失败", e.getMessage());
        }
    }

    @Override
    public Result<List<String>> uploadFiles(List<UploadFileRequest> requestList) {
        if (requestList == null || requestList.isEmpty()) {
            return Result.error("400", "上传列表不能为空", null);
        }

        List<String> uploadedFileNames = new ArrayList<>();

        try {
            for (int i = 0; i < requestList.size(); i++) {
                UploadFileRequest request = requestList.get(i);

                HttpResponse<byte[]> response = Unirest.get(request.getDownloadUrl()).asBytes();
                int status = response.getStatus();
                if (status < 200 || status >= 300) {
                    log.error("下载第{}个文件失败，状态码：{}", i, status);
                    continue;
                }

                byte[] body = response.getBody();
                if (body == null || body.length == 0) {
                    log.error("第{}个文件内容为空", i);
                    continue;
                }

                String contentType = response.getHeaders().getFirst("Content-Type");
                if (contentType == null || contentType.isBlank()) {
                    contentType = "application/octet-stream";
                }

                // UUID + 序号 + 扩展名
                String fileName = UUID.randomUUID() + (i == 0 ? "" : "_" + i) + findExtension(request.getFileName());
                ByteArrayMultipartFile multipartFile = new ByteArrayMultipartFile(
                        fileName,
                        contentType,
                        response.getBody()
                );

                boolean uploaded = fileClient.upload(fileName, multipartFile);
                if (uploaded) {
                    uploadedFileNames.add(fileName);
                } else {
                    log.error("上传第{}个文件失败，文件名：{}", i, fileName);
                }
            }

            return Result.success(uploadedFileNames);
        } catch (Exception e) {
            return Result.error("500", "批量上传文件失败", e.getMessage());
        }
    }

    @Override
    public Result<String> getUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return Result.error("400", "fileName不能为空", null);
        }

        try {
            String url = fileClient.getUrl(fileName, URL_EXPIRE_SECONDS, "robot");
            if (url == null || url.isBlank()) {
                return Result.error("404", "文件不存在或URL生成失败", fileName);
            }
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("500", "生成URL失败", e.getMessage());
        }
    }

    @Override
    public Result<List<String>> getUrlList(List<String> fileNameList) {
        if (fileNameList == null || fileNameList.isEmpty()) {
            return Result.error("400", "fileNameList不能为空", null);
        }

        List<String> urlList = new ArrayList<>();
        try {
            for (int i = 0; i < fileNameList.size(); i++) {
                String fileName = fileNameList.get(i);
                if (fileName == null || fileName.isBlank()) {
                    log.error("第{}个fileName无效", i);
                    continue;
                }

                String url = fileClient.getUrl(fileName, URL_EXPIRE_SECONDS, "robot");
                if (url == null || url.isBlank()) {
                    log.error("第{}个文件URL生成失败，文件名：{}", i, fileName);
                    continue;
                }
                urlList.add(url);
            }
            return Result.success(urlList);
        } catch (Exception e) {
            return Result.error("500", "批量获取URL失败", e.getMessage());
        }
    }

    /**
     * 获取文件扩展名，不是网络传输内容类型
     * @param fileName 文件名
     * @return 文件扩展名
     */
    private String findExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');

        // 没有点或点在结尾
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        // 点是第一个字符（如 ".hidden"），返回整个文件名作为扩展名
        if (lastDotIndex == 0) {
            return fileName;
        }

        // 扩展名
        return fileName.substring(lastDotIndex);
    }
}
