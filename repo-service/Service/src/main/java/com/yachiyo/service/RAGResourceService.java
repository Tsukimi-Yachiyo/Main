package com.yachiyo.service;

import com.yachiyo.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RAGResourceService {

    /**
     * 上传资源
     */
    Result<Boolean> uploadResource(List<MultipartFile> files);
}
