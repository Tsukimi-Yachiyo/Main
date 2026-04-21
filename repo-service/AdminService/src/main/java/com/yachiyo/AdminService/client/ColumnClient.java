package com.yachiyo.AdminService.client;

import com.yachiyo.AdminService.dto.AddColumnRequest;
import com.yachiyo.AdminService.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "column-service", path = "/internal/column")
public interface ColumnClient {

    @PostMapping("/add")
    public Result<Boolean> addColumn(@RequestPart AddColumnRequest addColumnRequest) ;

    @DeleteMapping
    public Result<Boolean> deleteColumn(@RequestPart Long id) ;
}
