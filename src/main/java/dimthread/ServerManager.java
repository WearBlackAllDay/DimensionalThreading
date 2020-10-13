package dimthread;

import dimthread.init.ModGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import threading.ThreadPool;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.WeakHashMap;

public class ServerManager {

	private final Map<MinecraftServer, Boolean> ACTIVES = Collections.synchronizedMap(new WeakHashMap<>());
	private final Map<MinecraftServer, ThreadPool> THREAD_POOLS = Collections.synchronizedMap(new WeakHashMap<>());

	public boolean isActive(MinecraftServer server) {
		return this.ACTIVES.computeIfAbsent(server, s -> s.getGameRules().get(ModGameRules.ACTIVE.getKey()).get());
	}

	public void setActive(MinecraftServer server, GameRules.BooleanRule value) {
		this.ACTIVES.put(server, value.get());
	}

	public ThreadPool getThreadPool(MinecraftServer server) {
		return this.THREAD_POOLS.computeIfAbsent(server, s -> new ThreadPool(s.getGameRules().get(ModGameRules.THREAD_COUNT.getKey()).get()));
	}

	public void setThreadCount(MinecraftServer server, GameRules.IntRule value) {
		ThreadPool current = this.THREAD_POOLS.get(server);

		if(current.getActiveCount() != 0) {
			throw new ConcurrentModificationException("Setting the thread count in wrong phase");
		}

		this.THREAD_POOLS.put(server, new ThreadPool(value.get()));
		current.shutdown();
	}

}
