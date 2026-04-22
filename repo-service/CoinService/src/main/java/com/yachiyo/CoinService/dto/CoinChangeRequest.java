package com.yachiyo.CoinService.dto;

import com.yachiyo.CoinService.utils.TradeType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CoinChangeRequest {
    private Long fromUserId;
    private Long toUserId;

    @NotBlank(message = "交易类型不能为空")
    private TradeType type;

    @NotBlank(message = "交易金额不能为空")
    private Double amount;
}
