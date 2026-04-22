package com.yachiyo.AdminService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor @NoArgsConstructor
public class UserResponse {

    Long id;

    String name;

    String password;

    String role;

    String email;

    Date createTime;

    Date updateTime;
}
