package com.citelic.game.entity.npc.impl.others;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.FloorItem;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class GraveStone extends NPC {

	private static final int GRAVE_STONE_INTERFACE = 266;
	private static final List<GraveStone> gravestones = new ArrayList<GraveStone>();
	private String username;
	private int ticks;
	private String inscription;
	private List<FloorItem> floorItems;
	private int graveStone;
	private boolean blessed;
	private int hintIcon;

	public GraveStone(Player player, Tile deathTile, Item[] items) {
		super(GraveStone.getNPCId(player.getGraveStone()), deathTile, -1, true);
		graveStone = player.getGraveStone();
		setDirection(Utilities.getFaceDirection(0, -1));
		setNextAnimation(new Animation(graveStone == 1 ? 7396 : 7394));
		username = player.getUsername();
		ticks = GraveStone.getMaximumTicks(graveStone);
		inscription = getInscription(player.getDisplayName());
		floorItems = new ArrayList<FloorItem>();
		for (Item item : items) {
			FloorItem i = Engine.addGroundItem(item, deathTile, player, true,
					-1, 1);
			if (i != null) {
				floorItems.add(i);
			}
		}
		synchronized (GraveStone.gravestones) {
			GraveStone oldStone = GraveStone.getGraveStoneByUsername(username);
			if (oldStone != null) {
				addLeftTime(false);
				oldStone.finish();
			}
			GraveStone.gravestones.add(this);
		}
		player.getPackets().sendRunScript(2434, ticks);
		hintIcon = player.getHintIconsManager().addHintIcon(this, 0, -1, true);
		player.getPackets().sendGlobalConfig(623, deathTile.getTileHash());
		player.getPackets().sendGlobalConfig(624, 0);
		player.getPackets().sendGlobalString(53, "Your gravestone marker");
		player.getPackets()
				.sendGameMessage(
						"Your items have been dropped at your gravestone, where they'll remain until it crumbles. Look at the World map to help find your gravestone.");
		player.getPackets().sendGameMessage(
				"It looks like it'll survive another " + ticks / 100
						+ " minutes.");
	}

	public static GraveStone getGraveStoneByUsername(String username) {
		for (GraveStone stone : GraveStone.gravestones)
			if (stone.username.equals(username))
				return stone;
		return null;
	}

	public static Item[][] getItemsKeptOnDeath(Player player, Integer[][] slots) {
		ArrayList<Item> droppedItems = new ArrayList<Item>();
		ArrayList<Item> keptItems = new ArrayList<Item>();
		for (int i : slots[0]) { // items kept on death
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null) {
				continue;
			}
			if (item.getAmount() > 1) {
				droppedItems.add(new Item(item.getId(), item.getAmount() - 1));
				item.setAmount(1);
			}
			keptItems.add(item);
		}
		for (int i : slots[1]) { // items droped on death
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null) {
				continue;
			}
			droppedItems.add(item);
		}
		for (int i : slots[2]) { // items protected by default
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null) {
				continue;
			}
			keptItems.add(item);
		}
		return new Item[][] { keptItems.toArray(new Item[keptItems.size()]),
				droppedItems.toArray(new Item[droppedItems.size()]) };

	}

	/*
	 * return arrays: items kept on death by default, items dropped on death by
	 * default, items protected by default, items lost by default
	 */
	public static Integer[][] getItemSlotsKeptOnDeath(final Player player,
			boolean atWilderness, boolean skulled, boolean protectPrayer) {
		ArrayList<Integer> droppedItems = new ArrayList<Integer>();
		ArrayList<Integer> protectedItems = atWilderness ? null
				: new ArrayList<Integer>();
		ArrayList<Integer> lostItems = new ArrayList<Integer>();
		for (int i = 1; i < 44; i++) {
			Item item = i >= 16 ? player.getInventory().getItem(i - 16)
					: player.getEquipment().getItem(i - 1);
			if (item == null) {
				continue;
			}
			int stageOnDeath = item.getDefinitions().getStageOnDeath();
			if (!atWilderness && stageOnDeath == 1) {
				protectedItems.add(i);
			} else if (stageOnDeath == -1) {
				lostItems.add(i);
			} else {
				droppedItems.add(i);
			}
		}
		int keptAmount = skulled ? 0 : 3;
		if (protectPrayer) {
			keptAmount++;
		}
		if (droppedItems.size() < keptAmount) {
			keptAmount = droppedItems.size();
		}
		Collections.sort(droppedItems, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return 0;
			}

		});
		Integer[] keptItems = new Integer[keptAmount];
		for (int i = 0; i < keptAmount; i++) {
			keptItems[i] = droppedItems.remove(0);
		}
		return new Integer[][] {
				keptItems,
				droppedItems.toArray(new Integer[droppedItems.size()]),
				atWilderness ? new Integer[0] : protectedItems
						.toArray(new Integer[protectedItems.size()]),
				atWilderness ? new Integer[0] : lostItems
						.toArray(new Integer[lostItems.size()]) };

	}

	public static int getMaximumTicks(int graveStone) {
		switch (graveStone) {
		case 0:
			return 500;
		case 1:
		case 2:
			return 600;
		case 3:
			return 800;
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
			return 1000;
		case 12:
			return 1200;
		case 13:
			return 1500;
		}
		return 500;
	}

	public static int getNPCId(int currentGrave) {
		if (currentGrave == 13)
			return 13296;
		return 6565 + currentGrave * 3;
	}

	public void addLeftTime(boolean clean) {
		if (clean) {
			for (FloorItem item : floorItems) {
				Engine.turnPublic(item, 60);
			}
		} else {
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						for (FloorItem item : floorItems) {
							Engine.turnPublic(item, 60);
						}
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, (long) (ticks * 0.6), TimeUnit.SECONDS);
		}
	}

	public void decrementGrave(int stage, String message) {
		Player player = getPlayer();
		if (player != null) {
			player.getPackets().sendGameMessage("<col=7E2217>" + message);
			player.getPackets().sendRunScript(2434, ticks);
		}
		if (stage == -1) {
			addLeftTime(true);
			finish();
		} else {
			setNextNPCTransformation(GraveStone.getNPCId(graveStone) + stage);
		}
	}

	public void demolish(Player demolisher) {
		if (!demolisher.getUsername().equals(username)) {
			demolisher
					.getPackets()
					.sendGameMessage(
							"It would be impolite to demolish someone else's gravestone.");
			return;
		}
		addLeftTime(true);
		finish();
		demolisher.getPackets().sendGameMessage(
				"It looks like it'll survive another " + ticks / 100
						+ " minutes. You demolish it anyway.");
	}

	@Override
	public void finish() {
		synchronized (GraveStone.gravestones) {
			GraveStone.gravestones.remove(this);
		}
		Player player = getPlayer();
		if (player != null) {
			player.getPackets().sendRunScript(2434, 0);
			player.getHintIconsManager().removeHintIcon(hintIcon);
			player.getPackets().sendGlobalConfig(623, -1);

		}
		super.finish();
	}

	private String getInscription(String username) {
		username = Utilities.formatPlayerNameForDisplay(username);
		switch (graveStone) {
		case 0:
		case 1:
			return "In memory of <i>" + username + "</i>,<br>who died here.";
		case 2:
		case 3:
			return "In loving memory of our dear friend <i>" + username
					+ "</i>,who <br>died in this place @X@ minutes ago.";
		case 4:
		case 5:
			return "In your travels, pause awhile to remember <i>" + username
					+ "</i>,<br>who passed away at this spot.";
		case 6:
			return "<i>"
					+ username
					+ "</i>, <br>an enlightened servant of Saradomin,<br>perished in this place.";
		case 7:
			return "<i>"
					+ username
					+ "</i>, <br>a most bloodthirsty follower of Zamorak,<br>perished in this place.";
		case 8:
			return "<i>"
					+ username
					+ "</i>, <br>who walked with the Balance of Guthix,<br>perished in this place.";
		case 9:
			return "<i>"
					+ username
					+ "</i>, <br>a vicious warrior dedicated to Bandos,<br>perished in this place.";
		case 10:
			return "<i>"
					+ username
					+ "</i>, <br>a follower of the Law of Armadyl,<br>perished in this place.";
		case 11:
			return "<i>"
					+ username
					+ "</i>, <br>servant of the Unknown Power,<br>perished in this place.";
		case 12:
			return "Ye frail mortals who gaze upon this sight, forget not<br>the fate of <i>"
					+ username
					+ "</i>, once mighty, now surrendered to the inescapable grasp of destiny.<br><i>Requiescat in pace.</i>";
		case 13:
			return "Here lies <i>"
					+ username
					+ "</i>, friend of dwarves. Great in<br>life, glorious in death. His/Her name lives on in<br>song and story.";
		}
		return "Cabbage";
	}

	private Player getPlayer() {
		return Engine.getPlayer(username);
	}

	@Override
	public void processNPC() {
		ticks--;
		if (ticks == 0) {
			decrementGrave(-1, "Your grave has collapsed!");
		} else if (ticks == 50) {
			decrementGrave(2, "Your grave is collapsing.");
		} else if (ticks == 100) {
			decrementGrave(1, "Your grave is about to collapse.");
		}
	}

	/*
	 * if (item.getAmount() > 1) { item.setAmount(item.getAmount() - 1);
	 * count++; } else containedItems.remove(count);
	 */

	public void repair(Player blesser, boolean bless) {
		if (blesser.getSkills().getLevel(Skills.PRAYER) < (bless ? 70 : 2)) {
			blesser.getPackets().sendGameMessage(
					"You need " + (bless ? 70 : 2) + " prayer to "
							+ (bless ? "bless" : "repair") + " a gravestone.");
			return;
		}
		if (blesser.getUsername().equals(username)) {
			blesser.getPackets().sendGameMessage(
					"The gods don't seem to approve of people attempting to "
							+ (bless ? "bless" : "repair")
							+ " their own gravestones.");
			return;
		}
		if (bless && blessed) {
			blesser.getPackets().sendGameMessage(
					"This gravestone has already been blessed.");
			return;
		} else if (!bless && ticks > 100) {
			blesser.getPackets().sendGameMessage(
					"This gravestone doesn't seem to need repair.");
			return;
		}
		ticks += bless ? 6000 : 500; // 5minutes, 1hour
		blessed = true;
		decrementGrave(0, blesser.getDisplayName() + "has "
				+ (bless ? "blessed" : "repaired")
				+ " your gravestone. It should survive another " + ticks / 100
				+ " minutes.");
		blesser.getPackets().sendGameMessage(
				"You " + (bless ? "bless" : "repair") + " the grave.");
		blesser.lock(2);
		blesser.setNextAnimation(new Animation(645));
	}

	public void sendGraveInscription(Player reader) {
		reader.getInterfaceManager().sendInterface(
				GraveStone.GRAVE_STONE_INTERFACE);
		reader.getPackets().sendConfigByFile(4191, graveStone == 0 ? 0 : 1);
		if (ticks <= 50) {
			reader.getPackets().sendIComponentText(
					GraveStone.GRAVE_STONE_INTERFACE, 5,
					"The inscription is too unclear to read.");
		} else if (reader == getPlayer()) {
			reader.getPackets()
					.sendIComponentText(
							GraveStone.GRAVE_STONE_INTERFACE,
							5,
							"It looks like it'll survive another "
									+ ticks
									/ 100
									+ " minutes. Isn't there something a bit odd about reading your own gravestone?");
		} else {
			reader.getPackets().sendIComponentText(
					GraveStone.GRAVE_STONE_INTERFACE, 5, inscription);
		}
	}
}