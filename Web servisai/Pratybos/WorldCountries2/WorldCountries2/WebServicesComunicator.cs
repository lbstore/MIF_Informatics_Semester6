using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using WorldCountries2.org.oorsprong.www;
//using WorldCountries2.CurrencyConvertorService;
using WorldCountries2.com.webservicex.www;
using System.Diagnostics;
using WorldCountries2.Properties;
using WorldCountries2.CountryInformationService;

namespace WorldCountries2
{
    class WebServicesCommunicator
    {
        private CountryInfoService _countryInfoService;
        private CurrencyConvertor _currencyClient;
        private CountryInformationServiceSoapClient _countryInformationService;

        /// <summary>
        /// 
        /// </summary>
        /// <exception cref="WebException">Throws when there is no internet connection</exception>
        /// <param name="from"></param>
        /// <param name="to"></param>
        /// <returns></returns>
        public double GetCurrencyRate(string from, string to)
        {

            try
            {
                _currencyClient = new CurrencyConvertor();
                

                Currency fromEnum;
                Currency toEnum;
                if (!Enum.TryParse<Currency>(from, out fromEnum))
                {
                    throw new ArgumentException(Resources.Exception_Could_not_indetify_currency_code, "from");
                }
                if (!Enum.TryParse<Currency>(to, out toEnum))
                {
                    throw new ArgumentException(Resources.Exception_Could_not_indetify_currency_code, "to");
                }
                return _currencyClient.ConversionRate(fromEnum, toEnum);
            }
            catch (ArgumentException e)
            {
                return -1;
            }
            catch (WebException e)
            {
                Debug.WriteLine(e.Message);
               // throw new WebException("No internet connection", e);
                MessageBox.Show("Could not connect to web service, please try again later","Internet connection (Currency rate");
                return -1;
            }

        }

        /// <summary>
        /// 
        /// </summary>
        /// <exception cref="WebException">Throws when there is no internet connection</exception>
        /// <param name="isoCode"></param>
        /// <returns></returns>
        public tCountryInfo GetFullCoutryInfo(string isoCode)
        {

            try
            {
                if (_countryInfoService == null)
                    _countryInfoService = new CountryInfoService();
                var info = _countryInfoService.FullCountryInfo(isoCode);
                //info.sCapitalCity;
                //info.Languages;
                //info.sCountryFlag;
                //info.sCurrencyISOCode;
                //info.sPhoneCode;
                //info.sContinentCode;
                //info.sName;
                return info;
            }
            catch (WebException e)
            {
                Debug.WriteLine(e.Message);
                //throw new WebException("No internet connection", e);
                MessageBox.Show("Could not connect to web service, please try again later", "Internet connection (Country Info)");
                return null;
            }


        }

        public string GetCurrencyName(string isoCurrencyCode)
        {
            try
            {
                if (_countryInfoService == null)
                    _countryInfoService = new CountryInfoService();
                var name = _countryInfoService.CurrencyName(isoCurrencyCode);
                return name;
            }
            catch (WebException e)
            {
                Debug.WriteLine(e.Message);
                //throw new WebException("No internet connection", e);
                MessageBox.Show("Could not connect to web service, please try again later", "Internet connection (Currency name)");
                return null;
            }
        }

        public int GetPopulation(string countryIsoCode)
        {
            try
            {
                if (_countryInformationService == null)
                    _countryInformationService = new CountryInformationServiceSoapClient("CountryInformationServiceSoap12");

              
                
                var countryName = _countryInformationService.GetCountryByTwoLetterISOCode(countryIsoCode);
                var population = _countryInformationService.GetPopulationByCountry(countryName);
                int popInt;
                try
                {
                    popInt = int.Parse(population);
                }
                catch (FormatException)
                {
                    popInt = -1;
                }
                return popInt;
            }
            catch (WebException e)
            {
                Debug.WriteLine(e.Message);
                //throw new WebException("No internet connection", e);
                MessageBox.Show("Could not connect to web service, please try again later", "Internet connection (Population)");
                return -1;
            }
        }
    }
}
