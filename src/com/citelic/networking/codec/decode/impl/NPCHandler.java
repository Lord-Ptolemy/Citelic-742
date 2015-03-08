package com.citelic.networking.codec.decode.impl;

import com.citelic.GameConstants;
import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.game.entity.npc.impl.others.FireSpirit;
import com.citelic.game.entity.npc.impl.others.GraveStone;
import com.citelic.game.entity.npc.impl.others.LiquidGoldNymph;
import com.citelic.game.entity.npc.impl.others.LivingRock;
import com.citelic.game.entity.npc.impl.others.WildyWyrm;
import com.citelic.game.entity.npc.impl.slayer.Strykewyrm;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.appearance.design.PlayerLook;
import com.citelic.game.entity.player.content.actions.resting.Listen;
import com.citelic.game.entity.player.content.actions.resting.Rest;
import com.citelic.game.entity.player.content.actions.skills.dungeoneering.DungeoneeringRewards;
import com.citelic.game.entity.player.content.actions.skills.fishing.Fishing;
import com.citelic.game.entity.player.content.actions.skills.fishing.Fishing.FishingSpots;
import com.citelic.game.entity.player.content.actions.skills.hunter.Hunter;
import com.citelic.game.entity.player.content.actions.skills.mining.LivingMineralMining;
import com.citelic.game.entity.player.content.actions.skills.mining.MiningBase;
import com.citelic.game.entity.player.content.actions.skills.runecrafting.SiphionActionCreatures;
import com.citelic.game.entity.player.content.actions.skills.slayer.Slayer.SlayerMaster;
import com.citelic.game.entity.player.content.actions.skills.slayer.SlayerManager;
import com.citelic.game.entity.player.content.actions.skills.thieving.PickPocketAction;
import com.citelic.game.entity.player.content.actions.skills.thieving.PickPocketableNPC;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.CommendationExchange;
import com.citelic.game.entity.player.content.controllers.impl.distractions.puropuro.PuroPuroController;
import com.citelic.game.entity.player.content.dialogue.impl.npcs.FremennikShipmaster;
import com.citelic.game.entity.player.content.transportation.GnomeGlider;
import com.citelic.game.map.pathfinding.RouteEvent;
import com.citelic.networking.streaming.InputStream;
import com.citelic.utility.Logger;
import com.citelic.utility.ShopsHandler;
import com.citelic.utility.Utilities;
import com.citelic.utility.npc.NPCExamines;
import com.citelic.utility.npc.NPCSpawns;

public class NPCHandler {

	public static void handleExamine(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort();
		boolean forceRun = stream.readByte() == 1;
		final NPC npc = Engine.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		if (forceRun)
			player.setRun(forceRun);
		player.getPackets().sendNPCMessage(0, npc,
				NPCExamines.getExamine(npc) + "");
		if (GameConstants.DEBUG)
			Logger.log("NPCHandler",
					"examined npc: " + npcIndex + ", " + npc.getId());
	}

	public static void handleOption1(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort();
		boolean forceRun = stream.readByte() == 1;
		final NPC npc = Engine.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (npc.getDefinitions().name.contains("Banker")
				|| npc.getDefinitions().name.contains("banker")) {
			player.faceEntity(npc);
			if (!player.withinDistance(npc, 2))
				return;
			npc.faceEntity(player);
			player.getDialogueManager().startDialogue("Banker", npc.getId());
			return;
		}
		if (SiphionActionCreatures.siphon(player, npc))
			return;
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.faceEntity(npc);
				if (!player.getControllerManager().processNPCClick1(npc))
					return;
				FishingSpots spot = FishingSpots.forId(npc.getId() | 1 << 24);
				if (spot != null) {
					player.getActionManager().setAction(new Fishing(spot, npc));
					return; // its a spot, they wont face us
				}
				if (npc.getDefinitions().name.contains("Marker plant")) {
					player.faceEntity(npc);
					if (player.getMarkerPlant() != null) {
						if (player.getMarkerPlant().getMarkPlant() == npc) {
							player.getMarkerPlant().throwPlant();
							return;
						} else {
							player.getPackets().sendGameMessage(
									"This isn't your Marker Plant!");
							return;
						}
					} else {
						player.getPackets().sendGameMessage(
								"This isn't your Marker Plant!");
						return;
					}
				}
				if (npc.getDefinitions().name.contains("Musician")
						|| npc.getId() == 3509) {
					player.stopAll();
					player.getActionManager().setAction(new Listen());
					return;
				}
				if (npc instanceof GraveStone) {
					GraveStone grave = (GraveStone) npc;
					grave.sendGraveInscription(player);
					return;
				}
				if (player.getControllerManager().getController() instanceof PuroPuroController) {
					Hunter.captureFlyingEntity(player, npc);
					return;
				}
				if (npc.getId() == 15451 && npc instanceof FireSpirit) {
					FireSpirit spirit = (FireSpirit) npc;
					spirit.giveReward(player);
				}
				if (npc instanceof Familiar) {
					if (npc.getDefinitions().hasOption("interact")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage(
									"That isn't your familiar.");
							return;
						}
						if (npc.getId() == 7341 || npc.getId() == 7342) {
							player.getDialogueManager().startDialogue(
									"FireTitan");
						} else {
							player.getDialogueManager()
									.startDialogue("SimpleMessage",
											"Why would I talk to a familiar? That's just weird.");
						}
					}
					return;
				}
				if (npc.getDefinitions().hasOption("Listen-to")) {
					if (player.isResting()) {
						player.stopAll();
						return;
					}
					long currentTime = Utilities.currentTimeMillis();
					if (player.getEmotesManager().getNextEmoteEnd() >= currentTime) {
						player.getPackets().sendGameMessage(
								"You can't rest while perfoming an emote.");
						return;
					}
					if (player.getLockDelay() >= currentTime) {
						player.getPackets().sendGameMessage(
								"You can't rest while perfoming an action.");
						return;
					}
					player.stopAll();
					player.getActionManager().setAction(new Rest());
				}
				if (npc.getId() == 14 && npc instanceof LiquidGoldNymph) {
					LiquidGoldNymph goldNymph = (LiquidGoldNymph) npc;
					goldNymph.giveReward(player);
					npc.faceEntity(player);
				}
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 1))
					return;
				switch(npc.getId()) {
				case 9711:
					DungeoneeringRewards.openRewardsShop(player);
					break;
				case 8837:
				case 8838:
				case 8839:
					player.getActionManager().setAction(
							new LivingMineralMining((LivingRock) npc));
					break;
				case 2824:
				case 14877:
					player.getDialogueManager().startDialogue("Tanner",
							npc.getId());
					break;
				case 7142:
					player.getDialogueManager().startDialogue("SafetyGuard",
							npc.getId());
					break;
				case 7600:
					player.getDialogueManager().startDialogue("Fiara");
					break;
				case 3705:
					player.getDialogueManager().startDialogue("Max",
							npc.getId());
					break;
				case 1783:
					player.getDialogueManager().startDialogue("Richard",
							npc.getId());
					break;
				case 9462:
				case 9464:
				case 9466:
					Strykewyrm.handleStomping(player, npc);
					break;
				case 2417:
					WildyWyrm.handleStomping(player, npc);
					break;
				case 14850:
					player.getDialogueManager().startDialogue("ShopsD",
							npc.getId());
					break;
				case 9707:
				case 9708:
					player.getDialogueManager().startDialogue(
							"FremennikShipmaster", npc.getId(), npc.getId() == 9707 ? true : false);
					break;
				case 15785:
					player.getDialogueManager().startDialogue("TrailAnnouncer",
							npc.getId());
					break;
				case 456:
					player.getDialogueManager().startDialogue("FatherAereck",
							npc.getId());
					break;
				case 15158:
					player.getDialogueManager().startDialogue("TheRaptor",
							npc.getId());
					break;
				case 5896:
					player.getDialogueManager().startDialogue("Cyrisus",
							npc.getId());
					break;
				case 6988:
					player.getDialogueManager().startDialogue("SummoningShop",
							npc.getId());
					break;
				case 598:
					player.getDialogueManager().startDialogue("Hairdresser",
							npc.getId());
					break;
				case 548:
					player.getDialogueManager().startDialogue("Thessalia",
							npc.getId());
					break;
				case 2676:
					player.getDialogueManager().startDialogue("MakeOverMage",
							npc.getId(), 0);
					break;
				default:
					player.getPackets().sendGameMessage(
							"Nothing interesting happens.");
					if (player.getRights() >= 2) {
						System.out.println("[NPCHandler] Click 1 : "
								+ npc.getId() + ", " + npc.getX() + ", "
								+ npc.getY() + ", " + npc.getZ());
					}
				}
			}
		}));
	}

	public static void handleOption2(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort();
		boolean forceRun = stream.readByte() == 1;
		final NPC npc = Engine.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (npc.getDefinitions().name.contains("Banker")
				|| npc.getDefinitions().name.contains("banker")) {
			player.faceEntity(npc);
			if (!player.withinDistance(npc, 2))
				return;
			npc.faceEntity(player);
			player.getBank().openBank();
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.faceEntity(npc);
				FishingSpots spot = FishingSpots.forId(npc.getId() | (2 << 24));
				if (spot != null) {
					player.getActionManager().setAction(new Fishing(spot, npc));
					return;
				}
				PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
				if (pocket != null) {
					player.getActionManager().setAction(
							new PickPocketAction(npc, pocket));
					return;
				}
				if (npc instanceof Familiar) {
					if (npc.getDefinitions().hasOption("store")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage(
									"That isn't your familiar.");
							return;
						}
						player.getFamiliar().store();
					} else if (npc.getDefinitions().hasOption("cure")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage(
									"That isn't your familiar.");
							return;
						}
						if (!player.getPoison().isPoisoned()) {
							player.getPackets().sendGameMessage(
									"Your arent poisoned or diseased.");
							return;
						} else {
							player.getFamiliar().drainSpecial(2);
							player.addPoisonImmune(120);
						}
					}
					return;
				}
				if (npc instanceof GraveStone) {
					GraveStone grave = (GraveStone) npc;
					grave.repair(player, false);
					return;
				}
				if (npc.getDefinitions().name.contains("Musician")
						|| npc.getId() == 3509) {
					player.stopAll();
					player.getDialogueManager().startDialogue("Musician",
							npc.getId());
					return;
				}
				if (!player.getControllerManager().processNPCClick2(npc))
					return;
				switch (npc.getDefinitions().name.toLowerCase()) {
				case "void knight":
					CommendationExchange.openExchangeShop(player);
					break;
				}
				npc.faceEntity(player);
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 2))
					return;
				switch(npc.getId()) {
				case 9711:
					DungeoneeringRewards.openRewardsShop(player);
					break;
				case 3810:
				    GnomeGlider.openInterface(player, 1);
					break;
				case 3809:
				    GnomeGlider.openInterface(player, 3);
					break;
				case 3812:
				    GnomeGlider.openInterface(player, 4);
				    break;
				case 1800:
				    GnomeGlider.openInterface(player, 5);
				    break;
				case 3811:
				    GnomeGlider.openInterface(player, 0);
				    break;
				case 9707:
				case 9708:
					FremennikShipmaster.sail(player, npc.getId() == 9707 ? true : false);
					break;
				case 13455:
					player.getBank().openBank();
					break;
				case 2320:
				case 2824:
					player.getDialogueManager().startDialogue("TanningD",
							npc.getId());
					break;
				case 598:
					PlayerLook.openHairdresserSalon(player);
					break;
				case 6988:
					player.getDialogueManager().startDialogue("SummoningShop",
							npc.getId());
					break;
				case 2676:
					PlayerLook.openMageMakeOver(player);
					break;
				default:
					player.getPackets().sendGameMessage(
							"Nothing interesting happens.");
					if (GameConstants.DEBUG)
						System.out.println("cliked 2 at npc id : "
								+ npc.getId() + ", " + npc.getX() + ", "
								+ npc.getY() + ", " + npc.getZ());
					break;		
				}
			}
		}));
	}

	public static void handleOption3(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort();
		boolean forceRun = stream.readByte() == 1;
		final NPC npc = Engine.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				if (!player.getControllerManager().processNPCClick3(npc))
					return;
				player.faceEntity(npc);
				if (npc.getId() >= 8837 && npc.getId() <= 8839) {
					MiningBase.propect(player, "You examine the remains...",
							"The remains contain traces of living minerals.");
					return;
				}
				if (npc.getId() == 13727) {
					player.getPackets().sendGameMessage(
							"Xuan cleared your Loyalty Title.");
					player.getPlayerAppearance().setTitle(0);
					player.getPlayerAppearance().generateAppearenceData();
				}
				if (npc instanceof GraveStone) {
					GraveStone grave = (GraveStone) npc;
					grave.repair(player, true);
					return;
				}
				if (npc.getDefinitions().name.contains("clerk")) {
					player.faceEntity(npc);
					npc.faceEntity(player);
					// player.grandExchange().sendHistoryInterface(player);
					return;
				}
				if (npc.getId() == 3374) {
					ShopsHandler.openShop(player, 18);
				}
				if (npc.getId() == 14877) {
					player.getDialogueManager().startDialogue("TanningD",
							npc.getId());
				}
				if (npc.getId() == 3705)
					player.getDialogueManager().startDialogue("WiseOldMan",
							npc.getId());
				npc.faceEntity(player);
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 3)) {
					ShopsHandler.openShop(player, 54);
				}
				if (npc.getId() == 548) {
					PlayerLook.openThessaliasMakeOver(player);
				}
				if (npc.getId() == 5532) {
					npc.setNextForceTalk(new ForceTalk(
							"Senventior Disthinte Molesko!"));
					player.getControllerManager().startController(
							"SorceressGarden");
				}
			}
		}));
		if (GameConstants.DEBUG)
			System.out.println("cliked 3 at npc id : " + npc.getId() + ", "
					+ npc.getX() + ", " + npc.getY() + ", " + npc.getZ());
	}

	public static void handleOption4(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort();
		boolean forceRun = stream.readByte() == 1;
		final NPC npc = Engine.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				if (!player.getControllerManager().processNPCClick3(npc))
					return;
				if (npc instanceof GraveStone) {
					GraveStone grave = (GraveStone) npc;
					grave.demolish(player);
					return;
				}
				if (npc.getId() == 14877) {
					ShopsHandler.openShop(player, 13);
				}
				player.faceEntity(npc);
				npc.faceEntity(player);
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 4)) {
					player.getSlayerManager().sendSlayerInterface(
							SlayerManager.BUY_INTERFACE);
				}
			}
		}));
		if (GameConstants.DEBUG)
			System.out.println("cliked 4 at npc id : " + npc.getId() + ", "
					+ npc.getX() + ", " + npc.getY() + ", " + npc.getZ());
	}
}
