/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrenttesting;

import LibraryLB.Log;
import concurrenttesting.ActualTests.*;

/**
 *
 * @author Lemmin
 */
public class ConcurrentTesting {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        Log.println("BEGIN");


//        AtomicVariableTests.simpleTest();
//        AtomicVariableTests.accumulatorTest();
//        AtomicVariableTests.atomicReferenceTest();;

//        MiscTests.CountDownLatchTest();
//        MiscTests.CyclicBarrierTest();

//        MiscTests.ExchangerTest();
//        MiscTests.PhaserTest();
        
//        ConcurrentCollectionTests.ArrayBlockingQueueTest(true);
//        ConcurrentCollectionTests.DelayQueueTest();
//        ConcurrentCollectionTests.SynchronousQueueTest();
//        ConcurrentCollectionTests.TransferQueueTest();

//        CallableInterfaceTests.CallableVsRunnable();
//        CallableInterfaceTests.FutureTaskUsage();
//        CallableInterfaceTests.CompletableFutureTest();
        


//        ThreadPools.ExecutorUsage();
        ThreadPools.forJoinPoolTest();

        Log.println("END");
        Log.close();
    }
    
    
}
