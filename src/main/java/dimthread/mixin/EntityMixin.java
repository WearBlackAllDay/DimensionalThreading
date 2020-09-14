package dimthread.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerWorld;onDimensionChanged(Lnet/minecraft/entity/Entity;)V"))
	public void onDimensionChanged(ServerWorld world, Entity entity) {
		world.getServer().execute(() -> {
			world.onDimensionChanged(entity);
		});
	}

	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerWorld;createEndSpawnPlatform(Lnet/minecraft/server/world/ServerWorld;)V"))
	public void createEndSpawnPlatform(ServerWorld world) {
		world.getServer().execute(() -> {
			ServerWorld.createEndSpawnPlatform(world);
		});
	}

}
