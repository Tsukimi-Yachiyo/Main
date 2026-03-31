package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data @AllArgsConstructor
@NoArgsConstructor @TableName("user_detail")
public class UserDetail {

    @TableId(value = "id")
    private Integer userId;

    @TableField(value = "introduction")
    private String userIntroduction;

    @TableField(value = "name")
    private String userName;

    @TableField(value = "city")
    private String userCity;

    @TableField(value = "gender")
    private String userGender;

    @TableField(value = "birthday")
    private Date userBirthday;
}
