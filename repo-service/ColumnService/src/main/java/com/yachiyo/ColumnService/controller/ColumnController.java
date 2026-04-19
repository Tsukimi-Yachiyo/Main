package com.yachiyo.ColumnService.controller;

import com.yachiyo.ColumnService.dto.ColumnResponse;
import com.yachiyo.ColumnService.dto.InteractionRequest;
import com.yachiyo.ColumnService.dto.InteractionResponse;
import com.yachiyo.ColumnService.dto.SearchRequest;
import com.yachiyo.ColumnService.result.Result;
import com.yachiyo.ColumnService.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/column")
@RequiredArgsConstructor
@Validated
public class ColumnController {

    @Autowired
    private ColumnService columnService;

    @GetMapping("/search")
    public Result<List<ColumnResponse>> searchColumn(@RequestBody SearchRequest searchRequest) {
        return columnService.searchColumn(searchRequest);
    }

    @PostMapping("/interaction")
    public Result<Boolean> interactionColumn(@RequestBody InteractionRequest interactionRequest) {
        return columnService.interactionColumn(interactionRequest);
    }
    
    @GetMapping("/getInteraction")
    public Result<InteractionResponse> getInteraction(@RequestParam Long columnId) {
        return columnService.getInteraction(columnId);
    }
}
