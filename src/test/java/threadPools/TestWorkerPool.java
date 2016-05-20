package threadPools;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertTrue;

/**
 * Created by root on 20.5.16.
 */
public class TestWorkerPool {

    @Test
    public void testAddTask(){
        WorkerPool workerPool = new MyWorkerPool(1);
        final boolean[] wasChanged = new boolean[] {false};
        Runnable task = () -> {
                wasChanged[0] = true;
        };
        workerPool.addTask(task);
        synchronized (this){
            try {
                wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }

        if(!wasChanged[0]) {
            assertTrue(false);
        }

    }
}
