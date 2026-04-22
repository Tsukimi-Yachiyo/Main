package com.yachiyo.UserService.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Date;

@Data
@Table("user_detail") // 表名不变
public class UserDetail {

    // 主键 → 必须用 @Id
    @Id
    @Column("id")     // 数据库字段是 id
    private Long userId;

    // 字段映射全部换成 @Column
    @Column("introduction")
    private String userIntroduction;

    @Column("name")
    private String userName;

    @Column("city")
    private String userCity;

    @Column("gender")
    private String userGender;

    @Column("birthday")
    private Date userBirthday;
}