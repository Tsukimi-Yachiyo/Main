package com.yachiyo.dto;

import lombok.Data;

@Data
public class MailEncapsulateResponse {

    private Long mailId;

    private Boolean isRead;

    private String title;

    private String senderName;

    private String senderAvatar;
}
