package com.citelic.game.entity.player.content.transportation;

import java.io.Serializable;

import com.citelic.game.engine.task.EngineTask;
import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.game.entity.Graphics;
import com.citelic.game.entity.player.Player;
import com.citelic.game.entity.player.content.actions.HomeTeleport;
import com.citelic.game.entity.player.content.cutscenes.Cutscene;
import com.citelic.game.map.objects.GameObject;

public class LodeStones implements Serializable {

	private static final long serialVersionUID = -2414976654365223059L;

	private static final int[] CONFIG_IDS = new int[] { 10900, 10901, 10902,
			10903, 10904, 10905, 10906, 10907, 10908, 10909, 10910, 10911,
			10912, 2448, 358 };

	private transient Player player;
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public void sendCutscene(final GameObject object) {
		EngineTaskManager.schedule(new EngineTask() {
			int ticks = 0;

			@Override
			public void run() {
				if (ticks == 0) {
					player.lock();
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, player.getX() - 6),
							Cutscene.getY(player, player.getY()), 3000);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, object.getX()),
							Cutscene.getY(player, object.getY()), 50);
				} else if (ticks == 3) {
					player.getPackets().sendResetCamera();
					player.unlock();
					player.stopAll();
				}
				ticks++;
			}
		}, 0, 1);
	}

	/**
	 * Handles the interface of the lodestone network. Checks if the player is
	 * able to teleport to the selected lodestone.
	 * 
	 * @param componentId
	 */
	public void handleButtons(int componentId) {
		player.stopAll();
		switch (componentId) {
		case 7:
			if (!player.getActivatedLodestones()[14]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.BANDIT_CAMP_LODE_STONE);
			break;
		case 39:
			if (!player.getActivatedLodestones()[13]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.LUNAR_ISLE_LODE_STONE);
			break;
		case 40:
			if (!player.getActivatedLodestones()[0]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.ALKARID_LODE_STONE);
			break;
		case 41:
			if (!player.getActivatedLodestones()[1]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.ARDOUGNE_LODE_STONE);
			break;
		case 42:
			player.setLodeStoneTile(HomeTeleport.BURTHORPE_LODE_STONE);
			break;
		case 43:
			if (!player.getActivatedLodestones()[3]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.CATHERBAY_LODE_STONE);
			break;
		case 44:
			if (!player.getActivatedLodestones()[4]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.DRAYNOR_VILLAGE_LODE_STONE);
			break;
		case 45:
			if (!player.getActivatedLodestones()[5]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.EDGEVILLE_LODE_STONE);
			break;
		case 46:
			if (!player.getActivatedLodestones()[6]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.FALADOR_LODE_STONE);
			break;
		case 47:
			player.setLodeStoneTile(HomeTeleport.LUMBRIDGE_LODE_STONE);
			break;
		case 48:
			if (!player.getActivatedLodestones()[8]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.PORT_SARIM_LODE_STONE);
			break;
		case 49:
			if (!player.getActivatedLodestones()[9]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.SEERS_VILLAGE_LODE_STONE);
			break;
		case 50:
			if (!player.getActivatedLodestones()[10]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.TAVERLY_LODE_STONE);
			break;
		case 51:
			if (!player.getActivatedLodestones()[11]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.VARROCK_LODE_STONE);
			break;
		case 52:
			if (!player.getActivatedLodestones()[12]) {
				player.getPackets().sendGameMessage(
						"You must activate this lodestone to teleport to it.");
				return;
			}
			player.setLodeStoneTile(HomeTeleport.YANILLE_LODE_STONE);
			break;
		}
		if (player.getLodeStoneTile() != null) {
			player.getActionManager().setAction(new HomeTeleport(player.getLodeStoneTile()));
		}
	}

	/**
	 * Checks the object id then sends the necessary config. Activates the
	 * lodestone for the player.
	 * 
	 * @param object
	 */
	public boolean processLodestone(GameObject object) {
		switch (object.getId()) {
		case 69827:
			player.getPackets().sendConfigByFile(CONFIG_IDS[14], 190);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[14] = true;
			sendCutscene(object);
			break;
		case 69828:
			player.getPackets().sendConfigByFile(CONFIG_IDS[13], 190);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[13] = true;
			sendCutscene(object);
			break;
		case 69829:
			player.getPackets().sendConfigByFile(CONFIG_IDS[0], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[0] = true;
			sendCutscene(object);
			break;
		case 69830:
			player.getPackets().sendConfigByFile(CONFIG_IDS[1], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[1] = true;
			sendCutscene(object);
			break;
		case 69831:
			player.getPackets().sendConfigByFile(CONFIG_IDS[2], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[2] = true;
			sendCutscene(object);
			break;
		case 69832:
			player.getPackets().sendConfigByFile(CONFIG_IDS[3], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[3] = true;
			sendCutscene(object);
			break;
		case 69833:
			sendCutscene(object);
			player.getPackets().sendConfigByFile(CONFIG_IDS[4], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[4] = true;
			sendCutscene(object);
			break;
		case 69834:
			sendCutscene(object);
			player.getPackets().sendConfigByFile(CONFIG_IDS[5], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[5] = true;
			sendCutscene(object);
			break;
		case 69835:
			sendCutscene(object);
			player.getPackets().sendConfigByFile(CONFIG_IDS[6], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[6] = true;
			sendCutscene(object);
			break;
		case 69837:
			sendCutscene(object);
			player.getPackets().sendConfigByFile(CONFIG_IDS[8], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[8] = true;
			sendCutscene(object);
			break;
		case 69838:
			player.getPackets().sendConfigByFile(CONFIG_IDS[9], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[9] = true;
			sendCutscene(object);
			break;
		case 69839:
			player.getPackets().sendConfigByFile(CONFIG_IDS[10], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[10] = true;
			sendCutscene(object);
			break;
		case 69840:
			sendCutscene(object);
			player.getPackets().sendConfigByFile(CONFIG_IDS[11], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[11] = true;
			sendCutscene(object);
			break;
		case 69841:
			player.getPackets().sendConfigByFile(CONFIG_IDS[12], 1);
			player.getPackets().sendGraphics(new Graphics(3019), object);
			player.getActivatedLodestones()[12] = true;
			sendCutscene(object);
			break;
		}
		return false;
	}

	/**
	 * Checks if the player has unlocked the lodestone during login.
	 * 
	 */
	public void checkActivation() {
		player.getPackets().sendConfigByFile(10907, 1);
		player.getPackets().sendConfigByFile(10902, 1);
		for (int x = 0; x <= 12; x++) {
			if (player.getActivatedLodestones()[x] == true) {
				player.getPackets().sendConfigByFile(CONFIG_IDS[x], 1);
			}
		}
		if (player.getActivatedLodestones()[13] == true) {
			player.getPackets().sendConfigByFile(CONFIG_IDS[13], 190);
		}
		if (player.getActivatedLodestones()[14] == true) {
			player.getPackets().sendConfigByFile(CONFIG_IDS[14], 15);
		}
	}

}
