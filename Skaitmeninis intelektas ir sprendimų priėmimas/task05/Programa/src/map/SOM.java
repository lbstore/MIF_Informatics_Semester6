/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Lemmin
 */
public class SOM {
    public int iteration = 0;
    public int dim;     //dimension  
    public int nodes;   //neuron amount
    public double decay = 1;
    public double decayRate = 0.99; //decay lowers by 1%
    public HashMap<Integer,Vector> vectors = new HashMap<>();
    public boolean single = false; //only modify winning node
    public boolean trace = false; //print progress
    public int gridSize = -1;
    public static Random rnd = new Random();
    
    public SOM(int dimension, int nodeCount){
        int potGridSize = (int)Math.floor(Math.sqrt(nodeCount));
        if(nodeCount == potGridSize*potGridSize){
            this.gridSize = potGridSize;
        }
        this.nodes = nodeCount;
        this.dim = dimension;
        for(int i = 0; i < nodeCount; i++){
            vectors.put(i,Vector.getRandom(dimension));
//            vectors.put(i, Vector.getWithValue(dimension, 0.5d));
        } 
    }
    
    // find neighbours in grid formation 4-connectivity
    public int resolve(int index, int type, int gridSize){
        int result = -1;
        switch(type){
            case 0://top
                result = index - gridSize;
                break;
            case 1://bot
                result = index + gridSize;
                break;
            case 2://left
                result = index - 1;
                break;
            case 3://right
                result = index + 1;
                break;
        }
        if(result < 0 || result >= this.nodes){
            return -1;
        }
        return result;  
    }    
    
    //euclidean distance
    public double distance(Vector input, Vector node){
        double sum = 0;
        for(int i = 0; i < input.size(); i++){
            sum += Math.pow(input.get(i) - node.get(i), 2);
        }
        return Math.sqrt(sum);
    }
    
    //use all inputs
    public void trainWithAll(List<Vector> input){
        for(Vector v:input){
            train(v);
        }
    }
    
    //use 1 random input
    public void trainPick1(List<Vector> input){
        int nextInt = rnd.nextInt(input.size());
        train(input.get(nextInt));
    }
    
    public void debug(String s){
        if(trace){
            System.out.println(s);
        }
    }
    
    //"bread and butter" train SOM with selected Vector
    public void train(Vector input){
        decay *= decayRate;
        iteration++;
        double smallestDistance = Double.MAX_VALUE;
        int smallestIndex = 0;
        //find winner
        for(int j = 0; j < this.nodes; j++){
            double tryNewDist = this.distance(input, this.vectors.get(j));
            if(smallestDistance > tryNewDist){
                smallestIndex = j;
                smallestDistance = tryNewDist;
            }
        }
        String t = "\nIteration:"+this.iteration+" decay:"+this.decay + " winner:" +smallestIndex;
        // update winner
        Vector winnerNode = this.vectors.get(smallestIndex);
        updateWeights(winnerNode,input,1);
        
        // update the rest
        if(!single){
            if(gridSize <= 0){
                for(Vector v:resolveNeighbours(smallestIndex)){
                    updateWeights(v,input,0.9);
                }  
            }
            else{
                HashSet<Integer> set = new HashSet<>();
                set.add(smallestIndex);
                recursiveResolve(set,smallestIndex,(int) Math.round(decay * this.gridSize));
                set.remove(smallestIndex);
                
                t += "\n"+smallestIndex + " Update set"+set.toString();
                
                for(Integer i:set){
                    int dist = this.manhattanDistance(i, smallestIndex, this.gridSize);
                    if(dist > 0 && dist < this.gridSize){
                        double distMult = 1;
                        distMult = distMult / (double)dist;
                        t+="\n"+i+" Top dist:"+dist +" Dist multi:"+distMult;
                        updateWeights(vectors.get(i),input,distMult);
                    }   
                }
            } 
        }
        if(this.trace){
            for(Vector v:this.vectors.values()){
                t += "\n"+v.toString();
            }
        }
        debug(t);  
    }
    
    //Get cluster id on specified input
    public int test(Vector input){
        int smallestIndex = 0;
        double minValue = Double.MAX_VALUE;
        for(Map.Entry<Integer, Vector> v :vectors.entrySet()){
            double tryNewDist = distance(input,v.getValue());
            if(minValue > tryNewDist){
                smallestIndex = v.getKey();
                minValue = tryNewDist;
            }
        }
        return smallestIndex;
    }
    
    //find 2 best matching units and test if they are connected
    public boolean BMUisConnected(Vector input){
        int firstBMU = -1;
        int secondBMU = -1;
        double first = Double.MAX_VALUE;
        double second = first;
        for(Map.Entry<Integer, Vector> v :vectors.entrySet()){
            double tryNewDist = distance(input,v.getValue());
            if((secondBMU >= 0) && (second > tryNewDist)){
                second = tryNewDist;
                secondBMU = v.getKey();
            }
            if(first > tryNewDist){
                secondBMU = firstBMU;
                firstBMU = v.getKey();
                second = first;
                first = tryNewDist;
            }
        }
        
        int topDist = manhattanDistance(firstBMU,secondBMU,this.gridSize);
        return topDist <= 1;
        
    }
    
    public int manhattanDistance(int first,int second, int gridSize){
        if(first > second){//ensure first <= second
            int t = first;
            first = second;
            second = t;
        }
        int y = 0;
        int x = 0;
        while(first + gridSize <= second){
            first = first + gridSize;
            y++;
        }
        while(first < second){
            first++;
            x++;
        }
        return x + y;  
    }
    
    // get neighbours of selected index and propagate recursively set ammount of iteration
    public void recursiveResolve(HashSet<Integer> set,int index, int iteration){
        if(iteration <= 0){
            return;
        }
        ArrayDeque<Integer> thisIteration = new ArrayDeque<>();
        for(int i = 0; i < 4; i++){
            int n = resolve(index,i,this.gridSize);
            if(n >= 0){
                if(!set.contains(n)){
                    thisIteration.add(n);
                }
            }
        }
        set.addAll(thisIteration);
        for(Integer i:thisIteration){
            recursiveResolve(set,i,iteration - 1);
        }
    }
    
    // unly used if no grid structure has been found
    public ArrayList<Vector> resolveNeighbours(int index){
        ArrayList<Vector> list = new ArrayList<>();
        int size = this.vectors.size();
        int last = size - 1;
        if(size < 2){
            return list;
        }else{
            if(index == 0){
                list.add(this.vectors.get(index + 1));
            }
            else if(index == last){
                list.add(vectors.get(last - 1));
            }
            else{
                list.add(vectors.get(index - 1));
                list.add(vectors.get(index + 1));
            } 
        }
        return list;
    }
    
    //update all weights relative to selected input
    public void updateWeights(Vector node,Vector input,double distanceMultiplier){
        for(int j = 0; j < this.dim; j++){
            Double w = node.get(j);
            double diff = distanceMultiplier * decay * (input.get(j) - w);
            w += diff;
            node.weights.set(j, w);
        }
    }
    
    public double quantizationError(List<Vector> input){
        double sum = 0;
        for(Vector v:input){
            sum += distance(this.vectors.get(this.test(v)),v);
        }
        return sum / (double) input.size();
    }
    
    public double topologicalError(List<Vector> input){
        double sum = 0;
        for(Vector v:input){
            if(!BMUisConnected(v)){
                sum += 1;
            }
        }
        return sum / (double)input.size();
    }
    
    
    // use for making SOM grid
    public static class ClassHitCounter{
        public int classCount;
        public HashMap<Integer,HashMap<Integer,Integer>> hitCount = new HashMap<>();
        public ClassHitCounter(int classCount){
            this.classCount = classCount;
        }
        public void hit(int node, int _class){
            if(!hitCount.containsKey(node)){
                hitCount.put(node, new HashMap<Integer,Integer>());
            }
            HashMap<Integer, Integer> classes = hitCount.get(node);
            if(!classes.containsKey(_class)){
                classes.put(_class, 0);
            }
            Integer res = classes.get(_class) + 1;
            classes.put(_class, res);
        }
        public Integer resolveClass(int node){
            HashMap<Integer, Integer> get = hitCount.get(node);
            if(get == null){
                return null;
            }else{
                //find max hit count of selected node
                int maxHits = 0;
                int classID = -1;
                for(Map.Entry<Integer, Integer> val:get.entrySet()){
                    if(val.getValue() > maxHits){
                        classID = val.getKey();
                    }
                }
                return classID;
            }
        }
    }
}
