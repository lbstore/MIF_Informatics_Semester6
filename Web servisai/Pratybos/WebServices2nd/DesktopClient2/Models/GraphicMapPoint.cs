using ESRI.ArcGIS.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Media;
using ESRI.ArcGIS.Client.Symbols;
using ESRI.ArcGIS.Client.Geometry;
using System.ComponentModel;
using ESRI.ArcGIS.Client.Tasks;
using System.Windows;
using System.Threading.Tasks;
using DesktopClient2.LocationBasedTasksService;



namespace DesktopClient2.Models
{
    public class GraphicMapPoint : Graphic
    {
        private Brush shapeColor = new SolidColorBrush(Colors.Red);
        private MapPoint mapPoint;
        public Brush ShapeColor
        {
            get { return shapeColor; }
            set { shapeColor = value; }
        }              

        private SimpleMarkerSymbol.SimpleMarkerStyle shapeStyle = SimpleMarkerSymbol.SimpleMarkerStyle.Circle;
        public SimpleMarkerSymbol.SimpleMarkerStyle ShapeStyle
        {
            get { return shapeStyle; }
            set { shapeStyle = value; }
        }

        private int shapeSize = 10;
        public int ShapeSize
        {
            get { return shapeSize; }
            set { shapeSize = value; }
        }


        //REWRITE THIS CLASS!!
        //REWRITE THIS CLASS!!
        //REWRITE THIS CLASS!!
        //REWRITE THIS CLASS!!
        // USE LocationTask from WebService
        //REWRITE THIS CLASS!!
        //REWRITE THIS CLASS!!
        //REWRITE THIS CLASS!!
        //REWRITE THIS CLASS!!
        //REWRITE THIS CLASS!!
   
        public MapPointContent MapPointContent { get; set; }        
        
        private SimpleMarkerSymbol simpleMarkerSymbol;

        public GraphicMapPoint(double X, double Y, int spatialReferenceWKID) : this(X,Y,new SpatialReference(spatialReferenceWKID))
        {                        
        }

        public GraphicMapPoint(double X, double Y, SpatialReference spatialReference)
        {
            var merc = new ESRI.ArcGIS.Client.Projection.WebMercator();
            var longLat = merc.ToGeographic(new MapPoint(X,Y));
            
            
            mapPoint = new MapPoint(X,Y,spatialReference);
            
            this.Geometry = mapPoint;
            simpleMarkerSymbol = new SimpleMarkerSymbol();
            simpleMarkerSymbol.Color = ShapeColor;
            simpleMarkerSymbol.Size = ShapeSize;
            simpleMarkerSymbol.Style = ShapeStyle;
            this.Symbol = simpleMarkerSymbol;
            
            this.MapPointContent = new MapPointContent(longLat.Extent.XMax, longLat.Extent.YMax);
            this.Attributes.Add("MapPointContent", MapPointContent);
            if (MapPointContent.Address == null)
            {
                ReverseGeocode();
            }
            
        }


        public GraphicMapPoint(LocationTask task)
        {
            var merc = new ESRI.ArcGIS.Client.Projection.WebMercator();
            //for esri Lat = Y Long = X
            var extent = merc.FromGeographic(new MapPoint(task.Location.Longtitude, task.Location.Latitude, new SpatialReference(4326)));

            
            //mapPoint = new MapPoint(extent.Extent.XMax, extent.Extent.YMax, new SpatialReference(102100));
            this.Geometry = extent;
            simpleMarkerSymbol = new SimpleMarkerSymbol();
            simpleMarkerSymbol.Color = ShapeColor;
            simpleMarkerSymbol.Size = ShapeSize;
            simpleMarkerSymbol.Style = ShapeStyle;
            this.Symbol = simpleMarkerSymbol;
            this.Attributes.Add("MapPointContent", MapPointContent);

            this.MapPointContent = new MapPointContent(extent.Extent.XMax, extent.Extent.YMax);
            this.MapPointContent.Address = task.Location.Address;
            this.MapPointContent.Id= task.DatabaseId;
            this.MapPointContent.Latitude = task.Location.Latitude;
            this.MapPointContent.Longtitude = task.Location.Longtitude;
            this.MapPointContent.Name = task.Name;
            this.MapPointContent.UserName = task.UserEmail;
            this.MapPointContent.PublishedDate = task.LastTimeModified;
            this.MapPointContent.Info = task.Info;            
            this.MapPointContent.Info = task.Info;
            this.MapPointContent.Label = task.Label;
            
            if (string.IsNullOrWhiteSpace(MapPointContent.Address))
            {
                ReverseGeocode();
            }



        }

        public async void ReverseGeocode()
        {
            try
            {
                
                var geoSpatialRef = new Esri.ArcGISRuntime.Geometry.SpatialReference(4326);
                var spatialRef = new Esri.ArcGISRuntime.Geometry.SpatialReference(102100);
                spatialRef.Wkt = "PROJCS[\"WGS_1984_Web_Mercator_Auxiliary_Sphere\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Mercator_Auxiliary_Sphere\"],PARAMETER[\"False_Easting\",0.0],PARAMETER[\"False_Northing\",0.0],PARAMETER[\"Central_Meridian\",0.0],PARAMETER[\"Standard_Parallel_1\",0.0],PARAMETER[\"Auxiliary_Sphere_Type\",0.0],UNIT[\"Meter\",1.0]]";
                var mapPoint = new Esri.ArcGISRuntime.Geometry.MapPoint(this.mapPoint.X, this.mapPoint.Y, spatialRef);
                var mapPointGeo = Esri.ArcGISRuntime.Geometry.GeometryEngine.Project(mapPoint, geoSpatialRef) as Esri.ArcGISRuntime.Geometry.MapPoint;
                var uri = new Uri("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
                var token = String.Empty;
                var locator = new Esri.ArcGISRuntime.Tasks.Geocoding.OnlineLocatorTask(uri, token);
                var addressInfo = await locator.ReverseGeocodeAsync(mapPointGeo, 150, new System.Threading.CancellationToken());
                if (addressInfo == null || addressInfo.AddressFields == null || addressInfo.AddressFields.Count == 0)
                {
                    //really wrong approch, but I was to bored and to tired to fix it
                    LocatorTask_Failed(this, "result is empty");
                }
                else
                {
                    LocatorTask_LocationToAddressCompleted(this, addressInfo.AddressFields);
                }
            }catch(Exception e){
                LocatorTask_Failed(this, e.Message);
            }
            
        }

        // Draw the graphic at the click point with attributes containing the location's address.
        private void LocatorTask_LocationToAddressCompleted(object sender, IDictionary<string,string> addressFields)
        {
            var address = "";
            bool needComa = false;
            if(addressFields.Keys.Any(x=> x == "Address")){
               address += addressFields["Address"].ToString();
                needComa = true;
            }
            if (addressFields.Keys.Any(x => x == "City"))
            {
                if (needComa)
                {
                    address += ", ";
                    needComa = false;
                }
                address += addressFields["City"].ToString();
                needComa = true;
            }
            if (addressFields.Keys.Any(x => x == "State"))
            {
                if (needComa)
                {
                    address += ", ";
                    needComa = false;
                }
                address += addressFields["State"].ToString();
                needComa = true;
            }
            
            if (addressFields.Keys.Any(x => x == "CountryCode"))
            {
                if (needComa)
                {
                    address += ", ";
                    needComa = false;
                }
                address += addressFields["CountryCode"].ToString();
                needComa = true;
            }

            if (addressFields.Keys.Any(x => x == "Zip"))
            {
                if (needComa)
                {
                    address += ", ";
                    needComa = false;
                }
                address += addressFields["Zip"].ToString();
                needComa = true;
            }
            else
            {
                if (addressFields.Keys.Any(x => x == "Postal"))
                {
                    if (needComa)
                    {
                        address += ", ";
                        needComa = false;
                    }
                    address += addressFields["Postal"].ToString();
                    needComa = true;
                }
            }
            MapPointContent.Address = address;

            //string address1 = String.Format("{0}, {1} {2}", addressFields["City"], addressFields["State"], addressFields["Zip"]);
            
        }

        private static DateTime lastError = new DateTime();
        // Notify the user if the task fails to execute.
        private void LocatorTask_Failed(object sender, string error)
        {
            var difference = DateTime.Now.Subtract(lastError);
            
            if (difference.CompareTo(new TimeSpan(0, 0, 10)) > 0)            
            {
                lastError = DateTime.Now;
               // MessageBox.Show("Locator service failed: " + error);                
            }
            MapPointContent.Address = "Unknown";
            
        }
    }
}
