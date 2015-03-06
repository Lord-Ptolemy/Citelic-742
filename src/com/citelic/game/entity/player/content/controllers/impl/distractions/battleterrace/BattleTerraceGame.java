package com.citelic.game.entity.player.content.controllers.impl.distractions.battleterrace;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.consumables.Potions;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class BattleTerraceGame extends Controller {

	private int foodOrdinance[] = new int[] { 379, 365, 373, 7946, 385, 391,
			15272 };

	private int warriorPotions[] = new int[] { 113, 2428, 2432, 2436, 2440,
			2442, 15308, 15312, 15316, 15332 };
	private int archerPotions[] = new int[] { 2444, 2432, 2442, 15324, 15316,
			15332 };
	private int magePotions[] = new int[] { 3040, 2432, 2442, 15320, 15316,
			15332 };
	// Warrior Gear
	private int commonWarriorGear[] = new int[] { 1153, 1115, 1067, 4121, 1121,
			1071, 1159, 1163, 1127, 1079, 4131, 7460, 7459 };

	private int mediumWarriorGear[] = new int[] { 21787, 4720, 4722, 4716,
			7462, 7461, 4753, 4757, 4759, 4745, 4749, 4751, 14479, 4087, 11335,
			11732 };

	private int rareWarriorGear[] = new int[] { 13896, 13884, 13890, 20139,
			20143, 20135, 6585 };

	// Archer Gear
	private int commonArcherGear[] = new int[] { 10498, 1095, 1129, 1131, 1167,
			1169, 1135, 1099, 12936, 2499, 2493, 12943, 7460, 7459 };

	private int mediumArcherGear[] = new int[] { 10499, 2501, 2495, 12950,
			2503, 2497, 12957, 24382, 24379, 24388, 7462, 7461 };

	private int rareArcherGear[] = new int[] { 20068, 4736, 4738, 4732, 20147,
			20151, 20155, 6585 };

	// Mage Gear
	private int commonMageGear[] = new int[] { 4091, 4101, 4111, 4093, 4103,
			4113, 577, 1011, 579, 2579, 7460, 7459 };

	private int mediumMageGear[] = new int[] { 7400, 7399, 7398, 2414, 2412,
			2413, 6920, 6916, 6924, 6918, 6889, 7462, 7461 };

	private int rareMageGear[] = new int[] { 6585, 4712, 4714, 4708, 20159,
			20163, 20167 };

	private Tile[] possibleSpawns = { new Tile(1663, 5694, 0),
			new Tile(1678, 5700, 0), new Tile(1688, 5715, 0),
			new Tile(1662, 5731, 0), new Tile(1638, 5722, 0),
			new Tile(1626, 5698, 0), new Tile(1634, 5674, 0),
			new Tile(1660, 5660, 0), new Tile(1694, 5675, 0),
			new Tile(1700, 5694, 0), new Tile(1678, 5688, 0),
			new Tile(1676, 5690, 0), new Tile(1674, 5688, 0),
			new Tile(1676, 5686, 0), new Tile(1669, 5704, 0),
			new Tile(1667, 5706, 0), new Tile(1664, 5704, 0),
			new Tile(1667, 5702, 0), new Tile(1653, 5703, 0),
			new Tile(1651, 5705, 0), new Tile(1649, 5703, 0),
			new Tile(1651, 5701, 0), new Tile(1656, 5685, 0),
			new Tile(1658, 5683, 0), new Tile(1656, 5681, 0),
			new Tile(1654, 5683, 0), new Tile(1657, 5669, 0),
			new Tile(1641, 5683, 0), new Tile(1637, 5703, 0),
			new Tile(1646, 5721, 0), new Tile(1670, 5719, 0),
			new Tile(1683, 5707, 0), new Tile(1692, 5688, 0),
			new Tile(1675, 5675, 0) };

	private int[] regionIds = { 6745, 6489, 6488, 6744 };

	private Tile lobbyCoords = new Tile(1718, 5598, 0);

	public void leaveBattleTerrace(boolean object) {
		if (object) {
			player.getPackets().sendGameMessage(
					"You left the unfinished game of Battle Terrace.");
			for (Player warrior : Engine.getPlayers()) {
				for (int i = 0; i < regionIds.length; i++) {
					if (warrior.getRegionId() == regionIds[i]) {
						warrior.getPackets().sendGameMessage(
								player.getDisplayName()
										+ " has left the game Battle Terrace");
					}
				}
			}
		}
		player.lock(2);
		player.setCanPvp(false);
		player.getInventory().reset();
		player.getEquipment().reset();
		player.getEquipment().init();
		player.setNextTile(new Tile(lobbyCoords));
		removeController();
	}

	public void leaveGame(Player player, boolean winner) {
		BattleTerrace.removeHintIcon(player);
		player.lock(2);
		player.setCanPvp(false);
		player.getInventory().reset();
		player.getEquipment().reset();
		player.getEquipment().init();
		removeController();
		player.getInventory().reset();
		player.getEquipment().reset();
		player.getEquipment().refresh();
		player.reset();
		player.getInterfaceManager().closeOverlay(false);
		BattleTerrace.playing.remove(player);
		player.setNextTile(new Tile(1718, 5599, 0));
		if (winner)
			player.getPackets()
					.sendGameMessage("You have quit battle terrace.");
		if (!winner)
			player.getPackets().sendGameMessage(
					"You have been defeated in bettle terrace.");
	}

	@Override
	public boolean login() {
		leaveGame(player, false);
		forceClose();
		return false;
	}

	@Override
	public boolean logout() {
		leaveGame(player, false);
		forceClose();
		return false;
	}

	@Override
	public void process() {
		// process crap here
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		synchronized (this) {
			synchronized (player.getControllerManager().getController()) {
			}
			switch (interfaceId) {
			case 271:
			case 749:
				player.getPackets().sendGameMessage(
						"You can't use prayers in this minigame.");
				return false;
			case 387:
				switch (componentId) {
				case 15:
				case 37:
					player.getPackets().sendGameMessage(
							"You can't unequip your weapon.");
					return false;
				}
				break;

			}
		}
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport away from Battle Terrace.");
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport away from Battle Terrace.");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 30143) {
			takeOrdinance(player);
		}
		if (object.getId() == 30144) {
			leaveGame(player, true);
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer.battleTarget == player) {
						if (killer != null) {
							switch (killer.selectedClass) {
							case "Warrior":
								killer.warriorKills++;
								if (killer.warriorKills >= killer.warriorLevel
										&& killer.warriorLevel < 21) {
									killer.warriorKills = 0;
									killer.warriorLevel++;
									// sendNPCDialogue(npcId, 9827,
									// "You have ad, warrior level is currently "+
									// player.warriorLevel +"." );
									killer.getPackets()
											.sendGameMessage(
													"You have advanced a warriors level.");
									BattleTerrace.startWeapons(killer);
								}
								break;
							case "Archer":
								killer.archerKills++;
								if (killer.archerKills >= killer.archerLevel
										&& killer.archerLevel < 21) {
									killer.archerKills = 0;
									killer.archerLevel++;
									killer.getPackets()
											.sendGameMessage(
													"You have advanced an archers level.");
									BattleTerrace.startWeapons(killer);
								}
								break;
							case "Magician":
								killer.mageKills++;
								if (killer.mageKills >= killer.mageLevel
										&& killer.mageLevel < 21) {
									killer.mageKills = 0;
									killer.mageLevel++;
									killer.getPackets()
											.sendGameMessage(
													"You have advanced a magicians level.");
									BattleTerrace.startWeapons(killer);
								}
								break;
							}
						}
					}
					killer.battlePoints += 2;
					BattleTerrace.removeHintIcon(player);
					BattleTerrace.removeHintIcon(killer);
					leaveGame(player, false);
					for (Player P : BattleTerrace.playing) {
						if (P.battleTarget == player) {
							BattleTerrace.selectTarget(P);
						}
					}
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean processCommand(String command, boolean console,
			boolean clientCommand) {
		if (!player.isAdministrator()) {
			player.getPackets().sendGameMessage("You can't use commands here!");
			return false;
		}
		return true;
	}

	@Override
	public void start() {
		player.setNextTile(new Tile(possibleSpawns[Utilities
				.random(possibleSpawns.length)]));
		player.setCanPvp(true);
		player.battleTarget = BattleTerrace.selectTarget(player);
		Potions.resetOverLoadEffect(player);
		player.reset();
		player.ordinanceTimer = 0;
	}

	public void takeOrdinance(Player player) {
		int prob = Utilities.random(100);
		if (player.ordinanceTimer > 0) {
			player.getPackets().sendGameMessage(
					"You can't take from the ordinance yet!");
			return;
		}
		if (prob < 34) {
			player.getInventory().addItem(
					foodOrdinance[Utilities.random(foodOrdinance.length)],
					Utilities.random(7 + 1));
		} else if (prob < 67 && prob > 33) {
			int rarity = Utilities.random(100);
			if (rarity <= 50)
				switch (player.selectedClass) {
				case "Warrior":
					player.getInventory()
							.addItem(
									commonWarriorGear[Utilities
											.random(commonWarriorGear.length)],
									1);
					break;
				case "Archer":
					player.getInventory()
							.addItem(
									commonArcherGear[Utilities
											.random(commonArcherGear.length)],
									1);
					break;
				case "Magician":
					player.getInventory()
							.addItem(
									commonMageGear[Utilities
											.random(commonMageGear.length)],
									1);
					break;
				}
			if (rarity > 50 && rarity <= 80)
				switch (player.selectedClass) {
				case "Warrior":
					player.getInventory()
							.addItem(
									mediumWarriorGear[Utilities
											.random(mediumWarriorGear.length)],
									1);
					break;
				case "Archer":
					player.getInventory()
							.addItem(
									mediumArcherGear[Utilities
											.random(mediumArcherGear.length)],
									1);
					break;
				case "Magician":
					player.getInventory()
							.addItem(
									mediumMageGear[Utilities
											.random(mediumMageGear.length)],
									1);
					break;
				}
			if (rarity > 80 && rarity <= 100)
				switch (player.selectedClass) {
				case "Warrior":
					player.getInventory()
							.addItem(
									rareWarriorGear[Utilities
											.random(rareWarriorGear.length)],
									1);
					break;
				case "Archer":
					player.getInventory()
							.addItem(
									rareArcherGear[Utilities
											.random(rareArcherGear.length)],
									1);
					break;
				case "Magician":
					player.getInventory()
							.addItem(
									rareMageGear[Utilities
											.random(rareMageGear.length)],
									1);
					break;
				}
		} else if (prob <= 100 && prob > 66) {
			switch (player.selectedClass) {
			case "Warrior":
				player.getInventory()
						.addItem(
								warriorPotions[Utilities
										.random(warriorPotions.length)],
								1);
				break;
			case "Archer":
				player.getInventory().addItem(
						archerPotions[Utilities.random(archerPotions.length)],
						1);
				break;
			case "Magician":
				player.getInventory().addItem(
						magePotions[Utilities.random(magePotions.length)], 1);
				break;
			}
		}
		player.ordinanceTimer = 45000;
		player.ordinanceTimer();

	}
}