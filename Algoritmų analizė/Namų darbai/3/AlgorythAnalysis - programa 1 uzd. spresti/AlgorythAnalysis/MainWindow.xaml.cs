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

namespace AlgorythAnalysis
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        StringBuilder stringBuilder = new StringBuilder();

        private int counter = 0;


        private void ResetStartingPoint()
        {
            counter = 0;
            stringBuilder.Clear();
        }
        private int[] Merge2(int[] A, int[] B)
        {
            var n = A.Length;
            var m = B.Length;
            var i = 1;
            var j = 1;
            var C = new int[n + m];
            for (int k = 1; k <= m + n; k++)
            {
                if (i <= n)
                {
                    if (j <= m)
                    {
                        counter++;
                        stringBuilder.AppendLine(counter + ") " + A[i - 1] + " < " + B[j - 1]);
                        
                        if (A[i-1] < B[j-1])
                        {
                            C[k-1] = A[i-1];
                            i++;
                        }
                        else
                        {
                            C[k-1] = B[j-1];
                            j++;
                        }
                    }
                    else
                    {
                        C[k-1] = A[i-1];
                        i++;
                    }
                }
                else
                {
                    C[k-1] = B[j-1];
                    j++;
                }
            }
            return C;
        }

        private int[] MergeSort(int[] A)
        {
            if (A.Length == 1)
            {
                return A;
            }
            else
            {
                var half1 = A.Take(A.Length/2).ToArray();
                var half2 = A.Skip(A.Length / 2).ToArray();
                return Merge2(MergeSort(half1), MergeSort(half2));
            }
        }
        
        public int[] A { get; set; }

        public MainWindow()
        {
            //A = new int[] {95, 76, 84, 23, 56, 42, 31, 5};
            //A = new int[] {12,97,16,34,97,48,76,8};
            //MergeSort(A);
            InitializeComponent();
        }

        private int[] ParseInput(string input)
        {
            var numbers = input.Split(',');
            return numbers.Select(number => int.Parse(number.Trim())).ToArray();
        }


        private void RunAlgorythm(string input)
        {
            if (string.IsNullOrWhiteSpace(input))
            {
                MessageBox.Show("Neteisingai įvesta");
                return;
            }
            try
            {
                var A = ParseInput(input);
                ResetStartingPoint();
                var result = MergeSort(A);
                OutputTextBox.Text = stringBuilder.ToString();
                ResulTextBlock.Text = string.Join(", ", result);
            }
            catch (Exception e)
            {
                MessageBox.Show("Exception: " + e.Message);
            }


        }

        private void RetunButtonPressed(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return)
            {
                RunAlgorythm(InputTextBox.Text);
            }
        }

        private void Button_OnClick(object sender, RoutedEventArgs e)
        {
            RunAlgorythm(InputTextBox.Text);
        }

        private void LinkToTeachersPage_OnClick(object sender, RoutedEventArgs e)
        {
            try
            {
                System.Diagnostics.Process.Start("http://uosis.mif.vu.lt/~valdas/ALGORITMAI/Namu_darbai/");

            }
            catch (Exception ex)
            {
                MessageBox.Show("Exception: " + ex.Message);
            }
        }

        private void AboutWindow_OnClick(object sender, RoutedEventArgs e)
        {
            new About().Show();
        }
    }
}
