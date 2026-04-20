package com.yachiyo.AuthService.service.Impl;

import com.yachiyo.AuthService.entity.User;
import com.yachiyo.AuthService.mapper.UserMapper;
import com.yachiyo.AuthService.service.ManageUserService;
import com.yachiyo.AuthService.utils.MailUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManageUserServiceImpl implements ManageUserService {

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean SendEmail(String title,String email){
        try {
            for (User user : userMapper.selectList(null)) {
                if (user.getEmail() != null) {
                    mailUtils.sendMail(user.getEmail(), title, email);
                }
            }
            return true;
        } catch (MessagingException e) {
            return  false;
        }
    }
}
