using System;
using System.IO;
using System.Diagnostics;
using System.Threading;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// Spring Boot服务管理类，负责Spring Boot服务的启动和管理
    /// </summary>
    public static class SpringBootServiceManager
    {
        /// <summary>
        /// Spring Boot JAR文件目录
        /// </summary>
        private static string JarDirectory => Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "resource", "jar");

        /// <summary>
        /// Spring Boot可执行JAR文件路径
        /// </summary>
        private static string SpringBootJarPath => Path.Combine(JarDirectory, "Common-0.0.1-SNAPSHOT.jar");

        /// <summary>
        /// Spring Boot服务进程
        /// </summary>
        private static Process _springBootProcess;

        /// <summary>
        /// 服务是否正在运行
        /// </summary>
        public static bool IsServiceRunning => _springBootProcess != null && !_springBootProcess.HasExited;

        /// <summary>
        /// 启动Spring Boot服务
        /// </summary>
        /// <returns>服务启动是否成功</returns>
        public static async Task<bool> StartServiceAsync()
        {
            try
            {
                LogManager.OperationStart("Spring Boot服务启动");
                LogManager.Operation("Spring Boot服务启动", "开始启动Spring Boot服务");
                
                // 检查JAR文件是否存在
                if (!File.Exists(SpringBootJarPath))
                {
                    LogManager.Error($"Spring Boot JAR文件不存在: {SpringBootJarPath}");
                    LogManager.Error("可能的原因: JAR文件未包含在资源目录中、路径配置错误");
                    return false;
                }
                
                // 检查JAR文件大小
                FileInfo jarFileInfo = new FileInfo(SpringBootJarPath);
                if (jarFileInfo.Length < 1024 * 1024) // 至少1MB
                {
                    LogManager.Error($"Spring Boot JAR文件大小异常: {jarFileInfo.Length} bytes");
                    LogManager.Error("可能的原因: JAR文件损坏、下载不完整");
                    return false;
                }
                
                LogManager.Info($"找到Spring Boot JAR文件: {SpringBootJarPath}");
                LogManager.Info($"JAR文件大小: {jarFileInfo.Length / (1024 * 1024)} MB");
                
                // 检查JDK环境是否配置
                if (!await ConfigureJavaEnvironmentAsync())
                {
                    LogManager.Error("JDK环境配置失败");
                    LogManager.Error("可能的原因: JDK未解压、java.exe不存在、环境变量设置失败");
                    return false;
                }
                
                // 创建日志目录
                try
                {
                    if (!Directory.Exists(LogManager.GetLogDirectory()))
                    {
                        Directory.CreateDirectory(LogManager.GetLogDirectory());
                        LogManager.Info($"已创建日志目录: {LogManager.GetLogDirectory()}");
                    }
                }
                catch (Exception ex)
                {
                    LogManager.Error($"创建日志目录失败: {ex.Message}");
                    LogManager.Warn("继续启动Spring Boot服务，但日志可能无法正常记录");
                }
                
                // 构建启动命令
                string javaExePath = FindJavaExe();
                if (string.IsNullOrEmpty(javaExePath))
                {
                    LogManager.Error("找不到java.exe");
                    LogManager.Error("可能的原因: JDK未正确安装、环境变量未设置");
                    return false;
                }
                
                // 检查java.exe是否可执行
                if (!File.Exists(javaExePath))
                {
                    LogManager.Error($"java.exe文件不存在: {javaExePath}");
                    return false;
                }
                
                LogManager.Info($"使用java.exe: {javaExePath}");
                
                // 构建启动参数
                string arguments = $"-jar \"{SpringBootJarPath}\"";
                string logFilePath = Path.Combine(LogManager.GetLogDirectory(), "spring.log");
                LogManager.Info($"启动JAR文件: {SpringBootJarPath}");
                LogManager.Info($"日志文件路径: {logFilePath}");
                
                // 检查工作目录是否存在
                if (!Directory.Exists(JarDirectory))
                {
                    LogManager.Error($"工作目录不存在: {JarDirectory}");
                    try
                    {
                        Directory.CreateDirectory(JarDirectory);
                        LogManager.Info($"已创建工作目录: {JarDirectory}");
                    }
                    catch (Exception ex)
                    {
                        LogManager.Error($"创建工作目录失败: {ex.Message}");
                        return false;
                    }
                }
                
                // 直接启动Java进程，不使用cmd.exe
                _springBootProcess = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = javaExePath,
                        Arguments = arguments,
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        UseShellExecute = false,
                        CreateNoWindow = true,
                        WorkingDirectory = JarDirectory
                    }
                };
                
                // 启动Spring Boot服务
                bool startSuccess = false;
                try
                {
                    startSuccess = _springBootProcess.Start();
                }
                catch (Exception ex)
                {
                    LogManager.Error($"启动Spring Boot进程失败: {ex.Message}");
                    LogManager.Error($"错误类型: {ex.GetType().Name}");
                    if (ex is UnauthorizedAccessException)
                    {
                        LogManager.Error("无权限执行java.exe，请以管理员身份运行应用程序");
                    }
                    else if (ex is FileNotFoundException)
                    {
                        LogManager.Error("找不到java.exe文件，请检查JDK安装是否正确");
                    }
                    return false;
                }
                
                // 开始异步读取输出
                if (startSuccess)
                {
                    LogManager.Info("Spring Boot服务进程已启动，开始读取输出");
                    
                    // 异步读取标准输出
                    _springBootProcess.BeginOutputReadLine();
                    _springBootProcess.BeginErrorReadLine();
                    
                    // 注册输出事件处理程序
                    _springBootProcess.OutputDataReceived += (sender, e) =>
                    {
                        if (!string.IsNullOrEmpty(e.Data))
                        {
                            LogManager.Info($"Spring Boot输出: {e.Data}");
                        }
                    };
                    
                    _springBootProcess.ErrorDataReceived += (sender, e) =>
                    {
                        if (!string.IsNullOrEmpty(e.Data))
                        {
                            LogManager.Error($"Spring Boot错误: {e.Data}");
                        }
                    };
                }
                
                if (startSuccess)
                {
                    LogManager.Operation("Spring Boot服务启动", "Spring Boot服务启动命令已执行");
                    
                    // 等待服务启动
                    LogManager.Operation("Spring Boot服务启动", "等待服务启动");
                    bool isServiceReady = await WaitForServiceReadyAsync();
                    
                    if (isServiceReady)
                    {
                        LogManager.OperationComplete("Spring Boot服务启动", "成功启动并已就绪");
                        return true;
                    }
                    else
                    {
                        LogManager.OperationComplete("Spring Boot服务启动", "启动超时，可能未完全就绪");
                        LogManager.Warn("服务可能需要更多时间启动，或启动过程中遇到问题");
                        return true; // 仍然返回成功，因为进程已经启动
                    }
                }
                else
                {
                    LogManager.OperationComplete("Spring Boot服务启动", "启动失败");
                    LogManager.Error("可能的原因: 权限不足、java.exe无法执行、JAR文件损坏");
                    return false;
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpringBootServiceManager.StartService");
                LogManager.Error($"启动Spring Boot服务时发生未预期的错误: {ex.Message}");
                LogManager.Error($"错误类型: {ex.GetType().Name}");
                return false;
            }
        }

        /// <summary>
        /// 启动Spring Boot服务（同步版本）
        /// </summary>
        /// <returns>服务启动是否成功</returns>
        public static bool StartService()
        {
            return StartServiceAsync().Result;
        }

        /// <summary>
        /// 停止Spring Boot服务
        /// </summary>
        /// <returns>服务停止是否成功</returns>
        public static bool StopService()
        {
            try
            {
                LogManager.Info("开始停止Spring Boot服务");
                
                // 停止跟踪的进程
                if (_springBootProcess != null && !_springBootProcess.HasExited)
                {
                    try
                    {
                        // 对于Java进程，直接使用Kill()更可靠
                        LogManager.Info("终止Spring Boot服务进程");
                        _springBootProcess.Kill();
                        
                        // 等待进程退出（最多5秒）
                        bool exited = _springBootProcess.WaitForExit(5000);
                        if (exited)
                        {
                            LogManager.Info("Spring Boot服务进程已终止");
                        }
                        else
                        {
                            LogManager.Warn("Spring Boot服务进程终止超时");
                        }
                    }
                    catch (Exception ex)
                    {
                        LogManager.Warn($"终止进程时出错: {ex.Message}");
                    }
                    finally
                    {
                        _springBootProcess = null;
                    }
                }
                else
                {
                    LogManager.Info("Spring Boot服务进程未运行或已退出");
                }
                
                // 额外检查：查找并终止所有可能的相关Java进程
                // 特别是那些占用8080端口的进程
                KillJavaProcessesOnPort(8080);
                
                // 清理所有相关的Java进程
                KillAllJavaProcesses();
                
                return true;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpringBootServiceManager.StopService");
                return false;
            }
        }

        /// <summary>
        /// 查找并终止占用指定端口的Java进程
        /// </summary>
        /// <param name="port">端口号</param>
        private static void KillJavaProcessesOnPort(int port)
        {
            try
            {
                LogManager.Info($"检查并终止占用{port}端口的Java进程");
                
                // 使用netstat命令查找占用指定端口的进程
                using Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = $"/c netstat -ano | findstr :{port}",
                        RedirectStandardOutput = true,
                        UseShellExecute = false,
                        CreateNoWindow = true
                    }
                };
                
                process.Start();
                string output = process.StandardOutput.ReadToEnd();
                process.WaitForExit();
                
                if (!string.IsNullOrEmpty(output))
                {
                    LogManager.Info($"发现占用{port}端口的进程: {output}");
                    
                    // 解析输出，找到PID
                    string[] lines = output.Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries);
                    foreach (string line in lines)
                    {
                        try
                        {
                            // 示例输出: TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING       12345
                            string[] parts = line.Split(new[] { ' ' }, StringSplitOptions.RemoveEmptyEntries);
                            if (parts.Length >= 5)
                            {
                                string pidStr = parts[4];
                                if (int.TryParse(pidStr, out int pid))
                                {
                                    // 检查该进程是否为Java进程
                                    if (IsJavaProcess(pid))
                                    {
                                        LogManager.Info($"终止占用{port}端口的Java进程 (PID: {pid})");
                                        KillProcess(pid);
                                    }
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                            LogManager.Warn($"解析端口占用信息时出错: {ex.Message}");
                        }
                    }
                }
                else
                {
                    LogManager.Info($"未发现占用{port}端口的进程");
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpringBootServiceManager.KillJavaProcessesOnPort");
            }
        }

        /// <summary>
        /// 终止所有Java进程
        /// </summary>
        private static void KillAllJavaProcesses()
        {
            try
            {
                LogManager.Info("检查并终止所有Java进程");
                
                // 使用tasklist命令查找所有Java进程
                using Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = "/c tasklist | findstr java",
                        RedirectStandardOutput = true,
                        UseShellExecute = false,
                        CreateNoWindow = true
                    }
                };
                
                process.Start();
                string output = process.StandardOutput.ReadToEnd();
                process.WaitForExit();
                
                if (!string.IsNullOrEmpty(output))
                {
                    LogManager.Info($"发现Java进程: {output}");
                    
                    // 解析输出，找到所有Java进程
                    string[] lines = output.Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries);
                    foreach (string line in lines)
                    {
                        try
                        {
                            // 示例输出: javaw.exe                    12345 Console                    1     12,345 K
                            string[] parts = line.Split(new[] { ' ' }, StringSplitOptions.RemoveEmptyEntries);
                            if (parts.Length >= 2 && (parts[0].Equals("java.exe", StringComparison.OrdinalIgnoreCase) || parts[0].Equals("javaw.exe", StringComparison.OrdinalIgnoreCase)))
                            {
                                string pidStr = parts[1];
                                if (int.TryParse(pidStr, out int pid))
                                {
                                    LogManager.Info($"终止Java进程 (PID: {pid}, 名称: {parts[0]})");
                                    KillProcess(pid);
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                            LogManager.Warn($"解析Java进程信息时出错: {ex.Message}");
                        }
                    }
                }
                else
                {
                    LogManager.Info("未发现Java进程");
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpringBootServiceManager.KillAllJavaProcesses");
            }
        }

        /// <summary>
        /// 检查指定PID的进程是否为Java进程
        /// </summary>
        /// <param name="pid">进程ID</param>
        /// <returns>是否为Java进程</returns>
        private static bool IsJavaProcess(int pid)
        {
            try
            {
                using Process process = Process.GetProcessById(pid);
                string processName = process.ProcessName.ToLower();
                return processName.Equals("java") || processName.Equals("javaw");
            }
            catch
            {
                return false;
            }
        }

        /// <summary>
        /// 终止指定PID的进程
        /// </summary>
        /// <param name="pid">进程ID</param>
        private static void KillProcess(int pid)
        {
            try
            {
                using Process process = Process.GetProcessById(pid);
                process.Kill();
                // 等待进程退出（最多2秒）
                process.WaitForExit(2000);
                LogManager.Info($"进程 (PID: {pid}) 已终止");
            }
            catch (Exception ex)
            {
                LogManager.Warn($"终止进程 (PID: {pid}) 时出错: {ex.Message}");
            }
        }

        /// <summary>
        /// 配置Java环境（异步版本）
        /// </summary>
        /// <returns>环境配置是否成功</returns>
        private static async Task<bool> ConfigureJavaEnvironmentAsync()
        {
            try
            {
                LogManager.Info("配置Java环境");
                
                // 尝试使用JdkEnvironmentManager配置的环境
                if (JdkEnvironmentManager.ValidateJdkArchive())
                {
                    LogManager.Info("内置JDK压缩文件验证成功");
                    
                    if (!Directory.Exists(JdkEnvironmentManager.GetJdkPath()))
                    {
                        LogManager.Info("内置JDK未解压，开始解压");
                        if (!await Task.Run(() => JdkEnvironmentManager.ExtractJdk()))
                        {
                            LogManager.Warn("JDK解压失败，尝试使用系统JDK");
                        }
                        else
                        {
                            LogManager.Info("JDK解压成功");
                        }
                    }
                    else
                    {
                        LogManager.Info("内置JDK已解压");
                    }
                    
                    if (await Task.Run(() => JdkEnvironmentManager.ConfigureJdkEnvironment()))
                    {
                        LogManager.Info("使用内置JDK环境");
                        return true;
                    }
                    else
                    {
                        LogManager.Warn("内置JDK环境配置失败，尝试使用系统JDK");
                    }
                }
                else
                {
                    LogManager.Warn("内置JDK验证失败，尝试使用系统JDK");
                }
                
                // 尝试使用系统JDK
                string systemJavaPath = FindJavaExe();
                if (systemJavaPath != null)
                {
                    LogManager.Info("使用系统JDK环境");
                    LogManager.Info($"系统JDK路径: {systemJavaPath}");
                    return true;
                }
                
                LogManager.Error("未找到可用的JDK环境");
                LogManager.Error("可能的原因: 内置JDK压缩文件不存在或损坏、系统未安装JDK");
                LogManager.Error("解决方案: 确保应用程序资源目录中包含完整的JDK压缩文件，或在系统中安装JDK 25或更高版本");
                return false;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpringBootServiceManager.ConfigureJavaEnvironment");
                LogManager.Error($"配置Java环境时发生未预期的错误: {ex.Message}");
                LogManager.Error($"错误类型: {ex.GetType().Name}");
                return false;
            }
        }

        /// <summary>
        /// 配置Java环境（同步版本）
        /// </summary>
        /// <returns>环境配置是否成功</returns>
        private static bool ConfigureJavaEnvironment()
        {
            return ConfigureJavaEnvironmentAsync().Result;
        }

        /// <summary>
        /// 查找java.exe路径
        /// </summary>
        /// <returns>java.exe路径</returns>
        private static string FindJavaExe()
        {
            // 首先尝试从JdkEnvironmentManager获取
            string jdkPath = JdkEnvironmentManager.GetJdkPath();
            string[] possiblePaths = {
                Path.Combine(jdkPath, "bin", "java.exe"),
                Path.Combine(jdkPath, "jdk-25", "bin", "java.exe"),
                Path.Combine(jdkPath, "jdk", "bin", "java.exe")
            };
            
            foreach (string path in possiblePaths)
            {
                if (File.Exists(path))
                {
                    return path;
                }
            }
            
            // 尝试从系统环境变量查找
            try
            {
                Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = "/c where java",
                        RedirectStandardOutput = true,
                        UseShellExecute = false,
                        CreateNoWindow = true
                    }
                };
                
                process.Start();
                string output = process.StandardOutput.ReadToEnd();
                process.WaitForExit();
                
                if (process.ExitCode == 0 && !string.IsNullOrEmpty(output))
                {
                    string[] paths = output.Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries);
                    if (paths.Length > 0 && File.Exists(paths[0]))
                    {
                        return paths[0];
                    }
                }
            }
            catch { }
            
            return null;
        }

        /// <summary>
        /// 等待服务就绪（异步版本）
        /// </summary>
        /// <returns>服务是否就绪</returns>
        private static async Task<bool> WaitForServiceReadyAsync()
        {
            LogManager.OperationStart("Spring Boot服务就绪检查");
            LogManager.Operation("Spring Boot服务就绪检查", "等待Spring Boot服务就绪");
            
            int elapsedMs = 0;
            int checkIntervalMs = 1000;
            
            while (true)
            {
                if (IsServiceRunning)
                {
                    // 尝试检查服务是否响应
                    try
                    {
                        if (await CheckServiceResponseAsync())
                        {
                            LogManager.OperationComplete("Spring Boot服务就绪检查", "服务已就绪");
                            return true;
                        }
                    }
                    catch (Exception ex)
                    {
                        LogManager.Error($"检查服务响应时发生异常: {ex.Message}");
                    }
                }
                else
                {
                    LogManager.OperationComplete("Spring Boot服务就绪检查", "服务进程已退出");
                    return false;
                }
                
                await Task.Delay(checkIntervalMs);
                elapsedMs += checkIntervalMs;
                
                if (elapsedMs % 5000 == 0)
                {
                    LogManager.Operation("Spring Boot服务就绪检查", $"等待中... ({elapsedMs}毫秒)");
                }
            }
        }

        /// <summary>
        /// 等待服务就绪（同步版本）
        /// </summary>
        /// <returns>服务是否就绪</returns>
        private static bool WaitForServiceReady()
        {
            return WaitForServiceReadyAsync().Result;
        }

        /// <summary>
        /// 检查服务是否响应（异步版本）
        /// </summary>
        /// <returns>服务是否响应</returns>
        private static async Task<bool> CheckServiceResponseAsync()
        {
            try
            {
                // 尝试访问服务的健康检查端点
                using (System.Net.Http.HttpClient client = new System.Net.Http.HttpClient())
                {
                    client.Timeout = TimeSpan.FromMinutes(8);
                    
                    // 尝试访问常见的健康检查端点
                    string[] endpoints = {
                        "http://localhost:8080/api/v3/test/hello",
                    };
                    
                    foreach (string endpoint in endpoints)
                    {
                        try
                        {
                            System.Net.Http.HttpResponseMessage response = await client.GetAsync(endpoint);
                            if (response.IsSuccessStatusCode)
                            {
                                LogManager.Info($"服务健康检查端点响应成功: {endpoint}");
                                return true;
                            }
                        }
                        catch
                        {
                            // 忽略单个端点的错误，尝试下一个
                        }
                    }
                }
                
                // 尝试访问API根路径
                try
                {
                    using (System.Net.Http.HttpClient client = new System.Net.Http.HttpClient())
                    {
                        client.Timeout = TimeSpan.FromMinutes(8);
                        System.Net.Http.HttpResponseMessage response = await client.GetAsync("http://localhost:8080");
                        if (response.StatusCode != System.Net.HttpStatusCode.RequestTimeout)
                        {
                            LogManager.Info("服务根路径响应成功");
                            return true;
                        }
                    }
                }
                catch
                {
                    // 忽略错误
                }
                
                return false;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpringBootServiceManager.CheckServiceResponse");
                return false;
            }
        }

        /// <summary>
        /// 检查服务是否响应（同步版本）
        /// </summary>
        /// <returns>服务是否响应</returns>
        private static bool CheckServiceResponse()
        {
            return CheckServiceResponseAsync().Result;
        }

        /// <summary>
        /// 停止Spring Boot服务
        /// </summary>
        public static void StopServiceIfRunning()
        {
            if (IsServiceRunning)
            {
                LogManager.Info("停止Spring Boot服务");
                StopService();
            }
        }

        /// <summary>
        /// 获取服务状态
        /// </summary>
        /// <returns>服务状态描述</returns>
        public static string GetServiceStatus()
        {
            if (IsServiceRunning)
            {
                return "Spring Boot服务正在运行";
            }
            else
            {
                return "Spring Boot服务未运行";
            }
        }
    }
}
