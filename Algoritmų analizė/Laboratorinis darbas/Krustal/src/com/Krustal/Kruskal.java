package com.Krustal;

import java.util.*;
import java.lang.*;
import java.io.*;

public class Kruskal{

    int V, E;
    Edge edge[];

    Kruskal(int v, int e){
        V = v;
        E = e;
        edge = new Edge[E];
        for (int i = 0; i < e; i++) {
            edge[i] = new Edge();
        }
    }

    class Vertex{
        int parent, rank;
    };

    class Edge {
        int start, destination;
        double weight;
    };

    void Union(Vertex [] vertexSet, int u, int v){
        if(vertexSet[u].rank == vertexSet[v].rank){
            vertexSet[v].parent = u;
            vertexSet[u].rank++;
        }
        else if(vertexSet[u].rank < vertexSet[v].rank){
            vertexSet[v].parent = u;
        }
        else{
            vertexSet[u].parent = v;
        }
    }

    void KruskalAlgorithm(){
        Edge result[] = new Edge[V];
        int i;
        int e = 0;
        for(i = 0; i < V; i++){
            result[i] = new Edge();
        }
        quicksort(edge, 0, E - 1);
        Vertex[] vertexSet = new Vertex[V];
        for (i = 0; i < V; i++) {
            vertexSet[i] = new Vertex();
            vertexSet[i].parent = i;
            vertexSet[i].rank = 0;
        }
        i = 0;
        while(e < V - 1){
            Edge nextEdge;
            nextEdge = edge[i++];
            int u = findSet(vertexSet, nextEdge.start);
            int v = findSet(vertexSet, nextEdge.destination);
            if (u != v){
                result[e++] = nextEdge;
                Union(vertexSet, u, v);
            }
        }
        double fullWeight = 0;
        for (i = 0; i < e; i++) {
//            System.out.println(result[i].start + " - " + result[i].destination+
//                    " weight " + result[i].weight);
            fullWeight += result[i].weight;
        }
        System.out.println("Result weight " + fullWeight);
    }


    int findSet(Vertex v[], int i){
        if (v[i].parent != i)
            v[i].parent = findSet(v, v[i].parent);
        return v[i].parent;
    }


    public void quicksort(Edge[] edges, int start, int end) {
        if (start < end) {
            swap(edges, end, start + (end - start) / 2);
            int pIndex = pivot(edges, start, end);
            quicksort(edges, start, pIndex - 1);
            quicksort(edges, pIndex + 1, end);
        }
    }

    public int pivot(Edge[] edges, int start, int end) {
        int pIndex = start;
        Edge pivot = edges[end];
        for (int i = start; i < end; i++) {
            if (edges[i].weight < pivot.weight) {
                swap(edges, i, pIndex);
                pIndex++;
            }
        }
        swap(edges, end, pIndex);
        return pIndex;
    }

    public void swap(Edge[] edges, int index1, int index2) {
        Edge temp = edges[index1];
        edges[index1] = edges[index2];
        edges[index2] = temp;
    }


    public static void main(String[] args) {
            double startTime = System.currentTimeMillis();
            try (BufferedReader br = new BufferedReader(new FileReader("NewFile.txt"))) {
                String line = br.readLine();
                int vertex = Integer.parseInt(line);
                line = br.readLine();
                int edge = Integer.parseInt(line);
                line = br.readLine();

                Kruskal graph = new Kruskal(vertex, edge);

                int i = 0;
                while (line != null) {
                    String[] tmp = line.split("\\s+");
                    graph.edge[i].start = Integer.parseInt(tmp[0]);
                    graph.edge[i].destination = Integer.parseInt(tmp[1]);
                    graph.edge[i].weight = Double.parseDouble(tmp[2]);
                    i++;
                    line = br.readLine();
                }
                graph.KruskalAlgorithm();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Time took " + (System.currentTimeMillis() - startTime) + " ms");
    }
}