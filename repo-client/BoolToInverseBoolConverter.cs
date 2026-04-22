using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Markup;

namespace EasyYachiyoClient
{
    /// <summary>
    /// 布尔值取反转换器
    /// </summary>
    public class BoolToInverseBoolConverter : MarkupExtension, IValueConverter
    {
        /// <summary>
        /// 转换布尔值到相反的布尔值
        /// </summary>
        /// <param name="value">布尔值</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">文化信息</param>
        /// <returns>相反的布尔值</returns>
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is bool boolValue)
            {
                return !boolValue;
            }
            return true;
        }

        /// <summary>
        /// 转换相反的布尔值到原始布尔值
        /// </summary>
        /// <param name="value">相反的布尔值</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">文化信息</param>
        /// <returns>原始布尔值</returns>
        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is bool boolValue)
            {
                return !boolValue;
            }
            return false;
        }

        /// <summary>
        /// 提供值
        /// </summary>
        /// <param name="serviceProvider">服务提供者</param>
        /// <returns>转换器实例</returns>
        public override object ProvideValue(IServiceProvider serviceProvider)
        {
            return this;
        }
    }
}
