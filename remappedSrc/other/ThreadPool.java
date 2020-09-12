package dimthread.other;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ThreadPool {

    private ThreadPoolExecutor executor;
    private final int threadCount;

    private final AtomicInteger activeCount = new AtomicInteger();

    public ThreadPool(int threadCount) {
        this.threadCount = threadCount;
        this.restart();
    }

    public int getThreadCount() {
        return this.threadCount;
    }

    public ThreadPoolExecutor getExecutor() {
        return this.executor;
    }

    public void run(Runnable action) {
        this.activeCount.getAndIncrement();

        this.executor.execute(() -> {
            action.run();
            activeCount.getAndDecrement();
        });
    }

    public <T> void iterate(Iterable<T> iterable, Consumer<T> action) {
        iterable.forEach(t -> this.run(() -> action.accept(t)));
    }

    public <T> void iterate(Iterator<T> iterator, Consumer<T> action) {
        iterator.forEachRemaining(t -> this.run(() -> action.accept(t)));
    }

    public void awaitFreeThread() {
        while(this.activeCount.get() >= this.getThreadCount()) {
            try {Thread.sleep(5);}
            catch(InterruptedException ignored) {}
        }
    }

    public void awaitCompletion() {
        while(this.activeCount.get() != 0) {
            try {Thread.sleep(5);}
            catch(InterruptedException ignored) {}
        }
    }

    public void restart() {
        if(this.executor == null || this.executor.isShutdown()) {
            this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.threadCount);
        }
    }

    public void shutdown() {
        this.executor.shutdown();
    }

}