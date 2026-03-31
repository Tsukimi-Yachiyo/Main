package com.yachiyo.controller;

import com.yachiyo.dto.MailEncapsulateResponse;
import com.yachiyo.dto.MailRequest;
import com.yachiyo.dto.MailResponse;
import com.yachiyo.entity.Mail;
import com.yachiyo.result.Result;
import com.yachiyo.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/mail")
@RequiredArgsConstructor
@Validated
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public Result<Boolean> sendMail(@Valid @RequestBody MailRequest mail) {
        return mailService.sendMail(mail);
    }

    @PostMapping("/read")
    public Result<MailResponse> readMail(@RequestBody Long mailId) {
        return mailService.readMail(mailId);
    }

    @PostMapping("/list")
    public Result<List<MailEncapsulateResponse>> readMailList(@RequestBody MailRequest mail) {
        return mailService.getMailList();
    }
}
