package org.revager.gamecontroller;

public class YawnEvent extends DashBoardEvent {

	public YawnEvent(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public void callback() {
		dashboard.addYawn(eventFinding);
	}

}
