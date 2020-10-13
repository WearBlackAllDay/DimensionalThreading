package dimthread.mixin;

import com.mojang.datafixers.util.Either;
import dimthread.DimThread;
import dimthread.thread.IMutableMainThread;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin implements IMutableMainThread {

	@Shadow @Final public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
	@Mutable @Shadow @Final private Thread serverThread;

	@Shadow @Final private ServerChunkManager.MainThreadExecutor mainThreadExecutor;
	@Shadow @Final private long[] chunkPosCache;
	@Shadow @Final private ChunkStatus[] chunkStatusCache;
	@Shadow @Final private Chunk[] chunkCache;
	@Shadow @Final private ServerWorld world;

	@Shadow protected abstract void putInCache(long pos, Chunk chunk, ChunkStatus status);
	@Shadow protected abstract CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

	@Inject(method = "getTotalChunksLoadedCount", at = @At("HEAD"), cancellable = true)
	private void getTotalChunksLoadedCount(CallbackInfoReturnable<Integer> ci) {
		if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
			int count = this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
			if(count < 441)ci.setReturnValue(441);
		}
	}

	@Override
	public Thread getMainThread() {
		return this.serverThread;
	}

	@Override
	public void setMainThread(Thread thread) {
		this.serverThread = thread;
	}

	@Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("HEAD"), cancellable = true)
	public void getChunk(int x, int z, ChunkStatus leastStatus, boolean create, CallbackInfoReturnable<Chunk> ci) {
		if(DimThread.MANAGER.isActive(this.world.getServer()) && DimThread.owns(Thread.currentThread())) {
			Profiler profiler = this.world.getProfiler();
			profiler.visit("getChunk");
			long l = ChunkPos.toLong(x, z);

			Chunk chunk;

			for(int i = 0; i < 4; ++i) {
				if(l == this.chunkPosCache[i] && leastStatus == this.chunkStatusCache[i]) {
					chunk = this.chunkCache[i];
					if(chunk != null || !create) {
						ci.setReturnValue(chunk);
					}
				}
			}

			profiler.visit("getChunkCacheMiss");
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> future = this.getChunkFuture(x, z, leastStatus, create);
			this.mainThreadExecutor.runTasks(future::isDone);

			chunk = future.join().map(c -> c, unloaded -> {
				if(create) {
					throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
				}

				return null;
			});

			this.putInCache(l, chunk, leastStatus);
			ci.setReturnValue(chunk);
		}
	}

}
