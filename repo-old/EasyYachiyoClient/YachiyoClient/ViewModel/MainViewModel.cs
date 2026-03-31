using System.Collections.ObjectModel;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Windows.Input;
using EasyYachiyoClient.Model;
using EasyYachiyoClient.Utils;
using EasyYachiyoClient;

namespace EasyYachiyoClient.ViewModel
{
    /// <summary>
    /// 主窗口视图模型
    /// </summary>
    public class MainViewModel : ViewModelBase
    {
        #region 属性
        private ObservableCollection<Conversation> _conversations = new ObservableCollection<Conversation>();
        /// <summary>
        /// 对话列表
        /// </summary>
        public ObservableCollection<Conversation> Conversations
        {
            get => _conversations;
            set => SetProperty(ref _conversations, value);
        }

        private Conversation? _selectedConversation;
        /// <summary>
        /// 当前选中的对话
        /// </summary>
        public Conversation? SelectedConversation
        {
            get => _selectedConversation;
            set 
            {
                // 切换对话时取消当前语音操作
                SpeechServiceManager.CancelCurrentOperation();
                SetProperty(ref _selectedConversation, value);
            }
        }

        /// <summary>
        /// 当前的取消令牌源
        /// </summary>
        private CancellationTokenSource? _currentCts;

        private string _messageText = string.Empty;
        /// <summary>
        /// 输入消息文本
        /// </summary>
        public string MessageText
        {
            get => _messageText;
            set => SetProperty(ref _messageText, value);
        }

        private bool _isLoading;
        /// <summary>
        /// 是否正在加载
        /// </summary>
        public bool IsLoading
        {
            get => _isLoading;
            set => SetProperty(ref _isLoading, value);
        }

        private string _loadingStatus = string.Empty;
        /// <summary>
        /// 加载状态文本
        /// </summary>
        public string LoadingStatus
        {
            get => _loadingStatus;
            set => SetProperty(ref _loadingStatus, value);
        }
        #endregion

        #region 命令
        /// <summary>
        /// 发送消息命令
        /// </summary>
        public ICommand SendMessageCommand { get; set; }

        /// <summary>
        /// 添加新对话命令
        /// </summary>
        public ICommand AddConversationCommand { get; set; }

        /// <summary>
        /// 设置命令
        /// </summary>
        public ICommand SettingsCommand { get; set; }

        /// <summary>
        /// 最小化命令
        /// </summary>
        public ICommand MinimizeCommand { get; set; }

        /// <summary>
        /// 关闭命令
        /// </summary>
        public ICommand CloseCommand { get; set; }

        /// <summary>
        /// 播放音频命令
        /// </summary>
        public ICommand PlayAudioCommand { get; set; }

        /// <summary>
        /// 播放/暂停音频命令
        /// </summary>
        public ICommand PlayPauseAudioCommand { get; set; }
        #endregion

        /// <summary>
        /// 构造函数
        /// </summary>
        public MainViewModel()
        {
            InitializeData();
            InitializeCommands();
        }

        /// <summary>
        /// 初始化数据
        /// </summary>
        private void InitializeData()
        {
            Conversations = new ObservableCollection<Conversation>
            {
                new Conversation { Id = 1, Title = "新对话", Messages = new ObservableCollection<Message>() }
            };
            SelectedConversation = Conversations[0];
        }

        /// <summary>
        /// 初始化命令
        /// </summary>
        private void InitializeCommands()
        {
            SendMessageCommand = new RelayCommand(SendMessage);
            AddConversationCommand = new RelayCommand(AddConversation);
            SettingsCommand = new RelayCommand(OpenSettings);
            MinimizeCommand = new RelayCommand(MinimizeWindow);
            CloseCommand = new RelayCommand(CloseWindow);
            PlayAudioCommand = new RelayCommand(PlayAudio);
            PlayPauseAudioCommand = new RelayCommand(PlayPauseAudio);
        }

        #region 命令执行方法
        /// <summary>
        /// 发送消息
        /// </summary>
        /// <param name="parameter">参数</param>
        private async void SendMessage(object? parameter)
        {
            if (!string.IsNullOrEmpty(MessageText) && SelectedConversation != null)
            {
                // 添加用户消息
                SelectedConversation.Messages.Add(new Message
                {
                    Id = SelectedConversation.Messages.Count + 1,
                    Content = MessageText,
                    IsUser = true,
                    Timestamp = System.DateTime.Now
                });

                // 清空输入框
                MessageText = string.Empty;

                // 显示加载状态
                IsLoading = true;
                LoadingStatus = "等待中...";

                try
                {
                    // 向API发送POST请求
                    using HttpClient client = new HttpClient();
                    client.Timeout = System.TimeSpan.FromMinutes(8);

                    // 构建请求体
                    var requestBody = new { prompt = SelectedConversation.Messages[SelectedConversation.Messages.Count - 1].Content };
                    string jsonBody = JsonSerializer.Serialize(requestBody);
                    var content = new StringContent(jsonBody, Encoding.UTF8, "application/json");

                    // 发送请求
                    string apiUrl = AddressConfigurationManager.GetFullAddress("/api/v1/ai/chat");
                    HttpResponseMessage response = await client.PostAsync(apiUrl, content);
                    response.EnsureSuccessStatusCode();

                    // 读取响应
                    string responseContent = await response.Content.ReadAsStringAsync();

                    // 尝试解析响应
                string aiReply;
                try
                {
                    // 尝试将响应解析为JSON（假设响应格式为 { "response": "AI回复内容" }）
                    var responseData = JsonSerializer.Deserialize<dynamic>(responseContent);
                    aiReply = responseData.response;
                }
                catch
                {
                    // 如果解析失败，则将整个响应内容作为AI回复（纯文本格式）
                    aiReply = responseContent;
                }

                // 获取新的取消令牌源
                _currentCts = SpeechServiceManager.GetNewCancellationTokenSource();

                // 调用语音服务将文本转换为语音
                byte[]? audioData = await SpeechServiceManager.TextToSpeechAsync(aiReply, _currentCts.Token);

                // 添加AI回复（等待语音生成完毕后）
                System.Windows.Application.Current.Dispatcher.Invoke(() =>
                {
                    if (SelectedConversation != null && (_currentCts == null || !_currentCts.IsCancellationRequested))
                    {
                        var aiMessage = new Message
                        {
                            Id = SelectedConversation.Messages.Count + 1,
                            Content = aiReply,
                            IsUser = false,
                            Timestamp = System.DateTime.Now,
                            AudioData = audioData,
                            AudioGenerated = audioData != null
                        };
                        SelectedConversation.Messages.Add(aiMessage);

                        // 如果音频生成成功，自动播放
                        if (audioData != null)
                        {
                            _ = SpeechServiceManager.PlayAudioAsync(audioData);
                        }
                    }
                });
                }
                catch (Exception ex)
                {
                    // 处理异常
                    System.Windows.Application.Current.Dispatcher.Invoke(() =>
                    {
                        if (SelectedConversation != null)
                        {
                            SelectedConversation.Messages.Add(new Message
                            {
                                Id = SelectedConversation.Messages.Count + 1,
                                Content = $"错误：{ex.Message}",
                                IsUser = false,
                                Timestamp = System.DateTime.Now
                            });
                        }
                    });
                }
                finally
                {
                    // 隐藏加载状态
                    IsLoading = false;
                    LoadingStatus = string.Empty;
                }
            }
        }

        /// <summary>
        /// 添加新对话
        /// </summary>
        /// <param name="parameter">参数</param>
        private void AddConversation(object? parameter)
        {
            var newConversation = new Conversation
            {
                Id = Conversations.Count + 1,
                Title = $"新对话 {Conversations.Count + 1}",
                Messages = new ObservableCollection<Message>()
            };

            Conversations.Add(newConversation);
            SelectedConversation = newConversation;
        }

        /// <summary>
        /// 打开设置
        /// </summary>
        /// <param name="parameter">参数</param>
        private void OpenSettings(object? parameter)
        {
            // 创建并显示设置窗口
            SettingsWindow settingsWindow = new SettingsWindow();
            // 设置窗口所有者为当前主窗口
            foreach (var window in System.Windows.Application.Current.Windows)
            {
                if (window.GetType().Name == "MainWindow")
                {
                    if (window is System.Windows.Window mainWindow)
                    {
                        settingsWindow.Owner = mainWindow;
                    }
                    break;
                }
            }
            // 以模态方式显示设置窗口
            settingsWindow.ShowDialog();
        }

        /// <summary>
        /// 最小化窗口
        /// </summary>
        /// <param name="parameter">参数</param>
        private void MinimizeWindow(object? parameter)
        {
            if (parameter is System.Windows.Window window)
            {
                window.WindowState = System.Windows.WindowState.Minimized;
            }
        }

        /// <summary>
        /// 关闭窗口
        /// </summary>
        /// <param name="parameter">参数</param>
        private void CloseWindow(object? parameter)
        {
            if (parameter is System.Windows.Window window)
            {
                window.Close();
            }
        }

        /// <summary>
        /// 播放音频
        /// </summary>
        /// <param name="parameter">消息对象</param>
        private async void PlayAudio(object? parameter)
        {
            if (parameter is Model.Message message && message.AudioData != null)
            {
                await SpeechServiceManager.PlayAudioAsync(message.AudioData, message);
            }
        }

        /// <summary>
        /// 播放/暂停音频
        /// </summary>
        /// <param name="parameter">消息对象</param>
        private async void PlayPauseAudio(object? parameter)
        {
            if (parameter is Model.Message message && message.AudioData != null)
            {
                if (message.IsPlaying)
                {
                    // 如果正在播放，则暂停
                    SpeechServiceManager.PausePlayback();
                }
                else
                {
                    // 如果未播放或已暂停，则开始播放
                    await SpeechServiceManager.PlayAudioAsync(message.AudioData, message);
                }
            }
        }
        #endregion
    }
}