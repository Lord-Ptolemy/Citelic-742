package com.citelic.game.entity.player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.citelic.GameConstants;
import com.citelic.cores.CoresManager;
import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.LocalNPCUpdate;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.game.entity.npc.impl.glacor.Glacor;
import com.citelic.game.entity.npc.impl.glacor.GlacorEffect;
import com.citelic.game.entity.npc.impl.glacor.SappingGlacyte;
import com.citelic.game.entity.npc.impl.godwars.zaros.Nex;
import com.citelic.game.entity.npc.impl.others.GraveStone;
import com.citelic.game.entity.npc.impl.pet.Pet;
import com.citelic.game.entity.player.appearance.PlayerAppearance;
import com.citelic.game.entity.player.chat.ChatMessage;
import com.citelic.game.entity.player.chat.PublicChatMessage;
import com.citelic.game.entity.player.chat.QuickChatMessage;
import com.citelic.game.entity.player.containers.Inventory;
import com.citelic.game.entity.player.containers.Trade;
import com.citelic.game.entity.player.containers.bank.Bank;
import com.citelic.game.entity.player.containers.bank.BankPin;
import com.citelic.game.entity.player.content.MoneyPouch;
import com.citelic.game.entity.player.content.SkillCapeCustomizer;
import com.citelic.game.entity.player.content.Toolbelt;
import com.citelic.game.entity.player.content.actions.combat.CombatDefinitions;
import com.citelic.game.entity.player.content.actions.combat.PlayerCombat;
import com.citelic.game.entity.player.content.actions.consumables.Potions;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.construction.House;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.Dungeoneering;
import com.citelic.game.entity.player.content.actions.skills.farming.Farming;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.actions.skills.prayer.Prayer;
import com.citelic.game.entity.player.content.actions.skills.slayer.SlayerManager;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.ImpossibleJad;
import com.citelic.game.entity.player.content.controllers.impl.JailController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.DTController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightCaves;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightKiln;
import com.citelic.game.entity.player.content.controllers.impl.distractions.WarriorsGuild;
import com.citelic.game.entity.player.content.controllers.impl.distractions.battleterrace.BattleTerraceGame;
import com.citelic.game.entity.player.content.controllers.impl.distractions.bosses.BarrelchestController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.bosses.CorpBeastController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.bosses.NomadsRequiem;
import com.citelic.game.entity.player.content.controllers.impl.distractions.bosses.QueenBlackDragonController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.castlewars.CastleWarsPlaying;
import com.citelic.game.entity.player.content.controllers.impl.distractions.castlewars.CastleWarsWaiting;
import com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars.FfaZone;
import com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars.WarController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.crucible.CrucibleController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.fightpits.FightPitsArena;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.GodWars;
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.ZGDController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.PestControlGame;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.PestControlLobby;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena.DuelArena;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena.DuelRules;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.GameController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.LobbyController;
import com.citelic.game.entity.player.content.controllers.impl.events.DeathEvent;
import com.citelic.game.entity.player.content.miscellaneous.ClueScrolls;
import com.citelic.game.entity.player.content.miscellaneous.DominionTower;
import com.citelic.game.entity.player.content.miscellaneous.DwarfCannon;
import com.citelic.game.entity.player.content.miscellaneous.MarkerPlant;
import com.citelic.game.entity.player.content.miscellaneous.Notes;
import com.citelic.game.entity.player.content.miscellaneous.SquealOfFortune;
import com.citelic.game.entity.player.content.miscellaneous.pets.PetManager;
import com.citelic.game.entity.player.content.socialization.FriendChatsManager;
import com.citelic.game.entity.player.content.socialization.FriendsIgnores;
import com.citelic.game.entity.player.content.socialization.clans.ClansManager;
import com.citelic.game.entity.player.content.transportation.LodeStones;
import com.citelic.game.entity.player.item.FloorItem;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemConstants;
import com.citelic.game.entity.player.managers.ActionManager;
import com.citelic.game.entity.player.managers.AuraManager;
import com.citelic.game.entity.player.managers.ChargesManager;
import com.citelic.game.entity.player.managers.ControllerManager;
import com.citelic.game.entity.player.managers.CutscenesManager;
import com.citelic.game.entity.player.managers.DialogueManager;
import com.citelic.game.entity.player.managers.EmotesManager;
import com.citelic.game.entity.player.managers.HintIconsManager;
import com.citelic.game.entity.player.managers.HiscoreManager;
import com.citelic.game.entity.player.managers.InterfaceManager;
import com.citelic.game.entity.player.managers.LoyaltyManager;
import com.citelic.game.entity.player.managers.MusicsManager;
import com.citelic.game.entity.player.managers.PriceCheckManager;
import com.citelic.game.entity.player.managers.QuestManager;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.objects.OwnedObjectManager;
import com.citelic.game.map.pathfinding.RouteEvent;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.Session;
import com.citelic.networking.codec.decode.WorldPacketsDecoder;
import com.citelic.networking.codec.decode.impl.ButtonHandler;
import com.citelic.networking.codec.encode.WorldPacketsEncoder;
import com.citelic.utility.Logger;
import com.citelic.utility.LoggingSystem;
import com.citelic.utility.MachineInformation;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.Utilities;
import com.citelic.utility.cryptology.IsaacKeyPair;

public class Player extends Entity {

	private static final long serialVersionUID = 2011932556974180375L;

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1,
			RUN_MOVE_TYPE = 2;

	/*
	 * Basic Player Constants
	 */
	private transient boolean active;
	private long creationDate;
	private String displayName;
	private String email;
	private String lastIP;
	private long lastLoggedIn;
	private boolean oldItemsLook;
	private String password;
	private String recovAnswer;
	private String recovQuestion;
	private String registeredMac, currentMac;
	private long uniqueID;
	private int uniquePlayerId;
	private int rights;
	public int usedMacs;
	private int questPoints;
	private boolean allowChatEffects;
	public boolean hideServerMessages;
	public transient String username;
	private boolean usingJAG;
	
	/*
	 * Player Donator Constants
	 * 0 - Donator
	 * 1 - Extreme Donator
	 * 2 - Super Donator
	 * 3 - Wizard Donator
	 */
	private boolean[] donator = new boolean[4];
	
	/*
	 * Energy Constants
	 */
	private byte runEnergy;
	private transient boolean running;
	private transient boolean resting;
	private transient boolean listening; 
	
	/*
	 * Player Punishment Constants 
	 */
	private long banned;
	private boolean permBanned;
	private boolean permMuted;
	private long muted;
	private boolean macBanned;
	private long jailed;
	
	/*
	 * Entity Updating
	 */
	public transient LocalNPCUpdate localNPCUpdate = new LocalNPCUpdate(this);
	public transient LocalPlayerUpdate localPlayerUpdate = new LocalPlayerUpdate(
			this);
	
	/*
	 * Banking
	 */
	private final Bank bank;
	private BankPin pin;
	private final int[] bankpins = new int[] { 0, 0, 0, 0 };
	private final int[] confirmpin = new int[] { 0, 0, 0, 0 };
	private final int[] changeBankPin = new int[] { 0, 0, 0, 0 };
	
	/*
	 * Managers
	 */
	public transient PriceCheckManager priceCheckManager = new PriceCheckManager(
			this);
	public transient DialogueManager dialogueManager = new DialogueManager(this);
	public transient CutscenesManager cutscenesManager = new CutscenesManager(
			this);
	public transient InterfaceManager interfaceManager = new InterfaceManager(
			this);
	public transient HintIconsManager hintIconsManager = new HintIconsManager(
			this);
	public transient ActionManager actionManager = new ActionManager(this);
	public transient LoyaltyManager loyaltyManager = new LoyaltyManager(this);
	
	/*
	 * Juju
	 */
	private int[] juju = new int[6];
	
	/*
	 * Lodestones
	 */
	public Tile lodeStoneTile = null;
	private LodeStones lodeStone;
	private boolean[] activatedLodestones;
	
	/*
	 * Recipe for Disaster
	 * 0 - AgrithNaNa
	 * 1 - Karamel
	 * 2 - Dessourt
	 * 3 - FlamBeed
	 * 4 - Culinaromancer
	 */
	private boolean[] rfd = new boolean[5];
	
	/*
	 * Stronghold of Player Safety 
	 */
	public boolean[] playerSafety = new boolean[4];
	public boolean hasClaimedBoots;
	
	private int assistStatus;

	private int barbarianAdvancedLaps;
	private int barrowsKillCount;
	private int barsDone;
	public int battlePoints;
	public Player battleTarget;
	private transient long boneDelay;
	private int cannonBalls = 0;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	private transient boolean castedVeng;

	private String clanName;
	private int clanStatus;
	public transient boolean clientLoadedMapRegion;
	private int clueReward;
	private boolean completedFightCaves;
	private boolean completedFightKiln;
	private boolean completedRfd;
	private int[] completionistCapeCustomized;
	private boolean connectedClanChannel;
	
	private transient Runnable closeInterfacesEvent;
	private final ChargesManager charges;
	private transient ClansManager clanManager, guestClanManager;
	private final CombatDefinitions combatDefinitions;
	private final ControllerManager controllerManager;
	public DominionTower dominionTower = new DominionTower();
	public transient FriendChatsManager currentFriendChat;
	public Familiar familiar;
	private Farming farming;
	private final EmotesManager emotesManager;
	private final Equipment equipment;
	private final FriendsIgnores friendsIgnores;
	private PlayerAppearance playerAppearance;
	private DuelArena duelarena;
	public DwarfCannon dwarfCannon;
	public AuraManager auraManager = new AuraManager();
	public SquealOfFortune sof = new SquealOfFortune();
	private SlayerManager slayerManager;
	private transient RouteEvent routeEvent;
	public QuestManager questManager;
	public Notes notes;
	private final MusicsManager musicsManager;
	public PetManager petManager = new PetManager();
	public MoneyPouch moneyPouch;
	private MarkerPlant markerPlant;

	private int crucibleHighScore;
	private String currentFriendChatOwner;

	private boolean dfsActivated;
	private int dfscoolDown;
	private transient boolean disableEquip;
	public transient int displayMode;
	private long displayTime;
	private int dominionFactor;
	private int dungeoneeringTokens;
	public int[] fairyRingCombination = new int[3];

	private boolean filterGame;
	private transient boolean finishing;
	private long fireImmune;
	private transient long foodDelay;
	private boolean forceNextMapLoadRefresh;
	private int friendChatSetup;
	private boolean gmaulSpecCheapFix;
	private int gnomeAdvancedLaps;
	private boolean gotInfernoAdze;
	private int graveStone;
	public boolean hasAugury;
	private boolean hasCutEnoughLogs;
	private boolean hasCutMoreEnoughLogs;
	public boolean hasRenewalPrayer;
	public boolean hasRigour;
	public boolean hasScrollOfCleansing;
	public boolean hasScrollOfEfficiency;
	public boolean hasScrollOfLife;
	
	public boolean hasTut;
	private int hiddenBrother;

	private boolean hideSofInterface;
	private boolean hideWorldAnnouncements;
	
	private House house;
	private transient double hpBoostMultiplier;
	private boolean inAnimationRoom;
	public int[] increaseSlayedStatistics = new int[26];

	private final Inventory inventory;
	private transient boolean invulnerable;
	public transient IsaacKeyPair isaacKeyPair;

	private boolean isForumModerator;
	private boolean isGraphicDesigner;

	private boolean isInDefenderRoom;
	private boolean isSupporter;

	private boolean khalphiteLairEntranceSetted;
	private boolean khalphiteLairSetted;
	private int killCount, deathCount;
	private boolean[] killedBarrowBrothers;

	private boolean killedBork;
	private boolean killedQueenBlackDragon;
	private transient boolean largeSceneView;
	private int lastBonfire;
	public transient DuelRules lastDuelRules;

	private String lastMsg;
	private transient long lastPublicMessage;
	private long lastRequestSQL;
	private long lastWalkedMillis;
	public int lendMessage;
	private int loadedLogs;

	private transient long lockDelay;
	
	public Tile getLodeStoneTile() {
		return lodeStoneTile;
	}
	
	public void setLodeStoneTile(Tile tile) {
		this.lodeStoneTile = tile;
	}

	public transient ConcurrentLinkedQueue<LogicPacket> logicPackets;
	private int logsCut;
	private boolean lootshareEnabled;
	private int loyaltyPoints;

	private int magicLogsBurned;


	private int[] maxedCapeCustomized;

	private boolean mouseButtons;

	private final int[] openBankPin = new int[] { 0, 0, 0, 0 };
	public boolean openPin = false;
	public int ordinanceTimer;
	private int overloadDelay;
	private List<String> ownedObjectsManagerKeys;

	private String Owner = "";
	private transient long packetsDecoderPing;

	private int pestControlGames;
	private int pestPoints;

	public transient Pet pet;

	private int pkPoints;

	private long poisonImmune;

	private transient long polDelay;

	private transient long potDelay;

	private final int[] pouches;

	public Prayer prayer;
	private int prayerRenewalDelay;

	private int privateChatSetup;
	// game bar status
	private int publicStatus;
	
	private boolean reportOption;

	private int rocktailsCooked;

	private int runeSpanPoints;

	public transient int screenHeight;

	public transient int screenWidth;

	public String selectedClass;

	public transient Session session;

	public String setMutedBy, mutedReason;

	public boolean setPin = false;

	private final Skills skills;

	// skull sceptre
	private int skullChargesLeft = 0;

	private int skullDelay;

	private int skullId;

	private int slayerPoints;

	private transient boolean spawnsMode;

	private int specRestoreTimer;

	private int spins;

	public boolean starter = true;

	public boolean startpin = false;

	private int summoningLeftClickOption;

	private transient List<Integer> switchItemCache;

	private boolean talkedtoCook;

	private boolean talkedWithMarv;

	private int temporaryMovementType;

	private long thievingDelay;

	private transient boolean toggleLootShare;

	public Toolbelt toolbelt;

	private int totalNpcsKilledTask;

	public transient Trade trade = new Trade(this);

	private int tradeStatus;

	private boolean updateMovementType;


	private int vecnaTimer;

	private boolean verboseShopDisplayMode;

	private int votePoints;

	public int warriorKills, archerKills, mageKills;

	public int warriorLevel, archerLevel, mageLevel;

	private double[] warriorPoints;

	private boolean wonFightPits;

	private boolean xpLocked;

	private transient long yellDelay;

	private boolean yellDisabled;

	private boolean yellOff;

	private int zeals;

	public Player(String password, String mac) {
		super(GameConstants.START_PLAYER_LOCATION);
		setHitpoints(GameConstants.START_PLAYER_HITPOINTS);
		this.password = password;
		registeredMac = mac;
		setPlayerAppearance(new PlayerAppearance());
		toolbelt = new Toolbelt();
		inventory = new Inventory();
		equipment = new Equipment();
		moneyPouch = new MoneyPouch();
		skills = new Skills();
		combatDefinitions = new CombatDefinitions();
		prayer = new Prayer();
		bank = new Bank();
		house = new House();
		pin = new BankPin();
		lodeStone = new LodeStones();
		controllerManager = new ControllerManager();
		musicsManager = new MusicsManager();
		emotesManager = new EmotesManager();
		friendsIgnores = new FriendsIgnores();
		dominionTower = new DominionTower();
		charges = new ChargesManager();
		auraManager = new AuraManager();
		farming = new Farming();
		slayerManager = new SlayerManager();
		sof = new SquealOfFortune();
		questManager = new QuestManager();
		petManager = new PetManager();
		runEnergy = 100;
		allowChatEffects = true;
		mouseButtons = true;
		pouches = new int[4];
		increaseSlayedStatistics = new int[26];
		warriorPoints = new double[6];
		resetBarrows();
		SkillCapeCustomizer.resetSkillCapes(this);
		ownedObjectsManagerKeys = new LinkedList<String>();
		setCreationDate(Utilities.currentTimeMillis());
		uniqueID(Utilities.random(Long.MAX_VALUE - 1000000000));
	}

	public void addBoneDelay(long time) {
		boneDelay = time + Utilities.currentTimeMillis();
	}

	public void addCannonBalls(int cannonBalls) {
		this.cannonBalls += cannonBalls;
	}

	public void addDisplayTime(long i) {
		displayTime = i + Utilities.currentTimeMillis();
	}

	public void addFireImmune(long time) {
		fireImmune = time + Utilities.currentTimeMillis();
	}

	public void addFoodDelay(long time) {
		foodDelay = time + Utilities.currentTimeMillis();
	}

	public void addLogicPacketToQueue(LogicPacket packet) {
		for (final LogicPacket p : logicPackets) {
			if (p.getId() == packet.getId()) {
				logicPackets.remove(p);
				break;
			}
		}
		logicPackets.add(packet);
	}

	public void addPoisonImmune(long time) {
		poisonImmune = time + Utilities.currentTimeMillis();
		getPoison().reset();
	}

	public void addPolDelay(long delay) {
		polDelay = delay + Utilities.currentTimeMillis();
	}

	public void addPotDelay(long time) {
		potDelay = time + Utilities.currentTimeMillis();
	}

	public void addRunespanPoints(int points) {
		runeSpanPoints += points;
	}

	public void addWalkSteps(Tile worldTile) {
		this.addWalkSteps(worldTile.getX(), worldTile.getY());

	}

	public boolean canSpawn() {
		if (Wilderness.isAtWild(this)
				|| getControllerManager().getController() instanceof LobbyController
				|| getControllerManager().getController() instanceof GameController
				|| getControllerManager().getController() instanceof FightPitsArena
				|| getControllerManager().getController() instanceof CorpBeastController
				|| getControllerManager().getController() instanceof PestControlLobby
				|| getControllerManager().getController() instanceof PestControlGame
				|| getControllerManager().getController() instanceof ZGDController
				|| getControllerManager().getController() instanceof GodWars
				|| getControllerManager().getController() instanceof JailController
				|| getControllerManager().getController() instanceof DTController
				|| getControllerManager().getController() instanceof WarController
				|| getControllerManager().getController() instanceof DeathEvent
				|| getControllerManager().getController() instanceof BarrelchestController
				|| getControllerManager().getController() instanceof DuelArena
				|| getControllerManager().getController() instanceof CastleWarsPlaying
				|| getControllerManager().getController() instanceof CastleWarsWaiting
				|| getControllerManager().getController() instanceof FightCaves
				|| getControllerManager().getController() instanceof FightKiln
				|| getControllerManager().getController() instanceof ImpossibleJad
				|| getControllerManager().getController() instanceof BarrelchestController
				|| getControllerManager().getController() instanceof BattleTerraceGame
				|| FfaZone.inPvpArea(this)
				|| getControllerManager().getController() instanceof NomadsRequiem
				|| getControllerManager().getController() instanceof QueenBlackDragonController) {
			return false;
		}
		if (getControllerManager().getController() instanceof CrucibleController) {
			final CrucibleController controller = (CrucibleController) getControllerManager()
					.getController();
			return !controller.isInside();
		}
		return true;
	}

	public void checkMovement(int x, int y, int plane) {
		Magic.teleControlersCheck(this, new Tile(x, y, plane));
	}

	@Override
	public void checkMultiArea() {
		if (!isActive()) {
			return;
		}
		final boolean isAtMultiArea = isForceMultiArea() ? true : Engine
				.isMultiArea(this);
		if (isAtMultiArea && !isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 1);
		} else if (!isAtMultiArea && isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 0);
		}
	}

	public int checkTotalLevel(int checktotal) {
		checktotal = 0;
		for (int i = 0; i < 25; i++) {
			checktotal += getSkills().getLevel(i);
		}
		return checktotal;
	}

	public boolean clientHasLoadedMapRegion() {
		return clientLoadedMapRegion;
	}

	public void closeInterfaces() {
		if (interfaceManager.containsScreenInter()) {
			interfaceManager.closeScreenInterface();
		}
		if (interfaceManager.containsInventoryInter()) {
			interfaceManager.closeInventoryInterface();
		}
		dialogueManager.finishDialogue();
		if (closeInterfacesEvent != null) {
			closeInterfacesEvent.run();
			closeInterfacesEvent = null;
		}
		getInterfaceManager().closeChatBoxInterface();
	}

	public void disableLootShare() {
		if (isToggleLootShare()) {
			toggleLootShare();
		}
	}

	public void drainRunEnergy() {
		setRunEnergy(runEnergy - 1);
	}

	@Override
	public void finish() {
		finish(0);
	}

	public void finish(final int tryCount) {
		if (finishing || hasFinished()) {
			return;
		}
		finishing = true;
		stopAll(false, true,
				!(actionManager.getAction() instanceof PlayerCombat));
		if (isUnderCombat(tryCount) || getEmotesManager().isDoingEmote()
				|| isLocked() || isDead()) {
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						packetsDecoderPing = Utilities.currentTimeMillis();
						finishing = false;
						finish(tryCount + 1);
					} catch (final Throwable e) {
						Logger.handle(e);
					}
				}
			}, 10, TimeUnit.SECONDS);
			return;
		}
		realFinish();
	}

	public void forceLogout() {
		getPackets().sendLogout();
		setRunning(false);
		realFinish();
	}

	public void forceSession() {
		setRunning(false);
		realFinish();
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public boolean[] getActivatedLodestones() {
		return activatedLodestones;
	}

	public PlayerAppearance getAppearence() {
		return playerAppearance;
	}

	public int getAssistStatus() {
		return assistStatus;
	}

	public Object getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public AuraManager getAuraManager() {
		return auraManager;
	}

	public Bank getBank() {
		return bank;
	}

	public BankPin getBankPin() {
		return pin;
	}

	public long getBanned() {
		return banned;
	}

	public int getBarbarianAdvancedLaps() {
		return barbarianAdvancedLaps;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int getBarsDone() {
		return barsDone;
	}

	public long getBoneDelay() {
		return boneDelay;
	}

	public int getCannonBalls() {
		return cannonBalls;
	}

	public int[] getChangeBankPin() {
		return changeBankPin;
	}

	public ChargesManager getCharges() {
		return charges;
	}

	public ClansManager getClanManager() {
		return clanManager;
	}

	public String getClanName() {
		return clanName;
	}

	public int getClanStatus() {
		return clanStatus;
	}

	public int getClueReward() {
		return clueReward;
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public int[] getConfirmPin() {
		return confirmpin;
	}

	public ControllerManager getControllerManager() {
		return controllerManager;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public int getCrucibleHighScore() {
		return crucibleHighScore;
	}

	public FriendChatsManager getCurrentFriendChat() {
		return currentFriendChat;
	}

	public String getCurrentFriendChatOwner() {
		return currentFriendChatOwner;
	}

	public String getCurrentMac() {
		return currentMac;
	}

	public CutscenesManager getCutscenesManager() {
		return cutscenesManager;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public int getdfscoolDown() {
		return dfscoolDown;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public String getDisplayName() {
		if (displayName != null) {
			return displayName;
		}
		return Utilities.formatPlayerNameForDisplay(username);
	}

	public long getDisplayTime() {
		return displayTime;
	}

	public int getDominionFactor() {
		return dominionFactor;
	}

	public DominionTower getDominionTower() {
		return dominionTower;
	}

	public DuelArena getDuelArena() {
		return duelarena;
	}

	public int getDungeoneeringTokens() {
		return dungeoneeringTokens;
	}

	public DwarfCannon getDwarfCannon() {
		// TODO Auto-generated method stub
		return dwarfCannon;
	}

	public String getEmailAttached() {
		return email;
	}

	public EmotesManager getEmotesManager() {
		return emotesManager;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public int[] getFairyRingCombination() {
		return fairyRingCombination;
	}

	public Familiar getFamiliar() {
		return familiar;
	}

	public Farming getFarming() {
		return farming;
	}

	public long getFireImmune() {
		return fireImmune;
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	public PlayerAppearance getPlayerAppearance() {
		return playerAppearance;
	}

	public PlayerAppearance getPlayerAppearances() {
		return playerAppearance;
	}

	public boolean getGmaulSpecCheapFix() {
		return gmaulSpecCheapFix;
	}

	public int getGnomeAdvancedLaps() {
		return gnomeAdvancedLaps;
	}

	public int getGraveStone() {
		return graveStone;
	}

	public ClansManager getGuestClanManager() {
		return guestClanManager;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public House getHouse() {
		return house;
	}

	public double getHpBoostMultiplier() {
		return hpBoostMultiplier;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public IsaacKeyPair getIsaacKeyPair() {
		return isaacKeyPair;
	}

	public long getJailed() {
		return jailed;
	}

	public int getKillCount() {
		return killCount;
	}

	public boolean[] getKilledBarrowBrothers() {
		return killedBarrowBrothers;
	}

	public int getLastBonfire() {
		return lastBonfire;
	}

	public DuelRules getLastDuelRules() {
		return lastDuelRules;
	}

	public String getLastHostname() {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(getLastIP());
			final String hostname = addr.getHostName();
			return hostname;
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getLastIP() {
		return lastIP;
	}

	public long getLastLoggedIn() {
		// TODO Auto-generated method stub
		return lastLoggedIn;
	}

	public String getLastMsg() {
		return lastMsg;
	}

	public long getLastPublicMessage() {
		return lastPublicMessage;
	}
	
	private long lastCommand;
	
	public long getLastCommand() {
		return lastCommand;
	}
	
	private long lastJoined;
	
	public long getLastJoined() {
		return lastJoined;
	}

	public long getLastRequestSQL() {
		// TODO Auto-generated method stub
		return lastRequestSQL;
	}

	public long getLastWalked() {
		// TODO Auto-generated method stub
		return lastWalkedMillis;
	}

	public int getLoadedLogs() {
		return loadedLogs;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public long getLockDelay() {
		return lockDelay;
	}

	public LodeStones getLodeStones() {
		return lodeStone;
	}

	public int getLogsCut() {
		return logsCut;
	}

	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	public int getMagicLogsBurned() {
		return magicLogsBurned;
	}

	public MarkerPlant getMarkerPlant() {
		return markerPlant;
	}

	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	@Override
	public int getMaxHitpoints() {
		return skills.getLevel(Skills.HITPOINTS) * 10
				+ equipment.getEquipmentHpIncrease();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	public int getMessageIcon() {
		return getRights() == 2 || getRights() == 1 ? getRights()
				: isForumModerator() ? 10 : isSupporter() ? 4
						: isExtremeDonator() ? 9 : isDonator() ? 8
								: getRights();
	}

	public MoneyPouch getMoneyPouch() {
		return moneyPouch;
	}

	public int getMovementType() {
		if (getTemporaryMoveType() != -1) {
			return getTemporaryMoveType();
		}
		return getRun() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
	}

	public long getMuted() {
		return muted;
	}

	public Notes getNotes() {
		return notes;
	}

	public int[] getOpenBankPin() {
		return openBankPin;
	}

	public boolean getOpenedPin() {
		return openPin;
	}

	public int getOverloadDelay() {
		return overloadDelay;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (ownedObjectsManagerKeys == null) {
			ownedObjectsManagerKeys = new LinkedList<String>();
		}
		return ownedObjectsManagerKeys;
	}

	public String getOwner() {
		return Owner;
	}

	public WorldPacketsEncoder getPackets() {
		return session.getWorldPackets();
	}

	public long getPacketsDecoderPing() {
		return packetsDecoderPing;
	}

	public String getPassword() {
		return password;
	}

	public int getPestControlGames() {
		return pestControlGames;
	}

	public int getPestPoints() {
		return pestPoints;
	}

	/**
	 * Gets the pet.
	 *
	 * @return The pet.
	 */
	public Pet getPet() {
		return pet;
	}

	/**
	 * Gets the petManager.
	 *
	 * @return The petManager.
	 */
	public PetManager getPetManager() {
		return petManager;
	}

	public int[] getPin() {
		return bankpins;
	}

	public int getPkPoints() {
		return pkPoints;
	}

	public long getPoisonImmune() {
		return poisonImmune;
	}

	public long getPolDelay() {
		return polDelay;
	}

	public long getPotDelay() {
		return potDelay;
	}

	public int[] getPouches() {
		return pouches;
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public long getPrayerDelay() {
		final Long teleblock = (Long) getTemporaryAttributtes().get(
				"PrayerBlocked");
		if (teleblock == null) {
			return 0;
		}
		return teleblock;
	}
	
	public PriceCheckManager getPriceCheckManager() {
		return priceCheckManager;
	}

	public int getPrivateChatSetup() {
		return privateChatSetup;
	}

	public int getPublicStatus() {
		return publicStatus;
	}

	public QuestManager getQuestManager() {
		return questManager;
	}

	public int getQuestPoints() {
		// TODO Auto-generated method stub
		return questPoints;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	public String getRecovAnswer() {
		return recovAnswer;
	}

	public String getRecovQuestion() {
		return recovQuestion;
	}

	public String getRegisteredMac() {
		return registeredMac;
	}

	public int getRights() {
		return rights;
	}

	public int getRocktailsCooked() {
		return rocktailsCooked;
	}

	public byte getRunEnergy() {
		return runEnergy;
	}

	/**
	 * @return the runeSpanPoint
	 */
	public int getRuneSpanPoints() {
		return runeSpanPoints;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public Session getSession() {
		return session;
	}

	public boolean getSetPin() {
		return setPin;
	}

	@Override
	public int getSize() {
		return getPlayerAppearances().getSize();
	}

	public Skills getSkills() {
		return skills;
	}

	public int getSkullChargesLeft() {
		return skullChargesLeft;
	}

	public int getSkullId() {
		return skullId;
	}

	public SlayerManager getSlayerManager() {
		return slayerManager;
	}

	public int getSlayerPoints() {
		return slayerPoints;
	}

	public int[] getSlayerStatistics() {
		return increaseSlayedStatistics;
	}

	public int getSpecRestoreTimer() {
		return specRestoreTimer;
	}

	public int getSpins() {
		return spins;
	}

	public SquealOfFortune getSquealOfFortune() {
		return sof;
	}

	public int getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public List<Integer> getSwitchItemCache() {
		return switchItemCache;
	}

	public long getTeleBlockDelay() {
		final Long teleblock = (Long) getTemporaryAttributtes().get(
				"TeleBlocked");
		if (teleblock == null) {
			return 0;
		}
		return teleblock;
	}

	public int getTemporaryMoveType() {
		return temporaryMovementType;
	}

	public long getThievingDelay() {
		// TODO Auto-generated method stub
		return thievingDelay;
	}

	public Toolbelt getToolbelt() {
		return toolbelt;
	}

	public int getTotalNpcsKilledTask() {
		return totalNpcsKilledTask;
	}

	public Trade getTrade() {
		return trade;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public int getUniquePlayerId() {
		return uniquePlayerId;
	}

	public String getUsername() {
		return username;
	}

	public boolean getUsingJAG() {
		// TODO Auto-generated method stub
		return usingJAG;
	}

	public int getVecnaTimer() {
		return vecnaTimer;
	}

	public int getVotePoints() {
		return votePoints;
	}

	public double[] getWarriorPoints() {
		return warriorPoints;
	}

	public long getYellDelay() {
		return yellDelay;
	}

	@Override
	public int getZ() {
		return plane;
	}

	public int getZeals() {
		return zeals;
	}

	public void gmaulSpecCheapFix(boolean b) {
		gmaulSpecCheapFix = b;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE
				&& hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE) {
			return;
		}
		if (invulnerable) {
			hit.setDamage(0);
			return;
		}
		if (auraManager.usingPenance()) {
			final int amount = (int) (hit.getDamage() * 0.2);
			if (amount > 0) {
				prayer.restorePrayer(amount);
			}
		}
		final Entity source = hit.getSource();
		if (source == null) {
			return;
		}
		if (polDelay > Utilities.currentTimeMillis()) {
			hit.setDamage((int) (hit.getDamage() * 0.5));
		}
		if (prayer.hasPrayersOn() && hit.getDamage() != 0) {
			if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				if (prayer.usingPrayer(0, 17)) {
					hit.setDamage((int) (hit.getDamage() * source
							.getMagePrayerMultiplier()));
				} else if (prayer.usingPrayer(1, 7)) {
					final int deflectedDamage = source instanceof Nex ? 0
							: (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source
							.getMagePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage,
								HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2228));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				if (prayer.usingPrayer(0, 18)) {
					hit.setDamage((int) (hit.getDamage() * source
							.getRangePrayerMultiplier()));
				} else if (prayer.usingPrayer(1, 8)) {
					final int deflectedDamage = source instanceof Nex ? 0
							: (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source
							.getRangePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage,
								HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2229));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				if (prayer.usingPrayer(0, 19)) {
					hit.setDamage((int) (hit.getDamage() * source
							.getMeleePrayerMultiplier()));
				} else if (prayer.usingPrayer(1, 9)) {
					final int deflectedDamage = source instanceof Nex ? 0
							: (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source
							.getMeleePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage,
								HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2230));
						setNextAnimation(new Animation(12573));
					}
				}
			}
		}
		if (hit.getDamage() >= 200) {
			if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				final int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MELEE_BONUS]
						/ 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage,
							HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				final int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_RANGE_BONUS]
						/ 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage,
							HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				final int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MAGE_BONUS]
						/ 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage,
							HitLook.ABSORB_DAMAGE));
				}
			}
		}
		final int shieldId = equipment.getShieldId();
		if (shieldId == 13742 || shieldId == 23699) { // elsyian
			if (Utilities.getRandom(100) <= 70) {
				hit.setDamage((int) (hit.getDamage() * 0.75));
			}
		} else if (shieldId == 13740 || shieldId == 23698) { // divine
			final int drain = (int) (Math.ceil(hit.getDamage() * 0.3) / 2);
			if (prayer.getPrayerpoints() >= drain) {
				hit.setDamage((int) (hit.getDamage() * 0.70));
				prayer.drainPrayer(drain);
			}
		}
		if (castedVeng && hit.getDamage() >= 4) {
			castedVeng = false;
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			source.applyHit(new Hit(this, (int) (hit.getDamage() * 0.75),
					HitLook.REGULAR_DAMAGE));
		}
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			if (p2.prayer.hasPrayersOn()) {
				if (p2.prayer.usingPrayer(0, 24)) { // smite
					final int drain = hit.getDamage() / 4;
					if (drain > 0) {
						prayer.drainPrayer(drain);
					}
				} else {
					if (hit.getDamage() == 0) {
						return;
					}
					if (!p2.prayer.isBoostedLeech()) {
						if (hit.getLook() == HitLook.MELEE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 19)) {
								if (Utilities.getRandom(4) == 0) {
									p2.prayer.increaseTurmoilBonus(this);
									p2.prayer.setBoostedLeech(true);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 1)) { // sap att
								if (Utilities.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(0)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your sap curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(0);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Attack from the enemy, boosting your Attack.",
														true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2214));
									p2.prayer.setBoostedLeech(true);
									Engine.sendProjectile(p2, this, 2215, 35,
											35, 20, 5, 0, 0);
									EngineTaskManager.schedule(
											new EngineTask() {
												@Override
												public void run() {
													setNextGraphics(new Graphics(
															2216));
												}
											}, 1);
									return;
								}
							} else {
								if (p2.prayer.usingPrayer(1, 10)) {
									if (Utilities.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(3)) {
											p2.getPackets()
													.sendGameMessage(
															"Your opponent has been weakened so much that your leech curse has no effect.",
															true);
										} else {
											p2.prayer.increaseLeechBonus(3);
											p2.getPackets()
													.sendGameMessage(
															"Your curse drains Attack from the enemy, boosting your Attack.",
															true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										Engine.sendProjectile(p2, this, 2231,
												35, 35, 20, 5, 0, 0);
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
								if (p2.prayer.usingPrayer(1, 14)) {
									if (Utilities.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(7)) {
											p2.getPackets()
													.sendGameMessage(
															"Your opponent has been weakened so much that your leech curse has no effect.",
															true);
										} else {
											p2.prayer.increaseLeechBonus(7);
											p2.getPackets()
													.sendGameMessage(
															"Your curse drains Strength from the enemy, boosting your Strength.",
															true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										Engine.sendProjectile(p2, this, 2248,
												35, 35, 20, 5, 0, 0);
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
							if (p2.prayer.usingPrayer(1, 2)) { // sap range
								if (Utilities.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(1)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your sap curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(1);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Range from the enemy, boosting your Range.",
														true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2217));
									p2.prayer.setBoostedLeech(true);
									Engine.sendProjectile(p2, this, 2218, 35,
											35, 20, 5, 0, 0);
									EngineTaskManager.schedule(
											new EngineTask() {
												@Override
												public void run() {
													setNextGraphics(new Graphics(
															2219));
												}
											}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 11)) {
								if (Utilities.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(4)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your leech curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(4);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Range from the enemy, boosting your Range.",
														true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									Engine.sendProjectile(p2, this, 2236, 35,
											35, 20, 5, 0, 0);
									EngineTaskManager
											.schedule(new EngineTask() {
												@Override
												public void run() {
													setNextGraphics(new Graphics(
															2238));
												}
											});
									return;
								}
							}
						}
						if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 3)) { // sap mage
								if (Utilities.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(2)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your sap curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(2);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Magic from the enemy, boosting your Magic.",
														true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2220));
									p2.prayer.setBoostedLeech(true);
									Engine.sendProjectile(p2, this, 2221, 35,
											35, 20, 5, 0, 0);
									EngineTaskManager.schedule(
											new EngineTask() {
												@Override
												public void run() {
													setNextGraphics(new Graphics(
															2222));
												}
											}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 12)) {
								if (Utilities.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(5)) {
										p2.getPackets()
												.sendGameMessage(
														"Your opponent has been weakened so much that your leech curse has no effect.",
														true);
									} else {
										p2.prayer.increaseLeechBonus(5);
										p2.getPackets()
												.sendGameMessage(
														"Your curse drains Magic from the enemy, boosting your Magic.",
														true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									Engine.sendProjectile(p2, this, 2240, 35,
											35, 20, 5, 0, 0);
									EngineTaskManager.schedule(
											new EngineTask() {
												@Override
												public void run() {
													setNextGraphics(new Graphics(
															2242));
												}
											}, 1);
									return;
								}
							}
						}

						// overall

						if (p2.prayer.usingPrayer(1, 13)) { // leech defence
							if (Utilities.getRandom(10) == 0) {
								if (p2.prayer.reachedMax(6)) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.prayer.increaseLeechBonus(6);
									p2.getPackets()
											.sendGameMessage(
													"Your curse drains Defence from the enemy, boosting your Defence.",
													true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2244, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2246));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 15)) {
							if (Utilities.getRandom(10) == 0) {
								if (getRunEnergy() <= 0) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.setRunEnergy(p2.getRunEnergy() > 90 ? 100
											: p2.getRunEnergy() + 10);
									setRunEnergy(p2.getRunEnergy() > 10 ? getRunEnergy() - 10
											: 0);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2256, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2258));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 16)) {
							if (Utilities.getRandom(10) == 0) {
								if (combatDefinitions
										.getSpecialAttackPercentage() <= 0) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
								} else {
									p2.combatDefinitions.restoreSpecialAttack();
									combatDefinitions
											.desecreaseSpecialAttack(10);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								Engine.sendProjectile(p2, this, 2252, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2254));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 4)) { // sap spec
							if (Utilities.getRandom(10) == 0) {
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2223));
								p2.prayer.setBoostedLeech(true);
								if (combatDefinitions
										.getSpecialAttackPercentage() <= 0) {
									p2.getPackets()
											.sendGameMessage(
													"Your opponent has been weakened so much that your sap curse has no effect.",
													true);
								} else {
									combatDefinitions
											.desecreaseSpecialAttack(10);
								}
								Engine.sendProjectile(p2, this, 2224, 35, 35,
										20, 5, 0, 0);
								EngineTaskManager.schedule(new EngineTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2225));
									}
								}, 1);
								return;
							}
						}
					}
				}
			}
		} else {
			final NPC n = (NPC) source;
			if (prayer.usingPrayer(1, 19)) {
				if (Utilities.getRandom(4) == 0) {
					prayer.increaseTurmoilBonus(n);
					prayer.setBoostedLeech(true);
				}
			}
			if (n.getId() == 13448) {
				sendSoulSplit(hit, n);
			}
			if (n instanceof Glacor) {
				final Glacor glacor = (Glacor) n;
				if (glacor.getEffect() == GlacorEffect.SAPPING) {
					getPrayer().drainPrayer(
							(int) Utilities.getPercent(10, getPrayer()
									.getPrayerpoints()));
				}
			} else if (n instanceof SappingGlacyte) {
				getPrayer().drainPrayer(
						(int) Utilities.getPercent(5, getPrayer()
								.getPrayerpoints()));
			}
		}
	}

	public boolean hasClueScroll() {
		for (final int scroll : ClueScrolls.ScrollIds) {
			if (getInventory().containsItem(scroll)) {
				return true;
			}
			if (getBank().containsItem(scroll, 1)) {
				;
			}
			return true;
		}
		return false;
	}

	public boolean hasDisabledYell() {
		return yellDisabled;
	}

	public boolean hasDisplayName() {
		return displayName != null;
	}

	public boolean hasInstantSpecial(final int weaponId) {
		switch (weaponId) {
		case 4153:
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
		case 1377:
		case 13472:
		case 35:// Excalibur
		case 8280:
		case 14632:
			return true;
		default:
			return false;
		}
	}

	public boolean hasJujuFarmingBoost() {
		return juju[0] > 1;
	}

	public boolean hasJujuFishingBoost() {
		return juju[1] > 1;
	}

	public boolean hasJujuGodBoost() {
		return juju[2] > 1;
	}

	public boolean hasJujuMiningBoost() {
		return juju[3] > 1;
	}

	public boolean hasJujuScentlessBoost() {
		return juju[4] > 1;
	}

	public boolean hasJujuWoodcuttingBoost() {
		return juju[5] > 1;
	}

	public boolean hasLargeSceneView() {
		return largeSceneView;
	}

	public boolean hasSkull() {
		return skullDelay > 0;
	}

	public boolean hasTalkedtoCook() {
		return talkedtoCook;
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public boolean hideSofInterface() {
		// TODO Auto-generated method stub
		return hideSofInterface;
	}

	public void increaseCrucibleHighScore() {
		crucibleHighScore++;
	}

	public void increaseZeals(int zeals) {
		this.zeals += zeals;
	}

	public void init(Player player, Session session, String username,
			String mac, int displayMode, int screenWidth, int screenHeight,
			MachineInformation machineInformation, IsaacKeyPair isaacKeyPair) {
		this.session = session;
		this.username = username;
		this.displayMode = displayMode;
		setCurrentMac(mac);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.isaacKeyPair = isaacKeyPair;
		if (getNotes() == null) {
			notes = new Notes();
		}
		if (slayerManager == null) {
			skills.resetSkillNoRefresh(Skills.SLAYER);
			slayerManager = new SlayerManager();

		}
		if (lodeStone == null) {
			lodeStone = new LodeStones();
		}
		if (activatedLodestones == null) {
			activatedLodestones = new boolean[16];
		}
		if (toolbelt == null) {
			toolbelt = new Toolbelt();
		}
		if (house == null) {
			house = new House();
		}
		if (pin == null) {
			pin = new BankPin();
		}
		if (moneyPouch == null) {
			moneyPouch = new MoneyPouch();
		}
		interfaceManager = new InterfaceManager(this);
		dialogueManager = new DialogueManager(this);
		loyaltyManager = new LoyaltyManager(this);
		hintIconsManager = new HintIconsManager(this);
		priceCheckManager = new PriceCheckManager(this);
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		actionManager = new ActionManager(this);
		cutscenesManager = new CutscenesManager(this);
		dwarfCannon = new DwarfCannon(this);
		pin = new BankPin();
		sof = new SquealOfFortune();
		petManager = new PetManager();
		auraManager = new AuraManager();
		dominionTower = new DominionTower();
		trade = new Trade(this);
		if (getPlayerAppearance() == null) {
			setPlayerAppearance(new PlayerAppearance());
		}
		getPlayerAppearance().setPlayer(this);
		getInventory().setPlayer(this);
		getEquipment().setPlayer(this);
		getDwarfCannon().setPlayer(this);
		getSkills().setPlayer(this);
		getCombatDefinitions().setPlayer(this);
		slayerManager.setPlayer(this);
		moneyPouch.setPlayer(this);
		getPrayer().setPlayer(this);
		house.setPlayer(this);
		getBank().setPlayer(this);
		pin.setPlayer(this);
		getControllerManager().setPlayer(this);
		getMusicsManager().setPlayer(this);
		getEmotesManager().setPlayer(this);
		toolbelt.setPlayer(this);
		getFriendsIgnores().setPlayer(this);
		dominionTower.setPlayer(this);
		auraManager.setPlayer(this);
		lodeStone = new LodeStones();
		lodeStone.setPlayer(this);
		sof.setPlayer(this);
		getNotes().setPlayer(this);
		getCharges().setPlayer(this);
		questManager.setPlayer(this);
		petManager.setPlayer(this);
		farming.initializePatches();
		setDirection(Utilities.getFaceDirection(0, -1));
		fairyRingCombination = new int[3];
		warriorCheck();
		setTemporaryMoveType(-1);
		logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
		setSwitchItemCache(Collections
				.synchronizedList(new ArrayList<Integer>()));
		initEntity();
		Engine.addPlayer(this);
		setPacketsDecoderPing(Utilities.currentTimeMillis());
		Engine.updateEntityRegion(this);
	}

	public void init(Session session, String string, IsaacKeyPair isaacKeyPair) {
		username = string;
		this.session = session;
		this.isaacKeyPair = isaacKeyPair;
		Engine.addLobbyPlayer(this);// .addLobbyPlayer(this);
		SerializableFilesManager.savePlayer(this);
		if (GameConstants.DEBUG) {
			Logger.log(this,
					new StringBuilder("Lobby Inited Player: ").append(string)
							.append(", pass: ").append(password).toString());
		}
	}

	public boolean isActive() {
		return active;
	}

	public boolean isAdmin() {
		return isAdministrator() ? false : getRights() == 1;
	}

	public boolean isAdministrator() {
		boolean o = false;
		for (final String owner : GameConstants.ADMINISTRATORS) {
			if (getUsername().equalsIgnoreCase(owner)) {
				o = true;
				break;
			}
		}
		return o;
	}

	public boolean isApeAtoll() {
		return getX() >= 2693 && getX() <= 2821 && getY() >= 2693
				&& getY() <= 2817;
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public boolean isCantTrade() {
		return cantTrade;
	}

	public boolean isCastVeng() {
		return castedVeng;
	}

	public boolean isCompletedFightCaves() {
		return completedFightCaves;
	}

	public boolean isCompletedFightKiln() {
		return completedFightKiln;
	}

	public boolean isCompletedRfd() {
		return completedRfd;
	}

	public boolean isConnectedClanChannel() {
		return connectedClanChannel;
	}

	public boolean isDfsActivated() {
		return dfsActivated;
	}

	public boolean isDonator() {
		return donator[0] || donator[1] || donator[2] || donator[3];
	}

	public boolean isEquipDisabled() {
		return disableEquip;
	}

	public boolean isExtremeDonator() {
		return donator[1] || donator[2] || donator[3];
	}
	
	public boolean isWizardDonator() {
		return donator[3];
	}
	
	public boolean isSuperDonator() {
		return donator[3] || donator[2];
	}

	public boolean isFilterGame() {
		return filterGame;
	}

	public boolean isFixedScreen() {
		return displayMode < 2;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public boolean isForumModerator() {
		return isForumModerator;
	}

	public boolean isGotInfernoAdze() {
		return gotInfernoAdze;
	}

	public boolean isGraphicDesigner() {
		return isGraphicDesigner;
	}

	public boolean isHasCutEnoughLogs() {
		return hasCutEnoughLogs;
	}

	public boolean isHasCutMoreEnoughLogs() {
		return hasCutMoreEnoughLogs;
	}

	public boolean isHidden() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isHideServerMessages() {
		return hideServerMessages;
	}

	public boolean isHidingWorldMessages() {
		return hideWorldAnnouncements;
	}

	public boolean isInAnimationRoom() {
		return inAnimationRoom;
	}

	public boolean isInDefenderRoom() {
		return isInDefenderRoom;
	}

	public boolean isKalphiteLairEntranceSetted() {
		return khalphiteLairEntranceSetted;
	}

	public boolean isKalphiteLairSetted() {
		return khalphiteLairSetted;
	}

	public boolean isKilledAgrithNaNa() {
		return rfd[0];
	}

	public boolean isKilledBork() {
		return killedBork;
	}

	/*
	 * Recipe for Disaster
	 * 0 - AgrithNaNa
	 * 1 - Karamel
	 * 2 - Dessourt
	 * 3 - FlamBeed
	 * 4 - Culinaromancer
	 */
	public boolean isKilledCulinaromancer() {
		return rfd[4];
	}

	public boolean isKilledDessourt() {
		return rfd[2];
	}

	public boolean isKilledFlambeed() {
		return rfd[3];
	}

	public boolean isKilledKaramel() {
		return rfd[1];
	}

	/**
	 * Gets the killedQueenBlackDragon.
	 *
	 * @return The killedQueenBlackDragon.
	 */
	public boolean isKilledQueenBlackDragon() {
		return killedQueenBlackDragon;
	}

	public boolean isListening() {
		return resting;
	}

	public boolean isLocked() {
		return lockDelay >= Utilities.currentTimeMillis();
	}

	public boolean isMacBanned() {
		return macBanned;
	}

	public boolean isOldItemsLook() {
		return oldItemsLook;
	}

	public boolean isPermBanned() {
		return permBanned;
	}

	public boolean isPermMuted() {
		return permMuted;
	}

	public boolean isResting() {
		return resting;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isSpawnsMode() {
		return spawnsMode;
	}

	public boolean isStarter() {
		return starter;
	}

	public boolean isSupporter() {
		return isSupporter;
	}

	public boolean isTalkedWithMarv() {
		return talkedWithMarv;
	}

	public boolean isToggleLootShare() {
		return toggleLootShare;
	}

	public boolean isUnderCombat(int tryCount) {
		return getAttackedByDelay() + 10000 > Utilities.currentTimeMillis()
				&& tryCount < 6 ? true : false;
	}

	public boolean isUpdateMovementType() {
		return updateMovementType;
	}

	public boolean isUsingReportOption() {
		return reportOption;
	}

	public boolean isWonFightPits() {
		return wonFightPits;
	}

	public boolean isXpLocked() {
		return xpLocked;
	}

	public boolean isYellOff() {
		return yellOff;
	}

	public void kickPlayerFromFriendsChannel(String name) {
		if (currentFriendChat == null) {
			return;
		}
		currentFriendChat.kickPlayerFromChat(this, name);
	}

	@Override
	public void loadMapRegions() {
		final boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		clientLoadedMapRegion = false;
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicMapRegion(!isActive());
			if (!wasAtDynamicRegion) {
				localNPCUpdate.reset();
			}
		} else {
			getPackets().sendMapRegion();
			if (wasAtDynamicRegion) {
				localNPCUpdate.reset();
			}
		}
		forceNextMapLoadRefresh = false;
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void lock(long time) {
		lockDelay = Utilities.currentTimeMillis() + time * 600;
	}

	/**
	 * Logs the player out.
	 *
	 * @param lobby
	 *            If we're logging out to the lobby.
	 */
	public void logout(boolean lobby) {
		if (!isRunning()) {
			return;
		}
		if (isUnderCombat(10)) {
			getPackets()
					.sendGameMessage(
							"You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (getEmotesManager().isDoingEmote()) {
			sendMessage("You can't log out while performing an emote.");
			return;
		}
		if (isLocked()) {
			sendMessage("You can't log out while performing an action.");
			return;
		}
		if (markerPlant != null) {
			markerPlant.logged();
		}
		getPackets().sendLogout();
		setRunning(false);
	}

	public boolean lootshareEnabled() {
		return lootshareEnabled;
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != -1
				|| isUpdateMovementType();
	}

	public void ordinanceTimer() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				if (ordinanceTimer > 0) {
					ordinanceTimer--;
				}
				if (ordinanceTimer == 0) {
					getPackets()
							.sendGameMessage(
									"<col=FFCC00>You are able to take more items from the ordinance.");
					cancel();
				}
			}
		}, 0, 1);
	}

	public void performInstantSpecial(final int weaponId) {
		int specAmt = PlayerCombat.getSpecialAmmount(weaponId);
		if (combatDefinitions.hasRingOfVigour()) {
			specAmt *= 0.9;
		}
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			sendMessage("You don't have enough power left.");
			combatDefinitions.desecreaseSpecialAttack(0);
			return;
		}
		if (getSwitchItemCache().size() > 0) {
			ButtonHandler.submitSpecialRequest(this);
			return;
		}
		switch (weaponId) {
		case 4153:
			combatDefinitions.setInstantAttack(true);
			combatDefinitions.switchUsingSpecialAttack();
			final Entity target = (Entity) getTemporaryAttributtes().get(
					"last_target");
			if (target != null
					&& target.getTemporaryAttributtes().get("last_attacker") == this) {
				if (!(getActionManager().getAction() instanceof PlayerCombat)
						|| ((PlayerCombat) getActionManager().getAction())
								.getTarget() != target) {
					getActionManager().setAction(new PlayerCombat(target));
				}
			}
			gmaulSpecCheapFix(true);
			break;
		case 1377:
		case 13472:
			setNextAnimation(new Animation(1056));
			setNextGraphics(new Graphics(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			final int defence = (int) (skills.getLevelForXp(Skills.DEFENCE) * 0.90D);
			final int attack = (int) (skills.getLevelForXp(Skills.ATTACK) * 0.90D);
			final int range = (int) (skills.getLevelForXp(Skills.RANGE) * 0.90D);
			final int magic = (int) (skills.getLevelForXp(Skills.MAGIC) * 0.90D);
			final int strength = (int) (skills.getLevelForXp(Skills.STRENGTH) * 1.2D);
			skills.set(Skills.DEFENCE, defence);
			skills.set(Skills.ATTACK, attack);
			skills.set(Skills.RANGE, range);
			skills.set(Skills.MAGIC, magic);
			skills.set(Skills.STRENGTH, strength);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			break;
		case 35:// Excalibur
		case 8280:
		case 14632:
			setNextAnimation(new Animation(1168));
			setNextGraphics(new Graphics(247));
			final boolean enhanced = weaponId == 14632;
			skills.set(
					Skills.DEFENCE,
					enhanced ? (int) (skills.getLevelForXp(Skills.DEFENCE) * 1.15D)
							: skills.getLevel(Skills.DEFENCE) + 8);
			EngineTaskManager.schedule(new EngineTask() {
				int count = 5;

				@Override
				public void run() {
					if (isDead() || hasFinished()
							|| getHitpoints() >= getMaxHitpoints()) {
						stop();
						return;
					}
					heal(enhanced ? 80 : 40);
					if (count-- == 0) {
						stop();
						return;
					}
				}
			}, 4, 2);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			break;
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
			setNextAnimation(new Animation(12804));
			setNextGraphics(new Graphics(2319));// 2320
			setNextGraphics(new Graphics(2321));
			addPolDelay(60000);
			combatDefinitions.desecreaseSpecialAttack(specAmt);
			break;
		}
	}

	public void print(String string) {
		getPackets().sendGameMessage(string, true);

	}

	@Override
	public void processEntity() {
		processLogicPackets();
		if (routeEvent != null && routeEvent.processEvent(this)) {
			routeEvent = null;
		}
		super.processEntity();
		cutscenesManager.process();
		charges.process();
		auraManager.process();
		actionManager.process();
		prayer.processPrayer();
		controllerManager.process();
		if (isDead()) {
			return;
		}
		if (musicsManager.musicEnded()) {
			musicsManager.replayMusic();
		}
		if (hasSkull()) {
			skullDelay--;
			if (!hasSkull()) {
				getPlayerAppearances().generateAppearenceData();
			}
		}
		if (polDelay != 0 && polDelay <= Utilities.currentTimeMillis()) {
			getPackets()
					.sendGameMessage(
							"The power of the light fades. Your resistance to melee attacks return to normal.");
			polDelay = 0;
		}
		if (juju[3] > 0) {
			if (juju[3] == 50) {
				getPackets()
						.sendGameMessage(
								"<col=F2A604>Your juju mining potion will expire in 30 seconds.");
			}
			if (juju[3] == 1) {
				getPackets().sendGameMessage(
						"<col=F2A604>Your juju mining potion has worn off.");
			}
			juju[3]--;
		}
		if (juju[1] > 0) {
			if (juju[1] == 50) {
				getPackets()
						.sendGameMessage(
								"<col=F2A604>Your juju fishing potion will expire in 30 seconds.");
			}
			if (juju[1] == 1) {
				getPackets().sendGameMessage(
						"<col=F2A604>Your juju fishing potion has worn off.");
			}
			juju[1]--;
		}
		if (juju[0] > 0) {
			if (juju[0] == 50) {
				getPackets()
						.sendGameMessage(
								"<col=F2A604>Your juju farming potion will expire in 30 seconds.");
			}
			if (juju[0] == 1) {
				getPackets().sendGameMessage(
						"<col=F2A604>Your juju farming potion has worn off.");
			}
			juju[0]--;
		}
		if (juju[5] > 0) {
			if (juju[5] == 50) {
				getPackets()
						.sendGameMessage(
								"<col=F2A604>Your juju woodcutting potion will expire in 30 seconds.");
			}
			if (juju[5] == 1) {
				getPackets()
						.sendGameMessage(
								"<col=F2A604>Your juju woodcutting potion has worn off.");
			}
			juju[5]--;
		}
		if (juju[4] > 0) {
			if (juju[4] == 50) {
				getPackets()
						.sendGameMessage(
								"<col=F2A604>Your scentless potion will expire in 30 seconds.");
			}
			if (juju[4] == 1) {
				getPackets().sendGameMessage(
						"<col=F2A604>Your scentless potion has worn off.");
			}
			juju[4]--;
		}
		if (juju[2] > 0) {
			if (juju[2] == 50) {
				getPackets()
						.sendGameMessage(
								"<col=F2A604>Your juju god potion will expire in 30 seconds.");
			}
			if (juju[2] == 1) {
				getPackets().sendGameMessage(
						"<col=F2A604>Your juju god potion has worn off.");
			}
			juju[2]--;
		}
		if (overloadDelay > 0) {
			if (overloadDelay == 1 || isDead()) {
				Potions.resetOverLoadEffect(this);
				return;
			} else if ((overloadDelay - 1) % 25 == 0) {
				Potions.applyOverLoadEffect(this);
			}
			overloadDelay--;
		}
		if (prayerRenewalDelay > 0) {
			if (prayerRenewalDelay == 1 || isDead()) {
				sendMessage("<col=0000FF>Your prayer renewal has ended.");
				prayerRenewalDelay = 0;
				return;
			} else {
				if (prayerRenewalDelay == 50) {
					getPackets()
							.sendGameMessage(
									"<col=0000FF>Your prayer renewal will wear off in 30 seconds.");
				}
				if (!prayer.hasFullPrayerpoints()) {
					getPrayer().restorePrayer(1);
					if ((prayerRenewalDelay - 1) % 25 == 0) {
						setNextGraphics(new Graphics(1295));
					}
				}
			}
			prayerRenewalDelay--;
		}
		if (dfscoolDown > 0) {
			dfscoolDown--;
		}
		if (specRestoreTimer > 0) {
			specRestoreTimer--;
		}
		if (yellDelay > 0) {
			yellDelay--;
		}
		if (lastBonfire > 0) {
			lastBonfire--;
			if (lastBonfire == 500) {
				getPackets()
						.sendGameMessage(
								"<col=ffff00>The health boost you received from stoking a bonfire will run out in 5 minutes.");
			} else if (lastBonfire == 0) {
				getPackets()
						.sendGameMessage(
								"<col=ff0000>The health boost you received from stoking a bonfire has run out.");
				equipment.refreshConfigs(false);
			}
		}
	}

	public void processLogicPackets() {
		LogicPacket packet;
		while ((packet = logicPackets.poll()) != null) {
			WorldPacketsDecoder.decodeLogicPacket(this, packet);
		}
	}

	@Override
	public void processReceivedHits() {
		if (isLocked()) {
			return;
		}
		super.processReceivedHits();
	}

	public void realFinish() {
		if (hasFinished()) {
			return;
		}
		stopAll();
		if (getRights() != 2) {
			HiscoreManager.saveHighScore(this);
		}
		house.finish();
		cutscenesManager.logout();
		controllerManager.logout();
		setRunning(false);
		friendsIgnores.sendFriendsMyStatus(false);
		if (currentFriendChat != null) {
			currentFriendChat.leaveChat(this, true);
		}
		if (clanManager != null) {
			clanManager.disconnect(this, false);
		}
		if (guestClanManager != null) {
			guestClanManager.disconnect(this, true);
		}
		if (familiar != null && !familiar.isFinished()) {
			familiar.dissmissFamiliar(true);
		}
		if (getDwarfCannon().hasCannon()) {
			getDwarfCannon().pickUpDwarfCannon(0, getDwarfCannon().getObject());
		}
		if (slayerManager.getSocialPlayer() != null) {
			slayerManager.resetSocialGroup(true);
		} else if (pet != null) {
			pet.finish();
		}
		setFinished(true);
		session.setDecoder(-1);
		lastLoggedIn = System.currentTimeMillis();
		SerializableFilesManager.savePlayer(this);
		Engine.updateEntityRegion(this);
		Engine.removePlayer(this);
		if (GameConstants.DEBUG) {
			Logger.log(this, "Finished Player: " + username + ", pass: "
					+ password);
		}
	}

	public void refreshAllowChatEffects() {
		getPackets().sendConfig(171, allowChatEffects ? 0 : 1);
	}

	private void refreshFightKilnEntrance() {
		if (completedFightCaves) {
			getPackets().sendConfigByFile(10838, 1);
		}
	}

	public void refreshHitPoints() {
		getPackets().sendConfigByFile(7198, getHitpoints());
	}

	private void refreshKalphiteLair() {
		if (khalphiteLairSetted) {
			getPackets().sendConfigByFile(7263, 1);
		}
	}

	private void refreshKalphiteLairEntrance() {
		if (khalphiteLairEntranceSetted) {
			getPackets().sendConfigByFile(7262, 1);
		}
	}

	private void refreshLodestoneNetwork() {
	}

	public void refreshMouseButtons() {
		getPackets().sendConfig(170, mouseButtons ? 0 : 1);
	}

	public void refreshOtherChatsSetup() {
		final int value = friendChatSetup << 6;
		getPackets().sendConfig(1438, value);
	}

	public void refreshPrivateChatSetup() {
		getPackets().sendConfig(287, privateChatSetup);
	}

	public void refreshReportOption() {
		// TODO Auto-generated method stub
		getPackets().sendConfig(1056, isUsingReportOption() ? 2 : 0);
	}

	public void refreshSpawnedItems() {
		for (final int regionId : getMapRegionsIds()) {
			final List<FloorItem> floorItems = Engine.getRegion(regionId)
					.getFloorItems();
			if (floorItems == null) {
				continue;
			}
			for (final FloorItem item : floorItems) {
				if ((item.isInvisible() || item.isGrave())
						&& this != item.getOwner()
						|| item.getTile().getZ() != getZ()) {
					continue;
				}
				getPackets().sendRemoveGroundItem(item);
			}
		}
		for (final int regionId : getMapRegionsIds()) {
			final List<FloorItem> floorItems = Engine.getRegion(regionId)
					.getFloorItems();
			if (floorItems == null) {
				continue;
			}
			for (final FloorItem item : floorItems) {
				if ((item.isInvisible() || item.isGrave())
						&& this != item.getOwner()
						|| item.getTile().getZ() != getZ()) {
					continue;
				}
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (final int regionId : getMapRegionsIds()) {
			final List<GameObject> spawnedObjects = Engine.getRegion(regionId)
					.getSpawnedObjects();
			if (spawnedObjects != null) {
				for (final GameObject object : spawnedObjects) {
					if (object.getZ() == getZ()) {
						getPackets().sendSpawnedObject(object);
					}
				}
			}
			final List<GameObject> removedObjects = Engine.getRegion(regionId)
					.getRemovedObjects();
			if (removedObjects != null) {
				for (final GameObject object : removedObjects) {
					if (object.getZ() == getZ()) {
						getPackets().sendDestroyObject(object);
					}
				}
			}
		}
	}

	public void refreshToggleLootShare() {
		// need to force cuz autoactivates when u click on it even if no chat
		getPackets().sendConfigByFile(4071, toggleLootShare ? 1 : 0);
	}

	public void refreshVerboseShopDisplayMode() {
		getPackets().sendConfigByFile(11055, verboseShopDisplayMode ? 0 : 1);
	}

	public void refreshWarriorPoints(int index) {
		getPackets().sendConfigByFile(index + 8662, (int) warriorPoints[index]);
	}

	public void removeCannonBalls() {
		cannonBalls = 0;
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		refreshHitPoints();
	}

	public void removeSkull() {
		skullDelay = -1;
		getPlayerAppearances().generateAppearenceData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		refreshHitPoints();
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		resting = false;
		listening = false;
		skullDelay = 0;
		foodDelay = 0;
		potDelay = 0;
		poisonImmune = 0;
		fireImmune = 0;
		castedVeng = false;
		setDfsActivated(false);
		dfscoolDown = 0;
		if (getLastBonfire() > 0) {
			getPackets()
					.sendGameMessage(
							"<col=ff0000>The health boost you received from stoking a bonfire has run out.");
		}
		setLastBonfire(0);
		getEquipment().refreshConfigs(false);
		if (getOverloadDelay() > 0) {
			Potions.resetOverLoadEffect(this);
		}
		setRunEnergy(100);
		removeDamage(this);
		getPlayerAppearances().generateAppearenceData();
	}

	public void resetBankPins() {
		setPin = false;
		openPin = false;
		startpin = false;
		for (int i = 0; i < 4; i++) {
			bankpins[i] = 0;
			changeBankPin[i] = 0;
			confirmpin[i] = 0;
			openBankPin[i] = 0;
		}
	}

	public void resetBarrows() {
		hiddenBrother = -1;
		killedBarrowBrothers = new boolean[7]; // includes new bro for future
		// use
		barrowsKillCount = 0;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		temporaryMovementType = -1;
		setUpdateMovementType(false);
		if (!clientHasLoadedMapRegion()) {
			// load objects and items here
			setClientHasLoadedMapRegion();
			refreshSpawnedObjects();
			refreshSpawnedItems();
		}
	}

	@Override
	public boolean restoreHitPoints() {
		final boolean update = super.restoreHitPoints();
		if (update) {
			if (prayer.usingPrayer(0, 9)) {
				super.restoreHitPoints();
			}
			if (resting || listening) {
				super.restoreHitPoints();
			}
			refreshHitPoints();
		}
		return update;
	}

	public void restoreRunEnergy() {
		if (getNextRunDirection() == -1 && runEnergy < 100) {
			runEnergy++;
			if (resting && runEnergy < 100) {
				runEnergy++;
			}
			if (listening && runEnergy < 99) {
				runEnergy += 2;
			}
			getPackets().sendRunEnergy();
		}
	}

	public void run() {
		if (Engine.exiting_start != 0) {
			final int delayPassed = (int) ((Utilities.currentTimeMillis() - Engine.exiting_start) / 1000);
			getPackets().sendSystemUpdate(Engine.exiting_delay - delayPassed);
		}
		if (starter) {
			FriendChatsManager.joinChat("ridiculous", this);
			getDialogueManager().startDialogue("StarterD");
		}
		getPackets().sendGameMessage(
				"Welcome to " + GameConstants.SERVER_NAME + ".");
		// TODO Auto-generated method stub
		getFarming().updateAllPatches(this);
		getPackets().sendItemsLook();
		getPackets().sendConfig(281, 1000);
		getPackets().sendConfigByFile(6774, 1);
		getInterfaceManager().sendInterfaces();
		getSquealOfFortune().refreshSqueal();
		getPackets().sendRunEnergy();
		refreshAllowChatEffects();
		refreshMouseButtons();
		moneyPouch.init();
		refreshReportOption();
		house.init();
		refreshPrivateChatSetup();
		refreshOtherChatsSetup();
		sendRunButtonConfig();
		sendDefaultPlayersOptions();
		checkMultiArea();
		getInventory().init();
		getEquipment().init();
		getSkills().init();
		sendByFiles();
		getCombatDefinitions().init();
		getPrayer().init();
		getFriendsIgnores().init();
		refreshHitPoints();
		getNotes().init();
		getEmotesManager().init();
		getPrayer().refreshPrayerPoints();
		getPoison().refresh();
		getPackets().sendGameBarStages();
		getMusicsManager().init();
		getQuestManager().init();
		sendUnlockedObjectConfigs();
		// GPI
		startpin = false;
		openPin = false;
		setRunning(true);
		setUpdateMovementType(true);
		getPlayerAppearance().generateAppearenceData();
		getControllerManager().login();
		OwnedObjectManager.linkKeys(this);
		LoggingSystem.logIP(this);
		getLodeStones().checkActivation();
		toggleLootShare(false);

		// friend chat connect
		if (getCurrentFriendChatOwner() != null) {
			FriendChatsManager.joinChat(getCurrentFriendChatOwner(), this);
			if (getCurrentFriendChat() == null) {
				setCurrentFriendChatOwner(null);
			}
		}
		if (getClanName() != null) {
			if (!ClansManager.connectToClan(this, getClanName(), false)) {
				setClanName(null);
			}
		}
		if (getFamiliar() != null) {
			getFamiliar().respawnFamiliar(this);
		} else {
			getPetManager().init();
		}
		vecnaTimer(getVecnaTimer());
		if (isAdministrator() && !isAdmin()) {
			setRights(2);
		}
	}

	public void sendByFiles() {
		getPackets().sendConfigByFile(6276, 1); // Locks plaques (Jail)
		getPackets().sendConfigByFile(6278, playerSafety[0] ? 1 : 0); // Poster
		getPackets().sendConfigByFile(4500, playerSafety[1] ? 1 : 0); // Lever
		getPackets().sendConfigByFile(4499, playerSafety[2] ? 1 : 0); // Chest
	}

	public void sendClanChannelMessage(ChatMessage message) {
		if (clanManager == null) {
			return;
		}
		clanManager.sendMessage(this, message);
	}

	public void sendClanChannelQuickMessage(QuickChatMessage message) {
		if (clanManager == null) {
			return;
		}
		clanManager.sendQuickMessage(this, message);
	}

	@Override
	public void sendDeath(final Entity source) {
		if (prayer.hasPrayersOn()
				&& getTemporaryAttributtes().get("startedDuel") != Boolean.TRUE) {
			if (prayer.usingPrayer(0, 22)) {
				setNextGraphics(new Graphics(437));
				final Player target = this;
				if (isAtMultiArea()) {
					for (final int regionId : getMapRegionsIds()) {
						final List<Integer> playersIndexes = Engine.getRegion(
								regionId).getPlayerIndexes();
						if (playersIndexes != null) {
							for (final int playerIndex : playersIndexes) {
								final Player player = Engine.getPlayers().get(
										playerIndex);
								if (player == null
										|| player.isDead()
										|| player.hasFinished()
										|| !player.withinDistance(this, 1)
										|| !player.isCanPvp()
										|| !target.getControllerManager()
												.canHit(player)) {
									continue;
								}
								player.applyHit(new Hit(
										target,
										Utilities.getRandom((int) (skills
												.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
						final List<Integer> npcsIndexes = Engine.getRegion(
								regionId).getNPCsIndexes();
						if (npcsIndexes != null) {
							for (final int npcIndex : npcsIndexes) {
								final NPC npc = Engine.getNPCs().get(npcIndex);
								if (npc == null
										|| npc.isDead()
										|| npc.hasFinished()
										|| !npc.withinDistance(this, 1)
										|| !npc.getDefinitions()
												.hasAttackOption()
										|| !target.getControllerManager()
												.canHit(npc)) {
									continue;
								}
								npc.applyHit(new Hit(
										target,
										Utilities.getRandom((int) (skills
												.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
					}
				} else {
					if (source != null && source != this && !source.isDead()
							&& !source.hasFinished()
							&& source.withinDistance(this, 1)) {
						source.applyHit(new Hit(target, Utilities
								.getRandom((int) (skills
										.getLevelForXp(Skills.PRAYER) * 2.5)),
								HitLook.REGULAR_DAMAGE));
					}
				}
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX() - 1, target.getY(),
										target.getZ()));
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX() + 1, target.getY(),
										target.getZ()));
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX(), target.getY() - 1,
										target.getZ()));
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX(), target.getY() + 1,
										target.getZ()));
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX() - 1, target.getY() - 1,
										target.getZ()));
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX() - 1, target.getY() + 1,
										target.getZ()));
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX() + 1, target.getY() - 1,
										target.getZ()));
						Engine.sendGraphics(target, new Graphics(438),
								new Tile(target.getX() + 1, target.getY() + 1,
										target.getZ()));
					}
				});
			} else if (prayer.usingPrayer(1, 17)) {
				Engine.sendProjectile(this, new Tile(getX() + 2, getY() + 2,
						getZ()), 2260, 24, 0, 41, 35, 30, 0);
				Engine.sendProjectile(this,
						new Tile(getX() + 2, getY(), getZ()), 2260, 41, 0, 41,
						35, 30, 0);
				Engine.sendProjectile(this, new Tile(getX() + 2, getY() - 2,
						getZ()), 2260, 41, 0, 41, 35, 30, 0);

				Engine.sendProjectile(this, new Tile(getX() - 2, getY() + 2,
						getZ()), 2260, 41, 0, 41, 35, 30, 0);
				Engine.sendProjectile(this,
						new Tile(getX() - 2, getY(), getZ()), 2260, 41, 0, 41,
						35, 30, 0);
				Engine.sendProjectile(this, new Tile(getX() - 2, getY() - 2,
						getZ()), 2260, 41, 0, 41, 35, 30, 0);

				Engine.sendProjectile(this,
						new Tile(getX(), getY() + 2, getZ()), 2260, 41, 0, 41,
						35, 30, 0);
				Engine.sendProjectile(this,
						new Tile(getX(), getY() - 2, getZ()), 2260, 41, 0, 41,
						35, 30, 0);
				final Player target = this;
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						setNextGraphics(new Graphics(2259));

						if (isAtMultiArea()) {
							for (final int regionId : getMapRegionsIds()) {
								final List<Integer> playersIndexes = Engine
										.getRegion(regionId).getPlayerIndexes();
								if (playersIndexes != null) {
									for (final int playerIndex : playersIndexes) {
										final Player player = Engine
												.getPlayers().get(playerIndex);
										if (player == null
												|| player.isDead()
												|| player.hasFinished()
												|| !player.isCanPvp()
												|| !player.withinDistance(
														target, 2)
												|| !target
														.getControllerManager()
														.canHit(player)) {
											continue;
										}
										player.applyHit(new Hit(
												target,
												Utilities.getRandom(skills
														.getLevelForXp(Skills.PRAYER) * 3),
												HitLook.REGULAR_DAMAGE));
									}
								}
								final List<Integer> npcsIndexes = Engine
										.getRegion(regionId).getNPCsIndexes();
								if (npcsIndexes != null) {
									for (final int npcIndex : npcsIndexes) {
										final NPC npc = Engine.getNPCs().get(
												npcIndex);
										if (npc == null
												|| npc.isDead()
												|| npc.hasFinished()
												|| !npc.withinDistance(target,
														2)
												|| !npc.getDefinitions()
														.hasAttackOption()
												|| !target
														.getControllerManager()
														.canHit(npc)) {
											continue;
										}
										npc.applyHit(new Hit(
												target,
												Utilities.getRandom(skills
														.getLevelForXp(Skills.PRAYER) * 3),
												HitLook.REGULAR_DAMAGE));
									}
								}
							}
						} else {
							if (source != null && source != target
									&& !source.isDead()
									&& !source.hasFinished()
									&& source.withinDistance(target, 2)) {
								source.applyHit(new Hit(
										target,
										Utilities.getRandom(skills
												.getLevelForXp(Skills.PRAYER) * 3),
										HitLook.REGULAR_DAMAGE));
							}
						}

						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() + 2, getY() + 2, getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() + 2, getY(), getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() + 2, getY() - 2, getZ()));

						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() - 2, getY() + 2, getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() - 2, getY(), getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() - 2, getY() - 2, getZ()));

						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX(), getY() + 2, getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX(), getY() - 2, getZ()));

						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() + 1, getY() + 1, getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() + 1, getY() - 1, getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() - 1, getY() + 1, getZ()));
						Engine.sendGraphics(target, new Graphics(2260),
								new Tile(getX() - 1, getY() - 1, getZ()));
					}
				});
			}
		}
		setNextAnimation(new Animation(-1));
		if (!controllerManager.sendDeath()) {
			return;
		}
		lock(7);
		stopAll();
		if (familiar != null) {
			familiar.sendDeath(this);
		}
		final Tile deathTile = new Tile(this);
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					controllerManager.startController("DeathEvent", deathTile,
							hasSkull());
				} else if (loop == 4) {
					getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void sendDefaultPlayersOptions() {
		getPackets().sendPlayerOption("Follow", 2, false);
		getPackets().sendPlayerOption("Trade with", 4, false);
	}

	public void sendFriendsChannelMessage(String message) {
		if (currentFriendChat == null) {
			return;
		}
		currentFriendChat.sendMessage(this, message);
	}

	public void sendFriendsChannelQuickMessage(QuickChatMessage message) {
		if (currentFriendChat == null) {
			return;
		}
		currentFriendChat.sendQuickMessage(this, message);
	}

	public void sendGuestClanChannelMessage(ChatMessage message) {
		if (guestClanManager == null) {
			return;
		}
		guestClanManager.sendMessage(this, message);
	}

	public void sendGuestClanChannelQuickMessage(QuickChatMessage message) {
		if (guestClanManager == null) {
			return;
		}
		guestClanManager.sendQuickMessage(this, message);
	}

	/*
	 * default items on death, now only used for wilderness
	 */
	public void sendItemsOnDeath(Player killer) {
		sendItemsOnDeath(killer, hasSkull());
	}

	/*
	 * default items on death, now only used for wilderness
	 */
	public void sendItemsOnDeath(Player killer, boolean dropItems) {
		final Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(this,
				true, dropItems, getPrayer().isProtectingItem());
		sendItemsOnDeath(killer, new Tile(this), new Tile(this), true, slots);
	}

	public void sendItemsOnDeath(Player killer, Tile deathTile,
			Tile respawnTile, boolean wilderness, Integer[][] slots) {
		charges.die(slots[1], slots[3]); // degrades droped and lost items only
		auraManager.removeAura();
		final Item[][] items = GraveStone.getItemsKeptOnDeath(this, slots);
		inventory.reset();
		equipment.reset();
		getPlayerAppearances().generateAppearenceData();
		for (final Item item : items[0]) {
			if (item != null) {
				inventory.addItemDrop(item.getId(), item.getAmount(),
						respawnTile);
			}
		}
		if (items[1].length != 0) {
			if (wilderness) {
				for (final Item item : items[1]) {
					Engine.addGroundItem(item, deathTile, killer == null ? this
							: killer, true, 60, 0);
				}
			} else {
				new GraveStone(this, deathTile, items[1]);
			}
		}
	}

	public void sendLobbyConfigs(Player player) {
		getPackets().sendConfig(2411, 1);
		getPackets().sendConfig(2528, 5359015);
		getPackets().sendConfig(2567, 52);
	}

	public void sendMessage(String message) {
		getPackets().sendGameMessage(message);
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (final int regionId : getMapRegionsIds()) {
			final List<Integer> playersIndexes = Engine.getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null) {
				continue;
			}
			for (final Integer playerIndex : playersIndexes) {
				final Player p = Engine.getPlayers().get(playerIndex);
				if (p == null
						|| !p.isActive()
						|| p.hasFinished()
						|| p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] == null) {
					continue;
				}
				p.getPackets().sendPublicMessage(this, message);
			}
		}
	}

	public void sendRandomJail(Player p) {
		p.resetWalkSteps();
		switch (Utilities.getRandom(2)) {
		case 0:
			p.setNextTile(new Tile(3230, 3407, 0));
			break;
		case 1:
			p.setNextTile(new Tile(3228, 3407, 0));
			break;
		case 2:
			p.setNextTile(new Tile(3226, 3407, 0));
			break;
		}
	}

	public void sendRunButtonConfig() {
		getPackets().sendConfig(173,
				resting ? 3 : listening ? 4 : getRun() ? 1 : 0);
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final Player target = this;
		if (hit.getDamage() > 0) {
			Engine.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		}
		user.heal(hit.getDamage() / 5);
		prayer.drainPrayer(hit.getDamage() / 5);
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0) {
					Engine.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0,
							0);
				}
			}
		}, 0);
	}

	public void sendTeleother(int x, int y, int z) {
		Magic.sendTeleotherTeleportSpell(this, 0, 0, new Tile(x, y, z));
		this.stopAll();
	}

	public void sendTeleport(int x, int y, int z) {
		Magic.sendNormalTeleportSpell(this, 0, 0, new Tile(x, y, z));
		this.stopAll();
	}

	public void sendUnlockedObjectConfigs() {
		refreshKalphiteLairEntrance();
		refreshKalphiteLair();
		refreshLodestoneNetwork();
		refreshFightKilnEntrance();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAssistStatus(int assistStatus) {
		this.assistStatus = assistStatus;
	}

	public void setBanned(long banned) {
		this.banned = banned;
	}

	public void setBarbarianAdvancedLaps(int barbarianAdvancedLaps) {
		this.barbarianAdvancedLaps = barbarianAdvancedLaps;
	}

	public int setBarrowsKillCount(int barrowsKillCount) {
		return this.barrowsKillCount = barrowsKillCount;
	}

	public void setBarsDone(int barsDone) {
		this.barsDone = barsDone;
	}

	public void setCanPvp(boolean canPvp) {
		this.canPvp = canPvp;
		getPlayerAppearances().generateAppearenceData();
		getPackets().sendPlayerOption(canPvp ? "Attack" : "null", 1, true);
		getPackets().sendPlayerUnderNPCPriority(canPvp);
	}

	public void setCantTrade(boolean canTrade) {
		cantTrade = canTrade;
	}

	public void setCastVeng(boolean castVeng) {
		castedVeng = castVeng;
	}

	public void setClanManager(ClansManager clanManager) {
		this.clanManager = clanManager;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public void setClanStatus(int clanStatus) {
		this.clanStatus = clanStatus;
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = true;
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = false;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public void setClueReward(int clueReward) {
		this.clueReward = clueReward;
	}

	public void setCompletedFightCaves() {
		if (!completedFightCaves) {
			completedFightCaves = true;
			refreshFightKilnEntrance();
		}
	}

	public void setCompletedFightKiln() {
		completedFightKiln = true;
	}

	public void setCompletedRfd() {
		completedRfd = true;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		completionistCapeCustomized = skillcapeCustomized;
	}

	public void setConnectedClanChannel(boolean connectedClanChannel) {
		this.connectedClanChannel = connectedClanChannel;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public void setCurrentFriendChat(FriendChatsManager currentFriendChat) {
		this.currentFriendChat = currentFriendChat;
	}

	public void setCurrentFriendChatOwner(String currentFriendChatOwner) {
		this.currentFriendChatOwner = currentFriendChatOwner;
	}

	public void setCurrentMac(String currentMac) {
		this.currentMac = currentMac;
	}

	public int setDeathCount(int deathCount) {
		return this.deathCount = deathCount;
	}

	public void setDefenderRoom(boolean isInDefenderRoom) {
		this.isInDefenderRoom = isInDefenderRoom;
	}

	public void setDfsActivated(boolean dfsActivated) {
		this.dfsActivated = dfsActivated;
	}

	public void setdfscoolDown(int dfscoolDown) {
		this.dfscoolDown = dfscoolDown;
	}

	public void setDisableEquip(boolean equip) {
		disableEquip = equip;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDominionFactor(int dominionFactor) {
		this.dominionFactor = dominionFactor;
	}

	public void setDonator(int i, boolean status) {
		switch (i) {
		case 0: 
			this.donator[0] = status;
			break;
		case 1:
			this.donator[1] = status;
			break;
		case 2:
			this.donator[2] = status;
			break;
		case 3:
			this.donator[3] = status;
			break;
		}
	}

	public void setDungeoneeringTokens(int dungeoneeringTokens) {
		this.dungeoneeringTokens = dungeoneeringTokens;
	}

	public void setEmailAttached(String email) {
		this.email = email;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public void setFarming(Farming farming) {
		this.farming = farming;
	}

	public void setFightPitsSkull() {
		skullDelay = Integer.MAX_VALUE;
		skullId = 1;
		getPlayerAppearances().generateAppearenceData();
	}

	public void setFilterGame(boolean filterGame) {
		this.filterGame = filterGame;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
	}

	public void setForumModerator(boolean isForumModerator) {
		this.isForumModerator = isForumModerator;
	}

	public void setFriendChatSetup(int friendChatSetup) {
		this.friendChatSetup = friendChatSetup;
	}

	public void setPlayerAppearance(PlayerAppearance globalPlayerUpdater) {
		this.playerAppearance = globalPlayerUpdater;
	}

	public void setGnomeAdvancedLaps(int gnomeAdvancedLaps) {
		this.gnomeAdvancedLaps = gnomeAdvancedLaps;
	}

	public void setGotInfernoAdze(boolean gotInfernoAdze) {
		this.gotInfernoAdze = gotInfernoAdze;
	}

	public void setGraphicDesigner(boolean isGraphicDesigner) {
		this.isGraphicDesigner = isGraphicDesigner;
	}

	public void setGraveStone(int graveStone) {
		this.graveStone = graveStone;
	}

	public void setGuestClanManager(ClansManager guestClanManager) {
		this.guestClanManager = guestClanManager;
	}

	public void setHasCutEnoughLogs(boolean hasCutEnoughLogs) {
		this.hasCutEnoughLogs = hasCutEnoughLogs;
	}

	public void setHasCutMoreEnoughLogs(boolean hasCutMoreEnoughLogs) {
		this.hasCutMoreEnoughLogs = hasCutMoreEnoughLogs;
	}

	public void setHiddenBrother(int hiddenBrother) {
		this.hiddenBrother = hiddenBrother;
	}

	public void setHideSofInterface(boolean hideSofInterface) {
		// TODO Auto-generated method stub
		this.hideSofInterface = hideSofInterface;
	}

	public void setHideWorldMessages(boolean hideWorldAnnouncements) {
		this.hideWorldAnnouncements = hideWorldAnnouncements;
	}

	public void setHpBoostMultiplier(double hpBoostMultiplier) {
		this.hpBoostMultiplier = hpBoostMultiplier;
	}

	public void setInAnimationRoom(boolean inAnimationRoom) {
		this.inAnimationRoom = inAnimationRoom;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public void setJailed(long jailed) {
		this.jailed = jailed;
	}

	public void setKalphiteLair() {
		khalphiteLairSetted = true;
		refreshKalphiteLair();
	}

	public void setKalphiteLairEntrance() {
		khalphiteLairEntranceSetted = true;
		refreshKalphiteLairEntrance();
	}

	public int setKillCount(int killCount) {
		return this.killCount = killCount;
	}
	
	public void setKilledBork(boolean killedBork) {
		this.killedBork = killedBork;
	}
	
	/*
	 * Recipe for Disaster
	 * 0 - AgrithNaNa
	 * 1 - Karamel
	 * 2 - Dessourt
	 * 3 - FlamBeed
	 * 4 - Culinaromancer
	 */
	public void setKilledAgrithNaNa(boolean agrithNaNa) {
		this.rfd[0] = agrithNaNa;
	}

	public void setKilledCulinaromancer(boolean culinaromancer) {
		this.rfd[4] = culinaromancer;
	}

	public void setKilledDessourt(boolean dessourt) {
		this.rfd[2] = dessourt;
	}

	public void setKilledFlamBeed(boolean flamBeed) {
		this.rfd[3] = flamBeed;
	}

	public void setKilledKaramel(boolean karamel) {
		this.rfd[1] = karamel;
	}

	/**
	 * Sets the killedQueenBlackDragon.
	 *
	 * @param killedQueenBlackDragon
	 *            The killedQueenBlackDragon to set.
	 */
	public void setKilledQueenBlackDragon(boolean killedQueenBlackDragon) {
		this.killedQueenBlackDragon = killedQueenBlackDragon;
	}

	public void setLargeSceneView(boolean largeSceneView) {
		this.largeSceneView = largeSceneView;
	}

	public void setLastBonfire(int lastBonfire) {
		this.lastBonfire = lastBonfire;
	}

	public void setLastDuelRules(DuelRules duelRules) {
		lastDuelRules = duelRules;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
	}

	// Skull sceptre

	public void setLastPublicMessage(long lastPublicMessage) {
		this.lastPublicMessage = lastPublicMessage;
	}

	public void setLastRequestSQL(long lastRequestSQL) {
		// TODO Auto-generated method stub
		this.lastRequestSQL = lastRequestSQL;

	}

	public void setLastUsedGodwarsAltar(long l) {
		// TODO Auto-generated method stub

	}

	public void setLastWalked(long lastWalkedMillis) {
		// TODO Auto-generated method stub
		this.lastWalkedMillis = lastWalkedMillis;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
		sendRunButtonConfig();
	}

	public void setLoadedLogs(int loadedLogs) {
		this.loadedLogs = loadedLogs;
	}

	public void setLogsCut(int logsCut) {
		this.logsCut = logsCut;
	}

	public void setLoyaltyPoints(int Loyaltypoints) {
		this.loyaltyPoints = Loyaltypoints;
	}

	public void setMacBanned(boolean macBanned) {
		this.macBanned = macBanned;
	}

	public void setMagicLogsBurned(int magicLogsBurned) {
		this.magicLogsBurned = magicLogsBurned;
	}

	public void setMarkerPlant(MarkerPlant markerPlant) {
		this.markerPlant = markerPlant;
	}

	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	public void setMuted(long muted) {
		this.muted = muted;
	}

	public void setOverloadDelay(int overloadDelay) {
		this.overloadDelay = overloadDelay;
	}

	public void setOwner(String Owner) {
		this.Owner = Owner;
	}

	public void setPacketsDecoderPing(long packetsDecoderPing) {
		this.packetsDecoderPing = packetsDecoderPing;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPermBanned(boolean permBanned) {
		this.permBanned = permBanned;
	}

	public void setPermMuted(boolean permMuted) {
		this.permMuted = permMuted;
	}

	public void setPestControlGames(int pestControlGames) {
		this.pestControlGames = pestControlGames;
	}

	public void setPestPoints(int pestPoints) {
		this.pestPoints = pestPoints;
	}

	/**
	 * Sets the pet.
	 *
	 * @param pet
	 *            The pet to set.
	 */
	public void setPet(Pet pet) {
		this.pet = pet;
	}

	/**
	 * Sets the petManager.
	 *
	 * @param petManager
	 *            The petManager to set.
	 */
	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

	public void setPkPoints(int pkPoints) {
		this.pkPoints = pkPoints;
	}

	public void setPolDelay(long delay) {
		polDelay = delay;
	}

	public void setPrayerDelay(long teleDelay) {
		getTemporaryAttributtes().put("PrayerBlocked",
				teleDelay + Utilities.currentTimeMillis());
		prayer.closeAllPrayers();
	}

	public void setPrayerRenewalDelay(int delay) {
		prayerRenewalDelay = delay;
	}

	public void setPrivateChatSetup(int privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
	}

	public void setPublicStatus(int publicStatus) {
		this.publicStatus = publicStatus;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public void setRecovAnswer(String recovAnswer) {
		this.recovAnswer = recovAnswer;
	}

	public void setRecovQuestion(String recovQuestion) {
		this.recovQuestion = recovQuestion;
	}

	public void setRegisteredMac(String registeredMac) {
		this.registeredMac = registeredMac;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	public void setRocktailsCooked(int rocktailsCooked) {
		this.rocktailsCooked = rocktailsCooked;
	}

	public void setRouteEvent(RouteEvent routeEvent) {
		this.routeEvent = routeEvent;
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			setUpdateMovementType(true);
			sendRunButtonConfig();
		}
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = (byte) runEnergy;
		getPackets().sendRunEnergy();
	}

	/**
	 * @param runeSpanPoint
	 *            the runeSpanPoint to set
	 */
	public void setRuneSpanPoint(int runeSpanPoints) {
		this.runeSpanPoints = runeSpanPoints;
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
		setUpdateMovementType(true);
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public void setSkullChargesLeft(int left) {
		skullChargesLeft = left;
	}

	public int setSkullDelay(int delay) {
		return skullDelay = delay;
	}

	public void setSkullId(int skullId) {
		this.skullId = skullId;
	}

	public void setSkullInfiniteDelay(int skullId) {
		skullDelay = Integer.MAX_VALUE;
		this.skullId = skullId;
		getPlayerAppearances().generateAppearenceData();
	}

	public void setSlayerPoints(int slayerPoints) {
		this.slayerPoints = slayerPoints;
	}

	public void setSpawnsMode(boolean spawnsMode) {
		this.spawnsMode = spawnsMode;
	}

	public void setSpecRestoreTimer(int specRestoreTimer) {
		this.specRestoreTimer = specRestoreTimer;
	}

	public void setSpins(int spins) {
		this.spins = spins;
	}

	public void setStarter(boolean starter) {
		this.starter = starter;
	}

	public void setSummoningLeftClickOption(int summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	public void setSupporter(boolean isSupporter) {
		this.isSupporter = isSupporter;
	}

	public void setSwitchItemCache(List<Integer> switchItemCache) {
		this.switchItemCache = switchItemCache;
	}

	public void setTalkedToCook() {
		talkedtoCook = true;
	}

	public void setTalkedWithMarv() {
		talkedWithMarv = true;
	}

	public void setTeleBlockDelay(long teleDelay) {
		getTemporaryAttributtes().put("TeleBlocked",
				teleDelay + Utilities.currentTimeMillis());
	}

	public void setTemporaryMoveType(int temporaryMovementType) {
		this.temporaryMovementType = temporaryMovementType;
	}

	public void setThievingDelay(long thievingDelay) {
		this.thievingDelay = thievingDelay;
	}

	public void setTotalNpcsKilledTask(int totalNpcsKilledTask) {
		this.totalNpcsKilledTask = totalNpcsKilledTask;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public void setUniquePlayerId(int uniquePlayerId) {
		this.uniquePlayerId = uniquePlayerId;
	}

	public void setUpdateMovementType(boolean updateMovementType) {
		this.updateMovementType = updateMovementType;
	}

	/*
	 * do not use this, only used by pm
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public void setUsingReportOption(boolean option) {
		reportOption = option;
	}

	public void setVecnaTimer(int vecnaTimer) {
		this.vecnaTimer = vecnaTimer;
	}

	public void setVerboseShopDisplayMode(boolean b) {
		getPackets().sendConfigByFile(11055, verboseShopDisplayMode ? 0 : 1);
	}
	public void setVotePoints(int votePoints) {
		this.votePoints = votePoints;
	}

	public void setWarriorPoints(int index, double pointsDifference) {
		warriorPoints[index] += pointsDifference;
		if (warriorPoints[index] < 0) {
			final Controller controller = getControllerManager().getController();
			if (controller == null || !(controller instanceof WarriorsGuild)) {
				return;
			}
			final WarriorsGuild guild = (WarriorsGuild) controller;
			guild.inCyclopse = false;
			setNextTile(WarriorsGuild.CYCLOPS_LOBBY);
			warriorPoints[index] = 0;
		} else if (warriorPoints[index] > 65535) {
			warriorPoints[index] = 65535;
		}
		refreshWarriorPoints(index);
	}

	public void setWildernessSkull() {
		skullDelay = 3000;
		skullId = 0;
		getPlayerAppearances().generateAppearenceData();
	}

	public void setWonFightPits() {
		wonFightPits = true;
	}

	public void setXpLocked(boolean locked) {
		xpLocked = locked;
	}

	public void setYellDelay(long l) {
		yellDelay = l;
	}

	public void setYellDisabled(boolean yellDisabled) {
		this.yellDisabled = yellDisabled;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	public void setZeals(int zeals) {
		this.zeals = zeals;
	}

	// now that we inited we can start showing game
	public void start() {
		loadMapRegions();
		run();
		if (isDead() || getHitpoints() <= 0) {
			sendDeath(null);
		}
		setActive(true);
	}

	public void startLobby(Player player) {
		player.sendLobbyConfigs(player);
		player.getPackets().sendFriendsChatChannel();
	}

	public void stopAll() {
		stopAll(true);
	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	public void stopAll(boolean stopWalk, boolean stopInterface) {
		stopAll(stopWalk, stopInterface, true);
	}

	// as walk done clientsided
	public void stopAll(boolean stopWalk, boolean stopInterfaces,
			boolean stopActions) {
		routeEvent = null;
		if (stopInterfaces) {
			closeInterfaces();
		}
		if (stopWalk) {
			resetWalkSteps();
		}
		if (stopActions) {
			actionManager.forceStop();
		}
		combatDefinitions.resetSpells(false);
	}

	public void switchAllowChatEffects() {
		allowChatEffects = !allowChatEffects;
		refreshAllowChatEffects();
	}
	
	public void switchItemsLook() {
		oldItemsLook = !oldItemsLook;
		getPackets().sendItemsLook();
	}

	public void switchMouseButtons() {
		mouseButtons = !mouseButtons;
		refreshMouseButtons();
	}

	public void switchReportOption() {
		// TODO Auto-generated method stub
		reportOption = !reportOption;
		refreshReportOption();
	}

	public void toggleLootShare() {
		toggleLootShare = !toggleLootShare;
		refreshToggleLootShare();
	}

	public void toggleLootShare(boolean message) {
		lootshareEnabled = !lootshareEnabled;
		getPackets().sendConfig(1083, lootshareEnabled ? 1 : 0);
		if (!message) {
			return;
		}
		sendMessage(String.format("Lootshare is now %sactive.</col>",
				lootshareEnabled ? "" : "un"));
	}

	public void toogleRun(boolean update) {
		super.setRun(!getRun());
		setUpdateMovementType(true);
		if (update) {
			sendRunButtonConfig();
		}
	}

	public long uniqueID() {
		return uniqueID;
	}

	public void uniqueID(long random) {
		uniqueID = random;
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void useStairs(int emoteId, final Tile dest, int useDelay,
			int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final Tile dest, int useDelay,
			int totalDelay, final String message) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1) {
			setNextAnimation(new Animation(emoteId));
		}
		if (useDelay == 0) {
			setNextTile(dest);
		} else {
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					if (isDead()) {
						return;
					}
					setNextTile(dest);
					if (message != null) {
						sendMessage(message);
					}
				}
			}, useDelay - 1);
		}
	}

	public void vecnaTimer(int amount) {
		if (getVecnaTimer() > 0) {
			CoresManager.fastExecutor.schedule(new TimerTask() {
				@Override
				public void run() {
					if (hasFinished()) {
						cancel();
					}
					if (getVecnaTimer() > 0) {
						setVecnaTimer(getVecnaTimer() - 1);
					}
					if (getVecnaTimer() == 0) {
						getPackets()
								.sendGameMessage(
										"<col=FFCC00>Your skull of Vecna has regained its mysterious aura.");
						cancel();
					}
				}
			}, 10, 1);
		}
	}

	public void warriorCheck() {
		if (warriorPoints == null || warriorPoints.length != 6) {
			warriorPoints = new double[6];
		}
	}

	private long lastPrivateMessage;
	
	public long getLastPrivateMessage() {
		return lastPrivateMessage;
	}
	
    private int favorPoints;

    public void refreshFavorPoints() {
        getPackets().sendConfigByFile(9511, favorPoints);
    }
    
    public int getFavorPoints() {
        return favorPoints;
    }

    public void setFavorPoints(int points) {
        if (points + favorPoints >= 2000) {
            points = 2000;
            getPackets().sendGameMessage("The offering stone is full! The jadinkos won't deposite any more rewards until you have taken some.");
        }
        favorPoints = points;
        refreshFavorPoints();
    }
    
    private Item[] dungBinds;
    
    public Item[] getDungBinds() {
        return dungBinds;
    }

    public void removeDungItems() {
        if (hasFamiliar()) {
            if (getFamiliar() != null) {
                if (getFamiliar().getBob() != null) {
                    for (Item item : getFamiliar().getBob().getBeastItems().getItems()) {
                        if (item != null) {
                            if (ItemConstants.isInsideDungItem(item.getId())) {
                                getFamiliar().getBob().getBeastItems().remove(item);
                            }
                        }
                    }
                }
            }
        }
        for (Item item : getInventory().getItems().getItems()) {
            if (item != null) {
                if (ItemConstants.isInsideDungItem(item.getId())) {
                    getInventory().deleteItem(item);
                }
            }
        }
        for (Item item : getBank().getContainerCopy()) {
            if (item != null) {
                if (ItemConstants.isInsideDungItem(item.getId())) {
                    getBank().getItem(item.getId()).setId(995);
                }
            }
        }
        for (Item item : getEquipment().getItems().getItems()) {
            if (item != null) {
                if (ItemConstants.isInsideDungItem(item.getId())) {
                    getEquipment().deleteItem(item.getId(), 2147000000);
                }
            }
        }
        getPlayerAppearances().generateAppearenceData();
    }

    public boolean hasFamiliar() {
        return familiar != null;
    }
    
    public void setDungBinds(Item[] dungBinds) {
        this.dungBinds = dungBinds;
    }

    private Dungeoneering dungeon;
    
    public Dungeoneering getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeoneering dungeon) {
        this.dungeon = dungeon;
    }
    
    public int dungTokens;

}