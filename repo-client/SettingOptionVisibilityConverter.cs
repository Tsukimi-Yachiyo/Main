using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows;
using System.Windows.Markup;

namespace EasyYachiyoClient
{
    /// <summary>
    /// 设置选项可见性转换器
    /// </summary>
    public class SettingOptionVisibilityConverter : MarkupExtension, IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            string selectedOption = value as string;
            string optionName = parameter as string;

            if (selectedOption == optionName)
            {
                return Visibility.Visible;
            }
            else
            {
                return Visibility.Collapsed;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }

        public override object ProvideValue(IServiceProvider serviceProvider)
        {
            return this;
        }
    }
}