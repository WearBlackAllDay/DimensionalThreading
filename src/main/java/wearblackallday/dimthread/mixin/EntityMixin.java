package wearblackallday.dimthread.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import wearblackallday.dimthread.DimThread;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow public abstract Entity moveToWorld(ServerWorld destination);

	@Shadow @Final public abstract boolean isRemoved();

	@Shadow private Entity.RemovalReason removalReason;

	/**
	 * Schedules moving entities between dimensions to the server thread. Once all
	 * the world finish ticking, {@code moveToWorld()} is processed in a safe manner
	 * avoiding concurrent modification exceptions.
	 *
	 * For example, the entity list is not thread-safe and modifying it from
	 * multiple threads will cause a crash. Additionally, loading chunks from
	 * another thread will cause a deadlock in the server chunk manager.
	 */
	@Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
	public void moveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> ci) {
		if (!DimThread.MANAGER.isActive(destination.getServer()))
			return;

		if (DimThread.owns(Thread.currentThread())) {
			destination.getServer().execute(() -> this.moveToWorld(destination));
			ci.setReturnValue(null);
		}
	}

	/**
	 * If in the moveToWorld method removalReason is DISCARDED, then we assume its
	 * caused by a sand duplicator.
	 */
	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isRemoved()Z"))
	public final boolean forceNotRemoved(Entity entity) {
		Entity.RemovalReason reason = this.removalReason;
		if ((((Object) this) instanceof FallingBlockEntity) && (reason == Entity.RemovalReason.DISCARDED)) {
			return false;
		}
		return entity.isRemoved();
	}
}
