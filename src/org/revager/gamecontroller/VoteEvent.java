package org.revager.gamecontroller;

public class VoteEvent extends DashBoardEvent {

	private final Vote vote;
	private final int owner;

	public VoteEvent(Dashboard dashboard, int owner, Vote vote) {
		super(dashboard);
		this.vote = vote;
		this.owner = owner;
	}

	@Override
	public boolean waitWithCallback() {
		dashboard.addVoteToQueue(eventFinding, owner, vote);
		return !dashboard.isVotingComplete(eventFinding);
	}

	@Override
	public void callback() {
		dashboard.addVote(eventFinding, owner, vote);
	}

}