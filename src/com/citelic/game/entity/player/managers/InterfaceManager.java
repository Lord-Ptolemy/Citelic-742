package com.citelic.game.entity.player.managers;

import java.util.concurrent.ConcurrentHashMap;

import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.containers.Inventory;

public class InterfaceManager {

	public static final int FIXED_WINDOW_ID = 548;
	public static final int RESIZABLE_WINDOW_ID = 746;
	public static final int CHAT_BOX_TAB = 13;
	public static final int FIXED_SCREEN_TAB_ID = 47;
	public static final int RESIZABLE_SCREEN_TAB_ID = 70;
	public static final int FIXED_INV_TAB_ID = 175;
	public static final int RESIZABLE_INV_TAB_ID = 151;
	private Player player;

	private final ConcurrentHashMap<Integer, int[]> openedinterfaces = new ConcurrentHashMap<Integer, int[]>();

	private boolean resizableScreen;
	private int windowsPane;

	public InterfaceManager(Player player) {
		this.player = player;
	}

	public boolean addInterface(int windowId, int tabId, int childId) {
		if (openedinterfaces.containsKey(tabId))
			player.getPackets().closeInterface(tabId);
		openedinterfaces.put(tabId, new int[] { childId, windowId });
		return openedinterfaces.get(tabId)[0] == childId;
	}

	public void closeChatBoxInterface() {
		player.getPackets().closeInterface(CHAT_BOX_TAB);
	}

	public void closeCombatStyles() {
		player.getPackets().closeInterface(resizableScreen ? 111 : 204);
	}

	public void closeEmotes() {
		player.getPackets().closeInterface(resizableScreen ? 124 : 217);
	}

	public void closeEquipment() {
		player.getPackets().closeInterface(resizableScreen ? 116 : 176);
	}

	public void closeFadingInterface() {
		if (hasRezizableScreen())
			player.getPackets().closeInterface(12);
		else
			player.getPackets().closeInterface(11);
	}

	public void closeInterface(int one, int two) {
		player.getPackets().closeInterface(resizableScreen ? two : one);
	}

	public void closeInventory() {
		player.getPackets().closeInterface(resizableScreen ? 115 : 175);
	}

	public void closeInventoryInterface() {
		player.getPackets().closeInterface(
				resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID);
	}

	public void closeMagicBook() {
		player.getPackets().closeInterface(resizableScreen ? 118 : 186);
	}

	public void closeOverlay(boolean fullScreen) {
		player.getPackets().closeInterface(fullScreen ? 69 : 49);
	}

	public void closePrayerBook() {
		player.getPackets().closeInterface(resizableScreen ? 117 : 210);
	}

	public void closeQuests() {
		player.getPackets().closeInterface(resizableScreen ? 114 : 190);
	}

	public void closeReplacedRealChatBoxInterface() {
		player.getPackets().closeInterface(752, 11);
	}

	public void closeScreenInterface() {
		player.getPackets()
				.closeInterface(
						resizableScreen ? RESIZABLE_SCREEN_TAB_ID
								: FIXED_SCREEN_TAB_ID);
	}

	public void closeSkills() {
		player.getPackets().closeInterface(resizableScreen ? 113 : 206);
	}

	public void closeTaskSystem() {
		player.getPackets().closeInterface(resizableScreen ? 112 : 205);
	}

	public void closeXPDisplay() {
		player.getPackets().closeInterface(resizableScreen ? 27 : 29);
	}

	public void closeXPPopup() {
		player.getPackets().closeInterface(resizableScreen ? 38 : 10);
	}

	public boolean containsChatBoxInter() {
		return containsTab(CHAT_BOX_TAB);
	}

	public boolean containsInterface(int childId) {
		if (childId == windowsPane)
			return true;
		for (int[] value : openedinterfaces.values())
			if (value[0] == childId)
				return true;
		return false;
	}

	public boolean containsInterface(int tabId, int childId) {
		if (childId == windowsPane)
			return true;
		if (!openedinterfaces.containsKey(tabId))
			return false;
		return openedinterfaces.get(tabId)[0] == childId;
	}

	public boolean containsInventoryInter() {
		return containsTab(resizableScreen ? RESIZABLE_INV_TAB_ID
				: FIXED_INV_TAB_ID);
	}

	public boolean containsScreenInter() {
		return containsTab(resizableScreen ? RESIZABLE_SCREEN_TAB_ID
				: FIXED_SCREEN_TAB_ID);
	}

	public boolean containsTab(int tabId) {
		return openedinterfaces.containsKey(tabId);
	}

	public void gazeOrbOfOculus() {
		player.getPackets().sendWindowsPane(475, 0);
		player.getPackets().sendInterface(true, 475, 57, 751);
		player.getPackets().sendInterface(true, 475, 55, 752);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getPackets().sendWindowsPane(
						player.getInterfaceManager().hasRezizableScreen() ? 746
								: 548, 0);
				player.getPackets().sendResetCamera();
			}

		});
	}

	public int getTabWindow(int tabId) {
		if (!openedinterfaces.containsKey(tabId))
			return FIXED_WINDOW_ID;
		return openedinterfaces.get(tabId)[1];
	}

	public int getWindowsPane() {
		return windowsPane;
	}

	public boolean hasRezizableScreen() {
		return resizableScreen;
	}

	/*
	 * returns lastGameTab
	 */
	public int openGameTab(int tabId) {
		player.getPackets().sendGlobalConfig(168, tabId);
		int lastTab = 4; // tabId
		// tab = tabId;
		return lastTab;
	}

	public void removeAll() {
		openedinterfaces.clear();
	}

	public boolean removeInterface(int tabId, int childId) {
		if (!openedinterfaces.containsKey(tabId))
			return false;
		if (openedinterfaces.get(tabId)[0] != childId)
			return false;
		return openedinterfaces.remove(tabId) != null;
	}

	public boolean removeTab(int tabId) {
		return openedinterfaces.remove(tabId) != null;
	}

	public void replaceRealChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, 11, interfaceId);
	}

	public void sendChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, CHAT_BOX_TAB, interfaceId);
	}

	public void sendCombatStyles() {
		sendTab(resizableScreen ? 154 : 179, 884);
	}

	public void sendEmotes() {
		sendTab(resizableScreen ? 167 : 192, 590);
	}

	public void sendEquipment() {
		sendTab(resizableScreen ? 159 : 184, 387);
	}

	public void sendFadingInterface(int backgroundInterface) {
		if (hasRezizableScreen())
			player.getPackets().sendInterface(true, RESIZABLE_WINDOW_ID, 12,
					backgroundInterface);
		else
			player.getPackets().sendInterface(true, FIXED_WINDOW_ID, 11,
					backgroundInterface);
	}

	private void sendTab(Player player2, int i, int j) {
		// TODO Auto-generated method stub
		player.getPackets().sendInterface(true,
				resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, j, i);
	}

	public void sendInterface(int interfaceId) {
		player.getPackets()
				.sendInterface(
						false,
						resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
						resizableScreen ? RESIZABLE_SCREEN_TAB_ID
								: FIXED_SCREEN_TAB_ID, interfaceId);
	}

	public void switchDisplay() {
		if (player.getDisplayMode() == 2 || player.getDisplayMode() == 3) {
			player.getPackets().sendWindowsPane(746, 0);
			resizableScreen = true;
		} else {
			player.getPackets().sendWindowsPane(548, 0);
			resizableScreen = false;
		}
		sendMagicBook();
		sendPrayerBook();
		sendEquipment();
		// sendQuests();
		sendInventory();
		sendTaskSystem();
		sendSkills();
		sendEmotes();
		sendSettings();
		sendCombatStyles();
		sendRightSideScreenTabs();
		sendNotes();
		sendMusic();
		sendFriendList();
		sendClanChat();
		sendLogout();
		sendFriendChat();
		sendGameBarInterface();
		player.getSquealOfFortune().sendTab();
		player.getPackets().sendAccessMask(player, 0, 27, 679, 0, 4554126);
		player.getPackets().sendAccessMask(player, 28, 55, 679, 0, 2097152);
		player.getPackets().sendAccessMask(player, -1, -1, 884, 7, 2);
		player.getPackets().sendAccessMask(player, -1, -1, 884, 8, 2);
		player.getPackets().sendAccessMask(player, -1, -1, 884, 9, 2);
		player.getPackets().sendAccessMask(player, -1, -1, 884, 10, 2);
		player.getPackets().sendAccessMask(player, 0, 30, 271, 8, 2);
		player.getPackets().sendAccessMask(player, 0, 118, 590, 8, 6);
	}

	public void sendGameBarInterface() {
		sendTab(resizableScreen ? 63 : 171, 752);
		player.getPackets().sendInterface(true, 752, 9, 137);
	}

	public final void sendInterfaces() {
		player.getInterfaceManager().switchDisplay();
		player.getSkills().sendInterfaces();
		player.getCombatDefinitions().sendUnlockAttackStylesButtons();
		player.getMusicsManager().unlockMusicPlayer();
		player.getEmotesManager().unlockEmotesBook();
		player.getInventory().unlockInventoryOptions();
		player.getPrayer().unlockPrayerBookButtons();
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().unlock();
		player.getControllerManager().sendInterfaces();
	}

	public void sendInventory() {
		// sendTab(resizableScreen ? 158 : 183, Inventory.INVENTORY_INTERFACE);
		sendTab(player, Inventory.INVENTORY_INTERFACE, resizableScreen ? 158
				: 183);
	}

	public void sendInventoryInterface(int childId) {
		player.getPackets().sendInterface(false,
				resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
				resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID,
				childId);
	}

	public void sendMagicBook() {
		sendTab(resizableScreen ? 161 : 186, player.getCombatDefinitions()
				.getSpellBook());
	}

	public void sendOverlay(int interfaceId, boolean fullScreen) {
		sendTab(fullScreen ? 69 : 49, interfaceId);
	}

	public void sendPrayerBook() {
		sendTab(resizableScreen ? 160 : 185, 271);
	}

	public void sendQuests() {

	}

	public void sendScreenInterface(int backgroundInterface, int interfaceId) {
		player.getInterfaceManager().closeScreenInterface();

		if (hasRezizableScreen()) {
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 40,
					backgroundInterface);
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 41,
					interfaceId);
		} else {
			player.getPackets().sendInterface(false, FIXED_WINDOW_ID, 17,
					backgroundInterface);
			player.getPackets().sendInterface(false, FIXED_WINDOW_ID, 20,
					interfaceId);
		}

		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				if (hasRezizableScreen()) {
					player.getPackets().closeInterface(40);
					player.getPackets().closeInterface(41);
				} else {
					player.getPackets().closeInterface(200);
					player.getPackets().closeInterface(201);
				}
			}
		});
	}

	public void sendNotes() {
		sendTab(resizableScreen ? 169 : 194, 34);
	}

	public void sendMusic() {
		sendTab(resizableScreen ? 168 : 193, 187);
	}

	public void sendFriendList() {
		sendTab(resizableScreen ? 163 : 188, 550);
	}

	public void sendClanChat() {
		sendTab(resizableScreen ? 165 : 190, 1110);
	}

	public void sendLogout() {
		sendTab(resizableScreen ? 172 : 197, 182);
	}

	public void sendFriendChat() {
		sendTab(resizableScreen ? 164 : 189, 1109);
	}

	public void sendRightSideScreenTabs() {
		sendTab(player, 751, resizableScreen ? 64 : 56);
		sendTab(player, 752, resizableScreen ? 63 : 171);
		sendTab(player, 745, resizableScreen ? 57 : 43);
		sendTab(player, 754, resizableScreen ? 67 : 45);
		sendTab(player, 747, resizableScreen ? 241 : 167);
		sendTab(player, 748, resizableScreen ? 238 : 163);
		sendTab(player, 749, resizableScreen ? 239 : 164);
		sendTab(player, 750, resizableScreen ? 240 : 165);
	}

	public void sendSettings() {
		sendSettings(261);
	}

	public void sendSettings(int interfaceId) {
		sendTab(player.getInterfaceManager().hasRezizableScreen() ? 166 : 191,
				interfaceId);
	}

	public void sendSkills() {
		sendTab(resizableScreen ? 156 : 181, 320);
	}

	public void sendTab(int tabId, int interfaceId) {
		player.getPackets().sendInterface(true,
				resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, tabId,
				interfaceId);
	}

	public void sendTaskSystem() {
		sendTab(resizableScreen ? 155 : 180, 1056);
	}

	public void sendWindowPane() {
		player.getPackets().sendWindowsPane(resizableScreen ? 746 : 548, 0);
	}

	public void sendXPDisplay() {
		sendXPDisplay(1215);
	}

	public void sendXPDisplay(int interfaceId) {
		int rez = 244, fixed = 53;
		if (interfaceId == 1214) {
			rez = RESIZABLE_SCREEN_TAB_ID;
			fixed = FIXED_SCREEN_TAB_ID;
		}
		sendTab(resizableScreen ? rez : fixed, interfaceId);
	}

	public void sendXPPopup() {
		sendTab(resizableScreen ? 83 : 51, 1213);
	}

	public void setWindowsPane(int windowsPane) {
		this.windowsPane = windowsPane;
	}

}
