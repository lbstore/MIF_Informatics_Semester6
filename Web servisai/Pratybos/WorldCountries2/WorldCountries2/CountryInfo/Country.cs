using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Cache;
using System.Security.Cryptography;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media.Imaging;
using WorldCountries2.com.webservicex.www;
using WorldCountries2.Properties;


namespace WorldCountries2.CountryInfo
{
    class InfoUpdateEventArgs
    {
        public enum InfoParts { BaseInfo, FlagImage, CurrencyConversion, CurrencyName, Population };

        public InfoUpdateEventArgs(InfoParts infoPart)
        {
            InfoPart = infoPart;
        }

        public InfoUpdateEventArgs()
        {
            // TODO: Complete member initialization
        }

        public InfoParts InfoPart { get; set; }
    }


    class Country
    {

        public Country(string isoCode2, string name)
        {
            if (isoCode2 == null)
            {
                throw new ArgumentNullException("isoCode2", Resources.Exception_iso_code_can_not_be_null);
            }
            IsoCode2Letters = isoCode2;
            Name = name;
            Languages = new List<Language>();
            CurrencyRates = new Dictionary<Currency, double>();
        }

        private readonly WebServicesCommunicator _webService = new WebServicesCommunicator();
        public BitmapImage Flag { get; set; }
        public string Capital { get; set; }
        public List<Language> Languages { get; private set; }
        public int Population { get; set; }
        public int PhoneCode { get; set; }
        public Continent Continent { get; set; }
        public string CurrencyCode { get; set; }
        public string CurrencyName { get; set; }
        public Dictionary<Currency, double> CurrencyRates { get; private set; }
        public string Name { get; set; }

        public delegate void CountryInfoUpdateHandler(object sender, InfoUpdateEventArgs eventArgs);

        public event CountryInfoUpdateHandler OnCountryInfoUpdated;



        private string _flagUrl;

        public string IsoCode2Letters { get; set; }

        public string IsoCode3Letters { get; set; }

        public void AddLanguage(Language lang)
        {
            Languages.Add(lang);
        }

        public String FormLanguagesAsString()
        {
            var langString = new StringBuilder();
            bool isFirst = true;
            foreach (var language in Languages)
            {
                if (!isFirst)
                {
                    langString.Append("\n");
                }
                else
                {
                    isFirst = false;
                }
                langString.Append(language.FormString());
            }
            return langString.ToString();
        }

        public void AddCurrencyConversion(Currency fromCountry, double value)
        {
            CurrencyRates.Add(fromCountry, value);
        }


        public string FormCurrencyDescription()
        {

            string description;
            if (!string.IsNullOrWhiteSpace(CurrencyName) && string.IsNullOrWhiteSpace(CurrencyCode))
            {
                return CurrencyName;
            }
            if (string.IsNullOrWhiteSpace(CurrencyName) && !string.IsNullOrWhiteSpace(CurrencyCode))
            {
                return CurrencyCode;
            }
            if (!string.IsNullOrWhiteSpace(CurrencyName) && !string.IsNullOrWhiteSpace(CurrencyCode))
            {
                return CurrencyName + " ("+CurrencyCode +")";
            }    
            return null;
        }

        public string FormCurrenciesAsString()
        {

            var currString = new StringBuilder();
            //currString.AppendLine(this.CurrencyCode);
            bool isFirst = true;
            foreach (var currency in CurrencyRates)
            {
                var fromCountry = currency.Key;
                var value = currency.Value;
                if (!isFirst)
                {
                    currString.Append("\n");
                }
                else
                {
                    isFirst = false;
                }
                currString.Append(value + " " + fromCountry);
            }
            return currString.ToString();
        }



        private void UpdateBaseInfo()
        {
            if (this.Capital == null || this.Continent == null || this.CurrencyCode == null ||
                this._flagUrl == null || this.Languages == null || this.PhoneCode == 0 || this.Name == null)
            {
                var countryInfo = _webService.GetFullCoutryInfo(this.IsoCode2Letters);

                this.Name = countryInfo.sName;
                this.Capital = countryInfo.sCapitalCity;
                this.Continent = Continent.GetContinent(countryInfo.sContinentCode);



                this.CurrencyCode = countryInfo.sCurrencyISOCode;
                this._flagUrl = countryInfo.sCountryFlag;
                try
                {
                    this.PhoneCode = int.Parse(countryInfo.sPhoneCode);
                }
                catch (FormatException)
                {
                    this.PhoneCode = 0;
                }

                if (this.Languages != null)
                    Languages.Clear();

                this.Languages = new List<Language>();
                foreach (var lang in countryInfo.Languages)
                {
                    this.Languages.Add(new Language { IsoCode = lang.sISOCode, LanguageName = lang.sName });
                }

                //notify listeners (if there is any) that base info has changed
                if (OnCountryInfoUpdated != null)
                {
                    OnCountryInfoUpdated(this, new InfoUpdateEventArgs(InfoUpdateEventArgs.InfoParts.BaseInfo));
                }

            }
        }


        private void UpdateFlagImage()
        {
            if (this.Flag == null && !string.IsNullOrWhiteSpace(this._flagUrl))
            {
                BackgroundWorker worker = new BackgroundWorker();
                worker.DoWork += (s, e) =>
                {
                    var uriSource = new Uri(this._flagUrl, UriKind.Absolute);
                    using (WebClient webClient = new WebClient())
                    {
                        webClient.CachePolicy = new RequestCachePolicy(RequestCacheLevel.Default);
                        try
                        {
                            byte[] imageBytes = null;

                            imageBytes = webClient.DownloadData(uriSource);
                            if (imageBytes == null)
                            {
                                e.Result = null;
                                return;
                            }
                            MemoryStream imageStream = new MemoryStream(imageBytes);
                            BitmapImage image = new BitmapImage();

                            image.BeginInit();
                            image.StreamSource = imageStream;
                            image.CacheOption = BitmapCacheOption.OnLoad;
                            image.EndInit();

                            image.Freeze();
                            imageStream.Close();
                            e.Result = image;
                        }
                        catch (WebException exception)
                        {
                            e.Result = null;
                        }
                    }
                };
                worker.RunWorkerCompleted += (s, e) =>
                {
                    BitmapImage bitmapImage = e.Result as BitmapImage;
                    if (bitmapImage != null)
                    {
                        Flag = bitmapImage;
                        if (OnCountryInfoUpdated != null)
                        {
                            OnCountryInfoUpdated(this, new InfoUpdateEventArgs(InfoUpdateEventArgs.InfoParts.FlagImage));
                        }
                    }
                    worker.Dispose();
                };

                worker.RunWorkerAsync(_flagUrl);

            }
        }



        private void UpdateCurrencyRates()
        {
            if (this.CurrencyRates == null)
            {
                CurrencyRates = new Dictionary<Currency, double>();
            }

            var checkedCurrencies = from currency in Settings.currencyConversion
                                    where currency.Value
                                    select currency;

            var tempCurrencyRates = CurrencyRates;
            CurrencyRates = new Dictionary<Currency, double>();

            foreach (var currency in checkedCurrencies)
            {

                try
                {
                    double value;
                    if (tempCurrencyRates.TryGetValue(currency.Key, out value))
                    {
                        CurrencyRates.Add(currency.Key, value);
                    }
                    else
                    {
                        try
                        {
                            value = _webService.GetCurrencyRate(this.CurrencyCode.ToString(), currency.Key.ToString());
                        }
                        catch (ArgumentException e)
                        {
                            value = -1;
                        }
                        CurrencyRates.Add(currency.Key, value);

                        //notify listeners (if there is any) that currency conversions info has changed                        
                    }
                    if (OnCountryInfoUpdated != null)
                    {
                        OnCountryInfoUpdated(this, new InfoUpdateEventArgs(InfoUpdateEventArgs.InfoParts.CurrencyConversion));
                    }
                }
                catch (ArgumentException e)
                {
                    // we just asked to update info to quickly, skip this one and continue :)
                    continue;
                }
            }
            tempCurrencyRates.Clear();
        }

        private void UpdateInfoThread()
        {
            try
            {
                UpdateBaseInfo();
                new Thread(UpdateFlagImage).Start();                
                new Thread(UpdateCurrencyRates).Start();
                new Thread(UpdateCurrencyName).Start();
                new Thread(UpdatePopulation).Start();
            }
            catch (WebException e)
            {
                MessageBox.Show("Could not connect to internet, please try again latter", "Internet connection");
            }



        }

        private void UpdatePopulation()
        {

            if (!string.IsNullOrWhiteSpace(this.Name) && this.Population == 0)
            {
                this.Population = _webService.GetPopulation(this.IsoCode2Letters);
                if (OnCountryInfoUpdated != null)
                {
                    OnCountryInfoUpdated(this, new InfoUpdateEventArgs(InfoUpdateEventArgs.InfoParts.Population));
                }
            }
        }

        private void UpdateCurrencyName()
        {
            if (!string.IsNullOrWhiteSpace(this.CurrencyCode) && string.IsNullOrWhiteSpace(CurrencyName))
            {
                CurrencyName = _webService.GetCurrencyName(CurrencyCode);
                if (OnCountryInfoUpdated != null)
                {
                    OnCountryInfoUpdated(this, new InfoUpdateEventArgs(InfoUpdateEventArgs.InfoParts.CurrencyName));
                }
            }
        }

        public void UpdateInfo()
        {

            try
            {
                var thread = new Thread(UpdateInfoThread) { Name = "update " + this.Name + " info" };
                thread.Start();
            }
            catch (WebException e)
            {
                MessageBox.Show("no internet");

            }
        }

        public override bool Equals(object obj)
        {
            if (obj == null || this.GetType() != obj.GetType())
            {
                return false;
            }
            var otherCountry = (Country)obj;
            if ((this.IsoCode2Letters == otherCountry.IsoCode2Letters))
            {
                return true;
            }
            return false;
        }

        public override int GetHashCode()
        {
            return IsoCode2Letters.GetHashCode();
        }
    }
}
