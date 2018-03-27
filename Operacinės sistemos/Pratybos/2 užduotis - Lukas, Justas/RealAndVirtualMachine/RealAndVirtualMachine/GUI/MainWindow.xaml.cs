
using System;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;
using RealAndVirtualMachine.Machines;


namespace RealAndVirtualMachine.GUI
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>   
    public partial class MainWindow : Window
    {
        
        private ObservableCollection<VirtualMachine> m_virtualMachines = new ObservableCollection<VirtualMachine>();

        public RealMachine RealMachine { get; private set; }


        public ObservableCollection<VirtualMachine> VirtualMachines
        {
            get { return m_virtualMachines; }
            set { m_virtualMachines = value; }
        }

        public MainWindow()
        {
            RealMachine = new RealMachine(m_virtualMachines);
            InitializeComponent();
        }

        //this should be moved to RealMachine class
        private void DoNextInstruction(VirtualMachine virtualMachine)
        {
            try
            {
                RealMachine.ExecuteAction(virtualMachine);               
            }
            catch (Exception exception)
            {
                virtualMachine.ReleaseResources();
                VirtualMachines.Remove(virtualMachine);
                MessageBox.Show(this, "Ooops... your program have crased\n" + exception.Message, "Program " + virtualMachine.Name + "("+ virtualMachine.PID +") have occured error");
            }
        }

        private void OpenFileButton_OnClick(object sender, RoutedEventArgs e)
        {
            // Create OpenFileDialog 
            Microsoft.Win32.OpenFileDialog dlg = new Microsoft.Win32.OpenFileDialog();



            // Set filter for file extension and default file extension 
            dlg.DefaultExt = ".txt";
            //dlg.Filter = "JPEG Files (*.jpeg)|*.jpeg|PNG Files (*.png)|*.png|JPG Files (*.jpg)|*.jpg|GIF Files (*.gif)|*.gif";


            // Display OpenFileDialog by calling ShowDialog method 
            Nullable<bool> result = dlg.ShowDialog();


            // Get the selected file name and display in a TextBox 
            if (result == true)
            {


                try
                {
                    // Open document 
                    string filename = dlg.FileName;
                    
                    var vm = new VirtualMachine(RealMachine);
                    vm.LoadProgramToMemmory(filename);
                    VirtualMachines.Add(vm);
                    VirtualMachinesTabControl.SelectedIndex = VirtualMachines.IndexOf(vm);
                }
                catch (InsufficientMemoryException ex)
                {
                    MessageBox.Show(this,"Sorry, no more memory left","No more memory");
                }
                catch (ProgramContractException ex)
                {
                    MessageBox.Show(this,"Sorry, could not load program. This program have errors:\n" + ex.Message,"Program code error");
                }
                catch (Exception ex)
                {
                    MessageBox.Show(this, "Sorry, could not load program.\n" + ex.Message, "Error");
                }
            }
        }

        private void VirtualMachinesOneStepButton_OnClick(object sender, RoutedEventArgs e)
        {
            var button = (Button) sender;
            var vm = button.DataContext as VirtualMachine;
            if (vm == null)
            {
                return;
            }
            DoNextInstruction(vm);
            e.Handled = true;
        }

        private void RemoveVirtualMachine_ButtonClick(object sender, RoutedEventArgs e)
        {
            var button = (Button)sender;
            var vm = button.DataContext as VirtualMachine;
            if (vm == null)
            {
                return;
            }
            vm.ReleaseResources();
            VirtualMachines.Remove(vm);
            e.Handled = true;
        }

        private void Run_ButtonClick(object sender, RoutedEventArgs e)
        {            
            RealMachine.FullyRunAllPrograms();                
        }

        private void RunUntilInterup_ButtonClick(object sender, RoutedEventArgs e)
        {
            RealMachine.RunVirtualMachinesUntilTimerInterupt();
        }
    }
}
