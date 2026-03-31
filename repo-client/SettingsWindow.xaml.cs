using System.Windows;
using System.Windows.Media.Animation;

namespace EasyYachiyoClient
{
    /// <summary>
    /// SettingsWindow.xaml 的交互逻辑
    /// </summary>
    public partial class SettingsWindow : Window
    {
        public SettingsWindow()
        {
            InitializeComponent();
            Loaded += SettingsWindow_Loaded;
        }

        private void SettingsWindow_Loaded(object sender, RoutedEventArgs e)
        {
            // 播放弹出动画
            Storyboard fadeInStoryboard = new Storyboard();
            DoubleAnimation fadeInAnimation = new DoubleAnimation
            {
                From = 0,
                To = 1,
                Duration = new Duration(new System.TimeSpan(0, 0, 0, 0, 300))
            };
            Storyboard.SetTarget(fadeInAnimation, this);
            Storyboard.SetTargetProperty(fadeInAnimation, new PropertyPath(OpacityProperty));
            fadeInStoryboard.Children.Add(fadeInAnimation);
            fadeInStoryboard.Begin();
        }

        public void CloseWithAnimation()
        {
            // 播放关闭动画
            Storyboard fadeOutStoryboard = new Storyboard();
            DoubleAnimation fadeOutAnimation = new DoubleAnimation
            {
                From = 1,
                To = 0,
                Duration = new Duration(new System.TimeSpan(0, 0, 0, 0, 300))
            };
            fadeOutAnimation.Completed += (sender, e) => Close();
            Storyboard.SetTarget(fadeOutAnimation, this);
            Storyboard.SetTargetProperty(fadeOutAnimation, new PropertyPath(OpacityProperty));
            fadeOutStoryboard.Children.Add(fadeOutAnimation);
            fadeOutStoryboard.Begin();
        }
    }
}