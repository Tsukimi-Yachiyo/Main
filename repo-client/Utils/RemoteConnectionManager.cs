using System;
using System.Net.Http;
using System.Threading.Tasks;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// 远程连接管理类，负责测试远程连接的可用性
    /// </summary>
    public static class RemoteConnectionManager
    {
        /// <summary>
        /// 测试远程连接的可用性
        /// </summary>
        /// <param name="progressUpdate">进度更新回调</param>
        /// <returns>连接是否成功</returns>
        public static async Task<bool> TestRemoteConnectionAsync(Action<int, string> progressUpdate = null)
        {
            try
            {
                progressUpdate?.Invoke(0, "准备测试远程连接...");
                LogManager.Info("开始测试远程连接");
                
                // 使用 AddressConfigurationManager 获取远程地址
                string remoteAddress = AddressConfigurationManager.GetBaseAddress();
                LogManager.Info($"测试地址: {remoteAddress}");

                using HttpClient client = new HttpClient
                {
                    Timeout = TimeSpan.FromMinutes(8)
                };

                string testUrl = AddressConfigurationManager.GetFullAddress("/api/v3/test/hello");
                progressUpdate?.Invoke(50, "正在测试远程连接...");

                HttpResponseMessage response = await client.GetAsync(testUrl);
                bool success = false;

                if (response.IsSuccessStatusCode)
                {
                    // 读取响应内容
                    string responseContent = await response.Content.ReadAsStringAsync();
                    success = responseContent.Trim() == "Hello World!";

                    if (success)
                    {
                        LogManager.Info("远程连接测试成功");
                        LogManager.Info($"响应状态码: {response.StatusCode}");
                        LogManager.Info($"响应内容: {responseContent}");
                        progressUpdate?.Invoke(100, "远程连接测试成功");
                    }
                    else
                    {
                        LogManager.Warn($"远程连接测试失败: 响应内容不符合预期");
                        LogManager.Info($"响应状态码: {response.StatusCode}");
                        LogManager.Info($"响应内容: {responseContent}");
                        progressUpdate?.Invoke(100, "远程连接测试失败: 响应内容不符合预期");
                    }
                }
                else
                {
                    LogManager.Warn($"远程连接测试失败: {response.StatusCode}");
                    progressUpdate?.Invoke(100, $"远程连接测试失败: {response.StatusCode}");
                }

                return success;
            }
            catch (TaskCanceledException ex)
            {
                LogManager.Error("远程连接测试超时");
                LogManager.WriteException(ex, "RemoteConnectionManager.TestRemoteConnectionAsync - 超时");
                progressUpdate?.Invoke(100, "远程连接测试超时");
                return false;
            }
            catch (Exception ex)
            {
                LogManager.Error($"远程连接测试异常: {ex.Message}");
                LogManager.WriteException(ex, "RemoteConnectionManager.TestRemoteConnectionAsync");
                progressUpdate?.Invoke(100, $"远程连接测试异常: {ex.Message}");
                return false;
            }
        }
    }
}