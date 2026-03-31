using System;
using System.Diagnostics;
using System.IO;
using System.Net.Http;
using System.Threading.Tasks;
using System.Windows;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// Ollama环境管理类，负责Ollama环境的检测和管理
    /// </summary>
    public static class OllamaEnvironmentManager
    {
        /// <summary>
        /// Ollama安装程序下载地址
        /// </summary>
        private const string OllamaInstallerUrl = "https://ollama.com/download/OllamaSetup.exe";

        /// <summary>
        /// 目标模型名称
        /// </summary>
        private const string TargetModelName = "1473443474/tsukimi-yachiyo";

        /// <summary>
        /// 进度更新委托
        /// </summary>
        /// <param name="progress">进度百分比（0-100）</param>
        /// <param name="status">当前状态描述</param>
        public delegate void ProgressUpdateHandler(int progress, string status);

        /// <summary>
        /// 检测Ollama环境是否已安装
        /// </summary>
        /// <returns>Ollama是否已安装</returns>
        public static bool IsOllamaInstalled()
        {
            try
            {
                using Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = "/c ollama -v",
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        UseShellExecute = false,
                        CreateNoWindow = true
                    }
                };

                process.Start();
                string output = process.StandardOutput.ReadToEnd();
                string error = process.StandardError.ReadToEnd();
                process.WaitForExit();

                return process.ExitCode == 0 && !string.IsNullOrEmpty(output);
            }
            catch (Exception ex)
            {
                ApplicationStateManager.LogException(ex, "OllamaEnvironmentManager.IsOllamaInstalled");
                return false;
            }
        }

        /// <summary>
        /// 检测目标模型是否存在
        /// </summary>
        /// <returns>模型是否存在</returns>
        public static bool IsTargetModelAvailable()
        {
            try
            {
                using Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = $"/c ollama show {TargetModelName}",
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        UseShellExecute = false,
                        CreateNoWindow = true
                    }
                };

                process.Start();
                string output = process.StandardOutput.ReadToEnd();
                string error = process.StandardError.ReadToEnd();
                process.WaitForExit();

                return process.ExitCode == 0 && !string.IsNullOrEmpty(output);
            }
            catch (Exception ex)
            {
                ApplicationStateManager.LogException(ex, "OllamaEnvironmentManager.IsTargetModelAvailable");
                return false;
            }
        }

        /// <summary>
        /// 下载Ollama安装程序
        /// </summary>
        /// <param name="progressUpdate">进度更新回调</param>
        /// <returns>下载是否成功</returns>
        public static async Task<bool> DownloadOllamaInstallerAsync(ProgressUpdateHandler progressUpdate)
        {
            try
            {
                string tempFilePath = Path.Combine(Path.GetTempPath(), "OllamaSetup.exe");
                
                using HttpClient client = new HttpClient();
                using HttpResponseMessage response = await client.GetAsync(OllamaInstallerUrl, HttpCompletionOption.ResponseHeadersRead);
                response.EnsureSuccessStatusCode();

                long totalBytes = response.Content.Headers.ContentLength ?? 0;
                long bytesRead = 0;

                using Stream contentStream = await response.Content.ReadAsStreamAsync();
                using FileStream fileStream = new FileStream(tempFilePath, FileMode.Create, FileAccess.Write, FileShare.None);

                byte[] buffer = new byte[8192];
                int bytesReadInBuffer;

                while ((bytesReadInBuffer = await contentStream.ReadAsync(buffer)) > 0)
                {
                    await fileStream.WriteAsync(buffer, 0, bytesReadInBuffer);
                    bytesRead += bytesReadInBuffer;

                    if (totalBytes > 0)
                    {
                        int progress = (int)((bytesRead * 100) / totalBytes);
                        progressUpdate(progress, $"正在下载Ollama安装程序: {progress}%");
                    }
                    else
                    {
                        progressUpdate(0, "正在下载Ollama安装程序...");
                    }
                }

                // 启动安装程序
                Process.Start(tempFilePath);
                return true;
            }
            catch (Exception ex)
            {
                ApplicationStateManager.LogException(ex, "OllamaEnvironmentManager.DownloadOllamaInstallerAsync");
                return false;
            }
        }

        /// <summary>
        /// 拉取目标模型
        /// </summary>
        /// <param name="progressUpdate">进度更新回调</param>
        /// <returns>拉取是否成功</returns>
        public static async Task<bool> PullTargetModelAsync(ProgressUpdateHandler progressUpdate)
        {
            try
            {
                // 显示cmd窗口执行拉取命令
                using Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = $"/c ollama pull {TargetModelName}",
                        UseShellExecute = true,
                        CreateNoWindow = false,
                        WindowStyle = ProcessWindowStyle.Normal,
                        WorkingDirectory = Environment.CurrentDirectory
                    }
                };

                progressUpdate(0, "正在启动模型拉取...");
                
                // 启动进程并等待完成
                process.Start();
                await Task.Run(() => process.WaitForExit());
                
                progressUpdate(100, "模型拉取完成");
                
                return process.ExitCode == 0;
            }
            catch (Exception ex)
            {
                ApplicationStateManager.LogException(ex, "OllamaEnvironmentManager.PullTargetModelAsync");
                progressUpdate(0, $"拉取模型时发生错误: {ex.Message}");
                return false;
            }
        }

        /// <summary>
        /// 执行环境检测并提供补全选项
        /// </summary>
        /// <param name="progressUpdate">进度更新回调</param>
        /// <returns>环境是否就绪</returns>
        public static async Task<bool> CheckAndPrepareEnvironmentAsync(ProgressUpdateHandler progressUpdate)
        {
            progressUpdate(0, "正在检测Ollama环境...");

            // 检测Ollama环境
            bool isOllamaInstalled = IsOllamaInstalled();
            if (!isOllamaInstalled)
            {
                progressUpdate(0, "Ollama环境未检测到");
                
                // 询问用户是否安装
                MessageBoxResult result = MessageBox.Show(
                    "未检测到Ollama环境，是否需要自动下载并安装？",
                    "环境检测",
                    MessageBoxButton.YesNo,
                    MessageBoxImage.Question);

                if (result == MessageBoxResult.Yes)
                {
                    bool downloadSuccess = await DownloadOllamaInstallerAsync(progressUpdate);
                    if (!downloadSuccess)
                    {
                        MessageBox.Show("Ollama安装程序下载失败，请手动安装。", "错误", MessageBoxButton.OK, MessageBoxImage.Error);
                        return false;
                    }

                    // 安装完成后需要用户手动启动Ollama
                    MessageBox.Show("Ollama安装程序已下载并启动，请完成安装后重新运行检测。", "提示", MessageBoxButton.OK, MessageBoxImage.Information);
                    return false;
                }
                else
                {
                    return false;
                }
            }

            progressUpdate(50, "Ollama环境已检测到，正在检测模型...");

            // 检测模型
            bool isModelAvailable = IsTargetModelAvailable();
            if (!isModelAvailable)
            {
                progressUpdate(50, "目标模型未检测到");
                
                // 询问用户是否拉取
                MessageBoxResult result = MessageBox.Show(
                    "未检测到目标模型，是否需要自动拉取？",
                    "模型检测",
                    MessageBoxButton.YesNo,
                    MessageBoxImage.Question);

                if (result == MessageBoxResult.Yes)
                {
                    bool pullSuccess = await PullTargetModelAsync(progressUpdate);
                    if (!pullSuccess)
                    {
                        MessageBox.Show("模型拉取失败，请手动拉取。", "错误", MessageBoxButton.OK, MessageBoxImage.Error);
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }

            progressUpdate(100, "环境检测完成，所有依赖项已就绪");
            return true;
        }
    }
}
