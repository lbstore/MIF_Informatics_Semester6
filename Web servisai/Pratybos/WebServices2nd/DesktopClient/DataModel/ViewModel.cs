using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace DesktopClient.DataModel
{
    class ViewModel
    {
         private const double width = 360;
        private const double height = 180;
        private static Random r = new Random();

        public MainModel Data { get; set; }
        private static ViewModel instance;
        public ViewModel Instance
        {
            get
            {
                if (instance == null)
                    instance = new ViewModel();
                return instance;
            }
        }

        public ICommand Randomize { get; private set; }
        public ICommand AddRandom { get; private set; }
        public ICommand RemoveFirst { get; private set; }

        public ViewModel()
        {
            Data = new MainModel()
            {
                PointsOfInterest = new
                    System.Collections.ObjectModel.ObservableCollection<DataPoint>()
            };
            GenerateDataSet();

            AddRandom = new DelegateCommand((a) => AddRandomEntry(), (b) => { return true; });

            RemoveFirst = new DelegateCommand((a) =>
            {
                if (Data.PointsOfInterest.Count > 0) Data.PointsOfInterest.RemoveAt(0);
            }, (b) => { return true; });
            
            Randomize = new DelegateCommand((a) => RandomizeEntries(), (b) => { return true; });
        }

        #region Generate random data
        private void GenerateDataSet()
        {
            for (int i = 0; i < 10; i++)
            {
                AddRandomEntry();
            }
        }

        private void AddRandomEntry()
        {
            Data.PointsOfInterest.Add(CreateRandomEntry(Data.PointsOfInterest.Count));
        }

        private DataPoint CreateRandomEntry(int i)
        {
            return new DataPoint()
            {
                X = r.NextDouble() * width - width * .5,
                Y = r.NextDouble() * height - height * .5,
                Name = string.Format("Item #{0}", i)
            };
        }

        private void RandomizeEntries()
        {
            for (int i = 0; i < Data.PointsOfInterest.Count; i++)
            {
                var pnt = Data.PointsOfInterest[r.Next(Data.PointsOfInterest.Count)];
                pnt.X = r.NextDouble() * width - width * .5;
                pnt.Y = r.NextDouble() * height - height * .5;
            }
        }
        #endregion

    }
}
