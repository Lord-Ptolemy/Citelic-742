package com.citelic.utility.economy;

import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemConstants;

public class Prices {

	public static double getAlchPrice(int itemId, boolean lowalch) {
		// TODO Auto-generated method stub
		// Street Prices * 1.25
		Item item = (new Item(itemId, 1));
		return item.getDefinitions().getValue() / (lowalch ? 4 : 2.5);
	}

	public static int getDeathPrice(Item item) {
		switch (item.getId()) {
		case 23659:
			return 1;
		case 6570:
			return 1;
		case 4151:
			return 10000000;
		case 11694:
			return 1500000000;
		case 11730:
			return 40000000;
		case 18353:
			return 285000000;
		case 6585:
			return 28500000;
		case 14484:
			return 1000000000;
		case 13740:
			return 2000000000;
		case 13738:
			return 1700000000;
		case 13744:
			return 1500000000;
		case 13742:
			return 1900000000;
		case 22494:
			return 35000000;
		case 21787:
		case 21793:
		case 21790:
			return 75000000;
		case 18357:
		case 18355:
		case 16403:
		case 16425:
		case 16955:
		case 18349:
		case 16423:
		case 18351:
			return 1000000000;
		case 6737:
		case 6731:
		case 6733:
		case 6734:
			return 80000000;
		case 1038:
		case 1040:
		case 1042:
		case 1044:
		case 1046:
		case 1048:
			return 1;
		case 1050:
			return 1;
		case 11724:
			return 170000000;
		case 19784:
			return 900000000;
		case 11726:
			return 150000000;
		case 21371:
			return 50000000;
		case 4722:
		case 4720:
		case 4718:
		case 4716:
			return 30000000;
		case 20143:
		case 20137:
		case 20135:
			return 500000000;
		}
		if (!ItemConstants.isTradeable(item)) {
			System.out.println("Item " + item.getId() + " is untradeable.");
			return 1;
		}
		return item.getDefinitions().getValue();
	}

	public static int getShopBuyPrice(Item item) {
		switch (item.getName()) {
		case "Grimy guam":
		case "Clean fellstalk":
		case "Clean toadflax":
		case "Grimy kwuarm":
		case "Grimy cadantine":
		case "Grimy avantoe":
		case "Grimy ranarr":
		case "Grimy dwarf weed":
		case "Grimy torstol":
		case "Grimy tarromin":
		case "Grimy irit":
		case "Grimy avanto":
		case "Grimy lantadyme":
		case "Grimy rannar":
		case "Grimy harralander":
		case "Grimy marrentill":
		case "Papaya fruit":
		case "Dragon scale dust":
		case "Crushed nest":
		case "Potato cactus":
			item.getDefinitions().setValue(4000);
			break;
		case "Varrock teleport":
		case "Falador teleport":
		case "Lumbridge teleport":
		case "House teleport":
		case "Camelot teleport":
		case "Ardougne teleport":
			item.getDefinitions().setValue(10000);
			break;
		case "Eye of newt":
		case "Red spiders' eggs":
		case "Limpwurt root":
		case "Snape grass":
		case "Unicorn horn dust":
		case "White berries":
		case "Wine of zamorak":
		case "Jangerberries":
			item.getDefinitions().setValue(2000);
			break;
		case "Grenwall spikes":
		case "Morchella mushroom":
			item.getDefinitions().setValue(6000);
			break;
		case "Fury ornament kit":
			item.getDefinitions().setValue(75000000);
			break;
		case "Ring of wealth":
			item.getDefinitions().setValue(100000000);
			break;
		case "Archery ticket":
			item.getDefinitions().setValue(100000000);
			break;
		case "Ring of life":
			item.getDefinitions().setValue(250000);
			break;
		case "Super prayer flask (6)":
			item.getDefinitions().setValue(15000);
			break;
		case "Steadfast boots":
			item.getDefinitions().setValue(30000000);
			break;
		case "Glaiven boots":
			item.getDefinitions().setValue(22000000);
			break;
		case "Ragefire boots":
			item.getDefinitions().setValue(16000000);
			break;
		case "Blue cape":
		case "Red cape":
			item.getDefinitions().setValue(2000000);
			break;
		case "Toadflax":
			item.getDefinitions().setValue(3000);
			break;
		case "Rune bar":
			item.getDefinitions().setValue(30000);
			break;
		case "Adamant bar":
			item.getDefinitions().setValue(10000);
			break;
		case "Mithril bar":
		case "Gold bar":
			item.getDefinitions().setValue(5000);
			break;
		case "Bronze bar":
		case "Iron bar":
		case "Steel bar":
		case "Silver bar":
			item.getDefinitions().setValue(1500);
			break;
		case "Armadyl godsword":
			item.getDefinitions().setValue(650000000);
			break;
		case "Dragon claws":
			item.getDefinitions().setValue(550000000);
			break;
		case "Divine spirit shield":
			item.getDefinitions().setValue(475000000);
			break;
		case "Arcane spirit shield":
			item.getDefinitions().setValue(85000000);
			break;
		case "Spectral spirit shield":
			item.getDefinitions().setValue(85000000);
			break;
		case "Elysian spirit shield":
			item.getDefinitions().setValue(475000000);
			break;
		case "Chaotic crossbow":
		case "Chaotic rapier":
		case "Chaotic maul":
		case "Chaotic longsword":
		case "Chaotic staff":
			item.getDefinitions().setValue(300000000);
			break;
		case "Flaming skull":
			item.getDefinitions().setValue(1500000000);
			break;
		case "Ardougne cloak 1":
		case "Ardougne cloak 2":
		case "Ardougne cloak 3":
		case "Ardougne cloak 4":
			item.getDefinitions().setValue(200000000);
			break;
		case "Blue partyhat":
		case "Green partyhat":
		case "Yellow partyhat":
		case "Purple partyhat":
		case "White partyhat":
			item.getDefinitions().setValue(1337);
			break;
		case "Santa hat":
			item.getDefinitions().setValue(1);
			break;
		case "Bandos chestplate":
			item.getDefinitions().setValue(100000000);
			break;
		case "Bandos tassets":
			item.getDefinitions().setValue(100000000);
			break;
		case "Armadyl chainskirt":
			item.getDefinitions().setValue(125000000);
			break;
		case "Armadyl chestplate":
			item.getDefinitions().setValue(125000000);
			break;
		case "Abyssal vine whip":
			item.getDefinitions().setValue(100000000);
			break;
		case "Barrelchest anchor":
			item.getDefinitions().setValue(6000000);
			break;
		case "Fighter torso":
		case "Fighter hat":
			item.getDefinitions().setValue(500);
			break;
		case "Baby troll":
		case "Freezy":
		case "Sneakerpeeper spawn":
		case "Bulldog puppy":
		case "Baby aquanite":
			item.getDefinitions().setValue(1);
			break;
		case "Dark bow":
			item.getDefinitions().setValue(6500000);
			break;
		case "Ranger boots":
			item.getDefinitions().setValue(40000000);
			break;
		case "Robin hood hat":
			item.getDefinitions().setValue(55000000);
			break;
		case "New crystal bow":
			item.getDefinitions().setValue(2500000);
			break;
		case "Staff of light":
			item.getDefinitions().setValue(4500000);
			break;
		case "Infinity boots":
		case "Infinity gloves":
		case "Infinity hat":
			item.getDefinitions().setValue(750000);
			break;
		case "Infinity top":
		case "Infinity bottoms":
			item.getDefinitions().setValue(3500000);
			break;
		case "Dragon full helm":
			item.getDefinitions().setValue(42000000);
			break;
		case "Dragon sq shield":
			item.getDefinitions().setValue(2200000);
			break;
		case "Dragon platelegs":
			item.getDefinitions().setValue(2500000);
			break;
		case "Dragon platebody":
			item.getDefinitions().setValue(22000000);
			break;
		case "Dragon chainbody":
			item.getDefinitions().setValue(7500000);
			break;
		case "Dragon boots":
			item.getDefinitions().setValue(2500000);
			break;
		case "Super restore flask (6)":
			item.getDefinitions().setValue(25000);
			break;
		case "Prayer flask (6)":
		case "Super antipoison flask (6)":
			item.getDefinitions().setValue(2000);
			break;
		case "Antifire flask (6)":
			item.getDefinitions().setValue(5000);
			break;
		case "Saradomin brew flask (6)":
			item.getDefinitions().setValue(30000);
			break;
		case "Prayer renewal flask (6)":
			item.getDefinitions().setValue(13000);
			break;
		case "Dragon scimitar":
			item.getDefinitions().setValue(150000);
			break;
		case "Dragon halberd":
			item.getDefinitions().setValue(4500000);
			break;
		case "Dragon mace":
			item.getDefinitions().setValue(100000);
			break;
		case "Granite maul":
			item.getDefinitions().setValue(300000);
			break;
		case "Prayer potion (4)":
		case "Super restore (4)":
			item.getDefinitions().setValue(1000);
			break;
		case "Gilded platebody":
		case "Gilded platelegs":
		case "Gilded plateskirt":
			item.getDefinitions().setValue(350000);
			break;
		case "Mages' book":
			item.getDefinitions().setValue(5500000);
			break;
		case "Master wand":
			item.getDefinitions().setValue(10000000);
			break;
		case "Ancient staff":
			item.getDefinitions().setValue(1500000);
			break;
		case "Dagon'hai hat":
		case "Dagon'hai robe top":
		case "Dagon'hai robe bottom":
			item.getDefinitions().setValue(1300000);
			break;
		case "Berserker ring":
		case "Archers' ring":
		case "Seers' ring":
			item.getDefinitions().setValue(20000000);
			break;
		case "Saradomin cape":
		case "Guthix cape":
		case "Zamorak cape":
			item.getDefinitions().setValue(1000000);
			break;
		case "Zamorak's unholy book":
		case "Saradomin's holy book":
			item.getDefinitions().setValue(2000000);
			break;
		case "Amulet of ranging":
			item.getDefinitions().setValue(650000);
			break;
		case "Polypore staff":
			item.getDefinitions().setValue(45000000);
			break;
		case "Diamond bolts (e)":
		case "Ruby bolts (e)":
			item.getDefinitions().setValue(10000);
			break;
		case "Proselyte sallet":
		case "Proselyte hauberk":
		case "Proselyte cuisse":
		case "Proselyte tasset":
			item.getDefinitions().setValue(50000);
			break;
		case "Brackish blade":
			item.getDefinitions().setValue(300000);
			break;
		case "Amulet of fury":
			item.getDefinitions().setValue(25000000);
			break;
		case "Bronze defender":
		case "Iron defender":
		case "Steel defender":
		case "Black defender":
		case "Mithril defender":
		case "Adamant defender":
		case "Rune defender":
		case "Dragon defender":
			item.getDefinitions().setValue(5000000);
			break;
		case "Mithril seeds":
			item.getDefinitions().setValue(50000);
			break;
		case "Ganodermic visor":
		case "Ganodermic leggings":
		case "Ganodermic poncho":
			item.getDefinitions().setValue(50000000);
			break;
		case "Armadyl battlestaff":
			item.getDefinitions().setValue(65000000);
			break;
		case "Recover special flask (6)":
			item.getDefinitions().setValue(50000);
			break;
		case "Overload flask (6)":
			item.getDefinitions().setValue(750000);
			break;
		case "Overload (4)":
			item.getDefinitions().setValue(50000);
			break;
		case "Super antifire flask (6)":
			item.getDefinitions().setValue(5000);
			break;
		case "Morrigan's javelin":
		case "Morrigan's throwing axe":
			item.getDefinitions().setValue(500000);
			break;
		case "Zuriel's staff":
		case "Statius's warhammer":
		case "Vesta's longsword":
		case "Vesta's spear":
			item.getDefinitions().setValue(5000000);
			break;
		case "Vesta's chainbody":
		case "Vesta's plateskirt":
		case "Statius's full helm":
		case "Statius's platebody":
		case "Statius's platelegs":
		case "Morrigan's coif":
		case "Morrigan's leather body":
		case "Morrigan's leather chaps":
		case "Zuriel's hood":
		case "Zuriel's robe top":
		case "Zuriel's robe bottom":
			item.getDefinitions().setValue(5000000);
			break;
		case "Culinaromancer's gloves 10":
			item.getDefinitions().setValue(1000000);
			break;
		case "Dragonstone":
			item.getDefinitions().setValue(10000);
			break;
		case "Diamond necklace":
			item.getDefinitions().setValue(40000);
			break;
		case "Silver dust":
			item.getDefinitions().setValue(30000);
			break;
		case "Gold ring":
			item.getDefinitions().setValue(20000);
			break;
		case "Cowhide":
			item.getDefinitions().setValue(10000);
			break;
		case "Silk":
			item.getDefinitions().setValue(8000);
			break;
		case "Dharok's greataxe":
		case "Dharok's helm":
		case "Dharok's platebody":
		case "Dharok's platelegs":
			item.getDefinitions().setValue(20000000);
			break;
		case "Dragon bones":
			item.getDefinitions().setValue(150000);
			break;
		case "Frost dragon bones":
			item.getDefinitions().setValue(350000);
			break;
		case "Loop half key":
		case "Teeth half":
			item.getDefinitions().setValue(3000000);
			break;
		}
		return item.getDefinitions().getValue();
	}

	public static int getShopSellPrice(Item item, int dq) {
		switch (item.getName()) {
		case "Archery ticket":
			item.getDefinitions().setValue(100000000);
			break;
		case "Fury ornament kit":
			item.getDefinitions().setValue(75000000);
			break;
		case "Ring of wealth":
			item.getDefinitions().setValue(100000000);
			break;
		case "Ring of life":
			item.getDefinitions().setValue(200000);
			break;
		case "Sneakerpeeper spawn":
			item.getDefinitions().setValue(1);
			break;
		case "Iron defender":
		case "Steel defender":
		case "Black defender":
		case "Mithril defender":
		case "Adamant defender":
		case "Rune defender":
		case "Dragon defender":
			item.getDefinitions().setValue(5000000);
			break;
		case "Super prayer flask (6)":
			item.getDefinitions().setValue(13000);
			break;
		case "Dharok's greataxe":
		case "Dharok's platelegs":
		case "Dharok's platebody":
		case "Dharok's helm":
			item.getDefinitions().setValue(15000000);
			break;
		case "Dragon bones":
			item.getDefinitions().setValue(100000);
			break;
		case "Mithril seeds":
			item.getDefinitions().setValue(50000);
			break;
		case "Loop half key":
		case "Teeth half":
			item.getDefinitions().setValue(2000000);
			break;
		case "Frost dragon bones":
			item.getDefinitions().setValue(200000);
			break;
		case "Steadfast boots":
			item.getDefinitions().setValue(20000000);
			break;
		case "Glaiven boots":
			item.getDefinitions().setValue(15000000);
			break;
		case "Ragefire boots":
			item.getDefinitions().setValue(10000000);
			break;
		case "Toadflax":
			item.getDefinitions().setValue(3000);
			break;
		case "Rune bar":
			item.getDefinitions().setValue(15000);
			break;
		case "Adamant bar":
			item.getDefinitions().setValue(7000);
			break;
		case "Mithril bar":
		case "Gold bar":
			item.getDefinitions().setValue(3000);
			break;
		case "Bronze bar":
		case "Iron bar":
		case "Steel bar":
		case "Silver bar":
			item.getDefinitions().setValue(1500);
			break;
		case "Barrelchest anchor":
			item.getDefinitions().setValue(5500000);
			break;
		case "Fighter torso":
		case "Fighter hat":
			item.getDefinitions().setValue(1);
			break;
		case "Dark bow":
			item.getDefinitions().setValue(5000000);
			break;
		case "Ranger boots":
			item.getDefinitions().setValue(30000000);
			break;
		case "Robin hood hat":
			item.getDefinitions().setValue(45000000);
			break;
		case "New crystal bow":
			item.getDefinitions().setValue(2500000);
			break;
		case "Staff of light":
			item.getDefinitions().setValue(4000000);
			break;
		case "Infinity boots":
		case "Infinity gloves":
		case "Infinity hat":
			item.getDefinitions().setValue(650000);
			break;
		case "Infinity top":
		case "Infinity bottoms":
			item.getDefinitions().setValue(3300000);
			break;
		case "Dragon full helm":
			item.getDefinitions().setValue(37000000);
			break;
		case "Dragon sq shield":
			item.getDefinitions().setValue(1800000);
			break;
		case "Dragon platelegs":
			item.getDefinitions().setValue(2100000);
			break;
		case "Dragon platebody":
			item.getDefinitions().setValue(20000000);
			break;
		case "Dragon chainbody":
			item.getDefinitions().setValue(6900000);
			break;
		case "Dragon boots":
			item.getDefinitions().setValue(1000000);
			break;
		case "Super restore flask (6)":
			item.getDefinitions().setValue(20000);
			break;
		case "Prayer flask (6)":
		case "Super antipoison flask (6)":
			item.getDefinitions().setValue(2000);
			break;
		case "Antifire flask (6)":
			item.getDefinitions().setValue(5000);
			break;
		case "Saradomin brew flask (6)":
			item.getDefinitions().setValue(30000);
			break;
		case "Prayer renewal flask (6)":
			item.getDefinitions().setValue(13000);
			break;
		case "Dragon scimitar":
			item.getDefinitions().setValue(150000);
			break;
		case "Dragon halberd":
			item.getDefinitions().setValue(4000000);
			break;
		case "Dragon mace":
			item.getDefinitions().setValue(100000);
			break;
		case "Granite maul":
			item.getDefinitions().setValue(300000);
			break;
		case "Prayer potion (4)":
		case "Super restore (4)":
			item.getDefinitions().setValue(1000);
			break;
		case "Gilded platebody":
		case "Gilded platelegs":
		case "Gilded plateskirt":
			item.getDefinitions().setValue(300000);
			break;
		case "Mages' book":
			item.getDefinitions().setValue(5000000);
			break;
		case "Master wand":
			item.getDefinitions().setValue(7800000);
			break;
		case "Ancient staff":
			item.getDefinitions().setValue(1250000);
			break;
		case "Dagon'hai hat":
		case "Dagon'hai robe top":
		case "Dagon'hai robe bottom":
			item.getDefinitions().setValue(1150000);
			break;
		case "Berserker ring":
		case "Archers' ring":
		case "Seers' ring":
			item.getDefinitions().setValue(15000000);
			break;
		case "Saradomin cape":
		case "Guthix cape":
		case "Zamorak cape":
			item.getDefinitions().setValue(1000000);
			break;
		case "Zamorak's unholy book":
		case "Saradomin's holy book":
			item.getDefinitions().setValue(2000000);
			break;
		case "Amulet of ranging":
			item.getDefinitions().setValue(650000);
			break;
		case "Polypore staff":
			item.getDefinitions().setValue(30000000);
			break;
		case "Diamond bolts (e)":
		case "Ruby bolts (e)":
			item.getDefinitions().setValue(3500);
			break;
		case "Proselyte sallet":
		case "Proselyte hauberk":
		case "Proselyte cuisse":
		case "Proselyte tasset":
			item.getDefinitions().setValue(50000);
			break;
		case "Brackish blade":
			item.getDefinitions().setValue(250000);
			break;
		case "Amulet of fury":
			item.getDefinitions().setValue(10000000);
			break;
		case "Ganodermic visor":
		case "Ganodermic leggings":
		case "Ganodermic poncho":
			item.getDefinitions().setValue(500000);
			break;
		case "Armadyl battlestaff":
			item.getDefinitions().setValue(35000000);
			break;
		case "Recover special flask (6)":
			item.getDefinitions().setValue(50000);
			break;
		case "Overload flask (6)":
			item.getDefinitions().setValue(750000);
			break;
		case "Overload (4)":
			item.getDefinitions().setValue(5000);
			break;
		case "Super antifire flask (6)":
			item.getDefinitions().setValue(5000);
			break;
		case "Morrigan's javelin":
		case "Morrigan's throwing axe":
			item.getDefinitions().setValue(500000);
			break;
		case "Zuriel's staff":
		case "Statius's warhammer":
		case "Vesta's longsword":
		case "Vesta's spear":
			item.getDefinitions().setValue(5000000);
			break;
		case "Vesta's chainbody":
		case "Vesta's plateskirt":
		case "Statius's full helm":
		case "Statius's platebody":
		case "Statius's platelegs":
		case "Morrigan's coif":
		case "Morrigan's leather body":
		case "Morrigan's leather chaps":
		case "Zuriel's hood":
		case "Zuriel's robe top":
		case "Zuriel's robe bottom":
			item.getDefinitions().setValue(5000000);
			break;
		case "Culinaromancer's gloves 10":
			item.getDefinitions().setValue(1000000);
			break;
		case "Culinaromancer's gloves 9":
			item.getDefinitions().setValue(100000);
			break;
		case "Dragonstone":
			item.getDefinitions().setValue(10000);
			break;
		case "Diamond necklace":
			item.getDefinitions().setValue(40000);
			break;
		case "Silver dust":
			item.getDefinitions().setValue(30000);
			break;
		case "Gold ring":
			item.getDefinitions().setValue(20000);
			break;
		case "Cowhide":
			item.getDefinitions().setValue(10000);
			break;
		case "Silk":
			item.getDefinitions().setValue(8000);
			break;
		case "Armadyl godsword":
			item.getDefinitions().setValue(400000000);
			break;
		case "Dragon claws":
			item.getDefinitions().setValue(350000000);
			break;
		case "Divine spirit shield":
			item.getDefinitions().setValue(300000000);
			break;
		case "Arcane spirit shield":
			item.getDefinitions().setValue(100000000);
			break;
		case "Spectral spirit shield":
			item.getDefinitions().setValue(100000000);
			break;
		case "Elysian spirit shield":
			item.getDefinitions().setValue(500000000);
			break;
		case "Chaotic crossbow":
		case "Chaotic rapier":
		case "Chaotic maul":
		case "Chaotic longsword":
		case "Chaotic staff":
			item.getDefinitions().setValue(1);
			break;
		case "flaming skull":
			item.getDefinitions().setValue(1);
			break;
		case "Ardougne cloak 1":
		case "Ardougne cloak 2":
		case "Ardougne cloak 3":
		case "Ardougne cloak 4":
			item.getDefinitions().setValue(1);
			break;
		case "Blue partyhat":
		case "Green partyhat":
		case "Yellow partyhat":
		case "Purple partyhat":
		case "White partyhat":
			item.getDefinitions().setValue(1337);
			break;
		case "Santa hat":
			item.getDefinitions().setValue(1);
			break;
		case "Bandos tassets":
			item.getDefinitions().setValue(75000000);
			break;
		case "Bandos chestplate":
			item.getDefinitions().setValue(75000000);
			break;
		case "Armadyl chainskirt":
			item.getDefinitions().setValue(75000000);
			break;
		case "Armadyl chestplate":
			item.getDefinitions().setValue(75000000);
			break;
		case "Abyssal vine whip":
			item.getDefinitions().setValue(65000000);
			break;
		}
		return item.getDefinitions().getValue();
	}

	public static int getTradePrices(Item item) {
		switch (item.getName()) {
		case "Santa hat":
			return 250000000;
		case "Armadyl godsword":
		case "Virtus mask":
		case "Virtus gloves":
		case "Virtus boots":
		case "Torva full helm":
		case "Torva gloves":
		case "Torva boots":
		case "Pernix gloves":
		case "Pernix boots":
			return 1000000000;
		case "Dragon claws":
		case "Torva platebody":
		case "Torva platelegs":
		case "Virtus robe top":
		case "Virtus robe legs":
		case "Pernix body":
		case "Pernix chaps":
		case "Blue partyhat":
		case "White partyhat":
		case "Red partyhat":
		case "Green partyhat":
		case "Purple partyhat":
		case "Yellow partyhat":
		case "Christmas cracker":
		case "Primal boots":
		case "Primal platebody":
		case "Primal platelegs":
		case "Primal full helm":
		case "Primal gauntlets":
		case "Primal 2h sword":
		case "Primal longsword":
		case "Primal rapier":
		case "Primal maul":
		case "Divine spirit shield":
		case "Divine sigil":
		case "Dragonbone plateskirt":
		case "Dragonbone platelegs":
		case "Dragonbone platebody":
		case "Dragonbone full helm":
		case "Dragonbone mage bottoms":
		case "Dragonbone mage top":
		case "Dragonbone mage hat":
		case "Dragonbone upgrade kit":
		case "Sled":
			return Integer.MAX_VALUE;
		case "Pernix cowl":
		case "Elysian spirit shield":
		case "Red h'ween mask":
		case "Green h'ween mask":
		case "Blue h'ween mask":
		case "Korasi's sword":
			return 1200000000;
		case "Zaryte bow":
		case "Spirt shield":
		case "Holy elixr":
		case "Berserker ring":
		case "Archers' ring":
		case "Seers' ring":
		case "Warrior ring":
		case "Flippers":
		case "Slayer helmet":
		case "Amulet of fury (or)":
		case "Third-age platebody":
		case "Third-age platelegs":
		case "Bandos helmet":
		case "Abyssal vine whip":
			return 100000000;
		case "Berserker ring (i)":
		case "Archers' ring (i)":
		case "Seers' ring (i)":
		case "Onyx ring (i)":
		case "Royal crossbow":
		case "Saradomin's whisper":
		case "Saradomin's murmur":
		case "Saradomin's hiss":
		case "Bandos tassets":
		case "Bandos chestplate":
		case "Armadyl chestplate":
		case "Armadyl chainskirt":
		case "Santa trousers":
		case "Santa costume top":
		case "Santa costume boots":
			return 200000000;
		case "Steadfast boots":
		case "Glaiven boots":
		case "Ragefire boots":
		case "Third-age druidic robe top":
		case "Third-age druidic robe":
		case "Gnome scarf (yellow)":
		case "Gnome scarf (blue)":
		case "Gnome scarf (green)":
		case "Gnome scarf (red)":
			return 150000000;
		case "Spellcaster gloves (black)":
		case "Spellcaster gloves (red)":
		case "Spellcaster gloves (yellow)":
		case "Spellcaster gloves (white)":
		case "Goliath gloves (black)":
		case "Goliath gloves (red)":
		case "Goliath gloves (yellow)":
		case "Goliath gloves (white)":
		case "Swift gloves (black)":
		case "Swift gloves (red)":
		case "Swift gloves (yellow)":
		case "Swift gloves (white)":
		case "Elysian sigil":
			return 900000000;
		case "Arcane spirit shield":
			return 700000000;
		case "Arcane sigil":
			return 400000000;
		case "Spectral spirit shield":
			return 350000000;
		case "Spectral sigil":
		case "Third-age druidic wreath":
		case "Third-age druidic cloak":
		case "Third-age druidic staff":
		case "Third-age full helmet":
		case "Third-age kiteshield":
		case "Third-age robe top":
		case "Third-age robe":
		case "Third-age mage hat":
		case "Third-age amulet":
		case "Third-age range top":
		case "Third-age range legs":
		case "Third-age range coif":
		case "Third-age vambraces":
		case "Abyssal whip":
		case "Dragonfire shield":
		case "Whip vine":
		case "Fighter hat":
		case "Enhanced excalibur":
		case "Zamorak godsword":
		case "Bandos godsword":
		case "Saradomin godsword":
			return 50000000;
		case "Armadyl helmet":
		case "Zamorakain spear":
		case "Saradomin sword":
		case "Ahrim's robe top":
		case "Ahrim's robe skirt":
		case "Ahrim's hood":
		case "Ahrim's staff":
		case "Dragon kiteshield":
			return 20000000;
		case "Torag's platebody":
		case "Torag's platelegs":
		case "Torag's helm":
		case "Torag's hammer":
		case "Guthan's platebody":
		case "Guthan's chainskirt":
		case "Guthan's helm":
		case "Guthan's warspear":
			return 2500000;
		case "Verac's brassard":
		case "Verac's plateskirt":
		case "Verac's helm":
		case "Verac's flail":
		case "Karil's top":
		case "Karil's skirt":
		case "Karil's coif":
		case "Karil's crossbow":
			return 10000000;
		case "Dharok's platebody":
		case "Dharok's platelegs":
		case "Dharok's helm":
		case "Dharok's greataxe":
		case "Royal torsion spring":
		case "Royal bolt stabiliser":
		case "Royal frame":
		case "Royal sight":
			return 75000000;
		case "Magic logs":
		case "Raw rocktail":
		case "Dragon bones":
		case "Ourg bones":
		case "Rune bar":
			return 250000;
		case "Frost dragon bones":
			return 1000000;
		case "Archery ticket":
			return 100000000;
		}
		return item.getDefinitions().getValue();
	}

}
