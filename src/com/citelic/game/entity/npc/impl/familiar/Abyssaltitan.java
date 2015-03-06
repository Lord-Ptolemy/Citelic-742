package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Abyssaltitan extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7635947578932404484L;

	public Abyssaltitan(Player owner, Pouches pouch, Tile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public int getBOBSize() {
		return 7;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public String getSpecialDescription() {
		return "Sends all your inventory and beast's essence to your bank.";
	}

	@Override
	public String getSpecialName() {
		return "Essence Shipment";
	}

	@Override
	public boolean submitSpecial(Object object) {
		if (getOwner().getBank().hasBankSpace()) {
			if (getBob().getBeastItems().getUsedSlots() == 0) {
				getOwner().getPackets().sendGameMessage(
						"You clearly have no essence.");
				return false;
			}
			getOwner().getBank().depositAllBob(false);
			getOwner().setNextGraphics(new Graphics(1316));
			getOwner().setNextAnimation(new Animation(7660));
			return true;
		}
		return false;
	}
}
