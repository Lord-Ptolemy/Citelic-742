package com.citelic.game.entity.player.content.actions.skills.crafting;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;

public class Spinning {

	public static final Animation SPINNING_ANIMATION = new Animation(896);

	public static boolean canSpin(Player player, int itemId) {
		for (SpinningItem item : SpinningItem.values()) {
			for (int beforeId : item.getBeforeId()) {
				if (beforeId == itemId) {
					Spinning.spin(player, item, itemId);
					return true;
				}
			}
		}
		return false;
	}

	public static int[] getBeforeFromAfter(int afterId) {
		for (SpinningItem item : SpinningItem.values()) {
			if (item.getAfterId() == afterId)
				return item.getBeforeId();
		}
		return null;
	}

	private static void spin(Player player, SpinningItem item, int itemId) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < item
				.getSkillRequirement()) {
			player.getPackets().sendGameMessage(
					"You need a Crafting level of "
							+ item.getSkillRequirement() + " to craft this.");
			return;
		}
		player.setNextAnimation(Spinning.SPINNING_ANIMATION);
		player.getInventory().deleteItem(new Item(itemId));
		player.getSkills().addXp(Skills.CRAFTING, item.getExp());
		player.getInventory().addItem(new Item(item.getAfterId()));
	}

	public enum SpinningItem {
		/**
		 * The wool.
		 */
		WOOL(new int[] { 1737 }, 1759, 1, 2.5), FLAX(new int[] { 1779 }, 1777,
				1, 15), SINEW(new int[] { 9436 }, 9438, 10, 15), MAGIC_ROOTS(
				new int[] { 6051 }, 6038, 19, 30), TREE_ROOTS(new int[] { 6049,
				6047, 6045, 6043 }, 6038, 19, 30), YAK_HAIR(
				new int[] { 10814 }, 954, 30, 25);

		private int[] beforeId;

		private int afterId;

		private int skillRequirement;

		private double exp;

		SpinningItem(int[] beforeId, int afterId, int skillRequirement,
				double exp) {
			this.beforeId = beforeId;
			this.afterId = afterId;
			this.skillRequirement = skillRequirement;
			this.exp = exp;
		}

		/**
		 * Gets the ID after.
		 *
		 * @return The ID after.
		 */
		public int getAfterId() {
			return afterId;
		}

		/**
		 * Gets the ID before.
		 *
		 * @return The ID before.
		 */
		public int[] getBeforeId() {
			return beforeId;
		}

		/**
		 * Gets the experience gained.
		 *
		 * @return The experience gained.
		 */
		public double getExp() {
			return exp;
		}

		/**
		 * Gets the Crafting level required.
		 *
		 * @return The Crafting level required.
		 */
		public int getSkillRequirement() {
			return skillRequirement;
		}
	}
}