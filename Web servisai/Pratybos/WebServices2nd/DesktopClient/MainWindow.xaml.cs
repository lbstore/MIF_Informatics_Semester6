using Esri.ArcGISRuntime.Controls;
using Esri.ArcGISRuntime.Geometry;
using Esri.ArcGISRuntime.Layers;
using Esri.ArcGISRuntime.Symbology;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
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

namespace DesktopClient
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        GraphicsLayer GraphicsLayer;
        MapPoint lastPoint = null;

        public MainWindow()
        {
           
            InitializeComponent();
            GraphicsLayer = MyMap.Layers["TemporaryGraphicsLayer"] as GraphicsLayer;


             
            GraphicsLayer.Graphics.Add(MakeMapPointGraphic2(-7356594.25, 4752385.95, 102100, 48, "Dead Man's Gulf"));
            GraphicsLayer.Graphics.Add(MakeMapPointGraphic2(654893.89, 7718746.02, 102100, 50, "Shark Reef"));
            GraphicsLayer.Graphics.Add(MakeMapPointGraphic2(4801033.36, 15325547.3, 102100, 52, "Crab Alley"));
            GraphicsLayer.Graphics.Add(MakeMapPointGraphic2(-5468910.57, 1741081.03, 102100, 51, "Goo Lagoon"));
            GraphicsLayer.Graphics.Add(MakeMapPointGraphic2(-1614958.43, -126382.05, 102100, 50, "Bikini Bottom"));
            
        }
        public async void GetAddressTest()
        {
            try
            {
                var mapPoint = await MyMapView.Editor.RequestPointAsync();
                var geoSpatialRef = new Esri.ArcGISRuntime.Geometry.SpatialReference(4326);
                var spatialRef = new Esri.ArcGISRuntime.Geometry.SpatialReference(102100);
                //spatialRef.Wkt = "PROJCS[\"WGS_1984_Web_Mercator_Auxiliary_Sphere\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Mercator_Auxiliary_Sphere\"],PARAMETER[\"False_Easting\",0.0],PARAMETER[\"False_Northing\",0.0],PARAMETER[\"Central_Meridian\",0.0],PARAMETER[\"Standard_Parallel_1\",0.0],PARAMETER[\"Auxiliary_Sphere_Type\",0.0],UNIT[\"Meter\",1.0]]";
                //var mapPoint = new Esri.ArcGISRuntime.Geometry.MapPoint(this.mapPoint.X, this.mapPoint.Y, spatialRef);
                var mapPointGeo = Esri.ArcGISRuntime.Geometry.GeometryEngine.Project(mapPoint, geoSpatialRef) as Esri.ArcGISRuntime.Geometry.MapPoint;
                var uri = new Uri("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
                var token = String.Empty;
                var locator = new Esri.ArcGISRuntime.Tasks.Geocoding.OnlineLocatorTask(uri, token);
                var addressInfo = await locator.ReverseGeocodeAsync(mapPointGeo, 100, new System.Threading.CancellationToken());
            }
            catch (Exception e)
            {
                MessageBox.Show("Error: " + e.Message, "Error");
            }
                
        }

        private void ComboBox_SelectionChanged(object sender, EventArgs e)
        {
            var combo = sender as ComboBox;
            var sel = combo.SelectedItem as ComboBoxItem;
            if (sel.Tag == null) { return; }

            // Find and remove the current basemap layer from the map
            if (MyMap == null) { return; }
            var oldBasemap = MyMap.Layers["BaseMap"];
            MyMap.Layers.Remove(oldBasemap);

            // Create a new basemap layer
            var newBasemap = new Esri.ArcGISRuntime.Layers.ArcGISTiledMapServiceLayer();

            // Set the ServiceUri with the url defined for the ComboBoxItem's Tag
            newBasemap.ServiceUri = sel.Tag.ToString();

            // Give the layer the same ID so it can still be found with the code above
            newBasemap.ID = "BaseMap";

            // Insert the new basemap layer as the first (bottom) layer in the map
            MyMap.Layers.Insert(0, newBasemap);
        }

        private void MyMapView_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            GetAddressTest();
            
        }


        public Graphic MakeMapPointGraphic2(double X, double Y, int SR, int anAttributeValue1, string anAttributeValue2)
        {
            // This function constructs a new Graphic using: 
            // (1) coordinate pairs (X, Y)
            // (2) a SpatialReference.WKID (SR)
            // (3) some Attribute values (anAttributeValue, anAttributeValue2)

            // Create a SpatialReference for the Graphic.
            
            Esri.ArcGISRuntime.Geometry.SpatialReference aSpatialReference = new Esri.ArcGISRuntime.Geometry.SpatialReference(SR);

            // Create a MapPoint object and set its SpatialReference and coordinate (X,Y) information. 
            Esri.ArcGISRuntime.Geometry.MapPoint aMapPoint = new Esri.ArcGISRuntime.Geometry.MapPoint(X, Y, aSpatialReference);

            // Create a new instance of one Graphic and assign its Geometry.
            
            Graphic aGraphic = new Graphic();
            aGraphic.Geometry = (Esri.ArcGISRuntime.Geometry.Geometry)aMapPoint;

            // Create a new instance of a SimpleMarkerSymbol and set its Color, Style, and Size Properties.
            SimpleMarkerSymbol aSimpleMarkerySymbol = new SimpleMarkerSymbol();
            aSimpleMarkerySymbol.Color = Colors.Red;
            aSimpleMarkerySymbol.Style = SimpleMarkerStyle.Circle;
            aSimpleMarkerySymbol.Size = 20;
            aGraphic.Symbol = (Symbol)aSimpleMarkerySymbol;

            // Add some Attributes to the Graphic.
            aGraphic.Attributes.Add("Temperature", anAttributeValue1);
            aGraphic.Attributes.Add("Location", anAttributeValue2);

            // Dynamically wire-up the MouseRightButtonDown Event handler.
            
            

            // Return the created Graphic.
            return aGraphic;
        }

        private void MyMapView_MouseDoubleClick(object sender, MouseButtonEventArgs e)
        {
            var mapView = sender as MapView;
            var pos = Mouse.GetPosition(mapView);
            var location = mapView.ScreenToLocation(pos);
            
            GraphicsLayer.Graphics.Add(MakeMapPointGraphic2(location.X,location.Y, location.SpatialReference.Wkid, 0, "Some"));
            
        }


    }



}
