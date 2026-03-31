using System;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// 地址配置管理类，用于管理网络请求的根地址
    /// </summary>
    public static class AddressConfigurationManager
    {
        /// <summary>
        /// 本地模式默认地址
        /// </summary>
        public static string LocalDefaultAddress => "127.0.0.1:8080";

        /// <summary>
        /// 远程模式默认地址
        /// </summary>
        public static string RemoteDefaultAddress => "adamantly-unappalled-bertie.ngrok-free.dev";

        /// <summary>
        /// 获取当前网络请求的根地址
        /// </summary>
        /// <returns>网络请求的根地址</returns>
        public static string GetBaseAddress()
        {
            try
            {
                // 加载用户设置
                UserSettings settings = SettingsManager.LoadSettings();

                // 根据运行模式确定根地址
                if (settings.ModelRunMode == "本地")
                {
                    // 本地模式使用本地默认地址
                    return LocalDefaultAddress;
                }
                else if (settings.ModelRunMode == "远程")
                {
                    // 远程模式下，优先使用自定义地址，如果没有设置则使用默认地址
                    if (!string.IsNullOrEmpty(settings.CustomRemoteAddress))
                    {
                        return settings.CustomRemoteAddress;
                    }
                    else
                    {
                        return RemoteDefaultAddress;
                    }
                }
                else
                {
                    // 未知模式，默认使用本地地址
                    return LocalDefaultAddress;
                }
            }
            catch (Exception ex)
            {
                // 发生异常时，默认使用本地地址
                LogManager.Error($"获取根地址时发生异常: {ex.Message}");
                return LocalDefaultAddress;
            }
        }

        /// <summary>
        /// 获取完整的请求地址
        /// </summary>
        /// <param name="relativePath">相对路径</param>
        /// <returns>完整的请求地址</returns>
        public static string GetFullAddress(string relativePath)
        {
            string baseAddress = GetBaseAddress();
            string fullAddress = baseAddress;

            // 确保基础地址以 http:// 或 https:// 开头
            if (!baseAddress.StartsWith("http://", StringComparison.OrdinalIgnoreCase) && 
                !baseAddress.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
            {
                fullAddress = "http://" + baseAddress;
            }

            // 确保基础地址不以 / 结尾
            if (fullAddress.EndsWith("/"))
            {
                fullAddress = fullAddress.Substring(0, fullAddress.Length - 1);
            }

            // 确保相对路径以 / 开头
            if (!string.IsNullOrEmpty(relativePath) && !relativePath.StartsWith("/"))
            {
                relativePath = "/" + relativePath;
            }

            return fullAddress + relativePath;
        }
    }
}