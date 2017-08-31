package org.revager.gamecontroller;

/**
 * React on focus key press events.
 */
public class FocusEvent extends DashboardEvent {

	public FocusEvent(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public void callback() {
		dashboard.addFocus(eventFinding);
	}

}
