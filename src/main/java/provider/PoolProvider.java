package provider;

import sun.nio.ch.ThreadPool;
import threadPools.MyWorkerPool;
import threadPools.WorkerPool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by root on 4.5.16.
 */
public class PoolProvider {

    private WorkerPool pool;

    public PoolProvider(int poolSize) {
        pool = new MyWorkerPool(poolSize);
    }

    public WorkerPool getPool() {
        return pool;
    }
}
