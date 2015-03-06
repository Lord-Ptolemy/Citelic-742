package com.citelic.game.entity.player.content.dialogue;

import java.util.HashMap;

import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

public final class DialogueHandler {

	private static final HashMap<Object, Class<? extends Dialogue>> handledDialogues = new HashMap<Object, Class<? extends Dialogue>>();

	private DialogueHandler() {

	}

	public static final Dialogue getDialogue(Object key) {
		if (key instanceof Dialogue)
			return (Dialogue) key;
		Class<? extends Dialogue> classD = DialogueHandler.handledDialogues
				.get(key);
		if (classD == null)
			return null;
		try {
			return classD.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static final void init() {
		try {
			Class<Dialogue>[] classes = Utilities
					.getClasses("com.citelic.game.entity.player.content.dialogue.impl");
			for (Class<Dialogue> c : classes) {
				if (c.isAnonymousClass()) {
					continue;
				}
				DialogueHandler.handledDialogues.put(c.getSimpleName(), c);
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		DialogueHandler.handledDialogues.clear();
		DialogueHandler.init();
	}
}
