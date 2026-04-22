package com.yachiyo.PostingService.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostEncapsulateResponse{

    private String title;

    private Long posterId;

    private String coverImage;
}
