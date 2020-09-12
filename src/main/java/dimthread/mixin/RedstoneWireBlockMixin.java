package dimthread.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {

	@Shadow @Final public static IntProperty POWER;
	@Shadow @Final public static Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;

	@Shadow protected abstract BlockState method_27840(BlockView world, BlockState state, BlockPos pos);

	/**
	 * {@code RedstoneWireBlock#wiresGivePower} is not thread-safe since it's a global flag. To ensure
	 * no interference between threads the field is replaced with this thread local one.
	 *
	 * @see RedstoneWireBlock#emitsRedstonePower(BlockState)
	 * */
	private final ThreadLocal<Boolean> wiresGivePowerSafe = ThreadLocal.withInitial(() -> true);

	@Inject(method = "getReceivedRedstonePower", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/World;getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I",
			shift = At.Shift.BEFORE))
	private void getReceivedRedstonePowerBefore(World world, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
		this.wiresGivePowerSafe.set(false);
	}

	@Inject(method = "getReceivedRedstonePower", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/World;getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I",
			shift = At.Shift.AFTER))
	private void getReceivedRedstonePowerAfter(World world, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
		this.wiresGivePowerSafe.set(true);
	}

	/**
	 * @author DimensionalThreading (WearBlackAllDay)
	 * @reason Made redstone thread-safe, please inject in the caller.
	 */
	@Overwrite
	public boolean emitsRedstonePower(BlockState state) {
		return this.wiresGivePowerSafe.get();
	}

	/**
	 * @author DimensionalThreading (WearBlackAllDay)
	 * @reason Made redstone thread-safe, please inject in the caller.
	 */
	@Overwrite
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return !this.wiresGivePowerSafe.get() ? 0 : state.getWeakRedstonePower(world, pos, direction);
	}

	/**
	 * @author DimensionalThreading (WearBlackAllDay)
	 * @reason Made redstone thread-safe, please inject in the caller.
	 */
	@Overwrite
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if(!this.wiresGivePowerSafe.get() || direction == Direction.DOWN) {
			return 0;
		}

		int i = state.get(POWER);
		if(i == 0)return 0;
		return direction != Direction.UP && !this.method_27840(world, state, pos)
				.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite())).isConnected() ? 0 : i;
	}

}
