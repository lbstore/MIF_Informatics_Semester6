package com.Krustal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by lukas on 2016-05-16.
 */

public class AAlgorithm {

    int V, E;
    Edge edgeArray[];

    public AAlgorithm(int v, int e) {
        V = v;
        E = e;
        edgeArray = new Edge[E];
        for(int i = 0; i < E; i++){
            edgeArray[i] = new Edge();
        }
    }

    class Edge {
        int start, destination;
        double weight;
    }


    void Kruskal(){
        Edge result[] = new Edge[V];
        int labels[] = new int[V];
        int i, resultIncrease = 0;
        for (i = 0; i < V; i++){labels[i] = i+1;}
        quicksort(edgeArray, 0, E-1);
        for (i = 0; i < E; i++){
            int labelStart = labels[edgeArray[i].start];
            int labelDestination = labels[edgeArray[i].destination];
            if (labelStart != labelDestination){
                result[resultIncrease] = edgeArray[i];
                resultIncrease += 1;
                if(labelStart < labelDestination){
                    labels[edgeArray[i].destination] = labelStart;
                    for(int j = 0; j < V; j++){
                        if(labels[j] == labelDestination){labels[j] = labelStart;}
                    }
                }
                else{
                    labels[edgeArray[i].start] = labelDestination;
                    for(int j = 0; j < V; j++){
                        if(labels[j] == labelStart){labels[j] = labelDestination;}
                    }
                }
            }
        }
        double fullWeight = 0;
        for (i = 0; i < resultIncrease; i++){
            fullWeight += result[i].weight;
        }
        System.out.println("Weight full " + fullWeight);
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



    public static void main(String [] args) {
        double startTime = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader("NewFile.txt"))) {
            String line = br.readLine();
            int vertexNumber = Integer.parseInt(line);
            line = br.readLine();
            int edgeNumber = Integer.parseInt(line);
            line = br.readLine();
            AAlgorithm aAlgorithm = new AAlgorithm(vertexNumber,edgeNumber);
            int i = 0;
            while(line != null){
                String[] tmp = line.split("\\s+");
                aAlgorithm.edgeArray[i].start = Integer.parseInt(tmp[0]);
                aAlgorithm.edgeArray[i].destination = Integer.parseInt(tmp[1]);
                aAlgorithm.edgeArray[i].weight = Double.parseDouble(tmp[2]);
                i++;
                line = br.readLine();
            }
            aAlgorithm.Kruskal();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Time spent " + (System.currentTimeMillis() - startTime) +" ms");
    }
}
