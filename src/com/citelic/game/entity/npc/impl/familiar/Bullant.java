package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Bullant extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4667052662212699631L;

	public Bullant(Player owner, Pouches pouch, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
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
		return SpecialAttack.CLICK;
	}

	@Override
	public String getSpecialDescription() {
		return "Restores the owner's run energy by half of their Agility level.";
	}

	@Override
	public String getSpecialName() {
		return "Unburden";
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		if (player.getRunEnergy() == 100) {
			player.getPackets().sendGameMessage(
					"This wouldn't effect you at all.");
			return false;
		}
		int agilityLevel = getOwner().getSkills().getLevel(Skills.AGILITY);
		double runEnergy = player.getRunEnergy() + (Math.round(agilityLevel / 2));
		player.setNextGraphics(new Graphics(1300));
		player.setNextAnimation(new Animation(7660));
		player.setRunEnergy(runEnergy > 100 ? 100 : runEnergy);
		return true;
	}
}
