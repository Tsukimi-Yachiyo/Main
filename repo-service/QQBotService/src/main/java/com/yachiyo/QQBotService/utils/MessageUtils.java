package com.yachiyo.QQBotService.utils;

import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import com.yachiyo.QQBotService.dto.FormattedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 格式化消息，在保持结构的前提下使其更精简、更适合被AI模型处理
 */
@Slf4j
@Component
public class MessageUtils {
    @Autowired
    private CQCodeUtils CQCodeUtils;

    public FormattedMessage format(MessageEvent event) {
        List<ArrayMsg> arrayMsgList = event.getArrayMsg();
        String plainText = event.getPlainText();

        StringBuilder sb = new StringBuilder();
        List<String> relevantUrls = new ArrayList<>();
        for (ArrayMsg arrayMsg : arrayMsgList) {
            switch (arrayMsg.getType()) {
                case MsgTypeEnum.text -> sb.append(arrayMsg.getStringData("text"));
                case MsgTypeEnum.at -> sb.append(formatAt(arrayMsg));
                case MsgTypeEnum.reply -> sb.append(formatReply(arrayMsg));
                case MsgTypeEnum.forward -> sb.append(formatForward(arrayMsg));
                case MsgTypeEnum.image -> sb.append(formatImage(arrayMsg));
                case MsgTypeEnum.video -> sb.append(formatVideo(arrayMsg));
                case MsgTypeEnum.record -> sb.append(formatRecord(arrayMsg));
                case MsgTypeEnum.dice -> sb.append(formatDice(arrayMsg));
                case MsgTypeEnum.rps -> sb.append(formatRps(arrayMsg));
                case MsgTypeEnum.face -> sb.append(formatFace(arrayMsg));
                case MsgTypeEnum.json -> sb.append(formatJson(arrayMsg, relevantUrls));
            }
            if (CQCodeUtils.typeEq(arrayMsg, "file")) {
                sb.append(formatFile(arrayMsg));
            }
            sb.append(" "); // 添加空格以分隔CQ码
        }
        sb.append(plainText);

        return new FormattedMessage(sb.toString().trim(), relevantUrls);
    }

    public String formatAt(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.at)) return "";
        return "AT";
    }

    public String formatReply(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.reply)) return "";
        return "REPLY:" + arrayMsg.getLongData("id");
    }

    public String formatForward(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.forward)) return "";
        return "FORWARD:" + arrayMsg.getLongData("id");
    }

    /**
     * 格式化图片或动画表情
     * @param arrayMsg CQ码
     * @return 格式化结果
     */
    public String formatImage(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.image)) return "";
        if (!CQCodeUtils.isNormalImage(arrayMsg)) {
            return "STICKER" + cleaningSummary(arrayMsg.getStringData("summary"));
        }
        return "IMAGE";
    }

    public String formatVideo(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.video)) return "";
        return "VIDEO";
    }

    public String formatRecord(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.record)) return "";
        return "RECORD";
    }

    public String formatFile(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, "file")) return "";
        return "FILE";
    }

    public String formatDice(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.dice)) return "";
        return "DICE:" + arrayMsg.getStringData("result");
    }

    public String formatRps(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.rps)) return "";
        return "RPS:" + arrayMsg.getStringData("result");
    }

    public String formatFace(ArrayMsg arrayMsg) {
        if (!CQCodeUtils.typeEq(arrayMsg, MsgTypeEnum.face)) return "";
        return "FACE:" + arrayMsg.getStringData("id");
    }

    public String formatJson(ArrayMsg arrayMsg, List<String> relevantUrls) {
        JsonNode root = arrayMsg.getData();
        // 我也不知道为什么data字段用字符串存了整个JSON对象，反正就是这样了
        String dataStr = root.path("data").asString();
        JsonNode data = JsonUtils.parseToJsonNode(dataStr);
        JsonNode news = data.path("meta").path("news");

        String tag = news.path("tag").asString();
        String title = news.path("title").asString();

        String jumpUrl = news.path("jumpUrl").asString();
        relevantUrls.add(jumpUrl);

        return "JSON:" + tag + ":" + title;
    }

    /**
     * 清理动画表情的摘要内容，去掉无用的部分
     * @param summary 表情的summary字段
     * @return 清理后的字符串，默认摘要或者空摘要会被删除
     */
    private String cleaningSummary(String summary) {
        String ret = summary.replace("&#91;", "").replace("&#93;", "");
        if ("动画表情".equals(ret) || ret.isBlank()) {
            return "";
        } else {
            return ":" + ret;
        }
    }
}
