package com.citelic.game.engine.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EngineTaskManager {

	private static final class EngineTaskInformation {

		private EngineTask task;
		private int continueMaxCount;
		private int continueCount;

		public EngineTaskInformation(EngineTask task, int continueCount,
				int continueMaxCount) {
			this.task = task;
			this.continueCount = continueCount;
			this.continueMaxCount = continueMaxCount;
			if (continueMaxCount == -1)
				task.needRemove = true;
		}
	}

	private static final List<EngineTaskInformation> tasks = Collections
			.synchronizedList(new LinkedList<EngineTaskInformation>());

	public static int getTasksCount() {
		return tasks.size();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100000; i++)
			schedule(new EngineTask() {

				@Override
				public void run() {

				}

			});
		processTasks();
		for (int i = 0; i < 100000; i++)
			schedule(new EngineTask() {

				@Override
				public void run() {

				}

			});
		processTasks();
		for (int i = 0; i < 100000; i++)
			schedule(new EngineTask() {

				@Override
				public void run() {

				}

			});
		processTasks();

	}

	public static void processTasks() {
		for (EngineTaskInformation taskInformation : tasks
				.toArray(new EngineTaskInformation[tasks.size()])) {
			if (taskInformation.continueCount > 0) {
				taskInformation.continueCount--;
				continue;
			}
			taskInformation.task.run();
			if (taskInformation.task.needRemove)
				tasks.remove(taskInformation);
			else
				taskInformation.continueCount = taskInformation.continueMaxCount;
		}
	}

	public static void schedule(EngineTask task) {
		if (task == null)
			return;
		tasks.add(new EngineTaskInformation(task, 0, -1));
	}

	public static void schedule(EngineTask task, int delayCount) {
		if (task == null || delayCount < 0)
			return;
		tasks.add(new EngineTaskInformation(task, delayCount, -1));
	}

	public static void schedule(EngineTask task, int delayCount, int periodCount) {
		if (task == null || delayCount < 0 || periodCount < 0)
			return;
		tasks.add(new EngineTaskInformation(task, delayCount, periodCount));
	}

	private EngineTaskManager() {

	}
}
