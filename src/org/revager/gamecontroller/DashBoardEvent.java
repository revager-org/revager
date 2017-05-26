package org.revager.gamecontroller;

import java.util.Random;

import org.revager.app.model.schema.Finding;

public abstract class DashBoardEvent {

	private static final Random random = new Random();
	private static final long MIN_WAIT = 1000 * 2L;

	protected final Finding eventFinding;
	protected final Dashboard dashboard;

	public DashBoardEvent(Dashboard dashboard) {
		this.dashboard = dashboard;
		this.eventFinding = dashboard.getFinding();
		Thread thread = new Thread(() -> {
			if (waitWithCallback()) {
				try {
					Thread.sleep(MIN_WAIT + random.nextInt(1000 * 8));
				} catch (InterruptedException e) {
				}
			}
			dashboard.rumble();
			callback();
		});
		thread.start();
	}

	public boolean waitWithCallback() {
		return true;
	}

	public abstract void callback();

}
