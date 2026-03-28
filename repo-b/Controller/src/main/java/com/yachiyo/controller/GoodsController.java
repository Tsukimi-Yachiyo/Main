package com.yachiyo.controller;

import com.yachiyo.dto.BuyResponse;
import com.yachiyo.entity.Good;
import com.yachiyo.result.Result;
import com.yachiyo.service.BuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class GoodsController {

    private final BuyService buyService;

    /**
     * 购买
     */
    @PostMapping("/buy")
    public Result<Boolean> buy(@RequestParam Integer goodId) {
        return buyService.buy(goodId);
    }

    /**
     * 获取购买列表
     */
    @GetMapping("/get/my/list")
    public Result<List<BuyResponse>> getBuyList() {
        return buyService.getBuyList();
    }

    /**
     * 获取所有礼物
     */
    @GetMapping("/get/all")
    public Result<List<Good>> getAllGoods() {
        return buyService.getAllGoods();
    }


}
