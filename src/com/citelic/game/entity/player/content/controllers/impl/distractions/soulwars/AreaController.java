package com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.distractions.soulwars.SoulWarsManager.PlayerType;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.networking.codec.decode.WorldPacketsDecoder;
import com.citelic.utility.Utilities;

public class AreaController extends Controller {

	/**
	 * The experience modifier.
	 */
	private static final int XP_MODIFIER = 2;

	/**
	 * The skills array.
	 */
	private final static int[] SKILLS = { Skills.SLAYER, Skills.HITPOINTS,
			Skills.DEFENCE, Skills.STRENGTH, Skills.RANGE, Skills.ATTACK,
			Skills.PRAYER, Skills.MAGIC };

	/**
	 * The array holding the gamble rewards.
	 */
	private final static Item[][] GAMBLE_REWARDS = { { // COMMON
			new Item(995, 200000 + Utilities.getRandom(350000)),
					new Item(18831, 50 + Utilities.getRandom(25)),
					new Item(15259, 1),
					new Item(527, 150 + Utilities.getRandom(75)),
					new Item(990, 1 + Utilities.getRandom(2)),
					new Item(6730, 20 + Utilities.getRandom(13)), }, { // UNCOMMON

			new Item(21465), new Item(21466), new Item(21470), new Item(21471),
					new Item(21475), new Item(21476), }, { // RARE
															// vanguard
					new Item(21473), new Item(21474), new Item(21472),
					// trickster
					new Item(21467), new Item(21468), new Item(21469),
					// battlemage
					new Item(21462), new Item(21463), new Item(21464),

			} };

	private final int[] CHARMS = { 12158, 12159, 12160, 12163 };

	@Override
	public void start() {
		player.getMusicsManager().playMusic(597);
		((AreaTask) Engine.soulWars.getTasks().get(PlayerType.OUTSIDE_LOBBY))
				.getPlayers().add(player);
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendTab(
				player.getInterfaceManager().hasRezizableScreen() ? 34 : 10,
				199);
	}

	@Override
	public void forceClose() {
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 34 : 10);
		((AreaTask) Engine.soulWars.getTasks().get(PlayerType.OUTSIDE_LOBBY))
				.getPlayers().remove(player);
	}

	@Override
	public void magicTeleported(int type) {
		forceClose();
		removeController();
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 42220:
			player.getControllerManager().forceStop();
			player.useStairs(-1, new Tile(3082, 3475, 0), 0, 1);
			return false;
		case 42029:
		case 42030:
		case 42031:
			Engine.soulWars.passBarrier(PlayerType.OUTSIDE_LOBBY, player,
					object);
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 8526) {
			sendShop();
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick2(NPC npc) {
		if (npc.getId() == 8526) {
			sendShop();
			return false;
		}
		return true;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (interfaceId == 276) {
			switch (componentId) {
			case 8:
				switch (packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.getPackets()
							.sendGameMessage(
									"For every 2 zeals you exchange, you get a random reward!");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(2, false)) {
						if (!player.getInventory().hasFreeSlots()) {
							player.getPackets()
									.sendGameMessage(
											"You don't have any space to obain your special reward!");
							return false;
						}
						checkZeals(2, true);
						final int foundation = getRandomRewardIndex();
						final Item reward = GAMBLE_REWARDS[foundation][Utilities
								.random(GAMBLE_REWARDS[foundation].length)];
						player.getInventory().addItem(reward);
					}
					break;
				}
				break;
			case 24:
			case 25:
			case 26:
			case 27:
				switch (packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.getPackets()
							.sendGameMessage(
									"For every 2 zeals you exchange, you receive a random amount of charms.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(2, false)
							&& player.getInventory().addItem(
									CHARMS[componentId - 24],
									1 + Utilities.getRandom(4)))
						checkZeals(2, true);
					break;
				}
				break;
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
				switch (packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.getPackets()
							.sendGameMessage(
									"For each zeal you exchange, you receive "
											+ calculateSkillExperience(
													componentId, 1)
											+ " experience in "
											+ Skills.SKILL_NAME[SKILLS[componentId - 32]]
													.toLowerCase() + ".");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					exchangeZealsForXp(componentId, 1);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
					exchangeZealsForXp(componentId, 10);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
					exchangeZealsForXp(componentId, 100);
					break;
				}
				break;
			}
		}
		return true;
	}

	private int getRandomRewardIndex() {
		final double wheel = Math.random() * 1000D;
		if (wheel < 1D) // 450 / 1000 = 4,5 * 10 == 45%
			return 3; // super rare
		else if (wheel < 150D) // 100 / 1000 = 0,1 * 10 == 10%
			return 2; // rare
		else if (wheel < 450D) // 10 / 1000 = 0,01 * 100 = 1%
			return 1; // uncommon
		return 0;
	}

	/**
	 * Exchanges zeals for xp.
	 * 
	 * @param componentId
	 *            The component id.
	 * @param zeals
	 *            The zeals.
	 */
	private void exchangeZealsForXp(int componentId, final int zeals) {
		if (checkZeals(zeals, false)) {
			final int xp = calculateSkillExperience(componentId,
					zeals == 10 ? 11 : zeals == 100 ? 110 : zeals);
			checkZeals(zeals, true);
			player.getPackets().sendConfigByFile(5827, player.getZeals());
			player.getTemporaryAttributtes().put("soul_wars_shop_xp",
					Boolean.TRUE);
			player.getSkills().addXp(SKILLS[componentId - 32], xp);
			player.getPackets().sendGameMessage(
					"You have received "
							+ xp
							+ " experience in "
							+ Skills.SKILL_NAME[SKILLS[componentId - 32]]
									.toLowerCase() + "!");
		}
	}

	/**
	 * Calculates skill experience.
	 * 
	 * @param componentId
	 *            The component id.
	 * @param zeals
	 *            The zeal amount.
	 * @return The skill experience.
	 */
	private int calculateSkillExperience(int componentId, int zeals) {
		final int skill = SKILLS[componentId - 32];
		final int playerLevel = player.getSkills().getLevelForXp(skill);
		double base = 0;
		switch (skill) {
		case Skills.HITPOINTS:
		case Skills.ATTACK:
		case Skills.STRENGTH:
		case Skills.DEFENCE:
			base = Math.floor(Math.pow(playerLevel, 2) / 600) * 525;
			break;
		case Skills.RANGE:
		case Skills.MAGIC:
			base = Math.floor(Math.pow(playerLevel, 2) / 600) * 480;
			break;
		case Skills.PRAYER:
			base = Math.floor(Math.pow(playerLevel, 2) / 600) * 270;
			break;
		default:
			if (playerLevel <= 30)
				base = Math.floor(Math.pow(1.1048, playerLevel) * 6.788);
			else
				base = (Math.floor(Math.pow(playerLevel, 2) / 349) + 1) * 45;
		}
		return (int) base * zeals * XP_MODIFIER;
	}

	/**
	 * Check if the player has the specific amount of zeals.
	 * 
	 * @param zeals
	 *            The zeals.
	 * @param remove
	 *            If the zeals should be removed after the check.
	 * @return {@code true} If the player has the amount of zeals.
	 */
	private boolean checkZeals(int zeals, boolean remove) {
		if (player.getZeals() < zeals) {
			player.getPackets().sendGameMessage(
					"You don't have "
							+ (player.getZeals() == 0 ? "any" : "enough")
							+ " zeals to spend!");
			return false;
		} else if (remove) {
			player.increaseZeals(-zeals);
			player.getPackets().sendConfigByFile(5827, player.getZeals());
		}
		return true;
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	/**
	 * Send the soul wars shop.
	 */
	public void sendShop() {
		player.getInterfaceManager().sendInterface(276);
		player.getPackets().sendConfigByFile(5827, player.getZeals());
	}
}