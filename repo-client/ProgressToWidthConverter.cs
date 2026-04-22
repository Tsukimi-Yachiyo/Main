using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Markup;

namespace EasyYachiyoClient
{
    /// <summary>
    /// 进度值到宽度转换器
    /// </summary>
    public class ProgressToWidthConverter : MarkupExtension, IMultiValueConverter
    {
        /// <summary>
        /// 转换进度值到宽度
        /// </summary>
        /// <param name="values">值数组，包含进度值和父容器宽度</param>
        /// <param name="targetType">目标类型</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">文化信息</param>
        /// <returns>宽度</returns>
        public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture)
        {
            if (values.Length >= 2 && values[0] is double progressValue && values[1] is double actualWidth)
            {
                // 计算宽度：进度值（0-100）转换为实际宽度的百分比
                double width = (progressValue / 100) * actualWidth;
                return width;
            }
            return 0.0;
        }

        /// <summary>
        /// 转换宽度到进度值
        /// </summary>
        /// <param name="value">宽度</param>
        /// <param name="targetTypes">目标类型数组</param>
        /// <param name="parameter">参数</param>
        /// <param name="culture">文化信息</param>
        /// <returns>进度值数组</returns>
        public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
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
