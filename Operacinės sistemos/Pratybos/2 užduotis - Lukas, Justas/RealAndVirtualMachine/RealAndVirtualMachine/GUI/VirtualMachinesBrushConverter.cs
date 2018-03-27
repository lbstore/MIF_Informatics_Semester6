using System;
using System.Globalization;
using System.Windows.Data;
using System.Windows.Media;
using RealAndVirtualMachine.Machines;
using RealAndVirtualMachine.Memory.Pages;

namespace RealAndVirtualMachine.GUI
{






    [ValueConversion(typeof(VirtualMachine), typeof(SolidColorBrush))]
    class VirtualMachinesBrushConverter : IValueConverter
    {

        public static readonly SolidColorBrush[] SolidColorBrushes =
        {
            new SolidColorBrush(Color.FromRgb(127,222,229)),
            new SolidColorBrush(Color.FromRgb(127,229,192)),
            new SolidColorBrush(Color.FromRgb(180,127,229)),
            new SolidColorBrush(Color.FromRgb(219,194,147)),
            new SolidColorBrush(Color.FromRgb(230,230,154)),
            new SolidColorBrush(Color.FromRgb(48,205,230)),
 
        };

        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var page = value as VirtualMachine;
            if (page == null)
            {
                return null;
            }
            var colorNR = page.PID % SolidColorBrushes.Length;

            return SolidColorBrushes[colorNR];
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

}
