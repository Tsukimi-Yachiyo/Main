# 基础Java镜像
FROM eclipse-temurin:25-jdk


# 工作目录
WORKDIR /app

# 复制 target 下的 jar 包到容器
COPY target/*.jar app.jar

# 暴露端口 8882
EXPOSE 8882

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]