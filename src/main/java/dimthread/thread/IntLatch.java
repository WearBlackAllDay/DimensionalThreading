package dimthread.thread;

import java.util.concurrent.CountDownLatch;
import java.util.function.IntPredicate;

public class IntLatch {

	private CountDownLatch latch;
	private final Object lock = new Object();

	public IntLatch() {
		this(0);
	}

	public IntLatch(int count) {
		this.latch = new CountDownLatch(count);
	}

	public int getCount() {
		synchronized(this.lock) {
			return (int)this.latch.getCount();
		}
	}

	public void decrement() {
		synchronized(this.lock) {
			this.latch.countDown();
			this.lock.notifyAll();
		}
	}

	public void increment() {
		synchronized(this.lock) {
			this.latch = new CountDownLatch((int)this.latch.getCount() + 1);
			this.lock.notifyAll();
		}
	}

	public void waitUntil(IntPredicate predicate) throws InterruptedException {
		synchronized(this.lock) {
			while(!predicate.test(this.getCount())) {
				this.lock.wait();
			}
		}
	}

}
