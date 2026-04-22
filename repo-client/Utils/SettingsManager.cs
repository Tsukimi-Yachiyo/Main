using System;
using System.IO;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// 用户设置数据模型
    /// </summary>
    public class UserSettings
    {
        /// <summary>
        /// 模型运行方式：本地或远程
        /// </summary>
        [JsonPropertyName("modelRunMode")]
        public string ModelRunMode { get; set; } = "本地";

        /// <summary>
        /// 模型URL地址
        /// </summary>
        [JsonPropertyName("modelUrl")]
        public string ModelUrl { get; set; } = "默认";

        /// <summary>
        /// 语言设置：中文或English
        /// </summary>
        [JsonPropertyName("language")]
        public string Language { get; set; } = "中文";

        /// <summary>
        /// 启动时自动打开上次对话
        /// </summary>
        [JsonPropertyName("autoOpenLastConversation")]
        public bool AutoOpenLastConversation { get; set; } = false;

        /// <summary>
        /// 启用日志记录
        /// </summary>
        [JsonPropertyName("enableLogging")]
        public bool EnableLogging { get; set; } = false;

        /// <summary>
        /// 生产模式
        /// </summary>
        [JsonPropertyName("productionMode")]
        public bool ProductionMode { get; set; } = true;

        /// <summary>
        /// 自定义远程地址
        /// </summary>
        [JsonPropertyName("customRemoteAddress")]
        public string CustomRemoteAddress { get; set; } = string.Empty;
    }

    /// <summary>
    /// 设置管理类，负责用户设置的读取、保存和更新
    /// </summary>
    public static class SettingsManager
    {
        /// <summary>
        /// 设置文件路径
        /// </summary>
        private static string SettingsFilePath => Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "setting.json");

        /// <summary>
        /// 读取用户设置
        /// </summary>
        /// <returns>用户设置对象</returns>
        public static UserSettings LoadSettings()
        {
            try
            {
                if (File.Exists(SettingsFilePath))
                {
                    string jsonContent = File.ReadAllText(SettingsFilePath);
                    return JsonSerializer.Deserialize<UserSettings>(jsonContent) ?? new UserSettings();
                }
            }
            catch (Exception ex)
            {
                ApplicationStateManager.LogException(ex, "SettingsManager.LoadSettings");
            }

            // 如果文件不存在或读取失败，返回默认设置
            return new UserSettings();
        }

        /// <summary>
        /// 保存用户设置
        /// </summary>
        /// <param name="settings">用户设置对象</param>
        /// <returns>是否保存成功</returns>
        public static bool SaveSettings(UserSettings settings)
        {
            try
            {
                // 清理字符串值
                if (settings.ModelRunMode.Contains("本地"))
                {
                    settings.ModelRunMode = "本地";
                }
                else if (settings.ModelRunMode.Contains("远程"))
                {
                    settings.ModelRunMode = "远程";
                }
                
                // 使用临时文件确保原子性
                string tempFilePath = SettingsFilePath + ".tmp";
                string jsonContent = JsonSerializer.Serialize(settings, new JsonSerializerOptions
                {
                    WriteIndented = true,
                    Encoder = System.Text.Encodings.Web.JavaScriptEncoder.UnsafeRelaxedJsonEscaping
                });

                // 写入临时文件
                File.WriteAllText(tempFilePath, jsonContent, System.Text.Encoding.UTF8);

                // 替换原文件
                if (File.Exists(SettingsFilePath))
                {
                    File.Delete(SettingsFilePath);
                }
                File.Move(tempFilePath, SettingsFilePath);

                return true;
            }
            catch (Exception ex)
            {
                ApplicationStateManager.LogException(ex, "SettingsManager.SaveSettings");
                return false;
            }
        }

        /// <summary>
        /// 更新用户设置
        /// </summary>
        /// <param name="updateAction">更新操作</param>
        /// <returns>是否更新成功</returns>
        public static bool UpdateSettings(Action<UserSettings> updateAction)
        {
            try
            {
                UserSettings settings = LoadSettings();
                updateAction(settings);
                return SaveSettings(settings);
            }
            catch (Exception ex)
            {
                ApplicationStateManager.LogException(ex, "SettingsManager.UpdateSettings");
                return false;
            }
        }

        /// <summary>
        /// 获取设置文件是否存在
        /// </summary>
        /// <returns>设置文件是否存在</returns>
        public static bool SettingsFileExists()
        {
            return File.Exists(SettingsFilePath);
        }
    }
}
