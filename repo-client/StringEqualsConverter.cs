using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Markup;

namespace EasyYachiyoClient
{
    /// <summary>
    /// 字符串相等比较转换器
    /// </summary>
    public class StringEqualsConverter : MarkupExtension, IValueConverter
    {
        /// <summary>
        /// 转换字符串值到布尔值
        /// </summary>
        /// <param name="value">字符串值</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">比较参数</param>
        /// <param name="culture">文化信息</param>
        /// <returns>布尔值</returns>
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is string stringValue && parameter is string parameterValue)
            {
                return stringValue.Equals(parameterValue);
            }
            return false;
        }

        /// <summary>
        /// 转换布尔值到字符串值
        /// </summary>
        /// <param name="value">布尔值</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">比较参数</param>
        /// <param name="culture">文化信息</param>
        /// <returns>字符串值</returns>
        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is bool boolValue && boolValue && parameter is string parameterValue)
            {
                return parameterValue;
            }
            return Binding.DoNothing;
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
