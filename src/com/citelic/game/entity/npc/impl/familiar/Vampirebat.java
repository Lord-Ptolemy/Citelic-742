package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Vampirebat extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 586089784797828590L;

	public Vampirebat(Player owner, Pouches pouch, Tile tile,
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
		return "Deals damage to your opponents, with a maximum hit of 120. It also has a chance of healing your lifepoints by 20. ";
	}

	@Override
	public String getSpecialName() {
		return "Vampyre Touch";
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}
}
