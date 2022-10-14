package wearblackallday.dimthread.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;

/**
 * This is used for teleport target which is not completed right now, we are going to complete it in another thread
 */
public interface UncompletedTeleportTarget {
	@Nullable TeleportTarget complete(ServerWorld dest);
}
