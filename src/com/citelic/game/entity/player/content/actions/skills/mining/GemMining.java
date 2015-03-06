package com.citelic.game.entity.player.content.actions.skills.mining;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.map.objects.GameObject;
import com.citelic.utility.Utilities;

public class GemMining extends MiningBase {

	private GameObject rock;
	private PickAxeDefinitions axeDefinitions;

	public GemMining(GameObject rock) {
		this.rock = rock;
	}

	private void addOre(Player player) {
		player.getSkills().addXp(Skills.MINING, 65);
		double random = Utilities.random(0, 100);
		player.getInventory().addItem(
				random <= 3.5 ? 1617 : random <= 3.7 ? 1619
						: random <= 4.1 ? 1621 : random <= 7 ? 1623
								: random <= 12.7 ? 1629 : random <= 21.9 ? 1627
										: 1625, 1);
		player.getPackets().sendGameMessage("You receive a gem.");
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
		return true;
	}

	private boolean checkRock(Player player) {
		return Engine.getRegion(rock.getRegionId()).containsObject(
				rock.getId(), rock);
	}

	private int getMiningDelay(Player player) {
		int mineTimer = 50 - player.getSkills().getLevel(Skills.MINING)
				- Utilities.getRandom(axeDefinitions.getPickAxeTime());
		if (mineTimer < 1 + 10) {
			mineTimer = 1 + Utilities.getRandom(10);
		}
		mineTimer /= player.getAuraManager().getMininingAccurayMultiplier();
		return mineTimer;
	}

	private boolean hasMiningLevel(Player player) {
		if (45 > player.getSkills().getLevel(Skills.MINING)) {
			player.getPackets().sendGameMessage(
					"You need a mining level of 45 to mine this rock.");
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
		Engine.spawnObjectTemporary(
				new GameObject(11193, rock.getType(), rock.getRotation(), rock
						.getX(), rock.getY(), rock.getZ()), 60000);
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return -1;
		}
		return -1;
	}

	@Override
	public boolean start(Player player) {
		axeDefinitions = MiningBase.getPickAxeDefinitions(player);
		if (!checkAll(player))
			return false;
		player.getPackets().sendGameMessage(
				"You swing your pickaxe at the rock.");
		setActionDelay(player, getMiningDelay(player));
		return true;
	}
}