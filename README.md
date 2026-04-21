# Tsukimi-Yachiyo 组织项目

## 项目结构

Tsukimi-Yachiyo 组织是一个专注于开发各类应用和服务的开源社区，目前包含以下项目：

| 项目名称 | 仓库链接 | 项目类型 | 主要功能 |
|---------|---------|---------|--------|
| StorageRoom | [https://github.com/Tsukimi-Yachiyo/StorageRoom](https://github.com/Tsukimi-Yachiyo/StorageRoom) | 存储服务 | 提供文件存储和管理功能 |
| YachiyoService | [https://github.com/Tsukimi-Yachiyo/YachiyoService](https://github.com/Tsukimi-Yachiyo/YachiyoService) | 微服务项目 | 基于 Spring Cloud Alibaba 的内容社区平台 |
| YachiyoAI | [https://github.com/Tsukimi-Yachiyo/YachiyoAI](https://github.com/Tsukimi-Yachiyo/YachiyoAI) | AI 框架 | 模仿 Spring 框架风格的 Python 轻量级开发框架 |
| YachiyoClient | [https://github.com/Tsukimi-Yachiyo/YachiyoClient](https://github.com/Tsukimi-Yachiyo/YachiyoClient) | 客户端应用 | 八千代客户端，提供窗口控制、对话管理和消息交互功能 |

## 快速开始

### 环境要求

- JDK 1.8+（YachiyoService）
- Python 3.7+（YachiyoAI）
- .NET Framework 4.7.2+（YachiyoClient）
- MySQL/PostgreSQL/SQLite（根据项目需求）
- Redis（缓存服务）
- Minio（对象存储，YachiyoService）
- Nacos（服务注册与发现，YachiyoService）

### 安装步骤

1. **克隆仓库**
   ```bash
   git clone https://github.com/Tsukimi-Yachiyo/All.git
   cd All
   ```

2. **构建项目**
   - YachiyoService：参考该项目的 README 文档
   - YachiyoAI：`pip install -r requirements.txt`
   - YachiyoClient：使用 Visual Studio 打开并构建

3. **启动服务**
   - YachiyoService：启动各个微服务
   - YachiyoAI：`python main.py`
   - YachiyoClient：运行生成的可执行文件

## 技术栈

| 项目 | 技术栈 |
|-----|-------|
| YachiyoService | Spring Cloud Alibaba, Nacos, Redis, Minio, PostgreSQL |
| YachiyoAI | Python, Peewee ORM, Flask（Web 插件） |
| YachiyoClient | C#, WPF |

## 开发规范

### 代码规范

1. **命名规范**
   - 类名：使用 PascalCase
   - 方法名：使用 camelCase
   - 变量名：使用 camelCase
   - 常量名：使用 UPPER_SNAKE_CASE

2. **代码风格**
   - Java：遵循 Google Java 风格指南
   - Python：遵循 PEP 8 规范
   - C#：遵循 .NET 设计规范

3. **提交规范**
   - 提交信息使用中文或英文，清晰描述修改内容
   - 提交前确保代码通过测试
   - 大的功能修改应拆分为多个小的提交

### 分支管理

1. **主分支**：`main` - 稳定版本
2. **开发分支**：`develop` - 开发中的版本
3. **功能分支**：`feature/xxx` - 新功能开发
4. **修复分支**：`fix/xxx` -  bug 修复

### 代码审查

1. 所有代码提交前需经过代码审查
2. 代码审查重点关注：
   - 代码质量和可读性
   - 功能正确性
   - 安全性
   - 性能优化

###  issue 管理

1. 使用 GitHub Issues 跟踪 bug 和功能请求
2. 每个 issue 应包含：
   - 清晰的标题
   - 详细的描述
   - 重现步骤（如果是 bug）
   - 预期行为

## 项目贡献者

| 角色 | 成员 | 职责 |
|-----|-----|------|
| 站长，群管理 | 初雪 | 项目整体规划和管理 |
| 后端 | drayee, 酒精, ko no su ba | 后端服务开发 |
| 前端 | 雾雨, Yuki, jingtll | 前端界面开发 |
| AI Agent, QQ Robot | 凉, aiagent, 酒精 | AI 功能和机器人开发 |
| 真群管理 | 认识人生（网名）果粒橙版, 酒精 | 社区管理 |
| 技术支持 | 不擅长捉弄的同学 | 技术问题解答和支持 |
| 美术设计 | 日月123 | 网站精美美术设计 |
| 安全 | lo2ele1（安全漏洞及时反馈喵） | 安全漏洞检测和修复 |
| 资金支持 | 老猫 | 提供大量资金支持，提供前后端服务器 |
| 活动策划 | UNIX.Box | 社区活动策划 |
| 设计支持 | BangBooM@小说家下场从0做美术（忙） | 提供部分设计 |
| 美术支持 | w502887221 | 提供美术支持，包括图标设计 |

## 联系方法

- 网站：[yachiyocat.top](https://yachiyocat.top)
- QQ 群：1094218305

## 致谢与欢迎

感谢所有贡献者的辛勤付出，使得项目能够不断发展壮大。我们欢迎更多志同道合的开发者加入我们的团队，一起构建更好的项目。

无论你是经验丰富的开发者，还是刚刚入门的新手，只要你对项目感兴趣，我们都非常欢迎你的加入。让我们一起创造更多精彩的作品！

---

© 2026 Tsukimi-Yachiyo. All rights reserved.