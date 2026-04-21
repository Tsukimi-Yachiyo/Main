package com.yachiyo.ColumnService.controller.internal;

import com.yachiyo.ColumnService.dto.AddColumnRequest;
import com.yachiyo.ColumnService.result.Result;
import com.yachiyo.ColumnService.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/column")
@RequiredArgsConstructor
@Validated
public class ManageController {

    private final ColumnService columnService;

    @PostMapping("/add-column")
    public Result<Boolean> addColumn(@RequestPart AddColumnRequest addColumnRequest) {
        return columnService.addColumn(addColumnRequest);
    }

    @DeleteMapping
    public Result<Boolean> deleteColumn(@RequestBody Long id) {
        return columnService.deleteColumn(id);
    }
}
