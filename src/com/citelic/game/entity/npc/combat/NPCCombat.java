package com.citelic.game.entity.npc.combat;

import com.citelic.game.ForceMovement;
import com.citelic.game.engine.Engine;
import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.Entity;
import com.citelic.game.entity.Hit;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.npc.impl.familiar.Familiar;
import com.citelic.game.entity.npc.impl.fightkiln.HarAkenTentacle;
import com.citelic.game.entity.npc.impl.godwars.zaros.Nex;
import com.citelic.game.entity.npc.impl.pest.PestPortal;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.combat.Combat;
import com.citelic.game.entity.player.content.actions.combat.PlayerCombat;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;
import com.citelic.utility.map.MapAreas;

public final class NPCCombat {

	private NPC npc;
	private int combatDelay;
	private Entity target;

	public NPCCombat(NPC npc) {
		this.npc = npc;
	}

	public int getCombatDelay() {
		return combatDelay;
	}

	/*
	 * returns if under combat
	 */
	public boolean process() {
		if (combatDelay > 0)
			combatDelay--;
		if (target != null) {
			if (!checkAll()) {
				removeTarget();
				return false;
			}
			if (combatDelay <= 0)
				combatDelay = combatAttack();
			return true;
		}
		return false;
	}

	/*
	 * return combatDelay
	 */
	private int combatAttack() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return 0;
		// if hes frooze not gonna attack
		if (npc.getFreezeDelay() >= Utilities.currentTimeMillis())
			return 0;
		// check if close to target, if not let it just walk and dont attack
		// this gameticket
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = defs.getAttackStyle();
		if (target instanceof Familiar && Utilities.random(3) == 0) {
			Familiar familiar = (Familiar) target;
			Player player = familiar.getOwner();
			if (player != null) {
				target = player;
				npc.setTarget(target);
			}

		}
		// MAGE_FOLLOW and RANGE_FOLLOW follow close but can attack far unlike
		// melee
		int maxDistance = attackStyle == NPCCombatDefinitions.MELEE ? 0
				: npc instanceof HarAkenTentacle ? 16 : 7;
		if ((!(npc instanceof Nex))
				&& !npc.clipedProjectile(target, maxDistance == 0
						&& !forceCheckClipAsRange(target))) {
			return 0;
		}
		if (npc.hasWalkSteps())
			maxDistance += npc.getRun() ? 2 : 1;
		int size = npc.getSize();
		int targetSize = target.getSize();
		if (!Utilities.isOnRange(npc.getX(), npc.getY(), size, target.getX(),
				target.getY(), targetSize, maxDistance))
			return 0;
		if (Utilities.colides(npc.getX(), npc.getY(), size, target.getX(),
				target.getY(), targetSize))
			return 0;
		addAttackedByDelay(target);
		return CombatScriptsHandler.specialAttack(npc, target);
	}

	protected void doDefenceEmote(Entity target) {
		/*
		 * if (target.getNextAnimation() != null) // if has att emote already
		 * return;
		 */
		target.setNextAnimationNoPriority(new Animation(Combat
				.getDefenceEmote(target)));
	}

	public Entity getTarget() {
		return target;
	}

	public void addAttackedByDelay(Entity target) { // prevents multithread
		// issues

		target.setAttackedBy(npc);
		target.setAttackedByDelay(Utilities.currentTimeMillis()
				+ npc.getCombatDefinitions().getAttackDelay() * 600 + 600); // 8seconds
	}

	public void setTarget(Entity target) {
		this.target = target;
		npc.setNextFaceEntity(target);
		if (!checkAll()) {
			removeTarget();
			return;
		}
	}

	public boolean checkAll() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return false;
		if (npc.isDead() || npc.hasFinished() || npc.isForceWalking()
				|| target.isDead() || target.hasFinished()
				|| npc.getZ() != target.getZ())
			return false;
		if (npc.getFreezeDelay() >= Utilities.currentTimeMillis())
			return true; // if freeze cant move ofc
		int distanceX = npc.getX() - npc.getRespawnTile().getX();
		int distanceY = npc.getY() - npc.getRespawnTile().getY();
		int size = npc.getSize();
		int maxDistance;
		int agroRatio = npc.getCombatDefinitions().getAgroRatio();
		if (!npc.isNoDistanceCheck() && !npc.isCantFollowUnderCombat()) {
			maxDistance = agroRatio > 32 ? agroRatio : 32;
			if (!(npc instanceof Familiar)) {

				if (npc.getMapAreaNameHash() != -1) {
					// if out his area
					if (!MapAreas.isAtArea(npc.getMapAreaNameHash(), npc)
							|| (!npc.canBeAttackFromOutOfArea() && !MapAreas
									.isAtArea(npc.getMapAreaNameHash(), target))) {
						npc.forceWalkRespawnTile();
						return false;
					}
				} else if (distanceX > size + maxDistance
						|| distanceX < -1 - maxDistance
						|| distanceY > size + maxDistance
						|| distanceY < -1 - maxDistance) {
					// if more than 32 distance from respawn place
					npc.forceWalkRespawnTile();
					return false;
				}
			}
			maxDistance = agroRatio > 16 ? agroRatio : 16;
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
			if (distanceX > size + maxDistance || distanceX < -1 - maxDistance
					|| distanceY > size + maxDistance
					|| distanceY < -1 - maxDistance) {
				return false; // if target distance higher 16
			}
		} else {
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
		}
		// checks for no multi area :)
		if (npc instanceof Familiar) {
			Familiar familiar = (Familiar) npc;
			if (!familiar.canAttack(target))
				return false;
		} else {
			if (!npc.isForceMultiAttacked()) {
				if (!target.isAtMultiArea() || !npc.isAtMultiArea()) {
					if (npc.getAttackedBy() != target
							&& npc.getAttackedByDelay() > Utilities
									.currentTimeMillis())
						return false;
					if (target.getAttackedBy() != npc
							&& target.getAttackedByDelay() > Utilities
									.currentTimeMillis())
						return false;
				}
			}
		}
		if (!npc.isCantFollowUnderCombat()) {
			// if is under
			int targetSize = target.getSize();
			/*
			 * if (distanceX < size && distanceX > -targetSize && distanceY <
			 * size && distanceY > -targetSize && !target.hasWalkSteps()) {
			 */
			if (!target.hasWalkSteps()
					&& Utilities.colides(npc.getX(), npc.getY(), size,
							target.getX(), target.getY(), targetSize)) {
				npc.resetWalkSteps();
				if (!npc.addWalkSteps(target.getX() + targetSize, npc.getY())) {
					npc.resetWalkSteps();
					if (!npc.addWalkSteps(target.getX() - size, npc.getY())) {
						npc.resetWalkSteps();
						if (!npc.addWalkSteps(npc.getX(), target.getY()
								+ targetSize)) {
							npc.resetWalkSteps();
							if (!npc.addWalkSteps(npc.getX(), target.getY()
									- size)) {
								return true;
							}
						}
					}
				}
				return true;
			}
			if (npc.getCombatDefinitions().getAttackStyle() == NPCCombatDefinitions.MELEE
					&& targetSize == 1
					&& size == 1
					&& Math.abs(npc.getX() - target.getX()) == 1
					&& Math.abs(npc.getY() - target.getY()) == 1
					&& !target.hasWalkSteps()) {
				if (!npc.addWalkSteps(target.getX(), npc.getY(), 1))
					npc.addWalkSteps(npc.getX(), target.getY(), 1);
				return true;
			}

			int attackStyle = npc.getCombatDefinitions().getAttackStyle();
            if (npc instanceof Nex) {
                Nex nex = (Nex) npc;
                maxDistance = nex.isFollowTarget() ? 0 : 7;
                if (nex.getFlyTime() == 0 && (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)) {
                    npc.resetWalkSteps();
                    npc.addWalkStepsInteract(target.getX(), target.getY(), 2, size, true);
                    if (!npc.hasWalkSteps()) {
                        int[][] dirs = Utilities.getCoordOffsetsNear(size);
                        for (int dir = 0; dir < dirs[0].length; dir++) {
                            final Tile tile = new Tile(new Tile(target.getX() + dirs[0][dir], target.getY() + dirs[1][dir], target.getZ()));
                            if (Engine.isTileFree(tile.getZ(), tile.getX(), tile.getY(), size)) { // if
                                // found
                                // done
                                nex.setFlyTime(4);
                                npc.setNextForceMovement(new ForceMovement(new Tile(npc), 0, tile, 1, Utilities.getMoveDirection(tile.getX() - npc.getX(), tile.getY() - npc.getY())));
                                npc.setNextAnimation(new Animation(17408));
                                npc.setNextTile(tile);
                                return true;
                            }
                        }
                    } else
                        npc.calcFollow(target, 2, true, npc.isIntelligentRouteFinder());
                    return true;
				} else
					// if doesnt need to move more stop moving
					npc.resetWalkSteps();
			} else {
				maxDistance = npc.isForceFollowClose() ? 0
						: (attackStyle == NPCCombatDefinitions.MELEE
								| attackStyle == NPCCombatDefinitions.SPECIAL || attackStyle == NPCCombatDefinitions.SPECIAL2) ? 0
								: 7;
				npc.resetWalkSteps();
				if ((!npc.clipedProjectile(target, maxDistance == 0
						&& !forceCheckClipAsRange(target)))
						|| !Utilities.isOnRange(npc.getX(), npc.getY(), size,
								target.getX(), target.getY(), targetSize,
								maxDistance)) {
					if (!npc.calcFollow(target, npc.getRun() ? 2 : 1, true,
							npc.isIntelligentRouteFinder())
							&& combatDelay < 3
							&& attackStyle == NPCCombatDefinitions.MELEE)
						combatDelay = 3;
					return true;
				}
				// if under target, moves

			}
		}
		return true;
	}

	public void delayHit(NPC npc, int delay, final Entity target,
			final Hit... hits) {
		npc.getCombat().addAttackedByDelay(target);
		EngineTaskManager.schedule(new EngineTask() {

			@Override
			public void run() {
				for (Hit hit : hits) {
					NPC npc = (NPC) hit.getSource();
					if (npc.isDead() || npc.hasFinished() || target.isDead()
							|| target.hasFinished())
						return;
					target.applyHit(hit);
					npc.getCombat().doDefenceEmote(target);
					if (target instanceof Player) {
						Player p2 = (Player) target;
						p2.closeInterfaces();
						if (p2.getCombatDefinitions().isAutoRelatie()
								&& !p2.getActionManager().hasSkillWorking()
								&& !p2.hasWalkSteps())
							p2.getActionManager().setAction(
									new PlayerCombat(npc));
					} else {
						NPC n = (NPC) target;
						if (!n.isUnderCombat()
								|| n.canBeAttackedByAutoRelatie())
							n.setTarget(npc);
					}

				}
			}

		}, delay);
	}

	private boolean forceCheckClipAsRange(Entity target) {
		return target instanceof PestPortal;
	}

	public void addCombatDelay(int delay) {
		combatDelay += delay;
	}

	public void setCombatDelay(int delay) {
		combatDelay = delay;
	}

	public boolean underCombat() {
		return target != null;
	}

	public void removeTarget() {
		this.target = null;
		npc.setNextFaceEntity(null);
	}

	public void reset() {
		combatDelay = 0;
		target = null;
	}

}
