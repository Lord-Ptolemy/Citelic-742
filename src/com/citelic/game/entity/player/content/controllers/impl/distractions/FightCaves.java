package com.citelic.game.entity.player.content.controllers.impl.distractions;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.citelic.GameConstants;
import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.impl.fightcaves.FightCavesNPC;
import com.citelic.game.entity.npc.impl.fightcaves.TzKekCaves;
import com.citelic.game.entity.npc.impl.fightcaves.TzTok_Jad;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.miscellaneous.pets.Pets;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

public class FightCaves extends Controller {

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	public static final Tile OUTSIDE = new Tile(4610, 5130, 0);

	private static final int THHAAR_MEJ_JAL = 2617;
	private static final int[] MUSICS = { 1088, 1082, 1086 };

	public static void enterFightCaves(Player player) {
		if (player.getFamiliar() != null || player.getPet() != null
				|| Summoning.hasPouch(player) || Pets.hasPet(player)) {
			player.getDialogueManager()
					.startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL,
							"No Kimit-Zil in the pits! This is a fight for YOU, not your friends!");
			return;
		}
		player.getControllerManager().startController("FightCaves", 1);
		isTask = 0;
	}

	private final int[][] WAVES = { { 2734 }, { 2734, 2734 }, { 2736 },
			{ 2736, 2734 }, { 2736, 2734, 2734 }, { 2736, 2736 }, { 2739 },
			{ 2739, 2734 }, { 2739, 2734, 2734 }, { 2739, 2736 },
			{ 2739, 2736, 2734 }, { 2739, 2736, 2734, 2734 },
			{ 2739, 2736, 2736 }, { 2739, 2739 }, { 2741 }, { 2741, 2734 },
			{ 2741, 2734, 2734 }, { 2741, 2736 }, { 2741, 2736, 2734 },
			{ 2741, 2736, 2734, 2734 }, { 2741, 2736, 2736 }, { 2741, 2739 },
			{ 2741, 2739, 2734 }, { 2741, 2739, 2734, 2734 },
			{ 2741, 2739, 2736 }, { 2741, 2739, 2736, 2734 },
			{ 2741, 2739, 2736, 2734, 2734 }, { 2741, 2739, 2736, 2736 },
			{ 2741, 2739, 2739 }, { 2741, 2741 }, { 2743 }, { 2743, 2734 },
			{ 2743, 2734, 2734 }, { 2743, 2736 }, { 2743, 2736, 2734 },
			{ 2743, 2736, 2734, 2734 }, { 2743, 2736, 2736 }, { 2743, 2739 },
			{ 2743, 2739, 2734 }, { 2743, 2739, 2734, 2734 },
			{ 2743, 2739, 2736 }, { 2743, 2739, 2736, 2734 },
			{ 2743, 2739, 2736, 2734, 2734 }, { 2743, 2739, 2736, 2736 },
			{ 2743, 2739, 2739 }, { 2743, 2741 }, { 2743, 2741, 2734 },
			{ 2743, 2741, 2734, 2734 }, { 2743, 2741, 2736 },
			{ 2743, 2741, 2736, 2734 }, { 2743, 2741, 2736, 2734, 2734 },
			{ 2743, 2741, 2736, 2736 }, { 2743, 2741, 2739 },
			{ 2743, 2741, 2739, 2734 }, { 2743, 2741, 2739, 2734, 2734 },
			{ 2743, 2741, 2739, 2736 }, { 2743, 2741, 2739, 2736, 2734 },
			{ 2743, 2741, 2739, 2736, 2734, 2734 },
			{ 2743, 2741, 2739, 2736, 2736 }, { 2743, 2741, 2739, 2739 },
			{ 2743, 2741, 2741 }, { 2743, 2743 }, { 2745 } };
	private int[] boundChuncks;
	private Stages stage;
	private boolean logoutAtEnd;
	private boolean login;
	public boolean spawned;

	public int selectedMusic;

	private static int isTask;

	/*
	 * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele
	 */
	public void exitCave(int type) {
		stage = Stages.DESTROYING;
		if (type != 1337) {
			Tile outside = new Tile(OUTSIDE, 2); // radomizes alil
			player.getInterfaceManager().sendQuests();

			if (type == 0 || type == 2)
				player.setLocation(outside);
			else {
				player.setForceMultiArea(false);
				player.getPackets().closeInterface(
						player.getInterfaceManager().hasRezizableScreen() ? 11
								: 0);
				if (type == 1 || type == 4) {
					player.setNextTile(outside);
					if (type == 4) {
						for (Player players : Engine.getPlayers()) {
							if (players == null)
								continue;
							players.getPackets()
									.sendGameMessage(
											"<img=7><col=CC0000>Server: "
													+ player.getDisplayName()
													+ " has just completed Fight caves.");
						}
						player.setCompletedFightCaves();
						player.reset();
						if (isTask == 0)
							player.getDialogueManager()
									.startDialogue(
											"SimpleNPCMessage",
											THHAAR_MEJ_JAL,
											"You even defeated Tz Tok-Jad, I am most impressed! Please accept this gift as a reward.");
						player.getPackets().sendGameMessage(
								"You were victorious!!");
						if (!player.getInventory().addItem(6570, 1)) {
							Engine.addGroundItem(new Item(6570, 1), new Tile(
									player), player, true, 180, true);
							Engine.addGroundItem(new Item(6529, 16064),
									new Tile(player), player, true, 180, true);
						} else if (!player.getInventory().addItem(6529, 16064))
							Engine.addGroundItem(new Item(6529, 16064),
									new Tile(player), player, true, 180, true);
					} else if (getCurrentWave() == 1)
						player.getDialogueManager()
								.startDialogue("SimpleNPCMessage",
										THHAAR_MEJ_JAL,
										"Well I suppose you tried... better luck next time.");
					else {
						int tokkul = getCurrentWave() * 8032 / WAVES.length;
						tokkul *= GameConstants.DROP_RATE; // 10x more
						if (!player.getInventory().addItem(6529, tokkul))
							Engine.addGroundItem(new Item(6529, tokkul),
									new Tile(player), player, true, 180, true);
						player.getDialogueManager()
								.startDialogue("SimpleNPCMessage",
										THHAAR_MEJ_JAL,
										"Well done in the cave, here, take TokKul as reward.");
					}
				}
			}
		}
		removeController();
		/*
		 * 1200 delay because of leaving
		 */
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	@Override
	public void forceClose() {
		/*
		 * shouldnt happen
		 */
		if (stage != Stages.RUNNING)
			return;
		exitCave(2);
	}

	public int getCurrentWave() {
		if (getArguments() == null || getArguments().length == 0)
			return 0;
		return (Integer) getArguments()[0];
	}

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getNPCDefinitions(2745);
	}

	public Tile getSpawnTile() {
		switch (Utilities.random(5)) {
		case 0:
			return getWorldTile(11, 16);
		case 1:
			return getWorldTile(51, 25);
		case 2:
			return getWorldTile(10, 50);
		case 3:
			return getWorldTile(46, 49);
		case 4:
		default:
			return getWorldTile(32, 30);
		}
	}

	/*
	 * gets worldtile inside the map
	 */
	public Tile getWorldTile(int mapX, int mapY) {
		return new Tile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY,
				0);
	}

	public void loadCave(final boolean login) {
		this.login = login;
		stage = Stages.LOADING;
		player.lock(); // locks player
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
				MapBuilder.copyAllPlanesMap(552, 640, boundChuncks[0],
						boundChuncks[1], 8);
				selectedMusic = MUSICS[Utilities.random(MUSICS.length)];
				player.setNextTile(!login ? getWorldTile(46, 61)
						: getWorldTile(32, 32));
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						if (!login) {
							Tile walkTo = getWorldTile(32, 32);
							player.addWalkSteps(walkTo.getX(), walkTo.getY());
						}
						player.getInterfaceManager().closeQuests();
						player.getDialogueManager()
								.startDialogue("SimpleNPCMessage",
										THHAAR_MEJ_JAL,
										"You're on your own now, JalYt.<br>Prepare to fight for your life!");
						player.setForceMultiArea(true);
						playMusic();
						player.unlock(); // unlocks player
						stage = Stages.RUNNING;
					}

				}, 1);
				if (!login) {
					CoresManager.fastExecutor.schedule(new TimerTask() {

						@Override
						public void run() {
							if (stage != Stages.RUNNING)
								return;
							try {
								startWave();
							} catch (Throwable t) {
								Logger.handle(t);
							}
						}
					}, 6000);
				}
			}
		});
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean login() {
		loadCave(true);
		return false;
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean logout() {
		/*
		 * only can happen if dungeon is loading and system update happens
		 */
		if (stage != Stages.RUNNING)
			return false;
		exitCave(0);
		return false;

	}

	@Override
	public void magicTeleported(int type) {
		player.setForceMultiArea(false);
		exitCave(1337);
	}

	@Override
	public void moved() {
		if (stage != Stages.RUNNING || !login)
			return;
		login = false;
		setWaveEvent();
	}

	public void nextWave() {
		playMusic();
		setCurrentWave(getCurrentWave() + 1);
		if (logoutAtEnd) {
			player.forceLogout();
			return;
		}
		setWaveEvent();
	}

	public void playMusic() {
		player.getMusicsManager().playMusic(selectedMusic);
	}

	@Override
	public void process() {
		if (spawned) {
			List<Integer> npcs = Engine.getRegion(
					getWorldTile(46, 61).getRegionId()).getNPCsIndexes();
			if (npcs == null || npcs.isEmpty()) {
				spawned = false;
				nextWave();
			}
		}
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (stage != Stages.RUNNING)
			return false;
		if (interfaceId == 182 && (componentId == 6 || componentId == 13)) {
			if (!logoutAtEnd) {
				logoutAtEnd = true;
				player.getPackets()
						.sendGameMessage(
								"<col=ff0000>You will be logged out automatically at the end of this wave.");
				player.getPackets()
						.sendGameMessage(
								"<col=ff0000>If you log out sooner, you will have to repeat this wave.");
			} else
				player.forceLogout();
			return false;
		}
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		return true;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 9357) {
			if (stage != Stages.RUNNING)
				return false;
			exitCave(1);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		return true;
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"You have been defeated!");
				} else if (loop == 3) {
					player.reset();
					exitCave(1);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public void setCurrentWave(int wave) {
		if (getArguments() == null || getArguments().length == 0)
			this.setArguments(new Object[1]);
		getArguments()[0] = wave;
	}

	public void setWaveEvent() {
		if (getCurrentWave() == 63)
			player.getDialogueManager().startDialogue("SimpleNPCMessage",
					THHAAR_MEJ_JAL, "Look out, here comes TzTok-Jad!");
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					if (stage != Stages.RUNNING)
						return;
					startWave();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 600);
	}

	public void spawnHealers() {
		if (stage != Stages.RUNNING)
			return;
		for (int i = 0; i < 4; i++)
			new FightCavesNPC(2746, getSpawnTile());
	}

	@Override
	public void start() {
		loadCave(false);
	}

	public void startWave() {
		int currentWave = getCurrentWave();
		if (currentWave > WAVES.length) {
			win();
			return;
		}
		player.getInterfaceManager().sendTab(
				player.getInterfaceManager().hasRezizableScreen() ? 69 : 47,
				316);
		player.getPackets().sendConfig(639, currentWave);
		if (stage != Stages.RUNNING)
			return;
		for (int id : WAVES[currentWave - 1]) {
			if (id == 2736)
				new TzKekCaves(id, getSpawnTile());
			else if (id == 2745)
				new TzTok_Jad(id, getSpawnTile(), this);
			else
				new FightCavesNPC(id, getSpawnTile());
		}
		spawned = true;
	}

	public void win() {
		if (stage != Stages.RUNNING)
			return;
		exitCave(4);
	}
}