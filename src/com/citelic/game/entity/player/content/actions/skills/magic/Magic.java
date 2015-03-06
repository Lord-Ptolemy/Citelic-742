package com.citelic.game.entity.player.content.actions.skills.magic;

import com.citelic.GameConstants;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.HomeTeleport;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars.FfaZone;
import com.citelic.game.entity.player.content.controllers.impl.distractions.clanwars.RequestController;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemConstants;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.codec.decode.WorldPacketsDecoder;
import com.citelic.utility.Utilities;

/*
 * content package used for static stuff
 */
public class Magic {

	public static final int MAGIC_TELEPORT = 0, ITEM_TELEPORT = 1,
			OBJECT_TELEPORT = 2;

	@SuppressWarnings("unused")
	private static final int AIR_RUNE = 556, WATER_RUNE = 555,
			EARTH_RUNE = 557, FIRE_RUNE = 554, MIND_RUNE = 558,
			NATURE_RUNE = 561, CHAOS_RUNE = 562, DEATH_RUNE = 560,
			BLOOD_RUNE = 565, SOUL_RUNE = 566, ASTRAL_RUNE = 9075,
			LAW_RUNE = 563, STEAM_RUNE = 4694, MIST_RUNE = 4695,
			DUST_RUNE = 4696, SMOKE_RUNE = 4697, MUD_RUNE = 4698,
			LAVA_RUNE = 4699, ARMADYL_RUNE = 21773;

	private final static Tile[] TABS = { new Tile(3217, 3426, 0),
			new Tile(3222, 3218, 0), new Tile(2965, 3379, 0),
			new Tile(2758, 3478, 0), new Tile(2660, 3306, 0) };

	public static final void castMiscellaneousSpell(Player player, int spellId,
			int itemId) {
		if (!ItemConstants.isTradeable(new Item(itemId))) {
			player.getPackets().sendGameMessage(
					"You cannot use this spell on an un-tradeable item.");
			return;
		}
		switch (spellId) {
		case 59:
			Alching.castAlchemy(player, itemId, false);
			break;
		case 50:
			Superheating.processSuperHeat(player, itemId);
			break;
		case 38:
			Alching.castAlchemy(player, itemId, true);
			break;
		}
	}

	public static final boolean checkCombatSpell(Player player, int spellId,
			int set, boolean delete) {
		if (spellId == 65535)
			return true;
		switch (player.getCombatDefinitions().getSpellBook()) {
		case 193:
			switch (spellId) {
			case 28:
				if (!checkSpellRequirements(player, 50, delete, CHAOS_RUNE, 2,
						DEATH_RUNE, 2, FIRE_RUNE, 1, AIR_RUNE, 1))
					return false;
				break;
			case 32:
				if (!checkSpellRequirements(player, 52, delete, CHAOS_RUNE, 2,
						DEATH_RUNE, 2, AIR_RUNE, 1, SOUL_RUNE, 1))
					return false;
				break;
			case 24:
				if (!checkSpellRequirements(player, 56, delete, CHAOS_RUNE, 2,
						DEATH_RUNE, 2, BLOOD_RUNE, 1))
					return false;
				break;
			case 20:
				if (!checkSpellRequirements(player, 58, delete, CHAOS_RUNE, 2,
						DEATH_RUNE, 2, WATER_RUNE, 2))
					return false;
				break;
			case 30:
				if (!checkSpellRequirements(player, 62, delete, CHAOS_RUNE, 4,
						DEATH_RUNE, 2, FIRE_RUNE, 2, AIR_RUNE, 2))
					return false;
				break;
			case 34:
				if (!checkSpellRequirements(player, 64, delete, CHAOS_RUNE, 4,
						DEATH_RUNE, 2, AIR_RUNE, 1, SOUL_RUNE, 2))
					return false;
				break;
			case 26:
				if (!checkSpellRequirements(player, 68, delete, CHAOS_RUNE, 4,
						DEATH_RUNE, 2, BLOOD_RUNE, 2))
					return false;
				break;
			case 22:
				if (!checkSpellRequirements(player, 70, delete, CHAOS_RUNE, 4,
						DEATH_RUNE, 2, WATER_RUNE, 4))
					return false;
				break;
			case 29:
				if (!checkSpellRequirements(player, 74, delete, DEATH_RUNE, 2,
						BLOOD_RUNE, 2, FIRE_RUNE, 2, AIR_RUNE, 2))
					return false;
				break;
			case 33:
				if (!checkSpellRequirements(player, 76, delete, DEATH_RUNE, 2,
						BLOOD_RUNE, 2, AIR_RUNE, 2, SOUL_RUNE, 2))
					return false;
				break;
			case 25:
				if (!checkSpellRequirements(player, 80, delete, DEATH_RUNE, 2,
						BLOOD_RUNE, 4))
					return false;
				break;
			case 21:
				if (!checkSpellRequirements(player, 82, delete, DEATH_RUNE, 2,
						BLOOD_RUNE, 2, WATER_RUNE, 3))
					return false;
				break;
			case 31:
				if (!checkSpellRequirements(player, 86, delete, DEATH_RUNE, 4,
						BLOOD_RUNE, 2, FIRE_RUNE, 4, AIR_RUNE, 4))
					return false;
				break;
			case 35:
				if (!checkSpellRequirements(player, 88, delete, DEATH_RUNE, 4,
						BLOOD_RUNE, 2, AIR_RUNE, 4, SOUL_RUNE, 3))
					return false;
				break;
			case 27:
				if (!checkSpellRequirements(player, 92, delete, DEATH_RUNE, 4,
						BLOOD_RUNE, 4, SOUL_RUNE, 1))
					return false;
				break;
			case 23:
				if (!checkSpellRequirements(player, 94, delete, DEATH_RUNE, 4,
						BLOOD_RUNE, 2, WATER_RUNE, 6))
					return false;
				break;
			case 36: // Miasmic rush.
				if (!checkSpellRequirements(player, 61, delete, CHAOS_RUNE, 2,
						EARTH_RUNE, 1, SOUL_RUNE, 1)) {
					return false;
				}
				int weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941
						&& weaponId != 13943) {
					player.getPackets().sendGameMessage(
							"You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage(
									"Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			case 38: // Miasmic burst.
				if (!checkSpellRequirements(player, 73, delete, CHAOS_RUNE, 4,
						EARTH_RUNE, 2, SOUL_RUNE, 2)) {
					return false;
				}
				weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941
						&& weaponId != 13943) {
					player.getPackets().sendGameMessage(
							"You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage(
									"Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			case 37: // Miasmic blitz.
				if (!checkSpellRequirements(player, 85, delete, BLOOD_RUNE, 2,
						EARTH_RUNE, 3, SOUL_RUNE, 3)) {
					return false;
				}
				weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941
						&& weaponId != 13943) {
					player.getPackets().sendGameMessage(
							"You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage(
									"Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			case 39: // Miasmic barrage.
				if (!checkSpellRequirements(player, 97, delete, BLOOD_RUNE, 4,
						EARTH_RUNE, 4, SOUL_RUNE, 4)) {
					return false;
				}
				weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941
						&& weaponId != 13943) {
					player.getPackets().sendGameMessage(
							"You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage(
									"Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			default:
				return false;
			}
			break;
		case 192:
			switch (spellId) {
			case 98:
				if (!checkSpellRequirements(player, 1, delete, AIR_RUNE, 2))
					return false;
				break;
			case 25:
				if (!checkSpellRequirements(player, 1, delete, AIR_RUNE, 1,
						MIND_RUNE, 1))
					return false;
				break;
			case 28:
				if (!checkSpellRequirements(player, 5, delete, WATER_RUNE, 1,
						AIR_RUNE, 1, MIND_RUNE, 1))
					return false;
				break;
			case 30:
				if (!checkSpellRequirements(player, 9, delete, EARTH_RUNE, 2,
						AIR_RUNE, 1, MIND_RUNE, 1))
					return false;
				break;
			case 32:
				if (!checkSpellRequirements(player, 13, delete, FIRE_RUNE, 3,
						AIR_RUNE, 2, MIND_RUNE, 1))
					return false;
				break;
			case 33:
				if (!checkSpellRequirements(player, 15, delete, EARTH_RUNE, 2,
						WATER_RUNE, 2, NATURE_RUNE, 1))
					return false;
				break;
			case 65:
				if (!checkSpellRequirements(player, 60, delete, EARTH_RUNE, 4,
						WATER_RUNE, 4, NATURE_RUNE, 2))
					return false;
				break;
			case 34: // air bolt
				if (!checkSpellRequirements(player, 17, delete, AIR_RUNE, 2,
						CHAOS_RUNE, 1))
					return false;
				break;
			case 36:// bind
				if (!checkSpellRequirements(player, 20, delete, EARTH_RUNE, 3,
						WATER_RUNE, 3, NATURE_RUNE, 2))
					return false;
				break;
			case 55: // snare
				if (!checkSpellRequirements(player, 50, delete, EARTH_RUNE, 4,
						WATER_RUNE, 4, NATURE_RUNE, 3))
					return false;
				break;
			case 81:// entangle
				if (!checkSpellRequirements(player, 79, delete, EARTH_RUNE, 5,
						WATER_RUNE, 5, NATURE_RUNE, 4))
					return false;
				break;
			case 39: // water bolt
				if (!checkSpellRequirements(player, 23, delete, WATER_RUNE, 2,
						AIR_RUNE, 2, CHAOS_RUNE, 1))
					return false;
				break;
			case 42: // earth bolt
				if (!checkSpellRequirements(player, 29, delete, EARTH_RUNE, 3,
						AIR_RUNE, 2, CHAOS_RUNE, 1))
					return false;
				break;
			case 45: // fire bolt
				if (!checkSpellRequirements(player, 35, delete, FIRE_RUNE, 4,
						AIR_RUNE, 3, CHAOS_RUNE, 1))
					return false;
				break;
			case 49: // air blast
				if (!checkSpellRequirements(player, 41, delete, AIR_RUNE, 3,
						DEATH_RUNE, 1))
					return false;
				break;
			case 52: // water blast
				if (!checkSpellRequirements(player, 47, delete, WATER_RUNE, 3,
						AIR_RUNE, 3, DEATH_RUNE, 1))
					return false;
				break;
			case 58: // earth blast
				if (!checkSpellRequirements(player, 53, delete, EARTH_RUNE, 4,
						AIR_RUNE, 3, DEATH_RUNE, 1))
					return false;
				break;
			case 63: // fire blast
				if (!checkSpellRequirements(player, 59, delete, FIRE_RUNE, 5,
						AIR_RUNE, 4, DEATH_RUNE, 1))
					return false;
				break;
			case 70: // air wave
				if (!checkSpellRequirements(player, 62, delete, AIR_RUNE, 5,
						BLOOD_RUNE, 1))
					return false;
				break;
			case 73: // water wave
				if (!checkSpellRequirements(player, 65, delete, WATER_RUNE, 7,
						AIR_RUNE, 5, BLOOD_RUNE, 1))
					return false;
				break;
			case 77: // earth wave
				if (!checkSpellRequirements(player, 70, delete, EARTH_RUNE, 7,
						AIR_RUNE, 5, BLOOD_RUNE, 1))
					return false;
				break;
			case 79: // teleother lumbridge
				if (!checkSpellRequirements(player, 74, delete, SOUL_RUNE, 1,
						LAW_RUNE, 1, EARTH_RUNE, 1))
					return false;
				break;
			case 85: // teleother falador
				if (!checkSpellRequirements(player, 82, delete, SOUL_RUNE, 1,
						LAW_RUNE, 1, WATER_RUNE, 1))
					return false;
				break;
			case 90: // teleother camelot
				if (!checkSpellRequirements(player, 90, delete, SOUL_RUNE, 2,
						LAW_RUNE, 1))
					return false;
				break;
			case 80: // fire wave
				if (!checkSpellRequirements(player, 75, delete, FIRE_RUNE, 7,
						AIR_RUNE, 5, BLOOD_RUNE, 1))
					return false;
				break;
			case 84:
				if (!checkSpellRequirements(player, 81, delete, AIR_RUNE, 7,
						DEATH_RUNE, 1, BLOOD_RUNE, 1))
					return false;
				break;
			case 87:
				if (!checkSpellRequirements(player, 85, delete, WATER_RUNE, 10,
						AIR_RUNE, 7, DEATH_RUNE, 1, BLOOD_RUNE, 1))
					return false;
				break;
			case 89:
				if (!checkSpellRequirements(player, 85, delete, EARTH_RUNE, 10,
						AIR_RUNE, 7, DEATH_RUNE, 1, BLOOD_RUNE, 1))
					return false;
				break;
			case 66: // Sara Strike
				if (player.getEquipment().getWeaponId() != 2415) {
					player.getPackets()
							.sendGameMessage(
									"You need to be equipping a Saradomin staff to cast this spell.",
									true);
					return false;
				}
				if (!checkSpellRequirements(player, 60, delete, AIR_RUNE, 4,
						FIRE_RUNE, 1, BLOOD_RUNE, 2))
					return false;
				break;
			case 67: // Guthix Claws
				if (player.getEquipment().getWeaponId() != 2416) {
					player.getPackets()
							.sendGameMessage(
									"You need to be equipping a Guthix Staff or Void Mace to cast this spell.",
									true);
					return false;
				}
				if (!checkSpellRequirements(player, 60, delete, AIR_RUNE, 4,
						FIRE_RUNE, 1, BLOOD_RUNE, 2))
					return false;
				break;
			case 68: // Flame of Zammy
				if (player.getEquipment().getWeaponId() != 2417) {
					player.getPackets()
							.sendGameMessage(
									"You need to be equipping a Zamorak Staff to cast this spell.",
									true);
					return false;
				}
				if (!checkSpellRequirements(player, 60, delete, AIR_RUNE, 4,
						FIRE_RUNE, 4, BLOOD_RUNE, 2))
					return false;
				break;
			case 91:
				if (!checkSpellRequirements(player, 85, delete, FIRE_RUNE, 10,
						AIR_RUNE, 7, DEATH_RUNE, 1, BLOOD_RUNE, 1))
					return false;
				break;
			case 86: // teleblock
				if (!checkSpellRequirements(player, 85, delete, CHAOS_RUNE, 1,
						LAW_RUNE, 1, DEATH_RUNE, 1))
					return false;
				break;
			case 99: // Storm of Armadyl
				if (!checkSpellRequirements(player, 77, delete, ARMADYL_RUNE, 1))
					return false;
				break;
			default:
				return false;
			}
			break;
		default:
			return false;
		}
		if (set >= 0) {
			if (set == 0) {
				player.getCombatDefinitions().setAutoCastSpell(spellId);
				player.getPackets().sendGameMessage("Autocast spell selected.");
			} else {
				player.getTemporaryAttributtes().put("tempCastSpell", spellId);
			}
		}
		return true;
	}

	public static final boolean checkRunes(Player player, boolean delete,
			int... runes) {
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		int runesCount = 0;
		while (runesCount < runes.length) {
			int runeId = runes[runesCount++];
			int ammount = runes[runesCount++];
			if (hasInfiniteRunes(runeId, weaponId, shieldId))
				continue;
			if (hasStaffOfLight(weaponId) && Utilities.getRandom(8) == 0
					&& runeId != 21773)// 1
				// in
				// eight
				// chance
				// of
				// keeping
				// runes
				continue;
			if (!player.getInventory().containsItem(runeId, ammount)) {
				player.getPackets().sendGameMessage(
						"You do not have enough "
								+ ItemDefinitions.getItemDefinitions(runeId)
										.getName().replace("rune", "Rune")
								+ "s to cast this spell.");
				return false;
			}
		}
		if (delete) {
			runesCount = 0;
			while (runesCount < runes.length) {
				int runeId = runes[runesCount++];
				int ammount = runes[runesCount++];
				if (hasInfiniteRunes(runeId, weaponId, shieldId))
					continue;
				player.getInventory().deleteItem(runeId, ammount);
			}
		}
		return true;
	}

	public static final boolean checkSpellRequirements(Player player,
			int level, boolean delete, int... runes) {
		if (player.getSkills().getLevelForXp(Skills.MAGIC) < level) {
			player.getPackets().sendGameMessage(
					"Your Magic level is not high enough for this spell.");
			return false;
		}
		return checkRunes(player, delete, runes);
	}

	public static final boolean hasInfiniteRunes(int runeId, int weaponId,
			int shieldId) {
		if (runeId == AIR_RUNE) {
			if (weaponId == 1381 || weaponId == 21777 || weaponId == 1397
					|| weaponId == 1405) // air
				// staff
				return true;
		} else if (runeId == WATER_RUNE) {
			if (weaponId == 1383 || shieldId == 18346 || weaponId == 1395
					|| weaponId == 1403 || weaponId == 6562 || weaponId == 6563
					|| weaponId == 11736 || weaponId == 11738) // water
				// staff
				return true;
		} else if (runeId == EARTH_RUNE) {
			if (weaponId == 1385 || weaponId == 1399 || weaponId == 1407
					|| weaponId == 3053 || weaponId == 3054 || weaponId == 6562
					|| weaponId == 6563) // earth
				// staff
				return true;
		} else if (runeId == FIRE_RUNE) {
			if (weaponId == 1387 || weaponId == 1387 || weaponId == 1401
					|| weaponId == 3053 || weaponId == 3054
					|| weaponId == 11736 || weaponId == 11738) // fire
				// staff
				return true;
		}
		return false;
	}

	public static boolean hasStaffOfLight(int weaponId) {
		if (weaponId == 15486 || weaponId == 22207 || weaponId == 22209
				|| weaponId == 22211 || weaponId == 22213)
			return true;
		return false;
	}

	static boolean isWearingFireStaff(int weaponId) {
		if (weaponId == 1387 || weaponId == 1393 || weaponId == 1401
				|| weaponId == 3053 || weaponId == 3054)
			return true;
		return false;
	}

	public static final void processAncientSpell(Player player, int spellId,
			int packetId) {
		player.stopAll(false);
		switch (spellId) {
		case 28:
		case 32:
		case 24:
		case 20:
		case 30:
		case 34:
		case 26:
		case 22:
		case 29:
		case 33:
		case 25:
		case 21:
		case 31:
		case 35:
		case 27:
		case 23:
		case 36:
		case 37:
		case 38:
		case 39:
			setCombatSpell(player, spellId);
			break;
		case 40:
			sendAncientTeleportSpell(player, 54, 64, new Tile(3099, 9882, 0),
					LAW_RUNE, 2, FIRE_RUNE, 1, AIR_RUNE, 1);
			break;
		case 41:
			sendAncientTeleportSpell(player, 60, 70, new Tile(3222, 3336, 0),
					LAW_RUNE, 2, SOUL_RUNE, 1);
			break;
		case 42:
			sendAncientTeleportSpell(player, 66, 76, new Tile(3492, 3471, 0),
					LAW_RUNE, 2, BLOOD_RUNE, 1);

			break;
		case 43:
			sendAncientTeleportSpell(player, 72, 82, new Tile(3006, 3471, 0),
					LAW_RUNE, 2, WATER_RUNE, 4);
			break;
		case 44:
			sendAncientTeleportSpell(player, 78, 88, new Tile(2990, 3696, 0),
					LAW_RUNE, 2, FIRE_RUNE, 3, AIR_RUNE, 2);
			break;
		case 45:
			sendAncientTeleportSpell(player, 84, 94, new Tile(3217, 3677, 0),
					LAW_RUNE, 2, SOUL_RUNE, 2);
			break;
		case 46:
			sendAncientTeleportSpell(player, 90, 100, new Tile(3288, 3886, 0),
					LAW_RUNE, 2, BLOOD_RUNE, 2);
			break;
		case 47:
			sendAncientTeleportSpell(player, 96, 106, new Tile(2977, 3873, 0),
					LAW_RUNE, 2, WATER_RUNE, 8);
			break;
		case 48:
			switch (packetId) {
			case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
				useHomeTele(player);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
				if (player.getLodeStoneTile() == null) {
					player.getPackets()
							.sendGameMessage(
									"You need to teleport somewhere before you can use this option.");
					return;
				}
				player.getActionManager().setAction(
						new HomeTeleport(player.getLodeStoneTile()));
				break;
			}
			break;
		}
	}

	public static final void processLunarSpell(Player player, int spellId,
			int packetId) {
		player.stopAll(false);
		switch (spellId) {
		case 37:
			if (player.getSkills().getLevel(Skills.MAGIC) < 94) {
				player.getPackets().sendGameMessage(
						"Your Magic level is not high enough for this spell.");
				return;
			} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
				player.getPackets().sendGameMessage(
						"You need a Defence level of 40 for this spell");
				return;
			}
			Long lastVeng = (Long) player.getTemporaryAttributtes().get(
					"LAST_VENG");
			if (lastVeng != null
					&& lastVeng + 30000 > Utilities.currentTimeMillis()) {
				player.getPackets()
						.sendGameMessage(
								"Players may only cast vengeance once every 30 seconds.");
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 4, DEATH_RUNE, 2,
					EARTH_RUNE, 10))
				return;
			player.setNextGraphics(new Graphics(726, 0, 100));
			player.setNextAnimation(new Animation(4410));
			player.setCastVeng(true);
			player.getTemporaryAttributtes().put("LAST_VENG",
					Utilities.currentTimeMillis());
			player.getPackets().sendGameMessage("You cast a vengeance.");
			break;
		case 74:
			Long lastVengGroup = (Long) player.getTemporaryAttributtes().get(
					"LAST_VENGGROUP");
			if (lastVengGroup != null
					&& lastVengGroup + 30000 > Utilities.currentTimeMillis()) {
				player.getPackets()
						.sendGameMessage(
								"Players may only cast vengeance group once every 30 seconds.");
				return;
			}
			if (!player.isAtMultiArea()) {
				player.getPackets().sendGameMessage(
						"You can only cast vengeance group in a multi area.");
				return;
			}
			if (player.getSkills().getLevel(Skills.MAGIC) < 95) {
				player.getPackets()
						.sendGameMessage(
								"You need a level of 95 magic to cast vengeance group.");
				return;
			}
			if (!player.getInventory().containsItem(560, 3)
					&& !player.getInventory().containsItem(557, 11)
					&& !player.getInventory().containsItem(9075, 4)) {
				player.getPackets().sendGameMessage(
						"You don't have enough runes to cast vengeance group.");
				return;
			}
			int count = 0;
			for (Player other : Engine.getPlayers()) {
				if (other.withinDistance(player, 4) && other.isAtMultiArea()) {
					other.getPackets()
							.sendGameMessage(
									"Someone cast the Group Vengeance spell and you were affected!");
					other.setCastVeng(true);
					other.setNextGraphics(new Graphics(725, 0, 100));
					other.getTemporaryAttributtes().put("LAST_VENGGROU P",
							Utilities.currentTimeMillis());
					other.getTemporaryAttributtes().put("LAST_VENG",
							Utilities.currentTimeMillis());
					count++;
				}
			}
			player.getPackets().sendGameMessage(
					"The spell affected " + count + " nearby people.");
			player.setNextGraphics(new Graphics(725, 0, 100));
			player.setNextAnimation(new Animation(4410));
			player.setCastVeng(true);
			player.getTemporaryAttributtes().put("LAST_VENGGROUP",
					Utilities.currentTimeMillis());
			player.getTemporaryAttributtes().put("LAST_VENG",
					Utilities.currentTimeMillis());
			player.getInventory().deleteItem(560, 3);
			player.getInventory().deleteItem(557, 11);
			player.getInventory().deleteItem(9075, 4);
			break;
		case 39:
			switch (packetId) {
			case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
				useHomeTele(player);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
				if (player.getLodeStoneTile() == null) {
					player.getPackets()
							.sendGameMessage(
									"You need to teleport somewhere before you can use this option.");
					return;
				}
				player.getActionManager().setAction(
						new HomeTeleport(player.getLodeStoneTile()));
				break;
			}
			break;
		case 42:
			if (player.getSkills().getLevel(Skills.MAGIC) < 93) {
				player.getPackets().sendGameMessage(
						"Your Magic level is not high enough for this spell.");
				return;
			} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
				player.getPackets().sendGameMessage(
						"You need a Defence level of 40 for this spell");
				return;
			}
			if (!checkRunes(player, true, EARTH_RUNE, 11, DEATH_RUNE, 3,
					ASTRAL_RUNE, 4))
				return;
			break;
		}
	}

	public static final void processNormalSpell(Player player, int spellId,
			int packetId) {
		player.stopAll(false);
		switch (spellId) {
		case 98: // wind rush
		case 25: // air strike
		case 28: // water strike
		case 30: // earth strike
		case 32: // fire strike
		case 34: // air bolt
		case 39: // water bolt
		case 42: // earth bolt
		case 45: // fire bolt
		case 49: // air blast
		case 52: // water blast
		case 58: // earth blast
		case 63: // fire blast
		case 70: // air wave
		case 73: // water wave
		case 77: // earth wave
		case 80: // fire wave
		case 99:
		case 84:
		case 87:
		case 89:
		case 91:
		case 36:
		case 55:
		case 81:
		case 66:
		case 67:
		case 68:
			setCombatSpell(player, spellId);
			break;
		case 27: // crossbow bolt enchant
			if (player.getSkills().getLevel(Skills.MAGIC) < 4) {
				player.getPackets().sendGameMessage(
						"Your Magic level is not high enough for this spell.");
				return;
			}
			player.stopAll();
			player.getInterfaceManager().sendInterface(432);
			break;
		case 33:
			BonesToBananas.castBonesToBananas(player);
			break;
		case 65:
			BonesToPeaches.castBonesToPeaches(player);
			break;
		case 24:
			switch (packetId) {
			case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
				useHomeTele(player);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
				if (player.getLodeStoneTile() == null) {
					player.getPackets()
							.sendGameMessage(
									"You need to teleport somewhere before you can use this option.");
					return;
				}
				player.getActionManager().setAction(
						new HomeTeleport(player.getLodeStoneTile()));
				break;
			}
			break;
		case 37: // mobi
			sendNormalTeleportSpell(player, 10, 19, new Tile(2413, 2848, 0),
					LAW_RUNE, 1, WATER_RUNE, 1, AIR_RUNE, 1);
			break;
		case 40: // varrock
			sendNormalTeleportSpell(player, 25, 19, new Tile(3212, 3424, 0),
					FIRE_RUNE, 1, AIR_RUNE, 3, LAW_RUNE, 1);
			break;
		case 43: // lumby
			sendNormalTeleportSpell(player, 31, 41, new Tile(3222, 3218, 0),
					EARTH_RUNE, 1, AIR_RUNE, 3, LAW_RUNE, 1);
			break;
		case 46: // fally
			sendNormalTeleportSpell(player, 37, 48, new Tile(2964, 3379, 0),
					WATER_RUNE, 1, AIR_RUNE, 3, LAW_RUNE, 1);
			break;
		case 51: // camelot
			sendNormalTeleportSpell(player, 45, 55.5, new Tile(2757, 3478, 0),
					AIR_RUNE, 5, LAW_RUNE, 1);
			break;
		case 57: // ardy
			sendNormalTeleportSpell(player, 51, 61, new Tile(2664, 3305, 0),
					WATER_RUNE, 2, LAW_RUNE, 2);
			break;
		case 62: // watch
			sendNormalTeleportSpell(player, 58, 68, new Tile(2547, 3113, 2),
					EARTH_RUNE, 2, LAW_RUNE, 2);
			break;
		case 69: // troll
			sendNormalTeleportSpell(player, 61, 68, new Tile(2888, 3674, 0),
					FIRE_RUNE, 2, LAW_RUNE, 2);
			break;
		case 72: // ape
			sendNormalTeleportSpell(player, 64, 76, new Tile(2776, 9103, 0),
					FIRE_RUNE, 2, WATER_RUNE, 2, LAW_RUNE, 2, 1963, 1);
			break;
		}
	}

	public static void pushLeverTeleport(final Player player, final Tile tile) {
		if (!player.getControllerManager().processObjectTeleport(tile))
			return;
		player.setNextAnimation(new Animation(2140));
		player.lock();
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.unlock();
				Magic.sendObjectTeleportSpell(player, false, tile);
			}
		}, 1);
	}

	public static void pushLeverTeleport(final Player player, final Tile tile,
			int emote, String startMessage, final String endMessage) {
		if (!player.getControllerManager().processObjectTeleport(tile))
			return;
		player.setNextAnimation(new Animation(emote));
		if (startMessage != null)
			player.getPackets().sendGameMessage(startMessage, true);
		player.lock();
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.unlock();
				Magic.sendObjectTeleportSpell(player, false, tile);
				if (endMessage != null)
					player.getPackets().sendGameMessage(endMessage, true);
			}
		}, 1);
	}

	public static final void sendAncientTeleportSpell(Player player, int level,
			double xp, Tile tile, int... runes) {
		sendTeleportSpell(player, 1979, -1, 1681, -1, level, xp, tile, 5, true,
				MAGIC_TELEPORT, runes);
	}

	public static final void sendDelayedObjectTeleportSpell(Player player,
			int delay, boolean randomize, Tile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, delay,
				randomize, OBJECT_TELEPORT);
	}

	public static final boolean sendItemTeleportSpell(Player player,
			boolean randomize, int upEmoteId, int upGraphicId, int delay,
			Tile tile) {
		return sendTeleportSpell(player, upEmoteId, -2, upGraphicId, -1, 0, 0,
				tile, delay, randomize, ITEM_TELEPORT);
	}

	public static final void sendNormalTeleportSpell(Player player, int level,
			double xp, Tile tile, int... runes) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3,
				true, MAGIC_TELEPORT, runes);
	}

	public static final void sendTeleotherTeleportSpell(Player player,
			int level, double xp, Tile tile, int... runes) {
		sendTeleportSpell(player, 1816, 8941, 342, -1, level, xp, tile, 3,
				true, MAGIC_TELEPORT, runes);
	}

	public static final void sendObjectTeleportSpell(Player player,
			boolean randomize, Tile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3,
				randomize, OBJECT_TELEPORT);
	}

	public static final boolean sendTeleportSpell(final Player player,
			int upEmoteId, final int downEmoteId, int upGraphicId,
			final int downGraphicId, int level, final double xp,
			final Tile tile, int delay, final boolean randomize,
			final int teleType, int... runes) {
		long currentTime = Utilities.currentTimeMillis();
		if (player.getLockDelay() > currentTime)
			return false;
		if (player.getX() >= 2956
				&& player.getX() <= 3067
				&& player.getY() >= 5512
				&& player.getY() <= 5630
				|| (player.getX() >= 2756 && player.getX() <= 2875
						&& player.getY() >= 5512 && player.getY() <= 5627)) {
			player.getPackets().sendGameMessage(
					"A magical force is blocking you from teleporting.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.MAGIC) < level) {
			player.getPackets().sendGameMessage(
					"Your Magic level is not high enough for this spell.");
			return false;
		}
		if (!checkRunes(player, false, runes))
			return false;
		if (teleType == MAGIC_TELEPORT) {
			if (!player.getControllerManager().processMagicTeleport(tile))
				return false;
		} else if (teleType == ITEM_TELEPORT) {
			if (!player.getControllerManager().processItemTeleport(tile))
				return false;
		} else if (teleType == OBJECT_TELEPORT) {
			if (!player.getControllerManager().processObjectTeleport(tile))
				return false;
		}
		checkRunes(player, true, runes);
		player.stopAll();
		if (upEmoteId != -1)
			player.setNextAnimation(new Animation(upEmoteId));
		if (upGraphicId != -1)
			player.setNextGraphics(new Graphics(upGraphicId));
		if (teleType == MAGIC_TELEPORT)
			player.getPackets().sendSound(5527, 0, 2);
		player.lock(3 + delay);
		EngineTaskManager.schedule(new EngineTask() {

			boolean removeDamage;

			@Override
			public void run() {
				if (!removeDamage) {
					Tile teleTile = tile;
					if (randomize) {
						// attemps to randomize tile by 4x4 area
						for (int trycount = 0; trycount < 10; trycount++) {
							teleTile = new Tile(tile, 2);
							if (Engine.canMoveNPC(tile.getZ(), teleTile.getX(),
									teleTile.getY(), player.getSize()))
								break;
							teleTile = tile;
						}
					}
					player.setNextTile(teleTile);
					player.getControllerManager().magicTeleported(teleType);
					if (player.getControllerManager().getController() == null)
						teleControlersCheck(player, teleTile);
					if (xp != 0)
						player.getSkills().addXp(Skills.MAGIC, xp);
					if (downEmoteId != -1)
						player.setNextAnimation(new Animation(
								downEmoteId == -2 ? -1 : downEmoteId));
					if (downGraphicId != -1)
						player.setNextGraphics(new Graphics(downGraphicId));
					if (teleType == MAGIC_TELEPORT) {
						player.getPackets().sendSound(5524, 0, 2);
						player.setNextFaceTile(new Tile(teleTile.getX(),
								teleTile.getY() - 1, teleTile.getZ()));
						player.setDirection(6);
					}
					removeDamage = true;
				} else {
					player.resetReceivedDamage();
					stop();
				}
			}
		}, delay, 0);
		return true;
	}

	public static final void setCombatSpell(Player player, int spellId) {
		if (player.getCombatDefinitions().getAutoCastSpell() == spellId)
			player.getCombatDefinitions().resetSpells(true);
		else
			checkCombatSpell(player, spellId, 0, false);
	}

	public static void teleControlersCheck(Player player, Tile teleTile) {
		if (player.getRegionId() == 11601)
			player.getControllerManager().startController("GodWars");
		else if (player.getRegionId() == 13626 || player.getRegionId() == 13625)
			player.getControllerManager().startController("DungeoneeringLobby");
		else if (Wilderness.isAtWild(teleTile))
			player.getControllerManager().startController("Wilderness");
		else if (RequestController.inWarRequest(player))
			player.getControllerManager().startController("clan_wars_request");
		else if (FfaZone.inArea(player))
			player.getControllerManager().startController("clan_wars_ffa");
	}

	private static void useHomeTele(Player player) {
		player.stopAll();
		player.getInterfaceManager().sendInterface(1092);
	}

	public static boolean useTabTeleport(final Player player, final int itemId) {
		if (itemId == 8013) {
			if (useTeleTab(player, GameConstants.RESPAWN_PLAYER_LOCATION))
				player.getInventory().deleteItem(itemId, 1);
			return true;
		}
		if (itemId < 8007 || itemId > 8007 + TABS.length - 1)
			return false;
		if (useTeleTab(player, TABS[itemId - 8007]))
			player.getInventory().deleteItem(itemId, 1);
		return true;
	}

	public static boolean useTeleTab(final Player player, final Tile tile) {
		if (!player.getControllerManager().processItemTeleport(tile))
			return false;
		player.lock();
		player.setNextAnimation(new Animation(9597));
		player.setNextGraphics(new Graphics(1680));
		EngineTaskManager.schedule(new EngineTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.setNextAnimation(new Animation(4731));
					stage = 1;
				} else if (stage == 1) {
					Tile teleTile = tile;
					// attemps to randomize tile by 4x4 area
					for (int trycount = 0; trycount < 10; trycount++) {
						teleTile = new Tile(tile, 2);
						if (Engine.canMoveNPC(tile.getZ(), teleTile.getX(),
								teleTile.getY(), player.getSize()))
							break;
						teleTile = tile;
					}
					player.setNextTile(teleTile);
					player.getControllerManager()
							.magicTeleported(ITEM_TELEPORT);
					if (player.getControllerManager().getController() == null)
						teleControlersCheck(player, teleTile);
					player.setNextFaceTile(new Tile(teleTile.getX(), teleTile
							.getY() - 1, teleTile.getZ()));
					player.setDirection(6);
					player.setNextAnimation(new Animation(-1));
					stage = 2;
				} else if (stage == 2) {
					player.resetReceivedDamage();
					player.unlock();
					stop();
				}

			}
		}, 2, 1);
		return true;
	}

	public static void vineTeleport(final Player player, final int x,
			final int y, final int plane) {
		player.lock(4);
		player.stopAll(true);
		player.setNextGraphics(new Graphics(1229));
		player.setNextAnimation(new Animation(7082));
		player.sendMessage("You feel at one with the spirit tree.");
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(7084));
				player.setNextTile(new Tile(x, y, plane));
				player.checkMovement(x, y, plane);
				player.stopAll(true);
				stop();
			}
		}, 3);
	}

	private Magic() {

	}
}