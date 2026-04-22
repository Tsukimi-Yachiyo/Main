using System.Windows;
using System.Windows.Input;
using System.Windows.Controls;
using System.Windows.Media.Imaging;
using EasyYachiyoClient.Utils;
using CefSharp.Wpf;
using CefSharp;

namespace EasyYachiyoClient
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private bool _isDragging;
        private Point _startPoint;
        private Live2DWebServer _webServer;
        private ChromiumWebBrowser live2DWebView;

        public MainWindow()
        {
            InitializeComponent();
            // 加载图片资源
            LoadImages();
            // 为标题栏添加鼠标事件，实现窗体拖动
            if (TitleBar is Grid titleBar)
            {
                titleBar.MouseLeftButtonDown += TitleBar_MouseLeftButtonDown;
                titleBar.MouseLeftButtonUp += TitleBar_MouseLeftButtonUp;
                titleBar.MouseMove += TitleBar_MouseMove;
            }
            // 初始化 Live2D 服务和 WebView2
            InitializeLive2D();
        }

        /// <summary>
        /// 初始化 Live2D 服务和 CefSharp
        /// </summary>
        private void InitializeLive2D()
        {
            try
            {
                LogManager.OperationStart("初始化 Live2D 服务和 CefSharp");
                
                // 启动 Web 服务
                string distPath = System.IO.Path.Combine(System.AppDomain.CurrentDomain.BaseDirectory, "resource", "dist");
                LogManager.Info($"Live2D 资源路径: {distPath}");
                
                _webServer = new Live2DWebServer(distPath);
                bool serverStarted = _webServer.Start();

                if (serverStarted)
                {
                    LogManager.Info("Live2D Web 服务启动成功");
                    
                    // 确保 CefSharp 已初始化
                    LogManager.Info("检查 CefSharp 初始化状态");
                    if (!Cef.IsInitialized.GetValueOrDefault())
                    {
                        LogManager.Info("CefSharp 尚未初始化，正在初始化...");
                        var settings = new CefSettings();
                        settings.CefCommandLineArgs.Add("enable-javascript", "1");
                        settings.CefCommandLineArgs.Add("enable-plugins", "1");
                        Cef.Initialize(settings);
                        LogManager.Info("CefSharp 初始化完成");
                    }
                    else
                    {
                        LogManager.Info("CefSharp 已初始化");
                    }

                    // 配置 ChromiumWebBrowser
                    LogManager.Info("配置 ChromiumWebBrowser");
                    live2DWebView = new ChromiumWebBrowser();
                    live2DWebView.MenuHandler = null; // 禁用右键菜单
                    live2DWebViewContainer.Children.Add(live2DWebView);
                    
                    // 加载 Web 服务提供的页面
                    string serverUrl = _webServer.ServerUrl;
                    LogManager.Info($"加载 Live2D 页面: {serverUrl}");
                    live2DWebView.Address = serverUrl;
                    
                    // 添加事件处理
                    live2DWebView.FrameLoadStart += (sender, e) =>
                    {
                        LogManager.Debug($"FrameLoadStart: {e.Url}");
                    };
                    
                    live2DWebView.FrameLoadEnd += (sender, e) =>
                    {
                        LogManager.Debug($"FrameLoadEnd: {e.Url}");
                    };
                    
                    live2DWebView.LoadError += (sender, e) =>
                    {
                        LogManager.Error($"LoadError: {e.FailedUrl}, ErrorCode: {e.ErrorCode}, ErrorText: {e.ErrorText}");
                    };
                    
                    LogManager.OperationComplete("初始化 Live2D 服务和 CefSharp", "成功");
                }
                else
                {
                    LogManager.Error("Live2D Web 服务启动失败");
                    LogManager.OperationComplete("初始化 Live2D 服务和 CefSharp", "失败");
                }
            }
            catch (Exception ex)
            {
                LogManager.Error($"初始化 Live2D 失败: {ex.Message}");
                LogManager.WriteException(ex, "MainWindow.InitializeLive2D");
                LogManager.OperationComplete("初始化 Live2D 服务和 CefSharp", "失败");
            }
        }

        /// <summary>
        /// 加载图片资源
        /// </summary>
        private void LoadImages()
        {
            try
            {
                // 这里可以添加图片加载逻辑
                // 例如：WindowTitleIcon.Source = ImageLoader.LoadImage("resource/window_title_icon.jpg");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"图片加载错误: {ex.Message}");
            }
        }

        #region 窗体拖动功能
        private void TitleBar_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            _isDragging = true;
            _startPoint = e.GetPosition(this);
            Mouse.Capture(sender as IInputElement);
        }

        private void TitleBar_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            _isDragging = false;
            Mouse.Capture(null);
        }

        private void TitleBar_MouseMove(object sender, MouseEventArgs e)
        {
            if (_isDragging)
            {
                Point currentPoint = e.GetPosition(this);
                double offsetX = currentPoint.X - _startPoint.X;
                double offsetY = currentPoint.Y - _startPoint.Y;
                Left += offsetX;
                Top += offsetY;
            }
        }
        #endregion



        /// <summary>
        /// 清理资源
        /// </summary>
        /// <param name="e">事件参数</param>
        protected override void OnClosed(EventArgs e)
        {
            try
            {
                LogManager.OperationStart("清理资源");
                
                // 取消事件订阅
                if (TitleBar is Grid titleBar)
                {
                    titleBar.MouseLeftButtonDown -= TitleBar_MouseLeftButtonDown;
                    titleBar.MouseLeftButtonUp -= TitleBar_MouseLeftButtonUp;
                    titleBar.MouseMove -= TitleBar_MouseMove;
                }

                // 停止 Live2D Web 服务
                LogManager.Info("停止 Live2D Web 服务");
                _webServer?.Dispose();

                // CefSharp 将在应用退出时清理

                // 清理图片缓存
                LogManager.Info("清理图片缓存");
                ImageLoader.ClearCache();

                LogManager.OperationComplete("清理资源", "成功");
            }
            catch (Exception ex)
            {
                LogManager.Error($"清理资源失败: {ex.Message}");
                LogManager.WriteException(ex, "MainWindow.OnClosed");
            }
            finally
            {
                base.OnClosed(e);
            }
        }
    }
}