using DesktopClient2.LocationBasedTasksService;
using ESRI.ArcGIS.Client.Geometry;
using ESRI.ArcGIS.Client.Tasks;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;

namespace DesktopClient2.Models
{
    public class MapPointContent : INotifyPropertyChanged 
    {

        private int id;
        public int Id
        {
            get { return id;}
            set
            {
                if (value < maxId)
                {
                    maxId = value + 1;
                }
                id = value;
            }
        }

        public string Label { get; set; }
        private static int maxId;
        // X coordinate
        public double Longtitude { get; set; }
        // Y coordinate
        public double Latitude { get; set; }

        public string Name { get; set; }
        public string Info { get; set; }
        public DateTime PublishedDate { get; set; }
        public string UserName { get; set; }
        private string address;
        public string Address
        {
            get
            {
                return address;
            }
            set
            {
                address = value;
                OnPropertyChanged("Address");
            }
        }


        

        public MapPointContent(double X, double Y)
        {
            id = maxId;
            maxId++;
            this.Latitude = Y;
            this.Longtitude = X;
            PublishedDate = DateTime.Now;
        }

        public MapPointContent()
            : this(0, 0)
        {
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }
            var otherMapPointConten = obj as MapPointContent;
            if (otherMapPointConten == null)
            {
                return false;
            }

            return (this.id == otherMapPointConten.id);
        }

        public override int GetHashCode()
        {
            return (int)id % 47;
        }       


        public event PropertyChangedEventHandler PropertyChanged;
        protected void OnPropertyChanged(string name)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null)
            {
                handler(this, new PropertyChangedEventArgs(name));
            }
        }
    }
}
