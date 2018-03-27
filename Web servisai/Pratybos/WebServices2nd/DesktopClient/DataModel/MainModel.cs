using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DesktopClient.DataModel
{
    public class MainModel : INotifyPropertyChanged
    {
        private ObservableCollection<DataPoint> _PointsOfInterest;
        public ObservableCollection<DataPoint> PointsOfInterest
        {
            get { return _PointsOfInterest; }
            set
            {
                if (_PointsOfInterest != value)
                {
                    _PointsOfInterest = value;
                    if (PropertyChanged != null)
                        PropertyChanged(this, new PropertyChangedEventArgs("PointsOfInterest"));
                }
            }
        }
        public event PropertyChangedEventHandler PropertyChanged;
    }

}
