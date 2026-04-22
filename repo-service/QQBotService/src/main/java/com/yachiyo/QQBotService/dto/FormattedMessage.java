package com.yachiyo.QQBotService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormattedMessage {
    private String content;
    private List<String> relevantUrls;

    public FormattedMessage(String content, String relevantUrl) {
        this.content = content;
        this.relevantUrls = List.of(relevantUrl);
    }

    public FormattedMessage append(String content, String relevantUrl) {
        this.content += " " + content;
        this.relevantUrls.add(relevantUrl);
        return this;
    }
}
