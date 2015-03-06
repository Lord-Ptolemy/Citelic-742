package com.citelic.game.entity.player.content.actions.skills.prayer;

import java.util.HashMap;
import java.util.Map;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.Action;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;

@SuppressWarnings("unused")
public class BonesOnAltar extends Action {

	public enum Bones {
		BONES(new Item(526, 1), 20), BIG_BONES(new Item(532, 1), 50), DRAGON_BONES(
				new Item(536, 1), 100), OURG_BONES(new Item(4834, 1), 120), DAGANNOTH_BONES(
				new Item(6729, 1), 150), FROST_DRAGON_BONES(new Item(18830, 1),
				200), SKELETAL_WYVERN(new Item(6812, 1), 200);

		private static Map<Short, Bones> bones = new HashMap<Short, Bones>();

		static {
			for (Bones bone : Bones.values()) {
				bones.put((short) bone.getBone().getId(), bone);
			}
		}

		public static Bones forId(short itemId) {
			return bones.get(itemId);
		}

		private Item item;
		private int xp;

		private Bones(Item item, int xp) {
			this.item = item;
			this.xp = xp;
		}

		public Item getBone() {
			return item;
		}

		public int getXP() {
			return xp;
		}
	}

	public static Bones isGood(Item item) {
		return Bones.forId((short) item.getId());
	}

	public final String MESSAGE = "The gods are very pleased with your offerings.";

	public final double MULTIPLIER = 2.5;
	private Bones bone;
	private int amount;
	private Item item;
	private GameObject object;

	private Animation USING = new Animation(896);

	public BonesOnAltar(GameObject object, Item item, int amount) {
		this.amount = amount;
		this.item = item;
		this.object = object;
	}

	@Override
	public boolean process(Player player) {
		if (!Engine.getRegion(object.getRegionId()).containsObject(
				object.getId(), object))
			return false;
		if (player.getX() == 3687 || player.getY() == 3686) {
			return false;
		}
		if (!player.getInventory().containsItem(item.getId(), 1)) {
			return false;
		}
		if (!player.getInventory().containsItem(bone.getBone().getId(), 1)) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (player.getX() == 3687 || player.getY() == 3686) {
			stop(player);
		}
		player.closeInterfaces();
		player.setNextAnimation(USING);
		player.getPackets().sendGraphics(new Graphics(624), object);
		player.getInventory().deleteItem(item.getId(), 1);
		player.getSkills().addXp(Skills.PRAYER,
				bone.getXP() * player.getAuraManager().getPrayerMultiplier());
		player.getPackets().sendGameMessage(MESSAGE);
		player.getInventory().refresh();
		return 3;
	}

	@Override
	public boolean start(Player player) {
		if ((this.bone = Bones.forId((short) item.getId())) == null) {
			return false;
		}
		player.faceObject(object);
		return true;
	}

	@Override
	public void stop(final Player player) {
		this.setActionDelay(player, 3);
	}
}