package com.yachiyo.QQBotService.utils;

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
    public final Predicate<ArrayMsg> HAS_URL = msg -> {
        if (typeEq(msg, MsgTypeEnum.image)) {
            String subType = msg.getStringData("sub_type");
            return "0".equals(subType); // 仅 sub_type=0 时
        }

        if (typeEq(msg, MsgTypeEnum.record, MsgTypeEnum.video)) return true;
        return "file".equals(msg.getRawType());
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
        if (typeEq(arrayMsg, HAS_URL)) {
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
                .filter(msg -> typeEq(msg, HAS_URL))
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
}
