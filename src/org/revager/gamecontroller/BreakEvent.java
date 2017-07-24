package org.revager.gamecontroller;

public class BreakEvent extends DashboardEvent {

	public BreakEvent(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public void callback() {
		dashboard.addBreak();
	}

}
