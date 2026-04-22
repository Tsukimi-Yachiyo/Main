package com.yachiyo.ColumnService.dto;

import com.yachiyo.ColumnService.dto.InteractionType;
import lombok.Data;

@Data
public class InteractionRequest {

    Long columnId;
    InteractionType type;
}
