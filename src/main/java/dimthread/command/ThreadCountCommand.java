package dimthread.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dimthread.DimThread;
import dimthread.thread.ThreadPool;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ThreadCountCommand extends Command {

	@Override
	public String getName() {
		return "thread_count";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
		builder
				.executes(this::printThreadCount)
				.then(CommandManager.argument("count", IntegerArgumentType.integer())
					.executes(context -> setThreadCount(context, IntegerArgumentType.getInteger(context, "count"))));
	}

	private int printThreadCount(CommandContext<ServerCommandSource> context) {
		int count = DimThread.THREAD_POOL.getThreadCount();
		this.sendFeedback(context, "Dimensional Threading is using " + count + (count == 1 ? " thread." : "threads."), false);
		return 1;
	}

	private int setThreadCount(CommandContext<ServerCommandSource> context, int count) {
		int maxThreads = Runtime.getRuntime().availableProcessors();

		if(count < 1 || count > maxThreads) {
			this.sendFeedback(context, "Count must be between 1 and " + maxThreads + ".", false);
			return 0;
		} else {
			DimThread.THREAD_POOL.shutdown();
			DimThread.THREAD_POOL = new ThreadPool(count);
			this.printThreadCount(context);
			return 1;
		}
	}

	@Override
	public boolean isDedicatedServerOnly() {
		return false;
	}

}
