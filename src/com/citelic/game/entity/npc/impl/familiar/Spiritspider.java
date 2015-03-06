package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class Spiritspider extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5995661005749498978L;

	public Spiritspider(Player owner, Pouches pouch, Tile tile,
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
		return SpecialAttack.CLICK;
	}

	@Override
	public String getSpecialDescription() {
		return "Spawns a random amount of red eggs around the familiar.";
	}

	@Override
	public String getSpecialName() {
		return "Egg Spawn";
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		setNextAnimation(new Animation(8267));
		player.setNextAnimation(new Animation(7660));
		player.setNextGraphics(new Graphics(1316));
		Tile tile = this;
		// attemps to randomize tile by 4x4 area
		for (int trycount = 0; trycount < Utilities.getRandom(10); trycount++) {
			tile = new Tile(this, 2);
			if (Engine.canMoveNPC(this.getZ(), tile.getX(), tile.getY(),
					player.getSize()))
				return true;
			for (Entity entity : this.getPossibleTargets()) {
				if (entity instanceof Player) {
					Player players = (Player) entity;
					players.getPackets().sendGraphics(new Graphics(1342), tile);
				}
				Engine.addGroundItem(new Item(223, 1), tile, player, false,
						120, true);
			}
		}
		return true;
	}
}
