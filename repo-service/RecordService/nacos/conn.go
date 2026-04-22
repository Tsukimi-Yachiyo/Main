package nacos

import (
	"log"
	"score/util"

	"github.com/nacos-group/nacos-sdk-go/v2/clients"
	"github.com/nacos-group/nacos-sdk-go/v2/common/constant"
	"github.com/nacos-group/nacos-sdk-go/v2/vo"
)

func ConnectNacos() {
	config, loaderr := util.LoadConfig(".")
	if loaderr != nil {
		log.Fatal("can conncet nacos", loaderr)
	}

	serverConfigs := []constant.ServerConfig{
		{IpAddr: config.NacosAddr, Port: config.NacosPort},
	} //定义Nacos端口地址

	clientConfig := constant.ClientConfig{
		NamespaceId: config.NacosNamespace, // 公共空间
		Username:    config.NacosUsername,
		Password:    config.NacosPassword,
	}

	namingClient, err := clients.NewNamingClient(
		vo.NacosClientParam{
			ClientConfig:  &clientConfig,
			ServerConfigs: serverConfigs,
		},
	)

	if err != nil {
		log.Fatal("can conncet nacos")
		return
	}

	_, err = namingClient.RegisterInstance(vo.RegisterInstanceParam{
		ServiceName: config.ServiceName,
		Ip:          config.ServiceIp,
		Port:        config.ServerPort,
		Weight:      1.0,
		ClusterName: "DEFAULT",
		Healthy:     true,
		Enable:      true,
		Ephemeral:   true,
	})

	if err != nil {
		log.Fatal("can conncet nacos")
		return
	}
}
