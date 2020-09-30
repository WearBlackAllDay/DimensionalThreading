package dimthread.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimthread.DimThread;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class Commands {

	public static final Commands INSTANCE = new Commands();

	private final List<Command> commands = new ArrayList<>();

	public void registerCommand(Command command) {
		this.commands.add(command);
	}

	public void registerBrigadierCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment env) {
		for(Command command: this.commands) {
			if(!command.isDedicatedServerOnly() || env == CommandManager.RegistrationEnvironment.DEDICATED
				|| env == CommandManager.RegistrationEnvironment.ALL) {
				LiteralArgumentBuilder<ServerCommandSource> builder = LiteralArgumentBuilder.literal(command.getName());
				builder.requires((sender) -> sender.hasPermissionLevel(command.getRequiredPermissionLevel()));
				command.build(builder);
				dispatcher.register(literal(DimThread.MOD_ID).then(builder));
			}
		}
	}

}
