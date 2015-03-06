package com.citelic.game.entity.npc.impl.godwars.bandos;

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
public class GodwarsBandosFaction extends NPC {

	public GodwarsBandosFaction(int id, Tile tile, int mapAreaNameHash,
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
				if (t instanceof GodwarsBandosFaction || t instanceof Player
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
			if (name.contains("bandos mitre")
					|| name.contains("bandos Full helm")
					|| name.contains("bandos coif")
					|| name.contains("torva full helm")
					|| name.contains("pernix cowl")
					|| name.contains("vitus mask"))
				return true;
			else if (name.contains("bandos cloak"))
				return true;
			else if (name.contains("bandos stole"))
				return true;
			else if (name.contains("ancient mace")
					|| name.contains("granite mace")
					|| name.contains("bandos godsword")
					|| name.contains("bandos crozier")
					|| name.contains("zaryte bow"))
				return true;
			else if (name.contains("bandos body")
					|| name.contains("bandos robe top")
					|| name.contains("bandos chestplate")
					|| name.contains("bandos platebody")
					|| name.contains("torva platebody")
					|| name.contains("pernix body")
					|| name.contains("virtus robe top"))
				return true;
			else if (name.contains("illuminated book of war")
					|| name.contains("book of war")
					|| name.contains("bandos kiteshield"))
				return true;
			else if (name.contains("bandos robe legs")
					|| name.contains("bandos tassets")
					|| name.contains("bandos chaps")
					|| name.contains("bandos platelegs")
					|| name.contains("bandos plateskirt")
					|| name.contains("torva platelegs")
					|| name.contains("pernix chaps")
					|| name.contains("virtus robe legs"))
				return true;
			else if (name.contains("bandos vambraces"))
				return true;
			else if (name.contains("bandos boots"))
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
					GodwarsBandosFaction.this.setNextAnimation(new Animation(
							defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controller = player.getControllerManager()
								.getController();
						if (controller != null && controller instanceof GodWars) {
							GodWars godController = (GodWars) controller;
							godController.incrementKillCount(0);
						}
					}
					GodwarsBandosFaction.this.drop();
					GodwarsBandosFaction.this.reset();
					GodwarsBandosFaction.this
							.setLocation(GodwarsBandosFaction.this
									.getRespawnTile());
					GodwarsBandosFaction.this.finish();
					if (!GodwarsBandosFaction.this.isSpawned()) {
						GodwarsBandosFaction.this.setRespawnTask();
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}