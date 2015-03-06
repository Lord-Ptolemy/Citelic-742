package com.citelic.game.engine.task;

public abstract class EngineTask implements Runnable {

	protected boolean needRemove;

	public final void stop() {
		needRemove = true;
	}
}
