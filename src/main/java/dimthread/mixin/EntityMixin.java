package dimthread.mixin;

import dimthread.DimThread;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow public abstract Entity moveToWorld(ServerWorld destination);

	/**
	 * Moving entities between dimensions on the main server thread to not cause
	 * chunk loading deadlocks or concurrent modification issues.
	 *
	 * This method is allowed to return null client-side. Modders who call this
	 * function and use the return value should explicitly run a null check instead
	 * of a {@code isClient} check.
	 *
	 * @see Entity#moveToWorld(ServerWorld)
	 * @see ServerWorld#onPlayerChangeDimension(ServerPlayerEntity)
	 * */
	@Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
	public void moveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> ci) {
		if(DimThread.owns(Thread.currentThread())) {
			destination.getServer().execute(() -> this.moveToWorld(destination));
			ci.setReturnValue(null);
		}
	}

}
