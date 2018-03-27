using System;
using System.Globalization;
using System.Windows.Data;
using RealAndVirtualMachine.Memory;
using RealAndVirtualMachine.Memory.Pages;


namespace RealAndVirtualMachine.GUI
{

    class PageReprensentation
    {

        public class MemoryEntry
        {
            public int? VirtualAddress { get; set; }
            public int? RealAddress { get; set; }
            public Word Value { get; set; }
        }
    }

    
    [ValueConversion(typeof(VirtualPage), typeof(VirtualPage))]
    class PagesViewConverter : IValueConverter
    {
         public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
         {
            //TODO: create new class and return it   
             return value;
         }

         public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
         {
             throw new NotImplementedException();
         }
    }
}
