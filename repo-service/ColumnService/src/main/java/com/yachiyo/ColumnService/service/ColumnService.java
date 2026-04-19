package com.yachiyo.ColumnService.service;

import com.yachiyo.ColumnService.dto.*;
import com.yachiyo.ColumnService.result.Result;

import java.util.List;

public interface ColumnService {

    Result<List<ColumnResponse>> searchColumn(SearchRequest searchRequest);

    Result<InteractionResponse> getInteraction(Long columnId);

    Result<Boolean> interactionColumn(InteractionRequest interactionRequest);

    Result<Boolean> addColumn(AddColumnRequest addColumnRequest);

    Result<Boolean> deleteColumn(Long id);
}
