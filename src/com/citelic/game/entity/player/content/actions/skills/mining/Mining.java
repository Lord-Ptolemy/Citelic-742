package com.citelic.game.entity.player.content.actions.skills.mining;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.map.objects.GameObject;
import com.citelic.utility.Utilities;

public final class Mining extends MiningBase {

	private static final int[] UNCUT_GEMS = { 1623, 1619, 1621, 1617 };
	private GameObject rock;
	private RockDefinitions definitions;
	private PickAxeDefinitions axeDefinitions;
	private boolean usedDeplateAurora;

	public Mining(GameObject rock, RockDefinitions definitions) {
		this.rock = rock;
		this.definitions = definitions;
	}

	private void addOre(Player player) {
		double xpBoost = 0;
		int idSome = 0;
		if (definitions == RockDefinitions.Granite_Ore) {
			idSome = Utilities.getRandom(2) * 2;
			if (idSome == 2) {
				xpBoost += 10;
			} else if (idSome == 4) {
				xpBoost += 25;
			}
		} else if (definitions == RockDefinitions.Sandstone_Ore) {
			idSome = Utilities.getRandom(3) * 2;
			xpBoost += idSome / 2 * 10;
		}
		double totalXp = definitions.getXp() + xpBoost;
		if (hasMiningSuit(player)) {
			totalXp *= 1.025;
		}
		player.getSkills().addXp(Skills.MINING, totalXp);
		if (definitions.getOreId() != -1) {
			player.getInventory().addItem(definitions.getOreId() + idSome, 1);
			String oreName = ItemDefinitions
					.getItemDefinitions(definitions.getOreId() + idSome)
					.getName().toLowerCase();
			player.getPackets().sendGameMessage(
					"You mine some " + oreName + ".", true);
			if (Utilities.random(150) == 0) {
				player.getInventory()
						.addItem(
								Mining.UNCUT_GEMS[Utilities
										.random(Mining.UNCUT_GEMS.length - 2)],
								1);
			} else if (Utilities.random(5000) == 0) {
				player.getInventory()
						.addItem(
								Mining.UNCUT_GEMS[Utilities
										.random(Mining.UNCUT_GEMS.length)],
								1);
			}
		}
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
		int summoningBonus = 0;
		if (player.getFamiliar() != null) {
			if (player.getFamiliar().getId() == 7342
					|| player.getFamiliar().getId() == 7342) {
				summoningBonus += 10;
			} else if (player.getFamiliar().getId() == 6832
					|| player.getFamiliar().getId() == 6831) {
				summoningBonus += 1;
			}
		}
		int mineTimer = definitions.getOreBaseTime()
				- (player.getSkills().getLevel(Skills.MINING) + summoningBonus)
				- Utilities.getRandom(axeDefinitions.getPickAxeTime());
		if (mineTimer < 1 + definitions.getOreRandomTime()) {
			mineTimer = 1 + Utilities.getRandom(definitions.getOreRandomTime());
		}
		mineTimer /= player.getAuraManager().getMininingAccurayMultiplier();
		return mineTimer;
	}

	private boolean hasMiningLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(Skills.MINING)) {
			player.getPackets().sendGameMessage(
					"You need a mining level of " + definitions.getLevel()
							+ " to mine this rock.");
			return false;
		}
		return true;
	}

	private boolean hasMiningSuit(Player player) {
		if (player.getEquipment().getHatId() == 20789
				&& player.getEquipment().getChestId() == 20791
				&& player.getEquipment().getLegsId() == 20790
				&& player.getEquipment().getBootsId() == 20788)
			return true;
		return false;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(axeDefinitions.getAnimationId()));
		return checkRock(player);
	}

	@Override
	public int processWithDelay(Player player) {
		addOre(player);
		if (definitions.getEmptyId() != -1) {
			if (!usedDeplateAurora
					&& 1 + Math.random() < player.getAuraManager()
							.getChanceNotDepleteMN_WC()) {
				usedDeplateAurora = true;
			} else if (Utilities.getRandom(definitions
					.getRandomLifeProbability()) == 0) {
				Engine.spawnTemporaryObject(
						new GameObject(definitions.getEmptyId(),
								rock.getType(), rock.getRotation(),
								rock.getX(), rock.getY(), rock.getPlane()),
						definitions.respawnDelay * 600, false);
				player.setNextAnimation(new Animation(-1));
				return -1;
			}
		}
		if (!player.getInventory().hasFreeSlots()
				&& definitions.getOreId() != -1) {
			player.setNextAnimation(new Animation(-1));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return -1;
		}
		return getMiningDelay(player);
	}

	@Override
	public boolean start(Player player) {
		axeDefinitions = MiningBase.getPickAxeDefinitions(player);
		if (!checkAll(player))
			return false;
		player.getPackets().sendGameMessage(
				"You swing your pickaxe at the rock.", true);
		setActionDelay(player, getMiningDelay(player));
		return true;
	}

	public static enum RockDefinitions {

		Clay_Ore(1, 5, 434, 10, 1, 11552, 5, 0), Copper_Ore(1, 17.5, 436, 10,
				1, 11552, 5, 0), Tin_Ore(1, 17.5, 438, 15, 1, 11552, 5, 0), Blurite_Ore(
				10, 17.5, 668, 15, 1, 11552, 7, 0), Iron_Ore(15, 35, 440, 15,
				1, 11552, 10, 0), Sandstone_Ore(35, 30, 6971, 30, 1, 11552, 10,
				0), Silver_Ore(20, 40, 442, 25, 1, 11552, 20, 0), Coal_Ore(30,
				50, 453, 50, 10, 11552, 30, 0), Granite_Ore(45, 50, 6979, 50,
				10, 11552, 20, 0), Gold_Ore(40, 60, 444, 80, 20, 11554, 40, 0), Mithril_Ore(
				55, 80, 447, 100, 20, 11552, 60, 0), Adamant_Ore(70, 95, 449,
				130, 25, 11552, 180, 0), Runite_Ore(85, 125, 451, 150, 30,
				11552, 360, 0), LRC_Coal_Ore(77, 50, 453, 50, 10, -1, -1, -1), LRC_Gold_Ore(
				80, 60, 444, 40, 10, -1, -1, -1), CRASHED_STAR(10, 150, 13727,
				2, 30, -1, -1, -1);

		private int level;
		private double xp;
		private int oreId;
		private int oreBaseTime;
		private int oreRandomTime;
		private int emptySpot;
		private int respawnDelay;
		private int randomLifeProbability;

		private RockDefinitions(int level, double xp, int oreId,
				int oreBaseTime, int oreRandomTime, int emptySpot,
				int respawnDelay, int randomLifeProbability) {
			this.level = level;
			this.xp = xp;
			this.oreId = oreId;
			this.oreBaseTime = oreBaseTime;
			this.oreRandomTime = oreRandomTime;
			this.emptySpot = emptySpot;
			this.respawnDelay = respawnDelay;
			this.randomLifeProbability = randomLifeProbability;
		}

		public int getEmptyId() {
			return emptySpot;
		}

		public int getLevel() {
			return level;
		}

		public int getOreBaseTime() {
			return oreBaseTime;
		}

		public int getOreId() {
			return oreId;
		}

		public int getOreRandomTime() {
			return oreRandomTime;
		}

		public int getRandomLifeProbability() {
			return randomLifeProbability;
		}

		public int getRespawnDelay() {
			return respawnDelay;
		}

		public double getXp() {
			return xp;
		}
	}
}
