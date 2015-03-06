package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Packyak extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1397015887332756680L;

	public Packyak(Player owner, Pouches pouch, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, false);
	}

	@Override
	public int getBOBSize() {
		return 30;
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ITEM;
	}

	@Override
	public String getSpecialDescription() {
		return "Use special move on an item in your inventory to send it to your bank.";
	}

	@Override
	public String getSpecialName() {
		return "Winter Storage";
	}

	@Override
	public boolean isAgressive() {
		return false;
	}

	@Override
	public boolean submitSpecial(Object object) {
		int slotId = (Integer) object;
		if (!getOwner().getBank().hasBankSpace() || getOwner().isLocked()) {
			getOwner().getPackets().sendGameMessage(
					"You are too busy to send items to your bank.");
			return false;
		}
		getOwner().getBank().depositItem(slotId, 1, true);
		getOwner().getPackets().sendGameMessage(
				"Your Pack Yak has sent an item to your bank.");
		getOwner().setNextGraphics(new Graphics(1316));
		getOwner().setNextAnimation(new Animation(7660));
		return true;
	}
}
