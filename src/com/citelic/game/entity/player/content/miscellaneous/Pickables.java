package com.citelic.game.entity.player.content.miscellaneous;

import java.util.TimerTask;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.utility.Utilities;

/**
 * Picking up things that are stackable and pickable.
 *
 * @author Arham Siddiqui
 */
public class Pickables {

	/**
	 * Picks up the Pickable.
	 *
	 * @param player
	 *            The Player picking up the Pickable.
	 * @param pickable
	 *            The Pickable item to pick.
	 */
	private static void pick(final Player player, final GameObject worldObject,
			final Pickable pickable) {
		if (player.getInventory().getFreeSlots() > 0) {
			if (pickable == Pickable.FLAX) {
				player.getPackets().sendGameMessage("You pick some flax.");
			} else if (pickable == Pickable.CABBAGE) {
				player.getPackets().sendGameMessage("You pick a cabbage.");
			} else if (pickable == Pickable.WHEAT) {
				player.getPackets().sendGameMessage("You pick some grain.");
			} else if (pickable == Pickable.POTATO) {
				player.getPackets().sendGameMessage("You pick a potato.");
			} else if (pickable == Pickable.BANANA) {
				player.getPackets().sendGameMessage("You pick a banana.");
			} else if (pickable == Pickable.ONION) {
				player.getPackets().sendGameMessage("You pick an onion.");
			}
			player.setNextAnimation(new Animation(827));
			player.lock(1);
			if (pickable != Pickable.FLAX) {
				CoresManager.fastExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						player.getInventory().addItem(
								pickable.getHarvestedItem());
						Engine.removeObject(worldObject, false);
						player.addWalkSteps(worldObject.getX(),
								worldObject.getY());
					}
				}, 750);
				CoresManager.fastExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						Engine.spawnObject(worldObject, false);
					}
				}, 13000);
			} else {
				CoresManager.fastExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						player.getInventory().addItem(
								pickable.getHarvestedItem());
						worldObject.decrementObjectLife();
						if (worldObject.getLife() < 1) {
							Engine.removeObject(worldObject, false);
							player.addWalkSteps(worldObject.getX(),
									worldObject.getY());
						}
					}
				}, 750);
				if (worldObject.getLife() <= 1) {
					CoresManager.fastExecutor.schedule(new TimerTask() {
						@Override
						public void run() {
							Engine.spawnObject(worldObject, false);
							worldObject.setLife(Utilities.random(7));
						}
					}, 13000);
				}
			}
		}
	}

	/**
	 * Handles if the object is a pickable.
	 *
	 * @param player
	 *            The player picking the object.
	 * @param worldObject
	 *            The literal pickable.
	 * @return If the object is a pickable.
	 */
	public static boolean handlePickable(Player player, GameObject worldObject) {
		for (Pickable pickable : Pickable.values()) {
			for (int i = 0; i < pickable.getObjectIds().length; i++) {
				if (pickable.getObjectIds()[i] == worldObject.getId()) {
					pick(player, worldObject, pickable);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * The single pickable.
	 */
	public enum Pickable {
		ONION(new int[] { 3366, 5538, 8584 }, new Item(1957)), CABBAGE(
				new int[] { 1161, 8535, 8536, 8537, 8538, 8539, 8540, 8541,
						8542, 8543, 11494, 22301 }, new Item(1965)), WHEAT(
				new int[] { 313, 5583, 5584, 5585, 15506, 15507, 15508, 22300 },
				new Item(1947)), POTATO(new int[] { 312, 8562, 9408 },
				new Item(1942)), FLAX(new int[] { 2646, 15075, 15076, 15077,
				15078, 67264, 67263 }, new Item(1779)), BANANA(new int[] {
				2073, 2074, 2075, 2076, 2077, 2078, 4749 }, new Item(1963));
		private int[] objectIds;
		private Item harvestedItem;

		Pickable(int[] objectIds, Item harvestedItem) {
			this.objectIds = objectIds;
			this.harvestedItem = harvestedItem;
		}

		public int[] getObjectIds() {
			return objectIds;
		}

		public Item getHarvestedItem() {
			return harvestedItem;
		}
	}
}