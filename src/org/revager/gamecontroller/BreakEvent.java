package org.revager.gamecontroller;

public class BreakEvent extends DashBoardEvent {

	public BreakEvent(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public void callback() {
		dashboard.addBreak();
	}

}
