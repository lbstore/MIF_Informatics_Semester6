/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrenttesting;

import LibraryLB.Log;
import concurrenttesting.BlockingQueueTest.DelayedElement;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAccumulator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 *
 * @author Lemmin
 */
public class ActualTests {
    public static ObservableList<Thread> threads = FXCollections.observableArrayList();
    public static void start(Collection<Thread> list){
        list.forEach(thread -> {
            thread.start();
        });
    }
    public static void join(Collection<Thread> list){
       list.forEach(thread -> {
           try {
               thread.join();
           } catch (InterruptedException ex) {
               ex.printStackTrace();
           }
        }); 
    }
    
    public static class AtomicVariableTests{
        
        public static void simpleTest(){
            AtomicTests.Ref  ref = new AtomicTests.Ref(0);         
            threads.addAll(
                AtomicTests.createReader(0, "", 1000, 1, ref),
                    
                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref),

                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref),
                AtomicTests.createAdder(0, "A", 100, 10, ref)
            );
            start(threads);
            join(threads);
            Log.println(ref);
        }
        public static void accumulatorTest(){
            LongAccumulator acc = new LongAccumulator((long l, long l1) -> l+l1,0);
            //Add to accumulator
            
            threads.addAll(
                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc),

                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc),
                AtomicTests.createAcc(0, "", 100, 0, acc)
            );
            start(threads);
            join(threads);
            Log.println(acc.get());
        }
        
        public static void atomicReferenceTest(){
            AtomicReference<String> ref = new AtomicReference<>("");
            //90 "1" 
            //10 \n
            //Undetermined order
            Runnable r = () ->{
                for(int i=0; i<9; i++){
                    ref.updateAndGet((String t) -> t+"1");
                }
                ref.updateAndGet((String t) -> t+"\n");
            };
            for(int i=0; i<10; i++){
                threads.add(new Thread(r));
            }
            start(threads);
            join(threads);
            Log.print(ref.get());
            Log.print("Length="+ref.get().length());
        }
        
        
    }
    
    public static class MiscTests{
        
        public static void CountDownLatchTest(){
            CountDownLatch latch = new CountDownLatch(5);
            Runnable dec = () ->{
                try {
                    Thread.sleep(1000);
                    
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                latch.countDown();
            };
            Runnable waiter = () ->{
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Log.println("FREEDOM!");
            };
            threads.addAll(
                new Thread(waiter),
                new Thread(dec),
                new Thread(dec),
                new Thread(dec),
                new Thread(dec),
                new Thread(dec),
                new Thread(dec)
            );//per daug būti negali
            start(threads);
            join(threads);
        }
        
        public static void CyclicBarrierTest(){
            CyclicBarrier bar = new CyclicBarrier(4);
            int times = 2;
            
            FutureTask t1 = new FutureTask(() ->{
                String name = Thread.currentThread().getName();
                for(int i=0; i<times; i++){
                    Thread.sleep(1000);
                    Log.print(name +" "+i);
                    bar.await();
                }
                Log.print(name+" finished");
                return null;
            });
            FutureTask t2 = new FutureTask(() ->{
                String name = Thread.currentThread().getName();
                for(int i=0; i<times; i++){
                    Thread.sleep(2000);
                    Log.print(name +" "+i);
                    bar.await();
                }
                Log.print(name+" finished");
                return null;
            });
            FutureTask t3 = new FutureTask(() ->{
                String name = Thread.currentThread().getName();
                for(int i=0; i<times; i++){
                    Thread.sleep(2500);
                    Log.print(name +" "+i);
                    bar.await();
                }
                Log.print(name+" finished");
                return null;
            });
            FutureTask t4 = new FutureTask(() ->{
                String name = Thread.currentThread().getName();
                for(int i=0; i<times; i++){
                    Thread.sleep(3000);
                    Log.print(name +" "+i);
                    bar.await();
                }
                Log.print(name+" finished");
                return null;
            });
            threads.addAll(
                new Thread(t1),
                new Thread(t2),
                new Thread(t3),
                new Thread(t4)
            );
            start(threads);
            join(threads);
            
        }
        
        public static void ExchangerTest(){
            Exchanger exchanger = new Exchanger();                
            threads.addAll(
                    new Thread(new Misc.ExchangerRunnable(exchanger, "A")),
                    new Thread(new Misc.ExchangerRunnable(exchanger, "B"))
            );
            start(threads);
            join(threads);
        }
        
        public static void PhaserTest() throws InterruptedException{
            Phaser phaser = new Phaser();
            
            int phasecount = 2;
            phaser.register();
            
            threads.addAll(
                Misc.phaserThread(phaser, phasecount,1000),
                Misc.phaserThread(phaser, phasecount,2000),
                Misc.phaserThread(phaser, phasecount-1,3000)
            );
            start(threads);
            for(int i = 0; i<phasecount; i++){
                Thread.sleep(500);
                Log.println("Phase "+phaser.getPhase());
                phaser.arriveAndAwaitAdvance();
            }
            join(threads);
            Log.print("After join");
            Log.println("Final phase "+phaser.getPhase());
        }
    }
    
    public static class ConcurrentCollectionTests{

        public static Collection ArrayBlockingQueueTest(boolean fair) throws InterruptedException{
            ArrayBlockingQueue queue = new ArrayBlockingQueue(1000,fair);
            threads.addAll(
                BlockingQueueTest.createAdder(0,"Adder1",5,1000,Long.MAX_VALUE,queue),
                BlockingQueueTest.createAdder(0,"Adder2",5,1000,Long.MAX_VALUE,queue),
                BlockingQueueTest.createTaker(0,"T1",2,100,5000,queue),
                BlockingQueueTest.createTaker(0,"T2",2,100,5000,queue),
                BlockingQueueTest.createTaker(0,"T3",2,100,5000,queue),
                BlockingQueueTest.createTaker(0,"T4",2,100,5000,queue),
                BlockingQueueTest.createTaker(2,"T5",2,100,5000,queue)
            );
            start(threads);
            join(threads);
            
            
            return queue;
        } 
        public static Collection DelayQueueTest() throws InterruptedException{
            
            DelayQueue queue = new DelayQueue();
            DelayedElement de1 = new DelayedElement(2000,1);
            DelayedElement de2 = new DelayedElement(3000,2);
            DelayedElement de3 = new DelayedElement(1000,3);

            Runnable producer = ()->{

                    queue.offer(de3);
                    queue.offer(de2);
                    queue.offer(de1);

            };
            Runnable consumer = () ->{
                    try {
                        Log.println(queue.take());
                        Log.println(queue.take());
                        Log.println(queue.take());
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }                
            };
            
            threads.addAll(new Thread(producer),new Thread(consumer));
            start(threads);
            join(threads);
            return queue;
        }
        public static Collection SynchronousQueueTest() throws InterruptedException{
            BlockingQueue queue = new SynchronousQueue(true);
            threads.addAll(
                BlockingQueueTest.createAdder(0,"adder1", 5, 1, Long.MAX_VALUE, queue),
                BlockingQueueTest.createTaker(0,"taker1", 3, 1000, 2000, queue),
                BlockingQueueTest.createTaker(0,"taker2", 2, 1000, 2000, queue)
            );
            start(threads);
            join(threads);
            return queue;
        }  
        public static Collection TransferQueueTest() throws InterruptedException{
            LinkedTransferQueue queue = new LinkedTransferQueue();
            threads.addAll(
                new Thread(BlockingQueueTest.createTransferer(0,"transferer1", 5, 100, 2000, queue)),
                new Thread(BlockingQueueTest.createTaker(0,"taker1", 2, 1000, 2000, queue)),
                new Thread(BlockingQueueTest.createTaker(0,"taker2", 2, 1000, 2000, queue))
            );
            start(threads);
            join(threads);
            return queue;
        }

    }
    
    public static class CallableInterfaceTests{
        
        public static void CallableVsRunnable(){
            Runnable run = () ->{
                Log.println("Runnable");
            };
            Callable call = () ->{
                Log.println("Callable");
                return null;
            };
            FutureTask task = new FutureTask(() ->{
                Log.println("FutureTask");
                return null;
            });
            threads.addAll(
                new Thread(run),
                new Thread(run),
                new Thread(new FutureTask(call)),
                new Thread(new FutureTask(call)),
                new Thread(task),
                new Thread(task)
            );
            start(threads);
            join(threads);
        }
        
        public static void FutureTaskUsage(){
            FutureTask task2 = new FutureTask( () ->{
               Thread.sleep(2000);
               
               return "result2";
            });
            FutureTask task1 = new FutureTask( () ->{
               
               Thread.sleep(2000);
               new Thread(task2).start();
               return "result1";
            });
           
            Thread thread1 = new Thread(task1);
            Thread thread2 = new Thread(task2);
            threads.addAll(thread1,thread2);
            thread1.start();
            try {
                
                Log.println(task1.get());
                Log.println(task2.get());
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        public static void CompletableFutureTest(){
            ExecutorService exec = Executors.newSingleThreadExecutor();
            CompletableFuture<Integer> f = CompletableFuture.supplyAsync(()->{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return 1;
            }, exec);
            Log.println(f.isDone()); // False
            CompletableFuture<Integer> f2 = f.thenApply((Integer x) -> x + 1);
            try {
                Log.println(f2.get());
                Thread.sleep(1000);
                exec.shutdown();
                exec.awaitTermination(3, TimeUnit.SECONDS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
        }
        
       
        
                
    }
    
    public static class ThreadPools{
        
         public static void ExecutorUsage(){
            
            
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
            final long[] time = new long[1];
            time[0] = 0;
            Runnable r1 = () ->{
                Log.println(System.currentTimeMillis() - time[0]+" r1");
            };
            Runnable r2 = () ->{
                Log.println(System.currentTimeMillis() - time[0]+" r2");
            };
            
            executor.scheduleAtFixedRate(r1,500, 1000, TimeUnit.MILLISECONDS);
            executor.scheduleAtFixedRate(r2,500, 500, TimeUnit.MILLISECONDS);
            time[0] = System.currentTimeMillis();
            Callable<String> call = () ->{
                Log.println("CALL");
                return "MSG"; 
            };
            ScheduledFuture<String> future = executor.schedule(call, 2000, TimeUnit.MILLISECONDS);
            try {
                Log.println(future.get());
                Thread.sleep(2000);
                executor.shutdown();
                executor.awaitTermination(3, TimeUnit.SECONDS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
        }
        
        public static void forJoinPoolTest(){
            ForkJoinPool pool = new ForkJoinPool(15);
            
            Log.println(pool.invoke(new Misc.MyRecursiveTask(1024)));
        }
        
    }
    
    
}