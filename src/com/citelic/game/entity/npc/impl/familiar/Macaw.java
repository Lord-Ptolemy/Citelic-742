package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.herblore.HerbCleaning.Herbs;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class Macaw extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7805271915467121215L;

	public Macaw(Player owner, Pouches pouch, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public String getSpecialDescription() {
		return "Creates a random herb.";
	}

	@Override
	public String getSpecialName() {
		return "Herbcall";
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		Herbs herb;
		player.setNextGraphics(new Graphics(1300));
		player.setNextAnimation(new Animation(7660));
		// TODO too lazy to find anims and gfx
		if (Utilities.getRandom(100) == 0)
			herb = Herbs.values()[Utilities.random(Herbs.values().length)];
		else
			herb = Herbs.values()[Utilities.getRandom(3)];
		Engine.addGroundItem(new Item(herb.getHerbId(), 1), player);
		return true;
	}
}
