package com.yachiyo.PostingService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPostingRequest {

    private String title;

    private String content;

    private String type;

    private MultipartFile coverImage;

    private List<MultipartFile> files;
}
