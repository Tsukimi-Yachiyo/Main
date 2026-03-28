package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yachiyo.enumeration.GoodType;
import lombok.Data;
@Data @TableName("goods")
public class Good  {

    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品类型
     */
    private GoodType goodType;

    /**
     * 商品描述
     */
    private String description;
}

