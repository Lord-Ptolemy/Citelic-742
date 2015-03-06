package com.citelic.game.entity.player.content.dialogue;

import com.citelic.cache.impl.NPCDefinitions;
import com.citelic.cache.impl.item.ItemDefinitions;
import com.citelic.game.entity.player.Player;
import com.citelic.utility.Utilities;

public abstract class Dialogue {

	public static int NO_EXPRESSION = 9760, SAD = 9764, SAD_TWO = 9768,
			NO_EXPRESSION_TWO = 9772, WHY = 9776;
	public static int SCARED = 9780, MIDLY_ANGRY = 9784, ANGRY = 9788,
			VERY_ANGRY = 9792, ANGRY_TWO = 9796;
	public static int MANIC_FACE = 9800, JUST_LISTEN = 9804,
			CALM_TALKING = 9808, CALM = 9808, LOOK_DOWN = 9812;
	public static int WHAT_THE = 9816, WHAT_THE_TWO = 9820, EYES_WIDE = 9824,
			CROOKED_HEAD = 9828;
	public static int GLANCE_DOWN = 9832, UNSURE = 9836, LISTEN_LAUGH = 9840,
			TALK_SWING = 9844, NORMAL = 9847;
	public static int GOOFY_LAUGH = 9851, NORMAL_STILL = 9855,
			THINKING_STILL = 9859, LOOKING_UP = 9862;

	protected Player player;
	protected byte stage = -1;

	public Dialogue() {

	}

	public Dialogue(Player player2) {
		// TODO Auto-generated constructor stub
	}

	public Object[] parameters;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public abstract void start();

	public abstract void run(int interfaceId, int option);

	public abstract void finish();

	protected final void pause() {
		player.getDialogueManager().pauseDialogue();
	}

	protected final void end() {
		player.getDialogueManager().finishDialogue();
	}

	protected static final short SEND_1_TEXT_INFO = 210;
	protected static final short SEND_2_TEXT_INFO = 211;
	protected static final short SEND_3_TEXT_INFO = 212;
	protected static final short SEND_4_TEXT_INFO = 213;
	protected static final String SEND_DEFAULT_OPTIONS_TITLE = "Select an Option";
	protected static final short SEND_2_OPTIONS = 236;
	protected static final short SEND_3_OPTIONS = 230;
	protected static final short SEND_4_OPTIONS = 237;
	protected static final short SEND_5_OPTIONS = 238;
	protected static final short SEND_2_LARGE_OPTIONS = 229;
	protected static final short SEND_3_LARGE_OPTIONS = 231;
	protected static final short SEND_1_TEXT_CHAT = 241;
	protected static final short SEND_2_TEXT_CHAT = 242;
	protected static final short SEND_3_TEXT_CHAT = 243;
	protected static final short SEND_4_TEXT_CHAT = 244;
	protected static final short SEND_NO_CONTINUE_1_TEXT_CHAT = 245;
	protected static final short SEND_NO_CONTINUE_2_TEXT_CHAT = 246;
	protected static final short SEND_NO_CONTINUE_3_TEXT_CHAT = 247;
	protected static final short SEND_NO_CONTINUE_4_TEXT_CHAT = 248;
	protected static final short SEND_NO_EMOTE = -1;
	protected static final byte IS_NOTHING = -1;
	protected static final byte IS_PLAYER = 0;
	protected static final byte IS_NPC = 1;
	protected static final byte IS_ITEM = 2;

	private static int[] getIComponentsIds(short interId) {
		int[] childOptions;
		switch (interId) {
		case SEND_1_TEXT_INFO:
			childOptions = new int[1];
			childOptions[0] = 1;
			break;
		case SEND_2_TEXT_INFO:
			childOptions = new int[2];
			childOptions[0] = 1;
			childOptions[1] = 2;
			break;
		case SEND_3_TEXT_INFO:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;
		case SEND_4_TEXT_INFO:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_2_LARGE_OPTIONS:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;
		case SEND_3_LARGE_OPTIONS:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_2_OPTIONS:
			childOptions = new int[3];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			break;
		case SEND_3_OPTIONS:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_4_OPTIONS:
			childOptions = new int[5];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			break;
		case SEND_5_OPTIONS:
			childOptions = new int[6];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			childOptions[5] = 5;
			break;
		case SEND_1_TEXT_CHAT:
		case SEND_NO_CONTINUE_1_TEXT_CHAT:
			childOptions = new int[2];
			childOptions[0] = 3;
			childOptions[1] = 4;
			break;
		case SEND_2_TEXT_CHAT:
		case SEND_NO_CONTINUE_2_TEXT_CHAT:
			childOptions = new int[3];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			break;
		case SEND_3_TEXT_CHAT:
		case SEND_NO_CONTINUE_3_TEXT_CHAT:
			childOptions = new int[4];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			break;
		case SEND_4_TEXT_CHAT:
		case SEND_NO_CONTINUE_4_TEXT_CHAT:
			childOptions = new int[5];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			childOptions[4] = 7;
			break;
		default:
			return null;
		}
		return childOptions;
	}

	public boolean sendNPCDialogue(int npcId, int animationId, String... text) {
		return sendEntityDialogue(IS_NPC, npcId, animationId, text);
	}

	public boolean sendPlayerDialogue(int animationId, String... text) {
		return sendEntityDialogue(IS_PLAYER, -1, animationId, text);
	}

	/**
	 * Sends the dialogue as the entity, whether it be a player or NPC.
	 * 
	 * @param type
	 *            The type of dialogue it is.
	 * @param entityId
	 *            The ID of the entity in the world. Used to get player face.
	 * @param animationId
	 *            The animation id of the entity face.
	 * @param text
	 *            The text to show.
	 * @return Sends the chat.
	 */
	public boolean sendEntityDialogue(int type, int entityId, int animationId,
			String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getNPCDefinitions(entityId).name;
		} else if (type == IS_ITEM)
			title = ItemDefinitions.forId(entityId).getName();
		return sendEntityDialogue(type, title, entityId, animationId, text);
	}

	/*
	 * idk what it for
	 */
	public int getP() {
		return 1;
	}

	public static final int OPTION_1 = 11, OPTION_2 = 13, OPTION_3 = 14,
			OPTION_4 = 15, OPTION_5 = 16;

	public boolean sendOptionsDialogue(String title, String... options) {
		int i = 0;
		player.getInterfaceManager().sendChatBoxInterface(1188);
		Object[] params = new Object[options.length + 1];
		params[i++] = options.length;
		for (int j = 0; j < options.length; j++)
			params[options.length - j] = options[j];
		player.getPackets().sendIComponentText(1188, 20, title);
		player.getPackets().sendRunScript(5589, params);
		return true;
	}

	public static boolean sendNPCDialogueNoContinue(Player player, int npcId,
			int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_NPC, npcId, animationId,
				text);
	}

	public static boolean sendPlayerDialogueNoContinue(Player player,
			int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_PLAYER, -1, animationId,
				text);
	}

	/*
	 * 
	 * auto selects title, new dialogues
	 */
	public static boolean sendEntityDialogueNoContinue(Player player, int type,
			int entityId, int animationId, String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getNPCDefinitions(entityId).name;
		} else if (type == IS_ITEM)
			title = ItemDefinitions.forId(entityId).getName();
		return sendEntityDialogueNoContinue(player, type, title, entityId,
				animationId, text);
	}

	public static boolean sendEntityDialogueNoContinue(Player player, int type,
			String title, int entityId, int animationId, String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append(" " + texts[line]);
		String text = builder.toString();
		player.getInterfaceManager().replaceRealChatBoxInterface(1192);
		player.getPackets().sendIComponentText(1192, 16, title);
		player.getPackets().sendIComponentText(1192, 12, text);
		player.getPackets().sendEntityOnIComponent(type == IS_PLAYER, entityId,
				1192, 11);
		if (animationId != -1)
			player.getPackets().sendIComponentAnimation(animationId, 1192, 11);
		return true;
	}

	public static void closeNoContinueDialogue(Player player) {
		player.getInterfaceManager().closeReplacedRealChatBoxInterface();
	}

	/*
	 * new dialogues
	 */
	public boolean sendEntityDialogue(int type, String title, int entityId,
			int animationId, String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++) {
			builder.append(" " + texts[line]);
		}
		String text = builder.toString();
		text = text.replace("@NAME@",
				Utilities.formatPlayerNameForDisplay(player.getUsername()));
		if (type == IS_NPC) {
			player.getInterfaceManager().sendChatBoxInterface(1184);
			player.getPackets().sendIComponentText(1184, 17, title);
			player.getPackets().sendIComponentText(1184, 13, text);
			player.getPackets().sendNPCOnIComponent(1184, 11, entityId);
			if (animationId != -1)
				player.getPackets().sendIComponentAnimation(animationId, 1184,
						11);
		} else if (type == IS_PLAYER) {
			player.getInterfaceManager().sendChatBoxInterface(1191);
			player.getPackets().sendIComponentText(1191, 8, title);
			player.getPackets().sendIComponentText(1191, 17, text);
			player.getPackets().sendPlayerOnIComponent(1191, 15);
			if (animationId != -1)
				player.getPackets().sendIComponentAnimation(animationId, 1191,
						15);
		}
		return true;
	}

	public boolean sendDialogue(String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append((line == 0 ? "<p=" + getP() + ">" : "<br>")
					+ texts[line]);
		String text = builder.toString();
		player.getInterfaceManager().sendChatBoxInterface(1186);
		player.getPackets().sendIComponentText(1186, 1, text);
		return true;
	}

	public boolean sendEntityDialogue(short interId, String[] talkDefinitons,
			byte type, int entityId, int animationId) {
		if (type == IS_PLAYER || type == IS_NPC) { // auto convert to new
			// dialogue all old
			// dialogues
			String[] texts = new String[talkDefinitons.length - 1];
			for (int i = 0; i < texts.length; i++)
				texts[i] = talkDefinitons[i + 1];
			sendEntityDialogue(type, talkDefinitons[0], entityId, animationId,
					texts);
			return true;
		}
		int[] componentOptions = getIComponentsIds(interId);
		if (componentOptions == null)
			return false;
		player.getInterfaceManager().sendChatBoxInterface(interId);
		if (talkDefinitons.length != componentOptions.length)
			return false;
		for (int childOptionId = 0; childOptionId < componentOptions.length; childOptionId++)
			player.getPackets().sendIComponentText(interId,
					componentOptions[childOptionId],
					talkDefinitons[childOptionId]);
		if (type == IS_PLAYER || type == IS_NPC) {
			player.getPackets().sendEntityOnIComponent(type == IS_PLAYER,
					entityId, interId, 2);
			if (animationId != -1)
				player.getPackets().sendIComponentAnimation(animationId,
						interId, 2);
		} else if (type == IS_ITEM)
			player.getPackets().sendItemOnIComponent(interId, 2, entityId,
					animationId);
		return true;
	}

}
