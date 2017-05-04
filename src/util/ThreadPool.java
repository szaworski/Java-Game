package util;
import java.util.LinkedList;

/**
    A thread pool is a group of a limited number of threads that
    are used to execute tasks.
*/
public class ThreadPool extends ThreadGroup {

    private boolean isAlive;
    private LinkedList<Runnable> taskQueue;
    private int threadID;
    private static int threadPoolID;

    public ThreadPool(int numThreads) {
        super("ThreadPool-" + (threadPoolID++));
        setDaemon(true);

        isAlive = true;

        taskQueue = new LinkedList<Runnable>();
        for (int i=0; i<numThreads; i++) {
            new PooledThread().start();
        }
    }

    public synchronized void runTask(Runnable task) {
        if (!isAlive) {
            throw new IllegalStateException();
        }
        if (task != null) {
            taskQueue.add(task);
            notify();
        }

    }


    protected synchronized Runnable getTask()
        throws InterruptedException
    {
        while (taskQueue.size() == 0) {
            if (!isAlive) {
                return null;
            }
            wait();
        }
        return (Runnable)taskQueue.removeFirst();
    }

    public synchronized void close() {
        if (isAlive) {
            isAlive = false;
            taskQueue.clear();
            interrupt();
        }
    }

    public void join() {
        // notify all waiting threads that this ThreadPool is no
        // longer alive
        synchronized (this) {
            isAlive = false;
            notifyAll();
        }

        // wait for all threads to finish
        Thread[] threads = new Thread[activeCount()];
        int count = enumerate(threads);
        for (int i=0; i<count; i++) {
            try {
                threads[i].join();
            }
            catch (InterruptedException ex) { }
        }
    }

    protected void threadStarted() {
        // do nothing
    }

    protected void threadStopped() {
        // do nothing
    }

    private class PooledThread extends Thread {


        public PooledThread() {
            super(ThreadPool.this,
                "PooledThread-" + (threadID++));
        }


        public void run() {
            // signal that this thread has started
            threadStarted();

            while (!isInterrupted()) {

                // get a task to run
                Runnable task = null;
                try {
                    task = getTask();
                }
                catch (InterruptedException ex) { }

                // if getTask() returned null or was interrupted,
                // close this thread.
                if (task == null) {
                    break;
                }

                // run the task, and eat any exceptions it throws
                try {
                    task.run();
                }
                catch (Throwable t) {
                    uncaughtException(this, t);
                }
            }
            // signal that this thread has stopped
            threadStopped();
        }
    }
}