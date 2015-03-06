package com.citelic.game.entity.player.content.controllers.impl;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

/**
 * A class containing the Crystal Chest.
 * 
 * @author Savions Sw
 */

public class CrystalChest {

	/**
	 * An Array holding all the keyparts.
	 */

	public static Item[] KEYPARTS = { new Item(985), new Item(987) };

	/**
	 * Item holding the Crystal Key.
	 */

	private static Item KEY = new Item(989);

	/**
	 * Animation, the Chest Animation.
	 */

	private static Animation CHEST_EMOTE = new Animation(536);

	/**
	 * Int[] the Sound ID.
	 */

	private static int[] soundId = { 52, 0, 1 };

	private static Item[] rewards;

	/**
	 * Item rewards.
	 */
	private static Item[] commonRewardsI = {
			new Item(385, Utilities.random(10)), new Item(1127, 1),
			new Item(892, 50), new Item(557, 50), new Item(559, 50),
			new Item(558, 50), new Item(560, 10), new Item(1337, 1),
			new Item(3202, 1), new Item(562, 10), new Item(563, 10),
			new Item(564, 10), new Item(1289, 1), new Item(995, 500000) };

	private static Item[] uncommonRewardsI = { new Item(4153, 1),
			new Item(4587, 1), new Item(1127, 1), new Item(5698, 1),
			new Item(6522, 10), new Item(537, Utilities.random(20)),
			new Item(995, 150000), new Item(1093, 1), new Item(6524, 1),
			new Item(4091, 1), new Item(4093, 1), new Item(4089, 1) };

	private static Item[] rareRewardsI = { new Item(6914, 1),
			new Item(6889, 1), new Item(6666, 1), new Item(2629, 1),
			new Item(18831, Utilities.random(30)), new Item(1187, 1),
			new Item(1149, 1), new Item(1514, Utilities.random(30)),
			new Item(2615, 1), new Item(2617, 1), new Item(2619, 1),
			new Item(3101, 1), new Item(563, 10), new Item(564, 10) };

	private static Item[] rareExtraRewardsI = { new Item(4712, 1),
			new Item(4714, 1), new Item(4708, 1), new Item(4716, 1),
			new Item(4718, 1), new Item(4720, 1), new Item(4722, 1),
			new Item(4724, 1), new Item(4726, 1), new Item(4728, 1),
			new Item(4730, 1), new Item(4732, 1), new Item(4736, 1),
			new Item(4638, 1) };

	public static void addCrystalReward(Player player) {
		int commonRewards = Utilities.random(50), uncommonRewards = Utilities
				.random(100), rareRewards = Utilities.random(200);
		if (uncommonRewards > 65 && rareRewards > 100) {
			rewards = new Item[] { rareRewardsI[Utilities
					.random(rareRewardsI.length)] };
		} else if (commonRewards > 15 && uncommonRewards > 50) {
			rewards = new Item[] { uncommonRewardsI[Utilities
					.random(uncommonRewardsI.length)] };
		} else if (commonRewards < 40 && uncommonRewards > 40) {
			rewards = new Item[] { commonRewardsI[Utilities
					.random(commonRewardsI.length)] };
		} else {
			rewards = new Item[] { commonRewardsI[Utilities
					.random(commonRewardsI.length)] };
		}
		if (Utilities.random(300) > 275) {
			rewards = new Item[] { rareExtraRewardsI[Utilities
					.random(rareExtraRewardsI.length)] };
		}
		for (Item item : rewards) {
			if (!player.getInventory().addItem(item)) {
				Engine.addGroundItem(item, new Tile(player), player, false,
						180, true);
			}
		}
	}

	/**
	 * Handles the Crystal Chest examine.
	 * 
	 * @param player
	 *            The player.
	 */

	public static void handleChestExamine(final GameObject object, Player player) {
		if (player.getInventory().containsItem(KEY.getId(), 1)) {
			Dialogue dialogue = new Dialogue() {

				@Override
				public void finish() {

				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case -1:
						stage = 0;
						sendOptionsDialogue("Unlock the chest?",
								"Yes, unlock the Crystal Chest",
								"No, I'd like to hold the Crystal key");
						break;
					case 0:
						switch (componentId) {
						case OPTION_1:
							openChest(object, player);
							end();
						case OPTION_2:
							end();
						}
					}
				}

				@Override
				public void start() {
					sendDialogue("You have a Crystal Key in your inventory, would "
							+ "you like to use it to unlock the chest?");
				}
			};
			player.getDialogueManager().startDialogue(dialogue);
		} else {
			player.getPackets().sendGameMessage(
					"The chest is securely locked shut.");
		}
	}

	/**
	 * Makes the Crystal Key
	 * 
	 * @param player
	 *            The Player.
	 */

	public static void makeKey(Player player) {
		player.getInventory().removeItems(KEYPARTS);
		player.getInventory().addItem(KEY);
		player.getPackets().sendGameMessage(
				"You bound the keyparts together and made a "
						+ KEY.getName().toLowerCase() + ".");
	}

	/**
	 * Opens the chest
	 * 
	 * @param object
	 *            The Chest.
	 * @param player
	 *            The Player.
	 */

	public static void openChest(GameObject object, final Player player) {
		if (player.getInventory().containsItem(989, 1) && !player.isLocked()) {
			player.faceObject(object);
			player.lock(2);
			player.getInventory().deleteItem(KEY);
			player.setNextAnimation(CHEST_EMOTE);
			player.getPackets().sendGameMessage(
					"You unlock the chest with your key.");
			player.getPackets().sendSound(soundId[0], soundId[1], soundId[2]);
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					addCrystalReward(player);
					player.getPackets().sendGameMessage(
							"You find some treasure in the chest!");
				}
			}, 1);
		} else if (!player.getInventory().containsItem(989, 1)) {
			player.getPackets().sendGameMessage(
					"You need a crystal key to open the chest.");
		}
	}

}