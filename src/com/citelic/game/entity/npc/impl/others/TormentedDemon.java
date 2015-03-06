package com.citelic.game.entity.npc.impl.others;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.citelic.cores.CoresManager;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.Hit.HitLook;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.combat.NPCCombatDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public final class TormentedDemon extends NPC {

	public static boolean atTD(Tile tile) {
		if ((tile.getX() >= 2560 && tile.getX() <= 2630)
				&& (tile.getY() >= 5710 && tile.getY() <= 5753))
			return true;
		return false;
	}

	private boolean[] demonPrayer;
	private int fixedCombatType;
	private int[] cachedDamage;
	private int shieldTimer;
	private int fixedAmount;

	private int prayerTimer;

	public TormentedDemon(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		demonPrayer = new boolean[3];
		cachedDamage = new int[3];
		shieldTimer = 0;
		switchPrayers(0);
	}

	public int getFixedAmount() {
		return fixedAmount;
	}

	public int getFixedCombatType() {
		return fixedCombatType;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		int type = 0;
		super.handleIngoingHit(hit);
		if (hit.getSource() instanceof Player) {// darklight
			Player player = (Player) hit.getSource();
			if ((player.getEquipment().getWeaponId() == 6746 || player
					.getEquipment().getWeaponId() == 2402)
					&& hit.getLook() == HitLook.MELEE_DAMAGE
					&& hit.getDamage() > 0) {
				shieldTimer = 60;
				player.getPackets().sendGameMessage(
						"The demon is temporarily weakend by your weapon.");
			}
		}
		if (shieldTimer <= 0) {// 75% of damage is absorbed
			hit.setDamage((int) (hit.getDamage() * 0.25));
			setNextGraphics(new Graphics(1885));
		}
		if (hit.getLook() == HitLook.MELEE_DAMAGE) {
			if (demonPrayer[0]) {
				hit.setDamage(0);
			} else {
				cachedDamage[0] += hit.getDamage();
			}
		} else if (hit.getLook() == HitLook.MELEE_DAMAGE) {
			type = 1;
			if (demonPrayer[1]) {
				hit.setDamage(0);
			} else {
				cachedDamage[1] += hit.getDamage();
			}
		} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
			type = 2;
			if (demonPrayer[2]) {
				hit.setDamage(0);
			} else {
				cachedDamage[2] += hit.getDamage();
			}
		} else if (hit.getLook() == HitLook.MISSED) {
			cachedDamage[type] += 20;
		} else {
			cachedDamage[Utilities.getRandom(2)] += 20;// random
		}
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (Utilities.getRandom(40) <= 2)
			sendRandomProjectile();
		if (getCombat().process()) {// no point in processing
			if (shieldTimer > 0)
				shieldTimer--;
			if (prayerTimer > 0)
				prayerTimer--;
			if (fixedAmount >= 5)
				fixedAmount = 0;
			if (prayerTimer == 0) {
				for (int i = 0; i < cachedDamage.length; i++) {
					if (cachedDamage[i] >= 310) {
						demonPrayer = new boolean[3];
						switchPrayers(i);
						cachedDamage = new int[3];
					}
				}
			}
			for (int i = 0; i < cachedDamage.length; i++) {
				if (cachedDamage[i] >= 310) {
					demonPrayer = new boolean[3];
					switchPrayers(i);
					cachedDamage = new int[3];
				}
			}
		}
	}

	private void resetPrayerTimer() {
		prayerTimer = 16;
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		shieldTimer = 0;
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	private void sendRandomProjectile() {
		Tile tile = new Tile(getX() + Utilities.random(7), getY()
				+ Utilities.random(7), getZ());
		setNextAnimation(new Animation(10918));
		Engine.sendProjectile(this, tile, 1887, 34, 16, 40, 35, 16, 0);
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = Engine.getRegion(regionId)
					.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = Engine.getPlayers().get(npcIndex);
					if (player == null || player.isDead()
							|| player.hasFinished() || !player.isActive()
							|| !player.withinDistance(tile, 3))
						continue;
					player.getPackets().sendGameMessage(
							"The demon's magical attack splashes on you.");
					player.applyHit(new Hit(this, 281, HitLook.MAGIC_DAMAGE, 1));
				}
			}
		}
	}

	public void setFixedAmount(int fixedAmount) {
		this.fixedAmount = fixedAmount;
	}

	public void setFixedCombatType(int fixedCombatType) {
		this.fixedCombatType = fixedCombatType;
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				setFinished(false);
				Engine.addNPC(npc);
				npc.setLastRegionId(0);
				Engine.updateEntityRegion(npc);
				loadMapRegions();
				checkMultiArea();
				shieldTimer = 0;
				fixedCombatType = 0;
				fixedAmount = 0;
			}
		}, getCombatDefinitions().getRespawnDelay() * 600,
				TimeUnit.MILLISECONDS);
	}

	public void switchPrayers(int type) {
		transformIntoNPC(8349 + type);
		demonPrayer[type] = true;
		resetPrayerTimer();
	}

}
