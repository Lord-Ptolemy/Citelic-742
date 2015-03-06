package com.citelic.game.entity.npc.combat.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.CombatScript;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class KalphiteQueenCombat extends CombatScript {

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utilities.random(3);
		if (attackStyle == 0) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if (distanceX > size || distanceX < -1 || distanceY > size
					|| distanceY < -1)
				attackStyle = Utilities.random(2); // set mage
			else {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getRandomMaxHit(npc, defs.getMaxHit(),
										NPCCombatDefinitions.MELEE, target)));
				return defs.getAttackDelay();
			}
		}
		npc.setNextAnimation(new Animation(npc.getId() == 1158 ? 6240 : 6234));
		if (attackStyle == 1) { // range easy one
			for (final Entity t : npc.getPossibleTargets()) {
				delayHit(
						npc,
						2,
						t,
						getRangeHit(
								npc,
								getRandomMaxHit(npc, defs.getMaxHit(),
										NPCCombatDefinitions.RANGE, t)));
				Engine.sendProjectile(npc, t, 288, 46, 31, 50, 30, 16, 0);
			}
		} else {
			npc.setNextGraphics(new Graphics(npc.getId() == 1158 ? 278 : 279));
			EngineTaskManager.schedule(new EngineTask() {

				@Override
				public void run() {
					attackMageTarget(new ArrayList<Player>(), npc, npc, target);
				}

			});
		}
		return defs.getAttackDelay();
	}

	private void attackMageTarget(final List<Player> arrayList,
			Entity fromEntity, final NPC startTile, Entity t) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity,
				startTile) : t;
		if (target == null)
			return;
		if (target instanceof Player)
			arrayList.add((Player) target);
		Engine.sendProjectile(fromEntity, target, 280,
				fromEntity == startTile ? 70 : 20, 20, 60, 30, 0, 0);
		delayHit(
				startTile,
				0,
				target,
				getMagicHit(
						startTile,
						getRandomMaxHit(startTile, startTile.getMaxHit(),
								NPCCombatDefinitions.MAGE, target)));
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				target.setNextGraphics(new Graphics(281));
				attackMageTarget(arrayList, target, startTile, null);
			}
		});
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Kalphite Queen" };
	}

	private Player getTarget(List<Player> list, final Entity fromEntity,
			Tile startTile) {
		if (fromEntity == null) {
			return null;
		}
		ArrayList<Player> added = new ArrayList<Player>();
		for (int regionId : fromEntity.getMapRegionsIds()) {
			List<Integer> playersIndexes = Engine.getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = Engine.getPlayers().get(playerIndex);
				if (player == null || list.contains(player)
						|| !player.withinDistance(fromEntity)
						|| !player.withinDistance(startTile))
					continue;
				added.add(player);
			}
		}
		if (added.isEmpty())
			return null;
		Collections.sort(added, new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				if (o1 == null)
					return 1;
				if (o2 == null)
					return -1;
				if (Utilities.getDistance(o1, fromEntity) > Utilities
						.getDistance(o2, fromEntity))
					return 1;
				else if (Utilities.getDistance(o1, fromEntity) < Utilities
						.getDistance(o2, fromEntity))
					return -1;
				else
					return 0;
			}
		});
		return added.get(0);

	}
}
