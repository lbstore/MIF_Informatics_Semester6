using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WorldCountries2.com.webservicex.www;
namespace WorldCountries2
{
    class Settings
    {
        /// <summary>
        /// Indicates to which currencys selected national currency should be converted
        /// </summary>
        public static Dictionary<Currency, bool> currencyConversion { get; private set; }


        static Settings()
        {
           currencyConversion = new Dictionary<Currency, bool>();

           var values = Enum.GetValues(typeof(Currency)).Cast<Currency>();
           foreach (var curr in values)
           {               
               currencyConversion.Add(curr,false);
           }
           selectDefaultCurrencys();
        }

        static void selectDefaultCurrencys()
        {
            currencyConversion[Currency.USD] = true;
            currencyConversion[Currency.LTL] = true;
            currencyConversion[Currency.GBP] = true;
            currencyConversion[Currency.EUR] = true;           
        }
        
        


    }
}
