package com.citelic.game.map.objects;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public class OwnedObjectManager {

	public static final AtomicLong keyMaker = new AtomicLong();

	private static final Map<String, OwnedObjectManager> ownedObjects = new ConcurrentHashMap<String, OwnedObjectManager>();

	private Player player;
	private GameObject[] objects;
	private int count;
	private long[] cycleTimes;
	private long lifeTime;
	private String managerKey;
	private ProcessEvent event;

	public static void processAll() {
		for (OwnedObjectManager object : ownedObjects.values())
			object.process();
	}

	public static boolean isPlayerObject(Player player, GameObject object) {
		for (Iterator<String> it = player.getOwnedObjectManagerKeys()
				.iterator(); it.hasNext();) {
			OwnedObjectManager manager = ownedObjects.get(it.next());
			if (manager == null) {
				it.remove();
				continue;
			}
			if (manager.getCurrentObject().getX() == object.getX()
					&& manager.getCurrentObject().getY() == object.getY()
					&& manager.getCurrentObject().getZ() == object.getZ()
					&& manager.getCurrentObject().getId() == object.getId())
				return true;
		}
		return false;
	}

	public static interface ConvertEvent {

		public boolean canConvert(Player player);

	}

	public static boolean convertIntoObject(GameObject object,
			GameObject toObject, ConvertEvent event) {
		for (OwnedObjectManager manager : ownedObjects.values()) {
			if (manager.getCurrentObject().getX() == toObject.getX()
					&& manager.getCurrentObject().getY() == toObject.getY()
					&& manager.getCurrentObject().getZ() == toObject.getZ()
					&& manager.getCurrentObject().getId() == object.getId()) {
				if (event != null && !event.canConvert(manager.player))
					return false;
				manager.convertIntoObject(toObject);
				return true;
			}
		}
		return false;
	}

	public static boolean removeObject(Player player, GameObject object) {
		for (Iterator<String> it = player.getOwnedObjectManagerKeys()
				.iterator(); it.hasNext();) {
			final OwnedObjectManager manager = ownedObjects.get(it.next());
			if (manager == null) {
				it.remove();
				continue;
			}
			if (manager.getCurrentObject().getX() == object.getX()
					&& manager.getCurrentObject().getY() == object.getY()
					&& manager.getCurrentObject().getZ() == object.getZ()
					&& manager.getCurrentObject().getId() == object.getId()) {
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						manager.delete();
					}
				});
				return true;
			}
		}
		return false;
	}

	public static void linkKeys(Player player) {
		for (Iterator<String> it = player.getOwnedObjectManagerKeys()
				.iterator(); it.hasNext();) {
			OwnedObjectManager manager = ownedObjects.get(it.next());
			if (manager == null) {
				it.remove();
				continue;
			}
			manager.player = player;
		}
	}

	public static void addOwnedObjectManager(Player player, GameObject object,
			long cycleTime) {
		addOwnedObjectManager(player, new GameObject[] { object },
				new long[] { cycleTime });
	}

	public static void addOwnedObjectManager(Player player,
			GameObject[] object, long[] cycleTimes) {
		addOwnedObjectManager(player, object, cycleTimes, null);
	}

	public static void addOwnedObjectManager(Player player,
			GameObject[] object, long[] cycleTimes, ProcessEvent event) {
		new OwnedObjectManager(player, object, cycleTimes, event);
	}

	private OwnedObjectManager(Player player, GameObject[] objects,
			long[] cycleTimes, ProcessEvent event) {
		managerKey = player.getUsername() + "_" + keyMaker.getAndIncrement();
		this.cycleTimes = cycleTimes;
		this.objects = objects;
		this.player = player;
		this.event = event;
		spawnObject();
		player.getOwnedObjectManagerKeys().add(managerKey);
		ownedObjects.put(managerKey, this);
	}

	public static int getObjectsforValue(Player player, int objectId) {
		int count = 0;
		for (Iterator<String> it = player.getOwnedObjectManagerKeys()
				.iterator(); it.hasNext();) {
			OwnedObjectManager manager = ownedObjects.get(it.next());
			if (manager == null) {
				it.remove();
				continue;
			}
			if (manager.getCurrentObject().getId() == objectId)
				count++;
		}
		return count;
	}

	public static boolean containsObjectValue(Player player, int... objectIds) {
		for (Iterator<String> it = player.getOwnedObjectManagerKeys()
				.iterator(); it.hasNext();) {
			OwnedObjectManager manager = ownedObjects.get(it.next());
			if (manager == null) {
				it.remove();
				continue;
			}
			for (int objectId : objectIds)
				if (manager.getCurrentObject().getId() == objectId)
					return true;
		}
		return false;
	}

	public void reset() {
		for (OwnedObjectManager object : ownedObjects.values())
			object.delete();
	}

	public void resetLifeTime() {
		this.lifeTime = Utilities.currentTimeMillis() + cycleTimes[count];
	}

	public boolean forceMoveNextStage() {
		if (count != -1)
			destroyObject(objects[count]);
		count++;
		if (count == objects.length) {
			remove();
			return false;
		}
		spawnObject();
		return true;
	}

	private void spawnObject() {
		Engine.spawnObject(objects[count], true);
		if (event != null)
			event.spawnObject(player, getCurrentObject());
		resetLifeTime();
	}

	public void convertIntoObject(GameObject object) {
		destroyObject(objects[count]);
		objects[count] = object;
		spawnObject();
	}

	private void remove() {
		ownedObjects.remove(managerKey);
		if (player != null)
			player.getOwnedObjectManagerKeys().remove(managerKey);
	}

	public void delete() {
		destroyObject(objects[count]);
		remove();
	}

	public void process() {
		if (Utilities.currentTimeMillis() > lifeTime)
			forceMoveNextStage();
		else if (event != null)
			event.process(player, getCurrentObject());
	}

	public GameObject getCurrentObject() {
		return objects[count];
	}

	public void destroyObject(GameObject object) {
		Engine.destroySpawnedObject(object);
	}

	public static interface ProcessEvent {

		public void spawnObject(Player player, GameObject object);

		public void process(Player player, GameObject currentObject);

	}

}
