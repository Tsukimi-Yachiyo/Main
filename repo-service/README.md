# YachiyoService

## 项目概述

YachiyoService 是一个基于 Spring Boot 的 AI 聊天服务系统，集成了多种 AI 模型（包括 Ollama 和 OpenAI 兼容模型）、语音合成功能、用户认证系统和会话历史管理。该项目采用模块化架构设计，支持多用户访问，并提供完整的 RESTful API 接口。

## 技术栈

- **后端框架**: Spring Boot 4.0.2
- **编程语言**: Java 25
- **数据库访问**: MyBatis-Plus
- **数据库**: MySQL (通过 MyBatis-Plus)
- **AI 框架**: Spring AI (集成 Ollama 和 OpenAI 兼容模型)
- **安全框架**: Spring Security
- **身份验证**: JWT
- **API 文档**: SpringDoc OpenAPI
- **验证框架**: Hibernate Validator
- **工具库**: Hutool
- **代码简化**: Lombok
- **构建工具**: Maven

## 项目架构

项目采用模块化设计，包含以下子模块：

- **Config**: 配置模块，包含 AI 配置、安全配置、Redis 配置等
- **Common**: 公共模块
- **Controller**: 控制层，处理 HTTP 请求
- **Service**: 业务逻辑层
- **Filter**: 过滤器模块，包含 JWT 过滤器和安全过滤器
- **dto**: 数据传输对象模块，包含请求和响应对象
- **entity**: 实体模块，对应数据库表结构
- **Mapper**: 数据访问层，MyBatis-Plus 映射接口
- **Utils**: 工具类模块

## 主要功能

### AI 聊天功能
- 支持与多种 AI 模型进行对话
- 集成 Ollama 模型和 OpenAI 兼容模型
- 提供会话记忆功能，保持对话上下文

### 语音合成
- 支持文本转语音功能 (TTS)
- 可自定义语音参数

### 用户认证
- 用户注册和登录功能
- 基于 JWT 的身份验证
- 角色权限管理

### 会话历史管理
- 记录和管理对话历史
- 支持获取特定会话的历史记录
- 提供会话列表查询功能

## API 接口说明
### 安全相关
- #### 接口访问
    - v1 登录接口无需认证
    - v2 所有接口都需要在请求头中包含 `Authorization: Bearer <JWT 令牌>` 才能访问
    - v3 测试接口无需认证
- #### 其他
    - 所有接口都返回 JSON 格式
### 统一回复格式
- 成功时的回复格式
    ```json5
      {
          "code": 200, 
          "message": "成功",  // 成功时返回的消息
          "data": "message", // 成功时返回的数据
          "detail": null
      }
    ```
- 失败时的回复格式
    ```json5
      {
          "code": 400, // 失败时返回的状态码
          "message": "失败",  // 失败时返回的消息
          "data": null, // 失败时返回的数据
          "detail": "错误详情" // 失败时返回的错误详情
      }
    ```
    > 以下接口返回的格式皆为 data 字段中包含具体数据
### 认证相关接口
- `POST /api/v1/auth/login` - 用户登录
  - 请求体
    ```json5
    {
      "username": "user123", // 用户名
      "password": "password123" // 密码
    }
    ```
  - 回复
    ```text
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... // JWT 令牌
    ```
- `POST /api/v1/auth/register` - 用户注册
  - 请求体
    ```json5
      {
        "username": "newuser", // 新用户名
        "password": "newpassword123" // 新密码
      }
    ```
  - 回复
    ```text
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... // JWT 令牌
    ```

### AI 相关接口
- `POST /api/v2/ai/chat` - 与 AI 模型对话
  - 请求体
    ```json5
      {
        "message": "你好", // 用户输入的消息
        "conservationId": "12" // 会话 ID
      }
    ```
  - 回复
    ```text
        你好，我是 AI 助手。 // AI 模型的回复
    ```
- `POST /api/v2/ai/speak` - 文本转语音
  - 请求体
    ```json5
      {
        "text": "你好", // 要转换为语音的文本
      }
    ```
  - 回复
    > 直接返回语音数据，前端可以直接播放
- `POST /api/v2/ai/create` - 创建新会话
  - 请求体
    > 无请求体
  - 回复
    ```text
        13 // 新会话的 ID
    ```

### 历史记录接口
- `GET /api/v2/history/{id}` - 获取指定会话的历史记录
  - 请求体
    ```text
        13 // 会话 ID
    ```
  - 回复
    ```json5
      [
        {
          user: "你好", // 用户输入的消息
          assistant: "你好，我是 AI 助手。" // AI 模型的回复
        },
        {
          user: "你是谁", // 用户输入的消息
          assistant: "你好，我是 AI 助手。" // AI 模型的回复
        }
      ]
    ```
- `GET /api/v2/history/list` - 获取会话列表
  - 请求体
    > 无请求体
  - 回复
    ```json5
      [
        12, // 会话 ID
        13 // 会话 ID
      ]
    ```

### AI 工具
- `POST /api/v2/tools/live2d` - 获取 Live2D 的动作信息
  - 请求体
    ```text
      你好呀！我是月见八千代，无论何时，只要你在月读的虚拟世界里，我都会在你的身边，为你唱歌，讲故事，一起度过愉快的时光！（^_^）
    ```
  - 回复
    ```json5
      {
        "Version": "3",
        "Expressions": [
        {
          "Name": "smile",
          "Parameters": [
        {
          "Value": 1.0
        },
        {
          "Id": "ParamEyeSmile",
          "Value": 0.8
        }
        ],
        "FadeInTime": 0.5,
        "FadeOutTime": 0.3
        }
        ]
      }
    ```
### 测试
- `GET /api/v3/ai/hello` - 测试 AI 模型连接
  - 请求体
    > 无请求体
  - 回复
    ```text
    Hello World!
    ```
## 安装与部署

### 环境要求
- Java 25 或更高版本
- Maven 3.6 或更高版本
- MySQL 数据库
- Ollama (如需使用本地 AI 模型)

### 安装步骤

1. 克隆项目到本地
   ```bash
   git clone <repository-url>
   cd YachiyoService
   ```

2. 配置数据库连接
   在 `application.yml` 中配置数据库连接信息

3. 配置 AI 模型
   在 `application-secret.yml` 中配置 Ollama 或 OpenAI 兼容模型的连接信息

4. 编译项目
   ```bash
   mvn clean install
   ```

5. 启动服务
   ```bash
   mvn spring-boot:run
   ```

## 配置说明

项目使用多环境配置：
- `application.yml` - 主配置文件
- `application-dev.yml` - 开发环境配置
- `application-secret.yml` - 敏感信息配置（如 API 密钥等）

## 项目特点

1. **模块化设计**: 采用清晰的模块划分，便于维护和扩展
2. **AI 集成**: 支持多种 AI 模型，灵活可扩展
3. **安全性**: 使用 JWT 进行身份验证，Spring Security 提供安全保护
4. **跨域支持**: 配置 CORS 支持前端跨域访问
5. **API 文档**: 集成 Swagger UI 便于 API 测试和文档查阅
6. **数据持久化**: 使用 MyBatis-Plus 简化数据库操作
7. **会话管理**: 提供完善的对话历史和会话管理功能

## 使用说明

1. 首先通过 `/api/v1/auth/register` 接口注册账户
2. 使用 `/api/v1/auth/login` 接口登录获取 JWT Token
3. 使用 Token 访问受保护的 AI 相关接口
4. 通过 `/api/v2/ai/chat` 与 AI 进行对话
5. 使用 `/api/v2/ai/create` 创建新的对话会话
6. 通过历史接口管理对话记录

## 扩展性

项目设计具有良好的扩展性：
- 新增 AI 模型只需在配置类中添加相应 Bean
- 新增业务逻辑可在 Service 模块中实现
- 新增数据实体可在 Entity 模块中定义并添加相应的 Mapper 接口

## 贡献

欢迎提交 Issue 和 Pull Request 来改进项目.