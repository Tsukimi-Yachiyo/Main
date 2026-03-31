package com.yachiyo.service.Impl;

import com.yachiyo.Utils.FileUrlUtil;
import com.yachiyo.dto.MailEncapsulateResponse;
import com.yachiyo.dto.MailRequest;
import com.yachiyo.dto.MailResponse;
import com.yachiyo.entity.Mail;
import com.yachiyo.entity.User;
import com.yachiyo.entity.UserDetail;
import com.yachiyo.mapper.MailMapper;
import com.yachiyo.mapper.UserDetailMapper;
import com.yachiyo.mapper.UserMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private MailMapper mailMapper;

    @Autowired
    private FileUrlUtil fileUrlUtil;
    @Autowired
    private UserDetailMapper userDetailMapper;

    @Override
    public Result<Boolean> sendMail(MailRequest mail) {
        try{
            Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            Mail mailEntity = new Mail();
            mailEntity.setSenderId(userId);
            mailEntity.setReceiverId(mail.getReceiverId());
            mailEntity.setContent(mail.getContent());
            mailEntity.setIsSpecial(mail.getIsSpecial());
            mailEntity.setIsRead(false);
            if (mailMapper.insert(mailEntity) > 0) {
                return Result.success(true);
            }
            else {
                return Result.success(false);
            }
        }catch (Exception e){
            return Result.error("500","发送邮件失败", e.getMessage());
        }
    }

    @Override
    public Result<MailResponse> readMail(Long mailId) {
        try{
            Mail mailEntity = mailMapper.selectById(mailId);
            if (mailEntity == null) {
                return Result.error("404","邮件不存在");
            }
            mailEntity.setIsRead(true);
            MailResponse mailResponse = new MailResponse();
            mailResponse.setSenderId(mailEntity.getSenderId());
            mailResponse.setTitle(Long.valueOf(mailEntity.getTitle()));
            mailResponse.setContent(mailEntity.getContent());
            mailMapper.updateById(mailEntity);
            return Result.success(mailResponse);
        }catch (Exception e){
            return Result.error("500","读取邮件失败", e.getMessage());
        }
    }

    @Override
    public Result<List<MailEncapsulateResponse>> getMailList() {
        try{
            Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            List<Mail> mailEntities = mailMapper.selectByMap(Map.of("receiver_id", userId));
            List<MailEncapsulateResponse> mailEncapsulateResponses = mailEntities.stream()
                    .map(mail -> {
                        MailEncapsulateResponse mailEncapsulateResponse = new MailEncapsulateResponse();
                        mailEncapsulateResponse.setMailId(mail.getId());
                        mailEncapsulateResponse.setIsRead(mail.getIsRead());
                        mailEncapsulateResponse.setTitle(mail.getTitle());
                        UserDetail sender = userDetailMapper.selectById(mail.getSenderId());
                        if (sender != null) {
                            mailEncapsulateResponse.setSenderName(sender.getUserName());
                            mailEncapsulateResponse.setSenderAvatar(fileUrlUtil.generateFileUrl(sender.getUserId()+"/avatar.jpg", 60 * 5));
                        }
                        return mailEncapsulateResponse;
                    })
                    .collect(Collectors.toList());
            return Result.success(mailEncapsulateResponses);
        }catch (Exception e){
            return Result.error("500","获取邮件列表失败", e.getMessage());
        }
    }
}
