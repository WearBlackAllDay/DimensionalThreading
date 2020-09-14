package dimthread.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

	@Shadow protected abstract void createEndSpawnPlatform(ServerWorld world, BlockPos centerPos);

	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/network/ServerPlayerEntity;createEndSpawnPlatform(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V"))
	public void createEndSpawnPlatform(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
		world.getServer().execute(() -> {
			this.createEndSpawnPlatform(world, pos);
		});
	}

}
