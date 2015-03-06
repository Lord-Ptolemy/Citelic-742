package com.citelic.game.entity.player.content.actions.skills.prayer;

import java.util.HashMap;
import java.util.Map;

import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.impl.distractions.pvp.Wilderness;
import com.citelic.game.entity.player.item.Item;

public class Burying {

	public enum Bone {
		NORMAL(526, 15),

		BIG(532, 30),

		DRAGON(536, 45),

		OURG(4834, 55),

		DAGANNOTH_BONES(6729, 75),

		FROST_DRAGON(18830, 100),

		SKELETAL_WYVERN(6812, 100);

		private int id;
		private double experience;

		private static Map<Integer, Bone> bones = new HashMap<Integer, Bone>();

		static {
			for (Bone bone : Bone.values()) {
				bones.put(bone.getId(), bone);
			}
		}

		public static final Animation BURY_ANIMATION = new Animation(827);

		public static void bury(final Player player, int inventorySlot) {
			final Item item = player.getInventory().getItem(inventorySlot);
			if (item == null || Bone.forId(item.getId()) == null)
				return;
			if (player.isLocked())
				return;
			final Bone bone = Bone.forId(item.getId());
			final ItemDefinitions itemDef = new ItemDefinitions(item.getId());
			player.lock(2);
			player.getPackets().sendSound(2738, 0, 1);
			player.setNextAnimation(new Animation(827));
			player.getPackets().sendGameMessage(
					"You dig a hole in the ground...");
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					player.getPackets().sendGameMessage(
							"You bury the " + itemDef.getName().toLowerCase());
					player.getInventory().deleteItem(item.getId(), 1);
					player.getSkills().addXp(Skills.PRAYER,
							increasedExperience(player, bone.getExperience()));
					stop();
				}

			}, 2);
		}

		public static Bone forId(int id) {
			return bones.get(id);
		}

		public static double increasedExperience(Player player, double totalXp) {
			if (Wilderness.isAtWild(player)
					&& player.getEquipment().getGlovesId() == 13848)
				totalXp *= 1.030;
			return totalXp;
		}

		private Bone(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public double getExperience() {
			return experience;
		}

		public int getId() {
			return id;
		}
	}
}
