package task01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Laimonas Beniušis 
 * VU MIF Kompiuterių Mokslas 1, 1 grupė
 */
public class executable {
/*
    Programos aprašymas:
    Masyvo A elementas N, iteracijos metu tampa A[N] = A[N] + A[N-1]
    Todėl pirmasis elementas nesikeičia, o toliau esantys elementai
    priklausomi nuo prieš tai buvusių reikšmių
    Naudoti demo(sinchronizuotas) rezultatams gauti
        
*/
    public static int arraySize  = 6; // >1
    public static int threadCount = 10; //Iteracijų skaičius
    public static class Command implements Runnable{
        AtomicIntegerArray array;
        boolean synchronize;
        public Command(AtomicIntegerArray arr,boolean synch){
            this.array = arr;
            this.synchronize = synch;
        }
        @Override
        public void run() {
            //Kritinė situacija
//            modify(array);
            if(this.synchronize){
                synchronized(array){
                    modify(array);
                }
            }else{
                modify(array);
            }
        }
        private void modify(AtomicIntegerArray ar){
            for(int i = 0; i<ar.length()-1; i++){
                ar.set(i+1, ar.get(i+1)+ar.get(i));
//                ar[i+1] += ar[i];
                
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {        
        print(args);
        if(args.length>0){
            try{
                arraySize = Integer.parseInt(args[0]);
                threadCount = Integer.parseInt(args[1]);
                print(demo(Boolean.parseBoolean(args[2])));
            }catch(Exception e){
                print("Usage:","[arraySize],[threadCount],[synchronized]");
                
                print(demo(true));
                print(demo(false));
                
            }
        }else{
            arraySize = 8;
            threadCount = 12;
            interestingOverflow();
        }
//        for(int i=0; i<1000; i++){
//            print(demo(false));
//        } 
        
        
    }
    public static Integer[] getDifferences(Integer[] ar1, Integer[] ar2){
       
        Integer[] differences  = new Integer[arraySize];
              
        for(int i=0; i<arraySize; i++){
            differences[i] = ar1[i] - ar2[i];
        }
        return differences;
    }
    public static void interestingOverflow() throws InterruptedException{
        Integer[] nonSync;
        Integer[] sync;
        Integer[] diff = new Integer[arraySize];
        Integer times = 0;
        boolean end = false;
        do{
            sync = demo(true);
            nonSync = demo(false);
            
            for(int i=0; i<arraySize; i++){
                diff[i] = sync[i] - nonSync[i];
                if(diff[i]<0){
                    end = true;
                }
            }
            times++;
//            print(sync);
//            print(diff);
            
            if(times>10000){
                end=true;
            }
        }while(!end); 
        print("Overflow happened at",times);
        print(sync);
        print(nonSync);
    }
    public static int[] populateArray(){
//        ArrayList<Integer> list = new ArrayList<>();
        int[] arr = new int[arraySize];
        for(int i = 0; i<arraySize; i++){
            arr[i] = 1;
        }
        
        return arr;
    }
    public static void print(Object... o){
        for(Object ob:o){
            System.out.print(ob + "\t");        
        }
        System.out.println();
    }
    public static Integer[] demo(boolean s) throws InterruptedException{
        AtomicIntegerArray array = new AtomicIntegerArray(populateArray());
//        print("Initial",Arrays.asList(array),"Synchronized="+s);
        List<Thread> threads = new ArrayList<>(threadCount);
        for(int i=0; i<threadCount; i++){
            Thread t = new Thread(new Command(array,s));
            threads.add(t);
        }
        //Startuojam
        threads.forEach(thread ->{
            thread.start();
            
        });
         threads.forEach(thread ->{
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(executable.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        //Laukiam kol pasibaigs
        
//        while(Thread.activeCount()>1){}
        Integer[] ar = new Integer[array.length()];
        for(int i=0; i<array.length(); i++){
            ar[i] = array.get(i);
        }
        return ar;
        
    }
    
}
