package dimthread.mixin;

import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dimthread.other.DimThread;
import dimthread.other.IMutableServerThread;

@Mixin(ThreadExecutor.class)
public abstract class ThreadExecutorMixin implements IMutableServerThread {

	@Inject(method = "isOnThread", at = @At("HEAD"), cancellable = true)
	private void isOnThread(CallbackInfoReturnable<Boolean> ci) {
		if(DimThread.owns(Thread.currentThread())) {
			//ci.setReturnValue(true);
		}
	}

}
