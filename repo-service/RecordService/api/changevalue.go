package api

import (
	"net/http"
	db "score/sqlc"

	"github.com/gin-gonic/gin"
)

// 更新积分端口函数,可以修改任意表的任意字段
type ChangeScoreRequest struct {
	ID        int32       `json:"id" binding:"required"`
	Field     string      `json:"field" binding:"required"`
	Tablename string      `json:"tablename" binding:"required"`
	Value     interface{} `json:"value" binding:"required"`
}

func (server *Server) ChangeScore(ctx *gin.Context) {

	var req ChangeScoreRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}
	arg := db.UpdataScoreParams{
		ID:        req.ID,
		Field:     req.Field,
		Tablename: req.Tablename,
		Value:     req.Value,
	}

	_, err := server.store.GetId(ctx, arg.Tablename, arg.ID)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}
	err = server.store.UpdateField(ctx, arg.Tablename, arg.ID, arg.Field, arg.Value)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	ctx.JSON(http.StatusOK, Response{
		Code: 200,
		Msg:  "success",
		Data: nil,
	})
}

type Response struct {
	Code int         `json:"code"`
	Msg  string      `json:"msg"`
	Data interface{} `json:"data"`
}
