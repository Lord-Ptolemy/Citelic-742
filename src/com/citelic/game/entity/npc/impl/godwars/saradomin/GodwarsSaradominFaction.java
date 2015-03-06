package com.citelic.game.entity.npc.impl.godwars.saradomin;

import java.util.ArrayList;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.GodWars;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;

@SuppressWarnings("serial")
public class GodwarsSaradominFaction extends NPC {

	public GodwarsSaradominFaction(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null) {
				continue; // shouldn't happen
			}
			String name = item.getDefinitions().getName().toLowerCase();
			// using else as only one item should count
			if (name.contains("saradomin coif")
					|| name.contains("citharede hood")
					|| name.contains("saradomin mitre")
					|| name.contains("saradomin full helm")
					|| name.contains("saradomin halo")
					|| name.contains("torva full helm")
					|| name.contains("pernix cowl")
					|| name.contains("virtus mask"))
				return true;
			else if (name.contains("saradomin cape")
					|| name.contains("saradomin cloak"))
				return true;
			else if (name.contains("holy symbol")
					|| name.contains("citharede symbol")
					|| name.contains("saradomin stole"))
				return true;
			else if (name.contains("saradomin arrow"))
				return true;
			else if (name.contains("saradomin godsword")
					|| name.contains("saradomin sword")
					|| name.contains("saradomin staff")
					|| name.contains("saradomin crozier")
					|| name.contains("zaryte Bow"))
				return true;
			else if (name.contains("saradomin robe top")
					|| name.contains("saradomin d'hide")
					|| name.contains("citharede robe top")
					|| name.contains("monk's robe top")
					|| name.contains("saradomin platebody")
					|| name.contains("torva platebody")
					|| name.contains("pernix body")
					|| name.contains("virtus robe top"))
				return true;
			else if (name.contains("illuminated holy book")
					|| name.contains("holy book")
					|| name.contains("saradomin kiteshield"))
				return true;
		}
		return false;
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		if (!this.withinDistance(new Tile(2881, 5306, 0), 200))
			return super.getPossibleTargets();
		else {
			ArrayList<Entity> targets = this.getPossibleTargets(true, true);
			ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
			for (Entity t : targets) {
				if (t instanceof GodwarsSaradominFaction || t instanceof Player
						&& GodwarsSaradominFaction.hasGodItem((Player) t)) {
					continue;
				}
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					GodwarsSaradominFaction.this
							.setNextAnimation(new Animation(defs
									.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controller = player.getControllerManager()
								.getController();
						if (controller != null && controller instanceof GodWars) {
							GodWars godController = (GodWars) controller;
							godController.incrementKillCount(2);
						}
					}
					GodwarsSaradominFaction.this.drop();
					GodwarsSaradominFaction.this.reset();
					GodwarsSaradominFaction.this
							.setLocation(GodwarsSaradominFaction.this
									.getRespawnTile());
					GodwarsSaradominFaction.this.finish();
					if (!GodwarsSaradominFaction.this.isSpawned()) {
						GodwarsSaradominFaction.this.setRespawnTask();
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}