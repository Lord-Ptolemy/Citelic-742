package com.citelic.game.entity.player.content.dialogue.impl.misc;

import com.citelic.GameConstants;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.player.appearance.design.PlayerDesign;
import com.citelic.game.entity.player.content.dialogue.Dialogue;

public class StarterD extends Dialogue {

	/**
	 * Reference to classes
	 */
	private boolean warrior, ranger, magician = false;

	/**
	 * Reference to weapons
	 */
	private boolean scim, sword, ls, thsword, sbow, lbow, cbow, as, fs, ws, es,
			ms = false;

	/**
	 * Reference to S.A.R (Shield/Ammo/Runes)
	 */
	private boolean kish, sqsh, adfs, arr, bo, rp, rp1, ar, fr, wr, er,
			mr = false;

	/**
	 * Reference to crates
	 */
	private boolean c1, c2, c3 = false;

	/**
	 * Reference to selected option
	 */
	@SuppressWarnings("unused")
	private boolean o1, o2, o3, o4, o5 = false;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("Select a Starter", "Warrior", "Ranger",
					"Magician");
		} else if (stage == 0) {
			stage = 1;
			if (componentId == Dialogue.OPTION_1) {
				warrior = true;
				sendDialogue("Now go select your weapon.");
			}
			if (componentId == Dialogue.OPTION_2) {
				ranger = true;
				sendDialogue("Now go select your weapon.");
			}
			if (componentId == Dialogue.OPTION_3) {
				magician = true;
				sendDialogue("Now go select your staff.");
			}
		} else if (stage == 1) {
			stage = 2;
			if (warrior == true) {
				sendOptionsDialogue("Select a Weapon", "Scimitar", "Sword",
						"Longsword", "Two-handed sword");
			}
			if (ranger == true) {
				sendOptionsDialogue("Select a Weapon", "Shortbow", "Longbow",
						"Crossbow");
			}
			if (magician == true) {
				sendOptionsDialogue("Select a Staff", "Air Staff",
						"Fire Staff", "Water Staff", "Earth Staff", "Mindspike");
			}
		} else if (stage == 2) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 3;
				if (warrior == true) {
					scim = true;
					sendDialogue("You did successfully pick your weapon, now go select your shield.");
				}
				if (ranger == true) {
					sbow = true;
					sendDialogue("You did successfully pick your weapon, now go select your ammo.");
				}
				if (magician == true) {
					as = true;
					sendDialogue("You did successfully pick your staff, now go select your runes.");
				}
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 3;
				if (warrior == true) {
					sword = true;
					sendDialogue("You did successfully pick your weapon, now go select your shield.");
				}
				if (ranger == true) {
					lbow = true;
					sendDialogue("You did successfully pick your weapon, now go select your ammo.");
				}
				if (magician == true) {
					fs = true;
					sendDialogue("You did successfully pick your staff, now go select your runes.");
				}
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 3;
				if (warrior == true) {
					ls = true;
					sendDialogue("You did successfully pick your weapon, now go select your crate.");
				}
				if (ranger == true) {
					cbow = true;
					sendDialogue("You did successfully pick your weapon, now go select your ammo.");
				}
				if (magician == true) {
					ws = true;
					sendDialogue("You did successfully pick your staff, now go select your runes.");
				}
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 3;
				if (ranger == true) {
					sendDialogue("You did successfully pick your weapon, now go select your ammo.");
				}
				if (magician == true) {
					es = true;
					sendDialogue("You did successfully pick your staff, now go select your runes.");
				}
			}
			if (componentId == Dialogue.OPTION_4 && warrior == true) {
				stage = 6;
				thsword = true;
				sendDialogue("You did successfully pick your weapon, now go select your crate.");
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 3;
				if (magician == true) {
					ms = true;
					sendDialogue("You did successfully pick your staff, now go select your runes.");
				}
			}
		} else if (stage == 3) {
			stage = 4;
			if (warrior == true) {
				sendOptionsDialogue("Select a Shield", "Kitehield",
						"Sq Shield", "Anti-dragon shield");
			}
			if (ranger == true) {
				sendOptionsDialogue("Select Ammo", "Arrows (Bow)",
						"Bolts (C'bow)");
			}
			if (magician == true) {
				sendOptionsDialogue("Select Runes",
						"Basic Runes (Click to view)", "Air (500x)",
						"Fire (500x)", "Water (500x)", "Next");
			}
		} else if (stage == 4) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 6;
				if (warrior == true) {
					kish = true;
					sendDialogue("You did successfully pick your shield, now go select your crate.");
				}
				if (ranger == true) {
					arr = true;
					sendDialogue("You did successfully pick your ammo, now go select your crate.");
				}
			}
			if (componentId == Dialogue.OPTION_1 && magician == true) {
				stage = 70;
				rp1 = true;
				sendDialogue("This package of basic runes contains: 100 Air, Fire, Water, Earth and Mind runes. Are you sure you want this package?");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 6;
				if (warrior == true) {
					sqsh = true;
					sendDialogue("You did successfully pick your shield, now go select your crate.");
				}
				if (ranger == true) {
					bo = true;
					sendDialogue("You did successfully pick your ammo, now go select your crate.");
				}
				if (magician == true) {
					ar = true;
					sendDialogue("You did successfully pick your runes, now go select your crate.");
				}
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 6;
				if (warrior == true) {
					adfs = true;
					sendDialogue("You did successfully pick your shield, now go select your crate.");
				}
				if (ranger == true) {
					sendDialogue("You did successfully pick your ammo, now go select your crate.");
				}
				if (magician == true) {
					fr = true;
					sendDialogue("You did successfully pick your runes, now go select your crate.");
				}
			}
			if (componentId == Dialogue.OPTION_4) {
				stage = 6;
				if (warrior == true) {
					sendDialogue("You did successfully pick your shield, now go select your crate.");
				}
				if (ranger == true) {
					sendDialogue("You did successfully pick your ammo, now go select your crate.");
				}
				if (magician == true) {
					wr = true;
					sendDialogue("You did successfully pick your runes, now go select your crate.");
				}
			}
			if (componentId == Dialogue.OPTION_5) {
				stage = 5;
				sendOptionsDialogue("Select Runes", "Earth (500x)",
						"Mind (500x)", "Back");
			}
		} else if (stage == 5) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 6;
				er = true;
				sendDialogue("You did successfully pick your runes, now go select your crate.");
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 6;
				mr = true;
				sendDialogue("You did successfully pick your runes, now go select your crate.");
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 4;
				sendOptionsDialogue("Select Runes",
						"Basic Runes (Click to view)", "Air (500x)",
						"Fire (500x)", "Water (500x)", "Next");
			}
		} else if (stage == 6) {
			stage = 7;
			sendOptionsDialogue("Select a Crate", "Melee Crate", "Range Crate",
					"Magic Crate");
		} else if (stage == 7) {
			if (componentId == Dialogue.OPTION_1) {
				stage = 70;
				sendDialogue("This Melee Crate contains: 450.000 Coins, 150 Lobsters, 250 Attack, Strength and Defence potions. Are you sure you want this package?");
				o1 = true;
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 70;
				sendDialogue("This Range Crate contains: 450.000 Coins, 150 Lobsters and 750 Range potions. Are you sure you want this package?");
				o2 = true;
			}
			if (componentId == Dialogue.OPTION_3) {
				stage = 70;
				sendDialogue("This Magic Crate contains: 450.000 Coins, 150 Lobsters and 750 Magic potions. Are you sure you want this package?");
				o3 = true;
			}
		} else if (stage == 8) {
			if (warrior == true) {
				player.getInventory().addItem(1153, 1);
				player.getInventory().addItem(1115, 1);
				player.getInventory().addItem(1067, 1);
				player.getInventory().addItem(4121, 1);
			}
			if (ranger == true) {
				player.getInventory().addItem(1167, 1);
				player.getInventory().addItem(1129, 1);
				player.getInventory().addItem(1095, 1);
				player.getInventory().addItem(1063, 1);
			}
			if (magician == true) {
				player.getInventory().addItem(579, 1);
				player.getInventory().addItem(577, 1);
				player.getInventory().addItem(1011, 1);
				player.getInventory().addItem(2579, 1);
			}
			if (scim == true) {
				player.getInventory().addItem(1323, 1);
			}
			if (sword == true) {
				player.getInventory().addItem(1279, 1);
			}
			if (ls == true) {
				player.getInventory().addItem(1293, 1);
			}
			if (thsword == true) {
				player.getInventory().addItem(1309, 1);
			}
			if (sbow == true) {
				player.getInventory().addItem(841, 1);
			}
			if (lbow == true) {
				player.getInventory().addItem(839, 1);
			}
			if (cbow == true) {
				player.getInventory().addItem(837, 1);
			}
			if (as == true) {
				player.getInventory().addItem(1381, 1);
			}
			if (fs == true) {
				player.getInventory().addItem(1387, 1);
			}
			if (ws == true) {
				player.getInventory().addItem(1383, 1);
			}
			if (es == true) {
				player.getInventory().addItem(1385, 1);
			}
			if (ms == true) {
				player.getInventory().addItem(23044, 1);
			}
			if (kish == true) {
				player.getInventory().addItem(1191, 1);
			}
			if (sqsh == true) {
				player.getInventory().addItem(1175, 1);
			}
			if (adfs == true) {
				player.getInventory().addItem(1540, 1);
			}
			if (arr == true) {
				player.getInventory().addItem(884, 450);
			}
			if (bo == true) {
				player.getInventory().addItem(877, 450);
			}
			if (rp == true) {
				player.getInventory().addItem(556, 100);
				player.getInventory().addItem(554, 100);
				player.getInventory().addItem(555, 100);
				player.getInventory().addItem(557, 100);
				player.getInventory().addItem(558, 100);
			}
			if (ar == true) {
				player.getInventory().addItem(556, 500);
			}
			if (fr == true) {
				player.getInventory().addItem(554, 500);
			}
			if (wr == true) {
				player.getInventory().addItem(555, 500);
			}
			if (er == true) {
				player.getInventory().addItem(557, 500);
			}
			if (mr == true) {
				player.getInventory().addItem(558, 500);
			}
			if (c1 == true) {
				player.getInventory().addItem(380, 350);
				player.getInventory().addItem(2429, 250);
				player.getInventory().addItem(114, 250);
				player.getInventory().addItem(2433, 250);
				player.getInventory().addItem(995, 450000);
			}
			if (c2 == true) {
				player.getInventory().addItem(380, 350);
				player.getInventory().addItem(2445, 750);
				player.getInventory().addItem(995, 450000);
			}
			if (c3 == true) {
				player.getInventory().addItem(380, 350);
				player.getInventory().addItem(3041, 750);
				player.getInventory().addItem(995, 450000);
			}
			player.starter = false;
			player.unlock();
			PlayerDesign.open(player);
			Engine.sendWorldMessage(
					"<img=7><col=DF0101>Welcome " + player.getDisplayName()
							+ " to Citelic.", false);
			end();
		} else if (stage == 70) {
			stage = 71;
			sendOptionsDialogue(Dialogue.SEND_DEFAULT_OPTIONS_TITLE, "Yes",
					"No");
		} else if (stage == 71) {
			if (componentId == Dialogue.OPTION_1) {
				if (rp1 == true) {
					stage = 6;
					rp = true;
					sendDialogue("You did successfully pick your runes, now go select your crate.");
				}
				if (o1 == true) {
					stage = 8;
					c1 = true;
					sendDialogue("Your starter is now complete, click the continue button to recieve your items.");
				}
				if (o2 == true) {
					stage = 8;
					c2 = true;
					sendDialogue("Your starter is now complete, click the continue button to recieve your items.");
				}
				if (o3 == true) {
					stage = 8;
					c3 = true;
					sendDialogue("Your starter is now complete, click the continue button to recieve your items.");
				}
			}
			if (componentId == Dialogue.OPTION_2) {
				stage = 4;
				sendOptionsDialogue("Select Runes",
						"Basic Runes (Click to view)", "Air (500x)",
						"Fire (500x)", "Water (500x)", "Next");
				if (o1 == true) {
					stage = 7;
					o1 = false;
					sendOptionsDialogue("Select a Crate", "Melee Crate",
							"Range Crate", "Magic Crate");
				}
				if (o2 == true) {
					stage = 7;
					o2 = false;
					sendOptionsDialogue("Select a Crate", "Melee Crate",
							"Range Crate", "Magic Crate");
				}
				if (o3 == true) {
					stage = 7;
					o3 = false;
					sendOptionsDialogue("Select a Crate", "Melee Crate",
							"Range Crate", "Magic Crate");
				}
			}
		}
	}

	@Override
	public void start() {
		sendDialogue("Welcome to " + GameConstants.SERVER_NAME
				+ ", please select a starter package.");
		player.lock();
	}

}
