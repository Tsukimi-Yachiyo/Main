package com.yachiyo.dto;

import lombok.Data;

import java.sql.Time;

@Data
public class BuyResponse {

    Long goodId;

    Time buyTime;
}
