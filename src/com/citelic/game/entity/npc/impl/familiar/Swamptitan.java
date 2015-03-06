package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Swamptitan extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6073150798974730997L;

	public Swamptitan(Player owner, Pouches pouch, Tile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public String getSpecialDescription() {
		return "Inflicts a magical attack on near by opponents and attempts to poison them as well.";
	}

	@Override
	public String getSpecialName() {
		return "Swamp Plague";
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}

}
