package com.yachiyo.dto;

import lombok.Data;

@Data
public class MailResponse {

    private Long senderId;

    private Long title;

    private String content;
}
