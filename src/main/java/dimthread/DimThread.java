package dimthread;

import dimthread.thread.IMutableMainThread;
import net.minecraft.server.world.ServerWorld;

public class DimThread {

	public static final String MOD_ID = "dimthread";

	public static void swapThreadsAndRun(Runnable task, Object... threadedObjects) {
		Thread currentThread = Thread.currentThread();
		Thread[] oldThreads = new Thread[threadedObjects.length];

		for(int i = 0; i < oldThreads.length; i++) {
			oldThreads[i] = ((IMutableMainThread)threadedObjects[i]).getMainThread();
			((IMutableMainThread)threadedObjects[i]).setMainThread(currentThread);
		}

		task.run();

		for(int i = 0; i < oldThreads.length; i++) {
			((IMutableMainThread)threadedObjects[i]).setMainThread(oldThreads[i]);
		}
	}

	public static void attach(Thread thread, ServerWorld world) {
		thread.setName(MOD_ID);
	}

	public static boolean owns(Thread thread) {
		return thread.getName().startsWith(MOD_ID);
	}

}
