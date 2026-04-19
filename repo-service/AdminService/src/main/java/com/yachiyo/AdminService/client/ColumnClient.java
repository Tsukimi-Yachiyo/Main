package com.yachiyo.AdminService.client;

import com.yachiyo.AdminService.dto.AddColumnRequest;
import com.yachiyo.AdminService.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "column-service", path = "/internal/column")
public interface ColumnClient {

    @PutMapping()
    public Result<Boolean> addColumn(@RequestPart AddColumnRequest addColumnRequest) ;

    @DeleteMapping
    public Result<Boolean> deleteColumn(@RequestBody Long id) ;
}
