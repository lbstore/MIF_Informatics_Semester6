/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrenttesting;

import LibraryLB.Log;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;

/**
 *
 * @author Lemmin
 */
public class AtomicTests {
    public static class Ref{
        public volatile Integer vi;
        public Integer i;
        public AtomicInteger ai;
        public Ref(int init){
            this.ai = new AtomicInteger(init);
            this.i = init;
            this.vi = init;
        }
        @Override
        public String toString(){
            return "i="+i+" vi="+vi+" ai="+ai.get();
        }
    }
    
    public static Thread createAdder(long sleep,String id,int count, long delay,Ref ref){
            Runnable adder = () ->{
                try{
                    Thread.sleep(sleep);
                    for(int i=0; i<count; i++){       
                        ref.ai.getAndIncrement();
                        
                        ref.vi++;
                        ref.i++;
                        Thread.sleep(delay);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(adder);
        }
    public static Thread createReader(long sleep,String id,int count, long delay,Ref ref){
            Runnable adder = () ->{
                try{
                    Thread.sleep(sleep);
                    for(int i=0; i<count; i++){       
                        Log.println(ref);
                        Thread.sleep(delay);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(adder);
        }
  
    public static Thread createAcc(long sleep,String id,int count, long delay,LongAccumulator acc){
            Runnable adder = () ->{
                try{
                    Thread.sleep(sleep);
                    for(int i=0; i<count; i++){       
                        acc.accumulate(1);
                        Thread.sleep(delay);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(adder);
        }
}
