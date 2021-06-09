package wearblackallday.dimthread.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public class BoolRule extends GameRule<GameRules.BooleanRule> {

	protected BoolRule(String name, GameRules.Category category, GameRules.Type<GameRules.BooleanRule> rule) {
		super(name, category, rule);
	}

	public static Builder builder(String name, GameRules.Category category) {
		return new Builder(name, category);
	}

	public static class Builder {
		private final String name;
		private final GameRules.Category category;

		private boolean initialValue = false;
		private BiConsumer<MinecraftServer, GameRules.BooleanRule> callback = (server, rule) -> {};

		private Builder(String name, GameRules.Category category) {
			this.name = name;
			this.category = category;
		}

		public Builder setInitial(boolean initial) {
			this.initialValue = initial;
			return this;
		}

		public Builder setCallback(BiConsumer<MinecraftServer, GameRules.BooleanRule> callback) {
			this.callback = callback;
			return this;
		}

		public BoolRule build() {
			return new BoolRule(this.name, this.category, GameRuleFactory.createBooleanRule(this.initialValue, this.callback));
		}
	}
}
