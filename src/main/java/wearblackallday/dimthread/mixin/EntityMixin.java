package wearblackallday.dimthread.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.AreaHelper;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wearblackallday.dimthread.DimThread;
import wearblackallday.dimthread.util.UncompletedTeleportTarget;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

	private NbtCompound nbtCachedForMoveToWorld;

	private UncompletedTeleportTarget uncompletedTeleportTargetForMoveToWorld;
	@Shadow
	protected abstract void removeFromDimension();


	@Shadow public abstract World getWorld();

	@Shadow public abstract NbtCompound writeNbt(NbtCompound nbt);

	@Shadow public abstract @Nullable Entity moveToWorld(ServerWorld destination);

	@Shadow private int netherPortalCooldown;

	@Shadow protected BlockPos lastNetherPortalPosition;

	@Shadow public abstract boolean isRemoved();

	@Shadow @Nullable public abstract MinecraftServer getServer();

	@Shadow public World world;

	@Shadow public abstract double getX();

	@Shadow public abstract double getY();

	@Shadow public abstract double getZ();

	@Shadow protected abstract Optional<BlockLocating.Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder);

	@Shadow protected abstract Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect);

	@Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

	@Shadow public abstract EntityPose getPose();

	@Shadow public abstract Vec3d getVelocity();

	@Shadow public abstract float getYaw();

	@Shadow public abstract float getPitch();

	@Shadow protected abstract @Nullable TeleportTarget getTeleportTarget(ServerWorld destination);

	@Shadow protected abstract void unsetRemoved();

	@Shadow public abstract void readNbt(NbtCompound nbt);

	@Shadow @Final private static Logger LOGGER;

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
			uncompletedTeleportTargetForMoveToWorld = createTeleportTargetUncompleted(destination);
			destination.getServer().execute(
					() -> {
						Entity entity = this.moveToWorld(destination);
						if(entity == null) {
							this.unsetRemoved();
							nbtCachedForMoveToWorld.putInt("PortalCooldown", this.netherPortalCooldown);
							this.readNbt(nbtCachedForMoveToWorld);
							this.uncompletedTeleportTargetForMoveToWorld = null;
							this.nbtCachedForMoveToWorld = null;
							this.world.spawnEntity((Entity) (Object) this);// if the teleporting failed, we need to add it back to the world
							LOGGER.debug("Failed to teleport {}, return it to its previous world", this);
						}
					}
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
		if(DimThread.MANAGER.isActive(getServer())) {
			NbtCompound nbtCompound = ((EntityMixin) (Object) original).nbtCachedForMoveToWorld;
			nbtCompound.remove("Dimension");
			instance.readNbt(nbtCompound);
			((EntityMixin) (Object) instance).netherPortalCooldown = ((EntityMixin) (Object) original).netherPortalCooldown;
			((EntityMixin) (Object) instance).lastNetherPortalPosition = ((EntityMixin) (Object) original).lastNetherPortalPosition;
		} else {
			instance.copyFrom(original);
		}
	}

	/**
	 * We have to use the data we cached when moveToWorld is called
	 * It's getting modified later
	 */
	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;"))
	private TeleportTarget onMoveToWorldGetTeleportTarget(@NotNull Entity instance, ServerWorld destination) {
		EntityMixin ins = ((EntityMixin) (Object) instance);
		if(DimThread.MANAGER.isActive(getServer())) {
			return ins.uncompletedTeleportTargetForMoveToWorld == null ? null : ins.uncompletedTeleportTargetForMoveToWorld.complete(destination);
		} else {
			return ins.getTeleportTarget(destination);
		}
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

	/**
	 * take a snapshot of some values so that codes modified it later doesn't affect teleporting
	 */
	private UncompletedTeleportTarget createTeleportTargetUncompleted(ServerWorld dest) {
		boolean isEndReturnPortal = world.getRegistryKey() == World.END && dest.getRegistryKey() == World.OVERWORLD;
		boolean isEndPortal = dest.getRegistryKey() == World.END;
		Vec3d velocity = getVelocity();
		float yaw = getYaw(), pitch = getPitch();
		if(!isEndPortal && !isEndReturnPortal) {
			boolean isNetherPortal = dest.getRegistryKey() == World.NETHER;
			boolean isNetherReturnPortal = world.getRegistryKey() == World.NETHER;
			if(!isNetherPortal && !isNetherReturnPortal) {
				return dest1 -> null;
			} else {
				WorldBorder border = dest.getWorldBorder();
				double scale = DimensionType.getCoordinateScaleFactor(world.getDimension(), dest.getDimension());
				BlockPos target = border.clamp(getX() * scale, getY(), getZ() * scale);
				BlockState portalState = world.getBlockState(lastNetherPortalPosition);
				Direction.Axis axis;
				Vec3d vec3d;
				EntityDimensions dimensions = getDimensions(getPose());
				if (portalState.contains(Properties.HORIZONTAL_AXIS)) {
					axis = portalState.get(Properties.HORIZONTAL_AXIS);
					BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle(this.lastNetherPortalPosition, axis, 21, Direction.Axis.Y, 21, (blockPos) -> this.world.getBlockState(blockPos) == portalState);
					vec3d = this.positionInPortal(axis, rectangle);
				} else {
					axis = Direction.Axis.X;
					vec3d = new Vec3d(0.5, 0.0, 0.0);
				}
				return dest1 -> getPortalRect(dest1, target, isNetherPortal, border).map((rect) -> AreaHelper.getNetherTeleportTarget(dest1, rect, axis, vec3d, dimensions, velocity, yaw, pitch)).orElse(null);
			}
		} else {
			return dest1 -> {
				BlockPos target = isEndPortal ? ServerWorld.END_SPAWN_POS : dest1.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, dest1.getSpawnPos());
				return new TeleportTarget(new Vec3d(target.getX() + 0.5, target.getY(), target.getZ() + 0.5), velocity, yaw, pitch);
			};
		}
	}
}
