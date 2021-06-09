package wearblackallday.dimthread.init;

import wearblackallday.dimthread.DimThread;
import wearblackallday.dimthread.gamerule.BoolRule;
import wearblackallday.dimthread.gamerule.IntRule;
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
