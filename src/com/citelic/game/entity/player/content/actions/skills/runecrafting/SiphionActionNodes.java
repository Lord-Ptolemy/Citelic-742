package com.citelic.game.entity.player.content.actions.skills.runecrafting;

import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.impl.distractions.RunespanController;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class SiphionActionNodes extends Action {

	private Nodes nodes;
	private GameObject node;
	private boolean started;

	private static final Tile[][] NODE_TILES = {
			{ new Tile(4337, 6076, 1), new Tile(4327, 6067, 1),
					new Tile(4338, 6062, 1), new Tile(4359, 6117, 1),
					new Tile(4379, 6058, 1), new Tile(4385, 6089, 1),
					new Tile(4322, 6091, 1), new Tile(4410, 6073, 1) },

			{ new Tile(3938, 6126, 1), new Tile(3981, 6139, 1),
					new Tile(3958, 6140, 1), new Tile(3931, 6139, 1),
					new Tile(3919, 6133, 1), new Tile(3912, 6082, 1),
					new Tile(3953, 6066, 1), new Tile(3982, 6064, 1),
					new Tile(4018, 6071, 1), new Tile(4136, 6136, 1),
					new Tile(4150, 6134, 1), new Tile(4188, 6122, 1),
					new Tile(4213, 6047, 1), new Tile(4190, 6027, 1),
					new Tile(4161, 6027, 1), new Tile(4165, 6027, 1),
					new Tile(4149, 6017, 1), new Tile(4133, 6022, 1),
					new Tile(4187, 6122, 1), new Tile(4172, 6137, 1),
					new Tile(4139, 6035, 1), new Tile(4141, 6082, 1) } };

	public SiphionActionNodes(Nodes nodes, GameObject node) {
		this.nodes = nodes;
		this.node = node;
	}

	public enum Nodes {

		CYCLONE(70455, 16596, 19, 1, 24215),

		MIND_STORM(70456, 16596, 20, 1, 24217),

		WATER_POOL(70457, 16596, 25.3, 5, 24214),

		ROCK_FRAGMENT(70458, 16596, 28.6, 9, 24216),

		FIRE_BALL(70459, 16596, 34.8, 14, 24213),

		VINE(70460, 16596, 34.8, 17, 24214, 24216),

		FLESHLY_GROWTH(70461, 16596, Utilities.random(30.3, 34.3), 20, 24218),

		FIRE_STORM(70462, 16596, Utilities.random(22.8, 41.7), 27, 24213, 24215),

		CHAOTIC_CLOUD(70463, 16596, 61.6, 35, 24221),

		NEBULA(70464, 16596, Utilities.random(63.8, 85.6), 40, 24223, 24224),

		SHIFTER(70465, 16596, 86.8, 44, 24220),

		JUMPER(70466, 16596, 107.8, 54, 24222),

		SKULLS(70467, 16596, 120, 65, 24219),

		BLOOD_POOL(70468, 16596, 146.3, 77, 24225),

		BLOODY_SKULLS(70469, 16596, Utilities.random(144, 175.5), 83, 24219,
				24225),

		LIVING_SOUL(70470, 16596, 213, 90, 24226),

		UNDEAD_SOUL(70471, 16596, Utilities.random(144, 255.5), 95, 24219,
				24226);

		private int objectId;
		private int emoteId;
		private double xp;
		private int levelRequired;
		private int[] runeId;

		Nodes(int objectId, int emoteId, double xp, int levelRequired,
				int... runeId) {
			this.objectId = objectId;
			this.emoteId = emoteId;
			this.xp = xp;
			this.levelRequired = levelRequired;
			this.runeId = runeId;
		}

		public int getObjectId() {
			return objectId;
		}

		public void setObjectId(int objectId) {
			this.objectId = objectId;
		}

		public int getEmoteId() {
			return emoteId;
		}

		public void setEmoteId(int emoteId) {
			this.emoteId = emoteId;
		}

		public int[] getRuneId() {
			return runeId;
		}

		public double getXp() {
			return xp;
		}

		public void setXp(int xp) {
			this.xp = xp;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public void setLevelRequired(int levelRequired) {
			this.levelRequired = levelRequired;
		}
	}

	public static boolean siphion(Player player, GameObject object) {
		Nodes node = getNode(object.getId());
		if (node == null)
			return false;
		player.getActionManager().setAction(
				new SiphionActionNodes(node, object));
		return true;
	}

	public static void init() {
		for (int j = 0; j < 5; j++) {
			if (j >= 4)
				if (Utilities.random(1) == 0)
					continue;
			for (int i = 0; i < (NODE_TILES[0].length + NODE_TILES[1].length); i++) {
				Nodes node = getNode(getRandomNodeId());
				Tile tile = getNodeWorldTile(node);
				Engine.spawnObject(new GameObject(getRandomNodeId(), 10, 0,
						tile.getX(), tile.getY(), tile.getZ(),
						getFixedLife(node)), true);
			}
		}
	}

	public static Tile getNodeWorldTile(Nodes node) {
		Tile tile = NODE_TILES[Utilities.random(NODE_TILES.length)][node
				.getLevelRequired() >= 83 ? 0 : 1];
		Tile teleTile = tile;
		for (int trycount = 0; trycount < 10; trycount++) {
			teleTile = new Tile(tile, 2);
			if (Engine.isTileFree(tile.getZ(), teleTile.getX(),
					teleTile.getY(), 1))
				break;
			teleTile = tile;
		}
		return teleTile;
	}

	private static int getRandomNodeId() {
		int index = Utilities.random(Nodes.values().length);
		return Nodes.values()[index].getObjectId();
	}

	private static Nodes getNode(int id) {
		for (Nodes node : Nodes.values())
			if (node.objectId == id)
				return node;
		return null;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player))
			return true;
		return false;
	}

	public boolean checkAll(final Player player) {
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < nodes
				.getLevelRequired()) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a runecrafting level of "
							+ nodes.getLevelRequired()
							+ " to siphon from that node.");
			return false;
		}
		if (!started
				&& (!player.withinDistance(node, 6) || !player
						.clipedProjectile(node, true))) {
			player.calcFollow(node, true);
			return true;
		}
		if (!player.getInventory().containsItem(24227, 1)) {
			if (!player.getInventory().hasFreeSlots())
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
			else
				player.getPackets()
						.sendGameMessage(
								"You don't have any rune essence to siphon from that node.",
								true);
			return false;
		}
		if (!started) {
			player.resetWalkSteps();
			player.setNextAnimation(new Animation(16596));
			started = true;
		}
		return true;
	}

	private static int getFixedLife(Nodes node) {
		return Utilities.getRandom(node.getLevelRequired()) + 20;
	}

	private void processNodeDestroy(final Player player) {
		player.getPackets()
				.sendGameMessage(
						"The node you were siphoning from has been depleted of energy.",
						true);
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						"You pick up the essence left by the node.", true);
				player.setNextAnimation(new Animation(16599));
				player.getInventory().addItem(24227, 50);
				Engine.removeObject(node, true);
				Tile tile = getNodeWorldTile(nodes);
				Engine.spawnObject(
						new GameObject(getRandomNodeId(), node.getType(), node
								.getRotation(), tile.getX(), tile.getY(), tile
								.getZ(), getFixedLife(nodes)), true);
				stop();
			}
		}, 2);
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(final Player player) {
		if (started) {
			if (node.getLife() == 0)
				processNodeDestroy(player);
			else {
				node.setLife(node.getLife() - 1);
				int level = player.getSkills().getLevel(Skills.RUNECRAFTING);
				if (level < 50 ? Utilities.getRandom(2) == 1 : Utilities
						.getRandom(1) == 1) {
					player.getInventory().addItem(
							nodes.getRuneId()[Utilities.random(nodes
									.getRuneId().length)], 1);
					player.getInventory().deleteItem(24227, 1);
					if (player.getControllerManager().getController() != null
							&& player.getControllerManager().getController() instanceof RunespanController) {
						for (int runeId : nodes.getRuneId())
							((RunespanController) player.getControllerManager()
									.getController()).addRunespanPoints(runeId);
					}
					double totalXp = nodes.getXp();
					if (RuneCrafting.hasRcingSuit(player))
						totalXp *= 1.025;
					player.getSkills().addXp(Skills.RUNECRAFTING, totalXp);
					player.setNextGraphics(new Graphics(3071));
				}
				player.setNextAnimation(new Animation(nodes.getEmoteId()));
				player.setNextFaceTile(node);
				Engine.sendProjectile(node, node, player, 3060, 31, 35, 35, 0,
						2, 0);
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						player.setNextGraphics(new Graphics(3062));
					}
				}, 1);
			}
		}
		return 1;
	}

	@Override
	public void stop(Player player) {
		player.setNextAnimation(new Animation(16599));
		setActionDelay(player, 3);
	}
}
