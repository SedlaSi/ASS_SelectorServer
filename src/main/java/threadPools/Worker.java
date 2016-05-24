package threadPools;

import java.util.concurrent.BlockingQueue;

/**
 * Created by root on 4.5.16.
 */
public class Worker implements Runnable {

    private BlockingQueue<Runnable> tasks;
    private Runnable task;

    public void setTasks(BlockingQueue<Runnable> tasks) {
        this.tasks = tasks;
    }

    private void doRunTask() {
        task.run();
    }

    @Override
    public void run() {
        while (true) {
            task = tasks.poll();
            if (task == null) continue;
            doRunTask();
        }
    }
}
