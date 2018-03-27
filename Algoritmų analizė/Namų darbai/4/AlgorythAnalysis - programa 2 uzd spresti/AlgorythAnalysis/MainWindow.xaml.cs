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

        private void ResetStartingPoint()
        {            
            stringBuilder.Clear();
        }
       

        public MainWindow()
        {

            InitializeComponent();
        }

        private int[] ParseInputToInts(string input)
        {
            var numbers = input.Split(',');
            return numbers.Select(number => int.Parse(number.Trim())).ToArray();
        }


        private string[] ParseInputToStrings(string input)
        {
            var strings = input.Split(',');
            return strings.Select(ch => ch.Trim()).ToArray();
        }

        private void KnapsackPacking(int[] sizes, int[] values, string[] names, int M, int N, out int[] cost, out int?[] best)
        {          
            cost = new int[M+1];
            best = new int?[M+1];

            var headerLine = "i:\t" + string.Concat(Enumerable.Range(1, M).Select(x => string.Format("{0}\t", x)));
            stringBuilder.AppendLine(headerLine);
            
            for (int j = 1; j <= N; j++)
            {
                
                for (int i = 1; i <= M; i++)
                {
                    if (i - sizes[j] >= 0)
                    {
                        if (cost[i] < cost[i - sizes[j]] + values[j])
                        {
                            cost[i] = cost[i - sizes[j]] + values[j];
                            best[i] = j;
                        }
                    }
                }
                stringBuilder.AppendLine(new String('-',M*10));
                stringBuilder.AppendLine("cost[i]:\t" + string.Concat(cost.Skip(1).Select(x => string.Format("{0}\t", x))));
                stringBuilder.AppendLine("best[i]:\t" + string.Concat(best.Skip(1).Select(x => string.Format("{0}\t", x))));
                
            }
            stringBuilder.AppendLine("names[i]:\t " + string.Concat(best.Skip(1).Select(x => String.Format("{0}\t", (x!=null)?names[(int)x]:null) )));
        }

        private void RunAlgorythm(string nameInput, string sizeInput, string valueInput, string mInput)
        {
            if (string.IsNullOrWhiteSpace(nameInput))
            {
                MessageBox.Show("Neteisingai įvesta");
                return;
            }

            if (string.IsNullOrWhiteSpace(sizeInput))
            {
                MessageBox.Show("Neteisingai įvesta");
                return;
            }
            if (string.IsNullOrWhiteSpace(valueInput))
            {
                MessageBox.Show("Neteisingai įvesta");
                return;
            }
            if (string.IsNullOrWhiteSpace(mInput))
            {
                MessageBox.Show("Neteisingai įvesta");
                return;
            }
            try
            {
                
                var names = ParseInputToStrings(nameInput);
                var sizes = ParseInputToInts(sizeInput);
                var values = ParseInputToInts(valueInput);
                var M = int.Parse(mInput.Trim());
                
                if (names.Length != sizes.Length || sizes.Length != values.Length)
                {
                    MessageBox.Show("Neteisingai įvesta, masyvų ilgiai turi būti vienodi");
                    return;
                }

                var N = names.Length;
                int[] temp = { 0 };
                values = temp.Concat(values).ToArray();
                int[] temp2 = { 0 };
                sizes = temp2.Concat(sizes).ToArray();
                string[] temp3 = { "" };
                names = temp3.Concat(names).ToArray();

                

                ResetStartingPoint();
                OutputTextBox.Text = stringBuilder.ToString();
                int[] cost;
                int?[] best;
                KnapsackPacking(sizes, values, names, M, N, out cost, out best);
                OutputTextBox.Text = stringBuilder.ToString();
           

                int size = M;
                var backpack = new List<string>();
                var totalSize = 0;
                var totalValue = 0;
                while (size > 0)
                {
                    if (best[size].HasValue)
                    {
                        var currentItemIndex = (int)best[size];
                        backpack.Add(names[currentItemIndex]);
                        size -= sizes[currentItemIndex];
                        totalSize += sizes[currentItemIndex];
                        totalValue += values[currentItemIndex];
                    }
                    else
                    {
                        break;
                    }                                        
                }
                backpack.Reverse();
                ResulTextBlock.Text = string.Join(", ", backpack) + " kuprinės vertė (VALUE): " + totalValue + ", dydis (SIZE): " + totalSize;
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
                Button_OnClick(this, null);
            }
        }

        private void Button_OnClick(object sender, RoutedEventArgs e)
        {
            RunAlgorythm(NameInputTextBox.Text, SizeInputTextBox.Text, ValueInputTextBox.Text, MInputTextBox.Text);
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
