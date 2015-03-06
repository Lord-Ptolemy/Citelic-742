package com.citelic.game.entity.npc.impl.soulwars;

import java.util.ArrayList;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.GameTask;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.PlayerType;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.Teams;
import com.citelic.game.map.tile.Tile;

public class Avatar extends NPC {

	private static final long serialVersionUID = -3814763084708900338L;

	public Avatar(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
		setLureDelay(0);
		setForceMultiAttacked(true);
		setCapDamage(500);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getSource() instanceof NPC
				|| SoulWarsManager.MINUTES_BEFORE_NEXT_GAME.get() < 4)
			return;
		final Teams team = Teams.values()[getId()
				- SoulWarsManager.AVATAR_INDEX];
		if (((GameTask) Engine.soulWars.getTasks().get(PlayerType.IN_GAME))
				.getAvatarSlayerLevel(team) > ((Player) hit.getSource())
				.getSkills().getLevelForXp(Skills.SLAYER)) {
			((Player) hit.getSource())
					.getPackets()
					.sendGameMessage(
							"Your slayer level is not high enough to damage this creature.");
			hit.setDamage(0);
		}
		super.handleIngoingHit(hit);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.5;
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = super.getPossibleTargets();
		final Teams team = Teams.values()[getId()
				- SoulWarsManager.AVATAR_INDEX];
		for (Player player : (((GameTask) Engine.soulWars.getTasks().get(
				PlayerType.IN_GAME)).getPlayers(team))) {
			if (player != null && targets.contains(player))
				targets.remove(player);
		}
		return targets;
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (SoulWarsManager.MINUTES_BEFORE_NEXT_GAME.get() > 3) {
						((GameTask) Engine.soulWars.getTasks().get(
								PlayerType.IN_GAME)).increaseKill(getId());
						reset();
						setLocation(getRespawnTile());
						finish();
						setRespawnTask();
						super.stop();
					}
				}
				loop++;
			}
		}, 0, 1);
	}
}