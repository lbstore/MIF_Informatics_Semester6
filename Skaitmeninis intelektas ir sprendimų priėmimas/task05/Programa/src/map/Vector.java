/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Lemmin
 */
public class Vector {
    public ArrayList<Double> weights = new ArrayList<>();
    private static Random rnd = new Random();
    
    
    public static ArrayList<Vector> prepareData(double[][] data) throws Exception{
        ArrayList<Vector> vectors = new ArrayList<>();
        int vectorLength = data[0].length;
        for(double[] array:data){
            if(vectorLength != array.length){
                throw new Exception("Jagged arrays");
            }
            vectors.add(Vector.asVector(array));
        }
        return vectors;
    }
    
    public static Vector getWithValue(int desiredSize, Double val){
        Vector v = new Vector();
        for(int i = 0; i < desiredSize; i++){
            v.weights.add(val);
        }
        return v;
    }
    
    public static Vector getRandom(int desiredSize){
        Vector v = new Vector();
        for(int i = 0; i < desiredSize; i++){
            v.weights.add(rnd.nextDouble());
        }
        return v;
    }
    
    public static Vector setRandom(Vector v){
        for(int i = 0; i < v.size(); i++){
            v.weights.set(i, rnd.nextDouble());
        }
        return v;
    }
    
    public static Vector asVector(double[] data){
        Vector v = new Vector();
        for(int i = 0; i < data.length; i++){
            v.weights.add(data[i]);
        }
        return v;
    }
    
    public static Vector[] minmax(ArrayList<Vector> vectors){
        Vector min = vectors.get(0).clone();
        Vector max = min.clone();
        int dim = max.size();
        for(Vector v:vectors){
            for(int i = 0; i < dim; i++){
                min.weights.set(i, Math.min(min.get(i),v.get(i)));
                max.weights.set(i, Math.max(max.get(i),v.get(i)));
            }
        }
        Vector[] data = new Vector[2];
        data[0] = min;
        data[1] = max;
        return data;
    }

    @Override
    public Vector clone(){
        Vector clone = new Vector();
        clone.weights.addAll(this.weights);
        return clone;
    }
    
    public void normalize(Vector globalMin, Vector globalMax){
        double[] data = new double[globalMax.size()];
        for(int i = 0; i < data.length; i++){
            data[i] = (this.get(i) - globalMin.get(i)) / (globalMax.get(i) - globalMin.get(i));
        }
        this.weights = Vector.asVector(data).weights;
    }
    
    public void denormalize(Vector globalMin, Vector globalMax){
        double[] data = new double[globalMax.size()];
        for(int i = 0; i < data.length; i++){
            data[i] = this.get(i) * (globalMax.get(i) * globalMin.get(i)) + globalMin.get(i);
        }
        this.weights = Vector.asVector(data).weights;
    }
    
    public Vector normalized(Vector globalMin, Vector globalMax){
        Vector n = this.clone();
        n.normalize(globalMin, globalMax);
        return n;
    }
    
    public Vector denormalized(Vector globalMin, Vector globalMax){
        Vector n = this.clone();
        n.denormalize(globalMin, globalMax);
        return n;
    }
     
    public int size(){
        return weights.size();
    }
    
    public Double get(int i){
        return weights.get(i);
    }
    
    @Override
    public String toString(){
        if(this.weights == null){
            return "null";
        }
        return this.weights.toString();
    }
}
