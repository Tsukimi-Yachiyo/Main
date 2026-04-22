package com.yachiyo.ColumnService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class CoinChangeRequest {
    private Long fromUserId;

    private Long toUserId;

    @NotBlank(message = "交易类型不能为空")
    private TradeType type;

    @NotBlank(message = "交易金额不能为空")
    private Double amount;
}
