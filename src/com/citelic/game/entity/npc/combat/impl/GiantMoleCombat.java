package com.citelic.game.entity.npc.combat.impl;

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

public class GiantMoleCombat extends CombatScript {

	private static final Tile[] COORDS = { new Tile(1737, 5228, 0),
			new Tile(1751, 5233, 0), new Tile(1778, 5237, 0),
			new Tile(1736, 5227, 0), new Tile(1780, 5152, 0),
			new Tile(1758, 5162, 0), new Tile(1745, 5169, 0),
			new Tile(1760, 5183, 0) };

	@Override
	public int attack(final NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utilities.random(5) == 0) { // bury
			npc.setNextAnimation(new Animation(3314));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			final Player player = (Player) (target instanceof Player ? target
					: null);
			if (player != null)
				player.getInterfaceManager().sendTab(
						player.getInterfaceManager().hasRezizableScreen() ? 1
								: 11, 226);
			final Tile middle = npc.getMiddleWorldTile();
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					if (player != null)
						player.getPackets().closeInterface(
								player.getInterfaceManager()
										.hasRezizableScreen() ? 1 : 11);
					npc.setCantInteract(false);
					if (npc.isDead())
						return;
					Engine.sendGraphics(npc, new Graphics(572), middle);
					Engine.sendGraphics(
							npc,
							new Graphics(571),
							new Tile(middle.getX(), middle.getY() - 1, middle
									.getZ()));
					Engine.sendGraphics(
							npc,
							new Graphics(571),
							new Tile(middle.getX(), middle.getY() + 1, middle
									.getZ()));
					Engine.sendGraphics(npc, new Graphics(571),
							new Tile(middle.getX() - 1, middle.getY() - 1,
									middle.getZ()));
					Engine.sendGraphics(npc, new Graphics(571),
							new Tile(middle.getX() - 1, middle.getY() + 1,
									middle.getZ()));
					Engine.sendGraphics(npc, new Graphics(571),
							new Tile(middle.getX() + 1, middle.getY() - 1,
									middle.getZ()));
					Engine.sendGraphics(npc, new Graphics(571),
							new Tile(middle.getX() + 1, middle.getY() + 1,
									middle.getZ()));
					Engine.sendGraphics(
							npc,
							new Graphics(571),
							new Tile(middle.getX() - 1, middle.getY(), middle
									.getZ()));
					Engine.sendGraphics(
							npc,
							new Graphics(571),
							new Tile(middle.getX() + 1, middle.getY(), middle
									.getZ()));
					npc.setNextTile(new Tile(COORDS[Utilities
							.random(COORDS.length)]));
					npc.setNextAnimation(new Animation(3315));

				}

			}, 2);

		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(),
									NPCCombatDefinitions.MELEE, target)));
		}
		return defs.getAttackDelay();
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 3340 };
	}

}
