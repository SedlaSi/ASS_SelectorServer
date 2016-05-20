package threadPools;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertTrue;

/**
 * Created by root on 20.5.16.
 */
public class TestWorker {

    @Test
    public void testSetTasks(){
        Worker worker = new Worker();
        worker.setTasks(null);
    }

    @Test
    public void testRun(){
        Worker worker =  new Worker();
        BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
        for(int i = 0; i < 20 ; i++){
            tasks.add(() -> {});
        }
        worker.setTasks(tasks);
        Thread workerThread = new Thread(() -> {
            worker.run();
        });
        workerThread.start();
        synchronized (this){
            try {
                wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertTrue(tasks.isEmpty());
    }

}
