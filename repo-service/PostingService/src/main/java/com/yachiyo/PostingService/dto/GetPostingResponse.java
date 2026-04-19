package com.yachiyo.PostingService.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetPostingResponse{

    private String content;

    private List<String> filenames;

    private List<String> files;
}
