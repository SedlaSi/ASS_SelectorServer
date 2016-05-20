package threadPools;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by root on 4.5.16.
 */
public class MyWorkerPool implements WorkerPool{

    private final static int POOLSIZE = 20;
    private List<Worker> workers;
    private BlockingQueue<Runnable> tasks;

    public MyWorkerPool(int poolSize){
        workers = new ArrayList<>(poolSize);
        for(int i = 0; i < poolSize; i++){
            workers.add(null);
        }
        tasks = new LinkedBlockingQueue<>();
        initWorkers();
    }


    private void initWorkers(){
        for(Worker w : workers){
            w = new Worker();
            w.setTasks(tasks);
            new Thread(w).start();
        }
    }

    public void addTask(Runnable task){
        tasks.add(task);
    }
}
