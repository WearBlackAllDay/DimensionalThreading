package mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import other.IMutableMainThread;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin implements IMutableMainThread {

	@Shadow @Final public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
	@Mutable @Shadow @Final private Thread serverThread;

	@Inject(method = "getTotalChunksLoadedCount", at = @At("HEAD"), cancellable = true)
	public void getTotalChunksLoadedCount(CallbackInfoReturnable<Integer> ci) {
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

}
