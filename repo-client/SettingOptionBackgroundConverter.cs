using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Markup;
using System.Windows.Media;

namespace EasyYachiyoClient
{
    /// <summary>
    /// 设置选项背景转换器
    /// </summary>
    public class SettingOptionBackgroundConverter : MarkupExtension, IMultiValueConverter
    {
        public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture)
        {
            if (values.Length >= 2)
            {
                string selectedOption = values[0] as string;
                string optionName = values[1] as string;

                if (selectedOption == optionName)
                {
                    return new SolidColorBrush((Color)ColorConverter.ConvertFromString("#ADD8E6")); // 蓝色背景
                }
                else
                {
                    return new SolidColorBrush((Color)ColorConverter.ConvertFromString("#F0F0F0")); // 默认背景
                }
            }
            else
            {
                return new SolidColorBrush((Color)ColorConverter.ConvertFromString("#F0F0F0")); // 默认背景
            }
        }

        public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }

        public override object ProvideValue(IServiceProvider serviceProvider)
        {
            return this;
        }
    }
}