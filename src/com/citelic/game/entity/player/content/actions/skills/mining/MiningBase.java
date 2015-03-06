package com.citelic.game.entity.player.content.actions.skills.mining;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;

public abstract class MiningBase extends Action {

	public static PickAxeDefinitions getPickAxeDefinitions(Player player) {
		for (PickAxeDefinitions definitions : PickAxeDefinitions.values()) {
			if (player.getEquipment().getWeaponId() == definitions
					.getPickAxeId()
					|| player.getInventory().containsItem(
							definitions.getPickAxeId(), 1)) {
				if (player.getSkills().getLevel(Skills.MINING) >= definitions
						.getLevelRequried())
					return definitions;
			}
		}
		if (player.getInventory().containsItemToolBelt(1265))
			return PickAxeDefinitions.BRONZE;
		return null;
	}

	public static void propect(final Player player, final String endMessage) {
		MiningBase.propect(player, "You examine the rock for ores....",
				endMessage);
	}

	public static void propect(final Player player, String startMessage,
			final String endMessage) {
		player.getPackets().sendGameMessage(startMessage, true);
		player.lock(5);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage(endMessage);
			}
		}, 4);
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

	public enum PickAxeDefinitions {

		ADZ(13661, 61, 10222, 13),

		DRAGON(15259, 61, 12189, 13),

		RUNE(1275, 41, 624, 10),

		ADAMANT(1271, 31, 628, 7),

		MITHRIL(1273, 21, 629, 5),

		STEEL(1269, 6, 627, 3),

		IRON(1267, 1, 626, 2),

		BRONZE(1265, 1, 625, 1);

		private int pickAxeId, levelRequried, animationId, pickAxeTime;

		private PickAxeDefinitions(int pickAxeId, int levelRequried,
				int animationId, int pickAxeTime) {
			this.pickAxeId = pickAxeId;
			this.levelRequried = levelRequried;
			this.animationId = animationId;
			this.pickAxeTime = pickAxeTime;
		}

		public int getAnimationId() {
			return animationId;
		}

		public int getLevelRequried() {
			return levelRequried;
		}

		public int getPickAxeId() {
			return pickAxeId;
		}

		public int getPickAxeTime() {
			return pickAxeTime;
		}
	}

}
