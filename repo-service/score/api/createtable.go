package api

import (
	"net/http"
	"github.com/gin-gonic/gin"
)

//创建任意表
type CreateTableRequest struct {
	Tablename    string		`json:"tablename" binding:"required"`
	Fields    map[string]string `json:"fields"`
}

func (server *Server) CreateTable (ctx *gin.Context) {

	var req CreateTableRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}


	err := server.store.CreateTableFromJSON(req.Tablename,req.Fields)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, errorResponse(err))
		return
	}

	ctx.JSON(http.StatusOK,  Response{
    Code: 200,
    Msg:  "success",
    Data: nil,
	}) 
}
