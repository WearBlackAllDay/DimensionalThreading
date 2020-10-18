package dimthread.mixin;

import dimthread.DimThread;
import dimthread.thread.IMutableMainThread;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin extends ChunkManager implements IMutableMainThread {

	@Shadow @Final @Mutable private Thread serverThread;
	@Shadow @Final public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
	@Shadow @Final private ServerWorld world;

	@Override
	public Thread getMainThread() {
		return this.serverThread;
	}

	@Override
	public void setMainThread(Thread thread) {
		this.serverThread = thread;
	}

	@Inject(method = "getTotalChunksLoadedCount", at = @At("HEAD"), cancellable = true)
	private void getTotalChunksLoadedCount(CallbackInfoReturnable<Integer> ci) {
		if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
			int count = this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
			if(count < 441)ci.setReturnValue(441);
		}
	}

	@Redirect(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
	public Thread currentThread(int x, int z, ChunkStatus leastStatus, boolean create) {
		Thread thread = Thread.currentThread();

		if(DimThread.MANAGER.isActive(this.world.getServer()) && DimThread.owns(thread)) {
			return this.serverThread;
		}

		return thread;
	}

}
