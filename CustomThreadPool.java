import java.util.concurrent.*;

class ThreadPool {
    private final WorkerThread[] workers;
    private final BlockingQueue<Runnable> taskQueue;
    private boolean isStopped = false;

    public ThreadPool(int numThreads) {
        workers = new WorkerThread[numThreads];
        taskQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    public synchronized void submit(Runnable task) throws IllegalStateException {
        if (isStopped) throw new IllegalStateException("ThreadPool is stopped");
        taskQueue.offer(task);
    }

    public synchronized void shutdown() {
        isStopped = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Runnable task;
                    task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    if (isStopped) break;
                }
            }
        }
    }
}

public class CustomThreadPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(3);
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            threadPool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + ": " + taskId);
            });
        }
        threadPool.shutdown();
    }
}
