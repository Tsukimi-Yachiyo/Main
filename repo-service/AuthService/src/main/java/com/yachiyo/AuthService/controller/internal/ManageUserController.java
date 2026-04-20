package com.yachiyo.AuthService.controller.internal;

import com.yachiyo.AuthService.service.ManageUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/manage/user")
@RequiredArgsConstructor
public class ManageUserController {

    private final ManageUserService manageUserService;

    @PostMapping("send_mail")
    public Boolean sendMail(@RequestParam String title,@RequestParam String email){
        return manageUserService.SendEmail(title, email);
    }
}
