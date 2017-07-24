package org.revager.gamecontroller;

import java.util.Random;

import org.revager.app.model.schema.Finding;

/**
 * Subclass this class to react on game controller events.
 */
public abstract class DashboardEvent {

	private static final Random RANDOM = new Random();
	private static final long MINIMUM_WAIT_MILLIS = 1000L;

	protected final Finding eventFinding;
	protected final Dashboard dashboard;

	public DashboardEvent(Dashboard dashboard) {
		this.dashboard = dashboard;
		this.eventFinding = dashboard.getFinding();
		Thread thread = new Thread(() -> {
			if (waitWithCallback()) {
				long millis = MINIMUM_WAIT_MILLIS + RANDOM.nextInt(1000 * 4);
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
				}
			}
			callback();
		});
		thread.start();
	}

	/**
	 * Specifies whether the {@link #callback()} should be called immediately.
	 */
	public boolean waitWithCallback() {
		return true;
	}

	/**
	 * This method is called when the event should show its effect.
	 */
	public abstract void callback();

}
