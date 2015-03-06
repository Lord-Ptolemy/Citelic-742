package com.citelic.game.entity.npc.impl.others;

import java.util.List;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.skills.hunter.BoxAction.HunterNPC;
import com.citelic.game.entity.player.content.actions.skills.hunter.Hunter;
import com.citelic.game.entity.player.content.actions.skills.hunter.Hunter.DynamicFormula;
import com.citelic.game.map.objects.GameObject;
import com.citelic.game.map.objects.OwnedObjectManager;
import com.citelic.game.map.objects.OwnedObjectManager.ConvertEvent;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class ItemHunterNPC extends NPC {

	public ItemHunterNPC(int id, Tile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	private void failedAttempt(GameObject object, HunterNPC info) {
		setNextAnimation(info.getFailCatchAnim());
		if (OwnedObjectManager.convertIntoObject(
				object,
				new GameObject(info.getFailedTransformObjectId(), 10, 0, this
						.getX(), this.getY(), this.getZ()), new ConvertEvent() {
					@Override
					public boolean canConvert(Player player) {
						// if(larupia)
						// blablabla
						return true;
					}
				})) {
		}
	}

	@Override
	public void processNPC() {
		super.processNPC();
		List<GameObject> objects = Engine.getRegion(getRegionId())
				.getSpawnedObjects();
		if (objects != null) {
			final HunterNPC info = HunterNPC.forId(getId());
			int objectId = info.getEquipment().getObjectId();
			for (final GameObject object : objects) {
				if (object.getId() == objectId) {
					if (OwnedObjectManager.convertIntoObject(
							object,
							new GameObject(info
									.getSuccessfulTransformObjectId(), 10, 0,
									this.getX(), this.getY(), this.getZ()),
							new ConvertEvent() {
								@Override
								public boolean canConvert(Player player) {
									if (player == null
											|| player.getLockDelay() > Utilities
													.currentTimeMillis())
										return false;
									if (Hunter.isSuccessful(player,
											info.getLevel(),
											new DynamicFormula() {
												@Override
												public int getExtraProbablity(
														Player player) {
													// bait here
													return 1;
												}
											})) {
										failedAttempt(object, info);
										return false;
									} else {
										setNextAnimation(info
												.getSuccessCatchAnim());
										return true;
									}
								}
							})) {
						setRespawnTask(); // auto finishes
					}
				}
			}
		}
	}
}