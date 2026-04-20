package util

import "github.com/spf13/viper"

//配置文件,来改数据库源和端口
type Config struct {
	DbDriver       string `mapstructure:"DB_DRIVER"`
	DbSource       string `mapstructure:"DB_SOURCE"`
	ServerAddress  string `mapstructure:"SERVER_ADDRESS"`
	NacosAddr      string `mapstructure:"NACOS_ADDR"`
	NacosPort      uint64 `mapstructure:"NACOS_PORT"`
	NacosNamespace string `mapstructure:"NACOS_NAMESPACE"`
	NacosUsername  string `mapstructure:"NACOS_USERNAME"`
	NacosPassword  string `mapstructure:"NACOS_PASSWORD"`
	NacosGroup     string `mapstructure:"NACOS_GROUP"`
	ServiceName    string `mapstructure:"SERVICE_NAME"`
	ServiceIp      string `mapstructure:"SERVICE_IP"`
	ServerPort     uint64 `mapstructure:"SERVER_PORT"`
	NacosName      string `mapstructure:"NACOS_NAME"`
	SafeTables 	 string `mapstructure:"SAFE_TABLES"`
}

func LoadConfig(path string) (config Config, err error) {
	viper.AddConfigPath(path)
	viper.SetConfigName("app")
	viper.SetConfigType("env")
	viper.AutomaticEnv()

	err = viper.ReadInConfig()
	if err != nil {
		return
	}

	err = viper.Unmarshal(&config)
	return

}
