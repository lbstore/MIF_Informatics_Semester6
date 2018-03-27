package task02;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lemmin
 */
public class Runnables {
    
    /**
     *
     * @param sleep sleep duration before start (Milliseconds)
     * @param id identification 
     * @param count iteration amount
     * @param delay delay between iterations (Milliseconds)
     * @param list list to interact with
     * @return Thread that runs
     */
    public static Thread createAdder(long sleep,String id,int count, long delay, List list){
            Runnable adder = () ->{
                try{
                    Thread.sleep(sleep);
                    for(int i=0; i<count; i++){  
                        list.add(id+":"+i);
                        System.out.println(id+" -> "+i);
                        Thread.sleep(delay);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(adder);
        }
    public static Thread createTaker(long sleep,String id,int count, long delay, List list){
            Runnable taker = () ->{
                try{
                    Thread.sleep(sleep);
                    ArrayList<String> taken = new ArrayList<>();
                    for(int i=0; i<count; i++){
                        String item = ""+list.remove(0);
                        System.out.println(id+" <- "+item);
                        taken.add(item);
                        Thread.sleep(delay);
                    }
                    System.out.println(id+" done:"+taken);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(taker);
        }
    public static Thread createReader(long sleep,String id,int count, long delay, List list){
        Runnable reader = () ->{
                try{
                    Thread.sleep(sleep);
                    for(int i=0; i<count; i++){
                        System.out.println(id+":"+list);
                        Thread.sleep(delay);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            };
            return new Thread(reader);
        
    }
}
