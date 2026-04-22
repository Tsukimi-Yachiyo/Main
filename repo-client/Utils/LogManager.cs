using System;
using System.IO;
using System.Reflection;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// 日志管理类，负责服务启动过程的详细日志记录
    /// </summary>
    public static class LogManager
    {
        /// <summary>
        /// 日志目录
        /// </summary>
        private static string LogDirectory => Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "logs");

        /// <summary>
        /// 日志文件路径
        /// </summary>
        private static string _logFilePath;
        
        /// <summary>
        /// 日志文件路径
        /// </summary>
        private static string LogFilePath
        {
            get
            {
                if (string.IsNullOrEmpty(_logFilePath))
                {
                    _logFilePath = Path.Combine(LogDirectory, $"{DateTime.Now:yyyyMMdd_HHmmss}.log");
                }
                return _logFilePath;
            }
        }

        /// <summary>
        /// 初始化日志管理器
        /// </summary>
        public static void Initialize()
        {
            try
            {
                Console.WriteLine($"[{DateTime.Now}] 初始化日志管理器");
                
                // 创建日志目录
                if (!Directory.Exists(LogDirectory))
                {
                    try
                    {
                        Directory.CreateDirectory(LogDirectory);
                        Console.WriteLine($"[{DateTime.Now}] 已创建日志目录: {LogDirectory}");
                    }
                    catch (Exception dirEx)
                    {
                        Console.WriteLine($"[{DateTime.Now}] 创建日志目录失败: {dirEx.Message}");
                        Console.WriteLine($"[{DateTime.Now}] 日志系统将使用默认位置");
                        // 继续执行，不中断应用启动
                    }
                }
                
                // 重置日志文件路径，确保每次启动都创建新文件
                _logFilePath = null;
                
                // 创建日志文件（如果不存在）
                if (!File.Exists(LogFilePath))
                {
                    try
                    {
                        using (File.Create(LogFilePath)) { }
                        Console.WriteLine($"[{DateTime.Now}] 已创建日志文件: {LogFilePath}");
                    }
                    catch (Exception fileEx)
                    {
                        Console.WriteLine($"[{DateTime.Now}] 创建日志文件失败: {fileEx.Message}");
                        Console.WriteLine($"[{DateTime.Now}] 日志系统将只输出到控制台");
                        // 继续执行，不中断应用启动
                    }
                }
                
                // 写入启动日志
                WriteLog("INFO", "日志管理器初始化完成");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[{DateTime.Now}] 日志管理器初始化异常: {ex.Message}");
                ApplicationStateManager.LogException(ex, "LogManager.Initialize");
                // 继续执行，不中断应用启动
            }
        }

        /// <summary>
        /// 写入日志
        /// </summary>
        /// <param name="level">日志级别</param>
        /// <param name="message">日志消息</param>
        public static void WriteLog(string level, string message)
        {
            try
            {
                string logEntry = $"[{DateTime.Now:yyyy-MM-dd HH:mm:ss}] [{level}] {message}";
                
                // 输出到控制台
                Console.WriteLine(logEntry);
                
                // 写入日志文件
                try
                {
                    if (!string.IsNullOrEmpty(LogFilePath))
                    {
                        // 确保目录存在
                        string logDir = Path.GetDirectoryName(LogFilePath);
                        if (!Directory.Exists(logDir))
                        {
                            try
                            {
                                Directory.CreateDirectory(logDir);
                                Console.WriteLine($"[{DateTime.Now}] 已创建日志目录: {logDir}");
                            }
                            catch (Exception dirEx)
                            {
                                Console.WriteLine($"[{DateTime.Now}] 创建日志目录失败: {dirEx.Message}");
                                return;
                            }
                        }
                        
                        // 写入日志文件
                        using (StreamWriter writer = new StreamWriter(LogFilePath, true))
                        {
                            writer.WriteLine(logEntry);
                        }
                        Console.WriteLine($"[{DateTime.Now}] 已写入日志文件: {LogFilePath}");
                    }
                }
                catch (Exception writeEx)
                {
                    // 输出日志异常
                    Console.WriteLine($"[{DateTime.Now}] 写入日志文件失败: {writeEx.Message}");
                    // 继续执行，不中断应用运行
                }
            }
            catch (Exception ex)
            {
                // 输出日志异常
                Console.WriteLine($"[{DateTime.Now}] 写入日志异常: {ex.Message}");
                // 继续执行，不中断应用运行
            }
        }

        /// <summary>
        /// 写入信息级别日志
        /// </summary>
        /// <param name="message">日志消息</param>
        public static void Info(string message)
        {
            WriteLog("INFO", message);
        }

        /// <summary>
        /// 写入警告级别日志
        /// </summary>
        /// <param name="message">日志消息</param>
        public static void Warn(string message)
        {
            WriteLog("WARN", message);
        }

        /// <summary>
        /// 写入错误级别日志
        /// </summary>
        /// <param name="message">日志消息</param>
        public static void Error(string message)
        {
            WriteLog("ERROR", message);
        }

        /// <summary>
        /// 写入调试级别日志
        /// </summary>
        /// <param name="message">日志消息</param>
        public static void Debug(string message)
        {
            WriteLog("DEBUG", message);
        }

        /// <summary>
        /// 写入异常日志
        /// </summary>
        /// <param name="ex">异常对象</param>
        /// <param name="source">异常来源</param>
        public static void WriteException(Exception ex, string source)
        {
            string message = $"{source} 异常: {ex.Message}\n{ex.StackTrace}";
            WriteLog("ERROR", message);
        }

        /// <summary>
        /// 记录详细的操作日志
        /// </summary>
        /// <param name="operation">操作名称</param>
        /// <param name="details">操作详情</param>
        public static void Operation(string operation, string details)
        {
            WriteLog("INFO", $"[{operation}] {details}");
        }

        /// <summary>
        /// 记录操作开始日志
        /// </summary>
        /// <param name="operation">操作名称</param>
        public static void OperationStart(string operation)
        {
            WriteLog("INFO", $"[{operation}] 开始执行");
        }

        /// <summary>
        /// 记录操作完成日志
        /// </summary>
        /// <param name="operation">操作名称</param>
        /// <param name="result">操作结果</param>
        public static void OperationComplete(string operation, string result)
        {
            WriteLog("INFO", $"[{operation}] 执行完成，结果: {result}");
        }

        /// <summary>
        /// 获取日志目录
        /// </summary>
        /// <returns>日志目录路径</returns>
        public static string GetLogDirectory()
        {
            return LogDirectory;
        }

        /// <summary>
        /// 获取日志文件路径
        /// </summary>
        /// <returns>日志文件路径</returns>
        public static string GetLogFilePath()
        {
            return LogFilePath;
        }

        /// <summary>
        /// 清理过期日志
        /// </summary>
        public static void CleanupOldLogs()
        {
            try
            {
                if (!Directory.Exists(LogDirectory))
                {
                    return;
                }
                
                // 删除7天前的日志文件
                string[] logFiles = Directory.GetFiles(LogDirectory, "*.log");
                foreach (string logFile in logFiles)
                {
                    try
                    {
                        FileInfo fileInfo = new FileInfo(logFile);
                        if (fileInfo.LastWriteTime < DateTime.Now.AddDays(-7))
                        {
                            File.Delete(logFile);
                            Console.WriteLine($"[{DateTime.Now}] 已删除过期日志文件: {logFile}");
                        }
                    }
                    catch (Exception fileEx)
                    {
                        Console.WriteLine($"[{DateTime.Now}] 删除过期日志文件失败: {fileEx.Message}");
                        // 继续执行，不中断清理过程
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[{DateTime.Now}] 清理过期日志异常: {ex.Message}");
                ApplicationStateManager.LogException(ex, "LogManager.CleanupOldLogs");
                // 继续执行，不中断应用运行
            }
        }
    }
}