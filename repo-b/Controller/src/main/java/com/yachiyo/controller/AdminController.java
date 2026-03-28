package com.yachiyo.controller;

import com.yachiyo.entity.Posting;
import com.yachiyo.entity.User;
import com.yachiyo.result.Result;
import com.yachiyo.service.AdminService;
import com.yachiyo.service.RAGResourceService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/yachiyo/168/mini/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

    @Autowired
    private RAGResourceService ragResourceService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/upload")
    public Result<Boolean> UploadResource(@RequestParam("files") List<MultipartFile> files) {
        return ragResourceService.uploadResource(files);
    }

    @PostMapping("/run-command")
    public Result<String> runCommand(@RequestParam("command") String command) {
        return adminService.RunCommand(command);
    }

    @PostMapping("/get-remaining-token")
    public Result<Long> getRemainingToken() {
        return adminService.GetRemainingToken();
    }

    @PostMapping("/change-api-key")
    public Result<Void> changeApiKey(@RequestParam("apiKey") String apiKey, @RequestParam("model") String model) {
        return adminService.ChangeApiKey(apiKey, model);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        return adminService.Login(user);
    }

    @PostMapping("/approve-posting")
    public Result<Boolean> approvePosting(@RequestParam("postingId") Long postingId) {
        return adminService.ApprovePosting(postingId);
    }

    @PostMapping("/reject-posting")
    public Result<Boolean> rejectPosting(@RequestParam("postingId") Long postingId) {
        return adminService.RejectPosting(postingId);
    }

    @PostMapping("/get-all-posting")
    public Result<List<Posting>> getAllPosting() {
        return adminService.GetAllPosting();
    }

    @PostMapping("/get-unapproved-posting")
    public Result<List<Posting>> getUnapprovedPosting() {
        return adminService.GetUnapprovedPosting();
    }
}
