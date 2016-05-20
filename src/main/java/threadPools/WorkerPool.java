package threadPools;

/**
 * Created by root on 4.5.16.
 */
public interface WorkerPool {
    void addTask(Runnable task);
}
