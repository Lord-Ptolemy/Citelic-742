package com.citelic.game.entity.player.content.dialogue.impl.actions;

import com.citelic.game.engine.Engine;
import com.citelic.game.entity.Animation;
import com.citelic.game.entity.npc.NPC;
import com.citelic.game.entity.player.content.dialogue.Dialogue;
import com.citelic.game.entity.player.content.miscellaneous.MarkerPlant;
import com.citelic.game.map.tile.Tile;

public class MarkerPlantD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Which way should the Marker point?",
				"The same way as me", "North/South/East/West",
				"Diagonal directions...");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				if (player.getDirection() == 8192) {
					NPC npc = new NPC(9154, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
				} else if (player.getDirection() == 0) {
					NPC npc = new NPC(9151, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
				} else if (player.getDirection() == 12288) {
					NPC npc = new NPC(9157, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
				} else if (player.getDirection() == 4096) {
					NPC npc = new NPC(9158, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
					// 2 digts
				} else if (player.getDirection() == 10240) {
					NPC npc = new NPC(9152, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
				} else if (player.getDirection() == 14366) {
					NPC npc = new NPC(9155, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
				} else if (player.getDirection() == 6144) {
					NPC npc = new NPC(9153, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
				} else if (player.getDirection() == 2048) {
					NPC npc = new NPC(9156, new Tile(player.getX(),
							player.getY(), player.getZ()), 0, false, false);
					Engine.addNPC(npc);
					doMT(npc);
				}
				end();
			} else if (componentId == OPTION_2) {
				stage = 2;
				sendOptionsDialogue("Which way should the Marker point?",
						"North", "South", "East", "West", "Back...");
			} else {
				stage = 3;
				sendOptionsDialogue("Which way should the Marker point?",
						"North-east", "South-east", "South-west", "North-west",
						"Back...");
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				NPC npc = new NPC(9154, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc);
				doMT(npc);
				end();
				break;
			case OPTION_2:
				NPC npc1 = new NPC(9151, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc1);
				doMT(npc1);
				end();
				break;
			case OPTION_3:
				NPC npc2 = new NPC(9157, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc2);
				doMT(npc2);
				end();
				break;
			case OPTION_4:
				NPC npc3 = new NPC(9158, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc3);
				doMT(npc3);
				end();
				break;
			case OPTION_5:
				stage = -1;
				sendOptionsDialogue("Which way should the Marker point?",
						"The same way as me", "North/South/East/West",
						"Diagonal directions...");
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				NPC npc = new NPC(9152, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc);
				doMT(npc);
				end();
				break;
			case OPTION_2:
				NPC npc1 = new NPC(9155, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc1);
				doMT(npc1);
				end();
				break;
			case OPTION_3:
				NPC npc2 = new NPC(9156, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc2);
				doMT(npc2);
				end();
				break;
			case OPTION_4:
				NPC npc3 = new NPC(9153, new Tile(player.getX(), player.getY(),
						player.getZ()), 0, false, false);
				Engine.addNPC(npc3);
				doMT(npc3);
				end();
				break;
			case OPTION_5:
				stage = -1;
				sendOptionsDialogue("Which way should the Marker point?",
						"The same way as me", "North/South/East/West",
						"Diagonal directions...");
				break;
			}
			break;
		case 50:
		default:
			end();
			break;
		}
	}

	public void doMT(NPC npc) {
		if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
			if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
				if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1))
					player.addWalkSteps(player.getX(), player.getY() - 1, 1);
		npc.setNextAnimation(new Animation(11905));
		MarkerPlant markerPlant = new MarkerPlant(player, npc);
		if (markerPlant.plantPlant())
			return;
	}

	@Override
	public void finish() {

	}

}