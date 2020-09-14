package dimthread.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

	@Shadow protected abstract void addPlayer(ServerPlayerEntity player);

	@Shadow public abstract void checkEntityChunkPos(Entity entity);

	@Shadow public abstract MinecraftServer getServer();

	@Inject(method = "onPlayerChangeDimension", at = @At("HEAD"), cancellable = true)
	public void onPlayerChangeDimension(ServerPlayerEntity player, CallbackInfo ci) {
		this.getServer().execute(() -> {
			this.addPlayer(player);
			this.checkEntityChunkPos(player);
		});

		ci.cancel();
	}

}
