package org.revager.gamecontroller;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A resettable timeout class.
 */
public abstract class Timeout {

	private final int timeoutSeconds;
	private final AtomicInteger secondsLeft;
	private final Thread thread;
	private AtomicBoolean wasReset = new AtomicBoolean(false);

	public Timeout(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
		secondsLeft = new AtomicInteger(timeoutSeconds);

		thread = new Thread(() -> {
			while (true) {
				if (secondsLeft.get() == 0) {
					timeout();
					secondsLeft.decrementAndGet();
					continue;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				if (wasReset.get()) {
					wasReset.set(false);
				} else {
					secondsLeft.decrementAndGet();
				}
			}

		});
		thread.start();
	}

	public abstract void timeout();

	public void reset() {
		wasReset.set(true);
		secondsLeft.set(timeoutSeconds);
	}

}
