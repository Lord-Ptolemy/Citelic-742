package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Thornysnail extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1147053487269627345L;

	public Thornysnail(Player owner, Pouches pouch, Tile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public int getBOBSize() {
		return 3;
	}

	@Override
	public int getSpecialAmount() {
		return 0;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public String getSpecialDescription() {
		return "Inflicts up to 80 damage against your opponent.";
	}

	@Override
	public String getSpecialName() {
		return "Slime Spray";
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}
}
