package com.citelic.game.entity.player.content.controllers;

import java.util.HashMap;

import com.citelic.utility.Logger;
import com.citelic.utility.Utilities;

/**
 * @author Ridiculous <knol@outlook.com>
 */

public final class ControllerHandler {

	private static final HashMap<Object, Class<? extends Controller>> handledControllers = new HashMap<Object, Class<? extends Controller>>();

	private ControllerHandler() {

	}

	public static final Controller getController(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<? extends Controller> classD = ControllerHandler.handledControllers
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
			Class<Controller>[] classes = Utilities
					.getClasses("com.citelic.game.entity.player.content.controllers.impl");
			for (Class<Controller> c : classes) {
				if (c.isAnonymousClass()) {
					continue;
				}
				ControllerHandler.handledControllers.put(c.getSimpleName(), c);
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		ControllerHandler.handledControllers.clear();
		ControllerHandler.init();
	}
}
