package com.citelic.game.entity.player.content.controllers.impl.distractions.warriorguild;

import java.io.Serializable;
import java.util.HashMap;

import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;

public class AnimationGame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8004106287509832157L;

	/**
	 * The instance for the player
	 */
	private Player player;

	private static HashMap<Integer, WarriorsArmour> armours = new HashMap<Integer, WarriorsArmour>();

	static {
		for (WarriorsArmour set : WarriorsArmour.values()) {
			for (int i : set.getArmour())
				armours.put(i, set);
		}
	}

	public AnimationGame(Player player) {
		this.player = player;
	}

	public boolean containsItem(int id) {
		Item item = new Item(id);
		return containsItem(item);
	}

	public boolean containsItem(Item item) {
		return player.getInventory().getItems()
				.contains(new Item(item.getId(), 1))
				|| player.getEquipment().getItems()
						.contains(new Item(item.getId(), 1));
	}

	public void createWarrior(int armours, final GameObject object) {
		final WarriorsArmour sets = WarriorsArmour.forId(armours);
		if (sets == null || sets.getArmour() == null)
			return;
		if (sets.getArmour()[0] == armours || sets.getArmour()[1] == armours
				|| sets.getArmour()[2] == armours) {
			if (player.getInventory().containsItem(sets.getArmour()[0], 1)
					&& player.getInventory().containsItem(sets.getArmour()[1],
							1)
					&& player.getInventory().containsItem(sets.getArmour()[2],
							1)) {
				final boolean running = player.isRunning();
				player.getInventory().deleteItem(sets.getArmour()[0], 1);
				player.getInventory().deleteItem(sets.getArmour()[1], 1);
				player.getInventory().deleteItem(sets.getArmour()[2], 1);
				player.setRun(false);
				player.setRunHidden(false);
				player.addWalkSteps(player.getX(), player.getY() + 5, -1, false);
				player.setNextAnimation(new Animation(827));
				player.lock(4);
				player.getDialogueManager().startDialogue("WAnimator",
						new Object[0]);
				EngineTaskManager.schedule(new EngineTask() {

					boolean secondloop;

					@Override
					public void run() {
						if (!secondloop) {
							secondloop = true;
							player.setRunHidden(running);
						} else {
							stop();
						}
					}
				}, 0, 6);
				EngineTaskManager.schedule(new EngineTask() {

					@Override
					public void run() {
						final NPC war = new NPC(sets.getNpcId(), new Tile(
								object.getX(), object.getY(), 0), -1, true);
						war.addWalkSteps(player.getY(), player.getY());
						player.setNextFaceEntity(war);
						war.setTarget(player);
						war.setNextAnimation(new Animation(4166));
						war.setNextForceTalk(new ForceTalk("I'M ALIVE!"));
						player.getInterfaceManager().closeChatBoxInterface();
						player.getInterfaceManager()
								.closeReplacedRealChatBoxInterface();
						player.getHintIconsManager().addHintIcon(war, 1, -1,
								false);
						EngineTaskManager.schedule(new EngineTask() {

							@Override
							public void run() {
								war.setFinished(true);
								war.setForceWalk(new Tile(-1, -1, -1));
								Engine.removeNPC(war);
								stop();
							}

						}, 60, 60);
						stop();
						return;
					}

				}, 3, 3);
			} else {
				return;
			}
			return;
		}
	}

	/**
	 * Handles the operations when you enter the room.
	 * 
	 * @param player
	 */

	public void enterRoom(final Player player) {
		final Tile inFightingRoom = new Tile(2847, 3536, 2);
		final Tile outside = new Tile(2846, 3536, 2);
		if (player.getX() == 2846 || player.getX() == 2845) {
			player.setNextTile(inFightingRoom);
		} else {
			player.setNextTile(outside);
		}
	}

	public Item getDefender() {
		int id = 8844;
		if (containsItem(8850) || containsItem(20072)) {
			id = 20072;
		} else if (containsItem(8849) || containsItem(8850)) {
			id = 8850;
		} else if (containsItem(8848)) {
			id = 8849;
		} else if (containsItem(8847)) {
			id = 8848;
		} else if (containsItem(8846)) {
			id = 8847;
		} else if (containsItem(8845)) {
			id = 8846;
		} else if (containsItem(8844)) {
			id = 8845;
		} else {
			id = 8844;
		}
		return new Item(id);
	}

	public boolean hasDefender() {
		if (containsItem(8844) || containsItem(8845) || containsItem(8846)
				|| containsItem(8847) || containsItem(8848)
				|| containsItem(8849) || containsItem(8850)
				|| containsItem(20072)) {
			return true;
		}
		return false;
	}
}