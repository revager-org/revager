package org.revager.gamecontroller;

public class FocusEvent extends DashboardEvent {

	public FocusEvent(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public void callback() {
		dashboard.addFocus(eventFinding);
	}

}
