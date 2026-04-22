package com.yachiyo.FileService.utils;

import io.minio.*;
import io.minio.messages.Item;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class IOFileUtils {

    private final MinioClient minioClient;

    // ===================== 保存文件（到 save 目录） =====================
    public boolean saveFile(String fileName, MultipartFile fileBytes) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("save")
                            .object(fileName)
                            .stream(fileBytes.getInputStream(), fileBytes.getSize(), -1)
                            .contentType(fileBytes.getContentType())
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("MinIO保存文件失败", e);
            return false;
        }
    }

    // ===================== 上传文件（到 upload 目录） =====================
    public boolean uploadFile(@NonNull String fileName, @NonNull MultipartFile fileBytes) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("upload")
                            .object(fileName)
                            .stream(fileBytes.getInputStream(), fileBytes.getSize(), -1)
                            .contentType(fileBytes.getContentType())
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("MinIO上传文件失败", e);
            return false;
        }
    }

    // ===================== 上传文件（到 upload 目录） =====================
    public boolean robotFile(@NonNull String fileName, @NonNull MultipartFile fileBytes) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("robot")
                            .object(fileName)
                            .stream(fileBytes.getInputStream(), fileBytes.getSize(), -1)
                            .contentType(fileBytes.getContentType())
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("MinIO上传文件失败", e);
            return false;
        }
    }

    // ===================== 读取文件（upload 目录） =====================
    public byte[] readFile(String fileName) {
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("upload")
                        .object(fileName)
                        .build()
        )) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("MinIO读取文件失败", e);
            return null;
        }
    }

    // ===================== 删除文件（upload 目录） =====================
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("upload")
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO删除文件失败", e);
        }
    }

    // ===================== 获取目录下所有文件名 =====================
    public List<String> getFileNames(String dirName) throws IOException {
        String prefix = dirName + "/";
        List<String> fileNames = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket("upload")
                            .prefix(prefix)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                // 过滤目录本身，只取文件
                if (!objectName.endsWith("/")) {
                    // 去掉前缀，只保留文件名
                    String name = objectName.replace(prefix, "");
                    fileNames.add(name);
                }
            }
            return fileNames;
        } catch (Exception e) {
            log.error("MinIO获取文件列表失败", e);
            return new ArrayList<>();
        }
    }

    // ===================== 检查文件是否存在 =====================
    public boolean fileExist(String fileName,String bucketName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("MinIO检查文件是否存在失败", e);
            return false;
        }
    }
}