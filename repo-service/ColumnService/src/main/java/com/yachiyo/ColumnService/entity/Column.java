package com.yachiyo.ColumnService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yachiyo.ColumnService.dto.EssayType;
import lombok.Data;

import java.time.LocalDateTime;

@Data @TableName("\"column\"")
public class Column {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String introduction;

    @TableField("file_name")
    private String fileName;

    private Long writer;

    @TableField("create_time")
    private LocalDateTime createTime;

    private EssayType type;

    private Long coin;

    @TableField("\"like\"")
    private Long like;
}
