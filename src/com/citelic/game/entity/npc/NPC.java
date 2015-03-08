package com.citelic.game.entity.npc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.citelic.GameConstants;
import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.cores.CoresManager;
import com.citelic.game.ForceTalk;
import com.citelic.game.SecondBar;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.combat.NPCCombat;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.npc.impl.corp.CorporealBeast;
import com.citelic.game.entity.npc.impl.dragons.KingBlackDragon;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.game.entity.npc.impl.fightcaves.TzTok_Jad;
import com.citelic.game.entity.npc.impl.fightkiln.HarAken;
import com.citelic.game.entity.npc.impl.glacor.UnstableGlacyte;
import com.citelic.game.entity.npc.impl.godwars.armadyl.KreeArra;
import com.citelic.game.entity.npc.impl.godwars.bandos.GeneralGraardor;
import com.citelic.game.entity.npc.impl.godwars.saradomin.CommanderZilyana;
import com.citelic.game.entity.npc.impl.godwars.zamorak.KrilTstsaroth;
import com.citelic.game.entity.npc.impl.godwars.zaros.Nex;
import com.citelic.game.entity.npc.impl.qbd.QueenBlackDragon;
import com.citelic.game.entity.player.Equipment;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.slayer.SlayerManager;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.content.miscellaneous.ClueScrolls;
import com.citelic.game.entity.player.content.miscellaneous.RingOfWealth;
import com.citelic.game.entity.player.content.miscellaneous.RingOfWealth.rare_drop;
import com.citelic.game.entity.player.content.socialization.FriendChatsManager;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;
import com.citelic.utility.map.MapAreas;
import com.citelic.utility.npc.NPCBonuses;
import com.citelic.utility.npc.NPCCombatDefinitionsL;
import com.citelic.utility.npc.NPCDrops;

public class NPC extends Entity implements Serializable {

	private static final long serialVersionUID = -4794678936277614443L;

	/*
	 * forces npc to random walk even if cache says no, used because of fake
	 * cache information
	 */
	private static boolean forceRandomWalk(int npcId) {
		switch (npcId) {
		case 11226:
			return true;
		case 3341:
		case 3342:
		case 3343:
			return true;
		default:
			return false;
		}
	}

	private int id;
	private Tile respawnTile;
	private int mapAreaNameHash;
	private boolean canBeAttackFromOutOfArea;
	private boolean randomwalk;
	private int[] bonuses; // 0 stab, 1 slash, 2 crush,3 mage, 4 range, 5 stab
	// def, blahblah till 9
	private boolean spawned;
	private transient NPCCombat combat;

	public Tile forceWalk;
	private long lastAttackedByTarget;
	private boolean cantInteract;
	private int capDamage;
	private int lureDelay;
	private boolean cantFollowUnderCombat;
	private boolean forceAgressive;
	private int forceTargetDistance;
	private boolean forceFollowClose;
	private boolean forceMultiAttacked;

	private boolean noDistanceCheck;
	// npc masks
	private transient Transformation nextTransformation;
	// name changing masks
	private String name;
	private transient boolean changedName;
	private int combatLevel;
	private transient boolean changedCombatLevel;

	private transient boolean locked;

	public String[] gnomeTrainerForceTalk = { "That's it, straight up!",
			"Come on scaredy cat get across that rope.",
			"My granny can move faster than you", "Move it, move it, move it" };

	private int[] BRAWLING_REWARDS = { 13848, 13849, 13850, 13851, 13857 };

	private int[] LUCIEN_REWARDS = { 24849, 24850, 24851, 24852 };

	private int[] PVP_ITEMS = { 13887, 13893, 13899, 13905, 13911, 13917,
			13923, 13929, 13884, 13890, 13896, 13902, 13908, 13914, 13920,
			13926, 13870, 13873, 13876, 13879, 13882, 13944, 13947, 13950,
			13858, 13861, 13864, 13867 };

	private static final Item[] HIGH_CHARMS_DROP = { new Item(12158, 6),
			new Item(12159, 6), new Item(12160, 6), new Item(12163, 6), };

	private static final Item[] MED_CHARMS_DROP = { new Item(12158, 6),
			new Item(12159, 6), new Item(12160, 6), };

	private static final Item[] LOW_CHARMS_DROP = { new Item(12158, 6),
			new Item(12159, 6), };

	private int[] announceDrops = { 14484, 20135, 20139, 20143, 20147, 20151,
			20155, 20159, 20163, 20167, 11702, 11704, 11706, 11708, 13746,
			13750, 13752, 13748, 24986, 24980, 24977, 24983, 24974, 24989 };

	private SecondBar secondBar;

	public NPC(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		this(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	/*
	 * creates and adds npc
	 */
	public NPC(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(tile);
		this.id = id;
		this.respawnTile = new Tile(tile);
		this.mapAreaNameHash = mapAreaNameHash;
		this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
		this.setSpawned(spawned);
		combatLevel = -1;
		setHitpoints(getMaxHitpoints());
		setDirection(getRespawnDirection());
		for (int npcId : GameConstants.NON_WALKING_NPCS) {
			if (npcId == id) {
				setRandomWalk(false);
				break;
			} else {
				setRandomWalk((getDefinitions().walkMask & 0x2) != 0
						|| forceRandomWalk(id));
			}
		}
		if (id == 531) {
			setRandomWalk(true);
		}
		setBonuses();
		combat = new NPCCombat(this);
		capDamage = -1;
		lureDelay = 12000;
		// npc is inited on creating instance
		initEntity();
		Engine.addNPC(this);
		Engine.updateEntityRegion(this);
		// npc is started on creating instance
		loadMapRegions();
		checkMultiArea();
	}

	public boolean canBeAttackedByAutoRelatie() {
		return Utilities.currentTimeMillis() - lastAttackedByTarget > lureDelay;
	}

	public boolean canBeAttackFromOutOfArea() {
		return canBeAttackFromOutOfArea;
	}

	public boolean checkAgressivity() {
		// if(!(Wilderness.isAtWild(this) &&
		// getDefinitions().hasAttackOption())) {
		if (!forceAgressive) {
			NPCCombatDefinitions defs = getCombatDefinitions();
			if (defs.getAgressivenessType() == NPCCombatDefinitions.PASSIVE)
				return false;
		}
		// }
		ArrayList<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(Utilities.random(possibleTarget
					.size()));
			setTarget(target);
			target.setAttackedBy(target);
			target.setFindTargetDelay(Utilities.currentTimeMillis() + 10000);
			return true;
		}
		return false;
	}

	public boolean containsItem(int id) {
		Item item = new Item(id);
		return containsItem(item);
	}

	public boolean containsItem(Item item) {
		Player killer = getMostDamageReceivedSourcePlayer();
		return killer.getInventory().getItems()
				.contains(new Item(item.getId(), 1))
				|| killer.getEquipment().getItems()
						.contains(new Item(item.getId(), 1));
	}

	public void deserialize() {
		if (combat == null)
			combat = new NPCCombat(this);
		spawn();
	}

	public void drop() {
		try {
			Drop[] drops = NPCDrops.getDrops(id);
			if (drops == null || getMaxHitpoints() == 1)
				return;
			Player killer = getMostDamageReceivedSourcePlayer();
			if (killer == null)
				return;
			Player otherPlayer = killer.getSlayerManager().getSocialPlayer();
			SlayerManager manager = killer.getSlayerManager();
			if (manager.isValidTask(getName()))
				manager.checkCompletedTask(getDamageReceived(killer),
						otherPlayer != null ? getDamageReceived(otherPlayer)
								: 0);
			if (isCyclops(getDefinitions().name) && Utilities.random(500) > 490)
				Engine.addGroundItem(new Item(whatDefender(), 1),
						new Tile(this.getLastWorldTile()), killer, true, 180,
						true);
			if (isRevenantNPC(getDefinitions().name)
					&& Utilities.random(65) == 0)
				Engine.addGroundItem(
						new Item(BRAWLING_REWARDS[Utilities
								.random(BRAWLING_REWARDS.length - 1)], 1),
						new Tile(this.getLastWorldTile()), killer, true, 180,
						true);
			if (isRevenantNPC(getDefinitions().name)
					&& killer.checkTotalLevel(350) < 350
					&& Utilities.random(3000) >= 2700) {
				int pvpItem = PVP_ITEMS[Utilities.random(PVP_ITEMS.length - 1)];
				Engine.addGroundItem(new Item(pvpItem,
						pvpItem == 13882 ? Utilities.random(30)
								: pvpItem == 13879 ? Utilities.random(30) : 1),
						new Tile(this.getLastWorldTile()), killer, true, 180,
						true);
				return;
			} else if (isRevenantNPC(getDefinitions().name)
					&& killer.checkTotalLevel(1500) > 1000
					&& Utilities.random(1000) >= 900) {
				int pvpItem = PVP_ITEMS[Utilities.random(PVP_ITEMS.length - 1)];
				Engine.addGroundItem(new Item(pvpItem,
						pvpItem == 13882 ? Utilities.random(30)
								: pvpItem == 13879 ? Utilities.random(30) : 1),
						new Tile(this.getLastWorldTile()), killer, true, 180,
						true);
				return;
			} else if (isRevenantNPC(getDefinitions().name)) {
				Engine.addGroundItem(new Item(995, Utilities.random(5000)),
						new Tile(this.getLastWorldTile()), killer, true, 180,
						true);
				return;
			}
			if (isClueScrollNPC(getDefinitions().name)
					&& Utilities.random(1000) >= 980)
				Engine.addGroundItem(
						new Item(ClueScrolls.ScrollIds[Utilities
								.random(ClueScrolls.ScrollIds.length)], 1),
						new Tile(this.getLastWorldTile()), killer, true, 180,
						true);
			if (isLucien(getDefinitions().name) && Utilities.random(15) == 0)
				Engine.addGroundItem(
						new Item(LUCIEN_REWARDS[Utilities
								.random(LUCIEN_REWARDS.length - 1)], 1),
						new Tile(this.getLastWorldTile()), killer, true, 180,
						true);
			Drop[] possibleDrops = new Drop[drops.length];
			Item ring = killer.getEquipment().getItem(Equipment.SLOT_RING);
			int possibleDropsCount = 0;
			for (Drop drop : drops) {
				if (drop.getRate() == 100)
					sendDrop(killer, drop);
				else {
					if (ring != null
							&& ring.getDefinitions().getName()
									.contains("Ring of wealth")) {
						if ((Utilities.getRandomDouble(99) + 1) <= (drop
								.getRate() + 3) * 1.0)
							possibleDrops[possibleDropsCount++] = drop;
					} else {
						if ((Utilities.getRandomDouble(99) + 1) <= drop
								.getRate() * 1.0)
							possibleDrops[possibleDropsCount++] = drop;
					}
				}
			}
			if (possibleDropsCount > 0)
				sendDrop(killer,
						possibleDrops[Utilities
								.getRandom(possibleDropsCount - 1)]);
			if (getCombatLevel() > 100) {
				if (Utilities.random(3) == 0)
					dropCharm(killer,
							HIGH_CHARMS_DROP[Utilities
									.random(HIGH_CHARMS_DROP.length)], id);
				return;
			} else if (getCombatLevel() > 50) {
				if (Utilities.random(3) == 0)
					dropCharm(killer,
							MED_CHARMS_DROP[Utilities
									.random(MED_CHARMS_DROP.length)], id);
				return;
			} else if (getCombatLevel() < 50) {
				if (Utilities.random(3) == 0)
					dropCharm(killer,
							LOW_CHARMS_DROP[Utilities
									.random(LOW_CHARMS_DROP.length)], id);
				return;
			}
			if (ring == null
					|| !ring.getDefinitions().getName()
							.contains("Ring of wealth")
					&& (Utilities.getRandomDouble(99) + 1) <= 8
					&& onDropTable(getDefinitions().name)) {
				killer.getPackets()
						.sendGameMessage(
								"<col=ff8c38>Your ring of wealth shines more brightly!");
				if ((Utilities.getRandomDouble(99) + 1) <= 6) {
					RingOfWealth DROP = RingOfWealth.random(killer,
							rare_drop.ULTRARARE);
					sendDrop(killer, DROP);
				} else if ((Utilities.getRandomDouble(99) + 1) <= 15) {
					RingOfWealth DROP = RingOfWealth.random(killer,
							rare_drop.RARE);
					sendDrop(killer, DROP);
				} else if ((Utilities.getRandomDouble(99) + 1) <= 40) {
					RingOfWealth DROP = RingOfWealth.random(killer,
							rare_drop.UNCOMMON);
					sendDrop(killer, DROP);
				} else {
					RingOfWealth DROP = RingOfWealth.random(killer,
							rare_drop.COMMON);
					sendDrop(killer, DROP);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	public void dropCharm(Player player, Item item, int id) {
		Item dropItem = new Item(item.getId(), Utilities.random(item
				.getDefinitions().isStackable() ? item.getAmount()
				* GameConstants.CHARM_RATE : item.getAmount()) + 1);
		Engine.addGroundItem(dropItem, new Tile(getCoordFaceX(getSize()),
				getCoordFaceY(getSize()), getZ()), player, false, 180, true);
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		setFinished(true);
		Engine.updateEntityRegion(this);
		Engine.removeNPC(this);
	}

	public void forceWalkRespawnTile() {
		setForceWalk(respawnTile);
	}

	public int[] getBonuses() {
		return bonuses;
	}

	public int getCapDamage() {
		return capDamage;
	}

	public NPCCombat getCombat() {
		return combat;
	}

	public NPCCombatDefinitions getCombatDefinitions() {
		return NPCCombatDefinitionsL.getNPCCombatDefinitions(id);
	}

	public int getCombatLevel() {
		return combatLevel >= 0 ? combatLevel : getDefinitions().combatLevel;
	}

	public int getCustomCombatLevel() {
		return combatLevel;
	}

	public String getCustomName() {
		return name;
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

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getNPCDefinitions(id);
	}

	public int getForceTargetDistance() {
		return forceTargetDistance;
	}

	public int getId() {
		return id;
	}

	public int getLureDelay() {
		return lureDelay;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0;
	}

	public int getMapAreaNameHash() {
		return mapAreaNameHash;
	}

	public int getMaxHit() {
		return getCombatDefinitions().getMaxHit();
	}

	@Override
	public int getMaxHitpoints() {
		return getCombatDefinitions().getHitpoints();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0;
	}

	public Tile getMiddleWorldTile() {
		int size = getSize();
		return new Tile(getCoordFaceX(size), getCoordFaceY(size), getZ());
	}

	public String getName() {
		return name != null ? name : getDefinitions().name;
	}

	public Transformation getNextTransformation() {
		return nextTransformation;
	}

	public ArrayList<Entity> getPossibleTargets(boolean checkNPCs,
			boolean checkPlayers) {
		int size = getSize();
		int agroRatio = getCombatDefinitions().getAgroRatio();
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			if (checkPlayers) {
				List<Integer> playerIndexes = Engine.getRegion(regionId)
						.getPlayerIndexes();
				if (playerIndexes != null) {
					for (int playerIndex : playerIndexes) {
						Player player = Engine.getPlayers().get(playerIndex);
						if (player == null
								|| player.isDead()
								|| player.hasFinished()
								|| !player.isRunning()
								|| player.getPlayerAppearance().isHidden()
								|| !Utilities
										.isOnRange(
												getX(),
												getY(),
												size,
												player.getX(),
												player.getY(),
												player.getSize(),
												forceTargetDistance > 0 ? forceTargetDistance
														: agroRatio)
								|| (!forceMultiAttacked
										&& (!isAtMultiArea() || !player
												.isAtMultiArea()) && (player
										.getAttackedBy() != this && (player
										.getAttackedByDelay() > Utilities
										.currentTimeMillis() || player
										.getFindTargetDelay() > Utilities
										.currentTimeMillis())))
								|| !clipedProjectile(player, false)
								|| (!forceAgressive
										&& !Wilderness.isAtWild(this) && player
										.getSkills()
										.getCombatLevelWithSummoning() >= getCombatLevel() * 2))
							continue;
						possibleTarget.add(player);
					}
				}
			}
			if (checkNPCs) {
				List<Integer> npcsIndexes = Engine.getRegion(regionId)
						.getNPCsIndexes();
				if (npcsIndexes != null) {
					for (int npcIndex : npcsIndexes) {
						NPC npc = Engine.getNPCs().get(npcIndex);
						if (npc == null
								|| npc == this
								|| npc.isDead()
								|| npc.hasFinished()
								|| !Utilities
										.isOnRange(
												getX(),
												getY(),
												size,
												npc.getX(),
												npc.getY(),
												npc.getSize(),
												forceTargetDistance > 0 ? forceTargetDistance
														: agroRatio)
								|| !npc.getDefinitions().hasAttackOption()
								|| ((!isAtMultiArea() || !npc.isAtMultiArea())
										&& npc.getAttackedBy() != this && npc
										.getAttackedByDelay() > Utilities
										.currentTimeMillis())
								|| !clipedProjectile(npc, false))
							continue;
						possibleTarget.add(npc);
					}
				}
			}
		}
		return possibleTarget;
	}

	public ArrayList<Entity> getPossibleTargets() {
		return getPossibleTargets(false, true);
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0;
	}

	public int getRespawnDirection() {
		NPCDefinitions definitions = getDefinitions();
		if (definitions.anInt853 << 32 != 0 && definitions.respawnDirection > 0
				&& definitions.respawnDirection <= 8)
			return (4 + definitions.respawnDirection) << 11;
		return 0;
	}

	public Tile getRespawnTile() {
		return respawnTile;
	}

	public SecondBar getSecondBar() {
		return secondBar;
	}

	@Override
	public int getSize() {
		return getDefinitions().size;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (capDamage != -1 && hit.getDamage() > capDamage)
			hit.setDamage(capDamage);
		if (hit.getLook() != HitLook.MELEE_DAMAGE
				&& hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		Entity source = hit.getSource();
		if (source == null)
			return;
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			if (p2.getPrayer().hasPrayersOn()) {
				if (p2.getPrayer().usingPrayer(1, 18))
					sendSoulSplit(hit, p2);
				if (hit.getDamage() == 0)
					return;
				if (!p2.getPrayer().isBoostedLeech()) {
					if (hit.getLook() == HitLook.MELEE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 19)) {
							p2.getPrayer().setBoostedLeech(true);
							return;
						} else if (p2.getPrayer().usingPrayer(1, 1)) { // sap
							// att
							if (Utilities.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(0)) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your sap curse has no effect.",
													true);
								} else {
									p2.getPrayer().increaseLeechBonus(0);
									p2.getPackets()
											.sendGameMessage(
													"Your curse drains Attack from the enemy, boosting your Attack.",
													true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2214));
								p2.getPrayer().setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2215, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2216));
									}
								}, 1);
								return;
							}
						} else {
							if (p2.getPrayer().usingPrayer(1, 10)) {
								if (Utilities.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(3)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your leech curse has no effect.",
														true);
									} else {
										p2.getPrayer().increaseLeechBonus(3);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Attack from the enemy, boosting your Attack.",
														true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									Engine.sendProjectile(p2, this, 2231, 35,
											35, 20, 5, 0, 0);
									EngineTaskManager.schedule(
											new EngineTask() {
												@Override
												public void run() {
													setNextGraphics(new Graphics(
															2232));
												}
											}, 1);
									return;
								}
							}
							if (p2.getPrayer().usingPrayer(1, 14)) {
								if (Utilities.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(7)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your leech curse has no effect.",
														true);
									} else {
										p2.getPrayer().increaseLeechBonus(7);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Strength from the enemy, boosting your Strength.",
														true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									Engine.sendProjectile(p2, this, 2248, 35,
											35, 20, 5, 0, 0);
									EngineTaskManager.schedule(
											new EngineTask() {
												@Override
												public void run() {
													setNextGraphics(new Graphics(
															2250));
												}
											}, 1);
									return;
								}
							}

						}
					}
					if (hit.getLook() == HitLook.RANGE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 2)) { // sap range
							if (Utilities.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(1)) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your sap curse has no effect.",
													true);
								} else {
									p2.getPrayer().increaseLeechBonus(1);
									p2.getPackets()
											.sendGameMessage(
													"Your curse drains Range from the enemy, boosting your Range.",
													true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2217));
								p2.getPrayer().setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2218, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2219));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 11)) {
							if (Utilities.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(4)) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.getPrayer().increaseLeechBonus(4);
									p2.getPackets()
											.sendGameMessage(
													"Your curse drains Range from the enemy, boosting your Range.",
													true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2236, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2238));
									}
								});
								return;
							}
						}
					}
					if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 3)) { // sap mage
							if (Utilities.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(2)) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your sap curse has no effect.",
													true);
								} else {
									p2.getPrayer().increaseLeechBonus(2);
									p2.getPackets()
											.sendGameMessage(
													"Your curse drains Magic from the enemy, boosting your Magic.",
													true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2220));
								p2.getPrayer().setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2221, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2222));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 12)) {
							if (Utilities.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(5)) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.getPrayer().increaseLeechBonus(5);
									p2.getPackets()
											.sendGameMessage(
													"Your curse drains Magic from the enemy, boosting your Magic.",
													true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2240, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2242));
									}
								}, 1);
								return;
							}
						}
					}

					// overall

					if (p2.getPrayer().usingPrayer(1, 13)) { // leech defence
						if (Utilities.getRandom(10) == 0) {
							if (p2.getPrayer().reachedMax(6)) {
								p2.getPackets()
										.sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
							} else {
								p2.getPrayer().increaseLeechBonus(6);
								p2.getPackets()
										.sendGameMessage(
												"Your curse drains Defence from the enemy, boosting your Defence.",
												true);
							}
							p2.setNextAnimation(new Animation(12575));
							p2.getPrayer().setBoostedLeech(true);
							Engine.sendProjectile(p2, this, 2244, 35, 35, 20,
									5, 0, 0);
							EngineTaskManager.schedule(new EngineTask() {
								@Override
								public void run() {
									setNextGraphics(new Graphics(2246));
								}
							}, 1);
							return;
						}
					}
				}
			}
		}

	}

	public boolean hasChangedCombatLevel() {
		return changedCombatLevel;
	}

	public boolean hasChangedName() {
		return changedName;
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

	public boolean hasForceWalk() {
		return forceWalk != null;
	}

	public boolean hasRandomWalk() {
		return randomwalk;
	}

	public boolean isCantFollowUnderCombat() {
		return cantFollowUnderCombat;
	}

	public boolean isCantInteract() {
		return cantInteract;
	}

	public boolean isClueScrollNPC(String npcName) {
		switch (npcName) {
		case "'Rum'-pumped crab":
		case "Aberrant spectre":
		case "Abyssal demon":
		case "Abyssal leech":
		case "Air elemental":
		case "Ancient mage":
		case "Ancient ranger":
		case "Ankou":
		case "Armoured zombie":
		case "Arrg":
		case "Astea Frostweb":
		case "Aviansie":
		case "Bandit":
		case "Banshee":
		case "Barbarian":
		case "Barbarian woman":
		case "Basilisk":
		case "Black Guard":
		case "Black Guard Berserker":
		case "Black Guard crossbowdwarf":
		case "Black Heather":
		case "Black Knight":
		case "Black Knight Titan":
		case "Black demon":
		case "Black dragon":
		case "Blood reaver":
		case "Bloodveld":
		case "Blue dragon":
		case "Bork":
		case "Brine rat":
		case "Bronze dragon":
		case "Brutal green dragon":
		case "Catablepon":
		case "Cave bug":
		case "Cave crawler":
		case "Cave horror":
		case "Cave slime":
		case "Chaos Elemental":
		case "Chaos druid":
		case "Chaos druid warrior":
		case "Chaos dwarf":
		case "Chaos dwarf hand cannoneer":
		case "Chaos dwogre":
		case "Cockatrice":
		case "Cockroach drone":
		case "Cockroach soldier":
		case "Cockroach worker":
		case "Columbarium":
		case "Columbarium key":
		case "Commander Zilyana":
		case "Corporeal Beast":
		case "Crawling Hand":
		case "Cyclops":
		case "Cyclossus":
		case "Dagannoth":
		case "Dagannoth Prime":
		case "Dagannoth Rex":
		case "Dagannoth Supreme":
		case "Dagannoth guardian":
		case "Dagannoth spawn":
		case "Dark beast":
		case "Desert Lizard":
		case "Desert strykewyrm":
		case "Dried zombie":
		case "Dust devil":
		case "Dwarf":
		case "Earth elemental":
		case "Earth warrior":
		case "Elf warrior":
		case "Elite Black Knight":
		case "Elite Dark Ranger":
		case "Elite Khazard guard":
		case "Exiled Kalphite Queen":
		case "Exiled kalphite guardian":
		case "Exiled kalphite marauder":
		case "Ferocious barbarian spirit":
		case "Fire elemental":
		case "Fire giant":
		case "Flesh Crawler":
		case "Forgotten Archer":
		case "Forgotten Mage":
		case "Forgotten Warrior":
		case "Frog":
		case "Frost dragon":
		case "Ganodermic beast":
		case "Gargoyle":
		case "General Graardor":
		case "General malpractitioner":
		case "Ghast":
		case "Ghostly warrior":
		case "Giant Mole":
		case "Giant ant soldier":
		case "Giant ant worker":
		case "Giant rock crab":
		case "Giant skeleton":
		case "Giant wasp":
		case "Glacor":
		case "Glod":
		case "Gnoeals":
		case "Goblin statue":
		case "Gorak":
		case "Greater demon":
		case "Greater reborn mage":
		case "Greater reborn ranger":
		case "Greater reborn warrior":
		case "Green dragon":
		case "Grotworm":
		case "Haakon the Champion":
		case "Harold":
		case "Harpie Bug Swarm":
		case "Hill giant":
		case "Hobgoblin":
		case "Ice giant":
		case "Ice strykewyrm":
		case "Ice troll":
		case "Ice troll female":
		case "Ice troll male":
		case "Ice troll runt":
		case "Ice warrior":
		case "Icefiend":
		case "Iron dragon":
		case "Jelly":
		case "Jogre":
		case "Jungle horror":
		case "Jungle strykewyrm":
		case "K'ril Tsutsaroth":
		case "Kalphite Guardian":
		case "Kalphite King":
		case "Kalphite Queen":
		case "Kalphite Soldier":
		case "Kalphite Worker":
		case "Killerwatt":
		case "King Black Dragon":
		case "Kraka":
		case "Kree'arra":
		case "Kurask":
		case "Lanzig":
		case "Lesser demon":
		case "Lesser reborn mage":
		case "Lesser reborn ranger":
		case "Lesser reborn warrior":
		case "Lizard":
		case "Locust lancer":
		case "Locust ranger":
		case "Locust rider":
		case "Mature grotworm":
		case "Mighty banshee":
		case "Minotaur":
		case "Mithril dragon":
		case "Molanisk":
		case "Moss giant":
		case "Mountain troll":
		case "Mummy":
		case "Mutated bloodveld":
		case "Mutated jadinko male":
		case "Mutated zygomite":
		case "Nechryael":
		case "Nex":
		case "Ogre":
		case "Ogre statue":
		case "Ork statue":
		case "Otherworldly being":
		case "Ourg statue":
		case "Paladin":
		case "Pee Hat":
		case "Pirate":
		case "Pyrefiend":
		case "Queen Black Dragon":
		case "Red dragon":
		case "Rock lobster":
		case "Rockslug":
		case "Salarin the Twisted":
		case "Scabaras lancer":
		case "Scarab mage":
		case "Sea Snake Hatchling":
		case "Shadow warrior":
		case "Skeletal Wyvern":
		case "Skeletal miner":
		case "Skeleton":
		case "Skeleton fremennik":
		case "Skeleton thug":
		case "Skeleton warlord":
		case "Small Lizard":
		case "Soldier":
		case "Sorebones":
		case "Speedy Keith":
		case "Spiritual mage":
		case "Spiritual warrior":
		case "Steel dragon":
		case "Stick":
		case "Suqah":
		case "Terror dog":
		case "Thrower Troll":
		case "Thug":
		case "Tortured soul":
		case "Trade floor guard":
		case "Tribesman":
		case "Troll general":
		case "Troll spectator":
		case "Tstanon Karlak":
		case "Turoth":
		case "Tyras guard":
		case "TzHaar-Hur":
		case "TzHaar-Ket":
		case "TzHaar-Mej":
		case "TzHaar-Xil":
		case "Undead troll":
		case "Vampyre":
		case "Vyre corpse":
		case "Vyrelady":
		case "Vyrelord":
		case "Vyrewatch":
		case "Wallasalki":
		case "Warped terrorbird":
		case "Warped tortoise":
		case "Warrior":
		case "Water elemental":
		case "Waterfiend":
		case "Werewolf":
		case "White Knight":
		case "WildyWyrm":
		case "Yeti":
		case "Yuri":
		case "Zakl'n Gritch":
		case "Zombie":
		case "Zombie hand":
		case "Zombie swab":
			return true;
		}
		return false;
	}

	public boolean isCyclops(String npcName) {
		switch (npcName) {
		case "Cyclops":
			return true;
		}
		return false;
	}

	public boolean isFamiliar() {
		return this instanceof Familiar;
	}

	public boolean isForceAgressive() {
		return forceAgressive;
	}

	public boolean isForceFollowClose() {
		return forceFollowClose;
	}

	public boolean isForceMultiAttacked() {
		return forceMultiAttacked;
	}

	public boolean isForceWalking() {
		return forceWalk != null;
	}

	/**
	 * Gets the locked.
	 * 
	 * @return The locked.
	 */
	public boolean isLocked() {
		return locked;
	}

	public boolean isLucien(String npcName) {
		switch (npcName) {
		case "Lucien":
			return true;
		}
		return false;
	}

	public boolean isNoDistanceCheck() {
		return noDistanceCheck;
	}

	public boolean isRevenantNPC(String npcName) {
		switch (npcName) {
		case "Revenant icefiend":
		case "Revenant knight":
		case "Revenant goblin":
		case "Revenant werewolf":
		case "Revenant hobgoblin":
		case "Revenant hellhound":
		case "Revenant vampyre":
		case "Revenant dragon":
		case "Revenant demon":
		case "Revenant pyrefiend":
		case "Revenant dark beast":
		case "Revenant imp":
		case "Revenant ork":
			return true;
		}
		return false;
	}

	public boolean isSpawned() {
		return spawned;
	}

	public boolean isUnderCombat() {
		return combat.underCombat();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || nextTransformation != null
				|| changedCombatLevel || changedName || secondBar != null;
	}

	public boolean onDropTable(String npcName) {
		switch (npcName) {
		case "'Rum'-pumped crab":
		case "Aberrant spectre":
		case "Abyssal demon":
		case "Abyssal leech":
		case "Air elemental":
		case "Ancient mage":
		case "Ancient ranger":
		case "Ankou":
		case "Armoured zombie":
		case "Arrg":
		case "Astea Frostweb":
		case "Aviansie":
		case "Bandit":
		case "Banshee":
		case "Barbarian":
		case "Barbarian woman":
		case "Basilisk":
		case "Black Guard":
		case "Black Guard Berserker":
		case "Black Guard crossbowdwarf":
		case "Black Heather":
		case "Black Knight":
		case "Black Knight Titan":
		case "Black demon":
		case "Black dragon":
		case "Blood reaver":
		case "Bloodveld":
		case "Blue dragon":
		case "Bork":
		case "Brine rat":
		case "Bronze dragon":
		case "Brutal green dragon":
		case "Catablepon":
		case "Cave bug":
		case "Cave crawler":
		case "Cave horror":
		case "Cave slime":
		case "Chaos Elemental":
		case "Chaos druid":
		case "Chaos druid warrior":
		case "Chaos dwarf":
		case "Chaos dwarf hand cannoneer":
		case "Chaos dwogre":
		case "Cockatrice":
		case "Cockroach drone":
		case "Cockroach soldier":
		case "Cockroach worker":
		case "Columbarium":
		case "Columbarium key":
		case "Commander Zilyana":
		case "Corporeal Beast":
		case "Crawling Hand":
		case "Cyclops":
		case "Cyclossus":
		case "Dagannoth":
		case "Dagannoth Prime":
		case "Dagannoth Rex":
		case "Dagannoth Supreme":
		case "Dagannoth guardian":
		case "Dagannoth spawn":
		case "Dark beast":
		case "Desert Lizard":
		case "Desert strykewyrm":
		case "Dried zombie":
		case "Dust devil":
		case "Dwarf":
		case "Earth elemental":
		case "Earth warrior":
		case "Elf warrior":
		case "Elite Black Knight":
		case "Elite Dark Ranger":
		case "Elite Khazard guard":
		case "Exiled Kalphite Queen":
		case "Exiled kalphite guardian":
		case "Exiled kalphite marauder":
		case "Ferocious barbarian spirit":
		case "Fire elemental":
		case "Fire giant":
		case "Flesh Crawler":
		case "Forgotten Archer":
		case "Forgotten Mage":
		case "Forgotten Warrior":
		case "Frog":
		case "Frost dragon":
		case "Ganodermic beast":
		case "Gargoyle":
		case "General Graardor":
		case "General malpractitioner":
		case "Ghast":
		case "Ghostly warrior":
		case "Giant Mole":
		case "Giant ant soldier":
		case "Giant ant worker":
		case "Giant rock crab":
		case "Giant skeleton":
		case "Giant wasp":
		case "Glacor":
		case "Glod":
		case "Gnoeals":
		case "Goblin statue":
		case "Gorak":
		case "Greater demon":
		case "Greater reborn mage":
		case "Greater reborn ranger":
		case "Greater reborn warrior":
		case "Green dragon":
		case "Grotworm":
		case "Haakon the Champion":
		case "Harold":
		case "Harpie Bug Swarm":
		case "Hill giant":
		case "Hobgoblin":
		case "Ice giant":
		case "Ice strykewyrm":
		case "Ice troll":
		case "Ice troll female":
		case "Ice troll male":
		case "Ice troll runt":
		case "Ice warrior":
		case "Icefiend":
		case "Iron dragon":
		case "Jelly":
		case "Jogre":
		case "Jungle horror":
		case "Jungle strykewyrm":
		case "K'ril Tsutsaroth":
		case "Kalphite Guardian":
		case "Kalphite King":
		case "Kalphite Queen":
		case "Kalphite Soldier":
		case "Kalphite Worker":
		case "Killerwatt":
		case "King Black Dragon":
		case "Kraka":
		case "Kree'arra":
		case "Kurask":
		case "Lanzig":
		case "Lesser demon":
		case "Lesser reborn mage":
		case "Lesser reborn ranger":
		case "Lesser reborn warrior":
		case "Lizard":
		case "Locust lancer":
		case "Locust ranger":
		case "Locust rider":
		case "Mature grotworm":
		case "Mighty banshee":
		case "Minotaur":
		case "Mithril dragon":
		case "Molanisk":
		case "Moss giant":
		case "Mountain troll":
		case "Mummy":
		case "Mutated bloodveld":
		case "Mutated jadinko male":
		case "Mutated zygomite":
		case "Nechryael":
		case "Nex":
		case "Ogre":
		case "Ogre statue":
		case "Ork statue":
		case "Otherworldly being":
		case "Ourg statue":
		case "Paladin":
		case "Pee Hat":
		case "Pirate":
		case "Pyrefiend":
		case "Queen Black Dragon":
		case "Red dragon":
		case "Rock lobster":
		case "Rockslug":
		case "Salarin the Twisted":
		case "Scabaras lancer":
		case "Scarab mage":
		case "Sea Snake Hatchling":
		case "Shadow warrior":
		case "Skeletal Wyvern":
		case "Skeletal miner":
		case "Skeleton":
		case "Skeleton fremennik":
		case "Skeleton thug":
		case "Skeleton warlord":
		case "Small Lizard":
		case "Soldier":
		case "Sorebones":
		case "Speedy Keith":
		case "Spiritual mage":
		case "Spiritual warrior":
		case "Steel dragon":
		case "Stick":
		case "Suqah":
		case "Terror dog":
		case "Thrower Troll":
		case "Thug":
		case "Tortured soul":
		case "Trade floor guard":
		case "Tribesman":
		case "Troll general":
		case "Troll spectator":
		case "Tstanon Karlak":
		case "Turoth":
		case "Tyras guard":
		case "TzHaar-Hur":
		case "TzHaar-Ket":
		case "TzHaar-Mej":
		case "TzHaar-Xil":
		case "Undead troll":
		case "Vampyre":
		case "Vyre corpse":
		case "Vyrelady":
		case "Vyrelord":
		case "Vyrewatch":
		case "Wallasalki":
		case "Warped terrorbird":
		case "Warped tortoise":
		case "Warrior":
		case "Water elemental":
		case "Waterfiend":
		case "Werewolf":
		case "White Knight":
		case "WildyWyrm":
		case "Yeti":
		case "Yuri":
		case "Zakl'n Gritch":
		case "Zombie":
		case "Zombie hand":
		case "Zombie swab":
			return true;
		}
		return false;
	}

	public boolean onHighPointsTable(String npcName) {
		switch (npcName) {
		case "Queen black dragon":
		case "Tztok-Jad":
		case "Corporeal Beast":
		case "Nex":
			return true;
		}
		return false;
	}

	public boolean onLowPointsTable(String npcName) {
		switch (npcName) {
		case "Dark beast":
		case "Ganodermic beast":
		case "Abyssal demon":
		case "Mutated jadinko male":
		case "Mutated jadinko guard":
		case "Green dragon":
		case "Frost dragon":
		case "Blue dragon":
		case "Red dragon":
		case "Steel dragon":
		case "Iron dragon":
		case "Glacor":
			return true;
		}
		return false;
	}

	public boolean onMedPointsTable(String npcName) {
		switch (npcName) {
		case "Kree'arra":
		case "Tormented demon":
		case "Kalphite queen":
		case "King black dragon":
		case "WildyWyrm":
		case "General Graardor":
			return true;
		}
		return false;
	}

	public void setNextNPCTransformation(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
		if (getCustomCombatLevel() != -1)
			changedCombatLevel = true;
		if (getCustomName() != null)
			changedName = true;
	}

	@Override
	public void processEntity() {
		super.processEntity();
		processNPC();
	}

	public void processNPC() {
		if (isDead() || locked)
			return;
		if (!combat.process()) { // if not under combat
			if (!isForceWalking()) {// combat still processed for attack delay
				// go down
				// random walk
				// dontWalkPlease(id);
				if (!cantInteract) {
					if (!checkAgressivity()) {
						if (getFreezeDelay() < Utilities.currentTimeMillis()) {
							if (((hasRandomWalk()) && Engine.getRotation(
									getZ(), getX(), getY()) == 0) // temporary
									// fix
									&& Math.random() * 1000.0 < 100.0) {
								int moveX = (int) Math
										.round(Math.random() * 10.0 - 5.0);
								int moveY = (int) Math
										.round(Math.random() * 10.0 - 5.0);
								resetWalkSteps();
								if (getMapAreaNameHash() != -1) {
									if (!MapAreas.isAtArea(
											getMapAreaNameHash(), this)) {
										forceWalkRespawnTile();
										return;
									}
									addWalkSteps(getX() + moveX,
											getY() + moveY, 5);
								} else
									addWalkSteps(respawnTile.getX() + moveX,
											respawnTile.getY() + moveY, 5);
							}
						}
					}
				}
			}
		}
		if (isForceWalking()) {
			if (getFreezeDelay() < Utilities.currentTimeMillis()) {
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps())
						addWalkSteps(forceWalk.getX(), forceWalk.getY(),
								getSize(), true);
					if (!hasWalkSteps()) { // failing finding route
						setNextTile(new Tile(forceWalk)); // force
						// tele
						// to
						// the
						// forcewalk
						// place
						forceWalk = null; // so ofc reached forcewalk place
					}
				} else
					// walked till forcewalk place
					forceWalk = null;
			}
		}
		if (id == 162 && Utilities.random(10) == 0) {
			setNextForceTalk(new ForceTalk(
					gnomeTrainerForceTalk[Utilities
							.random(gnomeTrainerForceTalk.length)]));
		}
	}

	public void removeTarget() {
		if (combat.getTarget() == null)
			return;
		combat.removeTarget();
	}

	@Override
	public void reset() {
		super.reset();
		setDirection(getRespawnDirection());
		combat.reset();
		bonuses = NPCBonuses.getBonuses(id); // back to real bonuses
		forceWalk = null;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		nextTransformation = null;
		changedCombatLevel = false;
		changedName = false;
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		combat.removeTarget();
		setNextAnimation(null);
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					setLocation(respawnTile);
					finish();
					if (!isSpawned())
						setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void sendDrop(Player player, Drop drop) {
		boolean dropOk = true;
		for (int i = 0; i < announceDrops.length; i++) {
			if (drop.getItemId() == announceDrops[i]) {
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(drop
						.getItemId());
				String itemName = defs.getName().toLowerCase();
				Engine.sendWorldMessage(
						"<img=7><col=ff8c38>News: " + player.getDisplayName()
								+ " just recieved "
								+ Utilities.getAorAn(itemName) + " " + itemName
								+ " drop!", false);
			}
		}
		for (int i = 0; i < ClueScrolls.ScrollIds.length; i++) {
			if (drop.getItemId() == ClueScrolls.ScrollIds[i]) {
				dropOk = false;
			}
		}
		if (drop.getItemId() == 536) {
			if (player.getInventory().containsItem(18337, 1)) {
				player.getSkills().addXp(Skills.PRAYER, 120);
				player.getPackets()
						.sendGameMessage(
								"Your bonecrusher turned your bone drop into Prayer XP.");
				return;
			}
		} else if (drop.getItemId() == 18830) {
			if (player.getInventory().containsItem(18337, 1)) {
				player.getSkills().addXp(Skills.PRAYER, 165);
				player.getPackets()
						.sendGameMessage(
								"Your bonecrusher turned your bone drop into Prayer XP.");
				return;
			}
		} else if (drop.getItemId() == 4834) {
			if (player.getInventory().containsItem(18337, 1)) {
				player.getSkills().addXp(Skills.PRAYER, 100);
				player.getPackets()
						.sendGameMessage(
								"Your bonecrusher turned your bone drop into Prayer XP.");
				return;
			}
		} else if (drop.getItemId() == 532) {
			if (player.getInventory().containsItem(18337, 1)) {
				player.getSkills().addXp(Skills.PRAYER, 80);
				player.getPackets()
						.sendGameMessage(
								"Your bonecrusher turned your bone drop into Prayer XP.");
				return;
			}
		} else if (drop.getItemId() == 6729) {
			if (player.getInventory().containsItem(18337, 1)) {
				player.getSkills().addXp(Skills.PRAYER, 140);
				player.getPackets()
						.sendGameMessage(
								"Your bonecrusher turned your bone drop into Prayer XP.");
				return;
			}
		} else if (drop.getItemId() == 526) {
			if (player.getInventory().containsItem(18337, 1)) {
				player.getSkills().addXp(Skills.PRAYER, 40);
				player.getPackets()
						.sendGameMessage(
								"Your bonecrusher turned your bone drop into Prayer XP.");
				return;
			}
		}
		if (drop.getItemId() == 618) {
			return;
		}
		if (dropOk) {
			Engine.addGroundItem(new Item(drop.getItemId(), drop.getMinAmount()
					+ Utilities.getRandom(drop.getExtraAmount())),
					new Tile(getCoordFaceX(getSize()),
							getCoordFaceY(getSize()), getZ()), player, false,
					180, true);
		}
	}

	public void sendDrop(Player player, Drop drop, boolean lootShare) {
		int size = getSize();
		String dropName = ItemDefinitions.getItemDefinitions(drop.getItemId())
				.getName();
		Item item = ItemDefinitions.getItemDefinitions(drop.getItemId())
				.isStackable() ? new Item(drop.getItemId(),
				(drop.getMinAmount() * GameConstants.DROP_RATE)
						+ Utilities.getRandom(drop.getExtraAmount()
								* GameConstants.DROP_RATE)) : new Item(
				drop.getItemId(), drop.getMinAmount()
						+ Utilities.getRandom(drop.getExtraAmount()));
		/* LootShare */
		FriendChatsManager fc = player.getCurrentFriendChat();
		if (player.lootshareEnabled()) {
			if (fc != null) {
				CopyOnWriteArrayList<Player> players = fc.getPlayers();
				CopyOnWriteArrayList<Player> playersWithLs = new CopyOnWriteArrayList<Player>();
				for (Player fcPlayers : players) {
					if (fcPlayers.lootshareEnabled()
							&& fcPlayers.getRegionId() == player.getRegionId()) {
						playersWithLs.add(fcPlayers);
					}
				}
				Player luckyPlayer = playersWithLs
						.get((int) (Math.random() * playersWithLs.size()));
				Engine.addGroundItem(item, new Tile(getCoordFaceX(size),
						getCoordFaceY(size), getZ()), luckyPlayer, false, 180,
						true);
				luckyPlayer.sendMessage(String.format(
						"<col=115b0d>You received: %sx %s.</col>",
						item.getAmount(), dropName));
				for (Player fcPlayers : playersWithLs) {
					if (!fcPlayers.equals(luckyPlayer)) {
						fcPlayers.sendMessage(String.format(
								"%s received: %sx %s.",
								luckyPlayer.getDisplayName(), item.getAmount(),
								dropName));
					}
				}
				return;
			}
		}
	}

	public void sendDrop(Player player, RingOfWealth drop) {
		Engine.addGroundItem(new Item(drop.getItemId(), drop.getAmount()),
				new Tile(getCoordFaceX(getSize()), getCoordFaceY(getSize()),
						getZ()), player, false, 180, true);
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final NPC target = this;
		if (hit.getDamage() > 0)
			Engine.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		user.heal(hit.getDamage() / 5);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0)
					Engine.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0,
							0);
			}
		}, 1);
	}

	@Override
	public void setAttackedBy(Entity target) {
		super.setAttackedBy(target);
		if (target == combat.getTarget()
				&& !(combat.getTarget() instanceof Familiar))
			lastAttackedByTarget = Utilities.currentTimeMillis();
	}

	public void setBonuses() {
		bonuses = NPCBonuses.getBonuses(id);
		if (bonuses == null) {
			bonuses = new int[10];
			int level = getCombatLevel();
			for (int i = 0; i < bonuses.length; i++)
				bonuses[i] = level;
		}
	}

	public void setCanBeAttackFromOutOfArea(boolean b) {
		canBeAttackFromOutOfArea = b;
	}

	public void setCantFollowUnderCombat(boolean canFollowUnderCombat) {
		this.cantFollowUnderCombat = canFollowUnderCombat;
	}

	public void setCantInteract(boolean cantInteract) {
		this.cantInteract = cantInteract;
		if (cantInteract)
			combat.reset();
	}

	public void setCapDamage(int capDamage) {
		this.capDamage = capDamage;
	}

	public void setCombatLevel(int level) {
		combatLevel = getDefinitions().combatLevel == level ? -1 : level;
		changedCombatLevel = true;
	}

	public void setForceAgressive(boolean forceAgressive) {
		this.forceAgressive = forceAgressive;
	}

	public void setForceFollowClose(boolean forceFollowClose) {
		this.forceFollowClose = forceFollowClose;
	}

	public void setForceMultiAttacked(boolean forceMultiAttacked) {
		this.forceMultiAttacked = forceMultiAttacked;
	}

	public void setForceTargetDistance(int forceTargetDistance) {
		this.forceTargetDistance = forceTargetDistance;
	}

	public void setForceWalk(Tile tile) {
		resetWalkSteps();
		forceWalk = tile;
	}

	/**
	 * Sets the locked.
	 * 
	 * @param locked
	 *            The locked to set.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setLureDelay(int lureDelay) {
		this.lureDelay = lureDelay;
	}

	public void setName(String string) {
		this.name = getDefinitions().name.equals(string) ? null : string;
		changedName = true;
	}

	public void setNoDistanceCheck(boolean noDistanceCheck) {
		this.noDistanceCheck = noDistanceCheck;
	}

	public void setNPC(int id) {
		this.id = id;
		setBonuses();
	}

	public void setRandomWalk(boolean forceRandomWalk) {
		this.randomwalk = forceRandomWalk;
	}

	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(respawnTile);
			finish();
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, getCombatDefinitions().getRespawnDelay() * 600,
				TimeUnit.MILLISECONDS);
	}

	public void setSecondBar(SecondBar secondBar) {
		this.secondBar = secondBar;
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}

	public void setTarget(Entity entity) {
		if (isForceWalking()) // if force walk not gonna get target
			return;
		combat.setTarget(entity);
		lastAttackedByTarget = Utilities.currentTimeMillis();
	}

	public void spawn() {
		setFinished(false);
		Engine.addNPC(this);
		setLastRegionId(0);
		Engine.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}

	@Override
	public String toString() {
		return getDefinitions().name + " - " + id + " - " + getX() + " "
				+ getY() + " " + getZ();
	}

	public void transformIntoNPC(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
	}

	public int whatDefender() {
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
		return id;
	}

	public boolean withinDistance(Player tile, int distance) {
		return super.withinDistance(tile, distance);
	}

	private Player owner;

	public void setMarkerPlantOwner(Player target) {
		this.owner = target;
	}

	public Player getMarkerPlantOwner() {
		return owner;
	}

	private boolean intelligentRouteFinder;

	public boolean isIntelligentRouteFinder() {
		return intelligentRouteFinder;
	}

	public void setIntelligentRouteFinder(boolean intelligentRouteFinder) {
		this.intelligentRouteFinder = intelligentRouteFinder;
	}

	public int getHpBarSize() {
		return this instanceof UnstableGlacyte ? 6
				: this instanceof CorporealBeast ? 3
						: this instanceof TzTok_Jad ? 3
								: this instanceof GeneralGraardor ? 4
										: this instanceof KreeArra ? 4
												: this instanceof CommanderZilyana ? 4
														: this instanceof KrilTstsaroth ? 4
																: this instanceof Nex ? 3
																		: this instanceof KingBlackDragon ? 3
																				: this instanceof QueenBlackDragon ? 3
																						: this instanceof HarAken ? 3
																								: 5;
	}
}