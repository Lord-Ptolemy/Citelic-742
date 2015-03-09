package com.citelic.game.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.citelic.GameConstants;
import com.citelic.GameServer;
import com.citelic.cores.CoresManager;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.EntityList;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.corp.CorporealBeast;
import com.citelic.game.entity.npc.impl.dragons.KingBlackDragon;
import com.citelic.game.entity.npc.impl.glacor.Glacor;
import com.citelic.game.entity.npc.impl.godwars.GodWarMinion;
import com.citelic.game.entity.npc.impl.godwars.armadyl.GodwarsArmadylFaction;
import com.citelic.game.entity.npc.impl.godwars.armadyl.KreeArra;
import com.citelic.game.entity.npc.impl.godwars.bandos.GeneralGraardor;
import com.citelic.game.entity.npc.impl.godwars.bandos.GodwarsBandosFaction;
import com.citelic.game.entity.npc.impl.godwars.saradomin.CommanderZilyana;
import com.citelic.game.entity.npc.impl.godwars.saradomin.GodwarsSaradominFaction;
import com.citelic.game.entity.npc.impl.godwars.zamorak.GodwarsZamorakFaction;
import com.citelic.game.entity.npc.impl.godwars.zamorak.KrilTstsaroth;
import com.citelic.game.entity.npc.impl.godwars.zaros.GodwarsZarosFaction;
import com.citelic.game.entity.npc.impl.godwars.zaros.Nex;
import com.citelic.game.entity.npc.impl.godwars.zaros.NexMinion;
import com.citelic.game.entity.npc.impl.kalph.KalphiteQueen;
import com.citelic.game.entity.npc.impl.nomad.FlameVortex;
import com.citelic.game.entity.npc.impl.nomad.Nomad;
import com.citelic.game.entity.npc.impl.others.AbyssalDemon;
import com.citelic.game.entity.npc.impl.others.BanditCampBandits;
import com.citelic.game.entity.npc.impl.others.Bork;
import com.citelic.game.entity.npc.impl.others.ChaosElemental;
import com.citelic.game.entity.npc.impl.others.ConditionalDeath;
import com.citelic.game.entity.npc.impl.others.HarpieBug;
import com.citelic.game.entity.npc.impl.others.HoleInTheWall;
import com.citelic.game.entity.npc.impl.others.ItemHunterNPC;
import com.citelic.game.entity.npc.impl.others.Jadinko;
import com.citelic.game.entity.npc.impl.others.Kurask;
import com.citelic.game.entity.npc.impl.others.LivingRock;
import com.citelic.game.entity.npc.impl.others.Lucien;
import com.citelic.game.entity.npc.impl.others.MasterOfFear;
import com.citelic.game.entity.npc.impl.others.MercenaryMage;
import com.citelic.game.entity.npc.impl.others.MutatedZygomites;
import com.citelic.game.entity.npc.impl.others.Revenant;
import com.citelic.game.entity.npc.impl.others.RockCrabs;
import com.citelic.game.entity.npc.impl.others.TormentedDemon;
import com.citelic.game.entity.npc.impl.others.Werewolf;
import com.citelic.game.entity.npc.impl.slayer.Strykewyrm;
import com.citelic.game.entity.npc.impl.sorgar.Elemental;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.hunter.BoxAction.HunterNPC;
import com.citelic.game.entity.player.content.controllers.impl.distractions.WarriorsGuild;
import com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars.FfaZone;
import com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars.RequestController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.GodWarsBosses;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.ZarosGodwars;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager;
import com.citelic.game.entity.player.content.controllers.impl.dungeons.JadinkoLair;
import com.citelic.game.entity.player.content.miscellaneous.LivingRockCavern;
import com.citelic.game.entity.player.content.miscellaneous.ServerMessages;
import com.citelic.game.entity.player.content.miscellaneous.distractions.WildyWyrmManager;
import com.citelic.game.entity.player.item.FloorItem;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemConstants;
import com.citelic.game.map.Region;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.objects.OwnedObjectManager;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.codec.decode.impl.ButtonHandler;
import com.citelic.utility.AntiFlood;
import com.citelic.utility.IPBanL;
import com.citelic.utility.IPMute;
import com.citelic.utility.Logger;
import com.citelic.utility.MACBan;
import com.citelic.utility.PkRank;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.ShopsHandler;
import com.citelic.utility.Utilities;

public final class Engine {

	public static int exiting_delay;
	public static long exiting_start;

	public static SoulWarsManager soulWars;

	public static long currentTime;

	private static final EntityList<Player> players = new EntityList<Player>(
			GameConstants.SV_PLAYERS_LIMIT);

	private static final EntityList<NPC> npcs = new EntityList<NPC>(
			GameConstants.SV_NPCS_LIMIT);
	private static final Map<Integer, Region> regions = Collections
			.synchronizedMap(new HashMap<Integer, Region>());

	private static boolean checkAgility;

	private static final Object LOCK = new Object();

	private static final EntityList<Player> lobbyPlayers = new EntityList<Player>(
			GameConstants.SV_PLAYERS_LIMIT);

	public static List<Tile> restrictedTiles = new ArrayList<Tile>();

	private static void addAutoRestock() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ShopsHandler.loadUnpackedShops();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 2, TimeUnit.HOURS);
	}

	private static void growPatchesTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : Engine.players) {
						if (player != null && player.getFarming() != null
								&& !player.hasFinished()) {
							player.getFarming().growAllPatches(player);
						}
					}
				} catch (Throwable e) {
				}
			}
		}, 5, 5, TimeUnit.MINUTES);
	}

	private static final void addDrainPrayerTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead()
								|| !player.isRunning())
							continue;
						player.getPrayer().processPrayerDrain();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 600);
	}

	private static final void addServerMessageEvent() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				try {
					ServerMessages.execute();

				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, 0, 4, TimeUnit.MINUTES);
	}

	public static final void addGroundItem(final Item item, final Tile tile) {
		final FloorItem floorItem = new FloorItem(item, tile, null, false,
				false);
		final Region region = getRegion(tile.getRegionId());
		region.forceGetFloorItems().add(floorItem);
		int regionId = tile.getRegionId();
		for (Player player : players) {
			if (player == null || !player.isActive() || player.hasFinished()
					|| player.getZ() != tile.getZ()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
	}

	public static final FloorItem addGroundItem(final Item item,
			final Tile tile, final Player owner/*
												 * null for default
												 */, boolean invisible,
			long hiddenTime/*
							 * default 3 minutes
							 */, int type) {
		return Engine.addGroundItem(item, tile, owner, invisible, hiddenTime,
				type, 150);
	}

	/*
	 * type 0 - gold if not tradeable type 1 - gold if destroyable type 2 - no
	 * gold
	 */
	public static final FloorItem addGroundItem(final Item item,
			final Tile tile, final Player owner, boolean invisible,
			long hiddenTime/*
							 * default 3 minutes
							 */, int type, final int publicTime) {
		if (type != 2) {
			if (type == 0 || type == 1 && item.getDefinitions().isDestroyItem()) {

				int price = item.getDefinitions().getValue();
				if (price <= 0)
					return null;
				item.setId(995);
				item.setAmount(price);
			}
		}
		final FloorItem floorItem = new FloorItem(item, tile, owner,
				owner != null, invisible);
		final Region region = Engine.getRegion(tile.getRegionId());
		region.forceGetFloorItems().add(floorItem);
		if (invisible) {
			if (owner != null) {
				owner.getPackets().sendGroundItem(floorItem);
			}
			// becomes visible after x time
			if (hiddenTime != -1) {
				CoresManager.slowExecutor.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							Engine.turnPublic(floorItem, publicTime);
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, hiddenTime, TimeUnit.SECONDS);
			}
		} else {
			// visible
			int regionId = tile.getRegionId();
			for (Player player : Engine.players) {
				if (player == null || player.hasFinished()
						|| player.getZ() != tile.getZ()
						|| !player.getMapRegionsIds().contains(regionId)) {
					continue;
				}
				player.getPackets().sendGroundItem(floorItem);
			}
			// disapears after this time
			if (publicTime != -1) {
				Engine.removeGroundItem(floorItem, publicTime);
			}
		}
		return floorItem;
	}

	public static final void addGroundItem(final Item item, final Tile tile,
			final Player owner/*
							 * null for default
							 */, final boolean underGrave, long hiddenTime/*
																		 * default
																		 * 3
																		 * minutes
																		 */,
			boolean invisible) {
		addGroundItem(item, tile, owner, underGrave, hiddenTime, invisible,
				false, 180);
	}

	public static final void addGroundItem(final Item item, final Tile tile,
			final Player owner/*
							 * null for default
							 */, final boolean underGrave, long hiddenTime/*
																		 * default
																		 * 3
																		 * minutes
																		 */,
			boolean invisible, boolean intoGold) {
		addGroundItem(item, tile, owner, underGrave, hiddenTime, invisible,
				intoGold, 180);
	}

	public static final void addGroundItem(final Item item, final Tile tile,
			final Player owner/*
							 * null for default
							 */, final boolean underGrave, long hiddenTime/*
																		 * default
																		 * 3
																		 * minutes
																		 */,
			boolean invisible, boolean intoGold, final int publicTime) {
		if (intoGold) {
			if (!ItemConstants.isTradeable(item)) {
				int price = item.getDefinitions().getValue();
				if (price <= 0)
					return;
				item.setId(995);
				item.setAmount(price);
			}
		}
		final FloorItem floorItem = new FloorItem(item, tile, owner,
				owner == null ? false : underGrave, invisible);
		final Region region = getRegion(tile.getRegionId());
		region.forceGetFloorItems().add(floorItem);
		if (invisible && hiddenTime != -1) {
			if (owner != null)
				owner.getPackets().sendGroundItem(floorItem);
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						if (!region.forceGetFloorItems().contains(floorItem))
							return;
						int regionId = tile.getRegionId();
						if (underGrave || !ItemConstants.isTradeable(floorItem)
								|| item.getName().contains("Dr nabanik")) {
							region.forceGetFloorItems().remove(floorItem);
							if (owner != null) {
								if (owner.getMapRegionsIds().contains(regionId)
										&& owner.getZ() == tile.getZ())
									owner.getPackets().sendRemoveGroundItem(
											floorItem);
							}
							return;
						}

						floorItem.setInvisible(false);
						for (Player player : players) {
							if (player == null
									|| player == owner
									|| !player.isActive()
									|| player.hasFinished()
									|| player.getZ() != tile.getZ()
									|| !player.getMapRegionsIds().contains(
											regionId))
								continue;
							player.getPackets().sendGroundItem(floorItem);
						}
						removeGroundItem(floorItem, publicTime);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, hiddenTime, TimeUnit.SECONDS);
			return;
		}
		int regionId = tile.getRegionId();
		for (Player player : players) {
			if (player == null || !player.isActive() || player.hasFinished()
					|| player.getZ() != tile.getZ()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
		removeGroundItem(floorItem, publicTime);
	}

	public static final void addLobbyPlayer(Player player) {
		synchronized (LOCK) {
			lobbyPlayers.add(player);
			AntiFlood.add(player.getSession().getIP());
		}
	}

	private static void addLoyaltyTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for (Player player : getPlayers()) {
					if (player == null) {
						continue;
					}
					if (player.checkTotalLevel(500) > 500) {
						if (player.isVipRank()) {
							player.setLoyaltyPoints(player.getLoyaltyPoints() + 2);
						} else {
							player.setLoyaltyPoints(player.getLoyaltyPoints() + 1);
						}
					}
				}
			}
		}, 0, 1, TimeUnit.HOURS);
	}

	public static final void addNPC(NPC npc) {
		npcs.add(npc);
	}

	private static void addOwnedObjectsTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					OwnedObjectManager.processAll();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public static final void addPlayer(Player player) {
		synchronized (LOCK) {
			players.add(player);
			if (Engine.containsLobbyPlayer(player.getUsername())) {
				Engine.removeLobbyPlayer(player);
				AntiFlood.remove(player.getSession().getIP());
			}
			AntiFlood.add(player.getSession().getIP());
		}
	}

	private static final void addRestoreHitPointsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead()
								|| !player.isRunning())
							continue;
						player.restoreHitPoints();
					}
					for (NPC npc : npcs) {
						if (npc == null || npc.isDead() || npc.hasFinished())
							continue;
						npc.restoreHitPoints();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 6000);
	}

	private static final void addRestoreRunEnergyTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null
								|| player.isDead()
								|| !player.isRunning()
								|| (checkAgility && player.getSkills()
										.getLevel(Skills.AGILITY) < 70))
							continue;
						player.restoreRunEnergy();
					}
					checkAgility = !checkAgility;
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 1000);
	}

	private static void addRestoreShopItemsTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ShopsHandler.restoreShops();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	private static final void addRestoreSkillsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || !player.isRunning())
							continue;
						int ammountTimes = player.getPrayer().usingPrayer(0, 8) ? 2
								: 1;
						if (player.isResting())
							ammountTimes += 1;
						boolean berserker = player.getPrayer()
								.usingPrayer(1, 5);
						for (int skill = 0; skill < 25; skill++) {
							if (skill == Skills.SUMMONING)
								continue;
							for (int time = 0; time < ammountTimes; time++) {
								int currentLevel = player.getSkills().getLevel(
										skill);
								int normalLevel = player.getSkills()
										.getLevelForXp(skill);
								if (currentLevel > normalLevel) {
									if (skill == Skills.ATTACK
											|| skill == Skills.STRENGTH
											|| skill == Skills.DEFENCE
											|| skill == Skills.RANGE
											|| skill == Skills.MAGIC) {
										if (berserker
												&& Utilities.getRandom(100) <= 15)
											continue;
									}
									player.getSkills().set(skill,
											currentLevel - 1);
								} else if (currentLevel < normalLevel)
									player.getSkills().set(skill,
											currentLevel + 1);
								else
									break;
							}
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30000);

	}

	/*
	 * public static final void updatePlayers() { for (Player player :
	 * World.getPlayers()) { if (player == null || !player.isRunning())
	 * continue; player.getPackets().sendIComponentText(751, 16,
	 * "Players Online: " + World.getPlayers().size()); } }
	 */

	private static final void addRestoreSpecialAttackTask() {

		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead()
								|| !player.isRunning())
							continue;
						player.getCombatDefinitions().restoreSpecialAttack();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30000);
	}

	private static final void addSummoningEffectTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.getFamiliar() == null
								|| player.isDead() || !player.hasFinished())
							continue;
						if (player.getFamiliar().getOriginalId() == 6814) {
							player.heal(20);
							player.setNextGraphics(new Graphics(1507));
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}

	public static void annouceEventMessageTask() {
		EngineTaskManager.schedule(new EngineTask() {
			int timesToAnnouceCommunityEvent = 0;

			@Override
			public void run() {
				if (timesToAnnouceCommunityEvent == 4
						|| !GameConstants.eventActive) {
					stop();
				}
				timesToAnnouceCommunityEvent++;
				if (GameConstants.eventActive)
					Engine.sendWorldMessage(
							"<img=7><col=FF0033>Community:</col><col=7D1616> An "
									+ GameConstants.eventType
									+ " event is being hosted, type ::event to teleport to the location.",
							false);
			}
		}, 0, 120);
	}

	/*
	 * checks clip
	 */
	public static boolean canMoveNPC(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (getMask(plane, tileX, tileY) != 0)
					return false;
		return true;
	}

	private static void checkControllersAtMove(Player player) {
		if (!(player.getControllerManager().getController() instanceof RequestController)
				&& RequestController.inWarRequest(player))
			player.getControllerManager().startController("clan_wars_request");
		else if (player.getRegionId() == 13363)
			player.getControllerManager().startController("DuelController");
		else if (player.getRegionId() == 13626 || player.getRegionId() == 13625)
			player.getControllerManager().startController("DungeoneeringLobby");
		else if (FfaZone.inArea(player))
			player.getControllerManager().startController("clan_wars_ffa");
		else if (!player.isApeAtoll()) {
			if (player.getEquipment().getWeaponId() == 4024) {
				ButtonHandler.sendRemove2(player, 3);
			}
		}
	}

	public static void checkplayerAFK() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for (Player player : getPlayers()) {
					if (player == null) {
						continue;
					}
					if (player.getLastWalked() == Utilities.currentTimeMillis()) {
						System.out.println("AFK detected for"
								+ player.getUsername());
					}
				}
			}
		}, 0, 1, TimeUnit.MINUTES);
	}

	public static final boolean checkProjectileStep(int plane, int x, int y,
			int dir, int size) {
		int xOffset = Utilities.DIRECTION_DELTA_X[dir];
		int yOffset = Utilities.DIRECTION_DELTA_Y[dir];
		/*
		 * int rotation = getRotation(plane,x+xOffset,y+yOffset); if(rotation !=
		 * 0) { dir += rotation; if(dir >= Utils.DIRECTION_DELTA_X.length) dir =
		 * dir - (Utils.DIRECTION_DELTA_X.length-1); xOffset =
		 * Utils.DIRECTION_DELTA_X[dir]; yOffset = Utils.DIRECTION_DELTA_Y[dir];
		 * }
		 */
		if (size == 1) {
			int mask = getClipedOnlyMask(plane, x
					+ Utilities.DIRECTION_DELTA_X[dir], y
					+ Utilities.DIRECTION_DELTA_Y[dir]);
			if (xOffset == -1 && yOffset == 0)
				return (mask & 0x42240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & 0x60240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & 0x40a40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & 0x48240000) == 0;
			if (xOffset == -1 && yOffset == -1) {
				return (mask & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == 1 && yOffset == -1) {
				return (mask & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == -1 && yOffset == 1) {
				return (mask & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
			if (xOffset == 1 && yOffset == 1) {
				return (mask & 0x78240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x + 2, y) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 1) & 0x78240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y - 1) & 0x60e40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getClipedOnlyMask(plane, x, y + 2) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 2) & 0x78240000) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x63e40000) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x + 1, y - 1) & 0x63e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y - 1) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y) & 0x78e40000) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 2) & 0x7e240000) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x + 1, y + 2) & 0x7e240000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 2) & 0x78240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 1) & 0x78e40000) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x - 1, -1 + (y + size)) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x + size, y) & 0x60e40000) != 0
						|| (getClipedOnlyMask(plane, x + size, y - (-size + 1)) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x + size - 1, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x, y + size) & 0x4e240000) != 0
						|| (getClipedOnlyMask(plane, x + (size - 1), y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + (-1 + sizeOffset)) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, sizeOffset - 1 + x,
									y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x + size, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, sizeOffset
							+ (-1 + y)) & 0x78e40000) != 0
							|| (getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x - 1, y + size) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, -1 + (x + sizeOffset),
									y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x + size, y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0
							|| (getClipedOnlyMask(plane, x + size, y
									+ sizeOffset) & 0x78e40000) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int dir,
			int size) {
		int xOffset = Utilities.DIRECTION_DELTA_X[dir];
		int yOffset = Utilities.DIRECTION_DELTA_Y[dir];
		int rotation = getRotation(plane, x + xOffset, y + yOffset);
		if (rotation != 0) {
			for (int rotate = 0; rotate < (4 - rotation); rotate++) {
				int fakeChunckX = xOffset;
				int fakeChunckY = yOffset;
				xOffset = fakeChunckY;
				yOffset = 0 - fakeChunckX;
			}
		}

		if (size == 1) {
			int mask = getMask(plane, x + Utilities.DIRECTION_DELTA_X[dir], y
					+ Utilities.DIRECTION_DELTA_Y[dir]);
			if (xOffset == -1 && yOffset == 0)
				return (mask & 0x42240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & 0x60240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & 0x40a40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & 0x48240000) == 0;
			if (xOffset == -1 && yOffset == -1) {
				return (mask & 0x43a40000) == 0
						&& (getMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == 1 && yOffset == -1) {
				return (mask & 0x60e40000) == 0
						&& (getMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == -1 && yOffset == 1) {
				return (mask & 0x4e240000) == 0
						&& (getMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getMask(plane, x, y + 1) & 0x48240000) == 0;
			}
			if (xOffset == 1 && yOffset == 1) {
				return (mask & 0x78240000) == 0
						&& (getMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getMask(plane, x, y + 1) & 0x48240000) == 0;
			}
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getMask(plane, x - 1, y) & 0x43a40000) == 0
						&& (getMask(plane, x - 1, y + 1) & 0x4e240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getMask(plane, x + 2, y) & 0x60e40000) == 0
						&& (getMask(plane, x + 2, y + 1) & 0x78240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getMask(plane, x, y - 1) & 0x43a40000) == 0
						&& (getMask(plane, x + 1, y - 1) & 0x60e40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getMask(plane, x, y + 2) & 0x4e240000) == 0
						&& (getMask(plane, x + 1, y + 2) & 0x78240000) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getMask(plane, x - 1, y) & 0x4fa40000) == 0
						&& (getMask(plane, x - 1, y - 1) & 0x43a40000) == 0
						&& (getMask(plane, x, y - 1) & 0x63e40000) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getMask(plane, x + 1, y - 1) & 0x63e40000) == 0
						&& (getMask(plane, x + 2, y - 1) & 0x60e40000) == 0
						&& (getMask(plane, x + 2, y) & 0x78e40000) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getMask(plane, x - 1, y + 1) & 0x4fa40000) == 0
						&& (getMask(plane, x - 1, y + 1) & 0x4e240000) == 0
						&& (getMask(plane, x, y + 2) & 0x7e240000) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getMask(plane, x + 1, y + 2) & 0x7e240000) == 0
						&& (getMask(plane, x + 2, y + 2) & 0x78240000) == 0
						&& (getMask(plane, x + 1, y + 1) & 0x78e40000) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getMask(plane, x - 1, y) & 0x43a40000) != 0
						|| (getMask(plane, x - 1, -1 + (y + size)) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getMask(plane, x + size, y) & 0x60e40000) != 0
						|| (getMask(plane, x + size, y - (-size + 1)) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getMask(plane, x, y - 1) & 0x43a40000) != 0
						|| (getMask(plane, x + size - 1, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getMask(plane, x, y + size) & 0x4e240000) != 0
						|| (getMask(plane, x + (size - 1), y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getMask(plane, x - 1, y - 1) & 0x43a40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + (-1 + sizeOffset)) & 0x4fa40000) != 0
							|| (getMask(plane, sizeOffset - 1 + x, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getMask(plane, x + size, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + size, sizeOffset + (-1 + y)) & 0x78e40000) != 0
							|| (getMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getMask(plane, x - 1, y + size) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0
							|| (getMask(plane, -1 + (x + sizeOffset), y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getMask(plane, x + size, y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0
							|| (getMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			}
		}
		return true;
	}

	public static int checkWildernessPlayers() {
		int pkers = 0;
		for (Player pker : getPlayers()) {
			if (pker == null) {
				continue;
			}
			if (!Wilderness.isAtWild(pker)) {
				continue;
			} else {
				pkers++;
			}
		}
		return pkers;
	}

	public static final boolean containsLobbyPlayer(String username) {
		synchronized (lobbyPlayers) {
			for (Player p2 : lobbyPlayers) {
				if (p2 == null) {
					continue;
				}
				if (p2.getUsername().equalsIgnoreCase(username)) {
					return true;
				}
			}
			return false;
		}
	}

	public static final boolean containsPlayer(String username) {
		synchronized (getPlayers()) {
			for (Player p2 : players) {
				if (p2 == null) {
					continue;
				}
				if (p2.getUsername().equals(username)) {
					return true;
				}
			}
			return false;
		}
	}

	public static void deleteObject(Tile tile) {
		// prevents memory leak lel
		if (!restrictedTiles.contains(tile)) {
			restrictedTiles.add(tile);
		}
	}

	public static void destroySpawnedObject(GameObject object) {
		int regionId = object.getRegionId();
		int baseLocalX = object.getX() - ((regionId >> 8) * 64);
		int baseLocalY = object.getY() - ((regionId & 0xff) * 64);
		Engine.getRegion(regionId).removeObject(object);
		Engine.getRegion(regionId).removeMapObject(object, baseLocalX,
				baseLocalY);
		for (Player p2 : Engine.getPlayers()) {
			if (p2 == null || !p2.isActive() || p2.hasFinished()
					|| !p2.getMapRegionsIds().contains(regionId))
				continue;
			p2.getPackets().sendDestroyObject(object);
		}
	}

	public static void destroySpawnedObject(GameObject object, boolean clip) {
		int regionId = object.getRegionId();
		int baseLocalX = object.getX() - ((regionId >> 8) * 64);
		int baseLocalY = object.getY() - ((regionId & 0xff) * 64);
		GameObject realMapObject = getRegion(regionId).getRealObject(object);

		Engine.getRegion(regionId).removeObject(object);
		if (clip)
			Engine.getRegion(regionId).removeMapObject(object, baseLocalX,
					baseLocalY);
		for (Player p2 : Engine.getPlayers()) {
			if (p2 == null || !p2.isActive() || p2.hasFinished()
					|| !p2.getMapRegionsIds().contains(regionId))
				continue;
			if (realMapObject != null)
				p2.getPackets().sendSpawnedObject(realMapObject);
			else
				p2.getPackets().sendDestroyObject(object);
		}
	}

	private static int getClipedOnlyMask(int plane, int x, int y) {
		Tile tile = new Tile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return -1;
		int baseLocalX = x - ((regionId >> 8) * 64);
		int baseLocalY = y - ((regionId & 0xff) * 64);
		return region.getMaskClipedOnly(tile.getZ(), baseLocalX, baseLocalY);
	}

	public static int getIdFromName(String playerName) {
		for (Player p : players) {
			if (p == null) {
				continue;
			}
			if (p.getUsername().equalsIgnoreCase(
					Utilities.formatPlayerNameForProtocol(playerName))) {
				return p.getIndex();
			}
		}
		return 0;
	}

	public static final Player getLobbyPlayerByDisplayName(String username) {
		String formatedUsername = Utilities
				.formatPlayerNameForDisplay(username);
		for (Player player : getLobbyPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.getUsername().equalsIgnoreCase(formatedUsername)
					|| player.getDisplayName().equalsIgnoreCase(
							formatedUsername)) {
				return player;
			}
		}
		return null;
	}

	public static final EntityList<Player> getLobbyPlayers() {
		return lobbyPlayers;
	}

	public static final EntityList<Player> getPlayers() {
		return players;
	}

	public static int getMask(int plane, int x, int y) {
		Tile tile = new Tile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return -1;
		int baseLocalX = x - ((regionId >> 8) * 64);
		int baseLocalY = y - ((regionId & 0xff) * 64);
		return region.getMask(tile.getZ(), baseLocalX, baseLocalY);
	}

	public static final EntityList<NPC> getNPCs() {
		return npcs;
	}

	public static final GameObject getObject(Tile tile) {
		int regionId = tile.getRegionId();
		int baseLocalX = tile.getX() - ((regionId >> 8) * 64);
		int baseLocalY = tile.getY() - ((regionId & 0xff) * 64);
		return getRegion(regionId).getObject(tile.getZ(), baseLocalX,
				baseLocalY);
	}

	public static final GameObject getObject(Tile tile, int type) {
		int regionId = tile.getRegionId();
		int baseLocalX = tile.getX() - ((regionId >> 8) * 64);
		int baseLocalY = tile.getY() - ((regionId & 0xff) * 64);
		return getRegion(regionId).getObject(tile.getZ(), baseLocalX,
				baseLocalY, type);
	}

	public static Player getPlayer(String username) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equals(username))
				return player;
		}
		return null;
	}

	public static final Player getPlayerByDisplayName(String username) {
		String formatedUsername = Utilities
				.formatPlayerNameForDisplay(username);
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(formatedUsername)
					|| player.getDisplayName().equalsIgnoreCase(
							formatedUsername))
				return player;
		}
		return null;
	}

	public static final Region getRegion(int id) {
		return getRegion(id, false);
	}

	public static final Region getRegion(int id, boolean load) {
		// synchronized (lock) {
		Region region = regions.get(id);
		if (region == null) {
			region = new Region(id);
			regions.put(id, region);
		}
		if (load)
			region.checkLoadMap();
		return region;
		// }
	}

	public static final Map<Integer, Region> getRegions() {
		// synchronized (lock) {
		return regions;
		// }
	}

	public static int getRotation(int plane, int x, int y) {
		Tile tile = new Tile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return 0;
		int baseLocalX = x - ((regionId >> 8) * 64);
		int baseLocalY = y - ((regionId & 0xff) * 64);
		return region.getRotation(tile.getZ(), baseLocalX, baseLocalY);
	}

	public static final void init() {
		addRestoreRunEnergyTask();
		addDrainPrayerTask();
		addServerMessageEvent();
		addRestoreHitPointsTask();
		addRestoreSkillsTask();
		addRestoreSpecialAttackTask();
		addSummoningEffectTask();
		addOwnedObjectsTask();
		LivingRockCavern.init();
		addRestoreShopItemsTask();
		addLoyaltyTask();
		growPatchesTask();
		addAutoRestock();
		WarriorsGuild.init();
		WildyWyrmManager.INSTANCE.init();
		JadinkoLair.init();
		soulWars = new SoulWarsManager();
		soulWars.start();
	}

	public static final boolean isMultiArea(Tile tile) {
		int destX = tile.getX();
		int destY = tile.getY();
		return (destX >= 3462 && destX <= 3511 && destY >= 9481
				&& destY <= 9521 && tile.getZ() == 0) // kalphite
				// queen
				// lair
				|| (destX >= 4540 && destX <= 4799 && destY >= 5052
						&& destY <= 5183 && tile.getZ() == 0) // thzaar
				// city
				|| (destX >= 1721 && destX <= 1791 && destY >= 5123 && destY <= 5249) // mole
				// || (destX >= 3029 && destX <= 3374 && destY >= 3759 && destY
				// <= 3903)//wild
				|| (destX >= 2250 && destX <= 2280 && destY >= 4670 && destY <= 4720)
				|| (destX >= 2987 && destX <= 3006 && destY >= 3912 && destY <= 3937)
				|| (destX >= 2895 && destX <= 2937 && destY >= 4430 && destY <= 4472)
				|| (destX >= 2245 && destX <= 2295 && destY >= 4675 && destY <= 4720)
				// || (destX >= 3216 && destX <= 10013 && destY >= 3250 && destY
				// <= 9991)
				// || (destX >= 3200 && destX <= 9991 && destY >= 3245 && destY
				// <= 10013)
				|| (destX >= 2450 && destX <= 3520 && destY >= 9450 && destY <= 9550)
				|| (destX >= 3006 && destX <= 3071 && destY >= 3602 && destY <= 3710)
				|| (destX >= 3134 && destX <= 3192 && destY >= 3519 && destY <= 3646)
				|| (destX >= 2815 && destX <= 2966 && destY >= 5240 && destY <= 5375)// wild
				|| (destX >= 2840 && destX <= 2950 && destY >= 5190 && destY <= 5230) // godwars
				|| (destX >= 3547 && destX <= 3555 && destY >= 9690 && destY <= 9699)
				|| (destX >= 3136 && destX <= 3327 && destY >= 3519 && destY <= 3607) // WILDY
				|| (destX >= 3190 && destX <= 3327 && destY >= 3648 && destY <= 3839)
				|| (destX >= 3200 && destX <= 3390 && destY >= 3840 && destY <= 3967)
				|| (destX >= 2992 && destX <= 3007 && destY >= 3912 && destY <= 3967)
				|| (destX >= 2946 && destX <= 2959 && destY >= 3816 && destY <= 3831)
				|| (destX >= 3008 && destX <= 3199 && destY >= 3856 && destY <= 3903)
				|| (destX >= 3008 && destX <= 3071 && destY >= 3600 && destY <= 3711)
				|| (destX >= 3072 && destX <= 3327 && destY >= 3608 && destY <= 3647)
				|| (destX >= 2624 && destX <= 2690 && destY >= 2550 && destY <= 2619)
				|| (destX >= 2371 && destX <= 2422 && destY >= 5062 && destY <= 5117)
				|| (destX >= 2896 && destX <= 2927 && destY >= 3595 && destY <= 3630)
				|| (destX >= 2892 && destX <= 2932 && destY >= 4435 && destY <= 4464)
				|| (destX >= 2256 && destX <= 2287 && destY >= 4680 && destY <= 4711)
				|| (destX >= 2863 && destX <= 2878 && destY >= 5350 && destY <= 5372)
				|| KingBlackDragon.atKBD(tile) // King
				// Black
				// Dragon
				// lair
				|| TormentedDemon.atTD(tile) // Tormented demon's area
				|| Bork.atBork(tile) // Bork's area
				|| (destX >= 2970 && destX <= 3000 && destY >= 4365 && destY <= 4400)// corp
				|| (destX >= 3195 && destX <= 3327 && destY >= 3520
						&& destY <= 3970 || (destX >= 2376 && 5127 >= destY
						&& destX <= 2422 && 5168 <= destY))
				|| (destX >= 2374 && destY >= 5129 && destX <= 2424 && destY <= 5168) // pits
				|| (destX >= 2622 && destY >= 5696 && destX <= 2573 && destY <= 5752) // torms
				|| (destX >= 2368 && destY >= 3072 && destX <= 2431 && destY <= 3135) // castlewars
				|| (destX >= 2365 && destY >= 9470 && destX <= 2436 && destY <= 9532) // castlewars
				|| (destX >= 2948 && destY >= 5537 && destX <= 3071 && destY <= 5631) // Risk
				// /ffa.
				|| (destX >= 2756 && destY >= 5537 && destX <= 2879 && destY <= 5631) // Safe
				|| (destX >= 4160 && destY >= 5695 && destX <= 4223 && destY <= 5760) // Glacors
				// //
				// ffa

				|| (tile.getX() >= 3011 && tile.getX() <= 3132
						&& tile.getY() >= 10052 && tile.getY() <= 10175 && (tile
						.getY() >= 10066 || tile.getX() >= 3094)); // fortihrnydungeon
	}

	/*
	 * checks clip
	 */
	public static boolean isNotCliped(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if ((getMask(plane, tileX, tileY) & 2097152) != 0)
					return false;
		return true;
	}

	public static final boolean isPvpArea(Tile tile) {
		return Wilderness.isAtWild(tile);
	}

	public static final boolean isSpawnedObject(GameObject object) {
		final int regionId = object.getRegionId();
		GameObject spawnedObject = getRegion(regionId).getSpawnedObject(object);
		if (spawnedObject != null && object.getId() == spawnedObject.getId())
			return true;
		return false;
	}

	private static final void removeGroundItem(final FloorItem floorItem,
			long publicTime) {
		if (publicTime < 0) {
			return;
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					int regionId = floorItem.getTile().getRegionId();
					Region region = getRegion(regionId);
					if (!region.forceGetFloorItems().contains(floorItem))
						return;
					region.forceGetFloorItems().remove(floorItem);
					for (Player player : Engine.getPlayers()) {
						if (player == null
								|| !player.isActive()
								|| player.hasFinished()
								|| player.getZ() != floorItem.getTile().getZ()
								|| !player.getMapRegionsIds()
										.contains(regionId))
							continue;
						player.getPackets().sendRemoveGroundItem(floorItem);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, publicTime, TimeUnit.SECONDS);
	}

	public static final boolean removeGroundItem(Player player,
			FloorItem floorItem) {
		return removeGroundItem(player, floorItem, true);
	}

	public static final boolean removeGroundItem(Player player,
			FloorItem floorItem, boolean add) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		if (!region.forceGetFloorItems().contains(floorItem))
			return false;
		if (player.getInventory().getFreeSlots() == 0)
			return false;
		int amount = player.getInventory().getNumberOf(floorItem.getId());
		if (floorItem.getAmount() + amount < 0) {
			player.getPackets().sendGameMessage(
					"Not enough inventory space to pick that item up.");
			return false;
		}
		if (add)
			if (!player.getInventory().addItem(floorItem.getId(),
					floorItem.getAmount())) {
				return false;
			}
		region.forceGetFloorItems().remove(floorItem);
		if (floorItem.isInvisible() || floorItem.isGrave()) {
			player.getPackets().sendRemoveGroundItem(floorItem);
			return true;
		} else {
			for (Player p2 : Engine.getPlayers()) {
				if (p2 == null || !p2.isActive() || p2.hasFinished()
						|| p2.getZ() != floorItem.getTile().getZ()
						|| !p2.getMapRegionsIds().contains(regionId))
					continue;
				p2.getPackets().sendRemoveGroundItem(floorItem);
			}
			return true;
		}
	}

	public static void removeLobbyPlayer(Player player) {
		for (Player p : lobbyPlayers) {
			if (p.getUsername().equalsIgnoreCase(player.getUsername())) {
				if (player.getCurrentFriendChat() != null) {
					player.getCurrentFriendChat().leaveChat(player, true);
				}
				lobbyPlayers.remove(p);
			}
		}
		if (player != null) {
			SerializableFilesManager.savePlayer(player);
		}
		AntiFlood.remove(player.getSession().getIP());
	}

	public static final void removeNPC(NPC npc) {
		npcs.remove(npc);
	}

	public static final void removeObject(GameObject object, boolean clip) {
		int regionId = object.getRegionId();
		getRegion(regionId).addRemovedObject(object);
		if (clip) {
			int baseLocalX = object.getX() - ((regionId >> 8) * 64);
			int baseLocalY = object.getY() - ((regionId & 0xff) * 64);
			getRegion(regionId).removeMapObject(object, baseLocalX, baseLocalY);
		}
		synchronized (players) {
			for (Player p2 : players) {
				if (p2 == null || !p2.isActive() || p2.hasFinished()
						|| !p2.getMapRegionsIds().contains(regionId))
					continue;
				p2.getPackets().sendDestroyObject(object);
			}
		}
	}

	public static void removePlayer(Player player) {
		for (Player p : players) {
			if (p.getUsername().equalsIgnoreCase(player.getUsername())) {
				players.remove(p);
			}
		}
		AntiFlood.remove(player.getSession().getIP());
	}

	public static final boolean removeTemporaryObject(final GameObject object,
			long time, final boolean clip) {
		final int regionId = object.getRegionId();
		final GameObject realObject = object == null ? null : new GameObject(
				object.getId(), object.getType(), object.getRotation(),
				object.getX(), object.getY(), object.getZ());
		removeObject(object, clip);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					getRegion(regionId).removeRemovedObject(object);
					if (clip) {
						int baseLocalX = object.getX() - ((regionId >> 8) * 64);
						int baseLocalY = object.getY()
								- ((regionId & 0xff) * 64);
						getRegion(regionId).addMapObject(realObject,
								baseLocalX, baseLocalY);
					}
					for (Player p2 : players) {
						if (p2 == null || !p2.isActive() || p2.hasFinished()
								|| !p2.getMapRegionsIds().contains(regionId))
							continue;
						p2.getPackets().sendSpawnedObject(realObject);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);

		return true;
	}

	public static final void safeShutdown(final boolean restart, int delay) {
		if (exiting_start != 0) {
			System.out
					.println("You cannot launch another restart if theres already one running.");
			return;
		}
		exiting_start = Utilities.currentTimeMillis();
		exiting_delay = delay;
		for (Player player : Engine.getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay);
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : Engine.getPlayers()) {
						if (player == null || !player.isActive())
							continue;
						player.realFinish();
					}
					IPBanL.save();
					IPMute.save();
					MACBan.save();
					PkRank.save();
					GameServer.shutdown();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, delay, TimeUnit.SECONDS);
	}

	public static final void sendGraphics(Entity creator, Graphics graphics,
			Tile tile) {
		if (creator == null) {
			for (Player player : Engine.getPlayers()) {
				if (player == null || !player.isActive()
						|| player.hasFinished() || !player.withinDistance(tile))
					continue;
				player.getPackets().sendGraphics(graphics, tile);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId)
						.getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || !player.isActive()
							|| player.hasFinished()
							|| !player.withinDistance(tile))
						continue;
					player.getPackets().sendGraphics(graphics, tile);
				}
			}
		}
	}

	public static final void sendObjectAnimation(Entity creator,
			GameObject object, Animation animation) {
		if (creator == null) {
			for (Player player : Engine.getPlayers()) {
				if (player == null || !player.isActive()
						|| player.hasFinished()
						|| !player.withinDistance(object))
					continue;
				player.getPackets().sendObjectAnimation(object, animation);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId)
						.getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || !player.isActive()
							|| player.hasFinished()
							|| !player.withinDistance(object))
						continue;
					player.getPackets().sendObjectAnimation(object, animation);
				}
			}
		}
	}

	public static final void sendObjectAnimation(GameObject object,
			Animation animation) {
		sendObjectAnimation(null, object, animation);
	}

	public static final void sendProjectile(Entity shooter, Entity receiver,
			int gfxId, int startHeight, int endHeight, int speed, int delay,
			int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null
						|| !player.isActive()
						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player
								.withinDistance(receiver)))
					continue;
				int size = shooter.getSize();
				player.getPackets().sendProjectile(
						receiver,
						new Tile(shooter.getCoordFaceX(size), shooter
								.getCoordFaceY(size), shooter.getZ()),
						receiver, gfxId, startHeight, endHeight, speed, delay,
						curve, startDistanceOffset, size);
			}
		}
	}

	public static final void sendProjectile(Entity shooter, Tile receiver,
			int gfxId, int startHeight, int endHeight, int speed, int delay,
			int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null
						|| !player.isActive()
						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player
								.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(null, shooter, receiver,
						gfxId, startHeight, endHeight, speed, delay, curve,
						startDistanceOffset, shooter.getSize());
			}
		}
	}

	public static final void sendProjectile(Entity shooter, Tile startTile,
			Tile receiver, int gfxId, int startHeight, int endHeight,
			int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null
						|| !player.isActive()
						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player
								.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(null, startTile, receiver,
						gfxId, startHeight, endHeight, speed, delay, curve,
						startDistanceOffset, 1);
			}
		}
	}

	public static final void sendProjectile(GameObject object, Tile startTile,
			Tile endTile, int gfxId, int startHeight, int endHeight, int speed,
			int delay, int curve, int startOffset) {
		for (Player pl : players) {
			if (pl == null || !pl.withinDistance(object, 20))
				continue;
			pl.getPackets()
					.sendProjectile(null, startTile, endTile, gfxId,
							startHeight, endHeight, speed, delay, curve,
							startOffset, 1);
		}
	}

	public static final void sendProjectile(Tile shooter, Entity receiver,
			int gfxId, int startHeight, int endHeight, int speed, int delay,
			int curve, int startDistanceOffset) {
		for (int regionId : receiver.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null
						|| !player.isActive()
						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player
								.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(null, shooter, receiver,
						gfxId, startHeight, endHeight, speed, delay, curve,
						startDistanceOffset, 1);
			}
		}
	}

	public static void sendWorldMessage(String message, boolean forStaff) {
		for (Player p : Engine.getPlayers()) {
			boolean staff = p.getRights() == 0 ? false
					: !p.isSupporter() ? false : true;
			if (p == null || !p.isRunning() || (forStaff && !staff)
					|| p.isHidingWorldMessages())
				continue;
			p.getPackets().sendGameMessage(message);
		}
	}

	public static void sendWorldYellMessage(String message, Player me) {
		for (Player player : Engine.getPlayers()) {
			if (player == null || !player.isRunning()) {
				continue;
			}
			if (player != me && player.hasDisabledYell()) {
				continue;
			}
			player.getPackets().sendGameMessage(message);
		}
	}

	public static void setMask(int plane, int x, int y, int mask) {
		Tile tile = new Tile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return;
		int baseLocalX = x - ((regionId >> 8) * 64);
		int baseLocalY = y - ((regionId & 0xff) * 64);
		region.setMask(tile.getZ(), baseLocalX, baseLocalY, mask);
	}

	public static final NPC spawnNPC(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
				false);
	}

	public static final NPC spawnNPC(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		NPC n = null;
		HunterNPC hunterNPCs = HunterNPC.forId(id);
		if (hunterNPCs != null) {
			if (id == hunterNPCs.getNpcId())
				n = new ItemHunterNPC(id, tile, mapAreaNameHash,
						canBeAttackFromOutOfArea, spawned);
		} else if (id >= 5533 && id <= 5558)
			n = new Elemental(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 7134)
			n = new Bork(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 14301)
			n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 9462 && id <= 9467)
			n = new Strykewyrm(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 9441)
			n = new FlameVortex(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 8832 && id <= 8834)
			n = new LivingRock(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 13465 && id <= 13481)
			n = new Revenant(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 1158 || id == 1160)
			n = new KalphiteQueen(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 8528 && id <= 8532)
			n = new Nomad(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 8133)
			n = new CorporealBeast(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 13456 || id == 13457 || id == 13458 || id == 13459) {
			n = new GodwarsZarosFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		} else if (id == 50 || id == 2642) {
			n = new KingBlackDragon(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 1266 || id == 1268 || id == 2453 || id == 2886) {
			n = new RockCrabs(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
        } else if (id == 1926 || id == 1931) {
            n = new BanditCampBandits(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        } else if (id >= 6026 && id <= 6045) {
            n = new Werewolf(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		} else if (id == 6261 || id == 6263 || id == 6265) {
			n = GodWarsBosses.graardorMinions[(id - 6261) / 2] = new GodWarMinion(
					id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		} else if (id == 6260) {
			n = new GeneralGraardor(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 6222) {
			n = new KreeArra(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 6203) {
			n = new KrilTstsaroth(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 6223 || id == 6225 || id == 6227) {
			n = GodWarsBosses.armadylMinions[(id - 6223) / 2] = new GodWarMinion(
					id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		} else if (id == 6204 || id == 6206 || id == 6208) {
			n = GodWarsBosses.zamorakMinions[(id - 6204) / 2] = new GodWarMinion(
					id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		} else if (id == 6248 || id == 6250 || id == 6252) {
			n = GodWarsBosses.commanderMinions[(id - 6248) / 2] = new GodWarMinion(
					id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		} else if (id == 6247) {
			n = new CommanderZilyana(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6210 && id <= 6221) {
			n = new GodwarsZamorakFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6254 && id <= 6259) {
			n = new GodwarsSaradominFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6268 && id <= 6283) {
			n = new GodwarsBandosFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id >= 6228 && id <= 6246) {
			n = new GodwarsArmadylFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 13447) {
			n = ZarosGodwars.nex = new Nex(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 13451) {
			n = ZarosGodwars.fumus = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 13452) {
			n = ZarosGodwars.umbra = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 13453) {
			n = ZarosGodwars.cruor = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 13454) {
			n = ZarosGodwars.glacies = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		} else if (id == 3200) {
			n = new ChaosElemental(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
        } else if (id == 1615) {
            n = new AbyssalDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		} else if (id == 14256)
			n = new Lucien(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 1610)
		    n = new ConditionalDeath(4162, "The gargoyle breaks into peices as you slam the hammer onto its head.", false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 14849)
		    n = new ConditionalDeath(23035, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1627 || id == 1628 || id == 1629 || id == 1630)
		    n = new ConditionalDeath(4158, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 2803 && id <= 2808)
		    n = new ConditionalDeath(6696, null, true, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1631 || id == 1632)
		    n = new ConditionalDeath(4161, "The rockslug shrivels and dies.", true, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 3344 || id == 3345 || id == 3346 || id == 3347)
		    n = new MutatedZygomites(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 13820 || id == 13821 || id == 13822)
		    n = new Jadinko(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1609 || id == 1610)
		    n = new Kurask(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 3153)
		    n = new HarpieBug(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 2058)
		    n = new HoleInTheWall(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 8335)
			n = new MercenaryMage(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 8349 || id == 8450 || id == 8451)
			n = new TormentedDemon(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 15149)
			n = new MasterOfFear(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		return n;
	}

	public static final void spawnObject(GameObject object, boolean clip) {
		int regionId = object.getRegionId();
		getRegion(regionId).addObject(object);
		if (clip) {
			int baseLocalX = object.getX() - ((regionId >> 8) * 64);
			int baseLocalY = object.getY() - ((regionId & 0xff) * 64);
			getRegion(regionId).addMapObject(object, baseLocalX, baseLocalY);
		}
		synchronized (players) {
			for (Player p2 : players) {
				if (p2 == null || !p2.isActive() || p2.hasFinished()
						|| !p2.getMapRegionsIds().contains(regionId))
					continue;
				p2.getPackets().sendSpawnedObject(object);
			}
		}
	}

	public static final void turnPublic(FloorItem floorItem, int publicTime) {
		if (!floorItem.isInvisible())
			return;
		int regionId = floorItem.getTile().getRegionId();
		final Region region = Engine.getRegion(regionId);
		if (!region.forceGetFloorItems().contains(floorItem))
			return;
		Player realOwner = floorItem.getOwner() != null ? Engine
				.getPlayer(floorItem.getOwner().getUsername()) : null;
		if (!ItemConstants.isTradeable(floorItem)) {
			region.forceGetFloorItems().remove(floorItem);
			if (realOwner != null) {
				if (realOwner.getMapRegionsIds().contains(regionId)
						&& realOwner.getZ() == floorItem.getTile().getZ()) {
					realOwner.getPackets().sendRemoveGroundItem(floorItem);
				}
			}
			return;
		}
		floorItem.setInvisible(false);
		for (Player player : Engine.players) {
			if (player == null || player == realOwner || player.hasFinished()
					|| player.getZ() != floorItem.getTile().getZ()
					|| !player.getMapRegionsIds().contains(regionId)) {
				continue;
			}
			player.getPackets().sendGroundItem(floorItem);
		}
		// disapears after this time
		if (publicTime != -1) {
			Engine.removeGroundItem(floorItem, publicTime);
		}
	}

	public static final void spawnTempGroundObject(final GameObject object,
			final int replaceId, long time) {
		final int regionId = object.getRegionId();
		GameObject realMapObject = getRegion(regionId).getRealObject(object);
		final GameObject realObject = realMapObject == null ? null
				: new GameObject(realMapObject.getId(),
						realMapObject.getType(), realMapObject.getRotation(),
						object.getX(), object.getY(), object.getZ());
		spawnObject(object, false);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (getRegion(regionId).getObject(
							object.getId(),
							new Tile(object.getX(), object.getY(), object
									.getPlane())) != null) {
						getRegion(regionId).removeObject(object);
						addGroundItem(new Item(replaceId), object, null, false,
								180, false);
						for (Player p2 : players) {
							if (p2 == null
									|| !p2.isActive()
									|| p2.hasFinished()
									|| p2.getZ() != object.getZ()
									|| !p2.getMapRegionsIds()
											.contains(regionId))
								continue;
							if (realObject != null)
								p2.getPackets().sendSpawnedObject(realObject);
							else
								p2.getPackets().sendDestroyObject(object);
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final void spawnTempGroundObject(final GameObject object,
			long time) {
		final int regionId = object.getRegionId();
		GameObject realMapObject = getRegion(regionId).getRealObject(object);
		final GameObject realObject = realMapObject == null ? null
				: new GameObject(realMapObject.getId(),
						realMapObject.getType(), realMapObject.getRotation(),
						object.getX(), object.getY(), object.getZ());
		spawnObject(object, false);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					getRegion(regionId).removeObject(object);
					for (Player p2 : players) {
						if (p2 == null || !p2.isActive() || p2.hasFinished()
								|| p2.getZ() != object.getZ()
								|| !p2.getMapRegionsIds().contains(regionId))
							continue;
						if (realObject != null)
							p2.getPackets().sendSpawnedObject(realObject);
						else
							p2.getPackets().sendDestroyObject(object);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	/*
	 * by default doesnt changeClipData
	 */
	public static final void spawnTemporaryObject(final GameObject object,
			long time) {
		spawnTemporaryObject(object, time, true);
	}

	/*
	 * by default doesnt changeClipData
	 */
	public static final void spawnTemporaryReplaceObject(
			final GameObject object, long time, int replaceId) {
		spawnTempGroundObject(object, replaceId, time);
	}

	public static final void spawnTemporaryObject(final GameObject object,
			long time, final boolean clip) {
		final int regionId = object.getRegionId();
		GameObject realMapObject = getRegion(regionId).getRealObject(object);
		// remakes object, has to be done because on static region coords arent
		// same of real
		final GameObject realObject = realMapObject == null ? null
				: new GameObject(realMapObject.getId(),
						realMapObject.getType(), realMapObject.getRotation(),
						object.getX(), object.getY(), object.getZ());
		spawnObject(object, clip);
		final int baseLocalX = object.getX() - ((regionId >> 8) * 64);
		final int baseLocalY = object.getY() - ((regionId & 0xff) * 64);
		if (realObject != null && clip)
			getRegion(regionId).removeMapObject(realObject, baseLocalX,
					baseLocalY);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					getRegion(regionId).removeObject(object);
					if (clip) {
						getRegion(regionId).removeMapObject(object, baseLocalX,
								baseLocalY);
						if (realObject != null) {
							int baseLocalX = object.getX()
									- ((regionId >> 8) * 64);
							int baseLocalY = object.getY()
									- ((regionId & 0xff) * 64);
							getRegion(regionId).addMapObject(realObject,
									baseLocalX, baseLocalY);
						}
					}
					for (Player p2 : players) {
						if (p2 == null || !p2.isActive() || p2.hasFinished()
								|| !p2.getMapRegionsIds().contains(regionId))
							continue;
						if (realObject != null)
							p2.getPackets().sendSpawnedObject(realObject);
						else
							p2.getPackets().sendDestroyObject(object);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	/*
	 * check if the entity region changed because moved or teled then we update
	 * it
	 */
	public static final void updateEntityRegion(Entity entity) {
		if (entity.hasFinished()) {
			if (entity instanceof Player)
				getRegion(entity.getLastRegionId()).removePlayerIndex(
						entity.getIndex());
			else
				getRegion(entity.getLastRegionId()).removeNPCIndex(
						entity.getIndex());
			return;
		}
		int regionId = entity.getRegionId();
		if (entity.getLastRegionId() != regionId) { // map region entity at
			// changed
			if (entity instanceof Player) {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removePlayerIndex(
							entity.getIndex());
				Region region = getRegion(regionId);
				region.addPlayerIndex(entity.getIndex());
				Player player = (Player) entity;
				int musicId = region.getMusicId();
				if (musicId != -1)
					player.getMusicsManager().checkMusic(musicId);
				player.getControllerManager().moved();
				if (player.isActive())
					checkControllersAtMove(player);
			} else {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removeNPCIndex(
							entity.getIndex());
				getRegion(regionId).addNPCIndex(entity.getIndex());
			}
			entity.checkMultiArea();
			entity.setLastRegionId(regionId);
		} else {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.getControllerManager().moved();
				if (player.isActive())
					checkControllersAtMove(player);
			}
			entity.checkMultiArea();
		}
	}

	public static final void updateGroundItem(Item item, final Tile tile,
			final Player owner) {
		final FloorItem floorItem = Engine.getRegion(tile.getRegionId())
				.getGroundItem(item.getId(), tile, owner);
		if (floorItem == null) {
			addGroundItem(item, tile, owner, false, 360, true);
			return;
		}
		floorItem.setAmount(floorItem.getAmount() + item.getAmount());
		owner.getPackets().sendRemoveGroundItem(floorItem);
		owner.getPackets().sendGroundItem(floorItem);

	}

	private Engine() {

	}

	public static final Player get(long index) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.uniqueID() == index)
				return player;
		}
		return null;
	}

	public static List<String> getPlayersOnline() {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean isTileFree(int plane, int x, int y, int i) {
		Tile spawnTile = new Tile(new Tile(x + 1, y, plane));
		if (!Engine.canMoveNPC(spawnTile.getZ(), spawnTile.getX(),
				spawnTile.getY(), i)) {
			spawnTile = null;
			int[][] dirs = Utilities.getCoordOffsetsNear(i);
			for (int dir = 0; dir < dirs[0].length; dir++) {
				final Tile tile = new Tile(new Tile(x + dirs[0][dir], y
						+ dirs[1][dir], plane));
				if (Engine.canMoveNPC(tile.getZ(), tile.getX(), tile.getY(), i)) {
					spawnTile = tile;
					break;
				}
			}
		}
		if (spawnTile == null) {
			return false;
		}
		return true;
	}

	public static final void spawnObjectTemporary(final GameObject object,
			long time) {
		spawnObject(object, false);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!Engine.isSpawnedObject(object))
						return;
					removeObject(object, false);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static void spawnObject(GameObject object) {
		int regionId = object.getRegionId();
		getRegion(regionId).addObject(object);
		int baseLocalX = object.getX() - ((regionId >> 8) * 64);
		int baseLocalY = object.getY() - ((regionId & 0xff) * 64);
		getRegion(regionId).addMapObject(object, baseLocalX, baseLocalY);
		synchronized (players) {
			for (Player p2 : players) {
				if (p2 == null || !p2.isActive() || p2.hasFinished()
						|| !p2.getMapRegionsIds().contains(regionId))
					continue;
				p2.getPackets().sendSpawnedObject(object);
			}
		}
	}

	public static void removeObject(GameObject object) {
		int regionId = object.getRegionId();
		getRegion(regionId).addRemovedObject(object);
		int baseLocalX = object.getX() - ((regionId >> 8) * 64);
		int baseLocalY = object.getY() - ((regionId & 0xff) * 64);
		getRegion(regionId).removeMapObject(object, baseLocalX, baseLocalY);
		synchronized (players) {
			for (Player p2 : players) {
				if (p2 == null || !p2.isActive() || p2.hasFinished()
						|| !p2.getMapRegionsIds().contains(regionId))
					continue;
				p2.getPackets().sendDestroyObject(object);
			}
		}

	}

}
