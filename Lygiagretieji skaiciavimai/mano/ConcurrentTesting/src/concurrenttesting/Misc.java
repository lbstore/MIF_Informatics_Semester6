/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrenttesting;

import LibraryLB.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Phaser;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lemmin
 */
public class Misc {
    public static class ExchangerRunnable implements Runnable{

        Exchanger exchanger = null;
        Object    object    = null;

        public ExchangerRunnable(Exchanger exchanger, Object object) {
            this.exchanger = exchanger;
            this.object = object;
        }

        @Override
        public void run() {
            try {
                Object previous = this.object;
                Thread.sleep((long)(Math.random()*5000));
                this.object = this.exchanger.exchange(this.object);

                Log.println(
                        Thread.currentThread().getName() +
                        " exchanged " + previous + " for " + this.object
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Thread phaserThread(Phaser phaser,int count,long sleep){
        Runnable run = () ->{
            try{
                phaser.register();//thread checks-in as attending
                for(int i=0; i<count; i++){
                    Thread.sleep(sleep);        
                    Log.println(Thread.currentThread().getName()+" arrived "+i);
                    //threads checks-in and waits for other threads
                    phaser.arriveAndAwaitAdvance();
                    Log.println(Thread.currentThread().getName()+" advanced "+i);
                }
                phaser.arriveAndDeregister();//thread no longer is attending
            }catch (InterruptedException e){
                  e.printStackTrace();
            }
            
            Log.println(Thread.currentThread().getName()+" finished");
        };
        return new Thread(run);
    }
    
    public static class MyRecursiveTask extends RecursiveTask<Long> {

        private long workLoad = 0;

        public MyRecursiveTask(long workLoad) {
            this.workLoad = workLoad;
        }

        @Override
        protected Long compute() {

            if(this.workLoad > 16) {
                Log.println("Splitting workLoad : " + this.workLoad);

                List<MyRecursiveTask> subtasks = new ArrayList<>();
                subtasks.addAll(createSubtasks());

                for(MyRecursiveTask subtask : subtasks){
                    subtask.fork();
                }

                long result = 0;
                for(MyRecursiveTask subtask : subtasks) {
                    result += subtask.join();
                }
                return result;

            } else {
                Log.println("Doing workLoad myself: " + this.workLoad);
                try {
                    Thread.sleep(50*this.workLoad);
                } catch (InterruptedException ex) {
                }
                return workLoad * 3;
            }
        }

    private List<MyRecursiveTask> createSubtasks() {
        List<MyRecursiveTask> subtasks = new ArrayList<MyRecursiveTask>();

        MyRecursiveTask subtask1 = new MyRecursiveTask(this.workLoad / 2);
        MyRecursiveTask subtask2 = new MyRecursiveTask(this.workLoad / 2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        return subtasks;
    }
}


}
