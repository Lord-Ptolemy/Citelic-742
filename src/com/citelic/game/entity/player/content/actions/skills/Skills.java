package com.citelic.game.entity.player.content.actions.skills;

import java.io.Serializable;

import com.citelic.GameConstants;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;

public final class Skills implements Serializable {

	private static final long serialVersionUID = -7086829989489745985L;

	public static final double MAXIMUM_EXP = 200000000;
	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2,
			HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6, COOKING = 7,
			WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11,
			CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15,
			AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19,
			RUNECRAFTING = 20, CONSTRUCTION = 22, HUNTER = 21, SUMMONING = 23,
			DUNGEONEERING = 24;

	public static final String[] SKILL_NAME = { "Attack", "Defence",
			"Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking",
			"Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting",
			"Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer",
			"Farming", "Runecrafting", "Hunter", "Construction", "Summoning",
			"Dungeoneering" };

	public static int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public short level[];
	private double xp[];
	private double[] xpTracks;
	private boolean[] trackSkills;
	private byte[] trackSkillsIds;

	private boolean xpDisplay, xpPopup;
	private transient int currentCounter;

	private transient Player player;

	public Skills() {
		level = new short[25];
		xp = new double[25];
		for (int i = 0; i < level.length; i++) {
			level[i] = 1;
			xp[i] = 0;
		}
		level[3] = 10;
		xp[3] = 1184;
		level[HERBLORE] = 3;
		xp[HERBLORE] = 250;
		xpPopup = true;
		xpTracks = new double[3];
		trackSkills = new boolean[3];
		trackSkillsIds = new byte[3];
		trackSkills[0] = true;
		for (int i = 0; i < trackSkillsIds.length; i++)
			trackSkillsIds[i] = 30;
	}

	public void addSkillXpRefresh(int skill, double xp) {
		this.xp[skill] += xp;
		level[skill] = (short) getLevelForXp(skill);
	}

	private double getXPRates(int skill, double exp) {
		// Combat Skills
		if (skill == ATTACK || skill == RANGE || skill == HITPOINTS
				|| skill == DUNGEONEERING || skill == MAGIC || skill == RANGE
				|| skill == DEFENCE || skill == STRENGTH)
			return Wilderness.isAtWild(player) ? 1.0
					: GameConstants.COMBAT_XP_RATE;
		// Skilling Skills (Special rates)
		if (skill == FISHING)
			return GameConstants.FISHING_XP_RATE;
		if (skill == CRAFTING)
			return GameConstants.CRAFTING_XP_RATE;
		if (skill == SUMMONING)
			return GameConstants.SUMMONING_XP_RATE;
		if (skill == RUNECRAFTING)
			return GameConstants.RUNECRAFTING_XP_RATE;
		return GameConstants.SKILLING_XP_RATE;
	}

	public void addXp(int skill, double exp) {
		if (player.isXpLocked())
			return;
		player.getControllerManager().trackXP(skill, (int) exp);
		exp *= getXPRates(skill, exp);
		if (GameConstants.DOUBLE_EXPERIENCE)
			exp *= 2.0;
		if (player.getAuraManager().usingWisdom())
			exp *= 1.025;
		if (skill == Skills.SLAYER)
			exp /= 3;
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29 && (skill == Skills.ATTACK
								|| skill == Skills.DEFENCE
								|| skill == Skills.STRENGTH
								|| skill == Skills.MAGIC
								|| skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}
		if (xp[skill] > MAXIMUM_EXP) {
			xp[skill] = MAXIMUM_EXP;
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getPlayerAppearance().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
			player.getQuestManager().checkCompleted();
		}
		refresh(skill);
	}

	public int drainLevel(int skill, int drain) {
		int drainLeft = drain - level[skill];
		if (drainLeft < 0) {
			drainLeft = 0;
		}
		level[skill] -= drain;
		if (level[skill] < 0) {
			level[skill] = 0;
		}
		refresh(skill);
		return drainLeft;
	}

	public void drainSummoning(int amt) {
		int level = getLevel(Skills.SUMMONING);
		if (level == 0)
			return;
		set(Skills.SUMMONING, amt > level ? 0 : level - amt);
	}

	public int getCombatLevel() {
        int attack = getLevelForXp(Skills.ATTACK);
        int defence = getLevelForXp(Skills.DEFENCE);
        int strength = getLevelForXp(Skills.STRENGTH);
        int hp = getLevelForXp(Skills.HITPOINTS);
        int prayer = getLevelForXp(Skills.PRAYER);
        int ranged = getLevelForXp(Skills.RANGE);
        int magic = getLevelForXp(Skills.MAGIC);
        int combatLevel = (int) Math.floor(0.25
                        * (defence + hp + Math.floor(prayer / 2))
                        + Math.max(0.325 * (attack + strength), Math.max(
                                        0.325 * (Math.floor(ranged / 2) + ranged),
                                        0.325 * (Math.floor(magic / 2) + magic))));
        return combatLevel;
	}

	public int getCombatLevelWithSummoning() {
		return getCombatLevel() + getSummoningCombatLevel();
	}

	public int getCounterSkill(int skill) {
		switch (skill) {
		case ATTACK:
			return 0;
		case STRENGTH:
			return 1;
		case DEFENCE:
			return 4;
		case RANGE:
			return 2;
		case HITPOINTS:
			return 5;
		case PRAYER:
			return 6;
		case AGILITY:
			return 7;
		case HERBLORE:
			return 8;
		case THIEVING:
			return 9;
		case CRAFTING:
			return 10;
		case MINING:
			return 12;
		case SMITHING:
			return 13;
		case FISHING:
			return 14;
		case COOKING:
			return 15;
		case FIREMAKING:
			return 16;
		case WOODCUTTING:
			return 17;
		case SLAYER:
			return 19;
		case FARMING:
			return 20;
		case CONSTRUCTION:
			return 21;
		case HUNTER:
			return 22;
		case SUMMONING:
			return 23;
		case DUNGEONEERING:
			return 24;
		case MAGIC:
			return 3;
		case FLETCHING:
			return 18;
		case RUNECRAFTING:
			return 11;
		default:
			return -1;
		}

	}

	public int getLevel(int skill) {
		return level[skill];
	}

	public int getLevelForXp(int skill) {
		double exp = xp[skill];
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= (skill == DUNGEONEERING ? 120 : 99); lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp) {
				return lvl;
			}
		}
		return skill == DUNGEONEERING ? 120 : 99;
	}

	public short[] getLevels() {
		return level;
	}

	public String getSkillName(int skill) {
		switch (skill) {
		case ATTACK:
			return "Attack";
		case STRENGTH:
			return "Strength";
		case DEFENCE:
			return "Defence";
		case RANGE:
			return "Ranged";
		case HITPOINTS:
			return "Hitpoints";
		case PRAYER:
			return "Prayer";
		case AGILITY:
			return "Agility";
		case HERBLORE:
			return "Herblore";
		case THIEVING:
			return "Thieving";
		case CRAFTING:
			return "Crafting";
		case MINING:
			return "Mining";
		case SMITHING:
			return "Smithing";
		case FISHING:
			return "Fishing";
		case COOKING:
			return "Cooking";
		case FIREMAKING:
			return "Firemaking";
		case WOODCUTTING:
			return "Woodcutting";
		case SLAYER:
			return "Slayer";
		case FARMING:
			return "Farming";
		case CONSTRUCTION:
			return "Construction";
		case HUNTER:
			return "Hunter";
		case SUMMONING:
			return "Summoning";
		case DUNGEONEERING:
			return "Dungeoneering";
		case MAGIC:
			return "Magic";
		case FLETCHING:
			return "Fletching";
		case RUNECRAFTING:
			return "Runecrafting";
		default:
			return "Null";
		}
	}

	public int getSummoningCombatLevel() {
		return getLevelForXp(Skills.SUMMONING) / 8;
	}

	public int getTotalLevel(Player player) {
		int totallevel = 0;
		for (int i = 0; i <= 24; i++) {
			totallevel += player.getSkills().getLevelForXp(i);
		}
		return totallevel;
	}

	public double[] getXp() {
		return xp;
	}

	public double getXp(int skill) {
		return xp[skill];
	}

	public void handleSetupXPCounter(int componentId) {
		if (componentId == 18)
			player.closeInterfaces();
		else if (componentId >= 22 && componentId <= 24)
			setCurrentCounter(componentId - 22);
		else if (componentId == 27)
			switchTrackCounter();
		else if (componentId == 61)
			resetCounterXP();
		else if (componentId >= 31 && componentId <= 57)
			if (componentId == 33)
				setCounterSkill(4);
			else if (componentId == 34)
				setCounterSkill(2);
			else if (componentId == 35)
				setCounterSkill(3);
			else if (componentId == 42)
				setCounterSkill(18);
			else if (componentId == 49)
				setCounterSkill(11);
			else
				setCounterSkill(componentId >= 56 ? componentId - 27
						: componentId - 31);

	}

	public boolean hasRequiriments(int... skills) {
		for (int i = 0; i < skills.length; i += 2) {
			int skillId = skills[i];
			int skillLevel = skills[i + 1];
			if (getLevelForXp(skillId) < skillLevel)
				return false;

		}
		return true;
	}

	public void init() {
		for (int skill = 0; skill < level.length; skill++)
			refresh(skill);
		sendXPDisplay();
	}

	public void passLevels(Player p) {
		this.level = p.getSkills().level;
		this.xp = p.getSkills().xp;
	}

	public void refresh(int skill) {
		player.getPackets().sendSkillLevel(skill);
		player.getPlayerAppearance().generateAppearenceData();
	}

	public void refreshCounterXp(int counter) {
		player.getPackets().sendConfig(counter == 0 ? 1801 : 2474 + counter,
				(int) (xpTracks[counter] * 10));
	}

	public void refreshCurrentCounter() {
		player.getPackets().sendConfig(2478, currentCounter + 1);
	}

	public void resetCounterXP() {
		xpTracks[currentCounter] = 0;
		refreshCounterXp(currentCounter);
	}

	public void resetSkillNoRefresh(int skill) {
		xp[skill] = 0;
		level[skill] = 1;
	}

	public void restoreSkills() {
		for (int skill = 0; skill < level.length; skill++) {
			level[skill] = (short) getLevelForXp(skill);
			refresh(skill);
		}
	}

	public void restoreSummoning() {
		level[23] = (short) getLevelForXp(23);
		refresh(23);
	}

	public void sendInterfaces() {
		if (xpDisplay)
			player.getInterfaceManager().sendXPDisplay();
		if (xpPopup)
			player.getInterfaceManager().sendXPPopup();
	}

	public void sendXPDisplay() {
		for (int i = 0; i < trackSkills.length; i++) {
			player.getPackets().sendConfigByFile(10444 + i,
					trackSkills[i] ? 1 : 0);
			player.getPackets().sendConfigByFile(10440 + i,
					trackSkillsIds[i] + 1);
			refreshCounterXp(i);
		}
	}

	public void set(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		refresh(skill);
	}

	public void setCounterSkill(int skill) {
		xpTracks[currentCounter] = 0;
		trackSkillsIds[currentCounter] = (byte) skill;
		player.getPackets().sendConfigByFile(10440 + currentCounter,
				trackSkillsIds[currentCounter] + 1);
		refreshCounterXp(currentCounter);
	}

	public void setCurrentCounter(int counter) {
		if (counter != currentCounter) {
			currentCounter = counter;
			refreshCurrentCounter();
		}
	}

	/*
	 * if(componentId == 33) setCounterSkill(4); else if(componentId == 34)
	 * setCounterSkill(2); else if(componentId == 35) setCounterSkill(3); else
	 * if(componentId == 42) setCounterSkill(18); else if(componentId == 49)
	 * setCounterSkill(11);
	 */

	public void setPlayer(Player player) {
		this.player = player;
		// temporary
		if (xpTracks == null) {
			xpPopup = true;
			xpTracks = new double[3];
			trackSkills = new boolean[3];
			trackSkillsIds = new byte[3];
			trackSkills[0] = true;
			for (int i = 0; i < trackSkillsIds.length; i++)
				trackSkillsIds[i] = 30;
		}
	}

	public void setupXPCounter() {
		player.getInterfaceManager().sendXPDisplay(1214);
	}

	public void setXp(int skill, double exp) {
		xp[skill] = exp;
		refresh(skill);
	}

	public void summonXP(int skill, double exp) {
		player.getControllerManager().trackXP(skill, (int) exp);
		if (player.isXpLocked())
			return;
		exp *= 1.0;
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29 && (skill == Skills.ATTACK
								|| skill == Skills.DEFENCE
								|| skill == Skills.STRENGTH
								|| skill == Skills.MAGIC
								|| skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}

		if (xp[skill] > MAXIMUM_EXP) {
			xp[skill] = MAXIMUM_EXP;
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getPlayerAppearance().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
			player.getQuestManager().checkCompleted();
		}
		refresh(skill);
	}

	public void switchTrackCounter() {
		trackSkills[currentCounter] = !trackSkills[currentCounter];
		player.getPackets().sendConfigByFile(10444 + currentCounter,
				trackSkills[currentCounter] ? 1 : 0);
	}

	public void switchXPDisplay() {
		xpDisplay = !xpDisplay;
		if (xpDisplay)
			player.getInterfaceManager().sendXPDisplay();
		else
			player.getInterfaceManager().closeXPDisplay();
	}

	public void switchXPPopup() {
		xpPopup = !xpPopup;
		player.getPackets().sendGameMessage(
				"XP pop-ups are now " + (xpPopup ? "en" : "dis") + "abled.");
		if (xpPopup)
			player.getInterfaceManager().sendXPPopup();
		else
			player.getInterfaceManager().closeXPPopup();
	}

	public double addXpLamp(int skill, double exp) {
		player.getControllerManager().trackXP(skill, (int) exp);
		if (player.isXpLocked())
			return 0;
		exp *= 110;
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| trackSkillsIds[i] == 29
						&& (skill == Skills.ATTACK || skill == Skills.DEFENCE
								|| skill == Skills.STRENGTH
								|| skill == Skills.MAGIC
								|| skill == Skills.RANGE || skill == Skills.HITPOINTS)
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}

		if (xp[skill] > Skills.MAXIMUM_EXP) {
			xp[skill] = Skills.MAXIMUM_EXP;
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == Skills.SUMMONING || skill >= Skills.ATTACK
					&& skill <= Skills.MAGIC) {
				player.getPlayerAppearance().generateAppearenceData();
				if (skill == Skills.HITPOINTS) {
					player.heal(levelDiff * 10);
				} else if (skill == Skills.PRAYER) {
					player.getPrayer().restorePrayer(levelDiff * 10);
				}
			}
			player.getQuestManager().checkCompleted();
		}
		refresh(skill);
		return exp;
	}

	public long getTotalXp(Player player) {
		long totalxp = 0;
		for (double xp : player.getSkills().getXp()) {
			totalxp += xp;
		}
		return totalxp;
	}
}