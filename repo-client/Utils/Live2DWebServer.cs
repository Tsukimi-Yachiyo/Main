using System;
using System.IO;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// Live2D Web 服务类，用于提供静态资源访问
    /// </summary>
    public class Live2DWebServer : IDisposable
    {
        private HttpListener _listener;
        private Thread _serverThread;
        private bool _isRunning;
        private string _rootDirectory;
        private int _port;
        private const int DefaultPort = 3250;
        private const int MaxPortAttempts = 10;

        /// <summary>
        /// 服务是否正在运行
        /// </summary>
        public bool IsRunning => _isRunning;

        /// <summary>
        /// 服务端口
        /// </summary>
        public int Port => _port;

        /// <summary>
        /// 服务 URL
        /// </summary>
        public string ServerUrl => _isRunning ? $"http://localhost:{_port}/" : null;

        /// <summary>
        /// 根目录
        /// </summary>
        public string RootDirectory => _rootDirectory;

        /// <summary>
        /// 构造函数
        /// </summary>
        /// <param name="rootDirectory">静态资源根目录</param>
        public Live2DWebServer(string rootDirectory)
        {
            _rootDirectory = rootDirectory;
        }

        /// <summary>
        /// 启动服务
        /// </summary>
        /// <returns>是否启动成功</returns>
        public bool Start()
        {
            if (_isRunning)
            {
                LogManager.Info("Live2D Web 服务已经在运行");
                return true;
            }

            LogManager.OperationStart("启动 Live2D Web 服务");
            LogManager.Info($"服务根目录: {_rootDirectory}");

            // 尝试从默认端口开始，最多尝试 MaxPortAttempts 个端口
            for (int i = 0; i < MaxPortAttempts; i++)
            {
                _port = DefaultPort + i;
                _listener = new HttpListener();
                _listener.Prefixes.Add($"http://localhost:{_port}/");

                try
                {
                    _listener.Start();
                    _isRunning = true;
                    _serverThread = new Thread(Run);
                    _serverThread.IsBackground = true;
                    _serverThread.Start();
                    
                    string serverUrl = ServerUrl;
                    LogManager.OperationComplete("启动 Live2D Web 服务", $"成功，服务 URL: {serverUrl}");
                    return true;
                }
                catch (HttpListenerException ex)
                {
                    // 端口被占用，尝试下一个端口
                    LogManager.Warn($"端口 {_port} 被占用，尝试下一个端口: {ex.Message}");
                    _listener.Close();
                    _listener = null;
                }
                catch (Exception ex)
                {
                    // 其他异常
                    LogManager.Error($"启动服务时发生异常: {ex.Message}");
                    _listener.Close();
                    _listener = null;
                }
            }

            LogManager.Error("启动 Live2D Web 服务失败，所有端口尝试都失败");
            return false;
        }

        /// <summary>
        /// 停止服务
        /// </summary>
        public void Stop()
        {
            if (!_isRunning)
                return;

            _isRunning = false;
            _listener?.Stop();
            _serverThread?.Join();
        }

        /// <summary>
        /// 服务运行方法
        /// </summary>
        private void Run()
        {
            while (_isRunning)
            {
                try
                {
                    var context = _listener.GetContext();
                    Task.Run(() => HandleRequest(context));
                }
                catch (HttpListenerException)
                {
                    // 服务可能已停止
                    break;
                }
                catch (Exception)
                {
                    // 忽略其他异常，继续运行
                }
            }
        }

        /// <summary>
        /// 处理 HTTP 请求
        /// </summary>
        /// <param name="context">HTTP 上下文</param>
        private void HandleRequest(HttpListenerContext context)
        {
            try
            {
                var request = context.Request;
                var response = context.Response;
                string filePath = request.Url.LocalPath;
                
                LogManager.Debug($"收到请求: {filePath}");

                // 获取请求的文件路径
                if (filePath == "/")
                    filePath = "/index.html";

                // 构建本地文件路径
                string localPath = Path.Combine(_rootDirectory, filePath.TrimStart('/'));

                // 检查文件是否存在
                if (File.Exists(localPath))
                {
                    try
                    {
                        // 读取文件内容
                        byte[] buffer = File.ReadAllBytes(localPath);

                        // 设置响应头
                        response.ContentLength64 = buffer.Length;
                        response.ContentType = GetContentType(Path.GetExtension(localPath));

                        // 发送响应
                        using (var output = response.OutputStream)
                        {
                            output.Write(buffer, 0, buffer.Length);
                        }
                        
                        LogManager.Debug($"成功响应: {filePath}");
                    }
                    catch (Exception ex)
                    {
                        // 读取文件异常
                        LogManager.Error($"读取文件失败: {localPath}, 错误: {ex.Message}");
                        response.StatusCode = (int)HttpStatusCode.InternalServerError;
                        string errorMessage = "Internal server error";
                        byte[] buffer = Encoding.UTF8.GetBytes(errorMessage);
                        response.ContentLength64 = buffer.Length;
                        response.ContentType = "text/plain";

                        using (var output = response.OutputStream)
                        {
                            output.Write(buffer, 0, buffer.Length);
                        }
                    }
                }
                else
                {
                    // 文件不存在，返回 404
                    LogManager.Warn($"文件不存在: {localPath}");
                    response.StatusCode = (int)HttpStatusCode.NotFound;
                    string errorMessage = "File not found";
                    byte[] buffer = Encoding.UTF8.GetBytes(errorMessage);
                    response.ContentLength64 = buffer.Length;
                    response.ContentType = "text/plain";

                    using (var output = response.OutputStream)
                    {
                        output.Write(buffer, 0, buffer.Length);
                    }
                }
            }
            catch (Exception ex)
            {
                // 其他异常
                LogManager.Error($"处理请求时发生异常: {ex.Message}");
            }
            finally
            {
                context.Response.Close();
            }
        }

        /// <summary>
        /// 根据文件扩展名获取内容类型
        /// </summary>
        /// <param name="extension">文件扩展名</param>
        /// <returns>内容类型</returns>
        private string GetContentType(string extension)
        {
            switch (extension.ToLower())
            {
                case ".html":
                case ".htm":
                    return "text/html";
                case ".css":
                    return "text/css";
                case ".js":
                    return "application/javascript";
                case ".json":
                    return "application/json";
                case ".png":
                    return "image/png";
                case ".jpg":
                case ".jpeg":
                    return "image/jpeg";
                case ".gif":
                    return "image/gif";
                case ".webp":
                    return "image/webp";
                case ".mp3":
                    return "audio/mpeg";
                case ".mp4":
                    return "video/mp4";
                default:
                    return "application/octet-stream";
            }
        }

        /// <summary>
        /// 释放资源
        /// </summary>
        public void Dispose()
        {
            Stop();
            _listener?.Close();
        }
    }
}