package api

import (
	db "score/sqlc"

	"github.com/gin-gonic/gin"
)

func Cors() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Header("Access-Control-Allow-Origin", "*")  //允许所有访问源，后续会进行修改
		c.Header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
		c.Header("Access-Control-Allow-Headers", "Content-Type, Authorization")
		// 处理 OPTIONS 预检请求
		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}
		c.Next()
	}
}
type Server struct {
	store  *db.Store
	router *gin.Engine
}

func NewServer(store *db.Store) *Server {
	server := &Server{store: store}
	router := gin.Default()
	router.Use(Cors())

	router.POST("/internal/change",server.ChangeScore)
	router.POST("/api/v3/get",server.GetScore)
	router.POST("/api/v3/add",server.InsertData)
	router.POST("/interanal/create",server.CreateTable)
	
	server.router = router
	return server
}

func (server *Server)  Start(address string) error {
	return server.router.Run(address)
}

func errorResponse(err error) gin.H {
  	return gin.H{"err": err.Error()}
}