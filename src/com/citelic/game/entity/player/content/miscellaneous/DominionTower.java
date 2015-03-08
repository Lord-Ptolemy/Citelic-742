package com.citelic.game.entity.player.content.miscellaneous;

import java.io.Serializable;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.cores.CoresManager;
import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.cutscenes.Cutscene;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.MapBuilder;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.DTRank;
import com.citelic.utility.Utilities;

public final class DominionTower implements Serializable {

	public static final class Boss {

		private String name;
		private String text;
		private int[] ids;
		private boolean forceMulti;
		private Item item;
		private int voice;
		private int[] arena;

		public Boss(String name, String text, int... ids) {
			this(name, text, -1, false, null, NORMAL_ARENA, ids);
		}

		public Boss(String name, String text, int voice, boolean forceMulti,
				Item item, int[] arena, int... ids) {
			this.name = name;
			this.text = text;
			this.forceMulti = forceMulti;
			this.ids = ids;
			this.item = item;
			this.voice = voice;
			this.arena = arena;
		}

		public String getName() {
			return name;
		}

		public boolean isForceMulti() {
			return forceMulti;
		}
	}

	public static final int CLIMBER = 0, ENDURANCE = 1, MAX_FACTOR = 10000000;

	private static final long serialVersionUID = -5230255619877910203L;
	private transient Player player;

	private transient int[] mapBaseCoords;
	private int nextBossIndex;
	private int progress;
	private long totalScore;
	private boolean talkedWithFace;
	private int killedBossesCount;
	private int maxFloorEndurance;

	private int maxFloorClimber;

	private static final int[] NORMAL_ARENA = { 456, 768 }, NOMAD_ARENA = {
			456, 776 };

	private static final int[] MUSICS = { 1015, 1022, 1018, 1016, 1021 };

	private static final Boss[] BOSSES = {
			new Boss("Elvarg", "Grrrr", 14548),
			new Boss("Delrith", "Grrrr", -1, false, new Item(2402, 1),
					NORMAL_ARENA, 14578),
			new Boss("Karamel", "WOOOOOOOOOOOOOOOOOT", 3495),
			new Boss("Evil Chicken", "Bwak bwak bwak", 3375),
			new Boss("The Black Knight Titan", "Kill kill kill!", 14436),
			new Boss("Bouncer", "Grrr", 14483),
			// custom bosses
			new Boss("Corporeal Beast", null, -1, true, null, NORMAL_ARENA,
					8133),
			new Boss("Jad", "Roarrrrrrrrrrrrrrrrrrrrrrrrrr", 2745),
			new Boss("Kalphite Queen", null, 1158),
			new Boss("King Black Dragon", "Grrrr", 50),
			new Boss("Nomad", "You don't stand a chance!", 7985, true, null,
					NOMAD_ARENA, 8528), };

	public DominionTower() {
		nextBossIndex = -1;
	}

	public void createArena(final int mode) {
		player.closeInterfaces();
		player.lock();
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					boolean needDestroy = mapBaseCoords != null;
					final int[] oldMapBaseCoords = mapBaseCoords;
					mapBaseCoords = MapBuilder.findEmptyChunkBound(8, 8);
					MapBuilder.copyAllPlanesMap(
							BOSSES[getNextBossIndex()].arena[0],
							BOSSES[getNextBossIndex()].arena[1],
							mapBaseCoords[0], mapBaseCoords[1], 8);
					teleportToArena(mode);
					if (needDestroy) {
						EngineTaskManager.schedule(new EngineTask() {
							@Override
							public void run() {
								CoresManager.slowExecutor
										.execute(new Runnable() {
											@Override
											public void run() {
												try {
													MapBuilder
															.destroyMap(
																	oldMapBaseCoords[0],
																	oldMapBaseCoords[1],
																	8, 8);
												} catch (Exception e) {
													e.printStackTrace();
												} catch (Error e) {
													e.printStackTrace();
												}
											}
										});
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}

			}
		});
	}

	public NPC[] createBosses() {
		NPC[] bosses = new NPC[BOSSES[getNextBossIndex()].ids.length];
		for (int i = 0; i < BOSSES[getNextBossIndex()].ids.length; i++)
			bosses[i] = Engine.spawnNPC(BOSSES[getNextBossIndex()].ids[i],
					new Tile(getBaseX() + 37 + (i * 2), getBaseY() + 31, 2),
					-1, true, true);
		return bosses;
	}

	public void destroyArena(final boolean logout, int mode) {
		Tile tile = new Tile(3744, 6425, 0);
		if (logout)
			player.setLocation(tile);
		else {
			player.getControllerManager().removeControllerWithoutCheck();
			player.lock();
			player.setNextTile(tile);
			if (mode == ENDURANCE)
				progress = 0;
		}
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				CoresManager.slowExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							MapBuilder.destroyMap(mapBaseCoords[0],
									mapBaseCoords[1], 8, 8);
							if (!logout) {
								mapBaseCoords = null;
								player.unlock();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} catch (Error e) {
							e.printStackTrace();
						}
					}
				});
			}
		}, 1);
	}

	public int getBaseX() {
		return mapBaseCoords[0] << 3;
	}

	public int getBaseY() {
		return mapBaseCoords[1] << 3;
	}

	public int getBossesTotalLevel() {
		int level = 0;
		for (int id : BOSSES[getNextBossIndex()].ids)
			level = +NPCDefinitions.getNPCDefinitions(id).combatLevel;
		return level;
	}

	public int getKilledBossesCount() {
		return killedBossesCount;
	}

	public int getMaxFloorClimber() {
		return maxFloorClimber;
	}

	public int getMaxFloorEndurance() {
		return maxFloorEndurance;
	}

	public Boss getNextBoss() {
		return BOSSES[getNextBossIndex()];
	}

	public int getNextBossIndex() {
		if (nextBossIndex < 0 || nextBossIndex >= BOSSES.length)
			selectBoss();
		return nextBossIndex;
	}

	public int getProgress() {
		return progress;
	}

	public String getStartFightText(int message) {
		switch (message) {
		case 0:
			return "Kick my ass!";
		case 1:
			return "Please don't hit my face";
		case 2:
			return "Argh!";
		default:
			return "Bring it on!";
		}
	}

	public long getTotalScore() {
		return totalScore;
	}

	public void growFace() {
		player.getPackets().sendSound(7913, 0, 2);
		player.getDialogueManager()
				.startDialogue(
						"SimpleMessage",
						"The face on the wall groans and cowls at you. Perhaps you should",
						"talk to it first.");
	}

	/*
	 * 4928 15936
	 */
	/*
	 * 4960, 15968
	 */

	public void handleButtons(int interfaceId, int componentId) {
		if (interfaceId == 1164) {
			if (componentId == 26)
				openClimberMode();
			else if (componentId == 28)
				openEnduranceMode();
			else if (componentId == 29)
				openSpecialMode();
			else if (componentId == 30)
				openFreeStyleMode();
			else if (componentId == 31)
				openSpectate();
		} else if (interfaceId == 1163) {
			if (componentId == 89)
				player.closeInterfaces();
		} else if (interfaceId == 1168) {
			if (componentId == 254)
				player.closeInterfaces();
		} else if (interfaceId == 1170) {
			if (componentId == 85)
				player.closeInterfaces();
		} else if (interfaceId == 1173) {
			if (componentId == 58)
				player.closeInterfaces();
			else if (componentId == 59)
				startEnduranceMode();
		}
	}

	public boolean hasRequiriments() {
		return player.getSkills().getCombatLevelWithSummoning() >= 110;
	}

	/*
	 * 4928 15936
	 */
	/*
	 * 4961, 15968
	 */

	public boolean isTalkedWithFace() {
		return talkedWithFace;
	}

	public void loss(final int mode) {
		/*
		 * if(mapBaseCoords == null) { //died on logout
		 * player.setNextWorldTile(new WorldTile(3744, 6425, 0));
		 * player.getControlerManager().removeControlerWithoutCheck(); return; }
		 */
		removeItem();
		nextBossIndex = -1;
		player.lock();
		player.setNextTile(new Tile(getBaseX() + 35, getBaseY() + 31, 2));
		player.setNextFaceTile(new Tile(player.getX() + 1, player.getY(), 0));

		EngineTaskManager.schedule(new EngineTask() {
			int count;

			@Override
			public void run() {
				if (count == 0) {
					player.setNextAnimation(new Animation(836));
					player.getPackets()
							.closeInterface(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 11 : 0);
					player.getInterfaceManager().sendInterface(1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 8,
							"Unlucky, you lost!");
					player.getPackets().sendIComponentText(
							1172,
							10,
							"You leave with a dominion factor of: "
									+ player.getDominionFactor());
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 37), 2500);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 28), 800);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 42),
							Cutscene.getY(player, getBaseY() + 37), 2500, 6, 6);
					player.getPackets().sendVoice(7874);
				} else if (count == 4) {
					player.setForceMultiArea(false);
					player.reset();
					player.setNextAnimation(new Animation(-1));
					player.closeInterfaces();
					player.getPackets().sendResetCamera();
					player.unlock();
					destroyArena(false, mode);
					stop();
				}
				count++;
			}
		}, 0, 1);
	}

	public void openBankChest() {
		if (!talkedWithFace) {
			growFace();
			return;
		}
		player.getBank().openBank();
	}

	public void openClimberMode() {
		player.getPackets().sendGameMessage(
				"Only endurance mode is currently working.");
		// player.getInterfaceManager().sendScreenInterface(96, 1163);
		// selectBoss();
		// player.getPackets().sendIComponentText(1163, 32, "0"); // you leave
		// with
	}

	public void openEnduranceMode() {
		selectBoss();
		player.getInterfaceManager().sendScreenInterface(96, 1173);
		player.getPackets().sendIComponentText(1173, 25,
				BOSSES[getNextBossIndex()].name); // current
		player.getPackets().sendIComponentText(1173, 38,
				String.valueOf(progress + 1)); // current
		player.getPackets().sendIComponentText(1173, 52, "Y ?"); // current
		player.getPackets().sendIComponentText(1173, 29,
				String.valueOf(player.getDominionFactor())); // current
		player.getPackets().sendIComponentText(
				1173,
				31,
				player.getDominionFactor() == MAX_FACTOR ? "" : String
						.valueOf(getBossesTotalLevel() * 10)); // on
		// win
		player.getPackets().sendIComponentText(1173, 33,
				String.valueOf(player.getDominionFactor())); // on
		// death
	}

	public void openFreeStyleMode() {
		player.getPackets().sendGameMessage(
				"Only endurance mode is currently working.");
		// player.getInterfaceManager().sendScreenInterface(96, 1168);
	}

	public void openModes() {
		if (!hasRequiriments()) {
			player.getDialogueManager().startDialogue("DTSpectateReq");
			return;
		}
		if (!talkedWithFace) {
			growFace();
			return;
		}
		if (progress == 256) {
			player.getDialogueManager()
					.startDialogue(
							"SimpleMessage",
							"You have some dominion factor which you must exchange before",
							"starting another match.");
			player.getPackets()
					.sendGameMessage(
							"You can't go back into the arena, you must go to the next floor on entrance.");
			return;
		}
		player.getInterfaceManager().sendInterface(1164);
		player.getPackets().sendIComponentText(
				1164,
				27,
				progress == 0 ? "Ready for a new match" : "Floor progress: "
						+ progress);
	}

	public void openRewards() {
		if (!talkedWithFace) {
			talkToFace();
			return;
		}
		player.getPackets().sendVoice(7893);
		player.getInterfaceManager().sendInterface(1156);
	}

	public void openRewardsChest() {
		if (!talkedWithFace) {
			growFace();
			return;
		}
		progress = 0;
		player.setDominionFactor(0);
		player.getInterfaceManager().sendInterface(1171);
	}

	public void openSpecialMode() {
		player.getPackets().sendGameMessage(
				"Only endurance mode is currently working.");
		// player.getInterfaceManager().sendScreenInterface(96, 1170);
	}

	public void openSpectate() {
		player.getInterfaceManager().sendInterface(1157);
	}

	public void removeItem() {
		if (nextBossIndex == -1)
			return;
		if (BOSSES[nextBossIndex].item != null) {
			player.getInventory().deleteItem(
					BOSSES[nextBossIndex].item.getId(),
					BOSSES[nextBossIndex].item.getAmount());
			player.getEquipment().deleteItem(
					BOSSES[nextBossIndex].item.getId(),
					BOSSES[nextBossIndex].item.getAmount());
			player.getPlayerAppearance().generateAppearenceData();
		}
	}

	public void selectBoss() {
		if (nextBossIndex < 0 || nextBossIndex >= BOSSES.length)
			nextBossIndex = Utilities.random(BOSSES.length);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setTalkedWithFace(boolean talkedWithFace) {
		this.talkedWithFace = talkedWithFace;
	}

	private void startEnduranceMode() {
		if (progress == 256) {
			player.getDialogueManager()
					.startDialogue(
							"SimpleMessage",
							"You have some dominion factor which you must exchange before",
							"starting another match.");
			player.getPackets()
					.sendGameMessage(
							"You can't go back into the arena, you must go to the next floor on entrance.");
			return;
		}
		createArena(ENDURANCE);
	}

	public void startFight(final NPC[] bosses) {
		for (NPC boss : bosses) {
			boss.setCantInteract(true);
			boss.setNextFaceTile(new Tile(boss.getX() - 1, boss.getY(), 0));
		}
		player.lock();
		player.setNextTile(new Tile(getBaseX() + 25, getBaseY() + 32, 2));
		player.setNextFaceTile(new Tile(getBaseX() + 26, getBaseY() + 32, 0));
		final int index = getNextBossIndex();
		EngineTaskManager.schedule(new EngineTask() {

			private int count;

			@Override
			public void run() {
				if (count == 0) {
					player.getInterfaceManager()
							.sendTab(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 11 : 0,
									1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 7, true);
					player.getPackets().sendIComponentText(1172, 4,
							player.getDisplayName());
					player.getPackets().sendConfig(1241, 1);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 25),
							Cutscene.getY(player, getBaseY() + 38), 1800);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 25),
							Cutscene.getY(player, getBaseY() + 29), 800);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 32),
							Cutscene.getY(player, getBaseY() + 38), 1800, 6, 6);
				} else if (count == 1) {
					player.setNextForceTalk(new ForceTalk(
							getStartFightText(Utilities.getRandom(1))));
				} else if (count == 3) {
					player.getPackets().sendHideIComponent(1172, 2, false);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 6,
							BOSSES[index].name);
					player.getPackets().sendConfig(1241, 0);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 37), 1800);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 28), 800);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 42),
							Cutscene.getY(player, getBaseY() + 37), 1800, 6, 6);
				} else if (count == 4) {
					if (BOSSES[index].text != null)
						bosses[0].setNextForceTalk(new ForceTalk(
								BOSSES[index].text));
					if (BOSSES[index].voice != -1)
						player.getPackets().sendVoice(BOSSES[index].voice);
				} else if (count == 6) {
					player.getControllerManager().sendInterfaces();
					player.getInterfaceManager().sendInterface(1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 8, "Fight!");
					player.getPackets().sendHideIComponent(1172, 10, true);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 32),
							Cutscene.getY(player, getBaseY() + 36), 0);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 32),
							Cutscene.getY(player, getBaseY() + 16), 5000);
					player.getPackets().sendVoice(7882);
				} else if (count == 8) {
					if (nextBossIndex != -1 && BOSSES[index].item != null)
						Engine.addGroundItem(BOSSES[index].item, new Tile(
								getBaseX() + 26, getBaseY() + 33, 2));
					player.closeInterfaces();
					player.getPackets().sendResetCamera();
					for (NPC boss : bosses) {
						boss.setCantInteract(false);
						boss.setTarget(player);
					}
					player.unlock();
					stop();
				}
				count++;
			}

		}, 0, 1);
	}

	public void talkToFace() {
		talkToFace(false);
	}

	public void talkToFace(boolean fromDialogue) {
		if (!hasRequiriments()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need at least level 110 combat to use this tower.");
			return;
		}
		if (!talkedWithFace)
			player.getDialogueManager().startDialogue("StrangeFace");
		else {
			if (!fromDialogue)
				player.getPackets().sendVoice(7893);
			player.getInterfaceManager().sendInterface(1160);
		}
	}

	private void teleportToArena(int mode) {
		player.setNextFaceTile(new Tile(getBaseX() + 11, getBaseY() + 29, 0));
		player.getControllerManager().startController("DTControler", mode);
		player.unlock();
		player.setNextTile(new Tile(getBaseX() + 10, getBaseY() + 29, 2));
		player.getMusicsManager().playMusic(
				MUSICS[Utilities.getRandom(MUSICS.length - 1)]);
	}

	public void viewScoreBoard() {
		DTRank.showRanks(player);
	}

	public void win(int mode) {
		removeItem();
		int factor = getBossesTotalLevel() * (mode == CLIMBER ? 100 : 10);
		progress++;
		if (mode == CLIMBER) {
			if (progress > maxFloorClimber)
				maxFloorClimber = progress;
		} else if (mode == ENDURANCE) {
			if (progress > maxFloorEndurance)
				maxFloorEndurance = progress;
		}

		killedBossesCount++;
		player.setDominionFactor(player.getDominionFactor() + factor);
		totalScore += factor;
		if (player.getDominionFactor() > MAX_FACTOR) {
			player.setDominionFactor(MAX_FACTOR);
			player.getPackets()
					.sendGameMessage(
							"You've reached the maximum Dominion Factor you can get so you should spend it!");
		}
		DTRank.checkRank(player, mode, BOSSES[getNextBossIndex()].name);
		nextBossIndex = -1;
		player.lock();
		player.setNextTile(new Tile(getBaseX() + 35, getBaseY() + 31, 2));
		player.setNextFaceTile(new Tile(getBaseX() + 36, getBaseY() + 31, 0));

		EngineTaskManager.schedule(new EngineTask() {

			private int count;

			@Override
			public void run() {
				if (count == 0) {
					player.getPackets()
							.closeInterface(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 11 : 0);
					player.getInterfaceManager().sendInterface(1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 8,
							"Yeah! You won!");
					player.getPackets().sendIComponentText(
							1172,
							10,
							"You now have a dominion factor of: "
									+ player.getDominionFactor());
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 37), 2500);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 28), 800);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 42),
							Cutscene.getY(player, getBaseY() + 37), 2500, 6, 6);
					player.getPackets().sendVoice(7897);
				} else if (count == 4) {
					player.reset();
					player.closeInterfaces();
					player.getPackets().sendResetCamera();
					player.unlock();
					stop();
				}
				count++;
			}
		}, 0, 1);

	}

}
