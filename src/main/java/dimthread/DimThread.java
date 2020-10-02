package dimthread;

import dimthread.thread.IMutableMainThread;
import dimthread.thread.ThreadPool;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class DimThread implements ModInitializer {

	public static final String MOD_ID = "dimthread";

	public static final GameRules.Type<GameRules.IntRule> THREAD_COUNT = GameRuleFactory
			.createIntRule(3, 0, Runtime.getRuntime().availableProcessors(),
					(server, intRule) -> THREAD_POOL = new ThreadPool(intRule.get()));

	public static ThreadPool THREAD_POOL = new ThreadPool(3);

	@Override
	public void onInitialize() {
		GameRuleRegistry.register("dimensionThreadCount", GameRules.Category.UPDATES, THREAD_COUNT);
	}

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

	/**
	 * Makes it easy to understand what is happening in crash reports and helps identify dimthread workers.
	 * */
	public static void attach(Thread thread, String name) {
		thread.setName(MOD_ID + "_" + name);
	}

	public static void attach(Thread thread, ServerWorld world) {
		attach(thread, world.getRegistryKey().getValue().getPath());
	}

	/**
	 * Checks if the given thread is a dimthread worker by checking the name. Probably quite fragile...
	 * */
	public static boolean owns(Thread thread) {
		return thread.getName().startsWith(MOD_ID);
	}

}
