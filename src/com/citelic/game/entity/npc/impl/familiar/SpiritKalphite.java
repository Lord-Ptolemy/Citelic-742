package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class SpiritKalphite extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6110983138725755250L;

	public SpiritKalphite(Player owner, Pouches pouch, Tile tile,
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
		return "Attacks up to five opponents with a max damage of 50.";
	}

	@Override
	public String getSpecialName() {
		return "Sandstorm";
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}
}
