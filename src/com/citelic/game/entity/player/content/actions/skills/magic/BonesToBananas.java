package com.citelic.game.entity.player.content.actions.skills.magic;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;

public class BonesToBananas {

	public static void castBonesToBananas(Player player) {
		if (!player.getInventory().containsItem(526)
				&& !player.getInventory().containsItem(532)) {
			player.getPackets().sendGameMessage(
					"You dont have any bones to convert to bananas.");
			return;
		}
		int bonesAmount = player.getInventory().getNumberOf(526)
				+ player.getInventory().getNumberOf(532);
		if (player.isLocked()) {
			player.getPackets().sendGameMessage(
					"You can't cast this spell while performing an action.");
			return;
		}
		if (!Magic.checkRunes(player, true, 557, 2, 555, 2, 561, 1))
			return;
		if (player.getSkills().getLevel(Skills.MAGIC) < 15) {
			player.getPackets().sendGameMessage(
					"Your Magic level is not high enough for this spell.");
			return;
		}
		player.setNextAnimation(new Animation(722));
		player.setNextGraphics(new Graphics(141));
		player.getInventory().deleteItem(new Item(526, 28));
		player.getInventory().deleteItem(new Item(532, 28));
		player.getInventory().refresh();
		player.getPackets().sendGlobalConfig(168, 7);
		player.getInventory().addItem(new Item(1963, bonesAmount));
		player.getSkills().addXp(Skills.MAGIC, (19));
		player.lock(3);
	}

}
