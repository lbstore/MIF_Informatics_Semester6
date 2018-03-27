using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WorldCountries2.CountryInfo
{
    class Language
    {
        public string IsoCode { get; set; }
        public string LanguageName { get; set; }

        public string FormString()
        {
            return LanguageName + " (ISO: " + IsoCode + ")";
        }
    }
}
