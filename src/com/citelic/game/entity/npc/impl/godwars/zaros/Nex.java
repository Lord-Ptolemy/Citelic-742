package com.citelic.game.entity.npc.impl.godwars.zaros;

import java.util.ArrayList;

import com.citelic.game.ForceTalk;
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
import com.citelic.game.entity.player.content.controllers.impl.distractions.godwars.ZarosGodwars;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public final class Nex extends NPC {

	private boolean followTarget;
	private int stage;
	private int minionStage;
	private int flyTime;
	private long lastVirus;
	private boolean embracedShadow;
	private boolean trapsSettedUp;
	private long lastSiphon;
	private boolean doingSiphon;
	private NPC[] bloodReavers;
	private int switchPrayersDelay;

	public Nex(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCantInteract(true);
		setCapDamage(500);
		setLureDelay(3000);
		bloodReavers = new NPC[3];
	}

	public ArrayList<Entity> calculatePossibleTargets(Tile current,
			Tile position, boolean northSouth) {
		ArrayList<Entity> list = new ArrayList<Entity>();
		for (Entity e : this.getPossibleTargets()) {
			if (e.withinArea(current.getX(), current.getY(), position.getX()
					+ (northSouth ? 2 : 0), position.getY()
					+ (!northSouth ? 2 : 0))

					|| e.withinArea(position.getX(), position.getY(),
							current.getX() + (northSouth ? 2 : 0),
							current.getY() + (!northSouth ? 2 : 0))) {
				list.add(e);
			}
		}
		return list;
	}

	public int getAttacksStage() {
		return getStage();
	}

	public NPC[] getBloodReavers() {
		return bloodReavers;
	}

	public int getFlyTime() {
		return flyTime;
	}

	public void setFlyTime(int flyTime) {
		this.flyTime = flyTime;
	}

	public long getLastSiphon() {
		return lastSiphon;
	}

	public void setLastSiphon(long lastSiphon) {
		this.lastSiphon = lastSiphon;
	}

	public long getLastVirus() {
		return lastVirus;
	}

	public void setLastVirus(long lastVirus) {
		this.lastVirus = lastVirus;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		return ZarosGodwars.getPossibleTargets();
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (doingSiphon) {
			hit.setHealHit();
		}
		if (getId() == 13449 && hit.getLook() == HitLook.MELEE_DAMAGE) {
			Entity source = hit.getSource();
			if (source != null) {
				int deflectedDamage = (int) (hit.getDamage() * 0.1);
				hit.setDamage((int) (hit.getDamage() * source
						.getMeleePrayerMultiplier()));
				if (deflectedDamage > 0) {
					source.applyHit(new Hit(this, deflectedDamage,
							HitLook.REFLECTED_DAMAGE));
				}
			}
		}
		super.handleIngoingHit(hit);
	}

	public boolean isDoingSiphon() {
		return doingSiphon;
	}

	public void setDoingSiphon(boolean doingSiphon) {
		this.doingSiphon = doingSiphon;
	}

	public boolean isEmbracedShadow() {
		return embracedShadow;
	}

	public void setEmbracedShadow(boolean embracedShadow) {
		this.embracedShadow = embracedShadow;
	}

	public boolean isFollowTarget() {
		return followTarget;
	}

	public void setFollowTarget(boolean followTarget) {
		this.followTarget = followTarget;
	}

	public boolean isTrapsSettedUp() {
		return trapsSettedUp;
	}

	public void setTrapsSettedUp(boolean trapsSettedUp) {
		this.trapsSettedUp = trapsSettedUp;
	}

	public void killBloodReavers() {
		for (int index = 0; index < bloodReavers.length; index++) {
			if (bloodReavers[index] == null) {
				continue;
			}
			NPC npc = bloodReavers[index];
			bloodReavers[index] = null;
			if (npc.isDead())
				return;
			this.heal(npc.getHitpoints());
			npc.sendDeath(this);
		}
	}

	public void moveNextStage() {
		if (getStage() == 0 && minionStage == 1) {
			setCapDamage(30000);
			setNextForceTalk(new ForceTalk("Darken my shadow!"));
			Engine.sendProjectile(ZarosGodwars.umbra, this, 2244, 18, 18, 60,
					30, 0, 0);
			getCombat().addCombatDelay(1);
			setStage(1);
			playSound(3302, 2);
		} else if (getStage() == 1 && minionStage == 2) {
			setCapDamage(30000);
			setNextForceTalk(new ForceTalk("Flood my lungs with blood!"));
			Engine.sendProjectile(ZarosGodwars.cruor, this, 2244, 18, 18, 60,
					30, 0, 0);
			getCombat().addCombatDelay(1);
			setStage(2);
			playSound(3306, 2);
		} else if (getStage() == 2 && minionStage == 3) {
			setCapDamage(30000);
			killBloodReavers();
			setNextForceTalk(new ForceTalk("Infuse me with the power of ice!"));
			Engine.sendProjectile(ZarosGodwars.glacies, this, 2244, 18, 18, 60,
					30, 0, 0);
			getCombat().addCombatDelay(1);
			setStage(3);
			playSound(3303, 2);
		} else if (getStage() == 3 && minionStage == 4) {
			setCapDamage(30000);
			setNextForceTalk(new ForceTalk("NOW, THE POWER OF ZAROS!"));
			setNextAnimation(new Animation(17410));
			setNextGraphics(new Graphics(3376));
			getCombat().addCombatDelay(1);
			this.heal(6000);
			setStage(4);
			playSound(3312, 2);
		}
	}

	@Override
	public void processNPC() {
		if (flyTime > 0) {
			flyTime--;
		}
		if (getStage() == 0 && minionStage == 0 && getHitpoints() <= 24000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Fumus, don't fail me!"));
			getCombat().addCombatDelay(1);
			ZarosGodwars.breakFumusBarrier();
			playSound(3321, 2);
			minionStage = 1;
		} else if (getStage() == 1 && minionStage == 1
				&& getHitpoints() <= 18000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Umbra, don't fail me!"));
			getCombat().addCombatDelay(1);
			ZarosGodwars.breakUmbraBarrier();
			playSound(3307, 2);
			minionStage = 2;
		} else if (getStage() == 2 && minionStage == 2
				&& getHitpoints() <= 12000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Cruor, don't fail me!"));
			getCombat().addCombatDelay(1);
			ZarosGodwars.breakCruorBarrier();
			playSound(3298, 2);
			minionStage = 3;
		} else if (getStage() == 3 && minionStage == 3
				&& getHitpoints() <= 6000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Glacies, don't fail me!"));
			getCombat().addCombatDelay(1);
			ZarosGodwars.breakGlaciesBarrier();
			playSound(3327, 2);
			minionStage = 4;
		} else if (getStage() == 4 && minionStage == 4) {
			if (switchPrayersDelay > 0) {
				switchPrayersDelay--;
			} else {
				switchPrayers();
				resetSwitchPrayersDelay();
			}
		}
		if (isDead() || doingSiphon || isCantInteract())
			return;
		if (!getCombat().process()) {
			checkAgressivity();
		}
	}

	public void resetSwitchPrayersDelay() {
		switchPrayersDelay = 35; // 25sec
	}

	@Override
	public void sendDeath(Entity source) {
		transformIntoNPC(13450);
		final NPCCombatDefinitions defs = getCombatDefinitions();
		EngineTaskManager.schedule(new EngineTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					Nex.this.setNextAnimation(new Animation(defs
							.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					Nex.this.drop();
					Nex.this.finish();
					ZarosGodwars.endWar();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		setNextForceTalk(new ForceTalk("Taste my wrath!"));
		playSound(3323, 2);
		final NPC target = this;
		EngineTaskManager.schedule(new EngineTask() {
			@Override
			public void run() {
				Nex.this.setNextGraphics(new Graphics(2259));
				ArrayList<Entity> possibleTargets = Nex.this
						.getPossibleTargets();
				if (possibleTargets != null) {
					for (Entity entity : possibleTargets) {
						if (entity == null || entity.isDead()
								|| entity.hasFinished()
								|| !entity.withinDistance(target, 10)) {
							continue;
						}
						Engine.sendProjectile(target, new Tile(
								Nex.this.getX() + 2, Nex.this.getY() + 2,
								Nex.this.getZ()), 2260, 24, 0, 41, 35, 30, 0);
						Engine.sendProjectile(target,
								new Tile(Nex.this.getX() + 2, Nex.this.getY(),
										Nex.this.getZ()), 2260, 41, 0, 41, 35,
								30, 0);
						Engine.sendProjectile(target, new Tile(
								Nex.this.getX() + 2, Nex.this.getY() - 2,
								Nex.this.getZ()), 2260, 41, 0, 41, 35, 30, 0);
						Engine.sendProjectile(target, new Tile(
								Nex.this.getX() - 2, Nex.this.getY() + 2,
								Nex.this.getZ()), 2260, 41, 0, 41, 35, 30, 0);
						Engine.sendProjectile(target,
								new Tile(Nex.this.getX() - 2, Nex.this.getY(),
										Nex.this.getZ()), 2260, 41, 0, 41, 35,
								30, 0);
						Engine.sendProjectile(target, new Tile(
								Nex.this.getX() - 2, Nex.this.getY() - 2,
								Nex.this.getZ()), 2260, 41, 0, 41, 35, 30, 0);
						Engine.sendProjectile(target, new Tile(Nex.this.getX(),
								Nex.this.getY() + 2, Nex.this.getZ()), 2260,
								41, 0, 41, 35, 30, 0);
						Engine.sendProjectile(target, new Tile(Nex.this.getX(),
								Nex.this.getY() - 2, Nex.this.getZ()), 2260,
								41, 0, 41, 35, 30, 0);
						entity.applyHit(new Hit(target, Utilities
								.getRandom(600), HitLook.REGULAR_DAMAGE));
					}
				}
			}
		});
	}

	public void sendVirusAttack(ArrayList<Entity> hitedEntitys,
			ArrayList<Entity> possibleTargets, Entity infected) {
		for (Entity t : possibleTargets) {
			if (hitedEntitys.contains(t)) {
				continue;
			}
			if (Utilities.getDistance(t.getX(), t.getY(), infected.getX(),
					infected.getY()) <= 1) {
				t.setNextForceTalk(new ForceTalk("*Cough*"));
				t.applyHit(new Hit(this, Utilities.getRandom(100),
						HitLook.REGULAR_DAMAGE));
				hitedEntitys.add(t);
				sendVirusAttack(hitedEntitys, possibleTargets, infected);
			}
		}
		playSound(3296, 2);
	}

	@Override
	public void setNextAnimation(Animation nextAnimation) {
		if (doingSiphon)
			return;
		super.setNextAnimation(nextAnimation);
	}

	@Override
	public void setNextGraphics(Graphics nextGraphic) {
		if (doingSiphon)
			return;
		super.setNextGraphics(nextGraphic);
	}

	public void switchPrayers() {
		transformIntoNPC(getId() == 13449 ? 13447 : getId() + 1);
	}
}
