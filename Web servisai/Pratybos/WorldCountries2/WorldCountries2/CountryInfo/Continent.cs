using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WorldCountries2.CountryInfo
{
    public enum ContinentCodes
    {
        AF, AN, AS, EU, NA, OC, SA, AM
    }

    public class Continent
    {
        public ContinentCodes Code { get; private set; }
        public string NameEglish { get; private set; }
        public string LocalName { get; set; }

        public static readonly Continent Asia = new Continent() { Code = ContinentCodes.AS, NameEglish = "Asia" };
        public static readonly Continent Africa = new Continent() { Code = ContinentCodes.AF, NameEglish = "Africa" };
        public static readonly Continent Antarctica = new Continent() { Code = ContinentCodes.AN, NameEglish = "Antarctica" };
        public static readonly Continent Europe = new Continent() { Code = ContinentCodes.EU, NameEglish = "Europe" };
        public static readonly Continent NorthAmerica = new Continent() { Code = ContinentCodes.NA, NameEglish = "North America" };
        public static readonly Continent Oceania = new Continent() { Code = ContinentCodes.OC, NameEglish = "Oceania" };
        public static readonly Continent SouthAmerica = new Continent() { Code = ContinentCodes.SA, NameEglish = "SouthAmerica" };
        public static readonly Continent Americas = new Continent() { Code = ContinentCodes.SA, NameEglish = "America" };


        public static Continent GetContinent(ContinentCodes code)
        {
            switch (code)
            {
                case ContinentCodes.AM:
                    return Americas;
                    
                case ContinentCodes.AF:
                    return Africa;
                case ContinentCodes.AN:
                    return Antarctica;
                case ContinentCodes.AS:
                    return Asia;
                case ContinentCodes.EU:
                    return Europe;
                case ContinentCodes.NA:
                    return NorthAmerica;
                case ContinentCodes.OC:
                    return Oceania;
                case ContinentCodes.SA:
                    return SouthAmerica;
                default:
                    throw new ArgumentException("Could not get continent name by given argument", code.ToString());
            }


        }

        public static Continent GetContinent(string code)
        {
            ContinentCodes contCode;
            Enum.TryParse<ContinentCodes>(code, true, out contCode);
            return GetContinent(contCode);
        }

        public override string ToString()
        {
            var nameEglish = this.NameEglish;
            if (nameEglish != null) return nameEglish;
            else return "";
        }
    }
}
