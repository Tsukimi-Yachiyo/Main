# YachiyoService 后端 API 文档

## 目录

- [快速开始](#快速开始)
- [概述](#概述)
- [安全认证](#安全认证)
- [统一响应格式](#统一响应格式)
- [API 接口详情](#api-接口详情)
  - [认证模块 (Auth)](#1-认证模块-auth)
  - [AI 聊天模块 (AI Chat)](#2-ai-聊天模块-ai-chat)
  - [AI 工具模块 (AI Tools)](#3-ai-工具模块-ai-tools)
  - [历史模块 (History)](#4-历史模块-history)
  - [帖子模块 (Posting)](#5-帖子模块-posting)
  - [帖子搜索模块 (Posting Search)](#6-帖子搜索模块-posting-search)
  - [评论模块 (Comment)](#7-评论模块-comment)
  - [用户详情模块 (User Detail)](#8-用户详情模块-user-detail)
  - [文件模块 (File)](#9-文件模块-file)
  - [管理员模块 (Admin)](#10-管理员模块-admin)
  - [测试模块 (Test)](#11-测试模块-test)
- [数据模型](#数据模型)
- [错误码说明](#错误码说明)
- [技术栈](#技术栈)
- [版本历史](#版本历史)
- [更新差异对比](#更新差异对比)

---

## 快速开始

### 1. 用户注册与登录

```bash
# 1. 发送验证码
POST http://localhost:8080/api/v1/auth/send-code
Content-Type: application/json

"your_email@example.com"

# 2. 注册账号
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "your_password",
  "email": "your_email@example.com",
  "code": "123456"
}

# 3. 登录获取 Token
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "username": "newuser",
  "password": "your_password"
}

# 响应示例：
{
  "code": "200",
  "message": "登录成功",
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. 使用 AI 聊天功能

```bash
# 1. 创建会话
POST http://localhost:8080/api/v2/ai/create
Authorization: Bearer YOUR_JWT_TOKEN

# 响应：返回会话 ID
{
  "code": "200",
  "message": "success",
  "data": "1234567890"
}

# 2. 发送消息
POST http://localhost:8080/api/v2/ai/chat
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "message": "你好，八千代！",
  "conversationId": "1234567890"
}
```

### 3. 发布帖子

```bash
POST http://localhost:8080/api/v2/posting/upload
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: multipart/form-data

title: 我的第一篇帖子
content: 这是帖子的内容...
type: text
coverImage: [文件]
files: [文件 1, 文件 2]
```

### 4. 文件访问

```bash
# 文件 URL 格式（需要签名，所有参数都是查询参数）
# fileName 需要 URL 编码（UTF-8）
# expire 是 Unix 时间戳（秒）
# sign 由服务端使用 MD5 生成

GET http://localhost:8080/file/generate?fileName=1%2Favatar.jpg&expire=1711500000&sign=abc123...

# 注意：签名需要后端生成，前端直接使用后端返回的完整 URL
```

---

## 概述

本文档描述了 YachiyoService 后端服务的所有 RESTful API 接口。

**服务地址**: `http://localhost:8080`

**API 版本**: v1, v2, v3

**主要功能模块**:
- 用户认证与授权（JWT）
- AI 聊天（支持流式响应）
- 语音合成（TTS）
- Live2D 模型控制
- 帖子管理（发布、点赞、收藏）
- 评论系统
- 用户详情管理
- 文件上传与访问
- RAG 资源管理（管理员）

---

## 安全认证

### 认证机制

项目使用 **JWT (JSON Web Token)** 进行身份验证。

#### 请求头要求

需要在 HTTP 请求头中携带 JWT 令牌：

```
Authorization: Bearer <your_jwt_token>
```

**JWT Token 说明**：
- **有效期**: 3600000 毫秒（1 小时）
- **签名算法**: HS256
- **包含信息**: 
  - `userID`: 用户 ID
  - `name`: 用户名
  - `sub`: 唯一标识（subject）

#### 免认证接口

以下接口无需认证即可访问：

- `/api/v1/auth/login` - 用户登录
- `/api/v1/auth/register` - 用户注册
- `/api/v1/auth/send-code` - 发送验证码
- `/api/v3/**` - 所有 v3 版本接口（测试接口）
- `/file/**` - 文件访问接口

#### 认证级别

- **USER 角色**: 需要访问 `/api/v2/**` 下的所有接口
- **AUTHENTICATED**: 其他所有需要认证的接口（已登录用户即可访问）
- **PERMIT_ALL**: 无需认证即可访问的接口（登录、注册、发送验证码、文件访问、测试接口）

#### 认证流程

1. 用户调用 `/api/v1/auth/login` 接口登录
2. 服务器验证成功后返回 JWT Token
3. 客户端在后续请求的 Header 中携带该 Token
4. 服务器通过 JwtFilter 验证 Token 有效性
5. 验证通过后设置用户上下文信息

---

## 统一响应格式

所有接口返回统一使用 `Result<T>` 格式：

```json
{
  "code": "200",
  "message": "success",
  "data": {},
  "detail": null
}
```

**字段说明**：

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| code | String | 状态码（200 表示成功，500 表示错误） | "200" |
| message | String | 响应消息 | "success" |
| data | T | 响应数据（泛型，根据接口不同而不同） | 任意类型 |
| detail | String | 详细信息（可选，通常用于调试） | null

**成功响应示例**：
```json
{
  "code": "200",
  "message": "登录成功",
  "data": "eyJhbGciOiJIUzI1NiJ9...",
  "detail": null
}
```

**错误响应示例**：
```json
{
  "code": "500",
  "message": "用户名或密码错误",
  "data": null,
  "detail": "认证失败：无效的凭证"
}
```

---

## API 接口详情

### 1. 认证模块 (Auth)

**基础路径**: `/api/v1/auth`

#### 1.1 用户登录

**接口**: `POST /api/v1/auth/login`

**认证**: ❌ 不需要

**Content-Type**: `application/json`

**请求体** (`LoginRequest`):

```json
{
  "username": "string",
  "password": "string"
}
```

**请求参数说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名或手机号 |
| password | String | 是 | 密码 |

**响应体** (`Result<String>`):

```json
{
  "code": "200",
  "message": "登录成功",
  "data": "jwt_token_string",
  "detail": null
}
```

---

#### 1.2 用户注册

**接口**: `POST /api/v1/auth/register`

**认证**: ❌ 不需要

**Content-Type**: `application/json`

**请求体** (`RegisterRequest`):

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "code": "string"
}
```

**请求参数说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| email | String | 是 | 邮箱 |
| code | String | 是 | 验证码 |

**响应体** (`Result<String>`):

```json
{
  "code": "200",
  "message": "注册成功",
  "data": "注册成功消息",
  "detail": null
}
```

---

#### 1.3 发送验证码

**接口**: `POST /api/v1/auth/send-code`

**认证**: ❌ 不需要

**Content-Type**: `application/json`

**请求体**: `String` (邮箱地址)

```
"example@qq.com"
```

**请求参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱地址 |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "验证码发送成功",
  "data": true,
  "detail": null
}
```

---

### 2. AI 聊天模块 (AI Chat)

**基础路径**: `/api/v2/ai`

**认证**: ✅ 需要 USER 角色

#### 2.1 AI 聊天

**接口**: `POST /api/v2/ai/chat`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体** (`ChatRequest`):

```json
{
  "message": "string",
  "conversationId": "string"
}
```

**请求参数说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| message | String | 是 | 消息内容 |
| conversationId | String | 是 | 会话 ID |

**响应体** (`Result<String>`):

```json
{
  "code": "200",
  "message": "success",
  "data": "AI 回复的消息内容",
  "detail": null
}
```

---

#### 2.2 流式聊天

**接口**: `POST /api/v2/ai/stream`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应类型**: `text/event-stream` (SSE)

**请求体** (`ChatRequest`):

```json
{
  "message": "string",
  "conversationId": "string"
}
```

**响应**: 服务器发送事件 (SSE) 流式返回 AI 回复

---

#### 2.3 创建会话

**接口**: `POST /api/v2/ai/create`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体**: 无

**响应体** (`Result<String>`):

```json
{
  "code": "200",
  "message": "success",
  "data": "新生成的会话 ID",
  "detail": null
}
```

---

#### 2.4 修改会话标题

**接口**: `POST /api/v2/ai/title`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体** (`ChangeConversationTitleRequest`):

```json
{
  "conversationId": 123,
  "title": "新的会话标题"
}
```

**请求参数说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| conversationId | Long | 否 | 会话 ID |
| title | String | 是 | 新的标题 |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "success",
  "data": true,
  "detail": null
}
```

---

#### 2.5 语音合成

**接口**: `POST /api/v2/ai/speak`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体** (`SpeakRequest`):

```json
{
  "text": "要合成语音的文本内容"
}
```

**请求参数说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| text | String | 是 | 待合成语音的文本 |

**响应体** (`Result<byte[]>`):

返回语音数据的字节数组

---

### 3. AI 工具模块 (AI Tools)

**基础路径**: `/api/v2/tools`

**认证**: ✅ 需要 USER 角色

#### 3.1 获取 Live2D 模型 JSON

**接口**: `POST /api/v2/tools/live2d`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体**: `String` (提示词)

```json
"生成一个开心的表情"
```

**请求参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| prompt | String | 是 | 描述表情或动作的提示词 |

**响应体** (`Result<String>`):

```json
{
  "code": "200",
  "message": "success",
  "data": "{\"Version\":\"3\",\"Expressions\":[...]}",
  "detail": null
}
```

返回符合 Live2D 规范的 JSON 字符串

---

### 4. 历史模块 (History)

**基础路径**: `/api/v2/history`

**认证**: ✅ 需要 USER 角色

#### 4.1 获取对话记忆

**接口**: `GET /api/v2/history/{id}`

**认证**: ✅ 需要

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 是 | 会话 ID |

**响应体** (`Result<List<PromptResponse>>`):

```json
{
  "code": "200",
  "message": "success",
  "data": [
    {
      "user": "用户消息",
      "assistant": "AI 回复"
    }
  ],
  "detail": null
}
```

---

#### 4.2 获取会话列表

**接口**: `GET /api/v2/history/list`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应体** (`Result<List<ConversationResponse>>`):

```json
{
  "code": "200",
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "会话标题 1"
    },
    {
      "id": 2,
      "title": "会话标题 2"
    }
  ],
  "detail": null
}
```

---

#### 4.3 删除对话记忆

**接口**: `GET /api/v2/history/clear/{id}`

**认证**: ✅ 需要

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 会话 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "success",
  "data": true,
  "detail": null
}
```

---

#### 4.4 清空所有对话记忆

**接口**: `GET /api/v2/history/clear/all`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "success",
  "data": true,
  "detail": null
}
```

---

### 5. 帖子模块 (Posting)

**基础路径**: `/api/v2/posting`

**认证**: ✅ 需要 USER 角色

#### 5.1 上传帖子

**接口**: `POST /api/v2/posting/upload`

**认证**: ✅ 需要

**Content-Type**: `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 帖子标题 |
| content | String | 是 | 帖子内容 |
| type | String | 是 | 帖子类型 |
| coverImage | MultipartFile | 否 | 封面图片 |
| files | List<MultipartFile> | 否 | 附件列表 |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "发布成功",
  "data": true,
  "detail": null
}
```

---

#### 5.2 获取帖子

**接口**: `POST /api/v2/posting/get`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<GetPostingResponse>`):

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "content": "帖子内容",
    "filenames": ["文件 1 名称", "文件 2 名称"],
    "files": ["文件 1 URL", "文件 2 URL"]
  },
  "detail": null
}
```

---

#### 5.3 点赞帖子

**接口**: `POST /api/v2/posting/like`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "点赞成功",
  "data": true,
  "detail": null
}
```

---

#### 5.4 收藏帖子

**接口**: `POST /api/v2/posting/collection`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "收藏成功",
  "data": true,
  "detail": null
}
```

---

#### 5.5 取消点赞帖子

**接口**: `POST /api/v2/posting/cancelLike`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "取消点赞成功",
  "data": true,
  "detail": null
}
```

---

#### 5.6 取消收藏帖子

**接口**: `POST /api/v2/posting/cancelCollection`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "取消收藏成功",
  "data": true,
  "detail": null
}
```

---

#### 5.7 获取帖子的收藏数

**接口**: `POST /api/v2/posting/getCollectionCount`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Long>`):

```json
{
  "code": "200",
  "message": "success",
  "data": 10,
  "detail": null
}
```

---

#### 5.8 获取帖子的点赞数

**接口**: `POST /api/v2/posting/getLikeCount`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Long>`):

```json
{
  "code": "200",
  "message": "success",
  "data": 25,
  "detail": null
}
```

---

#### 5.9 判断是否点赞帖子

**接口**: `POST /api/v2/posting/isLiked`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "success",
  "data": true,
  "detail": null
}
```

---

#### 5.10 判断是否收藏帖子

**接口**: `POST /api/v2/posting/isCollected`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "success",
  "data": false,
  "detail": null
}
```

---

#### 5.11 删除帖子

**接口**: `POST /api/v2/posting/delete`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "删除成功",
  "data": true,
  "detail": null
}
```

---

#### 5.12 获取自己的帖子

**接口**: `POST /api/v2/posting/getMyPosting`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应体** (`Result<Long>`):

```json
{
  "code": "200",
  "message": "success",
  "data": 123,
  "detail": null
}
```

返回用户发布的帖子 ID 列表

---

### 6. 帖子搜索模块 (Posting Search)

**基础路径**: `/api/v2/searching`

**认证**: ✅ 需要 USER 角色

#### 6.1 搜索帖子

**接口**: `POST /api/v2/searching/search`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 搜索关键词 |

**响应体** (`Result<List<Long>>`):

```json
{
  "code": "200",
  "message": "success",
  "data": [1, 2, 3],
  "detail": null
}
```

返回匹配的帖子 ID 列表

---

#### 6.2 获取点赞的帖子

**接口**: `POST /api/v2/searching/like`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应体** (`Result<List<Long>>`):

```json
{
  "code": "200",
  "message": "success",
  "data": [1, 2, 3],
  "detail": null
}
```

返回用户点赞的帖子 ID 列表

---

#### 6.3 获取收藏的帖子

**接口**: `POST /api/v2/searching/collection`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应体** (`Result<List<Long>>`):

```json
{
  "code": "200",
  "message": "success",
  "data": [4, 5, 6],
  "detail": null
}
```

返回用户收藏的帖子 ID 列表

---

#### 6.4 获取帖子简述

**接口**: `POST /api/v2/searching/encapsulate`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<PostEncapsulateResponse>`):

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "title": "帖子标题",
    "posterId": 123,
    "coverImage": "封面图片 URL"
  },
  "detail": null
}
```

---

### 7. 评论模块 (Comment)

**基础路径**: `/api/v1/auth`

**认证**: ✅ 需要（继承默认安全配置）

#### 7.1 添加评论

**接口**: `POST /api/v1/auth/add-comment`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体** (`CommentRequest`):

```json
{
  "postingId": 123,
  "content": "评论内容"
}
```

**请求参数说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |
| content | String | 是 | 评论内容 |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "评论成功",
  "data": true,
  "detail": null
}
```

---

#### 7.2 获取评论列表

**接口**: `POST /api/v1/auth/get-comment-list`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体**: `Long` (帖子 ID)

```
123
```

**请求参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postingId | Long | 是 | 帖子 ID |

**响应体** (`Result<List<CommentResponse>>`):

```json
{
  "code": "200",
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 100,
      "postingId": 123,
      "content": "评论内容 1"
    },
    {
      "id": 2,
      "userId": 101,
      "postingId": 123,
      "content": "评论内容 2"
    }
  ],
  "detail": null
}
```

---

#### 7.3 删除评论

**接口**: `POST /api/v1/auth/delete-comment`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体**: `Long` (评论 ID)

```
1
```

**请求参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| commentId | Long | 是 | 评论 ID |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "删除成功",
  "data": true,
  "detail": null
}
```

---

### 8. 用户详情模块 (User Detail)

**基础路径**: `/api/v1/user/detail`

**认证**: ✅ 需要（继承默认安全配置）

#### 8.1 更新用户头像

**接口**: `POST /api/v1/user/detail/avatar/update`

**认证**: ✅ 需要

**Content-Type**: `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| avatar | MultipartFile | 是 | 头像文件 |

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "更新成功",
  "data": true,
  "detail": null
}
```

---

#### 8.2 获取用户头像

**接口**: `POST /api/v1/user/detail/avatar/get`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应体** (`Result<String>`):

```json
{
  "code": "200",
  "message": "success",
  "data": "头像 URL",
  "detail": null
}
```

---

#### 8.3 获取用户详情

**接口**: `POST /api/v1/user/detail/detail/get`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**响应体** (`Result<UserDetailResponse>`):

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "userName": "用户名",
    "userIntroduction": "个人简介",
    "userCity": "城市",
    "userGender": "性别",
    "userPhone": "年龄",
    "userBirthday": "2000-01-01"
  },
  "detail": null
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| userName | String | 用户名 |
| userIntroduction | String | 个人简介 |
| userCity | String | 城市 |
| userGender | String | 性别 |
| userPhone | String | 年龄（字段名可能为笔误） |
| userBirthday | Date | 生日 |

---

#### 8.4 更新用户详情

**接口**: `POST /api/v1/user/detail/detail/update`

**认证**: ✅ 需要

**Content-Type**: `application/json`

**请求体** (`UserDetailResponse`):

```json
{
  "userName": "新用户名",
  "userIntroduction": "新简介",
  "userCity": "新城市",
  "userGender": "新性别",
  "userPhone": "新年纪",
  "userBirthday": "2000-01-01"
}
```

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "更新成功",
  "data": true,
  "detail": null
}
```

---

#### 8.5 获取某用户详情

**接口**: `POST /api/v1/user/detail/detail/get/user`

**认证**: ✅ 需要

**Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户 ID |

**响应体** (`Result<PosterDetailResponse>`):

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "userName": "用户名",
    "userAvatar": "用户头像 URL"
  },
  "detail": null
}
```

---

### 9. 文件模块 (File)

**基础路径**: `/file`

**认证**: ❌ 不需要（通过签名验证）

**说明**: 
- 文件访问接口使用签名验证机制，确保文件访问的安全性
- 签名由服务端生成，包含过期时间
- 文件名需要进行 URL 编码（UTF-8）
- 文件存储路径：`{项目根目录}/Common/src/main/resources/static/upload/`

#### 9.1 获取文件

**接口**: `GET /file/generate`

**认证**: ❌ 不需要（使用签名验证）

**请求参数**（查询参数）:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fileName | String | 是 | 文件名（需要 URL 编码，UTF-8） |
| expire | Long | 是 | 过期时间戳（秒，Unix 时间戳） |
| sign | String | 是 | 签名（MD5 加密） |

**签名生成规则**:
```java
String sign = md5Hex(fileName + expire + KEY);
```
其中 `KEY = "yachiyo_file_url" + 系统启动时间戳`

**响应**: 

- **成功 (200)**: 返回文件内容（根据文件类型自动设置对应的 Content-Type）
  - 图片：`image/jpeg`, `image/png`, `image/gif` 等
  - 音频：`audio/mpeg`, `audio/mp3`, `audio/wav` 等
  - 视频：`video/mp4`, `video/webm` 等
  - 文本：`text/plain`, `text/html` 等
  - 其他：`application/octet-stream`
- **失败 (403)**: 签名验证失败或已过期
- **失败 (404)**: 文件不存在

**使用示例**:
```bash
# 1. 假设文件路径为：1/avatar.jpg
# 2. 过期时间：1711500000（Unix 时间戳，秒）
# 3. 生成签名（需要后端生成）

# 请求示例（fileName 需要 URL 编码）
GET http://localhost:8080/file/generate?fileName=1%2Favatar.jpg&expire=1711500000&sign=abc123def456...

# 使用 cURL 示例
curl -X GET "http://localhost:8080/file/generate?fileName=1%2Favatar.jpg&expire=1711500000&sign=abc123"
```

**注意事项**:
1. `fileName` 参数需要进行 URL 编码（使用 UTF-8 编码）
2. `expire` 是 Unix 时间戳（秒），不是毫秒
3. 签名验证失败会返回 403 状态码
4. 文件不存在会返回 404 状态码
5. 签名过期后（当前时间 > expire），签名失效

---

### 10. 管理员模块 (Admin)

**基础路径**: `/api/yachiyo/168/mini/admin`

**认证**: ✅ 需要（需要管理员权限）

**说明**: 该模块用于管理 RAG（检索增强生成）资源，仅管理员可访问。

#### 10.1 上传资源

**接口**: `POST /api/yachiyo/168/mini/admin/upload`

**认证**: ✅ 需要

**Content-Type**: `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| files | List<MultipartFile> | 是 | 资源文件列表（支持多个文件） |

**说明**: 
- 上传的文件将用于 RAG（检索增强生成）系统的知识库
- 支持的文件类型：文本文件、PDF、Word 文档等
- 文件大小受服务器配置限制（默认 10MB）

**响应体** (`Result<Boolean>`):

```json
{
  "code": "200",
  "message": "上传成功",
  "data": true,
  "detail": null
}
```

---

### 11. 测试模块 (Test)

**基础路径**: `/api/v3/test`

**认证**: ❌ 不需要（v3 接口全部开放）

#### 11.1 测试接口

**接口**: `GET /api/v3/test/hello`

**认证**: ❌ 不需要

**Content-Type**: `text/plain`

**响应**: 

```
Hello World!
```

---

## 数据模型

### DTO 类

#### LoginRequest
```java
{
  "username": "String",  // 用户名或手机号
  "password": "String"   // 密码
}
```

#### RegisterRequest
```java
{
  "username": "String",  // 用户名
  "password": "String",  // 密码
  "email": "String",     // 邮箱
  "code": "String"       // 验证码
}
```

#### ChatRequest
```java
{
  "message": "String",        // 消息内容
  "conversationId": "String"  // 会话 ID
}
```

#### UploadPostingRequest
```java
{
  "title": "String",
  "content": "String",
  "type": "String",
  "coverImage": "MultipartFile",
  "files": "List<MultipartFile>"
}
```

#### CommentRequest
```java
{
  "postingId": "Long",
  "content": "String"
}
```

#### UserDetailResponse
```java
{
  "userName": "String",        // 用户名
  "userIntroduction": "String",// 个人简介
  "userCity": "String",        // 城市
  "userGender": "String",      // 性别
  "userPhone": "String",       // 年龄（注：字段名可能为笔误，实际表示年龄）
  "userBirthday": "Date"       // 生日
}
```

#### ConversationResponse
```java
{
  "id": "Long",
  "title": "String"
}
```

#### PostEncapsulateResponse
```java
{
  "title": "String",
  "posterId": "Long",
  "coverImage": "String"
}
```

#### GetPostingResponse
```java
{
  "content": "String",
  "filenames": "List<String>",
  "files": "List<String>"
}
```

#### CommentResponse
```java
{
  "id": "Long",
  "userId": "Long",
  "postingId": "Long",
  "content": "String"
}
```

#### PosterDetailResponse
```java
{
  "userName": "String",
  "userAvatar": "String"
}
```

#### PromptResponse
```java
{
  "user": "String",
  "assistant": "String"
}
```

### 实体类

#### User
```java
{
  "id": "Long",         // 用户 ID
  "name": "String",     // 用户名
  "password": "String", // 密码（加密）
  "role": "String",     // 角色
  "email": "String"     // 邮箱
}
```

#### UserDetail
```java
{
  "userId": "Long",
  "userIntroduction": "String",
  "userName": "String",
  "userCity": "String",
  "userGender": "String",
  "userBirthday": "Date"
}
```

#### Posting
```java
{
  "id": "Long",
  "userId": "Long",
  "title": "String",
  "content": "String",
  "type": "String"
}
```

#### PostDetail
```java
{
  "id": "Long",
  "love": "Long",        // 点赞数
  "collection": "Long",  // 收藏数
  "reading": "Long"      // 阅读数
}
```

#### Comment
```java
{
  "id": "Long",
  "userId": "Long",
  "postingId": "Long",
  "content": "String"
}
```

#### Conversation
```java
{
  "id": "Long",
  "userId": "Long",
  "title": "String",
  "createTime": "Date",
  "updateTime": "Date"
}
```

#### Message
```java
{
  "id": "String",           // 会话 ID
  "content": "String",      // 消息内容
  "type": "MessageType",    // 消息类型（USER/ASSISTANT）
  "time": "Date"            // 时间戳
}
```

#### LinkLike
```java
{
  "userId": "Long",
  "postingId": "Long"
}
```

#### LinkCollection
```java
{
  "userId": "Long",
  "postingId": "Long"
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证/认证失败 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 技术栈

### 核心框架
- **Spring Boot**: 3.x
- **Spring Security**: 安全认证框架
- **Spring AI**: AI 模型集成框架

### 数据库与存储
- **PostgreSQL**: 主数据库（开发环境）
- **MySQL**: 备选数据库（生产环境）
- **Redis**: 缓存与会话存储
- **MyBatis-Plus**: ORM 框架

### AI 与模型
- **OpenAI SDK**: 兼容 OpenAI 格式的 AI 模型（通义千问）
- **Spring AI Chat Memory**: 对话记忆管理
- **PGVector**: 向量数据库（用于 RAG）

### 安全与认证
- **JWT (io.jsonwebtoken)**: JSON Web Token 认证
- **Hutool**: Java 工具库（包含路径匹配等）

### 开发工具
- **Lombok**: 简化 Java 代码
- **Swagger/OpenAPI**: API 文档注解
- **Jakarta Validation**: 参数校验

### 其他
- **Live2D**: 虚拟主播模型控制
- **TTS**: 文本转语音
- **SSE (Server-Sent Events)**: 流式响应支持

---

## 版本历史

- **v1**: 基础认证、用户管理、评论功能
- **v2**: AI 聊天、帖子管理、搜索功能
- **v3**: 测试接口

---

## 更新差异对比

### v1.2 vs v1.1 更新内容

本次更新（v1.2）相对于上一版本（v1.1）的主要变化：

#### 1. 文件模块 (File) - 重大修正 ⚠️

**v1.1 版本**:
- 接口路径：`GET /file/generate/{fileName}`（路径参数）
- expire 单位：毫秒
- 缺少签名生成规则说明

**v1.2 版本**:
- ✅ 接口路径：`GET /file/generate`（查询参数）
- ✅ expire 单位：秒（Unix 时间戳）
- ✅ 添加签名生成规则：`sign = md5Hex(fileName + expire + KEY)`
- ✅ 添加 fileName URL 编码说明（UTF-8）
- ✅ 添加文件存储路径说明
- ✅ 补充更多注意事项
- ✅ 更新快速开始中的文件访问示例

**影响**: 前端需要修改文件访问 URL 的拼接方式

#### 2. 格式统一化改进 📋

**v1.1 版本**:
- 部分接口缺少 Content-Type 说明
- 响应示例不够详细

**v1.2 版本**:
- ✅ 所有接口都添加了 Content-Type 说明
- ✅ 统一了请求参数表格格式
- ✅ 添加了成功和错误响应示例

#### 3. 新增内容 🆕

- ✅ 快速开始部分（包含实际调用示例）
- ✅ 认证流程详细说明（5 步流程）
- ✅ JWT Token 详细说明（有效期、算法、包含信息）
- ✅ Message 实体类说明
- ✅ 技术栈分类说明

---

**文档生成时间**: 2026-03-26  
**文档版本**: v1.2 (完整更新版)

**更新日志**:

### v1.2 (2026-03-26)
- 修正文件模块接口路径（路径参数 → 查询参数）
- 修正 expire 时间单位（毫秒 → 秒）
- 添加签名生成规则详细说明
- 添加 fileName URL 编码要求
- 统一所有接口的 Content-Type 标注
- 添加响应示例（成功和错误）
- 添加快速开始部分
- 完善认证流程说明
- 添加更新差异对比章节

### v1.1 (2026-03-26)
- 添加快速开始部分
- 完善安全认证流程
- 补充 Content-Type 说明
- 更新技术栈分类
- 添加 Message 实体类
- 修正 UserDetailResponse 字段说明
