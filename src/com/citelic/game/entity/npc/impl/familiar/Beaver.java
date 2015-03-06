package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.entity.player.content.actions.skills.woodcutting.Woodcutting;
import com.citelic.game.entity.player.content.actions.skills.woodcutting.Woodcutting.TreeDefinitions;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class Beaver extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9181393770444014076L;

	public Beaver(Player owner, Pouches pouch, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.OBJECT;
	}

	@Override
	public String getSpecialDescription() {
		return "Chops a tree, giving the owner its logs. There is also a chance that random logs may be produced.";
	}

	@Override
	public String getSpecialName() {
		return "Multichop";
	}

	@Override
	public boolean submitSpecial(Object context) {
		GameObject object = (GameObject) context;
		getOwner().getActionManager().setAction(
				new Woodcutting(object, TreeDefinitions.NORMAL));
		return true;
	}
}
