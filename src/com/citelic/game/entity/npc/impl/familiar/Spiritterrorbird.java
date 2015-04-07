package com.citelic.game.entity.npc.impl.familiar;

import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.map.tile.Tile;

public class Spiritterrorbird extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5259052583696765531L;

	public Spiritterrorbird(Player owner, Pouches pouch, Tile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public int getBOBSize() {
		return 12;
	}

	@Override
	public int getSpecialAmount() {
		return 8;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public String getSpecialDescription() {
		return "Restores the player's run energy, by half the players agility level rounded up.";
	}

	@Override
	public String getSpecialName() {
		return "Tireless Run";
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		if (player.getRunEnergy() == 100) {
			player.getPackets().sendGameMessage(
					"This wouldn't effect you at all.");
			return false;
		}
		int newLevel = getOwner().getSkills().getLevel(Skills.AGILITY) + 2;
		double runEnergy = player.getRunEnergy() + (Math.round(newLevel / 2));
		if (newLevel > getOwner().getSkills().getLevelForXp(Skills.AGILITY) + 2)
			newLevel = getOwner().getSkills().getLevelForXp(Skills.AGILITY) + 2;
		setNextAnimation(new Animation(8229));
		player.setNextGraphics(new Graphics(1300));
		player.setNextAnimation(new Animation(7660));
		player.getSkills().set(Skills.AGILITY, newLevel);
		player.setRunEnergy(runEnergy > 100 ? 100 : runEnergy);
		return true;
	}
}
