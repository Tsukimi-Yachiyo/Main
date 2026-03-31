using System;
using System.Collections.Generic;
using System.IO;
using System.Windows.Media.Imaging;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// 图片加载辅助类，用于处理图片加载、错误处理和缓存
    /// </summary>
    public static class ImageLoader
    {
        /// <summary>
        /// 图片缓存
        /// </summary>
        private static readonly Dictionary<string, BitmapImage> _imageCache = new Dictionary<string, BitmapImage>();

        /// <summary>
        /// 加载图片
        /// </summary>
        /// <param name="relativePath">图片相对路径</param>
        /// <returns>加载的图片</returns>
        public static BitmapImage LoadImage(string relativePath)
        {
            try
            {
                // 检查缓存中是否已有该图片
                if (_imageCache.TryGetValue(relativePath, out BitmapImage cachedImage))
                {
                    return cachedImage;
                }

                // 构建绝对路径
                string basePath = AppDomain.CurrentDomain.BaseDirectory;
                string absolutePath = Path.Combine(basePath, relativePath);

                // 检查文件是否存在
                if (!File.Exists(absolutePath))
                {
                    // 尝试从项目目录加载
                    string projectPath = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
                    string projectImagePath = Path.Combine(projectPath, relativePath);
                    if (!File.Exists(projectImagePath))
                    {
                        // 返回默认图片或错误图片
                        return GetDefaultImage();
                    }
                    absolutePath = projectImagePath;
                }

                // 加载图片
                BitmapImage bitmapImage = new BitmapImage();
                bitmapImage.BeginInit();
                bitmapImage.UriSource = new Uri(absolutePath);
                bitmapImage.CacheOption = BitmapCacheOption.OnLoad;
                bitmapImage.EndInit();
                bitmapImage.Freeze(); // 冻结图片以提高性能

                // 存入缓存
                _imageCache[relativePath] = bitmapImage;

                return bitmapImage;
            }
            catch (Exception ex)
            {
                // 记录错误
                Console.WriteLine($"图片加载错误: {ex.Message}");
                return GetDefaultImage();
            }
        }

        /// <summary>
        /// 获取默认图片
        /// </summary>
        /// <returns>默认图片</returns>
        private static BitmapImage GetDefaultImage()
        {
            BitmapImage defaultImage = new BitmapImage();
            defaultImage.BeginInit();
            defaultImage.UriSource = new Uri("pack://application:,,,/EasyYachiyoClient;component/resource/window_title_icon.jpg");
            defaultImage.CacheOption = BitmapCacheOption.OnLoad;
            defaultImage.EndInit();
            defaultImage.Freeze();
            return defaultImage;
        }

        /// <summary>
        /// 清理图片缓存
        /// </summary>
        public static void ClearCache()
        {
            _imageCache.Clear();
        }

        /// <summary>
        /// 验证图片文件
        /// </summary>
        /// <param name="path">图片路径</param>
        /// <returns>是否为有效的图片文件</returns>
        public static bool ValidateImageFile(string path)
        {
            try
            {
                if (!File.Exists(path))
                {
                    return false;
                }

                // 检查文件扩展名
                string extension = Path.GetExtension(path).ToLower();
                string[] validExtensions = { ".jpg", ".jpeg", ".png", ".gif", ".bmp" };
                bool hasValidExtension = false;
                foreach (string ext in validExtensions)
                {
                    if (extension == ext)
                    {
                        hasValidExtension = true;
                        break;
                    }
                }

                if (!hasValidExtension)
                {
                    return false;
                }

                // 尝试打开文件验证格式
                using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
                {
                    BitmapImage bitmap = new BitmapImage();
                    bitmap.BeginInit();
                    bitmap.StreamSource = fs;
                    bitmap.CacheOption = BitmapCacheOption.OnLoad;
                    bitmap.EndInit();
                    return true;
                }
            }
            catch
            {
                return false;
            }
        }
    }
}
