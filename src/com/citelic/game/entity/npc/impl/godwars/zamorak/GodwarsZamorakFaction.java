package com.citelic.game.entity.npc.impl.godwars.zamorak;

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
public class GodwarsZamorakFaction extends NPC {

	public GodwarsZamorakFaction(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null || item.getId() == -1) {
				continue; // shouldn't happen
			}
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("zamorak coif") || name.contains("zamorak mitre")
					|| name.contains("zamorak full helm")
					|| name.contains("zamorak halo")
					|| name.contains("torva full helm")
					|| name.contains("pernix cowl")
					|| name.contains("virtus mask"))
				return true;
			else if (name.contains("zamorak cape")
					|| name.contains("zamorak cloak"))
				return true;
			else if (name.contains("unholy symbol")
					|| name.contains("zamorak stole"))
				return true;
			else if (name.contains("illuminated unholy book")
					|| name.contains("unholy book")
					|| name.contains("zamorak kiteshield"))
				return true;
			else if (name.contains("zamorak arrows"))
				return true;
			else if (name.contains("zamorak godsword")
					|| name.contains("zamorakian spear")
					|| name.contains("zamorak staff")
					|| name.contains("zamorak crozier")
					|| name.contains("zaryte Bow"))
				return true;
			else if (name.contains("zamorak d'hide")
					|| name.contains("zamorak platebody")
					|| name.contains("torva platebody")
					|| name.contains("pernix body")
					|| name.contains("virtus robe top"))
				return true;
			else if (name.contains("zamorak robe")
					|| name.contains("zamorak robe bottom ")
					|| name.contains("zamorak chaps")
					|| name.contains("zamorak platelegs")
					|| name.contains("zamorak plateskirt")
					|| name.contains("torva platelegs")
					|| name.contains("pernix chaps")
					|| name.contains("virtus robe legs"))
				return true;
			else if (name.contains("zamorak vambraces"))
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
				if (t instanceof GodwarsZamorakFaction || t instanceof Player
						&& GodwarsZamorakFaction.hasGodItem((Player) t)) {
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
					GodwarsZamorakFaction.this.setNextAnimation(new Animation(
							defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controller = player.getControllerManager()
								.getController();
						if (controller != null && controller instanceof GodWars) {
							GodWars godController = (GodWars) controller;
							godController.incrementKillCount(3);
						}
					}
					GodwarsZamorakFaction.this.drop();
					GodwarsZamorakFaction.this.reset();
					GodwarsZamorakFaction.this
							.setLocation(GodwarsZamorakFaction.this
									.getRespawnTile());
					GodwarsZamorakFaction.this.finish();
					if (!GodwarsZamorakFaction.this.isSpawned()) {
						GodwarsZamorakFaction.this.setRespawnTask();
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
