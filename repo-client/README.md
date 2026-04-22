# 八千代模型客户端

## 项目简介

八千代模型客户端是一个基于WPF技术栈开发的LLM聊天模型客户端，采用MVVM设计模式，具有现代化的界面设计和流畅的用户交互体验。

## 技术特性

- **无边框透明窗体**：完全摒弃Windows默认窗体样式，实现透明背景和圆角边框
- **现代化UI设计**：所有控件均应用圆角优化处理，提升视觉美感
- **流畅的动画效果**：按钮鼠标停留效果、缩放动画等，增强用户体验
- **MVVM架构**：采用Model-View-ViewModel设计模式，代码结构清晰可维护
- **性能优化**：透明区域渲染性能良好，确保动画流畅不卡顿
- **响应式布局**：保证在不同屏幕分辨率和缩放比例下的界面一致性

## 功能模块

### 1. 窗口控制
- 最小化窗口
- 关闭窗口
- 设置（预留接口）

### 2. 对话管理
- 添加新对话
- 切换对话
- 对话历史记录

### 3. 消息交互
- 发送消息
- 接收AI回复（模拟）
- 消息时间戳显示

## 资源文件

项目使用以下图片资源，位于`resource`目录：

- `add_talk.png`：添加新对话按钮图标
- `big_windwos_icon.jpg`：大型软件图标
- `close.png`：关闭按钮图标
- `minimize.png`：最小化按钮图标
- `set.png`：设置按钮图标
- `window_title_icon.jpg`：窗口标题栏图标

## 项目结构

```
EasyYachiyoClient/
├── Model/
│   ├── Conversation.cs       # 对话实体类
│   └── Message.cs            # 消息实体类
├── ViewModel/
│   ├── ViewModelBase.cs      # 视图模型基类
│   ├── MainViewModel.cs      # 主窗口视图模型
│   └── RelayCommand.cs       # 命令实现类
├── BoolToBackgroundConverter.cs      # 布尔值到背景色转换器
├── BoolToBorderBrushConverter.cs     # 布尔值到边框颜色转换器
├── BoolToHorizontalAlignmentConverter.cs  # 布尔值到水平对齐方式转换器
├── MainWindow.xaml           # 主窗口布局文件
├── MainWindow.xaml.cs        # 主窗口后台逻辑
├── App.xaml                  # 应用程序配置
├── App.xaml.cs               # 应用程序入口
└── resource/                 # 图片资源目录
```

## 开发环境

- .NET 8.0
- Visual Studio 2022+
- Windows 10+

## 运行项目

1. 克隆或下载项目到本地
2. 使用Visual Studio打开`EasyYachiyoClient.sln`解决方案
3. 构建并运行项目

## 后续开发计划

1. **后端集成**：实现与真实LLM模型的对接
2. **设置功能**：完善设置窗口，支持模型配置、API密钥管理等
3. **主题切换**：添加深色模式和自定义主题支持
4. **消息功能增强**：支持图片、文件发送，消息编辑、删除等
5. **多语言支持**：添加国际化语言包

## 注意事项

- 本项目目前为前端演示版本，AI回复功能为模拟实现
- 实际使用时需要集成真实的LLM模型后端
- 项目使用的图片资源仅供演示使用，实际部署时请替换为正式资源

## 许可证

本项目采用MIT许可证，详见LICENSE文件。
