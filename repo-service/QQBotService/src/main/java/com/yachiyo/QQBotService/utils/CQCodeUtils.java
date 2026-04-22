package com.yachiyo.QQBotService.utils;

import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import com.yachiyo.QQBotService.dto.UploadFileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Component
public class CQCodeUtils {
    // 文件、视频、语音、图片（仅sub_type=0，即普通图片，排除了动画表情等其它类型）
    public final Predicate<ArrayMsg> SHOULD_SAVE = msg -> {
        if (typeEq(msg, MsgTypeEnum.image)) {
            return isNormalImage(msg);
        }
        return isDownloadableType(msg);
    };

    public boolean typeEq(ArrayMsg arrayMsg, Predicate<ArrayMsg> condition) {
        return condition.test(arrayMsg);
    }

    public boolean typeEq(ArrayMsg arrayMsg, MsgTypeEnum... type) {
        for (MsgTypeEnum t : type) {
            if (arrayMsg.getType() == t) {
                return true;
            }
        }
        return false;
    }

    public boolean typeEq(ArrayMsg arrayMsg, MsgTypeEnum type) {
        return arrayMsg.getType() == type;
    }

    public boolean typeEq(ArrayMsg arrayMsg, String... rawType) {
        for (String t : rawType) {
            if (arrayMsg.getRawType().equals(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean typeEq(ArrayMsg arrayMsg, String rawType) {
        return arrayMsg.getRawType().equals(rawType);
    }

    public boolean atEq(ArrayMsg msg, Long userId) {
        return typeEq(msg, MsgTypeEnum.at) && msg.getLongData("qq") == userId;
    }

    /**
     * 从 CQ 码列表中筛选出所有的 at CQ 码
     * @param arrayMsgList CQ 码列表
     * @return 包含所有 at CQ 码的列表
     */
    public List<ArrayMsg> getAtCQCodeList(List<ArrayMsg> arrayMsgList) {
        return arrayMsgList.stream()
                .filter(msg -> typeEq(msg, MsgTypeEnum.at))
                .toList();
    }

    /**
     * 从 CQ 码列表中筛选出所有 at CQ 码中的 QQ 号
     * @param arrayMsgList CQ 码列表
     * @return 包含所有 at CQ 码中 QQ 号的列表
     */
    public List<Long> getAtIdList(List<ArrayMsg> arrayMsgList) {
        return arrayMsgList.stream()
                .filter(msg -> typeEq(msg, MsgTypeEnum.at))
                .map(msg -> msg.getLongData("qq"))
                .toList();
    }

    /**
     * 从 CQ 码列表中合并转发消息的 id
     * @param arrayMsgList CQ 码列表
     * @return 合并转发消息的 id，若不存在转发消息则返回 -1
     */
    public long findForwardId(List<ArrayMsg> arrayMsgList) {
        return arrayMsgList.stream()
                .filter(msg -> typeEq(msg, MsgTypeEnum.forward))
                .mapToLong(msg -> msg.getLongData("id"))
                .findFirst()
                .orElse(-1);
    }

    /**
     * 判断消息响应中是否包含合并转发消息
     * @param msgResp 消息响应对象
     * @return 消息响应中是否包含合并转发消息
     */
    public boolean containsForward(MsgResp msgResp) {
        return msgResp.getArrayMsg().stream()
                .anyMatch(msg -> typeEq(msg, MsgTypeEnum.forward));
    }

    /**
     * 将 CQ 码列表转换为字符串形式格式
     * @param arrayMsgList CQ 码列表
     * @return CQ 码字符串
     */
    public String toCQCode(List<ArrayMsg> arrayMsgList) {
        StringBuilder sb = new StringBuilder();
        for (ArrayMsg arrayMsg : arrayMsgList) {
            sb.append(arrayMsg.toCQCode());
        }
        return sb.toString();
    }

    /**
     * 从 CQ 码中提取文件信息
     * @param arrayMsg 包含文件信息的 CQ 码
     * @return 包含文件 URL 和文件名的 UploadFileRequest 对象，如果 CQ 码类型不支持则返回 null
     */
    public UploadFileRequest getUploadFile(ArrayMsg arrayMsg) {
        if (typeEq(arrayMsg, SHOULD_SAVE)) {
            return findFileInfo(arrayMsg);
        } else {
            log.warn("不支持的CQ码类型：{}", arrayMsg.getRawType());
            return null;
        }
    }

    /**
     * 从 CQ 码列表中筛选出所有包含文件信息的 CQ 码，并提取文件信息构造 UploadFileRequest 对象
     * @param arrayMsgList CQ 码列表
     * @return 包含所有提取成功的 UploadFileRequest 对象的列表，如果没有符合条件的 CQ 码则返回空列表
     */
    public List<UploadFileRequest> getUploadFileList(List<ArrayMsg> arrayMsgList) {
        return arrayMsgList.stream()
                .filter(msg -> typeEq(msg, SHOULD_SAVE))
                .map(this::findFileInfo)
                .toList();
    }

    /**
     * 从 CQ 码中提取文件信息，构造 UploadFileRequest 对象
     * @param arrayMsg 包含文件信息的 CQ 码
     * @return 包含文件 URL 和文件名的 UploadFileRequest 对象，如果提取失败则返回 null
     */
    private UploadFileRequest findFileInfo(ArrayMsg arrayMsg) {
        try {
            String url = arrayMsg.getStringData("url");
            String fileName = arrayMsg.getStringData("file");
            return new UploadFileRequest(url, fileName);
        } catch (Exception e) {
            log.error("从CQ码中提取文件信息失败：{}", arrayMsg, e);
            return null;
        }
    }

    /**
     * 判断 CQ 码是否是普通图片（即 sub_type=0 的图片，排除动画表情的情况）
     * @param arrayMsg 要判断的 image 类 CQ 码
     * @return CQ 码是否是普通图片而不是动画表情，获取 sub_type 失败时返回 false
     */
    public boolean isNormalImage(ArrayMsg arrayMsg) {
        try {
            String subType = arrayMsg.getStringData("sub_type");
            if (subType == null || subType.isBlank()) {
                // 不存在或为空时是系列动图表情
                return false;
            }
            return "0".equals(subType); // 仅存在且为 0 时是普通图片
        } catch (Exception e) {
            log.error("获取sub_type失败：{}", arrayMsg, e);
            return false;
        }
    }

    /**
     * 判断 CQ 码是否是可下载的类型（文件、视频、语音、图片）
     * @param arrayMsg 要判断的 CQ 码
     * @return CQ 码是否是可下载的类型
     */
    public boolean isDownloadableType(ArrayMsg arrayMsg) {
        if (typeEq(arrayMsg, MsgTypeEnum.record, MsgTypeEnum.video, MsgTypeEnum.image)) return true;
        return typeEq(arrayMsg, "file");
    }
}
