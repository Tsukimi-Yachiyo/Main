package com.yachiyo.AuthService.controller.internal;

import com.yachiyo.AuthService.dto.MailRequest;
import com.yachiyo.AuthService.service.ManageUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/manage/user")
@RequiredArgsConstructor
public class ManageUserController {

    private final ManageUserService manageUserService;

    @PostMapping("send_mail")
    public Boolean sendMail(@RequestBody MailRequest mailRequest){
        return manageUserService.SendEmail(mailRequest.getTitle(), mailRequest.getContent());
    }
}
