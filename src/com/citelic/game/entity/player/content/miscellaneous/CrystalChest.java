package com.citelic.game.entity.player.content.miscellaneous;

import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

/**
 * Represents the chest on which the key is used.
 * 
 * @author 'Corey 2010 <MobbyGFX96@hotmail.co.uk>
 */

public class CrystalChest {

	private static final int[] CHEST_REWARDS = { 5290, 5288, 5289, 5302, 219,
			985, 987, 1127, 2619, 2617, 2615, 2621, 2661, 2663, 2665, 2667,
			10294, 19251, 10440, 10442, 10444 };
	public static final int[] KEY_HALVES = { 985, 987 };
	public static final int KEY = 989;
	public static final int Animation = 881;

	/**
	 * Represents the key being made. Using tooth halves.
	 */
	public static void makeKey(Player p) {
		if (p.getInventory().containsItem(toothHalf(), 1)
				&& p.getInventory().containsItem(loopHalf(), 1)) {
			p.getInventory().deleteItem(toothHalf(), 1);
			p.getInventory().deleteItem(loopHalf(), 1);
			p.getInventory().addItem(KEY, 1);
			p.print("You succesfully make a crytal key.");
		}
	}

	/**
	 * If the player can open the chest.
	 */
	public static boolean canOpen(Player p) {
		if (p.getInventory().containsItem(KEY, 1)) {
			return true;
		} else {
			p.print("This chest is locked.");
			return false;
		}
	}

	/**
	 * When the player searches the chest.
	 */
	public static void searchChest(final Player p) {
		if (canOpen(p)) {
			p.print("You unlock the chest with your key.");
			p.getInventory().deleteItem(KEY, 1);
			p.getInventory().addItem(1631, 1);
			p.getInventory().addItem(
					CHEST_REWARDS[Utilities.random(getLength() - 1)], 1);
			int random = Utilities.random(2500);
			if (random == 1) {
				p.getInventory().addItem(Utilities.random(23679, 23700), 1);
			}
			p.print("You find some treasure in the chest.");
		}
	}

	public static int getLength() {
		return CHEST_REWARDS.length;
	}

	/**
	 * Represents the toothHalf of the key.
	 */
	public static int toothHalf() {
		return KEY_HALVES[0];
	}

	/**
	 * Represent the loop half of the key.
	 */
	public static int loopHalf() {
		return KEY_HALVES[1];
	}

}