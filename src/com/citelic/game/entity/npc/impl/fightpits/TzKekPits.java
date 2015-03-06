package com.citelic.game.entity.npc.impl.fightpits;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightPits;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class TzKekPits extends FightPitsNPC {

	public TzKekPits(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		if (hit.getLook() != HitLook.MELEE_DAMAGE || hit.getSource() == null)
			return;
		hit.getSource().applyHit(new Hit(this, 10, HitLook.REGULAR_DAMAGE));
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		final Tile tile = this;
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					setNextGraphics(new Graphics(2924 + getSize()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					FightPits.addNPC(new FightPitsNPC(2738, tile));
					if (Engine.canMoveNPC(getZ(), tile.getX() + 1, tile.getY(),
							1))
						tile.moveLocation(1, 0, 0);
					else if (Engine.canMoveNPC(getZ(), tile.getX() - 1,
							tile.getY(), 1))
						tile.moveLocation(-1, 0, 0);
					else if (Engine.canMoveNPC(getZ(), tile.getX(),
							tile.getY() - 1, 1))
						tile.moveLocation(0, -1, 0);
					else if (Engine.canMoveNPC(getZ(), tile.getX(),
							tile.getY() + 1, 1))
						tile.moveLocation(0, 1, 0);
					FightPits.addNPC(new FightPitsNPC(2738, tile));
					finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
