using System.Windows;

namespace EasyYachiyoClient
{
    /// <summary>
    /// 启动进度条窗口
    /// </summary>
    public partial class SplashWindow : Window
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        public SplashWindow()
        {
            InitializeComponent();
        }

        /// <summary>
        /// 更新进度
        /// </summary>
        /// <param name="value">进度值（0-100）</param>
        public void UpdateProgress(int value)
        {
            Dispatcher.Invoke(() =>
            {
                if (value >= 0 && value <= 100)
                {
                    ProgressBar.Value = value;
                    ProgressText.Text = $"{value}%";
                }
            });
        }

        /// <summary>
        /// 更新状态文本
        /// </summary>
        /// <param name="text">状态文本</param>
        public void UpdateStatus(string text)
        {
            Dispatcher.Invoke(() =>
            {
                StatusText.Text = text;
            });
        }

        /// <summary>
        /// 更新副进度条
        /// </summary>
        /// <param name="value">进度值（0-100）</param>
        public void UpdateSecondaryProgress(int value)
        {
            Dispatcher.Invoke(() =>
            {
                if (value >= 0 && value <= 100)
                {
                    SecondaryProgressBar.Value = value;
                }
            });
        }

        /// <summary>
        /// 更新任务描述
        /// </summary>
        /// <param name="text">任务描述文本</param>
        public void UpdateTaskDescription(string text)
        {
            Dispatcher.Invoke(() =>
            {
                TaskDescriptionText.Text = text;
            });
        }

        /// <summary>
        /// 关闭窗口
        /// </summary>
        public void CloseWindow()
        {
            Dispatcher.Invoke(() =>
            {
                Close();
            });
        }
    }
}