package com.citelic.networking.codec.decode.impl;

import java.util.List;

import com.citelic.GameConstants;
import com.citelic.cores.WorldThread;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.familiar.Familiar.SpecialAttack;
import com.citelic.game.entity.npc.impl.pet.Pet;
import com.citelic.game.entity.player.Equipment;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.containers.Inventory;
import com.citelic.game.entity.player.content.MoneyPouch;
import com.citelic.game.entity.player.content.SkillCapeCustomizer;
import com.citelic.game.entity.player.content.actions.Nests;
import com.citelic.game.entity.player.content.actions.consumables.Foods;
import com.citelic.game.entity.player.content.actions.consumables.Potions;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.crafting.GemCutting;
import com.citelic.game.entity.player.content.actions.skills.crafting.LeatherCrafting;
import com.citelic.game.entity.player.content.actions.skills.firemaking.Firemaking;
import com.citelic.game.entity.player.content.actions.skills.fletching.BoltTipFletching;
import com.citelic.game.entity.player.content.actions.skills.fletching.Fletching;
import com.citelic.game.entity.player.content.actions.skills.fletching.Fletching.Fletch;
import com.citelic.game.entity.player.content.actions.skills.herblore.HerbCleaning;
import com.citelic.game.entity.player.content.actions.skills.herblore.Herblore;
import com.citelic.game.entity.player.content.actions.skills.hunter.BoxAction;
import com.citelic.game.entity.player.content.actions.skills.hunter.BoxAction.HunterEquipment;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.actions.skills.prayer.Burying.Bone;
import com.citelic.game.entity.player.content.actions.skills.runecrafting.RuneCrafting;
import com.citelic.game.entity.player.content.actions.skills.slayer.Slayer;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning;
import com.citelic.game.entity.player.content.actions.skills.summoning.Summoning.Pouches;
import com.citelic.game.entity.player.content.controllers.impl.CrystalChest;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightKiln;
import com.citelic.game.entity.player.content.controllers.impl.distractions.bosses.Barrows;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.content.dialogue.impl.actions.AmuletAttachingD;
import com.citelic.game.entity.player.content.dialogue.impl.actions.CombinationsD.Combinations;
import com.citelic.game.entity.player.content.miscellaneous.ArmourSets;
import com.citelic.game.entity.player.content.miscellaneous.ArmourSets.Sets;
import com.citelic.game.entity.player.content.miscellaneous.ClueScrolls;
import com.citelic.game.entity.player.content.miscellaneous.MarkerPlant;
import com.citelic.game.entity.player.content.miscellaneous.RepairItems.BrokenItems;
import com.citelic.game.entity.player.content.miscellaneous.XPLamp;
import com.citelic.game.entity.player.content.miscellaneous.gamesofchance.Dicing;
import com.citelic.game.entity.player.content.miscellaneous.gamesofchance.Flowers;
import com.citelic.game.entity.player.content.transportation.ItemTransportation;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.managers.ChargesManager;
import com.citelic.game.map.pathfinding.RouteEvent;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.streaming.InputStream;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;
public class InventoryOptionsHandler {

	public static boolean contains(int id1, int id2, Item... items) {
		boolean containsId1 = false;
		boolean containsId2 = false;
		for (Item item : items) {
			if (item.getId() == id1)
				containsId1 = true;
			else if (item.getId() == id2)
				containsId2 = true;
		}
		return containsId1 && containsId2;
	}

	/*
	 * returns the other
	 */
	public static Item contains(int id1, Item item1, Item item2) {
		if (item1.getId() == id1)
			return item2;
		if (item2.getId() == id1)
			return item1;
		return null;
	}

	public static void dig(final Player player) {
		player.resetWalkSteps();
		player.setNextAnimation(new Animation(830));
		player.lock();
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				player.unlock();
				if (Barrows.digIntoGrave(player))
					return;
				if (player.getX() == 3005 && player.getY() == 3376
						|| player.getX() == 2999 && player.getY() == 3375
						|| player.getX() == 2996 && player.getY() == 3377
						|| player.getX() == 2989 && player.getY() == 3378
						|| player.getX() == 2987 && player.getY() == 3387
						|| player.getX() == 2984 && player.getY() == 3387) {
					// mole
					player.setNextTile(new Tile(1752, 5137, 0));
					player.getPackets()
							.sendGameMessage(
									"You seem to have dropped down into a network of mole tunnels.");
					return;
				}
				if (ClueScrolls.digSpot(player)) {
					return;
				}
				player.getPackets().sendGameMessage("You find nothing.");
			}

		});
	}

	public static void handleItemOnItem(final Player player, InputStream stream) {
		int fromSlot = stream.readShort128();
		int hash2 = stream.readIntLE();
		int itemUsedWithId = stream.readShort();
		int toSlot = stream.readShortLE();
		int itemUsedId = stream.readShort128();
		int hash1 = stream.readInt();
		int interfaceId = hash1 >> 16;
		int interfaceId2 = hash2 >> 16;
		int spellId = hash1 & 0xFFFF;
		if (interfaceId == 192 && interfaceId2 == Inventory.INVENTORY_INTERFACE) {
			if (spellId == 59 || spellId == 38 || spellId == 50) {
				Magic.castMiscellaneousSpell(player, spellId, itemUsedWithId);
				return;
			}
			if (player.isAdministrator()) {
				player.getPackets().sendGameMessage(
						"Spell:" + spellId + ", Item:" + itemUsedWithId);
			}
		}
		if ((interfaceId == 747 || interfaceId == 662)
				&& interfaceId2 == Inventory.INVENTORY_INTERFACE) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.ITEM) {
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(toSlot);
				}
			}
			return;
		}
		if (interfaceId == Inventory.INVENTORY_INTERFACE
				&& interfaceId == interfaceId2
				&& !player.getInterfaceManager().containsInventoryInter()) {
			if (toSlot >= 28 || fromSlot >= 28)
				return;
			Item usedWith = player.getInventory().getItem(toSlot);
			Item itemUsed = player.getInventory().getItem(fromSlot);
			if (itemUsed == null || usedWith == null
					|| itemUsed.getId() != itemUsedId
					|| usedWith.getId() != itemUsedWithId)
				return;
			player.stopAll();
			if (!player.getControllerManager().canUseItemOnItem(itemUsed,
					usedWith))
				return;
			if (Potions.mixPot(player, itemUsed, usedWith, fromSlot, toSlot))
				return;
			Fletch fletch = Fletching.isFletching(usedWith, itemUsed);
			if (fletch != null) {
				player.getDialogueManager().startDialogue("FletchingD", fletch);
				return;
			}
			if (itemUsed.getId() == CrystalChest.KEYPARTS[0].getId()
					&& usedWith.getId() == CrystalChest.KEYPARTS[1].getId()) {
				CrystalChest.makeKey(player);
			} else if (itemUsed.getId() == CrystalChest.KEYPARTS[1].getId()
					&& usedWith.getId() == CrystalChest.KEYPARTS[0].getId()) {
				CrystalChest.makeKey(player);
			}
			// --SKULL SCEPTRE--//
			// --STRANGE SKULL--//
			if (itemUsed.getId() == 9008 && usedWith.getId() == 9007) {
				player.getInventory().deleteItem(9008, 1);
				player.getInventory().deleteItem(9007, 1);
				player.getInventory().addItem(9009, 1);
				player.getDialogueManager()
						.startDialogue(
								"SimpleItemMessage",
								9009,
								"The two halves of the skull fit perfectly, they appear to have a fixing point, perhaps they are to be mounted on something?");
				return;
			}
			if (itemUsed.getId() == 9007 && usedWith.getId() == 9008) {
				player.getInventory().deleteItem(9008, 1);
				player.getInventory().deleteItem(9007, 1);
				player.getInventory().addItem(9009, 1);
				player.getDialogueManager()
						.startDialogue(
								"SimpleItemMessage",
								9009,
								"The two halves of the skull fit perfectly, they appear to have a fixing point, perhaps they are to be mounted on something?");
				return;
			}
			// --RUNED SCEPTRE--//
			if (itemUsed.getId() == 9010 && usedWith.getId() == 9011) {
				player.getInventory().deleteItem(9010, 1);
				player.getInventory().deleteItem(9011, 1);
				player.getInventory().addItem(9012, 1);
				player.getDialogueManager()
						.startDialogue(
								"SimpleItemMessage",
								9012,
								"The two halves of the skull fit perfectly, they appear to have a fixing point, perhaps they are to be mounted on something?");
				return;
			}
			if (itemUsed.getId() == 9011 && usedWith.getId() == 9010) {
				player.getInventory().deleteItem(9010, 1);
				player.getInventory().deleteItem(9011, 1);
				player.getInventory().addItem(9012, 1);
				player.getDialogueManager()
						.startDialogue(
								"SimpleItemMessage",
								9012,
								"The two halves of the Sceptre fit perfectly. The Sceptre appears to be designed to have something on top.");
				return;
			}
			// --FULL SCEPTRE--//
			if (itemUsed.getId() == 9012 && usedWith.getId() == 9009) {
				if (player.getBank().containsItem(9013, 1)
						|| player.getInventory().containsItem(9013, 1)
						|| player.getEquipment().getWeaponId() == 9013) {
					player.getDialogueManager().startDialogue(
							"SimpleItemMessage", 9013,
							"You may only have one Skull Sceptre at a time.");
					return;
				}
				player.setSkullChargesLeft(player.getSkullChargesLeft() + 5);
				player.getInventory().deleteItem(9012, 1);
				player.getInventory().deleteItem(9009, 1);
				player.getInventory().addItem(9013, 1);
				player.getDialogueManager()
						.startDialogue(
								"SimpleItemMessage",
								9013,
								"The skull fits perfectly atop the Sceptre, you feel there is great magical power at work here.");
				return;
			}
			if (itemUsed.getId() == 9009 && usedWith.getId() == 9012) {
				if (player.getBank().containsItem(9013, 1)
						|| player.getInventory().containsItem(9013, 1)
						|| player.getEquipment().getWeaponId() == 9013) {
					player.getDialogueManager().startDialogue(
							"SimpleItemMessage", 9013,
							"You may only have one Skull Sceptre at a time.");
					return;
				}
				player.setSkullChargesLeft(player.getSkullChargesLeft() + 5);
				player.getInventory().deleteItem(9012, 1);
				player.getInventory().deleteItem(9009, 1);
				player.getInventory().addItem(9013, 1);
				player.getDialogueManager()
						.startDialogue(
								"SimpleItemMessage",
								9013,
								"The skull fits perfectly atop the Sceptre, you feel there is great magical power at work here.");
				return;
			}
			// --END OF SCEPTRE--//
			if (itemUsed.getId() == 13734 || usedWith.getId() == 13754) { // blessed
				// spirit
				// shield
				if (player.getInventory().containsItem(13734, 1)
						&& player.getInventory().containsItem(13754, 1)) {
					player.getInventory().deleteItem(13734, 1);
					player.getInventory().deleteItem(13754, 1);
					player.getInventory().addItem(13736, 1);
					player.sendMessage("You use your Holy elixir to bless your Spirit Shield.");
				}
			}
			if (itemUsed.getId() == 13748 || usedWith.getId() == 13736) { // divine
				if (player.getInventory().containsItem(13736, 1)
						&& player.getInventory().containsItem(13748, 1)) {
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13748, 1);
					player.getInventory().addItem(13740, 1);
					player.sendMessage("You attach the sigil to the Blessed Spirit Shield.");
				}
			}
			if (itemUsed.getId() == 13750 || usedWith.getId() == 13736) { // ely
				if (player.getInventory().containsItem(13736, 1)
						&& player.getInventory().containsItem(13750, 1)) {
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13750, 1);
					player.getInventory().addItem(13742, 1);
					player.sendMessage("You attach the sigil to the Blessed Spirit Shield.");
				}
			}
			if (itemUsed.getId() == 13746 || usedWith.getId() == 13736) { // arcane
				if (player.getInventory().containsItem(13736, 1)
						&& player.getInventory().containsItem(13746, 1)) {
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13746, 1);
					player.getInventory().addItem(13738, 1);
					player.sendMessage("You attach the sigil to the Blessed Spirit Shield.");
				}
			}
			if (itemUsed.getId() == 13744 || usedWith.getId() == 13736) { // spectral
				if (player.getInventory().containsItem(13736, 1)
						&& player.getInventory().containsItem(13752, 1)) {
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13752, 1);
					player.getInventory().addItem(13744, 1);
					player.sendMessage("You attach the sigil to the Blessed Spirit Shield.");
				}
			}
			if (itemUsed.getId() == 11710 || usedWith.getId() == 11712
					|| usedWith.getId() == 11714) {
				if (player.getInventory().containsItem(11710, 1)
						&& player.getInventory().containsItem(11712, 1)
						&& player.getInventory().containsItem(11714, 1)) {
					player.getInventory().deleteItem(11710, 1);
					player.getInventory().deleteItem(11712, 1);
					player.getInventory().deleteItem(11714, 1);
					player.getInventory().addItem(11690, 1);
					player.getPackets().sendGameMessage(
							"You made a godsword blade.");
				}
			}
			if (itemUsed.getId() == 11690 || usedWith.getId() == 11702) {
				if (player.getInventory().containsItem(11690, 1)
						&& player.getInventory().containsItem(11702, 1)) {
					player.getInventory().deleteItem(11690, 1);
					player.getInventory().deleteItem(11702, 1);
					player.getInventory().addItem(11694, 1);
					player.getPackets()
							.sendGameMessage(
									"You attach the hilt to the blade and made an Armadyl godsword.");
				}
			}
			if (itemUsed.getId() == 11690 || usedWith.getId() == 11704) {
				if (player.getInventory().containsItem(11690, 1)
						&& player.getInventory().containsItem(11704, 1)) {
					player.getInventory().deleteItem(11690, 1);
					player.getInventory().deleteItem(11704, 1);
					player.getInventory().addItem(11696, 1);
					player.getPackets()
							.sendGameMessage(
									"You attach the hilt to the blade and made an Bandos godsword.");
				}
			}
			if (itemUsed.getId() == 11690 || usedWith.getId() == 11706) {
				if (player.getInventory().containsItem(11690, 1)
						&& player.getInventory().containsItem(11706, 1)) {
					player.getInventory().deleteItem(11690, 1);
					player.getInventory().deleteItem(11706, 1);
					player.getInventory().addItem(11698, 1);
					player.getPackets()
							.sendGameMessage(
									"You attach the hilt to the blade and made an Saradomin godsword.");
				}
			}
			if (itemUsed.getId() == 11690 || usedWith.getId() == 11708) {
				if (player.getInventory().containsItem(11690, 1)
						&& player.getInventory().containsItem(11708, 1)) {
					player.getInventory().deleteItem(11690, 1);
					player.getInventory().deleteItem(11708, 1);
					player.getInventory().addItem(11700, 1);
					player.getPackets()
							.sendGameMessage(
									"You attach the hilt to the blade and made an Zamorak godsword.");
				}
			}
			if (itemUsed.getId() == 21369 || usedWith.getId() == 4151) {
				if (player.getInventory().containsItem(21369, 1)
						&& player.getInventory().containsItem(4151, 1)) {
					if (player.getSkills().getLevel(Skills.SLAYER) < 80) {
						player.getPackets()
								.sendGameMessage(
										"You need an slayer level of 80 to attach the whip vine.");
						return;
					}
					player.getInventory().deleteItem(21369, 1);
					player.getInventory().deleteItem(4151, 1);
					player.getInventory().addItem(21371, 1);
					player.getPackets().sendGameMessage(
							"You attach the whip vine to the abyssal whip.");
					return;
				}
			}
			if (itemUsed.getId() == 19333 || usedWith.getId() == 6585) {
				if (player.getInventory().containsItem(19333, 1)
						&& player.getInventory().containsItem(6585, 1)) {
					player.getInventory().deleteItem(6585, 1);
					player.getInventory().deleteItem(19333, 1);
					player.getInventory().addItem(19335, 1);
					return;
				}
			}
			if (itemUsed.getId() > 0 && usedWith.getId() > 0) {
				int herblore = Herblore.isHerbloreSkill(itemUsed, usedWith);
				if (herblore > -1) {
					player.getDialogueManager().startDialogue("HerbloreD",
							herblore, itemUsed, usedWith);
					return;
				}
			}
			if (itemUsed.getId() == LeatherCrafting.NEEDLE.getId()
					|| usedWith.getId() == LeatherCrafting.NEEDLE.getId()) {
				if (LeatherCrafting
						.handleItemOnItem(player, itemUsed, usedWith)) {
					return;
				}
			}
		    Combinations combination = Combinations.isCombining(itemUsedId, itemUsedWithId);
		    if (combination != null) {
			player.getDialogueManager().startDialogue("CombinationsD", combination);
			return;
		    }
			Sets set = ArmourSets.getArmourSet(itemUsedId, itemUsedWithId);
			if (set != null) {
				ArmourSets.exchangeSets(player, set);
				return;
			}
			if (Firemaking.isFiremaking(player, itemUsed, usedWith))
				return;
			if (AmuletAttachingD.isAttaching(itemUsedId, itemUsedWithId)) {
				player.getDialogueManager().startDialogue("AmuletAttachingD");
			} else if (GemCutting.isCutting(player, itemUsed, usedWith))
				return;
			else
				player.getPackets().sendGameMessage(
						"Nothing interesting happens.");
			if (GameConstants.DEBUG)
				Logger.log("ItemHandler", "Used:" + itemUsed.getId()
						+ ", With:" + usedWith.getId());
		}
	}

	public static void handleItemOnNPC(final Player player, final NPC npc,
			final Item item) {
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				if (!player.getInventory().containsItem(item.getId(),
						item.getAmount())) {
					return;
				}
				if (npc.getId() == 519) {
					player.faceEntity(npc);
					if (BrokenItems.forId(item.getId()) == null) {
						player.getDialogueManager().startDialogue(
								"SimpleNPCMessage", 519,
								"I can't repair this item for you.");
						return;
					}
					player.getDialogueManager().startDialogue("BobRepair", 945,
							item.getId());
					return;
				}
				if (npc instanceof Pet) {
					player.faceEntity(npc);
					player.getPetManager().eat(item.getId(), (Pet) npc);
					return;
				}
			}
		}));
	}

	public static void handleItemOnPlayer(final Player player,
			final Player usedOn, final int itemId) {
		long time = Utilities.currentTimeMillis();
		Item item = player.getInventory().getItems().get(itemId);
		if (item == null || usedOn == player)
			return;
		if (player.getLockDelay() >= time
				|| player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		player.setRouteEvent(new RouteEvent(usedOn, new Runnable() {
			@Override
			public void run() {
				player.faceEntity(usedOn);
				if (usedOn.getInterfaceManager().containsScreenInter()) {
					player.sendMessage(usedOn.getDisplayName() + " is busy.");
					return;
				}
				switch (itemId) {
				case 962:// Christmas cracker
					if (player.getInventory().getFreeSlots() < 3
							|| usedOn.getInventory().getFreeSlots() < 3) {
						player.sendMessage((player.getInventory()
								.getFreeSlots() < 3 ? "You do"
								: "The other player does")
								+ " not have enough inventory space to open this cracker.");
						return;
					}
					player.getDialogueManager().startDialogue(
							"ChristmasCrackerD", usedOn, itemId);
					break;
				default:
					player.sendMessage("Nothing interesting happens.");
					break;
				}
			}
		}));
	}

	public static void handleItemOption1(Player player, final int slotId,
			final int itemId, Item item) {
		if (player.getLockDelay() >= Utilities.currentTimeMillis()
				|| player.getEmotesManager().getNextEmoteEnd() >= Utilities
						.currentTimeMillis())
			return;
		player.stopAll(false);
		switch (itemId) {
		case 2717:
			ClueScrolls.giveReward(player);
			break;
		case 22370:
			Summoning.openDreadnipInterface(player);
			break;
		case 15262:
			player.getInventory().deleteItem(itemId, slotId);
			player.getInventory().addItem(12183, 5000);
			break;
		case 405:
			player.getInventory().deleteItem(itemId, slotId);
			player.getInventory()
					.addItem(995, Utilities.random(100000, 250000));
			player.getPackets().sendGameMessage(
					"The casket slowly opens... You receive coins!");
			break;
		case 24155:
		case 24154:
			player.setSpins(player.getSpins() + itemId == 24155 ? 2 : 1);
			player.getSquealOfFortune().refreshSqueal();
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 20667:
			if (player.getVecnaTimer() > 0) {
				player.getPackets()
						.sendGameMessage(
								"The skull has not yet regained its magical aura. You will need to wait another "
										+ player.getVecnaTimer()
										/ 60000
										+ " minutes.");
			} else if (player.getVecnaTimer() == 0) {
				player.setVecnaTimer(7 * 60000);
				player.setNextGraphics(new Graphics(738, 0, 94));
				player.setNextAnimation(new Animation(10530));
				player.getSkills().set(Skills.MAGIC,
						player.getSkills().getLevelForXp(Skills.MAGIC) + 6);
				player.getPackets()
						.sendGameMessage(
								"The skull feeds off the life around you, boosting your magic ability.");
				player.vecnaTimer(7);
			}
			break;
		case 15084:
			player.getDialogueManager().startDialogue("DiceBag", itemId);
			break;
		case 299:
			if (Flowers.plantFlower(player))
				return;
			break;
		case 952:
			dig(player);
			break;
		case 10952:
			if (Slayer.isUsingBell(player))
				return;
			break;
		case 4155:
			player.getDialogueManager().startDialogue("EnchantedGemD",
					player.getSlayerManager().getCurrentMaster().getNPCId());
			break;
		case 15075:
			if (player.getMarkerPlant() != null) {
				player.sendMessage("You cannot do that yet.");
			} else {
				NPC npc = new NPC(9150, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc);
				if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
					if (!player.addWalkSteps(player.getX() + 1, player.getY(),
							1))
						if (!player.addWalkSteps(player.getX(),
								player.getY() + 1, 1))
							player.addWalkSteps(player.getX(),
									player.getY() - 1, 1);
				npc.setNextAnimation(new Animation(11905));
				MarkerPlant markerPlant = new MarkerPlant(player, npc);
				if (markerPlant.plantPlant())
					return;
			}
			break;
		case 6:
			player.getDwarfCannon().cannonSetup();
			break;
		}
		if (Foods.eat(player, item, slotId))
			return;
		if (Fletching.isFletching(player, itemId))
			return;
		if (XPLamp.isSelectable(itemId) || XPLamp.isSkillLamp(itemId)
				|| XPLamp.isOtherLamp(itemId)) {
			XPLamp.processLampClick(player, slotId, itemId);
		} if (BoltTipFletching.isCutting(player, itemId))
			return;
		if (Potions.pot(player, item, slotId))
			return;
		if (GemCutting.isCutting(player, itemId))
			return;
		if (HerbCleaning.clean(player, item, slotId))
			return;
		Bone bone = Bone.forId(itemId);
		if (bone != null) {
			Bone.bury(player, slotId);
			return;
		}
		if (Magic.useTabTeleport(player, itemId))
			return;
		if (ItemTransportation.transportationDialogue(player, item))
		    return;
		if (Nests.isNest(itemId)) {
			Nests.searchNest(player, slotId);
		}
		if (itemId == HunterEquipment.BOX.getId()) // almost done
			player.getActionManager().setAction(
					new BoxAction(HunterEquipment.BOX));
		if (itemId == HunterEquipment.BRID_SNARE.getId())
			player.getActionManager().setAction(
					new BoxAction(HunterEquipment.BRID_SNARE));
		if (itemId >= 15086 && itemId <= 15100) {
			Dicing.handleRoll(player, itemId, false);
			return;
		}
		if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509)
				pouch = 0;
			if (itemId == 5510)
				pouch = 1;
			if (itemId == 5512)
				pouch = 2;
			if (itemId == 5514)
				pouch = 3;
			RuneCrafting.fillPouch(player, pouch);
			return;
		}
		for (int i : ClueScrolls.ScrollIds) {
			if (itemId == i) {
				if (ClueScrolls.Scrolls.getMap(itemId) != null) {
					ClueScrolls.showMap(player,
							ClueScrolls.Scrolls.getMap(itemId));
					return;
				}
				if (ClueScrolls.Scrolls.getObjMap(itemId) != null) {
					ClueScrolls.showObjectMap(player,
							ClueScrolls.Scrolls.getObjMap(itemId));
					return;
				}
				if (ClueScrolls.Scrolls.getRiddles(itemId) != null) {
					ClueScrolls.showRiddle(player,
							ClueScrolls.Scrolls.getRiddles(itemId));
					return;
				}
			}
		}
		if (itemId >= 23653 && itemId <= 23658)
			FightKiln.useCrystal(player, itemId);
		else if (item.getDefinitions().getName().startsWith("Burnt"))
			player.getDialogueManager().startDialogue("SimplePlayerMessage",
					"Ugh, this is inedible.");
		if (GameConstants.DEBUG)
			Logger.log("ItemHandler", "Item Select:" + itemId + ", Slot Id:"
					+ slotId);
	}

	public static void handleItemOption2(final Player player, final int slotId,
			final int itemId, Item item) {
		if (Firemaking.isFiremaking(player, itemId))
			return;
		if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509)
				pouch = 0;
			if (itemId == 5510)
				pouch = 1;
			if (itemId == 5512)
				pouch = 2;
			if (itemId == 5514)
				pouch = 3;
			RuneCrafting.emptyPouch(player, pouch);
			player.stopAll(false);
		} else if (itemId >= 15086 && itemId <= 15100) {
			Dicing.handleRoll(player, itemId, true);
			return;
		} else if (itemId == 4155) {
			player.getSlayerManager().checkKillsLeft();
		} else if (itemId == 15075) {
			if (player.getMarkerPlant() != null) {
				player.sendMessage("You cannot do that yet.");
			} else {
				player.getDialogueManager().startDialogue("MarkerPlantD");
			}
		} else {
			if (player.isEquipDisabled())
				return;
			long passedTime = Utilities.currentTimeMillis()
					- WorldThread.WORLD_CYCLE;
			EngineTaskManager.schedule(new EngineTask() {

				@Override
				public void run() {
					List<Integer> slots = player.getSwitchItemCache();
					int[] slot = new int[slots.size()];
					for (int i = 0; i < slot.length; i++)
						slot[i] = slots.get(i);
					player.getSwitchItemCache().clear();
					ButtonHandler.sendWear(player, slot);
					player.stopAll(false, true, false);
				}
			}, passedTime >= 600 ? 0 : passedTime > 330 ? 1 : 0);
			if (player.getSwitchItemCache().contains(slotId))
				return;
			player.getSwitchItemCache().add(slotId);
		}
	}

	public static void handleItemOption3(Player player, int slotId, int itemId,
			Item item) {
		long time = Utilities.currentTimeMillis();
		if (player.getLockDelay() >= time
				|| player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		player.stopAll(false);
		if (itemId == 20767 || itemId == 20769 || itemId == 20771)
			SkillCapeCustomizer.startCustomizing(player, itemId);
		else if (itemId >= 15084 && itemId <= 15100)
			player.getDialogueManager().startDialogue("DiceBag", itemId);
		else if (itemId == 24437 || itemId == 24439 || itemId == 24440
				|| itemId == 24441)
			player.getDialogueManager().startDialogue("FlamingSkull", item,
					slotId);
		else if (itemId == 22444)
		    player.getCharges().checkCharges("There is " + ChargesManager.REPLACE + " doses of neem oil remaining.", itemId);
		else if ((itemId >= 24450 && itemId <= 24454) || (itemId >= 22358 && itemId <= 22369))
		    player.getCharges().checkPercentage("The gloves are " + ChargesManager.REPLACE + "% degraded.", itemId, true);
		else if (itemId >= 22458 && itemId <= 22497)
		    player.getCharges().checkPercentage(item.getName() + ": " + ChargesManager.REPLACE + "% remaining.", itemId, false);
		else if (itemId == 20171 || itemId == 20173)
		    player.getCharges().checkPercentage(item.getName() + ": has " + player.getCharges().getCharges(item.getId()) + " shots left.", itemId, false);
		else if (itemId == 23044 || itemId == 23045 || itemId == 23046
				|| itemId == 23047) {
			player.getDialogueManager().startDialogue("MindSpikeD", itemId,
					slotId);
		} else if (item.getDefinitions().containsOption("Teleport") && ItemTransportation.transportationDialogue(player, item))
			return;
		else if (itemId == 4155) {
			player.getInterfaceManager().sendInterface(1309);
		} else if (Equipment.getItemSlot(itemId) == Equipment.SLOT_AURA)
			player.getAuraManager().sendTimeRemaining(itemId);
	}

	public static void handleItemOption4(Player player, int slotId, int itemId,
			Item item) {
	}

	public static void handleItemOption5(Player player, int slotId, int itemId,
			Item item) {
	}

	public static void handleItemOption6(Player player, int slotId, int itemId,
			Item item) {
		long time = Utilities.currentTimeMillis();
		if (player.getLockDelay() >= time
				|| player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		player.stopAll(false);
		if (player.getToolbelt().addItem(slotId, item))
			return;
		if ((item.getDefinitions().containsOption("Rub") || item.getDefinitions().containsOption("Cabbage-port")) && ItemTransportation.transportationDialogue(player, item))
		    return;
		if (Potions.emptyPot(player, item, slotId))
		    return;
		Pouches pouches = Pouches.forId(itemId);
		if (itemId >= 15086 && itemId <= 15100) {
			Dicing.removePercentile(player, itemId);
			return;
		}
		if (pouches != null)
			Summoning.spawnFamiliar(player, pouches);
		else if (itemId == 1438)
			RuneCrafting.locate(player, 3127, 3405);
		else if (itemId == 1440)
			RuneCrafting.locate(player, 3306, 3474);
		else if (itemId == 1442)
			RuneCrafting.locate(player, 3313, 3255);
		else if (itemId == 1444)
			RuneCrafting.locate(player, 3185, 3165);
		else if (itemId == 1446)
			RuneCrafting.locate(player, 3053, 3445);
		else if (itemId == 1448)
			RuneCrafting.locate(player, 2982, 3514);
		else if (itemId <= 1712 && itemId >= 1706 || itemId >= 10354
				&& itemId <= 10362)
			player.getDialogueManager().startDialogue("Transportation",
					"Edgeville", new Tile(3087, 3496, 0), "Karamja",
					new Tile(2918, 3176, 0), "Draynor Village",
					new Tile(3105, 3251, 0), "Al Kharid",
					new Tile(3293, 3163, 0), itemId);
		else if (itemId == 995
				&& !(player.getControllerManager().getController() instanceof Wilderness)) {
			player.getMoneyPouch().sendDynamicInteraction(item.getAmount(),
					false, MoneyPouch.TYPE_POUCH_INVENTORY);
		} else if (itemId == 1704 || itemId == 10352)
			player.getPackets()
					.sendGameMessage(
							"The amulet has ran out of charges. You need to recharge it if you wish it use it once more.");
		else if (itemId >= 3853 && itemId <= 3867)
			player.getDialogueManager().startDialogue("Transportation",
					"Burthrope Games Room", new Tile(2880, 3559, 0),
					"Barbarian Outpost", new Tile(2519, 3571, 0),
					"Gamers' Grotto", new Tile(2970, 9679, 0),
					"Corporeal Beast", new Tile(2886, 4377, 0), itemId);
	}

	public static void handleItemOption7(Player player, int slotId, int itemId,
			Item item) {
		long time = Utilities.currentTimeMillis();
		if (player.getLockDelay() >= time
				|| player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		if (!player.getControllerManager().canDropItem(item))
			return;
		player.stopAll(false);
		if (item.getDefinitions().isOverSized()) {
			player.getPackets().sendGameMessage(
					"The item appears to be oversized.");
			player.getInventory().deleteItem(item);
			return;
		}
		if (item.getDefinitions().isDestroyItem()) {
			player.getDialogueManager().startDialogue("DestroyItemOption",
					slotId, item);
			return;
		}
		if (player.getPetManager().spawnPet(itemId, true))
			return;
		player.getInventory().deleteItem(slotId, item);
		if (player.getCharges().degradeCompletly(item))
			return;
		Engine.addGroundItem(item, new Tile(player), player, false, 180,
				true);
		player.getPackets().sendSound(2739, 0, 1);
	}

	public static void handleItemOption8(Player player, int slotId, int itemId,
			Item item) {
		player.getInventory().sendExamine(slotId);
		if (player.getRights() == 2) {
			player.getPackets().sendGameMessage(
					"id: " + item.getId() + " amnt: "
							+ item.getAmount() + " modelzoom: "
							+ item.getDefinitions().getModelZoom() + " value: "
							+ item.getDefinitions().getValue());
		}
		if (GameConstants.DEBUG)
			Logger.log("ItemHandler", "Item " + itemId
					+ " was selected in item option 8.");
	}

}