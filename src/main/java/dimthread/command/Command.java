package dimthread.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public abstract class Command {

	public abstract String getName();

	public abstract int getRequiredPermissionLevel();

	public abstract void build(LiteralArgumentBuilder<ServerCommandSource> builder);

	public abstract boolean isDedicatedServerOnly();

	protected final void sendFeedback(CommandContext<ServerCommandSource> context, String message, boolean showOps) {
		context.getSource().sendFeedback(new LiteralText(message), showOps);
	}

}
