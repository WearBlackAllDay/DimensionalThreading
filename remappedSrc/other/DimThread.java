package dimthread.other;

import net.minecraft.server.world.ServerWorld;

public class DimThread {

	public static final String MOD_ID = "dimthread";

	public static void attach(Thread thread, ServerWorld world) {
		thread.setName(MOD_ID + "_" + world.getDimension().getSkyProperties().getPath());
	}

	public static boolean owns(Thread thread) {
		return thread.getName().startsWith(MOD_ID);
	}

}
