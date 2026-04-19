package com.yachiyo.AdminService.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddColumnRequest {

    private String name;
    private String description;
    private EssayType type;
    private Long writerId;
    private MultipartFile file;
}
