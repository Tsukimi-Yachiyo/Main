#如果要修改数据库源和地址和端口,或者nacos的命名等在app.env配置文件中修改 

#主要的配置文件
DB_DRIVER //切换数据库
例子DB_SOURCE = "postgres://root:123456@localhost:5432/root?sslmode=disable"
1.postgres是数据库源，要和DB_DRIVER匹配
2.root是用户名 123456是密码 切换用户进行修改
3.local:5432是数据库地址与端口
4.地址与端口后的root是要连接的数据库名
5.?sslmode=disable 本地数据库关闭ssl连接

SERVER_ADDRESS = "0.0.0.0:8890"
开启监听的地址与端口

#api部分
均以post方式进行提交
接口1:http://127.0.0.1:8890/internal/change     修改指定id指定字段的值
请求格式为json 举例为 
    {
    "id":1,                 //要修改的id
    "field":"name",         //要修改的字段
    "tablename":"tested_table",        //要修改的表名
    "value":"sda"                       //要修改的值
}
   


接口2:http://127.0.0.1:8890/api/v3/get          获取指定用户指定字段的值
请求格式为json 举例为 
{
    "id":1,                                 //要访问的id
    "tablename":"tested_table",             //要访问的表名
    "field":"secret_info"                   //要访问的指定字段   
}




接口3:http://127.0.0.1:8890/interanal/create     创建新表-定义权限
请求格式为json 举例为 
{
    "tablename": "teste_tablesad",              表名
    "fields": {
        "name": "varchar(50),api",              数据类型后参数为权限类型，不设置则默认为api
        "score": "int,api",
        "secret_info": "text,internal"
    }
}

接口4：http://127.0.0.1:8890/api/v3/add         插入一行数据
请求格式为json 举例为 
{
    "tablename":"tested_table",
    "data":{                                    data里面用键对值来插入数据
        "id":5,
        "name":"sda23",
        "score":213,
        "secret_info":"sda2"
    }
}


***创建表信息后一定要去app.env写入白名单

make docker 创建镜像
make contain 创建并启动实例