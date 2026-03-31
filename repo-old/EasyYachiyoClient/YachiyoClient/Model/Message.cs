using EasyYachiyoClient.ViewModel;

namespace EasyYachiyoClient.Model
{
    /// <summary>
    /// 消息实体类
    /// </summary>
    public class Message : ViewModelBase
    {
        private int _id;
        /// <summary>
        /// 消息ID
        /// </summary>
        public int Id 
        { 
            get => _id; 
            set => SetProperty(ref _id, value); 
        }

        private string _content = string.Empty;
        /// <summary>
        /// 消息内容
        /// </summary>
        public string Content 
        { 
            get => _content; 
            set => SetProperty(ref _content, value); 
        }

        private bool _isUser;
        /// <summary>
        /// 是否为用户消息
        /// </summary>
        public bool IsUser 
        { 
            get => _isUser; 
            set => SetProperty(ref _isUser, value); 
        }

        private System.DateTime _timestamp;
        /// <summary>
        /// 消息时间戳
        /// </summary>
        public System.DateTime Timestamp 
        { 
            get => _timestamp; 
            set => SetProperty(ref _timestamp, value); 
        }

        private byte[]? _audioData;
        /// <summary>
        /// 音频数据
        /// </summary>
        public byte[]? AudioData 
        { 
            get => _audioData; 
            set => SetProperty(ref _audioData, value); 
        }

        private bool _isPlaying;
        /// <summary>
        /// 是否正在播放
        /// </summary>
        public bool IsPlaying 
        { 
            get => _isPlaying; 
            set => SetProperty(ref _isPlaying, value); 
        }

        private bool _audioGenerated;
        /// <summary>
        /// 音频是否已生成
        /// </summary>
        public bool AudioGenerated 
        { 
            get => _audioGenerated; 
            set => SetProperty(ref _audioGenerated, value); 
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        public Message()
        {
            Timestamp = System.DateTime.Now;
        }
    }
}