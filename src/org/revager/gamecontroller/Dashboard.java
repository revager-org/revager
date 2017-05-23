package org.revager.gamecontroller;

import java.util.HashMap;

import org.revager.app.model.schema.Finding;

public class Dashboard {

	private final HashMap<Integer, Vote> votes = new HashMap<>();
	private final HashMap<Integer, Vote> votesInQueue = new HashMap<>();

	private int breaks = 0;
	private int yawns = 0;
	private int maxVotes = 0;
	private Finding finding;

	public Dashboard() {
		new ControllerManager(this);
	}

	public void setNumberControllers(int length) {
		this.maxVotes = length;
	}

	public void setFinding(Finding finding) {
		// TODO: save yawn etc to finding.
		this.finding = finding;
	}

	public Finding getFinding() {
		return finding;
	}

	public synchronized void addVoteToQueue(Finding eventFinding, int owner, Vote vote) {
		// TODO: make finding aware.
		synchronized (votesInQueue) {
			votesInQueue.put(owner, vote);
		}
	}

	public synchronized void addVote(Finding eventFinding, int owner, Vote vote) {
		// TODO: make finding aware.
		votes.put(owner, vote);
	}

	public synchronized void addBreak() {
		breaks++;
	}

	public int getBreaks() {
		return breaks;
	}

	public synchronized void addYawn(Finding eventFinding) {
		// TODO: make finding aware.
		yawns++;
	}

	public synchronized void resetFindingRelevant() {
		// TODO: Remove when made finding aware.
		votes.clear();
		votesInQueue.clear();
		yawns = 0;
	}

	public synchronized boolean isVotingComplete(Finding eventFinding) {
		// TODO: make finding aware.
		synchronized (votesInQueue) {
			return votesInQueue.size() == maxVotes;
		}
	}

	public void resetBreak() {
		breaks = 0;
	}

	public int getContinue() {
		// TODO: make finding aware.
		return yawns;
	}

	public int getVotings() {
		// TODO: make finding aware.
		return votes.size();
	}

	public String getFindingTimeText() {
		// TODO: make finding aware.
		return "TODO 10min 10sec";
	}

	public String getBreakText() {
		return "" + breaks;
	}

}
