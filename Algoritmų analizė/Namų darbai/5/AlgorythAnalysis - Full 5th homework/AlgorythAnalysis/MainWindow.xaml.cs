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



        public MainWindow()
        {
            InitializeComponent();
        }

        private int?[] ParseInputToInts(string input)
        {
            var numbers = input.Split(',');
            return numbers.Select(number => (!char.IsLetter(number.TrimStart()[0])) ? (int?)int.Parse(number.Trim()) : null).ToArray();
        }

        private List<List<int?>> parseMatrix(string input)
        {
            List<List<int?>> matrix = new List<List<int?>>();
            foreach (var line in input.Split(new string[] { "\r\n", "\n" }, StringSplitOptions.None))
            {
                if (string.IsNullOrWhiteSpace(line))
                {
                    continue;
                }

                var matrixLine = new List<int?>();
                matrixLine.AddRange(ParseInputToInts(line));
                matrix.Add(matrixLine);
            }
            return matrix;
        }

        private void PrintMatrix(List<List<int?>> matrix, StringBuilder stringBuilder)
        {
            foreach (var line in matrix)
            {
                stringBuilder.AppendLine(string.Concat(line.Select(x => string.Format("{0,5}", (x == null) ? "∞" : x.ToString()))));
            }
            OutputTextBox.Text = stringBuilder.ToString();

        }

        private void FloydMarshallAlgorythm(List<List<int?>> matrix, StringBuilder stringBuilder)
        {
            int n = matrix[0].Count;

            for (var k = 0; k < n; k++)
            {
                for (var j = 0; j < n; j++)
                {
                    for (var i = 0; i < n; i++)
                    {
                        var sum = (matrix[i][k] == null || matrix[k][j] == null) ? null : (matrix[i][k] + matrix[k][j]);
                        if (sum != null)
                        {
                            if (matrix[i][j] == null)
                            {
                                matrix[i][j] = sum;
                            }
                            else
                            {
                                matrix[i][j] = (matrix[i][j] > sum) ? sum : matrix[i][j];
                            }
                        }
                    }
                }
                stringBuilder.AppendLine(new string('-', 5 * 2) + " D(" + (k + 1) + ") " + new string('-', 5 * 2 - 1));
                PrintMatrix(matrix, stringBuilder);
            }
        }



        private string FindShortestRoute(List<List<int?>> A_Matrix, List<List<int?>> D_Matrix, int startPoint, int endPoint)
        {
            int dimen = A_Matrix.Count;
            if (startPoint == endPoint)
            {
                return (startPoint + 1) + "";
            }
            var DstartEnd = D_Matrix[startPoint][endPoint];
            for (int k = 0; k < dimen; k++)
            {

                var AkEnd = A_Matrix[k][endPoint];
                var DStartK = D_Matrix[startPoint][k];
                if (k != endPoint && DStartK != null && AkEnd != null)
                {
                    if (DstartEnd == DStartK + AkEnd)
                    {
                        return FindShortestRoute(A_Matrix, D_Matrix, startPoint, k) + " → " + (endPoint + 1);
                    }
                    continue;
                }
            }
            return "";
        }



        private void RunAlgorythm(string input)
        {
            try
            {
                StringBuilder stringBuilder = new StringBuilder();
                List<List<int?>> D_matrix = parseMatrix(input);
                List<List<int?>> A_matrix = parseMatrix(input);
                int startPoint = int.Parse(IsVirsunesInput.Text) - 1;
                int endPoint = int.Parse(IVirsuneInput.Text) - 1;
                ResultLabel.Content = "Trumpiausias kelias nuo " + (startPoint + 1) + "-os iki " + (endPoint + 1) + "-os viršūnių:";

                foreach (var line in D_matrix)
                {
                    if (line.Count != D_matrix.Count)
                    {
                        MessageBox.Show(this, "Wrong matrix dimensions", "Input error");
                        return;
                    }
                }
                if (startPoint < 0 || startPoint > A_matrix.Count || endPoint < 0 || endPoint > A_matrix.Count)
                {
                    MessageBox.Show(this, "Wrong start or end point", "Input error");
                    return;
                }

                FloydMarshallAlgorythm(D_matrix, stringBuilder);

                stringBuilder.AppendLine();
                stringBuilder.AppendLine("Initial matrix:");
                stringBuilder.AppendLine(new string('-', 5 * 2) + "  A  " + new string('-', 5 * 2));

                ResulTextBlock.Text = FindShortestRoute(A_matrix, D_matrix, startPoint, endPoint);
                PrintMatrix(A_matrix, stringBuilder);
            }
            catch (Exception e)
            {
                MessageBox.Show(this, e.Message, "Error");
            }

        }


        private string[] ParseInputToStrings(string input)
        {
            var strings = input.Split(',');
            return strings.Select(ch => ch.Trim()).ToArray();
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
            RunAlgorythm(MatrixInputTextBox.Text);
            
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

        private void ComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {

            MatrixInputTextBox.Text = ((ComboBoxItem)VariantComboBox.SelectedItem).Tag.ToString();
            IVirsuneInput.Text = "6";
            Button_OnClick(this, null);
        }

        private void AboutWindow_OnClick(object sender, RoutedEventArgs e)
        {
            new About().Show();
        }



        private void RunPrimAndKruskalAlgorithms(string input)
        {

            List<Vertex> vertexes = new List<Vertex>();

           
            var matrix = parseMatrix(input);
            foreach (var line in matrix)
            {
                if (line.Count != matrix.Count)
                {
                    MessageBox.Show(this, "Wrong matrix dimensions", "Input error");
                    return;
                }
            }

            for (int i = 0; i < matrix.Count; i++)
            {
                vertexes.Add(new Vertex() { VertexID = i });
            }

            var edgesList = new List<Edge>();
            for (var i = 0; i < matrix.Count; i++)
            {
                for (var j = i + 1; j < matrix[i].Count; j++)
                {
                    if (i != j)
                    {
                        edgesList.Add(new Edge() { Vertex1 = vertexes[i], Vertex2 = vertexes[j], Cost = matrix[i][j] });
                    }
                }
            }

            var primStringBuilder = new StringBuilder();
            primStringBuilder.AppendLine("-----------  PRIM  -----------");
            PrimmAlgorithm(edgesList, vertexes, primStringBuilder);
            PrimmOutputTextBox.Text = primStringBuilder.ToString();

            var kruskallStringBuilder = new StringBuilder();
            kruskallStringBuilder.AppendLine("------------ KRUSKAL ------------");
            KruskalAlgorithm(edgesList, vertexes, kruskallStringBuilder);
            KruskallOutputTextBox.Text = kruskallStringBuilder.ToString();
            
        }


        private bool visit(Vertex v, Vertex from)
        {
            if (v.Tag.Equals("grey"))
            {
                return false;
            }

            v.Tag = "grey";

            foreach (var child in v.ChildVertexes)
            {
                if (from != null && from.Equals(child))
                {
                    continue;
                }
                if (!child.Tag.Equals("black"))
                {
                    if (!visit(child, v))
                    {
                        return false;
                    }
                }
            }

            v.Tag = "black";
            return true;
        }

        private bool IsACycle(List<Edge> edges, Edge edge)
        {

            var tempList = new List<Edge>();
            tempList.AddRange(edges);
            tempList.Add(edge);
            var initialVertexList = new HashSet<Vertex>();




            foreach (var edgeTemp in tempList)
            {
                var v1inInitial = initialVertexList.Where(x => x.VertexID == edgeTemp.Vertex1.VertexID);

                var newEdge = new Edge() { Cost = edgeTemp.Cost };
                if (v1inInitial.Count() == 0)
                {
                    var vertex1 = new Vertex() { VertexID = edgeTemp.Vertex1.VertexID, Tag = "white" };
                    initialVertexList.Add(vertex1);
                    newEdge.Vertex1 = vertex1;
                    //vertex1.Edges.Add(newEdge);
                }
                else
                {
                    newEdge.Vertex1 = v1inInitial.First();
                    v1inInitial.First().Edges.Add(newEdge);
                }

                var v2inInitial = initialVertexList.Where(x => x.VertexID == edgeTemp.Vertex2.VertexID);


                if (v2inInitial.Count() == 0)
                {
                    var vertex2 = new Vertex() { VertexID = edgeTemp.Vertex2.VertexID, Tag = "white" };
                    initialVertexList.Add(vertex2);
                    newEdge.Vertex2 = vertex2;
                    //vertex2.Edges.Add(newEdge);
                }
                else
                {
                    newEdge.Vertex2 = v2inInitial.First();
                    v2inInitial.First().Edges.Add(newEdge);
                }
            }

            foreach (var v in initialVertexList)
            {
                if (v.Tag.Equals("white") && !visit(v, null))
                {
                    return true;
                }
            }
            return false;
        }

        private List<Edge> GetNearEdges(List<Edge> takenEdges)
        {
            var nearEdges = new List<Edge>();

            foreach (var takenEdge in takenEdges)
            {
                nearEdges.AddRange(takenEdge.Vertex1.Edges);
                nearEdges.AddRange(takenEdge.Vertex2.Edges);
            }
            nearEdges.RemoveAll(x => takenEdges.Any(y => y.Vertex1 == x.Vertex1 && y.Vertex2 == x.Vertex2));
            nearEdges = nearEdges.Distinct().ToList();
            nearEdges.Sort();
            return nearEdges;
        }

        private void PrimmAlgorithm(List<Edge> edges, List<Vertex> vertexes, StringBuilder stringBuilder)
        {

            if (edges.Count <= 0)
            {
                return;
            }
            var takenEdges = new List<Edge>();
            edges.Sort();
            stringBuilder.AppendLine(edges[0].ToString());
            takenEdges.Add(edges[0]);

            while (true)
            {
            label:
                var nearEdges = GetNearEdges(takenEdges);
                if (nearEdges.Count == 0 || takenEdges.Count == vertexes.Count - 1)
                {
                    break;
                }
                foreach (var nearEdge in nearEdges)
                {
                    if (!IsACycle(takenEdges, nearEdge))
                    {
                        stringBuilder.AppendLine(nearEdge.ToString());
                        takenEdges.Add(nearEdge);
                        goto label;
                    }
                    if (takenEdges.Count == vertexes.Count - 1)
                    {
                        break;
                    }
                }
            }

        }

        private void KruskalAlgorithm(List<Edge> edges, List<Vertex> vertexes, StringBuilder stringBuilder)
        {

            var takenEdges = new List<Edge>();
            edges.Sort();


            foreach (var edge in edges)
            {
                if (!IsACycle(takenEdges, edge))
                {
                    stringBuilder.AppendLine(edge.ToString());
                    takenEdges.Add(edge);
                }
            }
        }

        private class Vertex
        {
            public int VertexID { get; set; }
            private List<Edge> edges = new List<Edge>();
            private List<Vertex> childVertexes;
            public string Tag { get; set; }
            public List<Vertex> ChildVertexes
            {
                get
                {
                    var childVertexes = new List<Vertex>();
                    foreach (var edge in Edges)
                    {
                        if (edge.Vertex1.VertexID == this.VertexID)
                        {
                            childVertexes.Add(edge.Vertex2);
                        }
                        else
                        {
                            if (edge.Vertex2.VertexID != this.VertexID)
                            {
                                throw new Exception("ERROR in code, wrong vertex child calcuation");
                            }
                            childVertexes.Add(edge.Vertex1);
                        }

                    }
                    return childVertexes;
                }
                private set { childVertexes = value; }
            }

            public List<Edge> Edges
            {
                get { return edges; }
                private set { edges = value; }
            }
            public override string ToString()
            {
                return "[" + (char)('a'+ VertexID) + "]" + Tag;
            }

            public override bool Equals(object obj)
            {
                if (obj == null)
                {
                    return false;
                }
                var v2 = obj as Vertex;
                if (v2 == null)
                {
                    return false;
                }
                return this.VertexID == v2.VertexID;
            }

            public override int GetHashCode()
            {
                return VertexID % 31;
            }
        }

        private class Edge : IComparable
        {
            private Vertex m_vertex1;

            public Vertex Vertex1
            {
                get { return m_vertex1; }
                set
                {
                    if (!value.Edges.Any(x => x.Equals(this)))
                    {
                        value.Edges.Add(this);
                    }
                    m_vertex1 = value;
                }
            }

            private Vertex m_vertex2;

            public Vertex Vertex2
            {
                get { return m_vertex2; }
                set
                {
                    if (!value.Edges.Any(x => x.Equals(this)))
                    {
                        value.Edges.Add(this);
                    }
                    m_vertex2 = value;
                }
            }


            public override bool Equals(object obj)
            {
                if (obj == null)
                {
                    return false;
                }
                var e2 = obj as Edge;
                if (e2 == null)
                {
                    return false;
                }

                if (e2.Vertex1 == this.Vertex1 && e2.Vertex2 == this.Vertex2 && e2.Cost == this.Cost)
                {
                    return true;
                }

                return false;

            }


            public int? Cost { get; set; }

            public int CompareTo(object obj)
            {
                if (obj == null)
                {
                    return -1;
                }
                var otherEdge = (Edge)obj;
                if (otherEdge.Cost == null)
                {
                    return -1;
                }
                if (Cost == null)
                {
                    return 1;
                }
                return (int)(Cost - otherEdge.Cost);
            }

            public override string ToString()
            {
                return Vertex1 + " <-> " + Vertex2 + " cost:" + ((Cost == null) ? "null" : Cost.ToString());
            }
        }

        private void PrimKruskallButton_OnClick(object sender, RoutedEventArgs e)
        {
            RunPrimAndKruskalAlgorithms(PrimKruskalMatrixInputTextBox.Text);
        }

        private void PrimKruskallComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {

            PrimKruskalMatrixInputTextBox.Text = ((ComboBoxItem)PrimmKruskalVariantComboBox.SelectedItem).Tag.ToString();
            PrimKruskallButton_OnClick(this, null);
        }
    }
}
