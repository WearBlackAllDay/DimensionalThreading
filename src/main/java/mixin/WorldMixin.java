package mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import other.IMutableMainThread;

@Mixin(World.class)
public abstract class WorldMixin implements IMutableMainThread {

	@Mutable @Shadow @Final private Thread thread;

	@Override
	public Thread getMainThread() {
		return this.thread;
	}

	@Override
	public void setMainThread(Thread thread) {
		this.thread = thread;
	}

}
