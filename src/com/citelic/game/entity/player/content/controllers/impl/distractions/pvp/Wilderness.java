package com.citelic.game.entity.player.content.controllers.impl.distractions.pvp;

import com.citelic.GameConstants;
import com.citelic.game.ForceMovement;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.Skills;
import com.citelic.game.entity.player.content.actions.skills.thieving.Thieving;
import com.citelic.game.entity.player.content.controllers.Controller;
import com.citelic.game.entity.player.content.controllers.impl.Kalaboss;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

public class Wilderness extends Controller {

	private boolean showingSkull;

	public static void checkBoosts(Player player) {
		boolean changed = false;
		int level = player.getSkills().getLevelForXp(Skills.ATTACK);
		int maxLevel = (int) (level + 5 + level * 0.15);
		if (maxLevel < player.getSkills().getLevel(Skills.ATTACK)) {
			player.getSkills().set(Skills.ATTACK, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.STRENGTH);
		maxLevel = (int) (level + 5 + level * 0.15);
		if (maxLevel < player.getSkills().getLevel(Skills.STRENGTH)) {
			player.getSkills().set(Skills.STRENGTH, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.DEFENCE);
		maxLevel = (int) (level + 5 + level * 0.15);
		if (maxLevel < player.getSkills().getLevel(Skills.DEFENCE)) {
			player.getSkills().set(Skills.DEFENCE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.RANGE);
		maxLevel = (int) (level + 5 + level * 0.1);
		if (maxLevel < player.getSkills().getLevel(Skills.RANGE)) {
			player.getSkills().set(Skills.RANGE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.MAGIC);
		maxLevel = level + 5;
		if (maxLevel < player.getSkills().getLevel(Skills.MAGIC)) {
			player.getSkills().set(Skills.MAGIC, maxLevel);
			changed = true;
		}
		if (changed) {
			player.getPackets().sendGameMessage(
					"Your extreme potion bonus has been reduced.");
		}
	}

	public static final boolean isAtWild(Tile tile) {// TODO fix this
		return tile.getX() >= 3011
				&& tile.getX() <= 3132
				&& tile.getY() >= 10052
				&& tile.getY() <= 10175 // fortihrny
				// dungeon
				|| tile.getX() >= 2940 && tile.getX() <= 3395
				&& tile.getY() >= 3525 && tile.getY() <= 4000
				|| tile.getX() >= 3264 && tile.getX() <= 3279
				&& tile.getY() >= 3279 && tile.getY() <= 3672
				|| tile.getX() >= 2756 && tile.getX() <= 2875
				&& tile.getY() >= 5512 && tile.getY() <= 5627
				|| tile.getX() >= 3158 && tile.getX() <= 3181
				&& tile.getY() >= 3679 && tile.getY() <= 3697
				|| tile.getX() >= 3280 && tile.getX() <= 3183
				&& tile.getY() >= 3885 && tile.getY() <= 3888
				|| tile.getX() >= 3012 && tile.getX() <= 3059
				&& tile.getY() >= 10303 && tile.getY() <= 10351;
	}

	public static boolean isDitch(int id) {
		return id >= 1440 && id <= 1444 || id >= 65076 && id <= 65087;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (player.isCanPvp() && !p2.isCanPvp()) {
				player.getPackets().sendGameMessage(
						"That player is not in the wilderness.");
				return false;
			}
			if (canHit(target))
				return true;
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC)
			return true;
		Player p2 = (Player) target;
		if (Math.abs(player.getSkills().getCombatLevel()
				- p2.getSkills().getCombatLevel()) > getWildLevel())
			return false;
		return true;
	}

	@Override
	public void forceClose() {
		removeIcon();
	}

	public int getWildLevel() {
		if (player.getY() > 9900)
			return (player.getY() - 9912) / 8 + 1;
		return (player.getY() - 3520) / 8 + 1;
	}

	public boolean isAtWildSafe() {
		return player.getX() >= 2940 && player.getX() <= 3395
				&& player.getY() <= 3524 && player.getY() >= 3523;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof NPC)
			return true;
		if (!canAttack(target))
			return false;
		if (target.getAttackedBy() != player
				&& player.getAttackedBy() != target) {
			player.setWildernessSkull();
		}
		if (player.getCombatDefinitions().getSpellId() <= 0
				&& Utilities.inCircle(new Tile(3105, 3933, 0), target, 24)) {
			player.getPackets().sendGameMessage(
					"You can only use magic in the arena.");
			return false;
		}
		return true;
	}

	@Override
	public boolean login() {
		moved();
		return false;
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void moved() {
		boolean isAtWild = Wilderness.isAtWild(player);
		boolean isAtWildSafe = isAtWildSafe();
		if (!showingSkull && isAtWild && !isAtWildSafe) {
			showingSkull = true;
			player.setCanPvp(true);
			showSkull();
			player.getPlayerAppearances().generateAppearenceData();
		} else if (showingSkull && (isAtWildSafe || !isAtWild)) {
			removeIcon();
		} else if (!isAtWildSafe && !isAtWild) {
			player.setCanPvp(false);
			removeIcon();
			removeController();
		} else if (Kalaboss.isAtKalaboss(player)) {
			removeIcon();
			player.setCanPvp(false);
			removeController();
			player.getControllerManager().startController("Kalaboss");
		}
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		if (getWildLevel() > 20) {
			player.getPackets().sendGameMessage(
					"A mysterious force prevents you from teleporting.");
			return false;
		}
		if (player.getTeleBlockDelay() > Utilities.currentTimeMillis()) {
			player.getPackets().sendGameMessage(
					"A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		if (getWildLevel() > 20) {
			player.getPackets().sendGameMessage(
					"A mysterious force prevents you from teleporting.");
			return false;
		}
		if (player.getTeleBlockDelay() > Utilities.currentTimeMillis()) {
			player.getPackets().sendGameMessage(
					"A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;

	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (Wilderness.isDitch(object.getId())) {
			player.lock();
			player.setNextAnimation(new Animation(6132));
			final Tile toTile = new Tile(object.getRotation() == 1
					|| object.getRotation() == 3 ? object.getX() + 2
					: player.getX(), object.getRotation() == 0
					|| object.getRotation() == 2 ? object.getY() - 1
					: player.getY(), object.getZ());

			player.setNextForceMovement(new ForceMovement(new Tile(player), 1,
					toTile, 2, object.getRotation() == 0
							|| object.getRotation() == 2 ? ForceMovement.SOUTH
							: ForceMovement.EAST));
			EngineTaskManager.schedule(new EngineTask() {
				@Override
				public void run() {
					Wilderness.this.player.setNextTile(toTile);
					Wilderness.this.player.faceObject(object);
					Wilderness.this.removeIcon();
					Wilderness.this.removeController();
					Wilderness.this.player.resetReceivedDamage();
					Wilderness.this.player.unlock();
				}
			}, 2);
			return false;
		} else if (object.getId() == 2557 || object.getId() == 65717) {
			player.getPackets()
					.sendGameMessage(
							"It seems it is locked, maybe you should try something else.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(final GameObject object) {
		if (object.getId() == 2557 || object.getId() == 65717) {
			Thieving.pickDoor(player, object);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		if (player.getTeleBlockDelay() > Utilities.currentTimeMillis()) {
			player.getPackets().sendGameMessage(
					"A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;
	}

	public void removeIcon() {
		if (showingSkull) {
			showingSkull = false;
			player.setCanPvp(false);
			player.getPackets().closeInterface(
					player.getInterfaceManager().hasRezizableScreen() ? 11 : 0);
			player.getPlayerAppearances().generateAppearenceData();
			player.getEquipment().refresh(null);
		}
	}

	@Override
	public boolean sendDeath() {

		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					Wilderness.this.player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					Wilderness.this.player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = Wilderness.this.player
							.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						killer.removeDamage(Wilderness.this.player);
						killer.increaseKillCount(Wilderness.this.player);
					}
					Wilderness.this.player.sendItemsOnDeath(killer);
					Wilderness.this.player.getEquipment().init();
					Wilderness.this.player.getInventory().init();
					Wilderness.this.player.reset();
					Wilderness.this.player.setNextTile(new Tile(
							GameConstants.START_PLAYER_LOCATION));
					Wilderness.this.player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					Wilderness.this.removeIcon();
					Wilderness.this.removeController();
					Wilderness.this.player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void sendInterfaces() {
		if (Wilderness.isAtWild(player)) {
			showSkull();
		}
	}

	public void showSkull() {
		player.getInterfaceManager()
				.sendTab(
						player.getInterfaceManager().hasRezizableScreen() ? 11
								: 0, 381);
	}

	@Override
	public void start() {
		Wilderness.checkBoosts(player);
	}

}
