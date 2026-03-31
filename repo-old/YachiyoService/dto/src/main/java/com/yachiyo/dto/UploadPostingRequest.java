package com.yachiyo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadPostingRequest {

    private String title;

    private String content;

    private String type;

    private List<MultipartFile> files;
}
