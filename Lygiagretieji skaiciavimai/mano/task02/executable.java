package task02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Laimonas Beniušis
 * Read/Write užrakto klasė ReadWriteLock
 * Nepilnai implementuota klasė SynchArrayList, kuri naudoja tą užraktą
 * 
 */
public class executable {
    static int adderTakerAmount = 5;
    static int readerAmount = 3;
    static int itemAmount = 5;
    public static void main(String[] args){
        System.out.println("ARGS:"+Arrays.asList(args));

        List list;
        list = new SynchArrayList();
//        list = new LinkedList();
        if(args.length>0){
            try{
                adderTakerAmount = Integer.parseInt(args[0]);
                readerAmount = Integer.parseInt(args[1]);
                itemAmount = Integer.parseInt(args[2]);
                        
            }catch(Exception e){
                System.out.println("Usage [modificators] [readers]");
                return;
            }
        }else{
            System.out.println("Running with default settings");
        }

        
        createTakerAdder(0,"",itemAmount,1,list,adderTakerAmount).forEach(t ->{
            t.start();
        });
        for(int i=0;i<readerAmount;i++){
            Runnables.createReader(1, "R"+i, itemAmount, 1, list).start();
        }
    }
    public static Collection<Thread> createTakerAdder(int sleep,String id,int count,int delay, List list,int amount){
        ArrayList<Thread> threads = new ArrayList<>();
        for(int i=0; i<amount; i++){
            threads.add(Runnables.createAdder(sleep, "A"+id+i, count, delay, list));
            threads.add(Runnables.createTaker(sleep+1000, "T"+id+i, count, delay, list));
        }
        return threads;
    }
}
