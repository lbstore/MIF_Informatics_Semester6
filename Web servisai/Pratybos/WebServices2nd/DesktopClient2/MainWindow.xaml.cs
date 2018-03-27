using System.Windows;
using ESRI.ArcGIS.Client;
using ESRI.ArcGIS.Client.Geometry;
using ESRI.ArcGIS.Client.WebMap;
using System.Windows.Media;
using System.Windows.Input;
using System.Collections.Generic;
using DesktopClient2.Models;
using System.Windows.Controls;
using System.ComponentModel;
using DesktopClient2.LocationBasedTasksService;
using System.Threading.Tasks;
using System.ServiceModel;
using System;

namespace DesktopClient2
{

    public partial class MainWindow : Window, INotifyPropertyChanged
    {

        private GraphicsLayer RuntimeGraphicsLayer;
        //private GraphicMapPoint TemporarySelectionMapPoint = null;
        public GraphicMapPoint temporarySelectionMapPoint;
        private GraphicMapPoint selectedExistingItem;

        public GraphicMapPoint SelectedExistingItem
        {
            get { return selectedExistingItem; }
            set
            {
                selectedExistingItem = value;
                OnPropertyChanged("SelectedExistingItem");
            }
        }

        public static GraphicMapPoint ConvertLocationTask(LocationTask locationTask)
        {
            return new GraphicMapPoint(locationTask);
        }

        public static LocationTask ConvertGraphicMapPoint(GraphicMapPoint graphicMapPoint)
        {
            var locationTask = new LocationTask();

            locationTask.Info = graphicMapPoint.MapPointContent.Info;
            locationTask.Label = graphicMapPoint.MapPointContent.Label;
            locationTask.LastTimeModified = graphicMapPoint.MapPointContent.PublishedDate;
            locationTask.Location = new Location()
            {
                Address = graphicMapPoint.MapPointContent.Address,
                Latitude = graphicMapPoint.MapPointContent.Latitude,
                Longtitude = graphicMapPoint.MapPointContent.Longtitude
            };
            locationTask.Name = graphicMapPoint.MapPointContent.Name;
            //locationTask.Status = graphicMapPoint.MapPointContent.
            locationTask.UserEmail = graphicMapPoint.MapPointContent.UserName;
            locationTask.DatabaseId = graphicMapPoint.MapPointContent.Id;
            return locationTask;
        }


        public GraphicMapPoint TemporarySelectionMapPoint
        {
            get { return temporarySelectionMapPoint; }
            set
            {
                temporarySelectionMapPoint = value;
                OnPropertyChanged("TemporarySelectionMapPoint");
            }
        }
        //public GraphicMapPoint TemporarySelectionMapPoint2 { get; set; }
        // public MapPointContent testContent = new MapPointContent(1545454, 2222545);
        /* public MapPointContent TestContent
         {
             get { return testContent; }
             set { testContent = value; }
         }*/
        // private GraphicMapPoint TestPoint = new GraphicMapPoint(1545454, 2222545, 20100);




        public MainWindow()
        {
            //TemporarySelectionMapPoint2 = new GraphicMapPoint(15554, 45454, 102100);
            //TemporarySelectionMapPoint = new GraphicMapPoint(15554, 45454, 102100);
            // License setting and ArcGIS Runtime initialization is done in Application.xaml.cs.
            this.DataContext = this;
            InitializeComponent();
            RuntimeGraphicsLayer = MyMap.Layers["TemporaryGraphicsLayer"] as GraphicsLayer;
            //RuntimeGraphicsLayer.Graphics.Add(new GraphicMapPoint(-7356594.25, 4752385.95, 102100));
            //RuntimeGraphicsLayer.Graphics.Add(new GraphicMapPoint(654893.89, 7718746.02, 102100));
            //RuntimeGraphicsLayer.Graphics.Add(new GraphicMapPoint(4801033.36, 15325547.3, 102100));
            //RuntimeGraphicsLayer.Graphics.Add(new GraphicMapPoint(-5468910.57, 1741081.03, 102100));
            //RuntimeGraphicsLayer.Graphics.Add(new GraphicMapPoint(-5468910.57, 1741081.03, 102100));

            var serviceTest = new com.somee.lukas.LocationBasedTasksService();
            

            ServiceClient = new LocationBasedTasksServiceClient();
            // Allows us to run an async Task from the non-async constructor
            Task.Run(async () =>
            {
                var request = new FindNearTasksRequest(new FindNearTasksRequestBody()
                {
                    center = new Location(),
                    radius = 9000000,
                    system = Location.MeasurementSystem.Metric                   
                });

                var result = await ServiceClient.FindNearTasksAsync(request);
                var list = result.Body.FindNearTasksResult;
                var listCout = list.Count;
                foreach (var locationTask in list)
                {

                    Dispatcher.Invoke(new Action(() =>
                        {
                            try
                            {
                                RuntimeGraphicsLayer.Graphics.Add(new GraphicMapPoint(locationTask));
                            }
                            catch (Exception e)
                            {
                                MessageBox.Show("error: " + e.Message, "Error");
                            }
                        }));


                }

            });
        }



        private void UploadeMapPointToWeb()
        {
            var uploadingPoint = TemporarySelectionMapPoint;
            TemporarySelectionMapPoint = null;
            /*if(!uploadSucceed){
             *  MessageBox.Show("ERROR");
             *  RuntimeGraphicsLayer.Graphics.Remove(uploadingPoint);
             * }*/
        }



        private void CreateNewSelectionMapPoint()
        {
            var pos = Mouse.GetPosition(MyMap);
            var location = MyMap.ScreenToMap(pos);
            var graphic = new GraphicMapPoint(location.X, location.Y, location.SpatialReference);
            RuntimeGraphicsLayer.Graphics.Add(graphic);
            TemporarySelectionMapPoint = graphic;
            //new InfoWindow
            NewPointInfoWindow.Anchor = location;
            NewPointInfoWindow.IsOpen = true;

        }

        private void ClearSelection()
        {
            if (SelectedPointInfoWindow.IsOpen)
            {
                SelectedPointInfoWindow.IsOpen = false;
            }
            if (NewPointInfoWindow.IsOpen)
            {
                NewPointInfoWindow.IsOpen = false;
            }
            if (TemporarySelectionMapPoint != null)
            {
                RuntimeGraphicsLayer.Graphics.Remove(TemporarySelectionMapPoint);
                TemporarySelectionMapPoint = null;
            }

        }


        private void MyMap_MouseClick(object sender, ESRI.ArcGIS.Client.Map.MouseEventArgs e)
        {

            System.Windows.Point screenPnt = MyMap.MapToScreen(e.MapPoint);
            // Account for difference between Map and application origin
            GeneralTransform generalTransform = MyMap.TransformToVisual(Application.Current.MainWindow);
            System.Windows.Point transformScreenPnt = generalTransform.Transform(screenPnt);
            var selected = new List<Graphic>();
            selected.AddRange(RuntimeGraphicsLayer.FindGraphicsInHostCoordinates(transformScreenPnt));

            //if button was pressen on clear map point
            if (selected.Count == 0)
            {
                ClearSelection();
                CreateNewSelectionMapPoint();
                return;
            }

            // if button was pressen on item (which is on map)             
            else
            {
                ClearSelection();
                foreach (Graphic g in selected)
                {
                    SelectedExistingItem = g as GraphicMapPoint;
                    SelectedPointInfoWindow.Anchor = new MapPoint(g.Geometry.Extent.XMax, g.Geometry.Extent.YMax, g.Geometry.SpatialReference);
                    SelectedPointInfoWindow.IsOpen = true;
                    //Since a ContentTemplate is defined, Content will define the DataContext for the ContentTemplate
                    //SelectedPointInfoWindow.Content = g.Attributes;
                    return;
                }
            }
        }






        private void graphic_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            var graphic = sender as Graphic;
            if (graphic == null)
            {
                return;
            }
            var pos = e.GetPosition(MyMap);
            SelectedPointInfoWindow.Anchor = MyMap.ScreenToMap(pos);
            SelectedPointInfoWindow.IsOpen = true;
            //SelectedPointInfoWindow.Content = graphic.Attributes;
        }




        /*
                private void Button_Click(object sender, RoutedEventArgs e)
                {

                }

          */
        private async void ButtonClick_DeleteSelectedPoint(object sender, RoutedEventArgs e)
        {

            try
            {
                var request = new DeleteTaskRequest(new DeleteTask()
                {
                    taskId = SelectedExistingItem.MapPointContent.Id,
                    user = new User()
                    {
                        Email = "Lukas@Klusis.lt",
                        Password = "Lukas"
                    }
                });
                var response = await ServiceClient.DeleteTaskAsync(request);
                var succeed = response.DeleteTaskResponse1;
                if (succeed)
                {
                    Dispatcher.Invoke(new Action(() =>
                    {
                        RuntimeGraphicsLayer.Graphics.Remove(selectedExistingItem);
                        ClearSelection();
                    }));
                }
                else
                {
                    MessageBox.Show("Could not delete this task", "Ooops");
                }


                e.Handled = true;
            }
            catch (FaultException ex)
            {
                Dispatcher.Invoke(new Action(() =>
                {
                    RuntimeGraphicsLayer.Graphics.Remove(selectedExistingItem);
                    ClearSelection();
                }));
            }
        }

        private async void ButtonClick_AddNewPoint(object sender, RoutedEventArgs e)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(temporarySelectionMapPoint.MapPointContent.Name))
                {
                    MessageBox.Show("Task don't have name.\nIt would be great idea to give one", "Give more info");
                    return;
                }
                //add web service connection
                var newPoint = temporarySelectionMapPoint;

                // remove point from temporary, thus this point wount be deleted
                temporarySelectionMapPoint = null;
                ClearSelection();

                var request = new AddTaskRequest(new AddTask()
                {
                    task = ConvertGraphicMapPoint(newPoint),
                    user = new User()
                    {
                        Email = "Lukas@Klusis.lt",
                        Password = "Lukas"
                    }
                });
                var response = await ServiceClient.AddTaskAsync(request);
                var newTaskId = response.AddTaskResponse1;
                newPoint.MapPointContent.Id = (int) newTaskId;
                if (newTaskId < 0)
                {
                    temporarySelectionMapPoint = newPoint;
                    ClearSelection();
                    MessageBox.Show("Could not add new point", "Error");
                }
            }
            catch (FaultException ex)
            {
                //MessageBox.Show("Could not add new point " + ex.Message, "Error");
            }

        }

        private void ComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            var combo = sender as ComboBox;
            var sel = combo.SelectedItem as ComboBoxItem;
            if (sel.Tag == null) { return; }

            // Find and remove the current basemap layer from the map
            if (MyMap == null) { return; }
            var oldBasemap = MyMap.Layers["BaseMap"];
            MyMap.Layers.Remove(oldBasemap);

            // Create a new basemap layer
            var newBasemap = new ArcGISTiledMapServiceLayer();

            // Set the ServiceUri with the url defined for the ComboBoxItem's Tag
            newBasemap.Url = sel.Tag.ToString();

            // Give the layer the same ID so it can still be found with the code above
            newBasemap.ID = "BaseMap";

            // Insert the new basemap layer as the first (bottom) layer in the map
            MyMap.Layers.Insert(0, newBasemap);
        }

        private void MyMap_MouseRightButtonUp(object sender, MouseButtonEventArgs e)
        {
            ClearSelection();
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





        public LocationBasedTasksServiceClient ServiceClient { get; set; }
    }
}
