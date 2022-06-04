package wearblackallday.dimthread.util;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
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
	private final IntLatch activeCount = new IntLatch();

	public ThreadPool() {
		this(Runtime.getRuntime().availableProcessors());
	}

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

	public void execute(Runnable action) {
		this.activeCount.increment();

		this.executor.execute(() -> {
			action.run();
			this.activeCount.decrement();
		});
	}

	public <T> void execute(Iterator<T> iterator, Consumer<T> action) {
		iterator.forEachRemaining(t -> this.execute(() -> action.accept(t)));
	}

	public <T> void execute(Iterable<T> iterable, Consumer<T> action) {
		iterable.forEach(t -> this.execute(() -> action.accept(t)));
	}

	public <T> void execute(Stream<T> stream, Consumer<T> action) {
		stream.forEach(t -> this.execute(() -> action.accept(t)));
	}

	public void execute(IntStream stream, IntConsumer action) {
		stream.forEach(t -> this.execute(() -> action.accept(t)));
	}

	public void execute(LongStream stream, LongConsumer action) {
		stream.forEach(t -> this.execute(() -> action.accept(t)));
	}

	public void execute(DoubleStream stream, DoubleConsumer action) {
		stream.forEach(t -> this.execute(() -> action.accept(t)));
	}

	public <T> void execute(T[] array, Consumer<T> action) {
		for(T t : array) this.execute(() -> action.accept(t));
	}

	public void execute(boolean[] array, Consumer<Boolean> action) {
		for(boolean t : array) this.execute(() -> action.accept(t));
	}

	public void execute(byte[] array, Consumer<Byte> action) {
		for(byte t : array) this.execute(() -> action.accept(t));
	}

	public void execute(short[] array, Consumer<Short> action) {
		for(short t : array) this.execute(() -> action.accept(t));
	}

	public void execute(int[] array, IntConsumer action) {
		for(int t : array) this.execute(() -> action.accept(t));
	}

	public void execute(float[] array, Consumer<Float> action) {
		for(float t : array) this.execute(() -> action.accept(t));
	}

	public void execute(long[] array, LongConsumer action) {
		for(long t : array) this.execute(() -> action.accept(t));
	}

	public void execute(double[] array, DoubleConsumer action) {
		for(double t : array) this.execute(() -> action.accept(t));
	}

	public void execute(char[] array, Consumer<Character> action) {
		for(char t : array) this.execute(() -> action.accept(t));
	}

	public void awaitFreeThread() {
		this.waitFor(value -> value < this.getThreadCount());
	}

	public void awaitCompletion() {
		this.waitFor(value -> value == 0);
	}

	public void waitFor(IntPredicate condition) {
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

	public boolean isShutdown() {
		return this.executor.isShutdown();
	}

	private static class IntLatch {
		private CountDownLatch latch;

		private IntLatch() {
			this(0);
		}

		private IntLatch(int count) {
			this.latch = new CountDownLatch(count);
		}

		private synchronized int getCount() {
			return (int)this.latch.getCount();
		}

		private synchronized void decrement() {
			this.latch.countDown();
			this.notifyAll();
		}

		private synchronized void increment() {
			this.latch = new CountDownLatch((int)this.latch.getCount() + 1);
			this.notifyAll();
		}

		private synchronized void waitUntil(IntPredicate predicate) throws InterruptedException {
			while(!predicate.test(this.getCount())) {
				this.wait();
			}
		}
	}
}
