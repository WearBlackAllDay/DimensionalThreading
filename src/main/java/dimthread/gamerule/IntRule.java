package dimthread.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public class IntRule extends GameRule<GameRules.IntRule> {

	protected IntRule(String name, GameRules.Category category, GameRules.Type<GameRules.IntRule> rule) {
		super(name, category, rule);
	}

	public static Builder builder(String name, GameRules.Category category) {
		return new Builder(name, category);
	}

	public static class Builder {
		private final String name;
		private final GameRules.Category category;

		private int initialValue = 0;
		private int minimumValue = Integer.MIN_VALUE;
		private int maximumValue = Integer.MAX_VALUE;
		private BiConsumer<MinecraftServer, GameRules.IntRule> callback = (server, rule) -> {};

		private Builder(String name, GameRules.Category category) {
			this.name = name;
			this.category = category;
		}

		public Builder setInitial(int initial) {
			this.initialValue = initial;
			return this;
		}

		public Builder setMin(int min) {
			this.minimumValue = min;
			return this;
		}

		public Builder setMax(int max) {
			this.maximumValue = max;
			return this;
		}

		public Builder setBounds(int min, int max) {
			return this.setMin(min).setMax(max);
		}

		public Builder setCallback(BiConsumer<MinecraftServer, GameRules.IntRule> callback) {
			this.callback = callback;
			return this;
		}

		public IntRule build() {
			return new IntRule(this.name, this.category, GameRuleFactory.createIntRule(
					this.initialValue, this.minimumValue, this.maximumValue, this.callback));
		}
	}

}
