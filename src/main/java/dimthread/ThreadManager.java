package dimthread;

import dimthread.thread.ThreadPool;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadManager {

	public Map<MinecraftServer, ThreadPool> THREAD_POOLS = new ConcurrentHashMap<>();

	private GameRules.Type<GameRules.IntRule> THREAD_COUNT_RULE = GameRuleFactory
			.createIntRule(3, 1, Runtime.getRuntime().availableProcessors(), this::setThreadCount);
	private GameRules.Key<GameRules.IntRule> THREAD_COUNT_KEY;

	public void onInitialize() {
		THREAD_COUNT_KEY = GameRuleRegistry.register("dimensionThreadCount", GameRules.Category.UPDATES, THREAD_COUNT_RULE);
	}

	public ThreadPool getThreadPool(MinecraftServer server) {
		return THREAD_POOLS.computeIfAbsent(server, s -> new ThreadPool(s.getGameRules().get(THREAD_COUNT_KEY).get()));
	}

	private void setThreadCount(MinecraftServer server, GameRules.IntRule threadCount) {
		ThreadPool current = THREAD_POOLS.get(server);

		if(current.getActiveCount() != 0) {
			throw new ConcurrentModificationException("Setting the thread count in wrong phase");
		}

		THREAD_POOLS.put(server, new ThreadPool(threadCount.get()));
		current.shutdown();
	}

}
