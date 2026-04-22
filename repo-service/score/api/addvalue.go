package api
import (

	"github.com/gin-gonic/gin"
)
type InsertRequest struct {
	Tablename string                 `json:"tablename" binding:"required"`
	Data      map[string]interface{} `json:"data" binding:"required"`
}

func (server *Server) InsertData(ctx *gin.Context) {
	var req InsertRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(400, gin.H{"err": "参数错误"})
		return
	}


	id, err := server.store.InsertData(req.Tablename, req.Data)
	if err != nil {
		ctx.JSON(400, gin.H{"err": "插入失败: " + err.Error()})
		return
	}

	// 成功返回
	ctx.JSON(200, gin.H{
		"msg": "插入成功",
		"id":  id,
	})
}