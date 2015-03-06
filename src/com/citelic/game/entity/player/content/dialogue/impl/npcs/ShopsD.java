package com.citelic.game.entity.player.content.dialogue.impl.npcs;

import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.utility.ShopsHandler;

public class ShopsD extends Dialogue {

	private int npcId;

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	public void openShop(int shopId) {
		ShopsHandler.openShop(player, shopId);
		end();
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
					"General Store", "Weapons", "Armour", "Skilling",
					"Consumables");
		} else if (stage == 0) {
			if (componentId == Dialogue.OPTION_1) {
				end();
				ShopsHandler.openShop(player, 252);
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 1;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Melee Weapons", "Ranged Weapons", "Magic Weapons");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 2;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Melee Armour", "Ranged Armour", "Magic Armour",
						"Pure Armour", "Miscellaneous");
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 3;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"General Skilling Supplies", "Woodcutting Supplies",
						"Mining Supplies", "Fishing Supplies", "Next Page");
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 22;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Food", "Potions");
			}
		} else if (stage == 1) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 4;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Swords", "Longswords", "Scimitars",
						"Two-handed Swords", "Next Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 5;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Ammo", "Shortbows", "Longbows", "Crossbows");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 6;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Runes", "Staffs", "Wands");
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 22;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Food", "Potions");
			}
		} else if (stage == 2) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 7;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Bronze", "Iron", "Steel", "Black", "Next Page");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 8;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Leather Armours", "Snakeskin Armour",
						"Dragonhide Armours", "Ava's");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 9;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Wizard Robes", "Mystic Robes", "Dagon'hai Robes",
						"Enchanted Robes");
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(249);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 19;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Skillcapes", "Helmets", "Amulets", "Rings", "Capes");
			}
		} else if (stage == 3) { // Opens shop
			if (componentId == Dialogue.OPTION_1) {
				openShop(214);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(215);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(216);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(217);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 14;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Smithing Supplies", "Runecrafting Supplies",
						"Hunter Supplies", "Herblore Supplies", "Next page");
			}
		} else if (stage == 4) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(182);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(183);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(184);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(185);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 11;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Daggers", "Battleaxes", "Maces", "Halberds",
						"Previous Page");
			}
		} else if (stage == 5) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(190);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(191);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(192);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(233);
			}
		} else if (stage == 6) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(193);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(194);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(195);
			}
		} else if (stage == 7) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(196);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(197);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(198);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(199);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 12;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Mithril", "Adamant", "Rune", "Dragon", "Next Page");
			}
		} else if (stage == 8) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(208);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(209);
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 13;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Green Dragonhide", "Blue Dragonhide",
						"Red Dragonhide", "Black Dragonhide");
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(245);
			}
		} else if (stage == 9) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(210);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(211);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(212);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(213);
			}
		} else if (stage == 22) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(238);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(237);
			}
		} else if (stage == 21) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(241);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(242);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(243);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(244);
			}
		} else if (stage == 20) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(234);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(235);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(236);
			}
		} else if (stage == 19) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 20;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Normal Skillcapes", "Trimmed Skillcapes",
						"Skill Hoods");
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(239);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(240);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(250);
			}
			if (componentId == Dialogue.OPTION_5) {
				openShop(251);
			}
		} else if (stage == 18) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(229);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(230);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(231);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(232);
			}
		} else if (stage == 17) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(224);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(225);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(226);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(227);
			}
			if (componentId == Dialogue.OPTION_5) {
				openShop(228);
			}
		} else if (stage == 16) { // Opens shops
			if (componentId == Dialogue.OPTION_1) {
				openShop(223);
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 17;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"General Farming Supplies",
						"Vegetable and Fruit Seeds", "Herb Seeds",
						"Flower Seeds", "Tree Saplings");
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(247);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 3;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"General Skilling Supplies", "Woodcutting Supplies",
						"Mining Supplies", "Fishing Supplies", "Next Page");
			}
		} else if (stage == 15) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(221);
			}
			if (componentId == Dialogue.OPTION_2) { // fletch
				stage = 18;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"General Fletching Supplies", "Bow Fletching Supplies",
						"Crossbow Fletching Supplies",
						"Projectile Fletching Supplies");
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(222);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 16;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Summoning Supplies", "Farming Supplies",
						"Construction Supplies", "Back to Page 1");
			}
		} else if (stage == 14) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(218);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(219);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(220);
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 21;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"General Herblore Supplies", "Herbs", "Potions",
						"Ingredients");
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 15;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Crafting Supplies", "Fletching Supplies",
						"Cooking Supplies", "Next page");
			}
		} else if (stage == 13) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(204);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(205);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(206);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(207);
			}
		} else if (stage == 12) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(200);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(201);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(202);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(203);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 23;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Proselyte", "First Page");
			}
		} else if (stage == 23) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(248);
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 7;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Bronze", "Iron", "Steel", "Black", "Next Page");
			}
		} else if (stage == 11) {
			if (componentId == Dialogue.OPTION_1) {
				openShop(186);
			}
			if (componentId == Dialogue.OPTION_2) {
				openShop(187);
			}
			if (componentId == Dialogue.OPTION_3) {
				openShop(188);
			}
			if (componentId == Dialogue.OPTION_4) {
				openShop(189);
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 4;
				sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE,
						"Swords", "Longswords", "Scimitars",
						"Two-handed Swords", "Next Page");
			}
		}
	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, Dialogue.NORMAL,
				"Hello there, they call me Major Nigel Corothers. Welcome to my store!");
	}
}
