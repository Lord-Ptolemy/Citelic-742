package com.citelic.game.entity.player.content.controllers.impl.distractions.bosses;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.nomad.Nomad;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.FadingScreen;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.cutscenes.Cutscene;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.managers.QuestManager.Quests;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

public class NomadsRequiem extends Controller {

	public static enum DungeonPart {
		ENTRANCE(409, 733, 7, 3, 46, 12, 23, 21), HALL(408, 736, 8, 8, 31, 2,
				31, 15), THRONE(418, 730, 5, 5, 16, 2);

		private int chunkX, chunkY, sizeX, sizeY;
		private int[] doorPositions;

		private DungeonPart(int chunkX, int chunkY, int sizeX, int sizeY,
				int... doorPositions) {
			this.chunkX = chunkX;
			this.chunkY = chunkY;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.doorPositions = doorPositions;
		}

	}

	public static final Tile OUTSIDE = new Tile(1895, 3177, 0);

	public static void enterNomadsRequiem(Player player) {
		player.getControllerManager().startController("NomadsRequiem");
	}

	private DungeonPart currentPart;

	private int[] mapBaseChunks;

	public void continueThroneScene() {
		final GameObject object = new GameObject(48072, 10, 0, getBaseX() + 14,
				getBaseY() + 20, 0);
		EngineTaskManager.schedule(new EngineTask() {

			private int stage;

			@Override
			public void run() {
				if (stage == 1) {
					Engine.sendObjectAnimation(player, object, new Animation(
							12703));
				} else if (stage == 4) {
					player.setNextAnimation(new Animation(7272));
				} else if (stage == 8) {
					Tile throne = new Tile(getBaseX() + 16, getBaseY() + 20, 0);
					Nomad nomad = (Nomad) Engine.spawnNPC(8528, throne, -1,
							true, true);
					nomad.setDirection(Utilities.getFaceDirection(0, -1));
					nomad.setThroneTile(throne);
					nomad.setTarget(player);
					Engine.destroySpawnedObject(object, false);
					player.getPackets().sendSpawnedObject(
							new GameObject(48073, 10, 0, getBaseX() + 14,
									getBaseY() + 22, 0));
					player.unlock();
					player.setRun(true);
					stop();
				}
				stage++;
			}
		}, 0, 0);
	}

	public void destroyCurrentPart() {
		destroyPart(mapBaseChunks, currentPart);
	}

	public void destroyPart(final int[] mapBaseChunks, final DungeonPart part) {
		// since it will change after
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				MapBuilder.destroyMap(mapBaseChunks[0], mapBaseChunks[1],
						part.sizeX, part.sizeY);
			}
		}, 1200, TimeUnit.MILLISECONDS);

	}

	public void enter(final DungeonPart part, final int doorIndex) {
		player.lock();
		final long time = FadingScreen.fade(player);
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					final int[] oldMapBaseChunks = mapBaseChunks;
					final DungeonPart oldPart = currentPart;
					mapBaseChunks = MapBuilder.findEmptyChunkBound(part.sizeX,
							part.sizeY);
					currentPart = part;
					MapBuilder.copyAllPlanesMap(part.chunkX, part.chunkY,
							mapBaseChunks[0], mapBaseChunks[1], part.sizeX,
							part.sizeY);

					FadingScreen.unfade(player, time, new Runnable() {
						@Override
						public void run() {
							destroyPart(oldMapBaseChunks, oldPart);
							enterDoor(doorIndex);
						}
					});

				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		});
	}

	public void enterDoor(int doorIndex) {
		player.setNextTile(new Tile(getBaseX()
				+ currentPart.doorPositions[doorIndex * 2], getBaseY()
				+ +currentPart.doorPositions[doorIndex * 2 + 1], 0));
		player.getMusicsManager().playMusic(
				currentPart == DungeonPart.THRONE ? 727 : 728);
		if (currentPart == DungeonPart.THRONE)
			startThroneScene();
		else if (currentPart == DungeonPart.ENTRANCE
				&& doorIndex == 0
				&& player.getQuestManager()
						.getQuestStage(Quests.NOMADS_REQUIEM) == 0)
			sendFirstScene();
		else {
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					player.unlock();
				}
			}, 1);
		}
	}

	@Override
	public void forceClose() {
		leave(false);
	}

	public int getBaseX() {
		return mapBaseChunks[0] << 3;
	}

	public int getBaseY() {
		return mapBaseChunks[1] << 3;
	}

	public void leave(boolean logout) {
		if (logout)
			player.setLocation(OUTSIDE);
		else {
			player.setNextTile(OUTSIDE);
			player.setForceMultiArea(false);
			removeController();
		}
		if (mapBaseChunks != null && currentPart != null)
			destroyCurrentPart();
	}

	@Override
	public boolean login() {
		leave(false);
		return true;
	}

	@Override
	public boolean logout() {
		leave(true);
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		if (type != Magic.OBJECT_TELEPORT)
			leave(false);
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 47981:
			enter(DungeonPart.HALL, 0);
			return false;
		case 47983:
			enter(DungeonPart.ENTRANCE, 1);
			return false;
		case 47974:
			enter(DungeonPart.THRONE, 0);
			return false;
		case 47976:
			enter(DungeonPart.HALL, 1);
			return false;
		case 47980:
			player.lock();
			player.setNextAnimation(new Animation(828));
			FadingScreen.fade(player, new Runnable() {

				@Override
				public void run() {
					player.getControllerManager().forceStop();
					player.unlock();
				}

			});
			return false;
		default:
			return true;
		}
	}

	@Override
	public boolean sendDeath() {
		if (currentPart == DungeonPart.THRONE) {
			Dialogue.closeNoContinueDialogue(player);
			List<Integer> indexes = Engine.getRegion(player.getRegionId())
					.getNPCsIndexes();
			if (indexes != null && indexes.size() >= 1) {
				NPC nomad = Engine.getNPCs().get(indexes.get(0));
				if (nomad != null) {
					player.getPackets().sendVoice(7987);
					nomad.setNextForceTalk(new ForceTalk("Pathetic!"));
				}
			}
		}
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
							"Oh dear, you have died.");
				} else if (loop == 3) {
					player.reset();
					player.setNextTile(OUTSIDE);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.setForceMultiArea(false);
					player.getPackets().sendMusicEffect(90);
					removeController();
					destroyCurrentPart();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public void sendFirstScene() {
		player.getQuestManager().setQuestStage(Quests.NOMADS_REQUIEM, 1);
		final NPC nomad = Engine.spawnNPC(8531, new Tile(getBaseX() + 41,
				getBaseY() + 11, 0), -1, true, true);
		nomad.setRun(true);
		nomad.setDirection(Utilities.getFaceDirection(1, 0));
		EngineTaskManager.schedule(new EngineTask() {
			private int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.getPackets().sendVoice(7985);
					nomad.setNextForceTalk(new ForceTalk(
							"You don't stand a chance "
									+ player.getDisplayName() + "!"));
					Dialogue.sendNPCDialogueNoContinue(
							player,
							nomad.getId(),
							9827,
							"You don't stand a chance "
									+ player.getDisplayName() + "!");
				} else if (stage == 6) {
					player.setNextFaceTile(nomad);
					player.setNextForceTalk(new ForceTalk("What? Who are you?"));
				} else if (stage == 12) {
					/*
					 * nomad.setNextAnimation(new Animation(12729));
					 * nomad.setNextGraphics(new Graphics(1576));
					 */
					Tile walk = new Tile(getBaseX() + 35, getBaseY() + 11, 0);
					nomad.addWalkSteps(walk.getX(), walk.getY(), -1, false);
					nomad.setNextForceTalk(new ForceTalk(
							"You'll find out... soon."));
					Dialogue.sendNPCDialogueNoContinue(player, nomad.getId(),
							9827, "You'll find out... soon.");
				} else if (stage == 16) {
					nomad.finish();
					player.getDialogueManager().startDialogue(
							"SimplePlayerMessage",
							"Maybe I should follow that guy..");
					Dialogue.closeNoContinueDialogue(player);
					player.unlock();
					stop();
				}
				stage++;
			}
		}, 1, 0);
	}

	@Override
	public void start() {
		enter(DungeonPart.ENTRANCE, 0);
		player.setForceMultiArea(true);
	}

	public void startThroneScene() {
		final GameObject object = new GameObject(48072, 10, 0, getBaseX() + 14,
				getBaseY() + 20, 0);
		Engine.spawnObject(object, false);
		final NomadsRequiem requiem = this;
		EngineTaskManager.schedule(new EngineTask() {

			private int stage;

			@Override
			public void run() {
				if (stage == 1) {
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 19),
							Cutscene.getY(player, getBaseY() + 14), 3000);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 17),
							Cutscene.getY(player, getBaseY() + 5), 2000);
					player.setRun(false);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 23),
							Cutscene.getY(player, getBaseY() + 8), 2500, 4, 4);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 17),
							Cutscene.getY(player, getBaseY() + 14), 2000, 2, 2);
					player.addWalkSteps(getBaseX() + 16, getBaseY() + 19, -1,
							false);
				} else if (stage == 10) {
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 16),
							Cutscene.getY(player, getBaseY() + 8), 2500, 4, 4);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 16),
							Cutscene.getY(player, getBaseY() + 14), 2000, 2, 2);
				} else if (stage == 15) {
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 16),
							Cutscene.getY(player, getBaseY() + 21), 800, 6, 6);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 16),
							Cutscene.getY(player, getBaseY() + 14), 1800, 6, 6);
				} else if (stage == 20) {
					player.getDialogueManager().startDialogue("NomadThrone",
							8528, requiem);
					player.getPackets().sendResetCamera();
					stop();
				}
				stage++;
			}
		}, 0, 0);
	}
}