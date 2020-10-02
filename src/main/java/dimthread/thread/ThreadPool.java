package dimthread.thread;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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

    public int getActiveCount() {
        return this.activeCount.getCount();
    }

    public ThreadPoolExecutor getExecutor() {
        return this.executor;
    }

    public void run(Runnable action) {
        this.activeCount.increment();

        this.executor.execute(() -> {
            action.run();
            this.activeCount.decrement();
        });
    }

    public <T> void iterate(Iterator<T> iterator, Consumer<T> action) {
        iterator.forEachRemaining(t -> this.run(() -> action.accept(t)));
    }

    public <T> void iterate(Iterable<T> iterable, Consumer<T> action) {
        iterable.forEach(t -> this.run(() -> action.accept(t)));
    }

    public <T> void iterate(Stream<T> stream, Consumer<T> action) {
        stream.forEach(t -> this.run(() -> action.accept(t)));
    }

    public void iterate(IntStream stream, IntConsumer action) {
        stream.forEach(t -> this.run(() -> action.accept(t)));
    }

    public void iterate(LongStream stream, LongConsumer action) {
        stream.forEach(t -> this.run(() -> action.accept(t)));
    }

    public void iterate(DoubleStream stream, DoubleConsumer action) {
        stream.forEach(t -> this.run(() -> action.accept(t)));
    }

    public <T> void iterate(T[] array, Consumer<T> action) {
        for(T t: array)this.run(() -> action.accept(t));
    }

    public void iterate(boolean[] array, Consumer<Boolean> action) {
        for(boolean t: array)this.run(() -> action.accept(t));
    }

    public void iterate(byte[] array, Consumer<Byte> action) {
        for(byte t: array)this.run(() -> action.accept(t));
    }

    public void iterate(short[] array, Consumer<Short> action) {
        for(short t: array)this.run(() -> action.accept(t));
    }

    public void iterate(int[] array, IntConsumer action) {
        for(int t: array)this.run(() -> action.accept(t));
    }

    public void iterate(float[] array, Consumer<Float> action) {
        for(float t: array)this.run(() -> action.accept(t));
    }

    public void iterate(long[] array, LongConsumer action) {
        for(long t: array)this.run(() -> action.accept(t));
    }

    public void iterate(double[] array, DoubleConsumer action) {
        for(double t: array)this.run(() -> action.accept(t));
    }

    public void iterate(char[] array, Consumer<Character> action) {
        for(char t: array)this.run(() -> action.accept(t));
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