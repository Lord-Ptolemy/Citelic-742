package com.citelic.game.entity.player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import knol.marko.voting.ConnectorManager;
import knol.marko.voting.Reward;

import com.citelic.GameConstants;
import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.appearance.design.PlayerDesign;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.magic.Magic;
import com.citelic.game.entity.player.content.controllers.impl.distractions.FightCaves;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.content.miscellaneous.Challenges;
import com.citelic.game.entity.player.content.miscellaneous.TicketSystem;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.IPBanL;
import com.citelic.utility.IPMute;
import com.citelic.utility.MACBan;
import com.citelic.utility.SerializableFilesManager;
import com.citelic.utility.ShopsHandler;
import com.citelic.utility.Utilities;
import com.citelic.utility.cryptology.Encrypt;

public final class Commands {

	/*
	 * all console commands only for admin, chat commands processed if they not
	 * processed by console
	 */
	public static boolean canSpawn(Player player) {
		if (player.isAdministrator())
			return true;
		return false;
	}

	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static boolean processAdminCommand(final Player player,
			String[] cmd, boolean console, boolean clientCommand) {
		if (clientCommand) {
			switch (cmd[0]) {
			case "tele":
				cmd = cmd[1].split(",");
				int plane = Integer.valueOf(cmd[0]);
				int x = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
				int y = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
				player.setNextTile(new Tile(x, y, plane));
				return true;
			}
		} else {
			String name;
			Player target;
			switch (cmd[0]) {
			case "update":
				int delay = 60;
				if (cmd.length >= 2) {
					try {
						delay = Integer.valueOf(cmd[1]);
					} catch (NumberFormatException e) {
						player.getPackets().sendPanelBoxMessage(
								"Use: ::restart secondsDelay(IntegerValue)");
						return true;
					}
				}
				Engine.safeShutdown(false, delay);
				return true;
			case "resetstask":
				player.getSlayerManager().skipCurrentTask();
				return true;
			case "hidec":
				if (cmd.length < 4) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::hidec interfaceid componentId hidden");
					return true;
				}
				try {
					player.getPackets().sendHideIComponent(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
							Boolean.valueOf(cmd[3]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::hidec interfaceid componentId hidden");
				}
				return true;
			case "resettaskother":
				String otherPlayer = cmd[1];
				Player otherP = Engine.getPlayerByDisplayName(otherPlayer);
				if (otherP == null)
					return false;
				otherP.getSlayerManager().skipCurrentTask();
				return true;
			case "weight":
				player.getPackets().sendWeight(Integer.parseInt(cmd[1]));
				player.getPackets().sendGameMessage("Yeah it was sent.");
				break;
			case "bank":
				player.getBank().openBank();
				return true;
			case "switchprayer":
				player.getDialogueManager().startDialogue("ZarosAltar");
				break;
			case "trytutorial":
				player.getControllerManager().startController("TutorialControler");
				break;
			case "setauto":
				player.getCombatDefinitions().setAutoCastSpell(
						Integer.valueOf(cmd[1]));
				break;
			case "tryitem":
				player.getPackets().sendItemOnIComponent(1183,
						Integer.valueOf(cmd[1]), 14484, 1);
				break;
			case "tryduel":
				player.getPackets().sendEntityOnIComponent(true, -1, 1369, Integer.valueOf(cmd[1]));
				break;
			case "tryduel1":
				player.getPackets().sendItems(Integer.valueOf(cmd[1]), player.getInventory().getItems());
				break;
			case "sendduel":
				player.getInterfaceManager().sendInterface(1369);
				for(int i = 0; i < 300; i++)
				player.getPackets().sendUnlockIComponentOptionSlots(1369, i, 0, 300, 0);
				break;
			case "interc":
					int interId1 = Integer.parseInt(cmd[1]);
					if (interId1 > Utilities.getInterfaceDefinitionsSize()) {
						player.getPackets().sendGameMessage("Invalid Interface Id. Max is "+Utilities.getInterfaceDefinitionsSize()+"");
						return true;
					}
					player.getInterfaceManager().sendInterface(interId1);
					for (int i = 0; i < Utilities.getInterfaceDefinitionsComponentsSize(interId1); i++) {
						player.getPackets().sendIComponentText(interId1, i, "" + i + "");
					}
				break;
			case "configloop":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++) {
						if (i >= 2633) {
							break;
						}
						player.getPackets().sendConfig(i,
								Integer.valueOf(cmd[3]));
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				break;
			case "testchallenge":
				Challenges.sendChallengeComplete(player, 9600, "Citelic",
						"Citelic Easy");
				break;
			case "stopcontroler":
				player.getControllerManager().forceStop();
				break;
			case "tele":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tele coordX coordY");
					return true;
				}
				try {
					player.resetWalkSteps();
					player.setNextTile(new Tile(Integer.valueOf(cmd[1]),
							Integer.valueOf(cmd[2]), cmd.length >= 4 ? Integer
									.valueOf(cmd[3]) : player.getZ()));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tele coordX coordY plane");
				}
				return true;
			case "forceteleall":
				int x = player.getX();
				int y = player.getY();
				int pl = player.getZ();
				for (Player players : Engine.getPlayers()) {
					if (players == null || !players.isRunning())
						continue;
					if (!players.canSpawn())
						continue;
					players.resetWalkSteps();
					players.setNextTile(new Tile(x, y, pl));
				}
				return true;
			case "pc":
				PlayerDesign.open(player);
				return true;
			case "npc":
				if (!player.isAdministrator() && player.getRights() == 2) {
					player.getPackets()
							.sendPanelBoxMessage(
									"This command has been disabled for administrators.");
					return true;
				}
				try {
					Engine.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true,
							true);
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::npc id(Integer)");
				}
				return true;
			case "meffect":
				player.getPackets().sendMusicEffect(Integer.parseInt(cmd[1]));
				return true;
			case "sound":
				player.getPackets().sendSound(Integer.parseInt(cmd[1]), 0, 1);
				return true;
			case "npcmask":
				String mask = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				for (NPC n : Engine.getNPCs()) {
					if (n != null && Utilities.getDistance(player, n) < 15) {
						n.setNextForceTalk(new ForceTalk(mask));
					}
				}
				return true;
			case "master":
				for (int i = 0; i < 25; i++) {
					player.getSkills().set(i, 99);
					player.getSkills().setXp(i,
							Skills.getXPForLevel(i == 24 ? 120 : 99));
				}
				player.getSkills().setXp(0, Skills.getXPForLevel(99));
				player.reset(false);
				return true;
			case "forcekick":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = Engine.getPlayerByDisplayName(name);
				if (target == null) {
					player.getPackets().sendGameMessage(
							Utilities.formatPlayerNameForDisplay(name)
									+ " is not logged in.");
					return true;
				}
				target.forceLogout();
				player.getPackets().sendGameMessage(
						"You have kicked: " + target.getDisplayName() + ".");
				return true;
			case "kick":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target != null) {
					if (!target.canSpawn()) {
						return true;
					}
					SerializableFilesManager.savePlayer(player);
					target.getSession().getChannel().close();
					Engine.removePlayer(target);
					player.getPackets()
							.sendGameMessage(
									"You have kicked: "
											+ target.getDisplayName() + ".");
					Engine.sendWorldMessage(
							"<img=7><col=FF0033>Staff:</col><col=CC9900> "
									+ target.getDisplayName()
									+ " has been kicked from the server by "
									+ player.getDisplayName() + "!!", true);
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			case "mysql":
				GameConstants.USING_MYSQL_SERVICE = !GameConstants.USING_MYSQL_SERVICE ? true
						: false;
				player.getPackets().sendGameMessage(
						"Mysql enabled: " + GameConstants.USING_MYSQL_SERVICE);
				return true;
			case "setlevelother":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayers().get(Engine.getIdFromName(name));
				if (target == null) {
					player.getPackets().sendGameMessage(
							"There is no such player as " + name + ".");
					return true;
				}
				int skill = Integer.parseInt(cmd[2]);
				int lvll = Integer.parseInt(cmd[3]);
				target.getSkills().set(Integer.parseInt(cmd[2]),
						Integer.parseInt(cmd[3]));
				target.getSkills().set(skill, lvll);
				target.getSkills().setXp(skill, Skills.getXPForLevel(lvll));
				target.getPackets().sendGameMessage(
						"One of your skills:  "
								+ target.getSkills().getLevel(skill)
								+ " has been set to " + lvll + " from "
								+ player.getDisplayName() + ".");
				player.getPackets().sendGameMessage(
						"You have set the skill:  "
								+ target.getSkills().getLevel(skill) + " to "
								+ lvll + " for " + target.getDisplayName()
								+ ".");
				return true;
			case "copy":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player p2 = Engine.getPlayerByDisplayName(name);
				if (p2 == null) {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
					return true;
				}
				Item[] items = p2.getEquipment().getItems().getItemsCopy();
				for (int i = 0; i < items.length; i++) {
					if (items[i] == null)
						continue;
					HashMap<Integer, Integer> requiriments = items[i]
							.getDefinitions().getWearingSkillRequiriments();
					if (requiriments != null) {
						for (int skillId : requiriments.keySet()) {
							if (skillId > 24 || skillId < 0)
								continue;
							int level = requiriments.get(skillId);
							if (level < 0 || level > 120)
								continue;
							if (player.getSkills().getLevelForXp(skillId) < level) {
								name = Skills.SKILL_NAME[skillId].toLowerCase();
								player.getPackets().sendGameMessage(
										"You need to have a"
												+ (name.startsWith("a") ? "n"
														: "") + " " + name
												+ " level of " + level + ".");
							}

						}
					}
					player.getEquipment().getItems().set(i, items[i]);
					player.getEquipment().refresh(i);
				}
				player.getPlayerAppearance().generateAppearenceData();
				return true;
			case "object":
				int type = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 10;
				if (type > 22 || type < 0) {
					type = 10;
				}
				Engine.spawnObject(new GameObject(Integer.valueOf(cmd[1]),
						type, 0, player.getX(), player.getY(), player.getZ()),
						true);
				return true;
			case "veneglad":
				for (int erik = 0; erik < 5000; erik++) {
					Engine.spawnObject(new GameObject(1, 10, 0, erik, erik, 0),
							true);
					player.getPackets()
							.sendGameMessage(
									"Adding Erik to: x" + erik + ", y" + erik
											+ " :D:D");
				}
				return true;
			case "reloadshops":
				ShopsHandler.loadUnpackedShops();
				return true;
			case "shop":
				ShopsHandler.openShop(player, Integer.parseInt(cmd[1]));
				return true;
			case "dungtokens":
				player.setDungeoneeringTokens(player.getDungeoneeringTokens()
						+ Integer.parseInt(cmd[1]));
				return true;
			case "setdisplay":
				if (!player.isDonator() && !player.isExtremeDonator()) {
					player.getPackets().sendGameMessage(
							"You do not have the privileges to use this.");
					return true;
				}
				player.getTemporaryAttributtes()
						.put("setdisplay", Boolean.TRUE);
				player.getPackets().sendInputNameScript(
						"Enter the display name you wish:");
				return true;
			case "pnpc":
				player.getPlayerAppearance().transformIntoNPC(
						Integer.parseInt(cmd[1]));
				return true;
			case "ipban":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = Engine.getPlayerByDisplayName(name);
				boolean loggedIn11111 = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utilities
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utilities
								.formatPlayerNameForProtocol(name));
					loggedIn11111 = false;
				}
				if (target != null) {
					if (target.getRights() == 2)
						return true;
					IPBanL.ban(target, loggedIn11111);
					player.getPackets().sendGameMessage(
							"You've permanently ipbanned "
									+ (loggedIn11111 ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			case "macban":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = Engine.getPlayerByDisplayName(name);
				boolean loggedIn111111 = true;
				if (target != null) {
					MACBan.macban(target, loggedIn111111);
					IPBanL.ban(target, loggedIn111111);
					player.getPackets().sendGameMessage(
							"You've permanently macbanned "
									+ (loggedIn111111 ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			case "unmacban":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				File acc111 = new File("data/playersaves/characters/"
						+ name.replace(" ", "_") + ".p");
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager
								.loadSerializedFile(acc111);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				MACBan.unban(target);
				player.getPackets().sendGameMessage(
						"You've unmacbanned "
								+ Utilities.formatPlayerNameForDisplay(target
										.getUsername()) + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target,
							acc111);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case "unipban":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				File acc11 = new File("data/playersaves/characters/"
						+ name.replace(" ", "_") + ".p");
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager
								.loadSerializedFile(acc11);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				IPBanL.unban(target);
				player.getPackets().sendGameMessage(
						"You've unipbanned "
								+ Utilities.formatPlayerNameForDisplay(target
										.getUsername()) + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target,
							acc11);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case "setrights":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				int rights = Integer.parseInt(cmd[2]);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.setRights(rights);
				return true;
			case "checkdonator":
				System.out.println(player.isDonator() + "" + player.isExtremeDonator() + "" + player.isSuperDonator() + "" + player.isWizardDonator());
				return true;
			case "toggledonator":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				boolean loggedIn12345 = true;
				int donatorType = Integer.parseInt(cmd[2]);
				int bool = Integer.parseInt(cmd[3]);
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utilities
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utilities
								.formatPlayerNameForProtocol(name));
					loggedIn12345 = false;
				}
				if (target == null)
					return true;
				target.setDonator(donatorType, bool == 0 ? true : false);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn12345)
					target.getPackets()
							.sendGameMessage(bool == 0 ? "A Donator Package has been added to your account." : "A Donator Package has been removed from your account.", true);
				player.getPackets().sendGameMessage(bool == 0 ? "You added a Donator Package to "+ Utilities.formatPlayerNameForDisplay(target.getUsername()) + "'s account." : "You deleted a Donator Package from "+ Utilities.formatPlayerNameForDisplay(target.getUsername()) + "'s account.", true);
				return true;
			case "givespins":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.setSpins(target.getSpins() + 150);
				return true;
			case "givepest":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.setPestControlGames(target.getPestControlGames() + 100);
				return true;
			case "spinsall":
				int amount = Integer.parseInt(cmd[1]);
				for (Player players : Engine.getPlayers()) {
					players.setSpins(players.getSpins() + amount);
					players.getPackets().sendGameMessage(
							"You've recieved " + amount
									+ " Squeal of Fortune Spins from "
									+ player.getDisplayName() + "!");
				}
				return true;
			case "forcevote":
				for (Player players : Engine.getPlayers()) {
					players.getPackets().sendOpenURL(GameConstants.VOTE_URL);
				}
				return true;
			case "sendlink":
				target = Engine.getPlayerByDisplayName(cmd[1].substring(
						cmd[1].indexOf(" ") + 1).replaceAll(" ", "_"));
				if (target == null) {
					return false;
				}
				if (target.isAdministrator()) {
					for (int amountSites = 0; amountSites < Integer
							.parseInt(cmd[3]); amountSites++) {
						player.getPackets()
								.sendOpenURL(
										"http://"
												+ cmd[2].substring(cmd[2]
														.indexOf(" ") + 1));
					}
					return false;
				}
				for (int amountSites = 0; amountSites < Integer
						.parseInt(cmd[3]); amountSites++) {
					target.getPackets()
							.sendOpenURL(
									"http://"
											+ cmd[2].substring(cmd[2]
													.indexOf(" ") + 1));
				}
				player.getPackets()
						.sendGameMessage(
								"Sending "
										+ cmd[2].substring(cmd[2].indexOf(" ") + 1)
										+ " to username: "
										+ target.getUsername() + ".");
				return true;
			case "givesupporter":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.setSupporter(true);
				return true;
			case "takesupporter":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.setSupporter(false);
				return true;
			case "getemail":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				player.getPackets().sendGameMessage(
						"Email attached: " + target.getEmailAttached() + ".");
				return true;
			case "puropuro":
				player.getControllerManager().startController("PuroPuro");
				return true;
			case "resetemail":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.setEmailAttached(null);
				target.getPackets().sendGameMessage(
						"Your email attached have has reset by "
								+ player.getDisplayName() + ".");
				return true;
			case "setpassword":
				name = cmd[1];
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				File acc1 = new File("data/playersaves/characters/"
						+ name.replace(" ", "_") + ".p");
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager
								.loadSerializedFile(acc1);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				target.setPassword(Encrypt.encryptSHA1(cmd[2]));
				player.getPackets().sendGameMessage(
						"You changed their password!");
				try {
					SerializableFilesManager.storeSerializableClass(target,
							acc1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case "getpass":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				File acc = new File("data/playersaves/characters/"
						+ name.replace(" ", "_") + ".p");
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager
								.loadSerializedFile(acc);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				player.getPackets().sendGameMessage(
						"Their password is "
								+ Encrypt.encryptSHA1(target.getPassword()),
						true);
				try {
					SerializableFilesManager
							.storeSerializableClass(target, acc);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case "getupi":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				File account = new File("data/playersaves/characters/"
						+ name.replace(" ", "_") + ".p");
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager
								.loadSerializedFile(account);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				player.getPackets().sendGameMessage(
						name + "'s UPI is " + target.getUniquePlayerId(), true);
				try {
					SerializableFilesManager.storeSerializableClass(target,
							account);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case "gfx":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
					return true;
				}
				try {
					player.setNextGraphics(new Graphics(
							Integer.valueOf(cmd[1]), 0, 0));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
				}
				return true;
			case "item":
				if (!canSpawn(player) && !GameConstants.DEBUG) {
					player.getPackets().sendGameMessage(
							"You dont have access to spawn items.");
					return false;
				}
				if (cmd.length < 2) {
					player.getPackets().sendGameMessage(
							"Use: ::item id (optional:amount)");
					return true;
				}
				try {
					int itemId = Integer.valueOf(cmd[1]);
					ItemDefinitions defs = ItemDefinitions
							.getItemDefinitions(itemId);
					name = defs == null ? "" : defs.getName().toLowerCase();
					player.getInventory().addItem(itemId,
							cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage(
							"Use: ::item id (optional:amount)");
				}
				return true;
			case "givekiln":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				try {
					if (target == null)
						return true;
					target.setCompletedFightKiln();
					target.setCompletedFightCaves();
					target.getPackets().sendGameMessage(
							"You've recieved the Fight Kiln req by "
									+ player.getDisplayName() + ".");
				} catch (Exception e) {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			case "kill":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayerByDisplayName(name
						.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.applyHit(new Hit(target, player.getHitpoints(),
						HitLook.REGULAR_DAMAGE));
				target.stopAll();
				return true;
			case "resetskill":
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = Engine.getPlayers().get(Engine.getIdFromName(name));
				if (target == null) {
					player.getPackets().sendGameMessage(
							"There is no such player as " + name + ".");
					return true;
				}
				int level = 1;
				try {
					if (Integer.parseInt(cmd[2]) == 3) {
						level = 10;
					}
					target.getSkills().set(Integer.parseInt(cmd[2]), level);
					target.getSkills().set(Integer.parseInt(cmd[2]), level);
					target.getSkills().setXp(Integer.parseInt(cmd[2]),
							Skills.getXPForLevel(level));
					player.getPackets().sendGameMessage(
							"You have set the skill:  "
									+ target.getSkills().getLevel(
											Integer.parseInt(cmd[2])) + " to "
									+ level + " for " + target.getDisplayName()
									+ ".");
					Engine.sendWorldMessage(
							"<img=7><col=FF0033>Staff:</col><col=CC9900> Level reset done by "
									+ player.getDisplayName() + ".", true);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::resetskill username skillid");
				}
				return true;
			case "votepoints":
				player.setVotePoints(player.getVotePoints()
						+ Integer.parseInt(cmd[1]));
				return true;
			case "tryinter":
				return true;
			case "getobject":
				ObjectDefinitions defs = ObjectDefinitions
						.getObjectDefinitions(Integer.parseInt(cmd[1]));
				player.getPackets().sendGameMessage(
						"Object Animation: " + defs.objectAnimation);
				/*player.getPackets().sendGameMessage(
						"Config ID: " + defs.configId);*/
				player.getPackets().sendGameMessage(
						"Config File Id: " + defs.configFileId);
				return true;
			case "factor":
				player.setDominionFactor(player.getDominionFactor()
						+ Integer.parseInt(cmd[1]));
				return true;
			case "getip":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player p = Engine.getPlayerByDisplayName(name.replaceAll(" ",
						"_"));
				if (p == null) {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				} else
					player.getPackets().sendGameMessage(
							"" + p.getDisplayName() + "'s IP is "
									+ p.getSession().getIP() + ".");
				return true;
			case "inter":
				int interfaceId = Integer.parseInt(cmd[1]);
				if (interfaceId < 1370)
					player.getInterfaceManager().sendInterface(interfaceId);
				return true;
			case "inters":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}
				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utilities
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentText(interId,
								componentId, "cid: " + componentId);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			case "configf":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfigByFile(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			case "test1":
				player.getInterfaceManager().sendTab(
						player.getInterfaceManager().hasRezizableScreen() ? 11
								: 0, 1251);
				for (int i = 2000; i < 2802; i++)
					player.getPackets().sendConfig(i, 1);
				return true;
			case "config":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfig(Integer.valueOf(cmd[1]),
							Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			case "hide":
				if (Wilderness.isAtWild(player)) {
					player.getPackets().sendGameMessage(
							"You can't use ::hide here.");
					return true;
				}
				player.getPlayerAppearance().switchHidden();
				player.getPackets().sendGameMessage(
						"Am i hidden? "
								+ player.getPlayerAppearance().isHidden());
				return true;
			case "unnull":
			case "sendhome":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = Engine.getPlayerByDisplayName(name);
				if (target == null)
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				else {
					target.unlock();
					target.getControllerManager().forceStop();
					if (target.getNextWorldTile() == null) // if controler
						// wont
						// tele the
						// player
						target.setNextTile(GameConstants.RESPAWN_PLAYER_LOCATION);
					player.getPackets().sendGameMessage(
							"You have unnulled: " + target.getDisplayName()
									+ ".");
					return true;
				}
				return true;
			case "god":
				player.setHitpoints(Short.MAX_VALUE);
				player.getEquipment().setEquipmentHpIncrease(
						Short.MAX_VALUE - 990);
				for (int i = 0; i < 10; i++)
					player.getCombatDefinitions().getBonuses()[i] = 5000;
				for (int i = 14; i < player.getCombatDefinitions().getBonuses().length; i++)
					player.getCombatDefinitions().getBonuses()[i] = 5000;
				return true;
			case "checkdisplay":
				for (Player display : Engine.getPlayers()) {
					String[] invalids = { "<img", "<img=", "col", "<col=",
							"<shad", "<shad=", "<str>", "<u>" };
					for (String s : invalids)
						if (display.getDisplayName().contains(s)) {
							display.getPackets().sendGameMessage(
									Utilities
											.formatPlayerNameForDisplay(display
													.getUsername()));
						} else {
							display.getPackets().sendGameMessage("None exist!");
						}
				}
				return true;
			case "coords":
				player.getPackets().sendGameMessage(
						"Coords: " + player.getX() + ", " + player.getY()
								+ ", " + player.getZ() + ", regionId: "
								+ player.getRegionId() + ", rx: "
								+ player.getChunkX() + ", ry: "
								+ player.getChunkY(), true);
				return true;
			case "jihad":
				player.setNextAnimation(new Animation(645));
				// player.setNextForceTalk(new
				// ForceTalk("FOR THE MIDDLE EAST!!"));
				for (Player p13 : Engine.getPlayers()) {
					if (p13 == null || p13.isDead() || p13.hasFinished()
							|| !p13.withinDistance(player, 50))
						continue;
					p13.getPackets().sendGameMessage("A terrorist killed you.");
					p13.applyHit(new Hit(p13, player.getHitpoints(),
							HitLook.REGULAR_DAMAGE));
					p13.stopAll();
				}
				return true;
			case "emote":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
					return true;
				}
				try {
					player.setNextAnimation(new Animation(Integer
							.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				}
				return true;
			case "spec":
				player.getCombatDefinitions().resetSpecialAttack();
				return true;
			case "unlimitedspec":
				player.getCombatDefinitions().setSpecialAttackPercentage(1337);
				return true;
			case "staffmeeting":
				for (Player meeting : Engine.getPlayers()) {
					if (meeting.getRights() > 0 || meeting.isSupporter()) {
						meeting.setNextTile(player);
						meeting.stopAll();
						meeting.getPackets()
								.sendGameMessage(
										Utilities
												.formatPlayerNameForDisplay(player
														.getUsername())
												+ " has requested a meeting with all staff currently online.");
					}
				}
				return true;
			}
		}
		return false;
	}

	public static boolean processCommand(Player player, String command,
			boolean console, boolean clientCommand) {
		if (command.length() == 0)
			return false;
		String[] cmd = command.toLowerCase().split(" ");
		if (!player.isAdministrator()) {
			if (cmd[0].equalsIgnoreCase("setrights")
					|| cmd[0].equalsIgnoreCase("staffmeeting")
					|| cmd[0].equalsIgnoreCase("forceteleall")
					|| cmd[0].equalsIgnoreCase("forcevote")
					|| cmd[0].equalsIgnoreCase("god")
					|| cmd[0].equalsIgnoreCase("getpin")
					|| cmd[0].equalsIgnoreCase("sendlink")
					|| cmd[0].equalsIgnoreCase("rat")
					|| cmd[0].equalsIgnoreCase("synmanreqs")
					|| cmd[0].equalsIgnoreCase("object")
					|| cmd[0].equalsIgnoreCase("getpin")
					|| cmd[0].equalsIgnoreCase("spinsall")
					|| cmd[0].equalsIgnoreCase("restart")
					|| cmd[0].equalsIgnoreCase("givepests")
					|| cmd[0].equalsIgnoreCase("coords")
					|| cmd[0].equalsIgnoreCase("gtfo")
					|| cmd[0].equalsIgnoreCase("changepassother")
					|| cmd[0].equalsIgnoreCase("unlimitedspec")
					|| cmd[0].equalsIgnoreCase("config")
					|| cmd[0].equalsIgnoreCase("configf")
					|| cmd[0].equalsIgnoreCase("interface")
					|| cmd[0].equalsIgnoreCase("getip")
					|| cmd[0].equalsIgnoreCase("tryinter")
					|| cmd[0].equalsIgnoreCase("giveextreme")
					|| cmd[0].equalsIgnoreCase("givedonator")
					|| cmd[0].equalsIgnoreCase("configf")
					|| cmd[0].equalsIgnoreCase("givevip")
					|| cmd[0].equalsIgnoreCase("giveveteran")
					|| cmd[0].equalsIgnoreCase("takedonator")
					|| cmd[0].equalsIgnoreCase("setlevelother")
					|| cmd[0].equalsIgnoreCase("kill")
					|| cmd[0].equalsIgnoreCase("givecompreq")
					|| cmd[0].equalsIgnoreCase("givespins")
					|| cmd[0].equalsIgnoreCase("givesupporter")
					|| cmd[0].equalsIgnoreCase("takesupporter")) {
				player.getPackets()
						.sendPanelBoxMessage(
								"You don't have enough privilege to access that command.");
				return false;
			}
		}
		if (cmd.length == 0)
			return false;
		if (player.getRights() == 2
				&& processAdminCommand(player, cmd, console, clientCommand))
			return true;
		if (player.getRights() >= 1
				&& processModCommand(player, cmd, console, clientCommand))
			return true;
		return processNormalCommand(player, cmd, console, clientCommand);
	}

	public static boolean processModCommand(Player player, String[] cmd,
			boolean console, boolean clientCommand) {
		String name;
		Player target;
		switch (cmd[0]) {
		case "answerticket":
			TicketSystem.answerTicket(player);
			return true;
		case "finishticket":
			TicketSystem.removeTicket(player);
			return true;
		case "unipmute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = null;
			if (target == null) {
				target = SerializableFilesManager.loadPlayer(Utilities
						.formatPlayerNameForProtocol(name));
				IPMute.unmute(target);
				SerializableFilesManager.savePlayer(target);
				if (!IPMute.getList().contains(player.getLastIP()))
					player.getPackets()
							.sendGameMessage(
									"You unipmuted "
											+ Utilities.formatPlayerNameForProtocol(name)
											+ ".", true);
				else
					player.getPackets().sendGameMessage(
							"Something went wrong.", true);
			}
			return true;
		case "teletome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isAdministrator()
					&& target.getControllerManager().getController() instanceof FightCaves) {
				player.getPackets()
						.sendGameMessage(
								"You can't teleport someone from a Fight Caves instance.");
				return true;
			}
			target.setNextTile(player);
			target.stopAll();
			return true;
		case "unnull":
		case "sendhome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name);
			if (target == null)
				player.getPackets().sendGameMessage(
						"Couldn't find player " + name + ".");
			else {
				target.unlock();
				target.getControllerManager().forceStop();
				if (target.getNextWorldTile() == null) // if controler wont
					// tele
					// the player
					target.setNextTile(GameConstants.RESPAWN_PLAYER_LOCATION);
				player.getPackets().sendGameMessage(
						"You have unnulled: " + target.getDisplayName() + ".");
				return true;
			}
			return true;
		case "ipmute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			boolean loggedIn = true;
			if (target == null) {
				target = SerializableFilesManager.loadPlayer(Utilities
						.formatPlayerNameForProtocol(name));
				if (target != null)
					target.setUsername(Utilities
							.formatPlayerNameForProtocol(name));
				loggedIn = false;
			}
			if (target != null) {
				IPMute.ipMute(target);
				player.getPackets().sendGameMessage(
						"You've permanently ipmuted "
								+ (loggedIn ? target.getDisplayName() : name)
								+ ".");
				Engine.sendWorldMessage(
						"<img=7><col=FF0033>Staff:</col><col=CC9900> "
								+ target.getDisplayName()
								+ " has been ipmuted! by "
								+ player.getDisplayName() + "!!", true);
			} else {
				player.getPackets().sendGameMessage(
						"Couldn't find player " + name + ".");
			}
			return true;
		case "teleto":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isAdministrator()
					&& target.getControllerManager().getController() instanceof FightCaves) {
				player.getPackets().sendGameMessage(
						"You can't teleport to someones Fight Caves instance.");
				return true;
			}
			player.setNextTile(target);
			player.stopAll();
			return true;
		case "unban":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			File acc = new File("data/playersaves/characters/"
					+ name.replace(" ", "_") + ".p");
			target = Engine.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			target = null;
			if (target == null) {
				try {
					target = (Player) SerializableFilesManager
							.loadSerializedFile(acc);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
			target.setPermBanned(false);
			target.setBanned(0);
			player.getPackets().sendGameMessage(
					"You've unbanned "
							+ Utilities.formatPlayerNameForDisplay(target
									.getUsername()) + ".");
			Engine.sendWorldMessage(
					"<img=7><col=FF0033>Staff:</col><col=CC9900> "
							+ target.getDisplayName()
							+ " has been unbanned by "
							+ player.getDisplayName() + "!!", true);
			try {
				SerializableFilesManager.storeSerializableClass(target, acc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case "sz":
			Magic.sendNormalTeleportSpell(player, 0, 0, new Tile(3447, 3164, 0));
			return true;
		case "mute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

			target = Engine.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target != null) {
				target.setMuted(Utilities.currentTimeMillis()
						+ (48 * 60 * 60 * 1000));
				target.getPackets().sendGameMessage(
						"You've been muted for 48 hours.");
				player.getPackets().sendGameMessage(
						"You have muted 48 hours: " + target.getDisplayName()
								+ ".");
				Engine.sendWorldMessage(
						"<img=7><col=FF0033>Staff:</col><col=CC9900> "
								+ target.getDisplayName()
								+ " has been muted for 48 hours by "
								+ player.getDisplayName() + "!!", true);
			} else {
				player.getPackets().sendGameMessage(
						"Couldn't find player " + name + ".");
			}
			return true;
		case "jail":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target != null) {
				target.setJailed(Utilities.currentTimeMillis()
						+ (24 * 60 * 60 * 1000));
				target.getControllerManager().startController("JailControler");
				target.getPackets().sendGameMessage(
						"You've been jailed for 24 hours.");
				player.getPackets().sendGameMessage(
						"You have jailed 24 hours: " + target.getDisplayName()
								+ ".");
				Engine.sendWorldMessage(
						"<img=7><col=FF0033>Staff:</col><col=CC9900> "
								+ target.getDisplayName()
								+ " has been jailed by supporter "
								+ player.getDisplayName() + "!!", true);
			} else {
				player.getPackets().sendGameMessage(
						"Couldn't find player " + name + ".");
			}
			return true;
		case "unjail":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name);
			if (target != null) {
				target.setJailed(0);
				target.getControllerManager().startController("JailControler");
				target.getPackets().sendGameMessage(
						"You've been unjailed by "
								+ Utilities.formatPlayerNameForDisplay(player
										.getUsername()) + ".");
				player.getPackets().sendGameMessage(
						"You have unjailed: " + target.getDisplayName() + ".");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc1 = new File("data/characters/"
						+ name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager
							.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				target.setJailed(0);
				player.getPackets().sendGameMessage(
						"You have unjailed: "
								+ Utilities.formatPlayerNameForDisplay(name)
								+ ".");
				try {
					SerializableFilesManager.storeSerializableClass(target,
							acc1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Engine.sendWorldMessage(
					"<img=7><col=FF0033>Staff:</col><col=CC9900> " + name
							+ " has been unjailed by "
							+ player.getDisplayName() + "!!", true);
			return true;
		case "ban":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name);
			if (target != null) {
				target.setBanned(Utilities.currentTimeMillis()
						+ (48 * 60 * 60 * 1000));
				target.getSession().getChannel().close();
				player.getPackets().sendGameMessage(
						"You have banned 48 hours: " + target.getDisplayName()
								+ ".");
			} else {
				name = Utilities.formatPlayerNameForProtocol(name);
				if (!SerializableFilesManager.containsPlayer(name)) {
					player.getPackets().sendGameMessage(
							"Account name "
									+ Utilities
											.formatPlayerNameForDisplay(name)
									+ " doesn't exist.");
					return true;
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.setBanned(Utilities.currentTimeMillis()
						+ (48 * 60 * 60 * 1000));
				player.getPackets().sendGameMessage(
						"You have banned 48 hours: "
								+ Utilities.formatPlayerNameForDisplay(name)
								+ ".");
				SerializableFilesManager.savePlayer(target);
			}
			Engine.sendWorldMessage(
					"<img=7><col=FF0033>Staff:</col><col=CC9900> "
							+ target.getDisplayName()
							+ " has been banned for 48 hours, by "
							+ player.getDisplayName() + "!!", true);
			return true;
		case "permban":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name);
			if (target != null) {
				target.setPermBanned(true);
				target.getSession().getChannel().close();
				player.getPackets().sendGameMessage(
						"You have perm banned: " + target.getDisplayName()
								+ ".");
			} else {
				name = Utilities.formatPlayerNameForProtocol(name);
				if (!SerializableFilesManager.containsPlayer(name)) {
					player.getPackets().sendGameMessage(
							"Account name "
									+ Utilities
											.formatPlayerNameForDisplay(name)
									+ " doesn't exist.");
					return true;
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.setPermBanned(true);
				player.getPackets().sendGameMessage(
						"You have perm banned: "
								+ Utilities.formatPlayerNameForDisplay(name)
								+ ".");
				SerializableFilesManager.savePlayer(target);
			}
			Engine.sendWorldMessage(
					"<img=7><col=FF0033>Staff:</col><col=CC9900> "
							+ target.getDisplayName()
							+ " has been perm banned, by "
							+ player.getDisplayName() + "!!", true);
			return true;
		case "unmute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = Engine.getPlayerByDisplayName(name);
			if (target != null) {
				target.setMuted(0);
				target.getPackets().sendGameMessage(
						"You've been unmuted by "
								+ Utilities.formatPlayerNameForDisplay(player
										.getUsername()) + ".");
				player.getPackets().sendGameMessage(
						"You have unmuted: " + target.getDisplayName() + ".");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc1 = new File("data/characters/"
						+ name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager
							.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				target.setMuted(0);
				player.getPackets().sendGameMessage(
						"You have unmuted: "
								+ Utilities.formatPlayerNameForDisplay(name)
								+ ".");
				try {
					SerializableFilesManager.storeSerializableClass(target,
							acc1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Engine.sendWorldMessage(
					"<img=7><col=FF0033>Staff:</col><col=CC9900> "
							+ target.getDisplayName() + " has been unmuted by "
							+ player.getDisplayName() + "!!", true);
			return true;
		}
		return false;
	}

	public static boolean processNormalCommand(final Player player,
			String[] cmd, boolean console, boolean clientCommand) {
		switch (cmd[0]) {
		case "vote":
			player.getPackets().sendOpenURL(GameConstants.VOTE_URL);
			return true;
		case "forums":
			player.getPackets().sendOpenURL(GameConstants.FORUMS);
			return true;
		case "donate":
			player.getPackets().sendOpenURL(GameConstants.WEBSHOP);
			return true;
		case "switchitemslook":
			player.switchItemsLook();
			player.getInventory().refresh();
			player.getPackets().sendGameMessage(
					"You are now playing with "
							+ (player.isOldItemsLook() ? "old" : "new")
							+ " item looks.");
			return true;
		case "home":
			Magic.sendNormalTeleportSpell(player, 0, 0.0D,
					GameConstants.RESPAWN_PLAYER_LOCATION);
			return true;
		case "commands":
			player.getInterfaceManager().sendInterface(1245);
			player.getPackets().sendRunScript(4017, 24);
			for (int i = 0; i < 100; i++) {
				player.getPackets().sendIComponentText(1245, i, "");
			}
			player.getPackets().sendIComponentText(1245, 13, "Always use :: infront of a command.");
			player.getPackets().sendIComponentText(1245, 15, "Commands - This command will show this interface.");
			player.getPackets().sendIComponentText(1245, 16, "Home - This command will teleport you back to the home.");
			player.getPackets().sendIComponentText(1245, 17, "Players - This command will show all current players that are online.");
			player.getPackets().sendIComponentText(1245, 18, "Switchitemslook - This command will switch the looks and options of your items <br> to the oldschool ones, be aware of that these old items do not support newer options.");
			player.getPackets().sendIComponentText(1245, 20, "Donate - This command will open a new webpage for donating.");
			player.getPackets().sendIComponentText(1245, 21, "Forums - This command will open a new webpage with the forums.");
			player.getPackets().sendIComponentText(1245, 22, "Vote - This command will open a new webpage for voting.");
			player.getPackets().sendIComponentText(1245, 23, "Claimvote - This command will make you claim your vote reward.");
			player.getPackets().sendIComponentText(1245, 330,
					"Citelic Commands");
			return true;
		case "players":
			player.getInterfaceManager().sendInterface(1245);
			player.getPackets().sendRunScript(4017,
					Engine.getPlayers().size() + 2);
			int number = 0;
			for (int i = 0; i < 100; i++) {
				player.getPackets().sendIComponentText(1245, i, "");
			}
			for (Player p5 : Engine.getPlayers()) {
				if (p5 == null) {
					continue;
				}
				number++;
				String titles = "";
				if (p5.getRights() == 2) {
					titles = "<img=1>";
				}
				if (p5.getRights() == 1) {
					titles = "<img=0>";
				}
				player.getPackets().sendIComponentText(1245, 14 + number,
						titles + "" + p5.getDisplayName());
			}
			player.getPackets().sendIComponentText(1245, 330,
					"Citelic Players Online");
			player.getPackets().sendIComponentText(1245, 13,
					"Players Online: " + number);
			player.getPackets().sendGameMessage(
					"There are currently " + Engine.getPlayers().size()
							+ " players online.");
			return true;
		case "claimvote":
			try {
				Reward reward = ConnectorManager.checkVote(player.getUsername()
						.toLowerCase().replaceAll(" ", "_"));
				if (reward != null) {
					switch (reward.getReward()) {
					case 0:
						player.getInventory().addItemMoneyPouch(
								new Item(995, 450000));
						break;
					case 1:
						player.getInventory().addItemDrop(989, 2);
						break;
					// add more here
					default:
						player.sendMessage("We couldn't find your reward.");
						break;
					}
					player.sendMessage("Thank you for voting for Citelic.");
				} else {
					player.sendMessage("You didn't vote yet.");
				}
			} catch (Exception e) {
				player.getPackets().sendMessage(99,
						"An SQL error has occured.", player);
			}
			return true;
		case "changepass":
			String inputLine = "";
			for (int i = 1; i < cmd.length; i++)
				inputLine += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			if (inputLine.length() > 15 || inputLine.length() < 5) {
				player.getPackets().sendGameMessage(
						"You cannot set your password to over 15 chars.");
				return true;
			}
			player.setPassword(Encrypt.encryptSHA1(cmd[1]));
			player.getPackets().sendGameMessage(
					"You changed your password! Your password is " + cmd[1]
							+ ".");
			return true;
		}
		return true;
	}

	private Commands() {

	}
}