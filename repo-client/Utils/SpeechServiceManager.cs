using System;
using System.IO;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Media;
using EasyYachiyoClient.Model;
using EasyYachiyoClient.Utils;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// 语音服务管理类，负责文本转语音和语音播放
    /// </summary>
    public static class SpeechServiceManager
    {
        /// <summary>
        /// 语音播放器
        /// </summary>
        private static MediaPlayer _mediaPlayer = new MediaPlayer();

        /// <summary>
        /// 当前的取消令牌源
        /// </summary>
        private static CancellationTokenSource _cts = new CancellationTokenSource();

        /// <summary>
        /// 当前是否正在播放语音
        /// </summary>
        public static bool IsPlaying { get; private set; } = false;

        /// <summary>
        /// 当前正在播放的消息
        /// </summary>
        private static Message? _currentPlayingMessage;

        /// <summary>
        /// 将文本转换为语音并返回音频数据
        /// </summary>
        /// <param name="text">要转换的文本</param>
        /// <param name="cancellationToken">取消令牌</param>
        /// <returns>音频数据</returns>
        public static async Task<byte[]?> TextToSpeechAsync(string text, CancellationToken cancellationToken = default)
        {
            try
            {
                LogManager.Info($"开始将文本转换为语音: {text}");

                // 发送POST请求到语音接口
                using HttpClient client = new HttpClient();
                client.Timeout = TimeSpan.FromMinutes(8);

                // 直接发送文本内容，不使用JSON格式
                var content = new StringContent(text);
                string apiUrl = AddressConfigurationManager.GetFullAddress("/api/v1/ai/speak");
                HttpResponseMessage response = await client.PostAsync(apiUrl, content, cancellationToken);
                response.EnsureSuccessStatusCode();

                // 读取响应内容（语音数据）
                byte[] audioData = await response.Content.ReadAsByteArrayAsync(cancellationToken);
                LogManager.Info($"接收到语音数据，大小: {audioData.Length} 字节");

                return audioData;
            }
            catch (OperationCanceledException)
            {
                LogManager.Info("语音转换操作已取消");
                return null;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpeechServiceManager.TextToSpeechAsync");
                return null;
            }
        }

        /// <summary>
        /// 播放音频数据
        /// </summary>
        /// <param name="audioData">音频数据</param>
        /// <param name="message">关联的消息对象</param>
        /// <returns>是否成功</returns>
        public static async Task<bool> PlayAudioAsync(byte[] audioData, Message message)
        {
            try
            {
                // 停止当前播放
                StopPlayback();

                // 设置当前播放的消息
                _currentPlayingMessage = message;
                if (message != null)
                {
                    message.IsPlaying = true;
                }

                // 创建临时文件保存音频数据
                string tempFilePath = Path.Combine(Path.GetTempPath(), $"temp_audio_{Guid.NewGuid()}.wav");
                await File.WriteAllBytesAsync(tempFilePath, audioData);

                // 播放音频
                _mediaPlayer = new MediaPlayer();
                _mediaPlayer.MediaEnded += (sender, e) =>
                {
                    // 播放结束后清理临时文件
                    try
                    {
                        if (File.Exists(tempFilePath))
                        {
                            File.Delete(tempFilePath);
                        }
                        // 重置播放状态
                        if (_currentPlayingMessage != null)
                        {
                            _currentPlayingMessage.IsPlaying = false;
                        }
                        IsPlaying = false;
                    }
                    catch (Exception ex)
                    {
                        LogManager.WriteException(ex, "SpeechServiceManager.MediaEnded");
                    }
                };

                _mediaPlayer.Open(new Uri(tempFilePath));
                _mediaPlayer.Play();
                IsPlaying = true;
                LogManager.Info("开始播放语音");

                return true;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpeechServiceManager.PlayAudioAsync");
                if (message != null)
                {
                    message.IsPlaying = false;
                }
                return false;
            }
        }

        /// <summary>
        /// 播放音频数据（重载方法，用于向后兼容）
        /// </summary>
        /// <param name="audioData">音频数据</param>
        /// <returns>是否成功</returns>
        public static async Task<bool> PlayAudioAsync(byte[] audioData)
        {
            return await PlayAudioAsync(audioData, null);
        }

        /// <summary>
        /// 停止播放
        /// </summary>
        public static void StopPlayback()
        {
            try
            {
                if (_mediaPlayer != null)
                {
                    _mediaPlayer.Stop();
                    _mediaPlayer.Close();
                    IsPlaying = false;
                    LogManager.Info("停止播放语音");
                }

                // 重置当前播放消息的状态
                if (_currentPlayingMessage != null)
                {
                    _currentPlayingMessage.IsPlaying = false;
                    _currentPlayingMessage = null;
                }

                // 取消当前操作
                if (_cts != null)
                {
                    _cts.Cancel();
                    _cts.Dispose();
                    _cts = new CancellationTokenSource();
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpeechServiceManager.StopPlayback");
            }
        }

        /// <summary>
        /// 暂停播放
        /// </summary>
        public static void PausePlayback()
        {
            try
            {
                if (_mediaPlayer != null)
                {
                    _mediaPlayer.Pause();
                    IsPlaying = false;
                    LogManager.Info("暂停播放语音");
                }

                // 更新当前播放消息的状态
                if (_currentPlayingMessage != null)
                {
                    _currentPlayingMessage.IsPlaying = false;
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpeechServiceManager.PausePlayback");
            }
        }

        /// <summary>
        /// 恢复播放
        /// </summary>
        public static void ResumePlayback()
        {
            try
            {
                if (_mediaPlayer != null)
                {
                    _mediaPlayer.Play();
                    IsPlaying = true;
                    LogManager.Info("恢复播放语音");
                }

                // 更新当前播放消息的状态
                if (_currentPlayingMessage != null)
                {
                    _currentPlayingMessage.IsPlaying = true;
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpeechServiceManager.ResumePlayback");
            }
        }

        /// <summary>
        /// 取消当前的语音处理操作
        /// </summary>
        public static void CancelCurrentOperation()
        {
            try
            {
                if (_cts != null)
                {
                    _cts.Cancel();
                    _cts.Dispose();
                    _cts = null;
                    LogManager.Info("取消当前语音处理操作");
                }
                StopPlayback();
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "SpeechServiceManager.CancelCurrentOperation");
            }
        }

        /// <summary>
        /// 获取新的取消令牌源
        /// </summary>
        /// <returns>取消令牌源</returns>
        public static CancellationTokenSource GetNewCancellationTokenSource()
        {
            // 取消之前的操作
            if (_cts != null)
            {
                _cts.Cancel();
                _cts.Dispose();
            }
            _cts = new CancellationTokenSource();
            return _cts;
        }
    }
}