package com.citelic.game.entity.player.content.controllers.impl.distractions.bosses;

import java.util.ArrayList;
import java.util.List;

import com.citelic.GameConstants;
import com.citelic.game.ForceTalk;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.impl.others.BarrowsBrother;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.item.Item;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public final class Barrows extends Controller {

	// they have to be handled by correct order
	// AHRIM
	// DHAROK
	// GUTHAN
	// KARIL
	// TORAG
	// VERAC
	private static enum Hills {
		AHRIM_HILL(new Tile(3564, 3287, 0), new Tile(3557, 9703, 3)), DHAROK_HILL(
				new Tile(3573, 3296, 0), new Tile(3556, 9718, 3)), GUTHAN_HILL(
				new Tile(3574, 3279, 0), new Tile(3534, 9704, 3)), KARIL_HILL(
				new Tile(3563, 3276, 0), new Tile(3546, 9684, 3)), TORAG_HILL(
				new Tile(3553, 3281, 0), new Tile(3568, 9683, 3)), VERAC_HILL(
				new Tile(3556, 3296, 0), new Tile(3578, 9706, 3));

		private Tile outBound;
		private Tile inside;

		// out bound since it not a full circle

		private Hills(Tile outBound, Tile in) {
			this.outBound = outBound;
			inside = in;
		}
	}

	public static boolean digIntoGrave(final Player player) {
		for (Hills hill : Hills.values()) {
			if (player.getZ() == hill.outBound.getZ()
					&& player.getX() >= hill.outBound.getX()
					&& player.getY() >= hill.outBound.getY()
					&& player.getX() <= hill.outBound.getX() + 3
					&& player.getY() <= hill.outBound.getY() + 3) {
				player.useStairs(-1, hill.inside, 1, 2,
						"You've broken into a crypt.");
				EngineTaskManager.schedule(new EngineTask() {
					@Override
					public void run() {
						player.getControllerManager()
								.startController("Barrows");
					}
				});
				return true;
			}
		}

		return false;
	}

	private BarrowsBrother target;

	private static final Item[] COMMUM_REWARDS = { new Item(558, 1795),
			new Item(562, 773), new Item(560, 391), new Item(565, 164),
			new Item(4740, 188) };

	// once i add the ring effect
	private static final Item[] RING_OF_WEALTH_REWARDS = { new Item(165, 1),
			new Item(159, 1), new Item(141, 1), new Item(129, 1),
			new Item(385, 4) };

	private static final Item[] RARE_REWARDS = { new Item(1149, 1),
			new Item(987, 1), new Item(985, 1) };

	Item[] BARROW_REWARDS = { new Item(4708, 1), new Item(4710, 1),
			new Item(4712, 1), new Item(4714, 1), new Item(4716, 1),
			new Item(4718, 1), new Item(4720, 1), new Item(4722, 1),
			new Item(4724, 1), new Item(4726, 1), new Item(4728, 1),
			new Item(4730, 1), new Item(4732, 1), new Item(4734, 1),
			new Item(4736, 1), new Item(4738, 1), new Item(4745, 1),
			new Item(4747, 1), new Item(4749, 1), new Item(4751, 1),
			new Item(4753, 1), new Item(4755, 1), new Item(4757, 1), };

	private boolean noSpaceOnInv;

	private int headComponentId;

	private int timer;

	public Barrows() {

	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof BarrowsBrother && target != this.target) {
			player.getPackets().sendGameMessage("This isn't your target.");
			return false;
		}
		return true;
	}

	public void drop(Item item) {
		Item dropItem = new Item(item.getId(), Utilities.random(item
				.getDefinitions().isStackable() ? item.getAmount()
				* GameConstants.DROP_RATE : item.getAmount()) + 1);
		if (noSpaceOnInv && player.getInventory().addItem(dropItem))
			return;
		noSpaceOnInv = true;
		Engine.addGroundItem(dropItem, player, player, true, 180, true);

	}

	private void exit(Tile outside) {
		player.setNextTile(outside);
		leave(false);
	}

	@Override
	public void forceClose() {
		leave(true);
	}

	public int getAndIncreaseHeadIndex() {
		Integer head = (Integer) player.getTemporaryAttributtes().remove(
				"BarrowsHead");
		if (head == null || head == player.getKilledBarrowBrothers().length - 1)
			head = 0;
		player.getTemporaryAttributtes().put("BarrowsHead", head + 1);
		return player.getKilledBarrowBrothers()[head] ? head : -1;
	}

	public int getRandomBrother() {
		List<Integer> bros = new ArrayList<Integer>();
		for (int i = 0; i < Hills.values().length; i++) {
			if (player.getKilledBarrowBrothers()[i]
					|| player.getHiddenBrother() == i)
				continue;
			bros.add(i);
		}
		if (bros.isEmpty())
			return -1;
		return bros.get(Utilities.random(bros.size()));

	};

	public int getSarcophagusId(int objectId) {
		switch (objectId) {
		case 66017:
			return 0;
		case 63177:
			return 1;
		case 66020:
			return 2;
		case 66018:
			return 3;
		case 66019:
			return 4;
		case 66016:
			return 5;
		default:
			return -1;
		}
	}

	private void leave(boolean logout) {
		if (target != null)
			target.finish(); // target also calls removing hint icon at remove
		if (!logout) {
			player.getPackets().sendMiniMapStatus(0); // unblacks minimap
			if (player.getHiddenBrother() == -1)
				player.getPackets().sendStopCameraShake();
			else
				player.getInterfaceManager().closeOverlay(
						player.getInterfaceManager().hasRezizableScreen());
			removeController();
		}
	}

	public void loadData() {
		resetHeadTimer();
		for (int i = 0; i < player.getKilledBarrowBrothers().length; i++)
			sendBrotherSlain(i, player.getKilledBarrowBrothers()[i]);
		sendCreaturesSlainCount(player.getBarrowsKillCount());
		player.getPackets().sendMiniMapStatus(2); // blacks minimap
	}

	@Override
	public boolean login() {
		if (player.getHiddenBrother() == -1)
			player.getPackets().sendCameraShake(3, 25, 50, 25, 50);
		loadData();
		sendInterfaces();
		return false;
	}

	@Override
	public boolean logout() {
		leave(true);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		leave(false);
	}

	// component 9, 10, 11

	@Override
	public void process() {
		if (timer > 0) {
			timer--;
			return;
		}
		if (headComponentId == 0) {
			if (player.getHiddenBrother() == -1) {
				player.applyHit(new Hit(player, Utilities.random(50) + 1,
						HitLook.REGULAR_DAMAGE));
				resetHeadTimer();
				return;
			}
			int headIndex = getAndIncreaseHeadIndex();
			if (headIndex == -1) {
				resetHeadTimer();
				return;
			}
			headComponentId = 9 + Utilities.random(2);
			player.getPackets().sendItemOnIComponent(24, headComponentId,
					4761 + headIndex, 0);
			player.getPackets().sendIComponentAnimation(9810, 24,
					headComponentId);
			int activeLevel = player.getPrayer().getPrayerpoints();
			if (activeLevel > 0) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
				player.getPrayer().drainPrayer(level / 6);
			}
			timer = 3;
		} else {
			player.getPackets()
					.sendItemOnIComponent(24, headComponentId, -1, 0);
			headComponentId = 0;
			resetHeadTimer();
		}
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() >= 6702 && object.getId() <= 6707) {
			Tile out = Hills.values()[object.getId() - 6702].outBound;
			// cant make a perfect middle since 3/ 2 wont make a real integer
			// number or wahtever u call it..
			exit(new Tile(out.getX() + 1, out.getY() + 1, out.getZ()));
			return false;
		} else if (object.getId() == 10284) {
			if (player.getHiddenBrother() == -1) {// reached chest
				player.getPackets().sendGameMessage("You found nothing.");
				return false;
			}
			if (!player.getKilledBarrowBrothers()[player.getHiddenBrother()])
				sendTarget(2025 + player.getHiddenBrother(), player);
			if (target != null) {
				player.getPackets().sendGameMessage("You found nothing.");
				return false;
			}
			sendReward();
			player.getPackets().sendCameraShake(3, 12, 25, 12, 25);
			player.getInterfaceManager().closeOverlay(
					player.getInterfaceManager().hasRezizableScreen());
			player.getPackets().sendSpawnedObject(
					new GameObject(6775, 10, 0, 3551, 9695, 0));
			player.resetBarrows();
			return false;
		} else if (object.getId() >= 6716 && object.getId() <= 6749) {
			Tile walkTo;
			if (object.getRotation() == 0)
				walkTo = new Tile(object.getX() + 5, object.getY(), 0);
			else if (object.getRotation() == 1)
				walkTo = new Tile(object.getX(), object.getY() - 5, 0);
			else if (object.getRotation() == 2)
				walkTo = new Tile(object.getX() - 5, object.getY(), 0);
			else
				walkTo = new Tile(object.getX(), object.getY() + 5, 0);
			if (!Engine.isNotCliped(walkTo.getZ(), walkTo.getX(),
					walkTo.getY(), 1))
				return false;
			player.addWalkSteps(walkTo.getX(), walkTo.getY(), -1, false);
			player.lock(6);
			if (player.getHiddenBrother() != -1) {
				int brother = getRandomBrother();
				if (brother != -1)
					sendTarget(2025 + brother, walkTo);
			}
			return false;
		} else {
			int sarcoId = getSarcophagusId(object.getId());
			if (sarcoId != -1) {
				if (sarcoId == player.getHiddenBrother())
					player.getDialogueManager().startDialogue("BarrowsD");
				else if (target != null
						|| player.getKilledBarrowBrothers()[sarcoId])
					player.getPackets().sendGameMessage("You found nothing.");
				else
					sendTarget(2025 + sarcoId, player);
				return false;
			}
		}
		return true;
	}

	public void resetHeadTimer() {
		timer = 20 + Utilities.random(6);
	}

	public void sendBrotherSlain(int index, boolean slain) {
		player.getPackets().sendConfigByFile(457 + index, slain ? 1 : 0);
	}

	public void sendCreaturesSlainCount(int count) {
		player.getPackets().sendConfigByFile(464, count);
	}

	@Override
	public boolean sendDeath() {
		leave(false);
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (player.getHiddenBrother() != -1)
			player.getInterfaceManager().sendOverlay(24,
					player.getInterfaceManager().hasRezizableScreen());
	}

	// 4% prob barrows reward per bro
	// 1% per 10kills.
	public void sendReward() {
		double percentage = 0;
		for (boolean died : player.getKilledBarrowBrothers()) {
			if (died)
				percentage += 4;
		}
		percentage += (player.getBarrowsKillCount() / 10);
		if (percentage >= Math.random() * 100) {
			// reard barrows
			drop(BARROW_REWARDS[Utilities.random(BARROW_REWARDS.length)]);
		}
		if (player.getEquipment().getRingId() == 2572) {
			if (Utilities.random(7) == 0)
				drop(RARE_REWARDS[Utilities.random(RARE_REWARDS.length)]);
		} else {
			if (Utilities.random(10) == 0)
				drop(RARE_REWARDS[Utilities.random(RARE_REWARDS.length)]);
		}
		if (Utilities.random(3) != 0)
			drop(COMMUM_REWARDS[Utilities.random(COMMUM_REWARDS.length)]);
		if (player.getEquipment().getRingId() == 2572)
			drop(RING_OF_WEALTH_REWARDS[Utilities
					.random(RING_OF_WEALTH_REWARDS.length)]);
		// here reward other stuff normaly
		drop(new Item(995, 1000000)); // money reward at least always
	}

	public void sendTarget(int id, Tile tile) {
		if (target != null)
			target.disapear();
		target = new BarrowsBrother(id, tile, this);
		target.setTarget(player);
		target.setNextForceTalk(new ForceTalk("You dare disturb my rest!"));
		player.getHintIconsManager().addHintIcon(target, 1, -1, false);
	}

	public void setBrotherSlained(int index) {
		player.getKilledBarrowBrothers()[index] = true;
		sendBrotherSlain(index, true);
	}

	@Override
	public void start() {
		if (player.getHiddenBrother() == -1)
			player.setHiddenBrother(Utilities.random(Hills.values().length));
		loadData();
		sendInterfaces();
	}

	public void targetDied() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		setBrotherSlained(target.getId() - 2025);
		target = null;

	}

	public void targetFinishedWithoutDie() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		target = null;
	}

}
