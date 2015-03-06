package com.citelic.game.entity.player.content.actions.skills.magic;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;
import com.citelic.utility.economy.Prices;

public class Alching {

	public static void castAlchemy(Player player, int itemId, boolean lowAlch) {
		boolean fireStaff = Magic.isWearingFireStaff(player.getEquipment()
				.getWeaponId());
		double alchPrice = Prices.getAlchPrice(itemId, lowAlch);
		if (player.isLocked() || !player.getInventory().containsOneItem(itemId)) {
			return;
		}
		if (!Magic.checkRunes(player, true, 554, lowAlch ? 3 : 5, 561, 1))
			return;
		if (player.getSkills().getLevel(Skills.MAGIC) < (lowAlch ? 21 : 55)) {
			player.getPackets().sendGameMessage(
					"Your Magic level is not high enough for this spell.");
			return;
		}
		if (itemId == 995) {
			player.getPackets().sendGameMessage(
					"You cannot cast an alchemy spell on coins.");
			return;
		}
		player.setNextAnimation(new Animation(fireStaff ? (lowAlch ? 9625
				: 9633) : (lowAlch ? 712 : 713)));
		player.setNextGraphics(new Graphics(fireStaff ? (lowAlch ? 1692 : 1693)
				: (lowAlch ? 112 : 113)));
		// refresh and delete item
		player.getInventory().deleteItem(new Item(itemId));
		player.getInventory().refresh();
		player.getPackets().sendGlobalConfig(168, 7);
		// money part
		player.getInventory().addItem(new Item(995, (int) alchPrice));
		// xp part
		player.getSkills().addXp(Skills.MAGIC, (lowAlch ? 20 : 65));
		// lock actions
		player.lock(3);
	}

}
