using System.Configuration;
using System.Data;
using System.IO;
using System.Windows;
using EasyYachiyoClient.Utils;
using CefSharp;
using CefSharp.Wpf;

namespace EasyYachiyoClient
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        /// <summary>
        /// 应用启动事件
        /// </summary>
        /// <param name="e">启动事件参数</param>
        protected override async void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            
            // 初始化CefSharp
            try
            {
                LogManager.Info("开始初始化 CefSharp");
                var settings = new CefSettings();
                settings.CefCommandLineArgs.Add("enable-javascript", "1");
                settings.CefCommandLineArgs.Add("enable-plugins", "1");
                Cef.Initialize(settings);
                LogManager.Info("CefSharp 初始化完成");
            }
            catch (Exception ex)
            {
                LogManager.Error($"CefSharp 初始化失败: {ex.Message}");
                LogManager.WriteException(ex, "App.OnStartup - CefSharp初始化");
            }
            
            // 初始化应用状态管理
            ApplicationStateManager.Initialize();
            
            // 显示启动进度条
            SplashWindow splashWindow = new SplashWindow();
            splashWindow.Show();
            
            // 执行启动配置检测
            await RunStartupConfigurationCheck(splashWindow);
        }
        
        /// <summary>
        /// 执行启动配置检测
        /// </summary>
        private async Task RunStartupConfigurationCheck(SplashWindow splashWindow)
        {
            try
            {
                Console.WriteLine($"[{DateTime.Now}] 开始执行启动配置检测...");
                
                // 初始化日志管理器
                try
                {
                    LogManager.Initialize();
                    LogManager.Info("开始执行启动配置检测");
                }
                catch (Exception ex)
                {
                    LogManager.WriteException(ex, "App.RunStartupConfigurationCheck - 初始化日志管理器");
                    Console.WriteLine($"[{DateTime.Now}] 初始化日志管理器失败: {ex.Message}");
                }
                
                // 更新进度
                splashWindow.UpdateStatus("初始化日志系统");
                splashWindow.UpdateProgress(10);
                
                // 检查配置文件是否存在
                if (!SettingsManager.SettingsFileExists())
                {
                    LogManager.Info("配置文件不存在，创建默认配置");
                    splashWindow.UpdateStatus("创建默认配置");
                    splashWindow.UpdateProgress(20);
                    
                    // 创建并保存默认配置
                    UserSettings defaultSettings = new UserSettings();
                    bool saveSuccess = SettingsManager.SaveSettings(defaultSettings);
                    
                    if (saveSuccess)
                    {
                        LogManager.Info("默认配置创建成功");
                    }
                    else
                    {
                        LogManager.Error("默认配置创建失败");
                        LogManager.Warn("继续启动，但配置可能无法正常保存");
                    }
                }
                else
                {
                    LogManager.Info("配置文件已存在");
                    splashWindow.UpdateStatus("加载配置文件");
                    splashWindow.UpdateProgress(20);
                }
                
                // 执行环境检查流程
                LogManager.Info("开始执行环境检查流程");
                splashWindow.UpdateStatus("检查系统环境");
                splashWindow.UpdateProgress(30);
                
                // 加载设置，根据模型运行方式更新状态
                UserSettings settings = SettingsManager.LoadSettings();
                if (settings.ModelRunMode == "远程")
                {
                    splashWindow.UpdateStatus("测试连接");
                }
                else
                {
                    splashWindow.UpdateStatus("检查系统环境");
                }
                
                ExecuteEnvironmentCheck();
                
                // 只有当模型运行方式为本地时，才执行JDK和Spring Boot服务相关的初始化
                if (settings.ModelRunMode == "本地")
                {
                    // 验证JDK压缩文件
                    LogManager.Info("验证JDK压缩文件");
                    splashWindow.UpdateStatus("验证JDK压缩文件");
                    splashWindow.UpdateProgress(40);
                    bool jdkArchiveValid = JdkEnvironmentManager.ValidateJdkArchive();
                    if (!jdkArchiveValid)
                    {
                        LogManager.Error("JDK压缩文件验证失败");
                        splashWindow.UpdateStatus("JDK压缩文件验证失败");
                        System.Threading.Thread.Sleep(2000);
                        splashWindow.CloseWindow();
                        return;
                    }
                    
                    // 解压JDK
                    LogManager.OperationStart("JDK解压");
                    LogManager.Operation("JDK解压", "开始解压JDK");
                    splashWindow.UpdateStatus("解压JDK");
                    splashWindow.UpdateProgress(50);
                    
                    // 使用进度报告委托
                    JdkEnvironmentManager.ProgressReportDelegate progressReport = (progress, message) =>
                    {
                        splashWindow.UpdateSecondaryProgress(progress);
                        splashWindow.UpdateTaskDescription(message);
                    };
                    
                    // 在后台线程中执行解压，避免UI阻塞
                    var extractTask = System.Threading.Tasks.Task.Run(() =>
                    {
                        return JdkEnvironmentManager.ExtractJdk(progressReport);
                    });
                    
                    bool jdkExtractSuccess = await extractTask;
                    
                    if (!jdkExtractSuccess)
                    {
                        LogManager.OperationComplete("JDK解压", "失败");
                        LogManager.Error("JDK解压失败");
                        splashWindow.UpdateStatus("JDK解压失败");
                        System.Threading.Thread.Sleep(2000);
                        splashWindow.CloseWindow();
                        return;
                    }
                    
                    LogManager.OperationComplete("JDK解压", "成功");
                    
                    // 重置副进度条和任务描述
                    splashWindow.UpdateSecondaryProgress(0);
                    splashWindow.UpdateTaskDescription("准备中...");
                    
                    // 配置JDK环境
                    LogManager.Info("配置JDK环境");
                    splashWindow.UpdateStatus("配置JDK环境");
                    splashWindow.UpdateProgress(70);
                    bool jdkConfigSuccess = await Task.Run(() => JdkEnvironmentManager.ConfigureJdkEnvironment());
                    if (!jdkConfigSuccess)
                    {
                        LogManager.Error("JDK环境配置失败");
                        splashWindow.UpdateStatus("JDK环境配置失败");
                        System.Threading.Thread.Sleep(2000);
                        splashWindow.CloseWindow();
                        return;
                    }
                    
                    // 启动Spring Boot服务
                    LogManager.Info("开始启动Spring Boot服务");
                    splashWindow.UpdateStatus("启动Spring Boot服务");
                    splashWindow.UpdateProgress(80);
                    bool serviceStartSuccess = await SpringBootServiceManager.StartServiceAsync();
                    if (serviceStartSuccess)
                    {
                        LogManager.Info("Spring Boot服务启动成功");
                        splashWindow.UpdateStatus("Spring Boot服务启动成功");
                        splashWindow.UpdateProgress(90);
                    }
                    else
                    {
                        LogManager.Error("Spring Boot服务启动失败");
                        splashWindow.UpdateStatus("Spring Boot服务启动失败");
                        System.Threading.Thread.Sleep(2000);
                        splashWindow.CloseWindow();
                        return;
                    }
                }
                else
                {
                    // 远程模式下，直接跳过JDK和Spring Boot服务初始化，更新进度条
                    splashWindow.UpdateProgress(90);
                    splashWindow.UpdateStatus("连接测试完成");
                }
                
                // 完成启动
                LogManager.Info("启动配置检测完成");
                splashWindow.UpdateStatus("启动完成");
                splashWindow.UpdateProgress(100);
                System.Threading.Thread.Sleep(500);
                
                // 显示主窗口
                try
                {
                    MainWindow mainWindow = new MainWindow();
                    Application.Current.MainWindow = mainWindow;
                    mainWindow.Show();
                    LogManager.Info("主窗口显示成功");
                }
                catch (Exception ex)
                {
                    LogManager.WriteException(ex, "App.RunStartupConfigurationCheck - 显示主窗口");
                    LogManager.Error("显示主窗口失败");
                    splashWindow.UpdateStatus("显示主窗口失败");
                    System.Threading.Thread.Sleep(2000);
                    splashWindow.CloseWindow();
                    return;
                }
                
                // 关闭启动窗口
                splashWindow.CloseWindow();
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "App.RunStartupConfigurationCheck");
                LogManager.Error($"启动过程中发生未预期的错误: {ex.Message}");
                LogManager.Error($"错误类型: {ex.GetType().Name}");
                ApplicationStateManager.LogException(ex, "App.RunStartupConfigurationCheck");
                
                // 显示错误信息
                splashWindow.UpdateStatus($"启动失败: {ex.Message}");
                System.Threading.Thread.Sleep(3000);
                splashWindow.CloseWindow();
            }
        }
        
        /// <summary>
        /// 执行环境检查流程
        /// </summary>
        private void ExecuteEnvironmentCheck()
        {
            try
            {
                // 加载配置
                UserSettings settings = SettingsManager.LoadSettings();
                bool isProductionMode = settings.ProductionMode;
                
                // 非生产模式下才输出控制台日志
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 加载配置成功");
                }
                
                // 验证配置参数的完整性和有效性
                ValidateConfiguration(settings);
                
                // 检查系统环境是否满足应用运行要求
                CheckSystemEnvironment();
                
                // 确认必要的依赖项和资源是否可用
                CheckDependencies();
                
                // 非生产模式下才输出控制台日志
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 环境检查流程完成");
                }
            }
            catch (Exception ex)
            {
                // 非生产模式下才输出控制台日志
                UserSettings settings = SettingsManager.LoadSettings();
                if (!settings.ProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 环境检查异常: {ex.Message}");
                }
                ApplicationStateManager.LogException(ex, "App.ExecuteEnvironmentCheck");
            }
        }
        
        /// <summary>
        /// 验证配置参数的完整性和有效性
        /// </summary>
        /// <param name="settings">用户设置对象</param>
        private void ValidateConfiguration(UserSettings settings)
        {
            try
            {
                bool isProductionMode = settings.ProductionMode;
                
                // 非生产模式下才输出控制台日志
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 验证配置参数...");
                }
                
                // 检查必要的配置参数
                if (string.IsNullOrEmpty(settings.ModelRunMode))
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 警告: 模型运行方式未设置，使用默认值");
                    }
                    settings.ModelRunMode = "本地";
                }
                
                if (string.IsNullOrEmpty(settings.ModelUrl))
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 警告: 模型URL未设置，使用默认值");
                    }
                    settings.ModelUrl = "默认";
                }
                
                if (string.IsNullOrEmpty(settings.Language))
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 警告: 语言设置未设置，使用默认值");
                    }
                    settings.Language = "中文";
                }
                
                // 非生产模式下才输出控制台日志
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 配置参数验证完成");
                }
            }
            catch (Exception ex)
            {
                // 非生产模式下才输出控制台日志
                UserSettings exceptionSettings = SettingsManager.LoadSettings();
                if (!exceptionSettings.ProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 配置验证异常: {ex.Message}");
                }
                ApplicationStateManager.LogException(ex, "App.ValidateConfiguration");
            }
        }
        
        /// <summary>
        /// 检查系统环境是否满足应用运行要求
        /// </summary>
        private void CheckSystemEnvironment()
        {
            try
            {
                UserSettings settings = SettingsManager.LoadSettings();
                bool isProductionMode = settings.ProductionMode;
                
                // 非生产模式下才输出控制台日志
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 检查系统环境...");
                    
                    // 检查操作系统版本
                    Console.WriteLine("[{DateTime.Now}] 操作系统: {Environment.OSVersion.VersionString}");
                    
                    // 检查.NET版本
                    Console.WriteLine("[{DateTime.Now}] .NET版本: {Environment.Version}");
                    
                    // 检查可用内存
                    System.Diagnostics.Process process = System.Diagnostics.Process.GetCurrentProcess();
                    long memoryUsage = process.PrivateMemorySize64 / (1024 * 1024);
                    Console.WriteLine("[{DateTime.Now}] 当前内存使用: {memoryUsage} MB");
                    
                    // 检查磁盘空间
                    DriveInfo systemDrive = DriveInfo.GetDrives().First(d => d.Name == Path.GetPathRoot(AppDomain.CurrentDomain.BaseDirectory));
                    long freeSpace = systemDrive.AvailableFreeSpace / (1024 * 1024 * 1024);
                    Console.WriteLine("[{DateTime.Now}] 可用磁盘空间: {freeSpace} GB");
                    
                    Console.WriteLine("[{DateTime.Now}] 系统环境检查完成");
                }
            }
            catch (Exception ex)
            {
                // 非生产模式下才输出控制台日志
                UserSettings settings = SettingsManager.LoadSettings();
                if (!settings.ProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 系统环境检查异常: {ex.Message}");
                }
                ApplicationStateManager.LogException(ex, "App.CheckSystemEnvironment");
            }
        }
        
        /// <summary>
        /// 确认必要的依赖项和资源是否可用
        /// </summary>
        private void CheckDependencies()
        {
            try
            {
                UserSettings settings = SettingsManager.LoadSettings();
                bool isProductionMode = settings.ProductionMode;
                
                // 非生产模式下才输出控制台日志
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 检查必要的依赖项和资源...");
                }
                
                // 根据模型运行方式执行差异化的环境检测
                if (settings.ModelRunMode == "本地")
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 检查Ollama环境...");
                    }
                    
                    // 执行完整的Ollama环境检测和修复流程
                    System.Threading.Tasks.Task.Run(async () =>
                    {
                        try
                        {
                            // 使用空的进度更新回调，因为在初始化阶段不需要显示UI
                            bool isReady = await OllamaEnvironmentManager.CheckAndPrepareEnvironmentAsync((progress, status) =>
                            {
                                if (!isProductionMode)
                                {
                                    Console.WriteLine($"[{DateTime.Now}] Ollama环境检测: {status}");
                                }
                            });
                            
                            if (!isProductionMode)
                            {
                                Console.WriteLine($"[{DateTime.Now}] Ollama环境检测结果: {isReady}");
                            }
                        }
                        catch (Exception ex)
                        {
                            if (!isProductionMode)
                            {
                                Console.WriteLine($"[{DateTime.Now}] Ollama环境检测异常: {ex.Message}");
                            }
                            ApplicationStateManager.LogException(ex, "App.CheckDependencies - Ollama环境检测");
                        }
                    });
                }
                else if (settings.ModelRunMode == "远程")
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 测试远程连接...");
                    }
                    
                    // 执行远程连接测试
                    System.Threading.Tasks.Task.Run(async () =>
                    {
                        try
                        {
                            // 使用空的进度更新回调，因为在初始化阶段不需要显示UI
                            bool isConnected = await RemoteConnectionManager.TestRemoteConnectionAsync((progress, status) =>
                            {
                                if (!isProductionMode)
                                {
                                    Console.WriteLine($"[{DateTime.Now}] 远程连接测试: {status}");
                                }
                            });
                            
                            if (!isProductionMode)
                            {
                                Console.WriteLine($"[{DateTime.Now}] 远程连接测试结果: {isConnected}");
                            }
                        }
                        catch (Exception ex)
                        {
                            if (!isProductionMode)
                            {
                                Console.WriteLine($"[{DateTime.Now}] 远程连接测试异常: {ex.Message}");
                            }
                            ApplicationStateManager.LogException(ex, "App.CheckDependencies - 远程连接测试");
                        }
                    });
                }
                
                // 检查必要的资源文件
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 检查必要的资源文件...");
                }
                CheckRequiredResources();
                
                // 非生产模式下才输出控制台日志
                if (!isProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 依赖项和资源检查完成");
                }
            }
            catch (Exception ex)
            {
                // 非生产模式下才输出控制台日志
                UserSettings settings = SettingsManager.LoadSettings();
                if (!settings.ProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 依赖项检查异常: {ex.Message}");
                }
                ApplicationStateManager.LogException(ex, "App.CheckDependencies");
            }
        }
        
        /// <summary>
        /// 检查必要的资源文件
        /// </summary>
        private void CheckRequiredResources()
        {
            try
            {
                UserSettings settings = SettingsManager.LoadSettings();
                bool isProductionMode = settings.ProductionMode;
                
                // 检查图标文件
                string iconPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "resource", "window_title_icon.jpg");
                if (File.Exists(iconPath))
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 窗口图标文件存在");
                    }
                }
                else
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 警告: 窗口图标文件不存在");
                    }
                }
                
                // 检查其他必要的资源文件
                string bigIconPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "resource", "big_window_icon.jpg");
                if (File.Exists(bigIconPath))
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 大图标文件存在");
                    }
                }
                else
                {
                    if (!isProductionMode)
                    {
                        Console.WriteLine("[{DateTime.Now}] 警告: 大图标文件不存在");
                    }
                }
            }
            catch (Exception ex)
            {
                // 非生产模式下才输出控制台日志
                UserSettings settings = SettingsManager.LoadSettings();
                if (!settings.ProductionMode)
                {
                    Console.WriteLine("[{DateTime.Now}] 资源文件检查异常: {ex.Message}");
                }
                ApplicationStateManager.LogException(ex, "App.CheckRequiredResources");
            }
        }

        /// <summary>
        /// 应用退出事件
        /// </summary>
        /// <param name="e">退出事件参数</param>
        protected override void OnExit(ExitEventArgs e)
        {
            try
            {
                // 停止Spring Boot服务
                LogManager.Info("应用退出，停止Spring Boot服务");
                SpringBootServiceManager.StopServiceIfRunning();
                
                // 清理CefSharp
                LogManager.Info("应用退出，清理 CefSharp");
                Cef.Shutdown();
                LogManager.Info("CefSharp 清理完成");
                
                // 清理资源
                ApplicationStateManager.Cleanup();
                JdkEnvironmentManager.Cleanup();
                LogManager.Info("应用退出，资源清理完成");
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "App.OnExit");
            }
            finally
            {
                base.OnExit(e);
            }
        }
    }
}
