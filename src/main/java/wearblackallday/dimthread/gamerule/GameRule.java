package wearblackallday.dimthread.gamerule;

import wearblackallday.dimthread.DimThread;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public abstract class GameRule<T extends GameRules.Rule<T>> {

	private final GameRules.Key<T> key;
	private final GameRules.Type<T> rule;

	public GameRule(String name, GameRules.Category category, GameRules.Type<T> rule) {
		this.key = GameRuleRegistry.register(DimThread.MOD_ID + "_" + name, category, rule);
		this.rule = rule;
	}

	public GameRules.Key<T> getKey() {
		return this.key;
	}

	public GameRules.Type<T> getRule() {
		return this.rule;
	}

}
