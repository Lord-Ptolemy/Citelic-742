package com.citelic.game.entity.player.content.actions.skills.mining;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.impl.others.LivingRock;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.utility.Utilities;

public class LivingMineralMining extends MiningBase {

	private LivingRock rock;
	private PickAxeDefinitions axeDefinitions;

	public LivingMineralMining(LivingRock rock) {
		this.rock = rock;
	}

	private void addOre(Player player) {
		player.getSkills().addXp(Skills.MINING, 25);
		player.getInventory().addItem(15263, Utilities.random(5, 25));
		player.getPackets().sendGameMessage(
				"You manage to mine some living minerals.", true);
	}

	private boolean checkAll(Player player) {
		if (axeDefinitions == null) {
			player.getPackets()
					.sendGameMessage(
							"You do not have a pickaxe or do not have the required level to use the pickaxe.");
			return false;
		}
		if (!hasMiningLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return false;
		}
		if (!rock.canMine(player)) {
			player.getPackets()
					.sendGameMessage(
							"You must wait at least one minute before you can mine a living rock creature that someone else defeated.");
			return false;
		}
		return true;
	}

	private boolean checkRock(Player player) {
		return !rock.hasFinished();
	}

	private int getMiningDelay(Player player) {
		int oreBaseTime = 50;
		int oreRandomTime = 20;
		int mineTimer = oreBaseTime
				- player.getSkills().getLevel(Skills.MINING)
				- Utilities.getRandom(axeDefinitions.getPickAxeTime());
		if (mineTimer < 1 + oreRandomTime) {
			mineTimer = 1 + Utilities.getRandom(oreRandomTime);
		}
		mineTimer /= player.getAuraManager().getMininingAccurayMultiplier();
		return mineTimer;
	}

	private boolean hasMiningLevel(Player player) {
		if (73 > player.getSkills().getLevel(Skills.MINING)) {
			player.getPackets().sendGameMessage(
					"You need a mining level of 73 to mine this rock.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(axeDefinitions.getAnimationId()));
		return checkRock(player);
	}

	@Override
	public int processWithDelay(Player player) {
		addOre(player);
		rock.takeRemains();
		player.setNextAnimation(new Animation(-1));
		return -1;
	}

	@Override
	public boolean start(Player player) {
		axeDefinitions = MiningBase.getPickAxeDefinitions(player);
		if (!checkAll(player))
			return false;
		setActionDelay(player, getMiningDelay(player));
		return true;
	}
}
