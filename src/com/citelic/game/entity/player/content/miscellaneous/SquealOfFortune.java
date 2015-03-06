package com.citelic.game.entity.player.content.miscellaneous;

import java.io.Serializable;

import com.citelic.GameConstants;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.entity.player.item.ItemsContainer;
import com.citelic.utility.Utilities;

public class SquealOfFortune implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4197173702556641051L;

	private Player player;

	private ItemsContainer<Item> items;

	public int INTERFACE_ID = 1253;

	public int TAB_INTERFACE_ID = 0;

	private int prizeId;

	/*
	 * public SquealOfFortune() { items = new ItemsContainer<Item>(13, false); }
	 */

	private int[] UN_COMMON = { 23722, 23718, 23726, 23815, 23730, 23726,
			23734, 24155 };
	private int[] RARE = { 23679, 23680, 23681, 23682, 23683, 23684, 23685,
			23686, 23687, 23688, 23689, 23690, 23691, 23692, 23695, 23697,
			23698, 23699, 23700 };
	private int[] COMMON = { 24154, 4587, 3105, 23814, 1079, 1127, 5698, 1215,
			1305, 9185, 1249 };
	private int[] SUPER_RARE = { 23723, 23816, 23731, 23735 };
	public boolean claimedReward;
	public boolean isSpinning;
	private int[] rares;

	private int[] common;
	private int[] uncommon;

	private int[] superRare;

	private boolean refreshRewards;

	private boolean claimLaterReward;

	public SquealOfFortune() {
		isSpinning = false;
	}

	public void claimBankReward(int priceok, int amount) {
		claimedReward = true;
		player.getBank().addItem(priceok, amount, true);
		player.getPackets().sendGameMessage(
				"Your Squeal of Fortune reward has been banked.");
	}

	public void claimReward(int priceok, int amount) {
		int spaceLeft = player.getInventory().getFreeSlots();
		if (spaceLeft == 0) {
			claimBankReward(priceok, amount);
		} else if (spaceLeft > 0) {
			claimedReward = true;
			player.getInventory().addItem(priceok, amount);
		}
		if (player.getSpins() > 0) {
			prizeId = 0;
			items.clear();
		} else if (player.getSpins() <= 0) {
			setRefreshRewards(true);
		}
		setClaimLaterReward(false);
	}

	public int common() {
		return COMMON[(int) (Math.random() * COMMON.length)];
	}

	public boolean getClaimLaterReward() {
		return claimLaterReward;
	}

	public boolean getRefreshRewards() {
		return refreshRewards;
	}

	/**
	 * Handles all the squeal of fortune buttons.
	 * 
	 * @param player
	 * @param componentId
	 */
	public void handleButtons(int componentId, int interfaceId) {
		if (interfaceId == 1252) {
			if (componentId == 5) {
				claimedReward = false;
				isSpinning = false;
				openSquealInterface();
			} else if (componentId == 7) {
				player.getPackets().closeInterface(
						player.getInterfaceManager().hasRezizableScreen() ? 11
								: 0);
				player.getPackets()
						.sendGameMessage(
								"You can access the Squeal of Fortune from the side panel, and you can show the button again by logging out and back in.");
			}
		} else if (interfaceId == 1253) {
			if (componentId == 23) {
				if (!isSpinning) {
					startSpinning(componentId);
				}
			} else if (componentId == 106) {
				if (isSpinning && !claimedReward) {
					setClaimLaterReward(true);
					player.getPackets()
							.sendWindowsPane(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 746 : 548,
									0);
					return;
				}
				resetSquealInterface();
			} else if (componentId == 273) {
				if (player.getSpins() > 0) {
					claimedReward = false;
					isSpinning = false;
					openSquealInterface();
				}
			} else if (componentId == 192) {
				if (!claimedReward && isSpinning) {
					if (items.get(prizeId) != null) {
						player.getPackets().sendConfigByFile(10861,
								player.getSpins() > 0 ? 0 : 2);
						player.getPackets().sendGlobalConfig(1790, 0);
						player.getPackets()
								.sendHideIComponent(1253, 240, false);
						player.getPackets()
								.sendHideIComponent(1253, 178, false);
						player.getPackets()
								.sendHideIComponent(1253, 225, false);
						player.getPackets().sendRunScript(5906);
						claimReward(items.get(prizeId).getId(), 0);
					}
				}
			} else if (componentId == 117 || componentId == 6) {
				if (!claimedReward)
					setClaimLaterReward(true);
				player.getPackets().sendWindowsPane(
						player.getInterfaceManager().hasRezizableScreen() ? 746
								: 548, 0);
			} else if (componentId == 239) {
				if (!claimedReward) {
					setClaimLaterReward(false);
					prizeId = 0;
					items.clear();
					player.getPackets()
							.sendWindowsPane(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 746 : 548,
									0);
				}
			}
		}
	}

	public void openClaimLaterInterface() {
		isSpinning = true;
		claimedReward = false;
		player.getPackets().sendConfigByFile(10861, 1);
		player.getPackets().sendGlobalConfig(1781, prizeId);
		player.getPackets().sendConfigByFile(10860, prizeId);
		player.getPackets().sendItems(665, false, items);
		player.getPackets().sendGlobalConfig(1790, 1);
		player.getPackets().sendWindowsPane(INTERFACE_ID, 0);
		player.getPackets().sendHideIComponent(1253, 240, false);
		player.getPackets().sendHideIComponent(1253, 178, false);
		player.getPackets().sendHideIComponent(1253, 225, false);
		player.getPackets().sendRunScript(5905);
	}

	@SuppressWarnings("unused")
	private void openLastRewardInterface() {
		isSpinning = false;
		claimedReward = true;
		if (items != null) {
			player.getPackets().sendConfigByFile(10861, 2);
			player.getPackets().sendGlobalConfig(1781, prizeId);
			player.getPackets().sendConfigByFile(10860, prizeId);
			player.getPackets().sendItems(665, false, items);
			player.getPackets().sendGlobalConfig(1790, 0);
			player.getPackets().sendWindowsPane(INTERFACE_ID, 0);
			player.getPackets().sendHideIComponent(1253, 240, false);
			player.getPackets().sendHideIComponent(1253, 178, false);
			player.getPackets().sendHideIComponent(1253, 225, false);
			player.getPackets().sendRunScript(5905);
		} else {
			items = new ItemsContainer<Item>(13, false);
			sendInterfaceConfigs();
		}

	}

	public void openSquealInterface() {
		if (!player.canSpawn()) {
			player.sendMessage("You can't use the Squeal Of Fortune at this location.");
			return;
		}
		if (!GameConstants.IS_SQUEAL_OF_FORTUNE_ENABLED) {
			return;
		}
		if (getClaimLaterReward()) {
			// openClaimLaterInterface();
			// return;
		}
		if (player.getSpins() <= 0) {
			// openLastRewardInterface();
			// return;
		}
		if (items == null || getRefreshRewards())
			items = new ItemsContainer<Item>(13, false);
		sendInterfaceConfigs();
	}

	public int rare() {
		return RARE[(int) (Math.random() * RARE.length)];
	}

	public void refreshSqueal() {
		player.getPackets().sendConfigByFile(11026, player.getSpins() + 1);
	}

	public void resetSquealInterface() {
		isSpinning = false;
		claimedReward = false;
		player.getPackets().sendConfigByFile(10861, 0);
		player.getPackets().sendGlobalConfig(1790, 0);
		player.getPackets().sendHideIComponent(1253, 240, false);
		player.getPackets().sendHideIComponent(1253, 178, false);
		player.getPackets().sendHideIComponent(1253, 225, false);
		player.getPackets().sendRunScript(5906);
		player.getPackets().sendWindowsPane(
				player.getInterfaceManager().hasRezizableScreen() ? 746 : 548,
				0);
	}

	public void sendInterfaceConfigs() {
		int spinsLeft = player.getSpins();
		setRefreshRewards(false);
		resetSquealInterface();
		player.getPackets().sendConfigByFile(11026, spinsLeft + 1);
		player.getPackets().sendConfigByFile(11155, 3);
		player.getPackets().sendConfigByFile(10861, 0);
		items.add(new Item(rare())); // Rare
		items.add(new Item(common())); // Common
		items.add(new Item(superRare())); // Super-Rare
		items.add(new Item(common())); // Common
		items.add(new Item(rare())); // Rare
		items.add(new Item(common())); // Common
		items.add(new Item(uncommon())); // UnCommon
		items.add(new Item(common())); // Common
		items.add(new Item(rare())); // Rare
		items.add(new Item(uncommon())); // UnCommon
		items.add(new Item(common())); // Com
		items.add(new Item(uncommon())); // UnCom
		items.add(new Item(common())); // Common
		player.getPackets().sendWindowsPane(1253, 0);
		// player.getPackets().sendInterface(false, 1252, 1, INTERFACE_ID); //
		// seems
		sendInterItems();
	}

	/**
	 * Sends the Items in the reward container.
	 */
	public void sendInterItems() {
		player.getPackets().sendItems(665, false, items);
		int random = Utilities.random(15000);
		if (prizeId == 0) {
			if (random < 50) {
				superRare = new int[] { 0 };
				prizeId = superRare[(int) (Math.random() * superRare.length)];
			} else if (random < 200) {
				rares = new int[] { 0, 3, 8 };
				prizeId = rares[(int) (Math.random() * rares.length)];
			} else if (random < 5000) {
				uncommon = new int[] { 2, 6, 9, 11 };
				prizeId = uncommon[(int) (Math.random() * uncommon.length)];
			} else {
				common = new int[] { 1, 3, 5, 7, 10, 12 };
				prizeId = common[(int) (Math.random() * common.length)];
			}
		}
	}

	private void setClaimLaterReward(boolean b) {
		claimLaterReward = b;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	private void setRefreshRewards(boolean i) {
		refreshRewards = i;
	}

	public void startSpinning(int componentId) {
		if (componentId == 23 && player.getSpins() > 0) {
			claimedReward = false;
			isSpinning = true;
			player.setSpins(player.getSpins() - 1);
			player.getPackets().sendConfigByFile(10861, 1);
			player.getPackets().sendGlobalConfig(1781, 0);
			player.getPackets().sendConfigByFile(10860, prizeId);
			player.getPackets().sendGlobalConfig(1790, 1);
			refreshSqueal();
		} else if (player.getSpins() <= 0) {
			player.getPackets().sendGameMessage(
					"You don't have any Squeal of Fortune Spins.");
		}
	}

	public int superRare() {
		return SUPER_RARE[(int) (Math.random() * SUPER_RARE.length)];
	}

	public int uncommon() {
		return UN_COMMON[(int) (Math.random() * UN_COMMON.length)];
	}

	public void sendTab() {
		player.getInterfaceManager().sendTab(
				player.getInterfaceManager().hasRezizableScreen() ? 162 : 187,
				1139);
		player.getPackets().sendGlobalConfig(823, 1); // enables tab
		player.getSquealOfFortune().refreshSqueal();
	}
}