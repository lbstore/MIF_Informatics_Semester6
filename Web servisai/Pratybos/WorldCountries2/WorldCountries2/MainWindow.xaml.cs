using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Threading;
using WorldCountries2.CountryInfo;

namespace WorldCountries2
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class CountriesWindow : Window
    {


        private static readonly RadialGradientBrush HoverStateBrush = new RadialGradientBrush();
        private static readonly SolidColorBrush NeutralStateBrush = new SolidColorBrush(Color.FromRgb(0x31, 0xDB, 0xF5));
        private static readonly SolidColorBrush SelectedStateBrush = new SolidColorBrush(Colors.Red);
        private Path _selectedCountryPath = null;
        private Country _selectedCountry = null;
        private readonly Dictionary<string, Country> _countriesList = new Dictionary<string, Country>();
        private readonly WebServicesCommunicator _serviceCommunicator;

        private delegate void UpdateCountryInfoDelegate();

        private UpdateCountryInfoDelegate updateMethod;

        private void InitializeRadBrush()
        {
            // Set the GradientOrigin to the center of the area being painted.
            HoverStateBrush.GradientOrigin = new Point(0.5, 0.5);

            // Set the gradient center to the center of the area being painted.
            HoverStateBrush.Center = new Point(0.5, 0.5);

            // Set the radius of the gradient circle so that it extends to 
            // the edges of the area being painted.
            HoverStateBrush.RadiusX = 0.65;
            HoverStateBrush.RadiusY = 0.85;

            // Create four gradient stops.
            HoverStateBrush.GradientStops.Add(new GradientStop(Color.FromRgb(0x29, 0xB0, 0xC4), 0.0));
            HoverStateBrush.GradientStops.Add(new GradientStop(Color.FromRgb(0x31, 0xDB, 0xF5), 1.0));

            // Freeze the brush (make it unmodifiable) for performance benefits.
            HoverStateBrush.Freeze();
        }

        public CountriesWindow()
        {
            InitializeComponent();
            InitializeRadBrush();
            CountriesLayout.AddHandler(UIElement.MouseLeftButtonUpEvent, new RoutedEventHandler(ClickHandler));
            _serviceCommunicator = new WebServicesCommunicator();
            updateMethod = UpdateCountryInfo2;
        }


        private void ClickHandler(object sender, RoutedEventArgs e)
        {

            if (_selectedCountryPath != null)
            {
                _selectedCountryPath.Fill = NeutralStateBrush;
            }
            Path p = e.OriginalSource as Path;
            if (p != null)
            {
                p.Fill = SelectedStateBrush;
                _selectedCountryPath = p;
            }
            if (p == _selectedCountryPath)
            {
                updateMethod();
            }
            e.Handled = true;
        }

        private void SetCountryName(string pathName)
        {
            string englishName;

            if (CountriesDictionary.FromXamlToEnglish.TryGetValue(pathName, out englishName))
            {
                TextBox.Text = englishName;
            }
            else
            {
                TextBox.Text = pathName;
            }
        }

        private string GetCountryName(string pathName)
        {
            string englishName;

            if (CountriesDictionary.FromXamlToEnglish.TryGetValue(pathName, out englishName))
            {
                return englishName;
            }
            else
            {
                return pathName;
            }
        }

        private void MouseEnterIntoCountry(object sender, MouseEventArgs e)
        {
            Path p = e.OriginalSource as Path;

            //if any country is selected just 
            if (p != null && _selectedCountryPath == p)
            {
                e.Handled = true;
                return;
            }
            if (p != null)
            {
                SetCountryName(p.Name);
                p.Fill = HoverStateBrush;

            }
            e.Handled = true;
        }

        private void MouseLeaveFromCountry(object sender, MouseEventArgs e)
        {
            Path p = e.OriginalSource as Path;
            if (p != null && p != _selectedCountryPath)
            {
                p.Fill = NeutralStateBrush;
                TextBox.Text = "";
            }
            if (_selectedCountryPath != null)
            {
                SetCountryName(_selectedCountryPath.Name);
            }
            e.Handled = true;
        }

        private void UpdateCountryInfo()
        {
            if (_selectedCountryPath == null)
            {
                MessageBox.Show(this, "Select country and then press \"check info\" button again", "Select country");
            }
            else
            {
                string isoCode;
                if (CountriesDictionary.FromXamlToISO.TryGetValue(_selectedCountryPath.Name, out isoCode))
                {
                    var countryInfo = _serviceCommunicator.GetFullCoutryInfo(isoCode);
                    CountryName.Text = countryInfo.sName;

                    BitmapImage bi = new BitmapImage();
                    bi.BeginInit();
                    bi.UriSource = new Uri(countryInfo.sCountryFlag, UriKind.Absolute);
                    bi.EndInit();
                    CountryFlag.Source = bi;

                    CountryPhoneCode.Content = "+" + countryInfo.sPhoneCode;

                    StringBuilder languages = new StringBuilder();
                    bool isFirst = true;
                    foreach (var language in countryInfo.Languages)
                    {
                        if (isFirst)
                            languages.Append(language.sName + " (ISO: " + language.sISOCode + ")");
                        else
                            languages.Append("\n" + language.sName + " (ISO: " + language.sISOCode + ")");
                        isFirst = false;
                    }
                    CountryLanguages.Content = languages;

                    CountryCapital.Content = countryInfo.sCapitalCity;
                    CountryContinent.Content = countryInfo.sContinentCode;
                    double rate = _serviceCommunicator.GetCurrencyRate(countryInfo.sCurrencyISOCode, "LTL");
                    CountryCurrencies.Content = countryInfo.sCurrencyISOCode + "( " + rate + " LTL)";


                }
                else
                {
                    MessageBox.Show(this, "Could not find info about selected country, please try again later", "Sorry :(");
                }


            }
        }


        private void UpdateInfo(object sender, InfoUpdateEventArgs e)
        {

            switch (e.InfoPart)
            {
                case InfoUpdateEventArgs.InfoParts.BaseInfo:
                    Dispatcher.BeginInvoke(DispatcherPriority.Input, new ThreadStart(() =>
                    {
                        try
                        {
                            CountryName.Text = string.IsNullOrWhiteSpace(_selectedCountry.Name) ? "" : _selectedCountry.Name;
                            CountryPhoneCode.Content = _selectedCountry.PhoneCode == 0 ? "" : ("+" + _selectedCountry.PhoneCode);
                            CountryLanguages.Content = string.IsNullOrWhiteSpace(_selectedCountry.FormLanguagesAsString()) ? "" : _selectedCountry.FormLanguagesAsString();
                            CountryCapital.Content = string.IsNullOrWhiteSpace(_selectedCountry.Capital) ? "" : _selectedCountry.Capital;
                            CountryContinent.Content = _selectedCountry.Continent;
                            CountryCurrencyInfo.Text = string.IsNullOrWhiteSpace(_selectedCountry.FormCurrencyDescription()) ? "" : _selectedCountry.FormCurrencyDescription();
                        }
                        catch (Exception ex)
                        {
                            MessageBox.Show("Base info exception " + ex.GetType().ToString() + " " + ex.Message);
                        }

                        //CountryCurrencies.Content
                    }));
                    break;
                case InfoUpdateEventArgs.InfoParts.FlagImage:

                    Dispatcher.BeginInvoke(DispatcherPriority.Input, new ThreadStart(() =>
                    {
                        try
                        {                           
                            CountryFlag.Source = _selectedCountry.Flag;
                        }
                        catch (Exception ex)
                        {
                            MessageBox.Show("Flag exception " + ex.GetType().ToString() + " " + ex.Message);

                        }
                    }));
                    break;
                case InfoUpdateEventArgs.InfoParts.CurrencyConversion:
                    Dispatcher.BeginInvoke(DispatcherPriority.Input, new ThreadStart(() =>
                    {
                        try
                        {
                            CountryCurrencies.Content = _selectedCountry.FormCurrenciesAsString();
                        }
                        catch (Exception ex)
                        {
                            MessageBox.Show("Currencies exception: " + ex.GetType().ToString() + " " + ex.Message + " " + ex.Source);
                        }
                    }));
                    break;
                case InfoUpdateEventArgs.InfoParts.Population:
                    Dispatcher.BeginInvoke(DispatcherPriority.Input, new ThreadStart(() =>
                    {
                        try
                        {
                            if (_selectedCountry.Population > 0)
                            {
                                CountryPopulation.Content = string.Format("{0,12:N0}",_selectedCountry.Population);
                            }
                            else
                            {
                                CountryPopulation.Content = "unknown";
                            }
                            
                        }
                        catch (Exception ex)
                        {
                            MessageBox.Show("Population exception: " + ex.GetType().ToString() + " " + ex.Message + " " + ex.Source);
                        }
                    }));
                    break;
                case InfoUpdateEventArgs.InfoParts.CurrencyName:
                    Dispatcher.BeginInvoke(DispatcherPriority.Input, new ThreadStart(() =>
                    {
                        try
                        {
                            CountryCurrencyInfo.Text = string.IsNullOrWhiteSpace(_selectedCountry.FormCurrencyDescription()) ? "" : _selectedCountry.FormCurrencyDescription();
                        }
                        catch (Exception ex)
                        {
                            MessageBox.Show("Currency info exception: " + ex.GetType().ToString() + " " + ex.Message + " " + ex.Source);
                        }
                    }));
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }

        }

        private void ShowKnownCountryInfo()
        {

            CountryName.Text = string.IsNullOrWhiteSpace(_selectedCountry.Name) ? "" : _selectedCountry.Name;
            CountryPhoneCode.Content = _selectedCountry.PhoneCode == 0 ? "" : ("+" + _selectedCountry.PhoneCode);
            CountryLanguages.Content = string.IsNullOrWhiteSpace(_selectedCountry.FormLanguagesAsString()) ? "" : _selectedCountry.FormLanguagesAsString();
            CountryCapital.Content = string.IsNullOrWhiteSpace(_selectedCountry.Capital) ? "" : _selectedCountry.Capital;
            CountryContinent.Content = _selectedCountry.Continent;
            CountryCurrencyInfo.Text = string.IsNullOrWhiteSpace(_selectedCountry.FormCurrencyDescription()) ? "" : _selectedCountry.FormCurrencyDescription();
            CountryCurrencies.Content = string.IsNullOrWhiteSpace(_selectedCountry.FormCurrenciesAsString()) ? "" : _selectedCountry.FormCurrenciesAsString();
            if (_selectedCountry.Population > 0)
            {
                CountryPopulation.Content = string.Format("{0,12:N0}", _selectedCountry.Population);
            }
            else
            {
                CountryPopulation.Content = "unknown";
            }
            if (_selectedCountry.Flag != null)
            {
                Dispatcher.BeginInvoke(new ThreadStart(delegate
                {
                    CountryFlag.Source = _selectedCountry.Flag;
                }));
            }
            else
            {
                CountryFlag.Source = null;
            }
        }

        private void UpdateCountryInfo2()
        {
            if (_selectedCountryPath == null)
            {
                MessageBox.Show(this, "Select country and then press \"check info\" button again", "Select country");
            }
            else
            {
                string isoCode;
                if (CountriesDictionary.FromXamlToISO.TryGetValue(_selectedCountryPath.Name, out isoCode))
                {

                    if (_selectedCountry != null)
                        _selectedCountry.OnCountryInfoUpdated -= UpdateInfo;

                    Country newSelectedCountry;
                    // if we already have retrieved information about this country, then display it and check if any updates are needed
                    if (_countriesList.TryGetValue(isoCode, out newSelectedCountry) && newSelectedCountry != null)
                    {
                        _selectedCountry = newSelectedCountry;
                    }
                    // if we don't have this country, then create and retrieve information from web services
                    else
                    {
                        _selectedCountry = new Country(isoCode, GetCountryName(_selectedCountryPath.Name));
                        _countriesList.Add(isoCode, _selectedCountry);
                    }
                    _selectedCountry.OnCountryInfoUpdated += new Country.CountryInfoUpdateHandler(UpdateInfo);

                    ShowKnownCountryInfo();
                    _selectedCountry.UpdateInfo();
                }
                else
                {
                    MessageBox.Show(this, "Could not find info about selected country, please try again later", "Sorry :(");
                }


            }
        }

    }
}
