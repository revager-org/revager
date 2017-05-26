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
		if (dashboard.getVoteForOwner(eventFinding, owner) != null) {
			return false;
		}
		dashboard.addVoteToQueue(eventFinding, owner, vote);
		return !dashboard.isVotingComplete(eventFinding);
	}

	@Override
	public void callback() {
		dashboard.addOrRemoveVote(eventFinding, owner, vote);
	}

}
