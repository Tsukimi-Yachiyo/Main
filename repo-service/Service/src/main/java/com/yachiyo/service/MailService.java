package com.yachiyo.service;

import com.yachiyo.dto.MailEncapsulateResponse;
import com.yachiyo.dto.MailRequest;
import com.yachiyo.dto.MailResponse;
import com.yachiyo.result.Result;

import java.util.List;

public interface MailService {


    /**
     * 发送邮件
     *
     * @param mail 邮件请求
     * @return 发送结果
     */
    Result<Boolean> sendMail(MailRequest mail);

    /**
     * 读取邮件
     *
     * @param mailId 邮件ID
     * @return 邮件响应
     */
    Result<MailResponse> readMail(Long mailId);

    /**
     * 获取邮件列表
     *
     * @return 邮件列表
     */
    Result<List<MailEncapsulateResponse>> getMailList();


}
