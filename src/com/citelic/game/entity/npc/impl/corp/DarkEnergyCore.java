package com.citelic.game.entity.npc.impl.corp;

import java.util.ArrayList;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class DarkEnergyCore extends NPC {

	private CorporealBeast beast;
	private Entity target;

	private int changeTarget;

	private int delay;

	public DarkEnergyCore(CorporealBeast beast) {
		super(8127, beast, -1, true, true);
		setForceMultiArea(true);
		this.beast = beast;
		changeTarget = 2;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void processNPC() {
		if (isDead() || hasFinished())
			return;
		if (delay > 0) {
			delay--;
			return;
		}
		if (changeTarget > 0) {
			if (changeTarget == 1) {
				ArrayList<Entity> possibleTarget = beast.getPossibleTargets();
				if (possibleTarget.isEmpty()) {
					finish();
					beast.removeDarkEnergyCore();
					return;
				}
				target = possibleTarget.get(Utilities.getRandom(possibleTarget
						.size() - 1));
				setNextTile(new Tile(target));
				Engine.sendProjectile(this, this, target, 1828, 0, 0, 40, 40,
						20, 0);
			}
			changeTarget--;
			return;
		}
		if (target == null || target.getX() != getX()
				|| target.getY() != getY() || target.getZ() != getZ()) {
			changeTarget = 5;
			return;
		}
		int damage = Utilities.getRandom(50) + 50;
		target.applyHit(new Hit(this, Utilities.random(1, 131),
				HitLook.MAGIC_DAMAGE));
		beast.heal(damage);
		delay = getPoison().isPoisoned() ? 10 : 3;
		if (target instanceof Player) {
			Player player = (Player) target;
			player.getPackets()
					.sendGameMessage(
							"The dark core creature steals some life from you for its master.");
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		beast.removeDarkEnergyCore();
	}

}
