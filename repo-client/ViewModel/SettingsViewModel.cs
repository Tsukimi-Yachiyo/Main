using System.Windows.Input;
using EasyYachiyoClient.Utils;

namespace EasyYachiyoClient.ViewModel
{
    /// <summary>
    /// 设置窗口视图模型
    /// </summary>
    public class SettingsViewModel : ViewModelBase
    {
        #region 属性

        private string _selectedOption = "Model";
        /// <summary>
        /// 当前选中的设置选项
        /// </summary>
        public string SelectedOption
        {
            get => _selectedOption;
            set => SetProperty(ref _selectedOption, value);
        }

        private UserSettings _userSettings;
        /// <summary>
        /// 用户设置
        /// </summary>
        public UserSettings UserSettings
        {
            get => _userSettings;
            set => SetProperty(ref _userSettings, value);
        }

        private string _modelRunMode = "本地";
        /// <summary>
        /// 模型运行方式
        /// </summary>
        public string ModelRunMode
        {
            get => _modelRunMode;
            set
            {
                // 确保value是正确的字符串
                string cleanValue = value;
                if (value.Contains("本地"))
                {
                    cleanValue = "本地";
                }
                else if (value.Contains("远程"))
                {
                    cleanValue = "远程";
                }
                
                if (SetProperty(ref _modelRunMode, cleanValue))
                {
                    // 更新用户设置
                    UserSettings.ModelRunMode = cleanValue;
                    SaveSettings();
                    
                    // 检测远程连接状态
                    if (cleanValue == "远程")
                    {
                        _ = CheckRemoteConnectionStatusAsync();
                    }
                    else
                    {
                        RemoteConnectionStatus = "未检测";
                    }
                }
            }
        }

        private string _language = "中文";
        /// <summary>
        /// 语言设置
        /// </summary>
        public string Language
        {
            get => _language;
            set
            {
                if (SetProperty(ref _language, value))
                {
                    // 更新用户设置
                    UserSettings.Language = value;
                    SaveSettings();
                }
            }
        }

        private string _customRemoteAddress = string.Empty;
        /// <summary>
        /// 自定义远程地址
        /// </summary>
        public string CustomRemoteAddress
        {
            get => _customRemoteAddress;
            set
            {
                if (SetProperty(ref _customRemoteAddress, value))
                {
                    // 更新用户设置
                    UserSettings.CustomRemoteAddress = value;
                    SaveSettings();
                }
            }
        }

        private string _remoteConnectionStatus = "未检测";
        /// <summary>
        /// 远程连接状态
        /// </summary>
        public string RemoteConnectionStatus
        {
            get => _remoteConnectionStatus;
            set => SetProperty(ref _remoteConnectionStatus, value);
        }

        private bool _autoOpenLastConversation;
        /// <summary>
        /// 启动时自动打开上次对话
        /// </summary>
        public bool AutoOpenLastConversation
        {
            get => _autoOpenLastConversation;
            set
            {
                if (SetProperty(ref _autoOpenLastConversation, value))
                {
                    // 更新用户设置
                    UserSettings.AutoOpenLastConversation = value;
                    SaveSettings();
                }
            }
        }

        private bool _enableLogging;
        /// <summary>
        /// 启用日志记录
        /// </summary>
        public bool EnableLogging
        {
            get => _enableLogging;
            set
            {
                if (SetProperty(ref _enableLogging, value))
                {
                    // 更新用户设置
                    UserSettings.EnableLogging = value;
                    SaveSettings();
                }
            }
        }

        private int _progressValue;
        /// <summary>
        /// 进度条值
        /// </summary>
        public int ProgressValue
        {
            get => _progressValue;
            set => SetProperty(ref _progressValue, value);
        }

        private string _progressStatus;
        /// <summary>
        /// 进度状态
        /// </summary>
        public string ProgressStatus
        {
            get => _progressStatus;
            set => SetProperty(ref _progressStatus, value);
        }

        private bool _isProgressVisible;
        /// <summary>
        /// 进度条是否可见
        /// </summary>
        public bool IsProgressVisible
        {
            get => _isProgressVisible;
            set => SetProperty(ref _isProgressVisible, value);
        }

        #endregion

        #region 命令

        /// <summary>
        /// 关闭命令
        /// </summary>
        public ICommand CloseCommand { get; set; }

        /// <summary>
        /// 选择设置选项命令
        /// </summary>
        public ICommand SelectSettingOptionCommand { get; set; }

        /// <summary>
        /// 检测环境命令
        /// </summary>
        public ICommand CheckEnvironmentCommand { get; set; }

        /// <summary>
        /// 保存设置命令
        /// </summary>
        public ICommand SaveSettingsCommand { get; set; }

        #endregion

        /// <summary>
        /// 构造函数
        /// </summary>
        public SettingsViewModel()
        {
            // 加载用户设置
            LoadSettings();
            InitializeCommands();
        }

        /// <summary>
        /// 初始化命令
        /// </summary>
        private void InitializeCommands()
        {
            CloseCommand = new RelayCommand(Close);
            SelectSettingOptionCommand = new RelayCommand(SelectSettingOption);
            CheckEnvironmentCommand = new RelayCommand(async (param) => await CheckEnvironmentAsync());
            SaveSettingsCommand = new RelayCommand(SaveSettings);
        }

        /// <summary>
        /// 加载用户设置
        /// </summary>
        private void LoadSettings()
        {
            UserSettings = SettingsManager.LoadSettings();
            // 确保ModelRunMode是正确的字符串
            if (UserSettings.ModelRunMode.Contains("本地"))
            {
                ModelRunMode = "本地";
            }
            else if (UserSettings.ModelRunMode.Contains("远程"))
            {
                ModelRunMode = "远程";
            }
            else
            {
                ModelRunMode = "本地";
            }
            
            // 加载语言设置
            Language = UserSettings.Language;
            
            // 加载自定义远程地址
            CustomRemoteAddress = UserSettings.CustomRemoteAddress;
            
            // 加载其他设置
            AutoOpenLastConversation = UserSettings.AutoOpenLastConversation;
            EnableLogging = UserSettings.EnableLogging;
            
            // 检测远程连接状态
            if (ModelRunMode == "远程")
            {
                _ = CheckRemoteConnectionStatusAsync();
            }
        }

        /// <summary>
        /// 保存用户设置
        /// </summary>
        private void SaveSettings()
        {
            SettingsManager.SaveSettings(UserSettings);
        }

        /// <summary>
        /// 保存用户设置（命令参数）
        /// </summary>
        /// <param name="parameter">参数</param>
        private void SaveSettings(object? parameter)
        {
            SaveSettings();
        }

        /// <summary>
        /// 检测环境
        /// </summary>
        private async System.Threading.Tasks.Task CheckEnvironmentAsync()
        {
            if (ModelRunMode == "本地")
            {
                IsProgressVisible = true;
                ProgressValue = 0;
                ProgressStatus = "准备检测环境...";

                // 执行环境检测
                bool isReady = await OllamaEnvironmentManager.CheckAndPrepareEnvironmentAsync((progress, status) =>
                {
                    ProgressValue = progress;
                    ProgressStatus = status;
                });

                if (isReady)
                {
                    ProgressStatus = "环境检测完成，所有依赖项已就绪";
                }
                else
                {
                    ProgressStatus = "环境检测未完成";
                }

                // 延迟隐藏进度条
                await System.Threading.Tasks.Task.Delay(1000);
                IsProgressVisible = false;
            }
            else if (ModelRunMode == "远程")
            {
                // 检测远程连接状态
                await CheckRemoteConnectionStatusAsync();
            }
        }

        /// <summary>
        /// 检测远程连接状态
        /// </summary>
        private async System.Threading.Tasks.Task CheckRemoteConnectionStatusAsync()
        {
            RemoteConnectionStatus = "检测中...";
            
            try
            {
                // 测试远程连接
                bool isConnected = await RemoteConnectionManager.TestRemoteConnectionAsync();
                
                if (isConnected)
                {
                    RemoteConnectionStatus = "连接成功";
                }
                else
                {
                    RemoteConnectionStatus = "连接失败";
                }
            }
            catch (Exception ex)
            {
                RemoteConnectionStatus = "检测失败";
                LogManager.Error($"检测远程连接状态时发生异常: {ex.Message}");
            }
        }

        /// <summary>
        /// 关闭设置窗口
        /// </summary>
        /// <param name="parameter">参数</param>
        private void Close(object? parameter)
        {
            // 保存设置
            SaveSettings();

            // 获取设置窗口并关闭
            foreach (var window in System.Windows.Application.Current.Windows)
            {
                if (window.GetType().Name == "SettingsWindow")
                {
                    // 使用反射调用CloseWithAnimation方法
                    var closeMethod = window.GetType().GetMethod("CloseWithAnimation");
                    if (closeMethod != null)
                    {
                        closeMethod.Invoke(window, null);
                    }
                    else
                    {
                        // 强制转换为Window类型
                        if (window is System.Windows.Window wpfWindow)
                        {
                            wpfWindow.Close();
                        }
                    }
                    break;
                }
            }
        }

        /// <summary>
        /// 选择设置选项
        /// </summary>
        /// <param name="parameter">参数</param>
        private void SelectSettingOption(object? parameter)
        {
            if (parameter is string optionName)
            {
                SelectedOption = optionName;
            }
        }
    }
}