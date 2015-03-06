package com.citelic.game.entity.player.content.controllers.impl.distractions.battleterrace;

import java.util.ArrayList;
import java.util.List;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Equipment;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning;
import com.citelic.game.entity.player.content.miscellaneous.pets.Pets;
import com.citelic.game.entity.player.item.Item;

public final class BattleTerrace {

	public static final List<Player> waiting = new ArrayList<Player>();
	public static final List<Player> playing = new ArrayList<Player>();

	public static int[] warriorWeapons = new int[] { 1323, 1325, 1327, 1329,
			1331, 1333, 1215, 4587, 4747, 4755, 4726, 4718, 4151, 11716, 11730,
			10887, 14484, 11696, 11700, 11698, 11694 };

	public static int[] archerWeapons = new int[] { 841, 864, 843, 863, 849,
			865, 853, 866, 857, 867, 861, 868, 4212, 19146, 19143, 19149, 4734,
			11235, 20171, 24474, 15241 };

	public static int[] archerAmmo = new int[] { 882, -1, 884, -1, 886, -1,
			888, -1, 890, -1, 892, -1, -1, 19157, 19152, 19162, 4740, 11212,
			-1, -1, 15243 };

	public static int[] mageWeapons = new int[] { 1379, 1381, 1405, 1409, 3054,
			4675, 4710, 6563, 6914, 21490, 21496, 21500, 21504, 21505, 24197,
			24201, 21777, 22347, 13867, 15486, 22494 };

	public static void addHintIcon(Player player, Player target) {
		player.getHintIconsManager().addHintIcon(target, 0, -1, false);
	}

	public static Player getTarget(Player toPlayer) {
		int combatLevel = toPlayer.getSkills().getCombatLevelWithSummoning();
		for (Player player : playing) {
			if (toPlayer == player || toPlayer.battleTarget != null)
				continue;
			if (Math.abs(player.getSkills().getCombatLevelWithSummoning()
					- combatLevel) <= 10
					|| Math.abs(player.getSkills()
							.getCombatLevelWithSummoning() - combatLevel) >= 10)
				return player;
		}
		return null;
	}

	public static void joinLobby(Player player) {
		if (player.warriorLevel < 1 && player.archerLevel < 1
				&& player.mageLevel < 1) {
			player.getDialogueManager()
					.startDialogue("SimpleNPCMessage", 7600,
							"You must go through the tutorial of this minigame before you can play.");
			return;
		}
		if (player.selectedClass == null) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 7600,
					"You must select a class before you play.");
			return;
		}
		if (player.getFamiliar() != null || player.getPet() != null
				|| Summoning.hasPouch(player) || Pets.hasPet(player)) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 7600,
					"Summonings are not allowed in this minigame!");
			return;
		}
		if (player.getEquipment().wearingArmour()) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 7600,
					"You cannot wear equipment into this minigame!");
			return;
		}
		if (player.getInventory().getFreeSlots() < 28) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 7600,
					"You cannot take items into this minigame!");
			return;
		}
		player.getControllerManager().startController("battleTerraceGame", 1);
		playing.add(player);
		startWeapons(player);
		if (player.selectedClass == "Magician") {
			player.getInventory().addItem(554, (1000 / 21) * player.mageLevel);
			player.getInventory().addItem(555, (1000 / 21) * player.mageLevel);
			player.getInventory().addItem(556, (1000 / 21) * player.mageLevel);
			player.getInventory().addItem(557, (1000 / 21) * player.mageLevel);
			player.getInventory().addItem(558, (1000 / 21) * player.mageLevel);
			player.getInventory().addItem(561, (1000 / 21) * player.mageLevel);
			player.getInventory().addItem(566, (1000 / 21) * player.mageLevel);
			if (player.mageLevel <= 5) {
				player.getInventory().addItem(562,
						(1000 / 21) * player.mageLevel);
			} else if (player.mageLevel <= 10) {
				player.getInventory().addItem(560,
						(1000 / 21) * player.mageLevel);
			} else if (player.mageLevel <= 15) {
				player.getInventory().addItem(565,
						(1000 / 21) * player.mageLevel);
			} else if (player.mageLevel <= 21) {
				player.getInventory().addItem(562,
						(1000 / 21) * player.mageLevel);
				player.getInventory().addItem(560,
						(1000 / 21) * player.mageLevel);
				player.getInventory().addItem(565,
						(1000 / 21) * player.mageLevel);
			}
		}
	}

	public static boolean needAmmo(int item) {
		switch (item) {
		case 841:
		case 843:
		case 849:
		case 853:
		case 857:
		case 861:
		case 19146:
		case 19143:
		case 19149:
		case 4734:
		case 11235:
		case 15241:
		case 4212:
		case 24474:
		case 20171:
		case 21777:
			return true;
		default:
			return false;
		}
	}

	public static void removeHintIcon(Player player) {
		player.getHintIconsManager().removeUnsavedHintIcon();
	}

	public static final Player selectTarget(final Player player) {
		player.battleTarget = getTarget(player);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {

				if (player.battleTarget == null) {
					player.battleTarget = getTarget(player);
					if (player.battleTarget != null) {
						startTargetInterface(player, player.battleTarget, true);
						addHintIcon(player, player.battleTarget);
					}
				}

				if (playing.size() < 1) {
					stop();
				}

				if (!playing.contains(player)) {
					stop();
				}
			}
		}, 0, 5);
		if (player.battleTarget != null) {
			startTargetInterface(player, player.battleTarget, true);
		} else {
			startTargetInterface(player, player.battleTarget, false);
		}
		return player.battleTarget;
	}

	public static void setAmmo(Player player, Item hood) {
		player.getEquipment().getItems().set(Equipment.SLOT_ARROWS, hood);
		player.getEquipment().refresh(Equipment.SLOT_ARROWS);
		player.getGlobalPlayerUpdate().generateAppearenceData();
	}

	public static void setWeapon(Player player, Item hood) {
		player.getEquipment().getItems().set(Equipment.SLOT_WEAPON, hood);
		player.getEquipment().refresh(Equipment.SLOT_WEAPON);
		player.getGlobalPlayerUpdate().generateAppearenceData();
	}

	public static void startTargetInterface(Player player, Player target,
			boolean Target) {
		player.getInterfaceManager().closeOverlay(false);
		player.getInterfaceManager().sendOverlay(
				1296,
				player.getInterfaceManager().hasRezizableScreen() ? false
						: true);
		player.getPackets().sendIComponentText(1296, 19, "Target Name");
		player.getPackets().sendIComponentText(1296, 20, "Target Level");
		if (Target) {
			player.getPackets().sendIComponentText(1296, 25,
					target.getDisplayName().toUpperCase());
			player.getPackets().sendIComponentText(1296, 26,
					"" + target.getSkills().getCombatLevel() + "");
			addHintIcon(player, target);
		} else {
			player.getPackets().sendIComponentText(1296, 25, "No Target");
			player.getPackets().sendIComponentText(1296, 26, "No Target");
		}
		player.getPackets().sendIComponentText(1296, 24, "Level Potential");
		switch (player.selectedClass) {
		case "Warrior":
			player.getPackets().sendIComponentText(1296, 27,
					player.warriorKills + "/" + player.warriorLevel);
			break;
		case "Archer":
			player.getPackets().sendIComponentText(1296, 27,
					player.archerKills + "/" + player.archerLevel);
			break;
		case "Magician":
			player.getPackets().sendIComponentText(1296, 27,
					player.mageKills + "/" + player.mageLevel);
			break;
		}
	}

	public static void startWeapons(Player player) {
		switch (player.selectedClass) {
		case "Warrior":
			setWeapon(player, new Item(warriorWeapons[player.warriorLevel - 1],
					1));
			break;
		case "Archer":
			int weapon = archerWeapons[player.archerLevel - 1];
			if (needAmmo(weapon)) {
				setWeapon(player, new Item(weapon, 1));
			} else {
				setWeapon(player, new Item(weapon, 5000));
			}
			int ammo = archerAmmo[player.archerLevel - 1];
			if (ammo > 0) {
				setAmmo(player, new Item(ammo, 5000));
			}
			break;
		case "Magician":
			weapon = mageWeapons[player.mageLevel - 1];
			if (needAmmo(weapon)) {
				setWeapon(player, new Item(weapon, 1));
				player.getInventory().addItem(21773,
						(1000 / 21) * player.mageLevel);
			} else {
				setWeapon(player, new Item(weapon, 1));
			}
			break;
		}
	}
}