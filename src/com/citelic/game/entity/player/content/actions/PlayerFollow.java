package com.citelic.game.entity.player.content.actions;

import com.citelic.game.entity.player.Player;
import com.citelic.game.map.pathfinding.RouteFinder;
import com.citelic.game.map.pathfinding.strategy.EntityStrategy;
import com.citelic.utility.Utilities;

public class PlayerFollow extends Action {

	private Player target;

	public PlayerFollow(Player target) {
		this.target = target;
	}

	@Override
	public boolean start(Player player) {
		player.setNextFaceEntity(target);
		if (checkAll(player))
			return true;
		player.setNextFaceEntity(null);
		return false;
	}

	private boolean checkAll(Player player) {
		if (player.isDead() || player.hasFinished() || target.isDead()
				|| target.hasFinished())
			return false;
		if (player.getZ() != target.getZ())
			return false;
		if (player.isFrozen())
			return true;
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = player.getSize();
		int maxDistance = 16;
		if (player.getZ() != target.getZ() || distanceX > size + maxDistance
				|| distanceX < -1 - maxDistance
				|| distanceY > size + maxDistance
				|| distanceY < -1 - maxDistance)
			return false;
		int lastFaceEntity = target.getLastFaceEntity();
		if (lastFaceEntity == player.getClientIndex()
				&& target.getActionManager().getAction() instanceof PlayerFollow)
			player.addWalkSteps(target.getX(), target.getY());
		else if (!player.clipedProjectile(target, true)
				|| !Utilities.isOnRange(player.getX(), player.getY(), size,
						target.getX(), target.getY(), target.getSize(), 0)) {
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER,
					player.getX(), player.getY(), player.getZ(),
					player.getSize(), new EntityStrategy(target), true);
			if (steps == -1)
				return false;

			if (steps > 0) {
				player.resetWalkSteps();

				int[] bufferX = RouteFinder.getLastPathBufferX();
				int[] bufferY = RouteFinder.getLastPathBufferY();
				for (int step = steps - 1; step >= 0; step--) {
					if (!player.addWalkSteps(bufferX[step], bufferY[step], 25,
							true))
						break;
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.setNextFaceEntity(null);
	}
}
