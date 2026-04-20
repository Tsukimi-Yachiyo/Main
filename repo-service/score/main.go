package main

import (
	"database/sql"
	"log"
	"score/api"
	"score/nacos"
	db "score/sqlc"
	"score/util"
	_ "github.com/lib/pq"
)


func main() {
	config, loaderr := util.LoadConfig(".")
	if loaderr != nil {
		log.Fatal("can not load config", loaderr)
	}

	conn, err := sql.Open(config.DbDriver, config.DbSource)   //连接数据库源
	if err != nil {
		log.Fatal("Cannot connect to db", err)

	}
	nacos.ConnectNacos() //连接微服务nacos


	store := db.NewStore(conn)
	server := api.NewServer(store)

	err = server.Start(config.ServerAddress)
	if err != nil {
		log.Fatal("server error!!", err)
	}
	
}
