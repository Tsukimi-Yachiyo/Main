# Yachiyo项目API文档

## 1. 项目概述

Yachiyo是一个完整的AI聊天系统，集成了AI对话、语音合成和Live2D可视化功能。系统采用前后端分离架构，提供多种客户端选择。

### 主要特性

- AI智能对话（支持Ollama和OpenAI模型）
- 流式对话输出（Server-Sent Events）
- 文本转语音功能（支持日语）
- Live2D虚拟形象展示
- 多客户端支持（Web前端、WPF桌面应用、Python桌面应用）
- 对话历史记忆功能
- 用户认证与授权
- 用户个人资料管理

### 项目组件

| 组件 | 路径 | 技术栈 | 说明 |
|------|------|--------|------|
| 后端服务 | `YachiyoService` | Spring Boot 4.0.2 + Spring AI 2.0.0-M2 + MyBatis-Plus | 提供AI聊天和语音合成服务 |
| Web前端 | `YachiyoWeb/web` | Vue 3.5.29 + Vite 7.3.1 + Vue Router 5.0.3 + Axios 1.13.6 | 完整功能的Web应用，支持Live2D |
| WPF前端 | `EasyYachiyoClient` | C# .NET 8.0 + WPF + CefSharp | 完整功能的桌面应用，支持Live2D |
| Python前端 | `YachiyoClient` | Python 3.x + Tkinter | 轻量级桌面应用 |
| 旧Python前端 | `EasyYachiyoClient-old` | Python 3.x | 早期版本 |
| 模型文件 | `ollama` | Ollama Modelfile | AI模型训练配置 |

## 2. API接口文档

### 2.1 基础信息

- **默认基础URL**: `http://localhost:8080`
- **API文档**: `http://localhost:8080/swagger-ui.html` (Swagger UI)
- **OpenAPI文档**: `http://localhost:8080/v3/api-docs`

### 2.2 接口列表（v1）

| 接口路径 | 方法 | 功能描述 | 请求内容类型 | 响应内容类型 |
|---------|------|----------|-------------|-------------|
| `/api/v1/ai/chat` | POST | AI聊天 | `text/plain` | `text/plain` |
| `/api/v1/ai/speak` | POST | 文本转语音 | `text/plain` | `application/octet-stream` |

### 2.3 接口列表（v2）

| 接口路径 | 方法 | 功能描述 | 请求内容类型 | 响应内容类型 | 认证要求 |
|---------|------|----------|-------------|-------------|---------|
| `/api/v2/ai/chat` | POST | AI聊天 | `application/json` | `application/json` | Bearer Token |
| `/api/v2/ai/stream` | POST | 流式AI聊天 | `application/json` | `text/event-stream` | Bearer Token |
| `/api/v2/ai/create` | POST | 创建新对话 | `application/json` | `application/json` | Bearer Token |
| `/api/v2/ai/speak` | POST | 文本转语音 | `application/json` | `application/octet-stream` | Bearer Token |
| `/api/v2/history/list` | GET | 获取对话列表 | - | `application/json` | Bearer Token |
| `/api/v2/history/{conversationId}` | GET | 获取对话历史 | - | `application/json` | Bearer Token |
| `/api/v1/user/detail/detail/get` | POST | 获取用户详情 | `application/json` | `application/json` | Bearer Token |
| `/api/v1/user/detail/detail/update` | POST | 更新用户详情 | `application/json` | `application/json` | Bearer Token |
| `/api/v1/user/detail/avatar/get` | POST | 获取用户头像 | `application/json` | `application/json` | Bearer Token |
| `/api/v1/user/detail/avatar/update` | POST | 更新用户头像 | `multipart/form-data` | `application/json` | Bearer Token |

### 2.4 详细接口说明

#### 2.4.1 聊天接口（v1）

**接口**: `POST /api/v1/ai/chat`

**功能**: 发送消息到AI并获取回复

**请求参数**:
- 请求体: 纯文本消息

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/ai/chat" \
  -H "Content-Type: text/plain" \
  -d "你好，今天天气怎么样？"
```

**响应示例**:
```
今天天气很好，阳光明媚，适合外出活动。
```

**实现说明**:
- 使用Spring AI的ChatClient与AI模型交互
- 支持Ollama和OpenAI模型
- 内置对话历史记忆功能（使用JDBC存储）

#### 2.4.2 语音合成接口（v1）

**接口**: `POST /api/v1/ai/speak`

**功能**: 将文本转换为日语语音

**请求参数**:
- 请求体: 纯文本消息（中文或日文）

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/v1/ai/speak" \
  -H "Content-Type: text/plain" \
  -d "你好，我是Yachiyo。" \
  --output output.wav
```

**响应**:
- 音频二进制数据（WAV格式）

**实现说明**:
1. 使用百度翻译API将文本自动翻译成日语
2. 调用外部TTS服务（默认 `http://0.0.0.0:9882`）生成语音
3. 返回音频二进制数据

#### 2.4.3 聊天接口（v2）

**接口**: `POST /api/v2/ai/chat`

**功能**: 发送消息到AI并获取回复（带对话ID）

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "message": "你好，今天天气怎么样？",
  "conversationId": "1"
}
```

**响应示例**:
```json
{
  "code": "200",
  "message": "成功",
  "data": "今天天气很好，阳光明媚，适合外出活动。"
}
```

#### 2.4.4 流式聊天接口（v2）

**接口**: `POST /api/v2/ai/stream`

**功能**: 流式发送消息到AI并实时获取回复

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "message": "你好，今天天气怎么样？",
  "conversationId": "1"
}
```

**响应格式**: Server-Sent Events (SSE)
```
data:今
data:天
data:天
data:气
data:很
data:好
data:，
data:[DONE]
```

#### 2.4.5 创建对话接口（v2）

**接口**: `POST /api/v2/ai/create`

**功能**: 创建新的对话

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "conversationId": "2"
  }
}
```

## 3. 前端调用示例

### 3.1 Python前端调用

```python
import requests

# 发送聊天请求
def chat(message):
    url = "http://localhost:8080/api/v1/ai/chat"
    response = requests.post(url, data=message)
    return response.text

# 发送语音合成请求
def text_to_speech(text, output_file="output.wav"):
    url = "http://localhost:8080/api/v1/ai/speak"
    response = requests.post(url, data=text)
    with open(output_file, "wb") as f:
        f.write(response.content)

# 使用示例
if __name__ == "__main__":
    reply = chat("你好，今天天气怎么样？")
    print(f"AI回复: {reply}")
    
    text_to_speech(reply, "reply.wav")
```

### 3.2 WPF前端调用 (C#)

```csharp
using System.Net.Http;
using System.Text;
using System.Text.Json;

public class YachiyoApiClient
{
    private readonly HttpClient _httpClient;
    private readonly string _baseUrl;

    public YachiyoApiClient(string baseUrl = "http://localhost:8080")
    {
        _httpClient = new HttpClient();
        _baseUrl = baseUrl;
        _httpClient.Timeout = TimeSpan.FromMinutes(8);
    }

    // 发送聊天请求
    public async Task<string> ChatAsync(string message)
    {
        var content = new StringContent(message, Encoding.UTF8, "text/plain");
        var response = await _httpClient.PostAsync($"{_baseUrl}/api/v1/ai/chat", content);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadAsStringAsync();
    }

    // 发送语音合成请求
    public async Task<byte[]> TextToSpeechAsync(string text)
    {
        var content = new StringContent(text, Encoding.UTF8, "text/plain");
        var response = await _httpClient.PostAsync($"{_baseUrl}/api/v1/ai/speak", content);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadAsByteArrayAsync();
    }
}
```

### 3.3 Web前端调用 (JavaScript/Vue)

```javascript
import axios from 'axios';

// 配置API客户端
const apiClient = axios.create({
  baseURL: '',
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 流式聊天
function streamChat(message, conversationId, onData, onComplete, onError) {
  const token = localStorage.getItem('token');
  const controller = new AbortController();

  fetch('/api/v2/ai/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify({ message, conversationId: String(conversationId) }),
    signal: controller.signal
  })
  .then(response => {
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';

    function processChunk() {
      return reader.read().then(({ done, value }) => {
        if (done) {
          onComplete && onComplete();
          return;
        }
        buffer += decoder.decode(value, { stream: true });
        
        while (buffer.includes('data:')) {
          const dataIndex = buffer.indexOf('data:');
          const nextDataIndex = buffer.indexOf('data:', dataIndex + 5);
          let dataChunk;
          if (nextDataIndex === -1) {
            dataChunk = buffer.slice(dataIndex + 5);
            buffer = '';
          } else {
            dataChunk = buffer.slice(dataIndex + 5, nextDataIndex);
            buffer = buffer.slice(nextDataIndex);
          }
          
          const dataStr = dataChunk.trim();
          if (dataStr === '[DONE]') {
            onComplete && onComplete();
            return;
          }
          if (dataStr) {
            onData && onData(dataStr);
          }
        }
        return processChunk();
      });
    }
    return processChunk();
  })
  .catch(error => {
    onError && onError(error);
  });
}
```

## 4. 技术栈与依赖

### 4.1 后端服务依赖

| 依赖 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.2 | 基础框架 |
| Spring AI | 2.0.0-M2 | AI集成框架 |
| MyBatis-Plus | 3.5.16 | ORM框架 |
| MySQL Connector | - | 数据库驱动 |
| SpringDoc OpenAPI | 2.5.0 | API文档生成 |
| Lombok | - | 代码简化工具 |
| Hutool | 5.8.40 | 工具类库 |
| Java | 25 | 运行环境 |

### 4.2 前端依赖

#### Web前端
| 依赖 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.29 | 前端框架 |
| Vue Router | 5.0.3 | 路由管理 |
| Axios | 1.13.6 | HTTP客户端 |
| Vite | 7.3.1 | 构建工具 |
| TypeScript | 5.8.3 | 类型系统（可选） |
| Node.js | ^20.19.0 \|\| >=22.12.0 | 运行环境 |

#### WPF前端
- **.NET 8.0**: 运行环境
- **CefSharp**: Web视图组件（用于Live2D展示）
- **SevenZipExtractor**: 压缩解压功能

#### Python前端
- **Python 3.x**: 运行环境
- **tkinter**: GUI框架
- **requests**: HTTP客户端库

## 5. Web前端项目详细说明

### 5.1 项目概述

YachiyoWeb是一个基于Vue 3的现代化Web前端应用，提供完整的AI聊天体验，包括Live2D虚拟形象展示、流式对话、语音合成等功能。

### 5.2 目录结构

```
YachiyoWeb/web/
├── public/                     # 静态资源
│   ├── Core/                  # Live2D核心库
│   ├── Framework/             # Live2D框架
│   ├── resource/              # 资源文件
│   │   └── 八千代辉夜姬/    # Live2D模型
│   └── favicon.ico
├── src/                        # 源代码
│   ├── assets/                # 静态资源
│   ├── components/            # 组件
│   │   ├── Live2DModel/      # Live2D组件
│   │   └── UserProfilePopover/ # 用户资料弹窗
│   ├── composables/           # Vue组合式函数
│   ├── live2d-demo/           # Live2D示例
│   ├── pages/                 # 页面组件
│   │   ├── ChatHome/          # 聊天主页
│   │   ├── Login/             # 登录页
│   │   └── UserSettings/      # 用户设置页
│   ├── router/                # 路由配置
│   ├── services/              # API服务
│   ├── styles/                # 全局样式
│   ├── templates/             # 模板文件
│   ├── App.vue                # 根组件
│   └── main.js                # 入口文件
├── index.html                 # HTML模板
├── vite.config.js             # Vite配置
├── package.json               # 依赖配置
└── jsconfig.json              # JS配置
```

### 5.3 主要功能模块

#### 5.3.1 认证模块 (useAuth.js)

负责用户认证状态管理，包括token的存储和获取、登录/登出功能。

#### 5.3.2 对话管理模块 (useConversations.js)

负责对话列表的加载、创建和切换。

#### 5.3.3 消息管理模块 (useMessages.js)

负责消息的发送、接收、流式展示，以及消息历史的加载。

#### 5.3.4 语音模块 (useVoice.js)

负责语音合成和播放功能。

#### 5.3.5 用户资料模块 (useUserProfile.js)

负责用户详情的获取和更新，包括头像上传。

#### 5.3.6 Live2D模块 (Live2DModel.vue)

负责Live2D模型的加载、渲染和交互。

### 5.4 组件使用说明

#### 5.4.1 Live2DModel组件

```vue
<template>
  <Live2DModel />
</template>

<script setup>
import Live2DModel from '@/components/Live2DModel/Live2DModel.vue';
</script>
```

**功能**: 展示Live2D虚拟形象，支持基本的动画效果。

#### 5.4.2 UserProfilePopover组件

```vue
<template>
  <UserProfilePopover />
</template>

<script setup>
import UserProfilePopover from '@/components/UserProfilePopover/UserProfilePopover.vue';
</script>
```

**功能**: 展示和编辑用户个人资料。

### 5.5 Composables使用说明

#### 5.5.1 useChatHome

主页面的组合式函数，整合了所有聊天相关功能。

```javascript
import { useChatHome } from '@/composables/useChatHome.js';

const {
  username,
  userAvatar,
  conversations,
  currentConversationId,
  messages,
  inputMessage,
  isLoading,
  isTyping,
  sendMessage,
  createNewConversation,
  playVoice,
  logout
} = useChatHome();
```

#### 5.5.2 useAuth

认证相关功能。

```javascript
import { useAuth } from '@/composables/useAuth.js';

const { token, logout, setToken } = useAuth();
```

## 6. 开发环境配置指南

### 6.1 后端服务开发环境

#### 环境要求
- JDK 25+
- Maven 3.6+
- MySQL 8.0+
- Ollama (可选，本地AI模型)

#### 构建步骤
```bash
cd YachiyoService
mvn clean package
```

#### 运行服务
```bash
cd YachiyoService/Controller
mvn spring-boot:run
```

或使用JAR包运行:
```bash
java -jar Controller/target/Controller-0.0.1-SNAPSHOT.jar
```

#### 配置文件

**主配置文件**: `Config/src/main/resources/application.yml`
```yaml
spring:
  application:
    name: Yachiyo
  profiles:
    include: secret
    active: dev

server:
  port: 8080
```

**密钥配置文件**: `Config/src/main/resources/application-secret.yml` (需自行创建)
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: yachiyo
    openai:
      api-key: your-api-key-here

  datasource:
    url: jdbc:mysql://localhost:3306/yachiyo
    username: root
    password: your-password-here
```

### 6.2 Web前端开发环境

#### 环境要求
- Node.js ^20.19.0 || >=22.12.0
- npm 或 yarn

#### 安装依赖
```bash
cd YachiyoWeb/web
npm install
```

#### 开发模式运行
```bash
npm run dev
```

开发服务器将在 `http://localhost:5173` 启动。

#### 生产构建
```bash
npm run build
```

构建产物将输出到 `dist` 目录。

#### 预览生产构建
```bash
npm run preview
```

#### 配置说明

**Vite配置**: `vite.config.js`

主要配置项:
- 开发服务器代理: 将 `/api` 请求代理到 `http://localhost:8080`
- 路径别名: `@` 指向 `src` 目录
- 允许的主机列表: 包括ngrok和自定义域名

### 6.3 WPF前端开发环境

1. 使用Visual Studio打开 `EasyYachiyoClient/EasyYachiyoClient.sln`
2. 还原NuGet包
3. 构建解决方案 (Release模式)
4. 运行 `bin/Release/net8.0-windows/EasyYachiyoClient.exe`

### 6.4 Python前端开发环境

1. 安装依赖:
```bash
cd YachiyoClient
pip install requests
```

2. 配置API地址 (编辑 `config.json`):
```json
{"url": "http://localhost:8080/api/v1/ai/chat"}
```

3. 运行应用:
```bash
python yachiyo.py
```

## 7. 配置选项

### 7.1 后端配置

| 配置项 | 文件 | 说明 |
|--------|------|------|
| 服务端口 | application.yml | 默认8080 |
| AI模型配置 | AIConfig.java | Ollama/OpenAI模型设置 |
| 翻译API配置 | TransUtil.java | 百度翻译API密钥 |
| 数据库配置 | application-secret.yml | MySQL连接信息 |
| TTS服务地址 | TransformConfig.java | 外部TTS服务地址 |

### 7.2 前端配置

#### Web前端
- **API代理配置**: `vite.config.js` 中的 `server.proxy`
- **开发服务器配置**: `vite.config.js` 中的 `server` 选项
- **路径别名**: `vite.config.js` 中的 `resolve.alias`

#### WPF前端
- **API地址配置**: `AddressConfigurationManager.cs`
- **设置管理**: `SettingsManager.cs`
- **日志目录**: `bin/Debug/net8.0-windows/logs/`

#### Python前端
- **API地址配置**: `config.json`

## 8. 代码规范

### 8.1 后端代码规范

- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 接口返回统一格式
- 代码注释规范

### 8.2 Web前端代码规范

- 使用Vue 3 Composition API
- 组件使用 `<script setup>` 语法
- Composables命名以 `use` 开头
- 遵循ESLint规范
- 组件文件结构: 模板、样式分离

### 8.3 Git提交规范

- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关

## 9. 项目结构

```
TsukimiYachiyo/
├── EasyYachiyoClient/         # WPF前端
│   ├── Model/                 # 数据模型 (Conversation, Message)
│   ├── Utils/                 # 工具类
│   ├── ViewModel/             # 视图模型 (MVVM模式)
│   ├── resource/              # 资源文件 (JAR, JDK, Live2D等)
│   └── Views/                 # 窗口视图
├── EasyYachiyoClient-old/     # 旧Python前端
├── ollama/                    # Ollama模型文件
│   ├── v0.0.1/                # 模型版本1
│   └── v0.0.2/                # 模型版本2
├── YachiyoClient/             # Python前端
│   ├── yachiyo.py             # 主程序
│   └── config.json            # 配置文件
├── YachiyoService/            # 后端服务 (Spring Boot多模块)
│   ├── Common/                # 通用模块
│   ├── Config/                # 配置模块
│   ├── Controller/            # 控制器 (API接口)
│   ├── Service/               # 业务逻辑层
│   ├── dto/                   # 数据传输对象
│   ├── entity/                # 实体类
│   ├── Mapper/                # MyBatis映射
│   └── Utils/                 # 工具类
└── YachiyoWeb/                # Web前端
    └── web/                   # Web应用
        ├── public/            # 静态资源
        ├── src/               # 源代码
        └── package.json       # 依赖配置
```

## 10. 故障排除

### 10.1 常见问题

#### 1. API连接失败
**症状**: 前端无法连接后端
**解决方案**:
- 检查后端服务是否正常运行: `curl http://localhost:8080/actuator/health`
- 检查防火墙设置
- 确认前端配置的API地址正确
- Web前端: 检查Vite代理配置

#### 2. 语音合成失败
**症状**: 无法生成语音
**解决方案**:
- 检查外部TTS服务是否运行在 `http://0.0.0.0:9882`
- 检查百度翻译API密钥配置
- 查看后端控制台日志

#### 3. AI模型无响应
**症状**: 聊天接口超时或无回复
**解决方案**:
- 检查Ollama服务是否运行: `curl http://localhost:11434/api/tags`
- 确认模型已加载: `ollama list`
- 检查application-secret.yml中的模型配置

#### 4. 前端启动失败
**症状**: 应用无法启动
**解决方案**:
- Web前端: 确认Node.js版本符合要求
- WPF: 确认.NET 8.0运行时已安装
- Python: 确认Python 3.x和requests库已安装
- 检查依赖是否完整

#### 5. Web前端CORS错误
**症状**: 浏览器控制台显示CORS错误
**解决方案**:
- 确保Vite开发服务器正在运行
- 检查vite.config.js中的代理配置
- 确认后端服务已启动

#### 6. Live2D模型加载失败
**症状**: Live2D模型无法显示
**解决方案**:
- 检查模型文件路径是否正确
- 确认Live2D核心库已正确加载
- 检查浏览器控制台错误信息

### 10.2 日志位置

| 组件 | 日志位置 |
|------|---------|
| 后端服务 | 控制台输出 |
| Web前端 | 浏览器控制台 |
| WPF前端 | `bin/Debug/net8.0-windows/logs/` |
| Ollama | 系统日志或Ollama服务日志 |

## 11. 版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| v0.0.1 | - | 初始版本，基础聊天功能 |
| v0.0.2 | - | 模型更新，语音合成优化，添加Web前端 |

## 12. 开发团队

- **drayee** - 1473443474@qq.com
- **Mr.Bone** - 13282447533@qq.com

## 13. 许可证

本项目为私有项目，未经授权不得用于商业用途。

---

如有问题，请联系项目维护者。
