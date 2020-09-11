package mixin;

import net.minecraft.SharedConstants;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import other.IMutableServerThread;
import other.ThreadPool;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	private static boolean DEBUG = false;

	@Shadow private Profiler profiler;
	@Shadow private int ticks;
	@Shadow private PlayerManager playerManager;
	@Shadow @Final private List<Runnable> serverGuiTickables;

	@Shadow public abstract ServerNetworkIo getNetworkIo();
	@Shadow public abstract CommandFunctionManager getCommandFunctionManager();
	@Shadow public abstract Iterable<ServerWorld> getWorlds();

	private ThreadPool pool = new ThreadPool(Runtime.getRuntime().availableProcessors());

	/**
	 * @author
	 */
	@Overwrite
	public void tickWorlds(BooleanSupplier shouldKeepTicking) {
		this.profiler.push("commandFunctions");
		this.getCommandFunctionManager().tick();
		this.profiler.swap("levels");

		if(DEBUG)System.out.format("==================================================\n");

		this.pool.iterate(this.getWorlds().iterator(), serverWorld -> {
//			String dimensionName = serverWorld.getDimension().getSkyProperties().getPath();
//			Thread.currentThread().setName("dimthreading_" + dimensionName);
//			if(DEBUG)System.out.format("[%d] Started %s\n", this.ticks, dimensionName);

			if(this.ticks % 20 == 0) {
				this.playerManager.sendToDimension(new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), serverWorld.getRegistryKey());
			}

			try {
				IMutableServerThread manager = (IMutableServerThread)serverWorld.getChunkManager();
				Thread oldServerThread = manager.getServerThread();
				manager.setServerThread(Thread.currentThread());
				serverWorld.tick(shouldKeepTicking);
				manager.setServerThread(oldServerThread);
			} catch(Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Exception ticking world");
				serverWorld.addDetailsToCrashReport(crashReport);
				throw new CrashException(crashReport);
			}

//			if(DEBUG)System.out.format("[%d] Finished %s\n", this.ticks, dimensionName);
		});

		this.pool.awaitCompletion();
		if(DEBUG)System.out.format("Ticking completed!\n");

		this.profiler.swap("connection");
		this.getNetworkIo().tick();
		this.profiler.swap("players");
		this.playerManager.updatePlayerLatency();
		if (SharedConstants.isDevelopment) {
			TestManager.INSTANCE.tick();
		}

		this.profiler.swap("server gui refresh");

		for (Runnable serverGuiTickable : this.serverGuiTickables) {
			serverGuiTickable.run();
		}

		this.profiler.pop();
	}

}
