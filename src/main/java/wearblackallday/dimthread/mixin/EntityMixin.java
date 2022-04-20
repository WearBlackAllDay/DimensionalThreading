package wearblackallday.dimthread.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wearblackallday.dimthread.DimThread;

import org.slf4j.LoggerFactory;

@Mixin(Entity.class)
public abstract class EntityMixin implements Cloneable {

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
			Entity snapshot = null;
			try {
				snapshot = (Entity) (this.clone());
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
			final Entity finalSnapshot = snapshot;
			destination.getServer().execute(
					() -> finalSnapshot.moveToWorld(destination)
			);
			ci.setReturnValue(null);
		}
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
