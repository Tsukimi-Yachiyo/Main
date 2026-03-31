package com.yachiyo.Utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component @Slf4j
public class IOFileUtils {

    /**
     * 运行时路径
     */
    public static final String RUNTIME_FILE_PATH = System.getProperty("user.dir")+"/Common";

    /**
     * 上传文件路径
     */
    @Getter
    public static final String UPLOAD_FILE_PATH = RUNTIME_FILE_PATH + "/src/main/resources/static/upload/";

    /**
     * 保存文件路径
     */
    @Getter
    public static final String SAVE_FILE_PATH = RUNTIME_FILE_PATH + "/src/main/resources/static/save/";

    /**
     * 保存文件
     */
    public boolean saveFile(String fileName, MultipartFile fileBytes) {
        try {
            fileBytes.transferTo(Paths.get(SAVE_FILE_PATH + fileName));
            return true;
        } catch (IOException e) {
            log.error("保存文件失败",e);
            return false;
        }
    }

    /**
     * 上传文件
     */
    public boolean uploadFile(@NonNull String fileName, @NonNull MultipartFile fileBytes) {
        try {
            fileBytes.transferTo(Paths.get(UPLOAD_FILE_PATH + fileName));
            return true;
        } catch (IOException e) {
            log.error("上传文件失败",e);
            return false;
        }
    }

    /**
     * 读取文件
     */
    public byte[] readFile(String fileName) {
        try {
            return Files.readAllBytes(Paths.get(UPLOAD_FILE_PATH + fileName));
        } catch (IOException e) {
            log.error("读取文件失败",e);
            return null;
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) {
        try {
            Files.delete(Paths.get(UPLOAD_FILE_PATH + fileName));
        } catch (IOException e) {
            log.error("删除文件失败",e);
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean checkDirExist(String dirName) {
        return !Files.exists(Paths.get(UPLOAD_FILE_PATH + dirName));
    }

    /**
     * 创建目录
     */
    public void createDir(String s) throws IOException {
        Files.createDirectory(Paths.get(UPLOAD_FILE_PATH + s));
    }

    /**
     * 获取路径下所有文件名
     * @return 文件名数组
     */
    public String[] getFileNames(String dirName) throws IOException {
        Path dir = Paths.get(UPLOAD_FILE_PATH + dirName);
        try (DirectoryStream<Path> _ = Files.newDirectoryStream(dir)) {
            Path[] paths = Files.list(dir).toArray(Path[]::new);
            List<String> fileNames = new ArrayList<>();
            for (Path path : paths) {
                if (Files.isDirectory(path)) {
                    continue;
                }
                fileNames.add(path.getFileName().toString());
            }
            return fileNames.toArray(new String[0]);
        }catch (NoSuchFileException e){
            return new String[0];
        }
    }

}