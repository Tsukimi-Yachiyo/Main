package com.yachiyo.QQBotService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadFileRequest {
    private String fileName;
    private String downloadUrl;
}
