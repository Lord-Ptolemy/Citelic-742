package com.citelic.utility.tools;

import java.io.File;
import java.io.IOException;

import com.citelic.cache.Cache;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.Utilities;

public class ItemRemoverC {

	public static String[] REMOVEITEMS = { "amulet of fury (or)",
			"staff of light", "dragon hatchet", "dragon pickaxe",
			"abyssal whip", "void knight top", "void knight robe",
			"void knight gloves", "void knight deflector", "Pestle and mortar",
			"Vial of weater", "grimy guam", "grimy marrentill",
			"grimy tarromin", "grimy harralander", "grimy rannar",
			"grimy irit", "grimy avanto", "grimy kwuarm", "grimy dwarf weed",
			"grimy torstol", "grimy lantadyme", "Eye of newt",
			"red spiders' eggs", "Limpwurt root", "Snape grass",
			"Unicorn horn dust", "White Berries", "Dragon scale dust",
			"Whine of zamorak", "Grenwall spikes", "Potato cactus",
			"Clean toadflax", "Morchelle mushroom", "Clean fellstalk",
			"Crushed nest", "Papaya fruit", "Mithril seeds", "Steadfast boots",
			"Ragefire boots", "Glaiven boots", "Mithril seeds" };

	public static void main(String[] args) {
		try {
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Starting");
		File[] chars = new File("data/playersaves/characters/").listFiles();
		for (File acc : chars) {
			if (Utilities.invalidAccountName(acc.getName().replace(".p", ""))) {
				acc.delete();
				continue;
			}
			try {
				Player player = (Player) SerializableFilesManager
						.loadSerializedFile(acc);
				if (player.getUniquePlayerId() == 0) {
					System.out.println("Removed account " + acc.getName());
					acc.delete();
					continue;
				}
				for (int id = 0; id < Utilities.getItemDefinitionsSize(); id++) {
					Item item = player.getBank().getItem(id);
					if (item == null)
						continue;
					if (item.getId() == 22207 || item.getId() == 22209
							|| item.getId() == 22211 || item.getId() == 22213)
						continue;
					String name = item.getDefinitions().getName().toLowerCase();
					for (String string : REMOVEITEMS) {
						if (name.equalsIgnoreCase(string)) {
							player.getBank().removeItem(
									player.getBank().getItemSlot(item.getId()),
									item.getAmount(), false, false);
						}
					}
				}
				for (Item item : player.getInventory().getItems().getItems()) {
					if (item == null)
						continue;
					if (item.getId() == 22207 || item.getId() == 22209
							|| item.getId() == 22211 || item.getId() == 22213)
						continue;
					if (item != null) {
						for (String string : REMOVEITEMS) {
							if (item.getDefinitions().getName().toLowerCase()
									.equalsIgnoreCase(string)) {
								player.getInventory().getItems().remove(item);
							}
						}
					}
				}
				for (Item item : player.getEquipment().getItems().getItems()) {
					if (item == null)
						continue;
					if (item.getId() == 22207 || item.getId() == 22209
							|| item.getId() == 22211 || item.getId() == 22213)
						continue;
					if (item != null) {
						for (String string : REMOVEITEMS) {
							if (item.getDefinitions().getName().toLowerCase()
									.equalsIgnoreCase(string)) {
								player.getEquipment().getItems().remove(item);
							}
						}
					}
				}
				SerializableFilesManager.storeSerializableClass(player, acc);
			} catch (Throwable e) {
				acc.delete();
				System.out.println("failed: " + acc.getName());
			}
		}
		System.out.println("done");
	}
}