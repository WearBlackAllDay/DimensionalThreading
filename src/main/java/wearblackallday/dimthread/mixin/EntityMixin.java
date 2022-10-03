package wearblackallday.dimthread.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wearblackallday.dimthread.DimThread;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	abstract void removeFromDimension();

	private NbtCompound nbtCachedForMoveToWorld;

	@Shadow public abstract World getWorld();

	@Shadow public abstract NbtCompound writeNbt(NbtCompound nbt);

	@Shadow public abstract @Nullable Entity moveToWorld(ServerWorld destination);

	@Shadow private int netherPortalCooldown;

	@Shadow protected BlockPos lastNetherPortalPosition;

	@Shadow public abstract boolean isRemoved();

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
	public void onMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> ci) {
		if (!DimThread.MANAGER.isActive(destination.getServer()))
			return;

		if (DimThread.owns(Thread.currentThread())) {
			nbtCachedForMoveToWorld = writeNbt(new NbtCompound());
			destination.getServer().execute(
					() -> this.moveToWorld(destination)
			);
			this.removeFromDimension();
			ci.setReturnValue(null);
		}
	}

	/**
	 * Perform deep copy instead of clone() to fix the bug that handed item disappear while teleporting
	 */
	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;copyFrom(Lnet/minecraft/entity/Entity;)V"))
	private void onMoveToWorldCopyFrom(Entity instance, Entity original) {
		NbtCompound nbtCompound = nbtCachedForMoveToWorld;
		nbtCompound.remove("Dimension");
		instance.readNbt(nbtCompound);
		((EntityMixin) (Object) instance).netherPortalCooldown = ((EntityMixin) (Object) original).netherPortalCooldown;
		((EntityMixin) (Object) instance).lastNetherPortalPosition = ((EntityMixin) (Object) original).lastNetherPortalPosition;
	}

	@Inject(method = "removeFromDimension", at = @At("HEAD"), cancellable = true)
	private void onRemoveFromDimension(CallbackInfo ci) {
		if(isRemoved()) {
			ci.cancel();
		}
	}

	/**
	 * We check this because when we call this method in getServer().execute(), the entity has already been removed
	 */
	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isRemoved()Z"))
	private boolean onMoveToWorldIsRemoved(Entity instance) {
		return instance.isRemoved() && ((EntityMixin) (Object) instance).nbtCachedForMoveToWorld == null;
	}
}
