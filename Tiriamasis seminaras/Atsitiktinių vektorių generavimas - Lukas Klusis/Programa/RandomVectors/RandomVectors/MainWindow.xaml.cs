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
//using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using MathNet.Numerics.LinearAlgebra;
using MathNet.Numerics.LinearAlgebra.Double;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;


namespace RandomVectors
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window, INotifyPropertyChanged
    {

        private Random random = new Random(0);
        private Matrix<double> T;
        private Matrix<double> R;


        private double[,] DummyMatrix;
        private double[,] matrix;
        private double[,] Matrix
        {
            get
            {
                return matrix;
            }
            set
            {
                matrix = value;
                OnPropertyChanged();
            }
        }

        private Queue<double> NormaliujuDydziuEile = new Queue<double>();
        public MainWindow()
        {
            InitializeComponent();
            this.DataContext = this;

            MatrixGrid.CellEditEnding += MatrixGrid_CellEditEnding;
            MatrixGrid.CurrentCellChanged += MatrixGrid_CurrentCellChanged;
            MatrixGrid.Visibility = System.Windows.Visibility.Hidden;
            this.Loaded += MainWindow_Loaded;

        }

        void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            CreateMatrixGrid();
        }




        public double GeneruotiNormalujiDydi()
        {
            //Tegul, kaip visada, U1 ir U2  nepriklausomi ir tolygiai pasiskirstę intervale
            //[0, 1) atsitiktiniai dydžiai. Tuomet atsitiktiniai dydžiai V1 = 2U1 − 1 ir V2 =
            //2U2 − 1 yra nepriklausomi ir tolygiai pasiskirstę intervale [−1, 1).
            //Pažymėkime S = V1^2 + V2^2;
            if (NormaliujuDydziuEile.Count() > 0)
            {
                return NormaliujuDydziuEile.Dequeue();
            }

            double S, V1, V2;

            do
            {
                var U1 = random.NextDouble();
                var U2 = random.NextDouble();

                V1 = 2 * U1 - 1;
                V2 = 2 * U2 - 1;
                S = V1 * V1 + V2 * V2;

            } while (S >= 1);

            var bendrasDaugiklis = Math.Sqrt((-2 * Math.Log(S)) / S);
            var X1 = V1 * bendrasDaugiklis;
            var X2 = V2 * bendrasDaugiklis;

            NormaliujuDydziuEile.Enqueue(X2);
            return X1;
        }

        private Vector<double> GeneruotiNepriklausomaVektoriu(int dydis)
        {
            var normalusDydziai = new double[dydis];

            for (int i = 0; i < dydis; i++)
            {
                normalusDydziai[i] = GeneruotiNormalujiDydi();
            }
            return MathNet.Numerics.LinearAlgebra.Double.DenseVector.OfArray(normalusDydziai);
        }


        private Matrix<double> SkaiciuotiT(Matrix<double> R)
        {
            var n = R.ColumnCount;
            var T = new double[n, n];

            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (i < j)
                    {
                        T[i, j] = 0;
                    }

                    if (j == 0)
                    {
                        T[i, 0] = R[i, 0] / Math.Sqrt(R[0, 0]);
                    }

                    if (i == j && i != 0)
                    {
                        double sum = 0;
                        for (int k = 0; k < i - 1; k++)
                        {
                            sum += T[i, k] * T[i, k];
                        }
                        T[i, i] = Math.Sqrt(R[i, i] - sum);
                    }

                    if (j > 0 && j < i)
                    {
                        double sum = 0;
                        for (int k = 0; k < j - 1; k++)
                        {
                            sum += T[i, k] * T[j, k];
                        }
                        T[i, j] = (R[i, j] - sum) / T[j, j];
                    }
                }
            }

            return DenseMatrix.OfArray(T);
        }

        public Vector<double> GeneruotiNormalujiVektoriu()
        {                      
            return T.Multiply(GeneruotiNepriklausomaVektoriu(R.ColumnCount));
        }


        private bool hasChanged = false;
        void MatrixGrid_CellEditEnding(object sender, DataGridCellEditEndingEventArgs e)
        {
            try
            {
                var x = int.Parse(e.Column.Header.ToString());
                var y = int.Parse(e.Row.Header.ToString());
                var value = double.Parse(((TextBox)e.EditingElement).Text);
                Matrix[x, y] = value;
                Matrix[y, x] = value;

                if (x != y)
                {
                    hasChanged = true;
                }
            }
            catch (FormatException ex)
            {
            }
        }

        void MatrixGrid_CurrentCellChanged(object sender, EventArgs e)
        {
            if (hasChanged)
            {
                MatrixGrid.ItemsSource2D = DummyMatrix;
                MatrixGrid.ItemsSource2D = Matrix;
                hasChanged = false;
            }

        }

        private void CreateMatrixGrid()
        {
            try
            {
                int vertexCount = int.Parse(MatrixDimensionsInput.Text);
                DummyMatrix = new double[vertexCount, vertexCount];
                Matrix = new double[vertexCount, vertexCount];

                for (int i = 0; i < vertexCount; i++)
                {
                    Matrix[i, i] = i + 1;
                }

                MatrixGrid.ItemsSource2D = Matrix;

                MatrixGrid.Visibility = System.Windows.Visibility.Visible;
            }
            catch (FormatException ex)
            {
                MatrixDimensionsInput.Text = "";
                MessageBox.Show("Number must be integer", "Error");
            }
        }

        private void MatrixDimensionInput_KeyUp(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return)
            {
                CreateMatrixGrid();
            }
        }

        private void GenerateVectors_Click(object sender, RoutedEventArgs e)
        {
            if (Matrix == null)
            {
                return;
            }
            try
            {
                RoundingPrecise = int.Parse(DecimalNumbersCount.Text);
            }
            catch (FormatException)
            {
                MessageBox.Show(this, "Klaida", "Skaitmenų po kablelio skaičius turi būti sveikasis skaičius");
            }

            try
            {
                VectorsCountInt = int.Parse(VectorsCount.Text);
            }
            catch (FormatException)
            {
                MessageBox.Show(this, "Klaida", "Generuojamų vektorių skaičius turi būti sveikasis skaičius");
            }

            R = DenseMatrix.OfArray(Matrix);

            //R turi būti simetriška ir jos determinantas turi būti teigiamai apibrėžtas
            if (!R.IsSymmetric() || R.Determinant() <= 0)
            {
                MessageBox.Show(this, "Nurodyta matrica turi būti teigiamai apibrėžta ir simetriška", "Klaida");
                return;
            }
            T = SkaiciuotiT(R);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < VectorsCountInt; i++)
            {
                var vektorius = GeneruotiNormalujiVektoriu();
                
                stringBuilder.Append("<");

                for(int j = 0; j < vektorius.Count; j++){
                    stringBuilder.Append(vektorius[j].ToString("0." + new string('#', RoundingPrecise)) + ((j == (vektorius.Count - 1)) ? ">" : ", "));
                }
                stringBuilder.AppendLine();
                
            }

            GeneratedVectorsTextBlock.Text = stringBuilder.ToString();
        }

        public event PropertyChangedEventHandler PropertyChanged;
        private int RoundingPrecise;
        private int VectorsCountInt;

        private void OnPropertyChanged([CallerMemberName]string caller = null)
        {
            // make sure only to call this if the value actually changes

            var handler = PropertyChanged;
            if (handler != null)
            {
                handler(this, new PropertyChangedEventArgs(caller));
            }
        }

        private void RestartRandomCalculate_Click(object sender, RoutedEventArgs e)
        {
            random = new Random(0);
        }
    }
}
