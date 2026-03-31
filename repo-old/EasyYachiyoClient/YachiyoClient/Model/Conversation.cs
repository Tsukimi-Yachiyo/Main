using System.Collections.ObjectModel;

namespace EasyYachiyoClient.Model
{
    /// <summary>
    /// 对话实体类
    /// </summary>
    public class Conversation
    {
        /// <summary>
        /// 对话ID
        /// </summary>
        public int Id { get; set; }

        /// <summary>
        /// 对话标题
        /// </summary>
        public string Title { get; set; } = string.Empty;

        /// <summary>
        /// 对话消息列表
        /// </summary>
        public ObservableCollection<Message> Messages { get; set; }

        /// <summary>
        /// 对话创建时间
        /// </summary>
        public System.DateTime CreatedAt { get; set; }

        /// <summary>
        /// 构造函数
        /// </summary>
        public Conversation()
        {
            CreatedAt = System.DateTime.Now;
            Messages = new ObservableCollection<Message>();
        }
    }
}