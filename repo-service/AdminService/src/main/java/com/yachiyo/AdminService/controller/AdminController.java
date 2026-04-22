package com.yachiyo.AdminService.controller;

import com.yachiyo.AdminService.client.ColumnClient;
import com.yachiyo.AdminService.dto.*;
import com.yachiyo.AdminService.result.Result;
import com.yachiyo.AdminService.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yachiyo/168/mini/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final AdminService adminService;

    private final ColumnClient columnClient;

    @PostMapping("/run-command")
    public Result<String> runCommand(@RequestParam("command") String command) {
        return adminService.RunCommand(command);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        UserResponse user = new UserResponse();
        user.setName(username);
        user.setPassword(password);
        return adminService.Login(user);
    }

    @PostMapping("/review")
    public Result<Boolean> reviewPosting(@RequestBody @Valid ReviewRequest request) {
        return adminService.reviewPosting(request);
    }

    @PostMapping("/query-postings")
    public Result<List<PostingResponse>> queryPostings(@RequestBody @Valid PostingQueryRequest request) {
        return adminService.queryPostings(request);
    }

    @PostMapping("/add-column")
    public Result<Boolean> addColumn(@RequestPart @Valid AddColumnRequest request) {
        return columnClient.addColumn(request);
    }

    @DeleteMapping("/delete-column")
    public Result<Boolean> deleteColumn(@RequestParam("id") Long id) {
        return columnClient.deleteColumn(id);
    }
}
