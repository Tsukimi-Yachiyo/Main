package com.yachiyo.AuthService.service;

public interface ManageUserService {

    /**
     * 发送邮件
     * @param email 邮箱
     * @return 发送结果
     */
    Boolean SendEmail(String title,String email);
}
