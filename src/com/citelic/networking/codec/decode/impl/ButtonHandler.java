package com.citelic.networking.codec.decode.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimerTask;

import com.citelic.GameConstants;
import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.game.entity.npc.impl.familiar.Familiar.SpecialAttack;
import com.citelic.game.entity.player.Equipment;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.appearance.design.PlayerDesign;
import com.citelic.game.entity.player.appearance.design.PlayerLook;
import com.citelic.game.entity.player.containers.Inventory;
import com.citelic.game.entity.player.containers.Shop;
import com.citelic.game.entity.player.content.SkillCapeCustomizer;
import com.citelic.game.entity.player.content.actions.FightPitsViewingOrb;
import com.citelic.game.entity.player.content.actions.resting.Rest;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.construction.House;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.actions.skills.runecrafting.RuneCrafting;
import com.citelic.game.entity.player.content.actions.skills.smithing.JewelrySmithing;
import com.citelic.game.entity.player.content.actions.skills.smithing.Smithing.ForgingInterface;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning;
import com.citelic.game.entity.player.content.actions.skills.summoning.SummoningScroll;
import com.citelic.game.entity.player.content.controllers.impl.ImpossibleJad;
import com.citelic.game.entity.player.content.controllers.impl.distractions.crucible.Crucible;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pestcontrol.CommendationExchange;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.duelarena.DuelControler;
import com.citelic.game.entity.player.content.dialogue.SkillsDialogue;
import com.citelic.game.entity.player.content.dialogue.impl.misc.LevelUp;
import com.citelic.game.entity.player.content.miscellaneous.AdventurersLog;
import com.citelic.game.entity.player.content.miscellaneous.GravestoneSelection;
import com.citelic.game.entity.player.content.socialization.clans.ClansManager;
import com.citelic.game.entity.player.content.transportation.FairyRing;
import com.citelic.game.entity.player.content.transportation.GnomeGlider;
import com.citelic.game.entity.player.content.transportation.ItemTransportation;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemConstants;
import com.citelic.game.entity.player.managers.EmotesManager;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.codec.decode.WorldPacketsDecoder;
import com.citelic.networking.streaming.InputStream;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;
import com.citelic.utility.economy.Prices;
import com.citelic.utility.item.ItemExamines;

public class ButtonHandler {

	public static String sendLoanItemExamine(int slotId2) {
		Item item = new Item(slotId2);
		return item.getDefinitions().getExamine();
	}

	public static void handleButtons(final Player player, InputStream stream,
			int packetId) {
		int interfaceHash = stream.readIntV1();
		int interfaceId = interfaceHash >> 16;
		if (Utilities.getInterfaceDefinitionsSize() <= interfaceId) {
			// hack, or server error or client error
			// player.getSession().getChannel().close();
			return;
		}
		if (player.isDead()
				|| !player.getInterfaceManager().containsInterface(interfaceId)) {
			return;
		}
		final int componentId = interfaceHash & 0xFF;
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		if (componentId != 65535
				&& Utilities.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId) {
			return;
		}
		final int slotId2 = stream.readUnsignedShortLE128();
		final int slotId = stream.readUnsignedShortLE();
		if (player.getRights() == 2 && GameConstants.DEBUG)
			player.print("InterfaceId " + interfaceId
					+ ", componentId " + componentId + ", slotId " + slotId
					+ ", slotId2 " + slotId2 + ", PacketId: " + packetId);
		if (!player.getControllerManager().processButtonClick(interfaceId,
				componentId, slotId, packetId))
			return;
		switch (interfaceId) {
		case 164:
		case 161:
		case 378:
			player.getSlayerManager().handleRewardButtons(interfaceId,
					componentId);
			break;
		case 1309:
			if (componentId == 20) {
				player.getPackets()
						.sendGameMessage(
								"Use your enchanted stone ring onto the player that you would like to invite.",
								true);
			} else if (componentId == 22) {
				Player p2 = player.getSlayerManager().getSocialPlayer();
				if (p2 == null) {
					player.getPackets()
							.sendGameMessage(
									"You have no slayer group, invite a player to start one.");
				} else {
					player.getPackets().sendGameMessage(
							"Your current slayer group consists of you and "
									+ p2.getDisplayName() + ".");
				}
			} else if (componentId == 24) {
				player.getSlayerManager().resetSocialGroup(true);
			}
			player.closeInterfaces();
			break;
		case 13:
		case 14:
		case 759:
			player.getBankPin().handleButtons(interfaceId, componentId);
			break;
		case 1252:
		case 1253:
			player.getSquealOfFortune().handleButtons(componentId, interfaceId);
			break;
		case 1263:
			player.getDialogueManager().continueDialogue(interfaceId,
					componentId);
			break;
		case 652:
			if (componentId == 31) {
				GravestoneSelection
						.handleSelectionInterface(player, slotId / 6);
			} else if (componentId == 34) {
				GravestoneSelection.confirmSelection(player);
			}
			break;
		case 1139:
			if (componentId == 7) {
				player.getSquealOfFortune().openSquealInterface();
			}
			if (componentId == 23) {
				player.getPackets().sendOpenURL(GameConstants.WEBSHOP);
			}
			break;
		case 138:
			int selectedComponent = componentId - 23;
		    if (selectedComponent == 0 || player.getTemporaryAttributtes().get("using_carrier") != null)
			return;
		    if (componentId == 22)
			selectedComponent = 4;
		    else if (componentId == 27)
			selectedComponent = 5;
		    GnomeGlider.sendGlider(player, selectedComponent, false);
			break;
		case 734:
			 if (componentId == 21)
				 FairyRing.confirmRingHash(player);
			 else
				FairyRing.handleDialButtons(player, componentId);
			 break;
		case 735:
			if (componentId >= 14 && componentId <= 14 + 64)
				FairyRing.sendRingTeleport(player, componentId - 14);
			break;
		case 1369:
			if (componentId == 18 || componentId == 22) {
				player.getTemporaryAttributtes().put("WillDuelFriendly", true);
				player.getPackets().sendConfig(283, 67108864);
			} else if (componentId == 19 || componentId == 21) {
				player.getTemporaryAttributtes().put("WillDuelFriendly", false);
				player.getPackets().sendConfig(283, 134217728);
			} else if (componentId == 20) {
				DuelControler.challenge(player);
			}
			break;
		case 548:
			if (componentId == 86) {
				player.getSquealOfFortune().sendTab();
			}
		case 746:
			if (interfaceId == 548 && componentId == 211 || interfaceId == 746
			&& componentId == 168) {
		player.getPackets().sendOpenURL(GameConstants.HOMEPAGE);
	}
	if (interfaceId == 548 && componentId == 219 || interfaceId == 746
			&& componentId == 176) {
		player.getDialogueManager().startDialogue("GamebarSupport");
	}
	if (interfaceId == 548 && componentId == 227 || interfaceId == 746
			&& componentId == 184) {
		player.getPackets().sendOpenURL(GameConstants.FORUMS);
	}
	if (interfaceId == 548 && componentId == 242 || interfaceId == 746
			&& componentId == 199) {
		player.getPackets().sendOpenURL(GameConstants.WEBSHOP);
	}
	if ((interfaceId == 548 && componentId == 160)
			|| (interfaceId == 746 && componentId == 242)) {
		if (player.getInterfaceManager().containsScreenInter()
				|| player.getInterfaceManager()
						.containsInventoryInter()) {
			player.getPackets()
					.sendGameMessage(
							"Please finish what you're doing before opening the world map.");
			return;
		}
		player.getPackets().sendWindowsPane(755, 0);
		int posHash = player.getX() << 14 | player.getY();
		player.getPackets().sendGlobalConfig(622, posHash);
		player.getPackets().sendGlobalConfig(674, posHash);
	} else if ((interfaceId == 746 && componentId == 250)
			|| (interfaceId == 548 && componentId == 170)) {
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
			if (player.getInterfaceManager().containsScreenInter()
					|| player.isLocked()) {
				player.getPackets()
						.sendGameMessage(
								"Please finish what you're doing before opening the price checker.");
				return;
			}
			player.stopAll();
			player.getPriceCheckManager().openPriceCheck();
		}
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
			player.getMoneyPouch().switchPouch();
		} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
			player.getMoneyPouch().withdrawPouch();
		} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
			player.getMoneyPouch().examinePouch();
		}
	} else if ((interfaceId == 548 && componentId == 38)
			|| (interfaceId == 746 && componentId == 97)) {
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
			player.getSkills().switchXPDisplay();
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
			player.getSkills().switchXPPopup();
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
			player.getSkills().setupXPCounter();
	} else if ((interfaceId == 746 && componentId == 207)
			|| (interfaceId == 548 && componentId == 159)) {
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
			if (player.getInterfaceManager().containsScreenInter()) {
				player.getPackets()
						.sendGameMessage(
								"Please finish what you're doing before opening the price checker.");
				return;
			}
			player.stopAll();
			player.getPriceCheckManager().openPriceCheck();
		}
	}
			break;
		case 34:
			switch (componentId) {
			case 35:
			case 37:
			case 39:
			case 41:
				player.getNotes().colour((componentId - 35) / 2);
				player.getPackets().sendHideIComponent(34, 16, true);
				break;
			case 3:
				player.getPackets().sendInputLongTextScript("Add note:");
				player.getTemporaryAttributtes().put("entering_note",
						Boolean.TRUE);
				break;
			case 9:
				switch (packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					if (player.getNotes().getCurrentNote() == slotId)
						player.getNotes().removeCurrentNote();
					else
						player.getNotes().setCurrentNote(slotId);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					player.getPackets().sendInputLongTextScript("Edit note:");
					player.getNotes().setCurrentNote(slotId);
					player.getTemporaryAttributtes().put("editing_note",
							Boolean.TRUE);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
					player.getNotes().setCurrentNote(slotId);
					player.getPackets().sendHideIComponent(34, 16, false);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
					player.getNotes().delete(slotId);
					break;
				}
				break;
			case 11:
				switch (packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.getNotes().delete();
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					player.getNotes().deleteAll();
					break;
				}
				break;
			}
		case 1011:
			CommendationExchange.handleButtonOptions(player, componentId);
			break;
		case 182:
			if (player.getInterfaceManager().containsInventoryInter())
				return;
			if (componentId == 6 || componentId == 13)
				if (!player.hasFinished())
					player.logout(componentId == 6);
			break;
		case 1165:
			if (componentId == 22)
				Summoning.closeDreadnipInterface(player);
			break;
		case 880:
			if (componentId >= 7 && componentId <= 19)
				Familiar.setLeftclickOption(player, (componentId - 7) / 2);
			else if (componentId == 21)
				Familiar.confirmLeftOption(player);
			else if (componentId == 25)
				Familiar.setLeftclickOption(player, 7);
			break;
		case 662:
			if (player.getFamiliar() == null) {
				if (player.getPet() == null) {
					return;
				}
				if (componentId == 49)
					player.getPet().call();
				else if (componentId == 51)
					player.getDialogueManager().startDialogue("DismissD");
				return;
			}
			if (componentId == 49)
				player.getFamiliar().call();
			else if (componentId == 51)
				player.getDialogueManager().startDialogue("DismissD");
			else if (componentId == 67)
				player.getFamiliar().takeBob();
			else if (componentId == 69)
				player.getFamiliar().renewFamiliar();
			else if (componentId == 74) {
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.CLICK)
					player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().hasSpecialOn())
					player.getFamiliar().submitSpecial(player);
			}
			break;
		case 747:
			if (componentId == 8) {
				Familiar.selectLeftOption(player);
			} else if (player.getPet() != null) {
				if (componentId == 11 || componentId == 20) {
					player.getPet().call();
				} else if (componentId == 12 || componentId == 21) {
					player.getDialogueManager().startDialogue("DismissD");
				} else if (componentId == 10 || componentId == 19) {
					player.getPet().sendFollowerDetails();
				}
			} else if (player.getFamiliar() != null) {
				if (componentId == 11 || componentId == 20)
					player.getFamiliar().call();
				else if (componentId == 12 || componentId == 21)
					player.getDialogueManager().startDialogue("DismissD");
				else if (componentId == 13 || componentId == 22)
					player.getFamiliar().takeBob();
				else if (componentId == 14 || componentId == 23)
					player.getFamiliar().renewFamiliar();
				else if (componentId == 19 || componentId == 10)
					player.getFamiliar().sendFollowerDetails();
				else if (componentId == 18) {
					if (player.getFamiliar().getSpecialAttack() == SpecialAttack.CLICK)
						player.getFamiliar().setSpecial(true);
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(player);
				}
			}
			break;
		case 309:
			PlayerLook.handleHairdresserSalonButtons(player, componentId,
					slotId);
			break;
		case 729:
			PlayerLook.handleThessaliasMakeOverButtons(player, componentId,
					slotId);
			break;
		case 187:
			if (componentId == 1) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getMusicsManager().playAnotherMusic(slotId / 2);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getMusicsManager().sendHint(slotId / 2);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getMusicsManager().addToPlayList(slotId / 2);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getMusicsManager().removeFromPlayList(slotId / 2);
			} else if (componentId == 4)
				player.getMusicsManager().addPlayingMusicToPlayList();
			else if (componentId == 11)
				player.getMusicsManager().switchPlayListOn();
			else if (componentId == 12)
				player.getMusicsManager().clearPlayList();
			else if (componentId == 14)
				player.getMusicsManager().switchShuffleOn();
			break;
		case 275:
			if (componentId == 14) {
				player.getPackets().sendOpenURL(GameConstants.HOMEPAGE);
			}
			break;
		case 192:
			if (componentId == 2)
				player.getCombatDefinitions().switchDefensiveCasting();
			else if (componentId == 7)
				player.getCombatDefinitions().switchShowCombatSpells();
			else if (componentId == 9)
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			else if (componentId == 11)
				player.getCombatDefinitions().switchShowMiscallaneousSpells();
			else if (componentId == 13)
				player.getCombatDefinitions().switchShowSkillSpells();
			else if (componentId >= 15 & componentId <= 17)
				player.getCombatDefinitions()
						.setSortSpellBook(componentId - 15);
			else
				Magic.processNormalSpell(player, componentId, packetId);
			break;
		case 334:
			if (componentId == 22) {
				player.closeInterfaces();
			} else if (componentId == 21) {
				player.getTrade().accept(false);
			}
			break;
		case 335:
			if (componentId == 18) {
				player.getTrade().accept(true);
			} else if (componentId == 20) {
				player.closeInterfaces();
			} else if (componentId == 32) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					player.getTrade().removeItem(slotId, 1);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					player.getTrade().removeItem(slotId, 5);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					player.getTrade().removeItem(slotId, 10);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					player.getTrade().removeItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("trade_item_X_Slot",
							slotId);
					player.getTemporaryAttributtes().put("trade_isRemove",
							Boolean.TRUE);
					player.getPackets().sendRunScript(108,
							new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
					player.getTrade().sendValue(slotId, false);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
					player.getTrade().sendExamine(slotId, false);
				}
			} else if (componentId == 35) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					player.getTrade().sendValue(slotId, true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
					player.getTrade().sendExamine(slotId, true);
				}
			}
			break;
		case 336:
			if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					player.getTrade().addItem(slotId, 1);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					player.getTrade().addItem(slotId, 5);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					player.getTrade().addItem(slotId, 10);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					player.getTrade().addItem(slotId, Integer.MAX_VALUE);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("trade_item_X_Slot",
							slotId);
					player.getTemporaryAttributtes().remove("trade_isRemove");
					player.getPackets().sendRunScript(108,
							new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
					player.getTrade().sendValue(slotId);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			}
			break;
		case 300:
			ForgingInterface.handleIComponents(player, componentId);
			break;
		case 206:
			if (componentId == 15) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getPriceCheckManager().removeItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getPriceCheckManager().removeItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getPriceCheckManager().removeItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getPriceCheckManager().removeItem(slotId,
							Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("pc_item_X_Slot",
							slotId);
					player.getTemporaryAttributtes().put("pc_isRemove",
							Boolean.TRUE);
					player.getPackets().sendRunScript(108,
							new Object[] { "Enter Amount:" });
				}
			}
			break;
		case 666:
			switch (componentId) {
			case 18:
				Summoning.sendInterface(player);
				break;
			case 16:
				switch (packetId) {
				case 14:
					SummoningScroll.createScroll(player, slotId2, 1);
					break;
				case 67: // 5
					SummoningScroll.createScroll(player, slotId2, 5);
					break;
				case 5: // 10
					SummoningScroll.createScroll(player, slotId2, 10);
					break;
				case 55: // all
					SummoningScroll.createScroll(player, slotId2, 28);
					break;
				case 68: // x
					player.getTemporaryAttributtes().put("item1", slotId2);
					player.getTemporaryAttributtes().put("sum1",
							Integer.valueOf(0));
					player.getPackets()
							.sendRunScript(
									108,
									new Object[] { "How many would you like to make? " });
					break;
				}
				break;
			}
			break;
		case 672:
			if (componentId == 19)
				SummoningScroll.sendInterface(player);
			if (componentId == 16) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					Summoning.createPouch(player, slotId2, 1);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					Summoning.createPouch(player, slotId2, 5);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					Summoning.createPouch(player, slotId2, 10);
				}
			}
			break;
		case 207:
			if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getPriceCheckManager().addItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getPriceCheckManager().addItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getPriceCheckManager().addItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getPriceCheckManager().addItem(slotId,
							Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("pc_item_X_Slot",
							slotId);
					player.getTemporaryAttributtes().remove("pc_isRemove");
					player.getPackets().sendRunScript(108,
							new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getInventory().sendExamine(slotId);
			}
			break;
		case 665:
			if (player.getFamiliar() == null
			|| player.getFamiliar().getBob() == null)
		return;
	if (componentId == 0) {
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
			player.getFamiliar().getBob().addItem(slotId, 1);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
			player.getFamiliar().getBob().addItem(slotId, 5);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
			player.getFamiliar().getBob().addItem(slotId, 10);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
			player.getFamiliar().getBob()
					.addItem(slotId, Integer.MAX_VALUE);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
			player.getTemporaryAttributtes().put("bob_item_X_Slot",
					slotId);
			player.getTemporaryAttributtes().remove("bob_isRemove");
			player.getPackets().sendRunScript(108,
					new Object[] { "Enter Amount:" });
		} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
			player.getInventory().sendExamine(slotId);
	}
			break;
		case 671:
			if (player.getFamiliar() == null
			|| player.getFamiliar().getBob() == null)
		return;
	if (componentId == 27) {
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
			player.getFamiliar().getBob().removeItem(slotId, 1);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
			player.getFamiliar().getBob().removeItem(slotId, 5);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
			player.getFamiliar().getBob().removeItem(slotId, 10);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
			player.getFamiliar().getBob()
					.removeItem(slotId, Integer.MAX_VALUE);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
			player.getTemporaryAttributtes().put("bob_item_X_Slot",
					slotId);
			player.getTemporaryAttributtes().put("bob_isRemove",
					Boolean.TRUE);
			player.getPackets().sendRunScript(108,
					new Object[] { "Enter Amount:" });
		}
	} else if (componentId == 29)
		player.getFamiliar().takeBob();
			break;
		case 916:
			SkillsDialogue.handleSetQuantityButtons(player, componentId);
			break;
		case 193:
			if (componentId == 5)
				player.getCombatDefinitions().switchShowCombatSpells();
			else if (componentId == 7)
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			else if (componentId >= 9 && componentId <= 11)
				player.getCombatDefinitions().setSortSpellBook(componentId - 9);
			else if (componentId == 18)
				player.getCombatDefinitions().switchDefensiveCasting();
			else
				Magic.processAncientSpell(player, componentId, packetId);
			break;
		case 430:
			if (componentId == 5)
				player.getCombatDefinitions().switchShowCombatSpells();
			else if (componentId == 7)
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			else if (componentId == 9)
				player.getCombatDefinitions().switchShowMiscallaneousSpells();
			else if (componentId >= 11 & componentId <= 13)
				player.getCombatDefinitions()
						.setSortSpellBook(componentId - 11);
			else if (componentId == 20)
				player.getCombatDefinitions().switchDefensiveCasting();
			else
				Magic.processLunarSpell(player, componentId, packetId);
			break;
		case 261:
			if (player.getInterfaceManager().containsInventoryInter())
				return;
			if (componentId == 6) {
				player.switchReportOption();
			} else if (componentId == 22) {
				if (player.getInterfaceManager().containsScreenInter()) {
					player.getPackets()
							.sendGameMessage(
									"Please close the interface you have open before setting your graphic options.");
					return;
				}
				player.stopAll();
				player.getInterfaceManager().sendInterface(742);
			} else if (componentId == 12)
				player.switchAllowChatEffects();
			else if (componentId == 13) { // chat setup
				player.getInterfaceManager().sendSettings(982);
			} else if (componentId == 14)
				player.switchMouseButtons();
			else if (componentId == 16)
				player.getInterfaceManager().sendSettings(398);
			else if (componentId == 24) // audio options
				player.getInterfaceManager().sendSettings(429);
			else if (componentId == 26)
				AdventurersLog.open(player);
			break;
		case 398:
			if (componentId == 19) {
				player.getInterfaceManager().sendSettings();
			} else if (componentId == 15 || componentId == 1) {
				player.getHouse().setBuildMode(componentId == 15);
			} else if (componentId == 25 || componentId == 26) {
				player.getHouse().setArriveInPortal(componentId == 25);
			} else if (componentId == 27) {
				player.getHouse().expelGuests();
			} else if (componentId == 29) {
				House.leaveHouse(player);
			}
			break;
		case 402:
			if (componentId >= 93 && componentId <= 115) {
				player.getHouse().createRoom(componentId - 93);
			}
			break;
		case 394:
		case 396:
			if (componentId == 11) {
				player.getHouse().build(slotId);
			}
			break;
		case 429:
			if (componentId == 18)
				player.getInterfaceManager().sendSettings();
			break;
		case 982:
			if (componentId == 5)
				player.getInterfaceManager().sendSettings();
			else if (componentId == 41)
				player.setPrivateChatSetup(player.getPrivateChatSetup() == 0 ? 1
						: 0);
			else if (componentId >= 49 && componentId <= 66)
				player.setPrivateChatSetup(componentId - 48);
			else if (componentId >= 72 && componentId <= 91)
				player.setFriendChatSetup(componentId - 72);
			break;
		case 271:
			if (player.getControllerManager().getController() instanceof ImpossibleJad) {
				player.getPackets().sendGameMessage(
						"You're not allowed to use prayer in here.");
				return;
			}
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					if (componentId == 8 || componentId == 42)
						player.getPrayer().switchPrayer(slotId);

					else if (componentId == 43
							&& player.getPrayer().isUsingQuickPrayer())
						player.getPrayer().switchSettingQuickPrayer();
				}
			});
			break;
		case 320: {
		player.stopAll();
		int lvlupSkill = -1;
		int skillMenu = -1;
		switch (componentId) {
		case 150: // Attack
			skillMenu = 1;
			if (player.getTemporaryAttributtes().remove("leveledUp[0]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 1);
			} else {
				lvlupSkill = 0;
				player.getPackets().sendConfig(1230, 10);
			}
			break;
		case 9: // Strength
			skillMenu = 2;
			if (player.getTemporaryAttributtes().remove("leveledUp[2]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 2);
			} else {
				lvlupSkill = 2;
				player.getPackets().sendConfig(1230, 20);
			}
			break;
		case 22: // Defence
			skillMenu = 5;
			if (player.getTemporaryAttributtes().remove("leveledUp[1]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 5);
			} else {
				lvlupSkill = 1;
				player.getPackets().sendConfig(1230, 40);
			}
			break;
		case 40: // Ranged
			skillMenu = 3;
			if (player.getTemporaryAttributtes().remove("leveledUp[4]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 3);
			} else {
				lvlupSkill = 4;
				player.getPackets().sendConfig(1230, 30);
			}
			break;
		case 58: // Prayer
			if (player.getTemporaryAttributtes().remove("leveledUp[5]") != Boolean.TRUE) {
				skillMenu = 7;
				player.getPackets().sendConfig(965, 7);
			} else {
				lvlupSkill = 5;
				player.getPackets().sendConfig(1230, 60);
			}
			break;
		case 71: // Magic
			if (player.getTemporaryAttributtes().remove("leveledUp[6]") != Boolean.TRUE) {
				skillMenu = 4;
				player.getPackets().sendConfig(965, 4);
			} else {
				lvlupSkill = 6;
				player.getPackets().sendConfig(1230, 33);
			}
			break;
		case 84: // Runecrafting
			if (player.getTemporaryAttributtes().remove("leveledUp[20]") != Boolean.TRUE) {
				skillMenu = 12;
				player.getPackets().sendConfig(965, 12);
			} else {
				lvlupSkill = 20;
				player.getPackets().sendConfig(1230, 100);
			}
			break;
		case 102: // Construction
			skillMenu = 22;
			if (player.getTemporaryAttributtes().remove("leveledUp[21]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 22);
			} else {
				lvlupSkill = 21;
				player.getPackets().sendConfig(1230, 698);
			}
			break;
		case 145: // Hitpoints
			skillMenu = 6;
			if (player.getTemporaryAttributtes().remove("leveledUp[3]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 6);
			} else {
				lvlupSkill = 3;
				player.getPackets().sendConfig(1230, 50);
			}
			break;
		case 15: // Agility
			skillMenu = 8;
			if (player.getTemporaryAttributtes().remove("leveledUp[16]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 8);
			} else {
				lvlupSkill = 16;
				player.getPackets().sendConfig(1230, 65);
			}
			break;
		case 28: // Herblore
			skillMenu = 9;
			if (player.getTemporaryAttributtes().remove("leveledUp[15]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 9);
			} else {
				lvlupSkill = 15;
				player.getPackets().sendConfig(1230, 75);
			}
			break;
		case 46: // Thieving
			skillMenu = 10;
			if (player.getTemporaryAttributtes().remove("leveledUp[17]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 10);
			} else {
				lvlupSkill = 17;
				player.getPackets().sendConfig(1230, 80);
			}
			break;
		case 64: // Crafting
			skillMenu = 11;
			if (player.getTemporaryAttributtes().remove("leveledUp[12]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 11);
			} else {
				lvlupSkill = 12;
				player.getPackets().sendConfig(1230, 90);
			}
			break;
		case 77: // Fletching
			skillMenu = 19;
			if (player.getTemporaryAttributtes().remove("leveledUp[9]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 19);
			} else {
				lvlupSkill = 9;
				player.getPackets().sendConfig(1230, 665);
			}
			break;
		case 90: // Slayer
			skillMenu = 20;
			if (player.getTemporaryAttributtes().remove("leveledUp[18]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 20);
			} else {
				lvlupSkill = 18;
				player.getPackets().sendConfig(1230, 673);
			}
			break;
		case 108: // Hunter
			skillMenu = 23;
			if (player.getTemporaryAttributtes().remove("leveledUp[22]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 23);
			} else {
				lvlupSkill = 22;
				player.getPackets().sendConfig(1230, 689);
			}
			break;
		case 140: // Mining
			skillMenu = 13;
			if (player.getTemporaryAttributtes().remove("leveledUp[14]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 13);
			} else {
				lvlupSkill = 14;
				player.getPackets().sendConfig(1230, 110);
			}
			break;
		case 135: // Smithing
			skillMenu = 14;
			if (player.getTemporaryAttributtes().remove("leveledUp[13]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 14);
			} else {
				lvlupSkill = 13;
				player.getPackets().sendConfig(1230, 115);
			}
			break;
		case 34: // Fishing
			skillMenu = 15;
			if (player.getTemporaryAttributtes().remove("leveledUp[10]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 15);
			} else {
				lvlupSkill = 10;
				player.getPackets().sendConfig(1230, 120);
			}
			break;
		case 52: // Cooking
			skillMenu = 16;
			if (player.getTemporaryAttributtes().remove("leveledUp[7]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 16);
			} else {
				lvlupSkill = 7;
				player.getPackets().sendConfig(1230, 641);
			}
			break;
		case 130: // Firemaking
			skillMenu = 17;
			if (player.getTemporaryAttributtes().remove("leveledUp[11]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 17);
			} else {
				lvlupSkill = 11;
				player.getPackets().sendConfig(1230, 649);
			}
			break;
		case 125: // Woodcutting
			skillMenu = 18;
			if (player.getTemporaryAttributtes().remove("leveledUp[8]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 18);
			} else {
				lvlupSkill = 8;
				player.getPackets().sendConfig(1230, 660);
			}
			break;
		case 96: // Farming
			skillMenu = 21;
			if (player.getTemporaryAttributtes().remove("leveledUp[19]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 21);
			} else {
				lvlupSkill = 19;
				player.getPackets().sendConfig(1230, 681);
			}
			break;
		case 114: // Summoning
			skillMenu = 24;
			if (player.getTemporaryAttributtes().remove("leveledUp[23]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 24);
			} else {
				lvlupSkill = 23;
				player.getPackets().sendConfig(1230, 705);
			}
			break;
		case 120: // Dung
			skillMenu = 25;
			if (player.getTemporaryAttributtes().remove("leveledUp[24]") != Boolean.TRUE) {
				player.getPackets().sendConfig(965, 25);
			} else {
				lvlupSkill = 24;
				player.getPackets().sendConfig(1230, 705);
			}
			break;
		}
		player.getInterfaceManager().sendScreenInterface(317, 1218);
		player.getPackets().sendInterface(false, 1218, 1, 1217); // seems to
		// fix
		if (lvlupSkill != -1)
			LevelUp.switchFlash(player, lvlupSkill, false);
		if (skillMenu != -1)
			player.getTemporaryAttributtes().put("skillMenu", skillMenu);
			break;
		}
		case 1218:
			if ((componentId >= 33 && componentId <= 55) || componentId == 120
			|| componentId == 151 || componentId == 189)
		player.getPackets().sendInterface(false, 1218, 1, 1217);
			break;
		case 499:
			int skillMenu = -1;
			if (player.getTemporaryAttributtes().get("skillMenu") != null)
				skillMenu = (Integer) player.getTemporaryAttributtes().get(
						"skillMenu");
			if (componentId >= 10 && componentId <= 25)
				player.getPackets().sendConfig(965,
						((componentId - 10) * 1024) + skillMenu);
			else if (componentId == 29)
				// close inter
				player.stopAll();
			break;
		case 387:
			if (player.getInterfaceManager().containsInventoryInter())
				return;
			if (componentId == 21) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET
						&& shieldId == 11283) {
					if (player.getdfscoolDown() < 1) {
						player.setDfsActivated(true);
					} else {
						player.getPackets()
								.sendGameMessage(
										"Your dragonfire shield is still cooling down.");
						player.setDfsActivated(false);
					}
					return; // So it doesn't unequip the shield
				}
			}
			if (componentId == 6) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == 24437 || hatId == 24439 || hatId == 24440
							|| hatId == 24441) {
						player.getDialogueManager().startDialogue(
								"FlamingSkull",
								player.getEquipment().getItem(
										Equipment.SLOT_HAT), -1);
						return;
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_HAT);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_HAT);
			} else if (componentId == 9) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20769 || capeId == 20771) {
						player.getSkills().restoreSummoning();
						player.setNextAnimation(new Animation(8502));
						player.setNextGraphics(new Graphics(1308));
						player.getPackets()
								.sendGameMessage(
										"You restored your Summoning points with your awesome cape!",
										true);
					}
				}
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20769 || capeId == 20771)
						SkillCapeCustomizer.startCustomizing(player, capeId);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20767)
						SkillCapeCustomizer.startCustomizing(player, capeId);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_CAPE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_CAPE);
			} else if (componentId == 12) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
				    ItemTransportation.sendTeleport(player, player.getEquipment().getItem(Equipment.getItemSlot(slotId2)), 0, true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
				    ItemTransportation.sendTeleport(player, player.getEquipment().getItem(Equipment.getItemSlot(slotId2)), 1, true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
				    ItemTransportation.sendTeleport(player, player.getEquipment().getItem(Equipment.getItemSlot(slotId2)), 2, true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
				    ItemTransportation.sendTeleport(player, player.getEquipment().getItem(Equipment.getItemSlot(slotId2)), 3, true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
				    ButtonHandler.sendRemove(player, Equipment.SLOT_AMULET);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
				    player.getEquipment().sendExamine(Equipment.SLOT_AMULET);
			} else if (componentId == 15) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					// int weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 15484)
						player.getInterfaceManager().gazeOrbOfOculus();
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_WEAPON);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_WEAPON);
			} else if (componentId == 18) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_CHEST);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_CHEST);
			} else if (componentId == 21) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_SHIELD);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_SHIELD);
			} else if (componentId == 24) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_LEGS);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_LEGS);
			} else if (componentId == 27) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_HANDS);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_HANDS);
			} else if (componentId == 30) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_FEET);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_FEET);
			} else if (componentId == 33) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_RING);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_RING);
			} else if (componentId == 37) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_ARROWS);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_ARROWS);
			} else if (componentId == 46) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					ButtonHandler.sendRemove(player, Equipment.SLOT_AURA);
					player.getAuraManager().removeAura();
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_AURA);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getAuraManager().activate();
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getAuraManager().sendAuraRemainingTime();
			} else if (componentId == 41) {
				openItemsKeptOnDeath(player);
			} else if (componentId == 42) {
				player.getInterfaceManager().sendInterface(1178);
			} else if (componentId == 43) {
				player.getInterfaceManager().sendScreenInterface(317, 1311);
				player.getPackets().sendConfig(2793, 93);
				player.getPackets().sendConfig(2686, 2);
				player.getPackets().sendConfig(2795, 14);
				player.getPackets().sendConfig(2689, 0);
				player.getPackets().sendConfig2(2687, 25165824);
				player.getPackets().sendConfig2(2687, 25165979);
				player.getPackets().sendConfig2(2687, 25226395);
				player.getPackets().sendConfig2(2666, 1023);
				player.getPackets().sendConfig2(2666, 1048575);
				player.getPackets().sendConfig2(2666, 1073741823);
				player.getPackets().sendConfig2(2667, 1023);
				player.getPackets().sendConfig2(2667, 1048575);
				player.getPackets().sendConfig2(2667, 1073741823);
				player.getPackets().sendConfig2(2686, 4194306);
				player.getPackets().sendConfig2(2668, 1023);
				player.getPackets().sendConfig2(2668, 1048575);
				player.getPackets().sendConfig2(2668, 1073741823);
				player.getPackets().sendConfig2(2686, 8355843);
				player.getPackets().sendConfig2(2686, 8388579);
				player.getPackets().sendGlobalConfig(2017, 7);
				player.getPackets().sendGlobalConfig(2018, 0);
				player.getPackets().sendGlobalConfig(1963, -1);
				player.getPackets().sendGlobalConfig(1964, -1);
				player.getPackets().sendGlobalConfig(1965, -1);
				player.getPackets().sendGlobalConfig(1966, -1);
				player.getPackets().sendRunScript(6874);
				player.getPackets().sendRunScript(6453);
				player.getPackets().sendRunScript(6462);
				player.getPackets().sendRunScript(140);
			} else if (componentId == 38) {
				openEquipmentBonuses(player, false);
			}
			break;
		case 1265: {
			Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
			if (shop == null)
				return;
			if (componentId == 49 || componentId == 50) {
				player.setVerboseShopDisplayMode(componentId == 50);
			} else if (componentId == 28 || componentId == 29) {
				Shop.setBuying(player, componentId == 28);
			} else if (componentId == 20) {
				boolean buying = Shop.isBuying(player);
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					shop.sendInfo(player, slotId, !buying);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					if (buying) {
						shop.buy(player, slotId, 1);
					} else {
						shop.sell(player, slotId, 1);
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					if (buying) {
						shop.buy(player, slotId, 5);
					} else {
						shop.sell(player, slotId, 5);
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					if (buying) {
						shop.buy(player, slotId, 10);
					} else {
						shop.sell(player, slotId, 10);
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					if (buying) {
						shop.buy(player, slotId, 50);
					} else {
						shop.sell(player, slotId, 50);
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
					if (buying) {
						shop.buy(player, slotId, 500);
					} else {
						shop.sell(player, slotId, 500);
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET) {
					if (buying) {
						shop.buyAll(player, slotId);
					}
				}
			} else if (componentId == 220) {
				shop.setTransaction(player, 1);
			} else if (componentId == 217) {
				shop.increaseTransaction(player, -5);
			} else if (componentId == 214) {
				shop.increaseTransaction(player, -1);
			} else if (componentId == 15) {
				shop.increaseTransaction(player, 1);
			} else if (componentId == 208) {
				shop.increaseTransaction(player, 5);
			} else if (componentId == 211) {
				shop.setTransaction(player, Integer.MAX_VALUE);
			} else if (componentId == 201) {
				shop.pay(player);
			}
			break;
		}
		case 1266:
			if (componentId == 0) {
				Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
				if (shop == null)
					return;
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					shop.sendInfo(player, slotId, true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					shop.sell(player, slotId, 1);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					shop.sell(player, slotId, 5);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					shop.sell(player, slotId, 10);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					shop.sell(player, slotId, 50);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
					player.getInventory().sendExamine(slotId);
				}
			}
			break;
		case 634:
			if (componentId == 28) {
				Item item = new Item(slotId2);
				player.getPackets().sendGameMessage(
						ItemExamines.getExamine(item));
			}
			break;
		case 650:
			if (componentId == 15) {
				player.stopAll();
				player.setNextTile(new Tile(2974, 4384, player.getZ()));
				player.getControllerManager().startController(
						"CorpBeastControler");
			} else if (componentId == 16)
				player.closeInterfaces();
			break;
		case 667:
			if (componentId == 9 && slotId == 0) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_HAT);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 1) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_CAPE);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 2) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_AMULET);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 3) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_WEAPON);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 4) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_CHEST);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 5) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_SHIELD);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 7) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_LEGS);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 9) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_HANDS);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 10) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_FEET);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 12) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_RING);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 13) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_ARROWS);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 14) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_AURA);
				ButtonHandler.refreshEquipBonuses(player);
			}
			if (componentId == 14) {
				if (slotId >= 14)
					return;
				Item item = player.getEquipment().getItem(slotId);
				if (item == null)
					return;
				if (packetId == 3)
					player.getPackets().sendGameMessage(
							ItemExamines.getExamine(item));
				else if (packetId == 216) {
					sendRemove(player, slotId);
					ButtonHandler.refreshEquipBonuses(player);
				}
			} else if (componentId == 46
					&& player.getTemporaryAttributtes().remove("Banking") != null) {
				player.getBank().openBank();
			}
			break;
		case 670:
			if (componentId == 0) {
				if (slotId >= player.getInventory().getItemsContainerSize())
					return;
				Item item = player.getInventory().getItem(slotId);
				if (item == null)
					return;
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					if (sendWear(player, slotId, item.getId()))
						ButtonHandler.refreshEquipBonuses(player);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getInventory().sendExamine(slotId);
			}
			break;
		case Inventory.INVENTORY_INTERFACE:
			if (componentId == 0) {
				if (slotId > 27)
					return;
				Item item = player.getInventory().getItem(slotId);
				if (item == null || item.getId() != slotId2)
					return;
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					InventoryOptionsHandler.handleItemOption1(player, slotId,
							slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					InventoryOptionsHandler.handleItemOption2(player, slotId,
							slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					InventoryOptionsHandler.handleItemOption3(player, slotId,
							slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					InventoryOptionsHandler.handleItemOption4(player, slotId,
							slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					InventoryOptionsHandler.handleItemOption5(player, slotId,
							slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					InventoryOptionsHandler.handleItemOption6(player, slotId,
							slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					InventoryOptionsHandler.handleItemOption7(player, slotId,
							slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					InventoryOptionsHandler.handleItemOption8(player, slotId,
							slotId2, item);
			}
			break;
		case 742:
			if (componentId == 46) // close
				player.stopAll();
			break;
		case 743:
			if (componentId == 20) // close
				player.stopAll();
			break;
		case 741:
			if (componentId == 9) // close
				player.stopAll();
			break;
		case 749:
			if (player.getControllerManager().getController() instanceof ImpossibleJad) {
				player.getPackets().sendGameMessage(
						"You're not allowed to use prayer in here.");
				return;
			}
			if (componentId == 4) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) // activate
					player.getPrayer().switchQuickPrayers();
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) // switch
					player.getPrayer().switchSettingQuickPrayer();
			}
			break;
		case 750:
			if (componentId == 4) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					player.toogleRun(player.isResting() ? false : true);
					if (player.isResting())
						player.stopAll();
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
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
			}
			break;
		case 11:
			if (componentId == 17) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getBank().depositItem(slotId, 1, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getBank().depositItem(slotId, 5, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getBank().depositItem(slotId, 10, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getBank().depositItem(slotId, Integer.MAX_VALUE,
							false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bank_item_X_Slot",
							slotId);
					player.getTemporaryAttributtes().remove("bank_isWithdraw");
					player.getPackets().sendRunScript(108,
							new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getInventory().sendExamine(slotId);
			} else if (componentId == 18)
				player.getBank().depositAllInventory(false);
			else if (componentId == 20)
				player.getBank().depositAllEquipment(false);
			break;
		case 762:
			if (componentId == 15)
				player.getBank().switchInsertItems();
			else if (componentId == 19)
				player.getBank().switchWithdrawNotes();
			else if (componentId == 33)
				player.getBank().depositAllInventory(true);
			/*
			 * else if (componentId == 35)
			 * player.getBank().depositMoneyPouch(true);
			 */
			else if (componentId == 37)
				player.getBank().depositAllEquipment(true);
			else if (componentId == 39)
				player.getBank().depositAllBob(true);
			else if (componentId == 46) {
				player.closeInterfaces();
				player.getInterfaceManager().sendInterface(767);
				player.setCloseInterfacesEvent(new Runnable() {
					@Override
					public void run() {
						player.getBank().openBank();
					}
				});
			} else if (componentId >= 46 && componentId <= 64) {
				int tabId = 9 - ((componentId - 46) / 2);
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getBank().setCurrentTab(tabId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getBank().collapse(tabId);
			} else if (componentId == 95) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getBank().withdrawItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getBank().withdrawItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getBank().withdrawItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getBank().withdrawLastAmount(slotId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bank_item_X_Slot",
							slotId);
					player.getTemporaryAttributtes().put("bank_isWithdraw",
							Boolean.TRUE);
					player.getPackets().sendRunScript(108,
							new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getBank().withdrawItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					player.getBank().withdrawItemButOne(slotId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getBank().sendExamine(slotId);

			} else if (componentId == 119) {
				openEquipmentBonuses(player, true);
			}
			break;
		case 763:
			if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getBank().depositItem(slotId, 1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getBank().depositItem(slotId, 5, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getBank().depositItem(slotId, 10, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getBank().depositLastAmount(slotId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bank_item_X_Slot",
							slotId);
					player.getTemporaryAttributtes().remove("bank_isWithdraw");
					player.getPackets().sendRunScript(108,
							new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getBank().depositItem(slotId, Integer.MAX_VALUE,
							true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getInventory().sendExamine(slotId);
			}
			break;
		case 767:
			if (componentId == 10)
				player.getBank().openBank();
			break;
		case 884:
			if (componentId == 4) {
				// int weaponId = player.getEquipment().getWeaponId();
				if (player.hasInstantSpecial(weaponId)) {
					player.performInstantSpecial(weaponId);
					return;
				}
				submitSpecialRequest(player);
			} else if (componentId >= 7 && componentId <= 10)
				player.getCombatDefinitions().setAttackStyle(componentId - 7);
			else if (componentId == 11)
				player.getCombatDefinitions().switchAutoRelatie();
			break;
		case 755:
			if (componentId == 44)
				player.getPackets().sendWindowsPane(
						player.getInterfaceManager().hasRezizableScreen() ? 746
								: 548, 2);
			else if (componentId == 42) {
				player.getHintIconsManager().removeAll();
				player.getPackets().sendConfig(1159, 1);
			}
			break;
		case 20:
			SkillCapeCustomizer.handleSkillCapeCustomizer(player, componentId);
			break;
		case 1056:
			if (componentId == 57)
				player.getInterfaceManager().sendScreenInterface(317, 1345);
			break;
		case 1343:
			if (componentId == 27)
				player.getInterfaceManager().sendScreenInterface(317, 1345);
			if (componentId == 28)
				return;
			if (componentId == 29)
				player.getInterfaceManager().sendScreenInterface(317, 190);
			if (componentId == 30)
				player.getInterfaceManager().sendScreenInterface(317, 917);
			if (componentId == 44)
				player.getInterfaceManager().sendScreenInterface(317, 1344);
			break;
		case 1344:
			if (componentId == 65)
				player.getInterfaceManager().sendScreenInterface(317, 1345);
			if (componentId == 70)
				player.getInterfaceManager().sendScreenInterface(317, 1343);
			if (componentId == 75)
				player.getInterfaceManager().sendScreenInterface(317, 190);
			if (componentId == 80)
				player.getInterfaceManager().sendScreenInterface(317, 917);
			if (componentId == 85)
				return;
			break;
		case 1345:
			if (componentId == 178)
				return;
			if (componentId == 183)
				player.getInterfaceManager().sendScreenInterface(317, 1343);
			if (componentId == 188)
				player.getInterfaceManager().sendScreenInterface(317, 190);
			if (componentId == 193)
				player.getInterfaceManager().sendScreenInterface(317, 917);
			if (componentId == 198)
				player.getInterfaceManager().sendScreenInterface(317, 1344);
			break;
		case 190:
			if (componentId == 225)
				player.getInterfaceManager().sendScreenInterface(317, 1345);
			if (componentId == 230)
				player.getInterfaceManager().sendScreenInterface(317, 1343);
			if (componentId == 235)
				return;
			if (componentId == 240)
				player.getInterfaceManager().sendScreenInterface(317, 917);
			if (componentId == 245)
				player.getInterfaceManager().sendScreenInterface(317, 1344);
			break;
		case 917:
			if (componentId == 211)
				player.getInterfaceManager().sendScreenInterface(317, 1345);
			if (componentId == 216)
				player.getInterfaceManager().sendScreenInterface(317, 1343);
			if (componentId == 221)
				player.getInterfaceManager().sendScreenInterface(317, 190);
			if (componentId == 226)
				return;
			if (componentId == 231)
				player.getInterfaceManager().sendScreenInterface(317, 1344);
			break;
		case 751:
			if (!Engine.containsLobbyPlayer(player.getUsername())) {
				if (componentId == 26) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.getFriendsIgnores().setPrivateStatus(0);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.getFriendsIgnores().setPrivateStatus(1);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.getFriendsIgnores().setPrivateStatus(2);
				} else if (componentId == 32) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.setFilterGame(false);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.setFilterGame(true);
				} else if (componentId == 29) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.setPublicStatus(0);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.setPublicStatus(1);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.setPublicStatus(2);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
						player.setPublicStatus(3);
				} else if (componentId == 0) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.getFriendsIgnores().setFriendsChatStatus(0);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.getFriendsIgnores().setFriendsChatStatus(1);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.getFriendsIgnores().setFriendsChatStatus(2);
				} else if (componentId == 23) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.setClanStatus(0);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.setClanStatus(1);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.setClanStatus(2);
				} else if (componentId == 20) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.setTradeStatus(0);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.setTradeStatus(1);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.setTradeStatus(2);
				} else if (componentId == 17) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.setAssistStatus(0);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.setAssistStatus(1);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.setAssistStatus(2);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
						// ASSIST XP Earned/Time
					}
				}
			}
			break;
		case 1163:
		case 1164:
		case 1168:
		case 1170:
		case 1173:
			player.getDominionTower().handleButtons(interfaceId, componentId);
			break;
		case 900:
			PlayerLook.handleMageMakeOverButtons(player, componentId);
			break;
		case 1028:
			PlayerDesign.handle(player, componentId, slotId);
			break;
		case 1108:
		case 1109:
			player.getFriendsIgnores().handleFriendChatButtons(interfaceId,
					componentId, packetId);
			break;
		case 374:
			if (componentId >= 5 && componentId <= 9)
				player.setNextTile(new Tile(
						FightPitsViewingOrb.ORB_TELEPORTS[componentId - 5]));
			else if (componentId == 15)
				player.stopAll();
			break;
		case 1089:
			if (componentId == 30)
				player.getTemporaryAttributtes().put("clanflagselection",
						slotId);
			else if (componentId == 26) {
				Integer flag = (Integer) player.getTemporaryAttributtes()
						.remove("clanflagselection");
				player.stopAll();
				if (flag != null)
					ClansManager.setClanFlagInterface(player, flag);
			}
			break;
		case 1096:
			if (componentId == 41)
				ClansManager.viewClammateDetails(player, slotId);
			else if (componentId == 94)
				ClansManager.switchGuestsInChatCanEnterInterface(player);
			else if (componentId == 95)
				ClansManager.switchGuestsInChatCanTalkInterface(player);
			else if (componentId == 96)
				ClansManager.switchRecruitingInterface(player);
			else if (componentId == 97)
				ClansManager.switchClanTimeInterface(player);
			else if (componentId == 124)
				ClansManager.openClanMottifInterface(player);
			else if (componentId == 131)
				ClansManager.openClanMottoInterface(player);
			else if (componentId == 240)
				ClansManager.setTimeZoneInterface(player, -720 + slotId * 10);
			else if (componentId == 262)
				player.getTemporaryAttributtes().put("editclanmatejob", slotId);
			else if (componentId == 276)
				player.getTemporaryAttributtes()
						.put("editclanmaterank", slotId);
			else if (componentId == 309)
				ClansManager.kickClanmate(player);
			else if (componentId == 318)
				ClansManager.saveClanmateDetails(player);
			else if (componentId == 290)
				ClansManager.setWorldIdInterface(player, slotId);
			else if (componentId == 297)
				ClansManager.openForumThreadInterface(player);
			else if (componentId == 346)
				ClansManager.openNationalFlagInterface(player);
			else if (componentId == 113)
				ClansManager.showClanSettingsClanMates(player);
			else if (componentId == 120)
				ClansManager.showClanSettingsSettings(player);
			else if (componentId == 133)
				ClansManager.showClanSettingsPermissions(player);
			else if (componentId >= 395 && componentId <= 475) {
				int selectedRank = (componentId - 395) / 8;
				if (selectedRank == 10)
					selectedRank = 125;
				else if (selectedRank > 5)
					selectedRank = 100 + selectedRank - 6;
				ClansManager.selectPermissionRank(player, selectedRank);
			} else if (componentId == 489)
				ClansManager.selectPermissionTab(player, 1);
			else if (componentId == 498)
				ClansManager.selectPermissionTab(player, 2);
			else if (componentId == 506)
				ClansManager.selectPermissionTab(player, 3);
			else if (componentId == 514)
				ClansManager.selectPermissionTab(player, 4);
			else if (componentId == 522)
				ClansManager.selectPermissionTab(player, 5);
			break;
		case 1105:
			if (componentId == 63 || componentId == 66)
				ClansManager.setClanMottifTextureInterface(player,
						componentId == 66, slotId);
			else if (componentId == 35)
				ClansManager.openSetMottifColor(player, 0);
			else if (componentId == 80)
				ClansManager.openSetMottifColor(player, 1);
			else if (componentId == 92)
				ClansManager.openSetMottifColor(player, 2);
			else if (componentId == 104)
				ClansManager.openSetMottifColor(player, 3);
			else if (componentId == 120)
				player.stopAll();
			break;
		case 1110:
			if (componentId == 82)
				ClansManager.joinClanChatChannel(player);
			else if (componentId == 75)
				ClansManager.openClanDetails(player);
			else if (componentId == 78)
				ClansManager.openClanSettings(player);
			else if (componentId == 91)
				ClansManager.joinGuestClanChat(player);
			else if (componentId == 95)
				ClansManager.banPlayer(player);
			else if (componentId == 99)
				ClansManager.unbanPlayer(player);
			else if (componentId == 11)
				ClansManager.unbanPlayer(player, slotId);
			else if (componentId == 109)
				ClansManager.leaveClan(player);
			break;
		case 1079:
			player.closeInterfaces();
			break;
		case 1092:
			player.getLodeStones().handleButtons(componentId);
			break;
		case 675:
			JewelrySmithing.handleButtonClick(player, componentId,
					packetId == 14 ? 1 : packetId == 67 ? 5 : 10);
			break;
		case 1214:
			player.getSkills().handleSetupXPCounter(componentId);
			break;
		case 1292:
			if (componentId == 12)
				Crucible.enterArena(player);
			else if (componentId == 13)
				player.closeInterfaces();
			break;
		case 590:
		case 464:
			if (componentId == 8) {
				player.getEmotesManager().useBookEmote(
						interfaceId == 464 ? componentId : EmotesManager.getId(
								slotId, packetId));
			}
		}
	}

	public static void openItemsKeptOnDeath(Player player) {
		player.getInterfaceManager().sendInterface(17);
		sendItemsKeptOnDeath(player, Wilderness.isAtWild(player));
	}

	public static void openEquipmentBonuses(final Player player, boolean banking) {
		player.stopAll();
		player.getInterfaceManager().closeInterface(11, 0);
		player.getInterfaceManager().sendInventoryInterface(670);
		player.getInterfaceManager().sendInterface(667);
		player.getPackets().sendConfigByFile(4894, banking ? 1 : 0);
		player.getPackets().sendRunScript(787, 1);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().sendInterSetItemsOptionsScript(670, 0, 93, 4, 7,
				"Equip", "Compare", "Stats", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(670, 0, 0, 27, 0,
				1, 2, 3);
		player.getPackets().sendUnlockIComponentOptionSlots(667, 9, 0, 14, 0);
		player.getPackets().sendIComponentSettings(667, 14, 0, 13, 1030);
		refreshEquipBonuses(player);
		if (banking) {
			player.getTemporaryAttributtes().put("Banking", Boolean.TRUE);
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					player.getTemporaryAttributtes().remove("Banking");
				}

			});
		}
	}

	public static Item[][] getItemsKeptOnDeath(Player player, Integer[][] slots) {
		ArrayList<Item> droppedItems = new ArrayList<Item>();
		ArrayList<Item> keptItems = new ArrayList<Item>();
		for (int i : slots[0]) { // items kept on death
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null) // shouldnt
				continue;
			if (item.getAmount() > 1) {
				droppedItems.add(new Item(item.getId(), item.getAmount() - 1));
				item.setAmount(1);
			}
			keptItems.add(item);
		}
		for (int i : slots[1]) { // items droped on death
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null) // shouldnt
				continue;
			droppedItems.add(item);
		}
		for (int i : slots[2]) { // items protected by default
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null) // shouldnt
				continue;
			keptItems.add(item);
		}
		return new Item[][] { keptItems.toArray(new Item[keptItems.size()]),
				droppedItems.toArray(new Item[droppedItems.size()]) };

	}

	public static Integer[][] getItemSlotsKeptOnDeath(final Player player,
			boolean atWilderness, boolean skulled, boolean protectPrayer) {
		ArrayList<Integer> droppedItems = new ArrayList<Integer>();
		ArrayList<Integer> protectedItems = atWilderness ? null
				: new ArrayList<Integer>();
		ArrayList<Integer> lostItems = new ArrayList<Integer>();
		for (int i = 1; i < 44; i++) {
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null)
				continue;
			int stageOnDeath = item.getDefinitions().getStageOnDeath();
			if (!atWilderness && stageOnDeath == 1)
				protectedItems.add(i);
			else if (stageOnDeath == -1)
				lostItems.add(i);
			else
				droppedItems.add(i);
		}
		int keptAmount = skulled ? 0 : 3;
		if (protectPrayer)
			keptAmount++;
		if (droppedItems.size() < keptAmount)
			keptAmount = droppedItems.size();
		Collections.sort(droppedItems, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				Item i1 = o1 >= 16 ? player.getInventory().getItem(o1 - 16)
						: player.getEquipment().getItem(o1 - 1);
				Item i2 = o2 >= 16 ? player.getInventory().getItem(o2 - 16)
						: player.getEquipment().getItem(o2 - 1);
				int price1 = i1 == null ? 0 : i1.getDefinitions().value;
				int price2 = i2 == null ? 0 : i2.getDefinitions().value;
				if (price1 > price2)
					return -1;
				if (price1 < price2)
					return 1;
				return 0;
			}

		});
		Integer[] keptItems = new Integer[keptAmount];
		for (int i = 0; i < keptAmount; i++)
			keptItems[i] = droppedItems.remove(0);
		return new Integer[][] {
				keptItems,
				droppedItems.toArray(new Integer[droppedItems.size()]),
				atWilderness ? new Integer[0] : protectedItems
						.toArray(new Integer[protectedItems.size()]),
				atWilderness ? new Integer[0] : lostItems
						.toArray(new Integer[lostItems.size()]) };

	}

	public static void sendItemsKeptOnDeath(Player player, boolean wilderness) {
		boolean skulled = player.hasSkull();
		Integer[][] slots = getItemSlotsKeptOnDeath(player, wilderness,
				skulled, player.getPrayer().isProtectingItem());
		Item[][] items = getItemsKeptOnDeath(player, slots);
		long riskedWealth = 0;
		long carriedWealth = 0;
		for (Item item : items[1]) {
			if (carriedWealth + riskedWealth + Prices.getDeathPrice(item) > Integer.MAX_VALUE) {
				carriedWealth = Integer.MAX_VALUE;
				break;
			}
			carriedWealth = riskedWealth += Prices.getDeathPrice(item);
		}
		for (Item item : items[0]) {
			if (carriedWealth + Prices.getDeathPrice(item) > Integer.MAX_VALUE) {
				carriedWealth = Integer.MAX_VALUE;
				break;
			}
			carriedWealth += Prices.getDeathPrice(item);
		}
		if (slots[0].length > 0) {
			for (int i = 0; i < slots[0].length; i++)
				player.getPackets().sendConfigByFile(9222 + i, slots[0][i]);
			player.getPackets().sendConfigByFile(9227, slots[0].length);
		} else {
			player.getPackets().sendConfigByFile(9222, -1);
			player.getPackets().sendConfigByFile(9227, 1);
		}
		player.getPackets().sendConfigByFile(9226, wilderness ? 1 : 0);
		player.getPackets().sendConfigByFile(9229, skulled ? 1 : 0);
		StringBuffer text = new StringBuffer();
		text.append("The number of items kept on").append("<br>")
				.append("death is normally 3.").append("<br>").append("<br>")
				.append("<br>");
		if (wilderness) {
			text.append("Your gravestone will not").append("<br>")
					.append("appear.");
		}
		text.append("<br>")
				.append("<br>")
				.append("Carried wealth:")
				.append("<br>")
				.append(carriedWealth > Integer.MAX_VALUE ? "Too high!"
						: Utilities.getFormattedNumber((int) carriedWealth))
				.append("<br>")
				.append("<br>")
				.append("Risked wealth:")
				.append("<br>")
				.append(riskedWealth > Integer.MAX_VALUE ? "Too high!"
						: Utilities.getFormattedNumber((int) riskedWealth))
				.append("<br>").append("<br>");
		if (wilderness) {
			text.append("Your hub will be set to:").append("<br>")
					.append("Edgeville.");
		} else {
			text.append("Your hub will be set to:").append("<br>")
					.append("Sarah's Kitchen.");
		}
		player.getPackets().sendGlobalString(352, text.toString());
	}

	public static String getPrefix(Player player, int slotId) {
		int i = player.getCombatDefinitions().getBonuses()[slotId];
		String prefix;
		String n = String.valueOf(i);
		if (n.startsWith("-") || n.contentEquals("0")) {
			prefix = "";
		} else
			prefix = "+";
		return prefix;
	}

	public static void refreshEquipBonuses(Player player) {
		player.getPackets().sendWeight(Utilities.getWeight(player));
		player.getPackets().sendIComponentText(
				667,
				28,
				"Stab: " + getPrefix(player, 0)
						+ player.getCombatDefinitions().getBonuses()[0]);
		player.getPackets().sendIComponentText(
				667,
				29,
				"Slash: " + getPrefix(player, 1)
						+ player.getCombatDefinitions().getBonuses()[1]);
		player.getPackets().sendIComponentText(
				667,
				30,
				"Crush: " + getPrefix(player, 2)
						+ player.getCombatDefinitions().getBonuses()[2]);
		player.getPackets().sendIComponentText(
				667,
				31,
				"Magic: " + getPrefix(player, 3)
						+ player.getCombatDefinitions().getBonuses()[3]);
		player.getPackets().sendIComponentText(
				667,
				32,
				"Range: " + getPrefix(player, 4)
						+ player.getCombatDefinitions().getBonuses()[4]);
		player.getPackets().sendIComponentText(
				667,
				33,
				"Stab: " + getPrefix(player, 5)
						+ player.getCombatDefinitions().getBonuses()[5]);
		player.getPackets().sendIComponentText(
				667,
				34,
				"Slash: " + getPrefix(player, 6)
						+ player.getCombatDefinitions().getBonuses()[6]);
		player.getPackets().sendIComponentText(
				667,
				35,
				"Crush: " + getPrefix(player, 7)
						+ player.getCombatDefinitions().getBonuses()[7]);
		player.getPackets().sendIComponentText(
				667,
				36,
				"Magic: " + getPrefix(player, 8)
						+ player.getCombatDefinitions().getBonuses()[8]);
		player.getPackets().sendIComponentText(
				667,
				37,
				"Range: " + getPrefix(player, 9)
						+ player.getCombatDefinitions().getBonuses()[9]);
		player.getPackets().sendIComponentText(
				667,
				38,
				"Summoning: " + getPrefix(player, 10)
						+ player.getCombatDefinitions().getBonuses()[10]);
		player.getPackets().sendIComponentText(
				667,
				39,
				"Absorb Melee: " + getPrefix(player, 11)
						+ player.getCombatDefinitions().getBonuses()[11] + "%");
		player.getPackets().sendIComponentText(
				667,
				40,
				"Absorb Magic: " + getPrefix(player, 12)
						+ player.getCombatDefinitions().getBonuses()[12] + "%");
		player.getPackets().sendIComponentText(
				667,
				41,
				"Absorb Ranged: " + getPrefix(player, 13)
						+ player.getCombatDefinitions().getBonuses()[13] + "%");
		player.getPackets().sendIComponentText(
				667,
				42,
				"Strength: " + getPrefix(player, 14)
						+ player.getCombatDefinitions().getBonuses()[14]);
		player.getPackets().sendIComponentText(
				667,
				43,
				"Ranged Str: " + getPrefix(player, 15)
						+ player.getCombatDefinitions().getBonuses()[15]);
		player.getPackets().sendIComponentText(
				667,
				44,
				"Prayer: " + getPrefix(player, 16)
						+ player.getCombatDefinitions().getBonuses()[16]);
		player.getPackets().sendIComponentText(
				667,
				45,
				"Magic Damage: " + getPrefix(player, 17)
						+ player.getCombatDefinitions().getBonuses()[17] + "%");
		player.getPackets().sendIComponentText(667, 22, "0 kg");
	}

	public static void sendRemove(Player player, int slotId) {
		if (slotId >= 15)
			return;
		player.stopAll(false, false);
		Item item = player.getEquipment().getItem(slotId);
		if (item == null
				|| !player.getInventory().addItem(item.getId(),
						item.getAmount()))
			return;
		player.getEquipment().getItems().set(slotId, null);
		player.getEquipment().refresh(slotId);
		if (item.getId() == 4024) {
			player.getGlobalPlayerUpdate().transformIntoNPC(-1);
		}
		player.getGlobalPlayerUpdate().generateAppearenceData();
		if (RuneCrafting.isTiara(item.getId()))
			player.getPackets().sendConfig(491, 0);
		if (slotId == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
	}

	public static void sendRemove2(Player player, int slotId) {
		player.stopAll(false, false);
		Item item = player.getEquipment().getItem(slotId);
		player.getEquipment().getItems().set(slotId, null);
		player.getEquipment().refresh(slotId);
		if (item.getId() == 4024) {
			player.getGlobalPlayerUpdate().transformIntoNPC(-1);
		}
		player.getGlobalPlayerUpdate().generateAppearenceData();
		if (slotId == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
	}

	public static boolean sendWear(Player player, int slotId, int itemId) {
		if (player.hasFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		if (item.getDefinitions().isNoted()
				|| !item.getDefinitions().isWearItem(
						player.getGlobalPlayerUpdate().isMale())) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return true;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (targetSlot == -1) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return true;
		}
		if (!ItemConstants.canWear(item, player))
			return true;
		boolean isTwoHandedWeapon = targetSlot == 3
				&& Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots()
				&& player.getEquipment().hasShield()) {
			player.getPackets().sendGameMessage(
					"Not enough free space in your inventory.");
			return true;
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions()
				.getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;
				int level = requiriments.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments) {
						player.getPackets()
								.sendGameMessage(
										"You are not high enough level to use this item.");
					}
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.getPackets().sendGameMessage(
							"You need to have a"
									+ (name.startsWith("a") ? "n" : "") + " "
									+ name + " level of " + level + ".");
				}

			}
		}
		if (!hasRequiriments)
			return true;
		if (!player.getControllerManager().canEquip(targetSlot, itemId))
			return false;
		player.stopAll(false, false);
		player.getInventory().deleteItem(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().addItem(
						player.getEquipment().getItem(5).getId(),
						player.getEquipment().getItem(5).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null
					&& Equipment.isTwoHandedWeapon(player.getEquipment()
							.getItem(3))) {
				if (!player.getInventory().addItem(
						player.getEquipment().getItem(3).getId(),
						player.getEquipment().getItem(3).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().getItems().set(3, null);
			}

		}
		if (player.getEquipment().getItem(targetSlot) != null
				&& (itemId != player.getEquipment().getItem(targetSlot).getId() || !item
						.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory()
						.getItems()
						.set(slotId,
								new Item(player.getEquipment()
										.getItem(targetSlot).getId(), player
										.getEquipment().getItem(targetSlot)
										.getAmount()));
				player.getInventory().refresh(slotId);
			} else
				player.getInventory().addItem(
						new Item(player.getEquipment().getItem(targetSlot)
								.getId(), player.getEquipment()
								.getItem(targetSlot).getAmount()));
			player.getEquipment().getItems().set(targetSlot, null);
		}
		if (targetSlot == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot,
				targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		player.getGlobalPlayerUpdate().generateAppearenceData();
		player.getPackets().sendSound(2240, 0, 1);
		if (targetSlot == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		player.getCharges().wear(targetSlot);
		return true;
	}

	public static void sendWear(Player player, int[] slotIds) {
		if (player.hasFinished() || player.isDead())
			return;
		boolean worn = false;
		Item[] copy = player.getInventory().getItems().getItemsCopy();
		for (int slotId : slotIds) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null)
				continue;
			if (sendWear2(player, slotId, item.getId()))
				worn = true;
		}
		player.getInventory().refreshItems(copy);
		if (worn) {
			player.getGlobalPlayerUpdate().generateAppearenceData();
			player.getPackets().sendSound(2240, 0, 1);
		}
	}

	public static boolean sendWear2(Player player, int slotId, int itemId) {
		if (player.hasFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		if (item.getDefinitions().isNoted()
				|| !item.getDefinitions().isWearItem(
						player.getGlobalPlayerUpdate().isMale())
				&& itemId != 4084 && itemId != 4024) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (itemId == 4084)
			targetSlot = 3;
		if (itemId == 4024) {
			if (!player.isApeAtoll()) {
				player.getPackets()
						.sendGameMessage(
								"You've to be in Ape Atoll to transform into a monkey.");
				return false;
			}
			targetSlot = 3;
			player.getGlobalPlayerUpdate().transformIntoNPC(1481);
			player.getPackets().sendGameMessage(
					"You transform into a ninja monkey.");
		}
		if (targetSlot == -1) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		if (!ItemConstants.canWear(item, player))
			return false;
		boolean isTwoHandedWeapon = targetSlot == 3
				&& Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots()
				&& player.getEquipment().hasShield()) {
			player.getPackets().sendGameMessage(
					"Not enough free space in your inventory.");
			return false;
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions()
				.getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;
				int level = requiriments.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments)
						player.getPackets()
								.sendGameMessage(
										"You are not high enough level to use this item.");
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.getPackets().sendGameMessage(
							"You need to have a"
									+ (name.startsWith("a") ? "n" : "") + " "
									+ name + " level of " + level + ".");
				}

			}
		}
		if (!hasRequiriments)
			return false;
		if (!player.getControllerManager().canEquip(targetSlot, itemId))
			return false;
		player.getInventory().getItems().remove(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().getItems()
						.add(player.getEquipment().getItem(5))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null
					&& Equipment.isTwoHandedWeapon(player.getEquipment()
							.getItem(3))) {
				if (!player.getInventory().getItems()
						.add(player.getEquipment().getItem(3))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(3, null);
			}

		}
		if (player.getEquipment().getItem(targetSlot) != null
				&& (itemId != player.getEquipment().getItem(targetSlot).getId() || !item
						.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory()
						.getItems()
						.set(slotId,
								new Item(player.getEquipment()
										.getItem(targetSlot).getId(), player
										.getEquipment().getItem(targetSlot)
										.getAmount()));
				if (player.getEquipment().getItem(targetSlot).getId() == 4024) {
					player.getGlobalPlayerUpdate().transformIntoNPC(-1);
				}
			} else
				player.getInventory()
						.getItems()
						.add(new Item(player.getEquipment().getItem(targetSlot)
								.getId(), player.getEquipment()
								.getItem(targetSlot).getAmount()));
			if (player.getEquipment().getItem(targetSlot).getId() == 4024) {
				player.getGlobalPlayerUpdate().transformIntoNPC(-1);
			}
			player.getEquipment().getItems().set(targetSlot, null);
		}
		if (targetSlot == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot,
				targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		if (targetSlot == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		player.getCharges().wear(targetSlot);
		return true;
	}

	public static void submitSpecialRequest(final Player player) {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					EngineTaskManager.schedule(new EngineTask() {
						@Override
						public void run() {
							player.getCombatDefinitions()
									.switchUsingSpecialAttack();
						}
					}, 0);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 200);
	}
}