package dimthread.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public class CrashInfo {

	private final ServerWorld world;
	private final Throwable throwable;

	public CrashInfo(ServerWorld world, Throwable throwable) {
		this.world = world;
		this.throwable = throwable;
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public Throwable getThrowable() {
		return this.throwable;
	}

	public void crash(String title) {
		CrashReport report = CrashReport.create(this.getThrowable(), title);
		this.getWorld().addDetailsToCrashReport(report);
		throw new CrashException(report);
	}

}
