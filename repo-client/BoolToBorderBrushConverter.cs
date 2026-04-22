using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Media;

namespace EasyYachiyoClient
{
    /// <summary>
    /// 布尔值到边框颜色的转换器
    /// </summary>
    public class BoolToBorderBrushConverter : IValueConverter
    {
        /// <summary>
        /// 转换值
        /// </summary>
        /// <param name="value">源值</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">文化</param>
        /// <returns>转换后的值</returns>
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is bool isUser)
            {
                return isUser ? new SolidColorBrush(Color.FromRgb(67, 160, 71)) : new SolidColorBrush(Color.FromRgb(224, 224, 224));
            }
            return new SolidColorBrush(Color.FromRgb(224, 224, 224));
        }

        /// <summary>
        /// 转换回值
        /// </summary>
        /// <param name="value">源值</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">文化</param>
        /// <returns>转换后的值</returns>
        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}