package com.yachiyo.ColumnService.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yachiyo.ColumnService.client.CoinClient;
import com.yachiyo.ColumnService.client.FileClient;
import com.yachiyo.ColumnService.dto.*;
import com.yachiyo.ColumnService.entity.Column;
import com.yachiyo.ColumnService.mapper.ColumnMapper;
import com.yachiyo.ColumnService.result.Result;
import com.yachiyo.ColumnService.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnMapper columnMapper;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private CoinClient coinClient;

    @Override
    public Result<List<ColumnResponse>> searchColumn(SearchRequest searchRequest) {
        Page<Column> page = new Page<>(searchRequest.getPageNum(), searchRequest.getPageSize() );
        LambdaQueryWrapper<Column> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Column::getTitle, searchRequest.getKeyword());
        queryWrapper.orderByDesc(Column::getId);

        List<Column> columnList = columnMapper.selectPage(page, queryWrapper).getRecords();

        List<ColumnResponse> columnResponseList = new ArrayList<>();
        for (Column column : columnList) {
            String essayUrl = fileClient.getUrl(column.getFileName(), 24*60*60, "save");
            columnResponseList.add(new ColumnResponse(column.getId(), column.getTitle(), column.getIntroduction(), column.getType(), column.getWriter(),essayUrl, column.getCreateTime()));
        }
        return Result.success(columnResponseList);
    }

    @Override
    public Result<InteractionResponse> getInteraction(Long columnId) {

        Column column = columnMapper.selectById(columnId);
        if (column == null) {
            return Result.error("404", "专栏不存在");
        }

        return Result.success(new InteractionResponse(column.getCoin(), column.getLike()));
    }

    @Override
    public Result<Boolean> interactionColumn(InteractionRequest interactionRequest) {
        Long userId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Column column = columnMapper.selectById(interactionRequest.getColumnId());
        if (column == null) {
            return Result.error("404", "专栏不存在");
        }

        if (!coinClient.changeCoin(userId, new CoinChangeRequest(userId, column.getWriter(), TradeType.TIP, 1.0)).getData()) {
            return Result.error("500", "扣除积分失败");
        }

        if (interactionRequest.getType() == InteractionType.COIN) {
            column.setCoin(column.getCoin() + 1);
        } else if (interactionRequest.getType() == InteractionType.LIKE) {
            column.setLike(column.getLike() + 1);
        }
        columnMapper.updateById(column);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> addColumn(AddColumnRequest addColumnRequest) {
        if (!fileClient.save(addColumnRequest.getFile().getName(),addColumnRequest.getFile())) {
            return Result.error("500", "保存文件失败");
        }
        Column column = new Column();
        column.setTitle(addColumnRequest.getName());
        column.setIntroduction(addColumnRequest.getDescription());
        column.setType(addColumnRequest.getType());
        column.setWriter(addColumnRequest.getWriterId());

        return Result.success(columnMapper.insert(column) > 0);
    }

    @Override
    public Result<Boolean> deleteColumn(Long id) {
        return Result.success(columnMapper.deleteById(id) > 0);
    }
}
