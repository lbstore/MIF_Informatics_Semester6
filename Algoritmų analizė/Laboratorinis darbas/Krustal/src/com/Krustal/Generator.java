package com.Krustal;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by lukas on 2016-05-17.
 */
public class Generator {

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("NewFile.txt", "UTF-8");
        Random r = new Random();
        System.out.println("Enter Vertex number");
        Scanner sc = new Scanner(System.in);
        int vertex = sc.nextInt();
        writer.println(vertex);
        System.out.println("Enter how many edges will have every vertex");
        int edge = sc.nextInt();
        writer.println(edge);
        if(edge >= vertex) {
            for (int j = 0; j < (edge / vertex); j++)
                for (int i = 0; i < vertex; i++) {
                    writer.println(i + " " + getRandomNumberInRange(0, vertex - 1) + " " + r.nextDouble());
                }
        }else{
            System.out.println("Sorry not all vertexes are connected");
        }
        writer.close();
    }
}
