package com.citelic.game.entity.npc.impl.nomad;

import java.util.List;

import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class FlameVortex extends NPC {

	private long explodeTime;

	public FlameVortex(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		explodeTime = Utilities.currentTimeMillis() + 60000;
		setNextAnimation(new Animation(12720));
	}

	public FlameVortex(Tile tile) {
		this(9441, tile, -1, true, true);
	}

	public void explode(final Player target, final int damage) {
		explodeTime = -1;
		final NPC npc = this;
		EngineTaskManager.schedule(new EngineTask() {

			private boolean secondLoop;

			@Override
			public void run() {
				if (!secondLoop) {
					setNextAnimation(new Animation(12722));
					if (target != null) {
						target.applyHit(new Hit(npc, damage,
								HitLook.REGULAR_DAMAGE));
						target.setRunEnergy(0);
						target.setNextForceTalk(new ForceTalk("Aiiiiiieeeee!"));
					}
					secondLoop = true;
				} else {
					finish();
					stop();
				}
			}
		}, 0, 0);
	}

	public Player getTargetToCheck() {
		List<Integer> playerIndexes = Engine.getRegion(getRegionId())
				.getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = Engine.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || !player.isRunning())
					continue;
				return player;
			}
		}
		return null;
	}

	@Override
	public void processNPC() {
		if (explodeTime == -1)
			return;
		Player target = getTargetToCheck();
		if (target != null
				&& ((target.getX() == getX() && target.getY() == getY()) || (target
						.getNextRunDirection() != -1
						&& target.getX()
								- Utilities.DIRECTION_DELTA_X[target
										.getNextRunDirection()] == getX() && target
						.getY()
						- Utilities.DIRECTION_DELTA_Y[target
								.getNextRunDirection()] == getY()))) {
			explode(target, 400);
		} else if (explodeTime < Utilities.currentTimeMillis())
			explode(target != null && withinDistance(target, 1) ? target : null,
					Utilities.random(400, 701));
	}

}
