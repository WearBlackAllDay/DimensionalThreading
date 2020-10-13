package dimthread.init;

import dimthread.DimThread;
import dimthread.gamerule.BoolRule;
import dimthread.gamerule.IntRule;
import net.minecraft.world.GameRules;

public class ModGameRules {

	public static BoolRule ACTIVE;
	public static IntRule THREAD_COUNT;

	public static void registerGameRules() {
		ACTIVE = BoolRule.builder("active", GameRules.Category.UPDATES).setInitial(true)
				.setCallback(DimThread.MANAGER::setActive).build();

		THREAD_COUNT = IntRule.builder("thread_count", GameRules.Category.UPDATES).setInitial(3)
				.setBounds(1, Runtime.getRuntime().availableProcessors()).setCallback(DimThread.MANAGER::setThreadCount).build();
	}

}
