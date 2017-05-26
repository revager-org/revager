package org.revager.gamecontroller;

import java.util.Random;

import org.revager.app.model.schema.Finding;

public abstract class DashBoardEvent {

	private static final Random RANDOM = new Random();
	private static final long MINIMUM_WAIT_MILLIS = 1000 * 2L;
	private static final long CHECK_STEPS_MILLIS = 100L;

	protected final Finding eventFinding;
	protected final Dashboard dashboard;

	public DashBoardEvent(Dashboard dashboard) {
		this.dashboard = dashboard;
		this.eventFinding = dashboard.getFinding();
		Thread thread = new Thread(() -> {
			long millis = MINIMUM_WAIT_MILLIS + RANDOM.nextInt(1000 * 4);
			while (0 < millis) {
				millis -= CHECK_STEPS_MILLIS;
				try {
					Thread.sleep(CHECK_STEPS_MILLIS);
				} catch (InterruptedException e) {
				}
				if (!waitWithCallback()) {
					break;
				}
			}
			callback();
			dashboard.rumble();
		});
		thread.start();

	}

	public boolean waitWithCallback() {
		return true;
	}

	public abstract void callback();

}
