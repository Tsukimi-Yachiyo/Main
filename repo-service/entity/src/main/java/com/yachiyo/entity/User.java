package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data @TableName("users")
@AllArgsConstructor @NoArgsConstructor
public class User {

    @TableId(value = "user_id",type = IdType.AUTO)
    Long id;

    String name;

    String password;

    String role;

    String email;

    Date createTime;

    Date updateTime;
}
