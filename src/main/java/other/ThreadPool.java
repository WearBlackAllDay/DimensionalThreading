package other;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class ThreadPool {

    private ThreadPoolExecutor executor;
    private final int threadCount;

    private final IntLatch activeCount = new IntLatch(0);

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
        this.activeCount.increment();

        this.executor.execute(() -> {
            action.run();
            activeCount.decrement();
        });
    }

    public <T> void iterate(Iterable<T> iterable, Consumer<T> action) {
        iterable.forEach(t -> this.run(() -> action.accept(t)));
    }

    public <T> void iterate(Iterator<T> iterator, Consumer<T> action) {
        iterator.forEachRemaining(t -> this.run(() -> action.accept(t)));
    }

    public void awaitFreeThread() {
        this.waitUntil(value -> value < this.getThreadCount());
    }

    public void awaitCompletion() {
        this.waitUntil(value -> value == 0);
    }

    public void waitUntil(IntPredicate condition) {
        try {
            this.activeCount.waitUntil(condition);
        } catch(InterruptedException e) {
            e.printStackTrace();
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