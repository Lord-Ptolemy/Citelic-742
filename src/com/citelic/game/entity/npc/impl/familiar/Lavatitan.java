package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Lavatitan extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -778365732778023700L;

	public Lavatitan(Player owner, Pouches pouch, Tile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 4;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public String getSpecialDescription() {
		return "Possibly drain an enemy's special attack energy by 10%";
	}

	@Override
	public String getSpecialName() {
		return "Ebon Thunder";
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}

}
