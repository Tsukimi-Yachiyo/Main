using System;
using System.Diagnostics;
using System.Timers;
using Timer = System.Timers.Timer;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// 应用状态管理类，用于监控应用状态并处理异常情况
    /// </summary>
    public static class ApplicationStateManager
    {
        /// <summary>
        /// 内存监控定时器
        /// </summary>
        private static Timer _memoryMonitorTimer;

        /// <summary>
        /// 内存使用阈值（MB）
        /// </summary>
        private const int MemoryThreshold = 500;

        /// <summary>
        /// 初始化应用状态管理
        /// </summary>
        public static void Initialize()
        {
            // 加载设置
            UserSettings settings = SettingsManager.LoadSettings();
            bool isProductionMode = settings.ProductionMode;
            
            // 设置全局异常处理
            AppDomain.CurrentDomain.UnhandledException += CurrentDomain_UnhandledException;
            System.Windows.Application.Current.DispatcherUnhandledException += Current_DispatcherUnhandledException;

            // 非生产模式下才初始化内存监控
            if (!isProductionMode)
            {
                InitializeMemoryMonitor();
            }

            // 记录应用启动（非生产模式）
            if (!isProductionMode)
            {
                LogApplicationStart();
            }
        }

        /// <summary>
        /// 初始化内存监控
        /// </summary>
        private static void InitializeMemoryMonitor()
        {
            _memoryMonitorTimer = new Timer(30000); // 每30秒检查一次
            _memoryMonitorTimer.Elapsed += MemoryMonitorTimer_Elapsed;
            _memoryMonitorTimer.Start();
        }

        /// <summary>
        /// 内存监控定时器事件
        /// </summary>
        /// <param name="sender">发送者</param>
        /// <param name="e">事件参数</param>
        private static void MemoryMonitorTimer_Elapsed(object sender, ElapsedEventArgs e)
        {
            try
            {
                Process currentProcess = Process.GetCurrentProcess();
                long memoryUsage = currentProcess.PrivateMemorySize64 / (1024 * 1024); // 转换为MB

                Console.WriteLine($"内存使用: {memoryUsage} MB");

                // 检查内存使用是否超过阈值
                if (memoryUsage > MemoryThreshold)
                {
                    Console.WriteLine("警告: 内存使用超过阈值，尝试清理资源...");
                    // 清理图片缓存
                    ImageLoader.ClearCache();
                    // 强制垃圾回收
                    GC.Collect();
                    GC.WaitForPendingFinalizers();
                    Console.WriteLine("资源清理完成");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"内存监控错误: {ex.Message}");
            }
        }

        /// <summary>
        /// 全局未处理异常事件
        /// </summary>
        /// <param name="sender">发送者</param>
        /// <param name="e">事件参数</param>
        private static void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            Exception ex = e.ExceptionObject as Exception;
            LogException(ex, "全局未处理异常");
        }

        /// <summary>
        /// Dispatcher未处理异常事件
        /// </summary>
        /// <param name="sender">发送者</param>
        /// <param name="e">事件参数</param>
        private static void Current_DispatcherUnhandledException(object sender, System.Windows.Threading.DispatcherUnhandledExceptionEventArgs e)
        {
            LogException(e.Exception, "Dispatcher未处理异常");
            e.Handled = true; // 标记异常已处理，防止应用崩溃
        }

        /// <summary>
        /// 记录异常
        /// </summary>
        /// <param name="ex">异常</param>
        /// <param name="source">异常来源</param>
        public static void LogException(Exception ex, string source)
        {
            string logMessage = $"[{DateTime.Now}] 来源: {source}\n" +
                               $"异常类型: {ex.GetType().Name}\n" +
                               $"异常消息: {ex.Message}\n" +
                               $"堆栈跟踪: {ex.StackTrace}\n";

            // 非生产模式下才输出到控制台
            UserSettings settings = SettingsManager.LoadSettings();
            if (!settings.ProductionMode)
            {
                Console.WriteLine(logMessage);
            }

            // 这里可以添加日志文件写入逻辑
        }

        /// <summary>
        /// 记录应用启动
        /// </summary>
        private static void LogApplicationStart()
        {
            string logMessage = $"[{DateTime.Now}] 应用启动\n" +
                               $"应用版本: {System.Reflection.Assembly.GetExecutingAssembly().GetName().Version}\n" +
                               $"操作系统: {Environment.OSVersion.VersionString}\n" +
                               $"CLR版本: {Environment.Version}\n";

            Console.WriteLine(logMessage);
        }

        /// <summary>
        /// 清理资源
        /// </summary>
        public static void Cleanup()
        {
            // 停止内存监控定时器
            if (_memoryMonitorTimer != null)
            {
                _memoryMonitorTimer.Stop();
                _memoryMonitorTimer.Dispose();
                _memoryMonitorTimer = null;
            }

            // 清理图片缓存
            ImageLoader.ClearCache();

            // 强制垃圾回收
            GC.Collect();
            GC.WaitForPendingFinalizers();

            // 非生产模式下才记录应用关闭
            UserSettings settings = SettingsManager.LoadSettings();
            if (!settings.ProductionMode)
            {
                Console.WriteLine($"[{DateTime.Now}] 应用关闭");
            }
        }
    }
}
