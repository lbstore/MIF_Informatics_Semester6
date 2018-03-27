/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrenttesting;

import LibraryLB.Log;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 *
 * @author Lemmin
 */
public class BlockingQueueTest {

    public static class DelayedElement implements Delayed{
           private Long initTime;
           private Long delay;
           private Object data;
           public DelayedElement(long delay,Object data){
               this.delay = delay;
               this.data = data;
               this.initTime = Instant.now().toEpochMilli();
           }
           @Override
           public long getDelay(TimeUnit tu) {
               Long timeLeft = Instant.now().toEpochMilli()- initTime;
               return tu.convert(delay - timeLeft, TimeUnit.MILLISECONDS);
           }

           @Override
           public int compareTo(Delayed t) {
               return Long.compare(this.getDelay(TimeUnit.MILLISECONDS),t.getDelay(TimeUnit.MILLISECONDS));
           }
           public String toString(){
               return this.data.toString();
           }

       }

    /**
     *
     * @param sleep sleep duration before start (Milliseconds)
     * @param id identification 
     * @param count iteration amount
     * @param delay delay between iterations (Milliseconds)
     * @param timeout operation timeout (Milliseconds)
     * @param queue queue to interact with
     * @return 
     */
    public static Thread createAdder(long sleep,String id,int count, long delay,long timeout, BlockingQueue queue){
            Runnable adder = () ->{
                try{
                    Thread.sleep(sleep);
                    for(int i=0; i<count; i++){       
                        Log.println(id+" -> "+i+" "+queue.offer(id+":"+i,timeout,TimeUnit.MILLISECONDS));
                        Thread.sleep(delay);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(adder);
        }
    public static Thread createTaker(long sleep,String id,int count, long delay,long timeout, BlockingQueue queue){
            Runnable taker = () ->{
                try{
                    Thread.sleep(sleep);
                    ArrayList<String> list = new ArrayList<>();
                    for(int i=0; i<count; i++){
                        String item = ""+queue.poll(timeout,TimeUnit.MILLISECONDS);
                        Log.println(id+" <- "+item);
                        list.add(item);
                        Thread.sleep(delay);
                    }
                    Log.println(id+list);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(taker);
        }
    public static Thread createTransferer(long sleep,String id,int count, long delay,long timeout, TransferQueue queue){
        Runnable adder = () ->{
                try{
                    Thread.sleep(sleep);
                    for(int i=0; i<count; i++){       
                        Log.println(id+" -> "+i+" "+queue.tryTransfer(id+":"+i,timeout,TimeUnit.MILLISECONDS));
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
