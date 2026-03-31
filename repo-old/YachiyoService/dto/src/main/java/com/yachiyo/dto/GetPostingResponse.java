package com.yachiyo.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetPostingResponse {

    private String content;

    private List<byte[]> files;
}
