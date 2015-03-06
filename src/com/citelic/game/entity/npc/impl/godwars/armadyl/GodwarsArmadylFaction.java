package com.citelic.game.entity.npc.impl.godwars.armadyl;

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
public class GodwarsArmadylFaction extends NPC {

	public GodwarsArmadylFaction(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		if (!this.withinDistance(new Tile(2881, 5306, 0), 200))
			return super.getPossibleTargets();
		else {
			ArrayList<Entity> targets = this.getPossibleTargets(true, true);
			ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
			for (Entity t : targets) {
				if (t instanceof GodwarsArmadylFaction || t instanceof Player
						&& hasGodItem((Player) t)) {
					continue;
				}
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}

	private boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null) {
				continue; // shouldn't happen
			}
			String name = item.getDefinitions().getName().toLowerCase();
			// using else as only one item should count
			if (name.contains("armadyl Helmet")
					|| name.contains("armadyl mitre")
					|| name.contains("armadyl full helm")
					|| name.contains("armadyl coif")
					|| name.contains("torva full helm")
					|| name.contains("pernix cowl")
					|| name.contains("virtus mask"))
				return true;
			else if (name.contains("armadyl cloak"))
				return true;
			else if (name.contains("armadyl pendant")
					|| name.contains("armadyl stole"))
				return true;
			else if (name.contains("armadyl godsword")
					|| name.contains("armadyl crozier")
					|| name.contains("zaryte Bow"))
				return true;
			else if (name.contains("armadyl body")
					|| name.contains("armadyl robe top")
					|| name.contains("armadyl chestplate")
					|| name.contains("armadyl platebody")
					|| name.contains("torva platebody")
					|| name.contains("pernix body")
					|| name.contains("virtus robe top"))
				return true;
			else if (name.contains("illuminated book of law")
					|| name.contains("book of law")
					|| name.contains("armadyl kiteshield"))
				return true;
			else if (name.contains("armadyl robe legs")
					|| name.contains("armadyl plateskirt")
					|| name.contains("armadyl chaps")
					|| name.contains("armadyl platelegs")
					|| name.contains("armadyl Chainskirt")
					|| name.contains("torva platelegs")
					|| name.contains("pernix chaps")
					|| name.contains("virtus robe legs"))
				return true;
			else if (name.contains("armadyl vambraces"))
				return true;
		}
		return false;
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
					GodwarsArmadylFaction.this.setNextAnimation(new Animation(
							defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controller = player.getControllerManager()
								.getController();
						if (controller != null && controller instanceof GodWars) {
							GodWars godController = (GodWars) controller;
							godController.incrementKillCount(1);
						}
					}
					GodwarsArmadylFaction.this.drop();
					GodwarsArmadylFaction.this.reset();
					GodwarsArmadylFaction.this
							.setLocation(GodwarsArmadylFaction.this
									.getRespawnTile());
					GodwarsArmadylFaction.this.finish();
					if (!GodwarsArmadylFaction.this.isSpawned()) {
						GodwarsArmadylFaction.this.setRespawnTask();
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
