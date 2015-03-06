package com.citelic.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.citelic.game.engine.task.EngineTaskManager;
import com.citelic.networking.ServerChannelHandler;

public final class Logger {

	public static void debug(long processTime) {
		log(Logger.class, "---DEBUG--- start");
		log(Logger.class, "WorldProcessTime: " + processTime);
		log(Logger.class,
				"WorldRunningTasks: " + EngineTaskManager.getTasksCount());
		log(Logger.class,
				"ConnectedChannels: "
						+ ServerChannelHandler.getConnectedChannelsSize());
		log(Logger.class, "---DEBUG--- end");
	}

	public static void handle(Throwable throwable) {
		System.out.println("ERROR! THREAD NAME: "
				+ Thread.currentThread().getName());
		throwable.printStackTrace();
	}

	public static void log(Object classInstance, Object message) {
		log(classInstance.getClass().getSimpleName(), message);
	}

	public static void log(String message) {
		System.out.println(message);
	}

	public static void log(String className, Object message) {
		String text = "[" + className + "]" + " " + message.toString();
		System.out.println(text);
	}

	public static String date() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			Date now = new Date();
			String parsed = format.format(now);
			return parsed;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private Logger() {

	}

}
