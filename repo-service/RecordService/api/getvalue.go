package api

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
)

type GetscoreRequest struct {
	Id int64 `json:"id" binding:"required"`
	Tablename string `json:"tablename" binding:"required"`
	Field string `json:"field" binding:"required"`
}



func (server *Server) GetScore (ctx *gin.Context) {

	var req GetscoreRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}
	
	isApi := strings.HasPrefix(ctx.Request.URL.Path, "/api/v3")
	if isApi && !server.store.CheckFieldAccess(req.Tablename, req.Field) {
		ctx.JSON(403, gin.H{"err": "无权访问内部字段"})
		return
	}

	score,err := server.store.GetAnyField(ctx,req.Tablename,int32(req.Id),req.Field) 
	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}
	ctx.JSON(http.StatusOK, Response{
    Code: 200,
    Msg:  "success",
    Data: score,
})
}
