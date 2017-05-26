package org.revager.gamecontroller;

import java.util.HashMap;

import org.revager.app.model.schema.Finding;
import org.revager.gui.UI;

public class Dashboard {

	private final HashMap<Finding, FindingStatus> findingStatuses = new HashMap<>();

	private int breaks = 0;
	private int maxVotes = 0;
	private Finding finding;
	private ControllerManager controllerManager;

	public Dashboard() {
		controllerManager = new ControllerManager(this);
		UI.getInstance().getProtocolClockWorker().addPropertyChangeListener(evt -> {
			Object newValue = evt.getNewValue();
			if (newValue instanceof Integer) {
				Object oldValue = evt.getOldValue();
				if (oldValue instanceof Integer) {
					int newSeconds = (int) newValue;
					int oldSeconds = (int) oldValue;
					if (oldSeconds == 0) {
						resetTime();
					} else if (oldSeconds < newSeconds) {
						getFindingStatus().addFindingTime(newSeconds - oldSeconds);
					}
				}
			}
		});
	}

	public void setNumberControllers(int length) {
		this.maxVotes = length;
	}

	public void setFinding(Finding finding) {
		findingStatuses.putIfAbsent(finding, new FindingStatus());
		this.finding = finding;

	}

	public Finding getFinding() {
		return finding;
	}

	public void addVoteToQueue(Finding eventFinding, int owner, Vote vote) {
		getFindingStatus(eventFinding).addVoteToQueue(owner, vote);
	}

	public synchronized void addVote(Finding eventFinding, int owner, Vote vote) {
		getFindingStatus(eventFinding).addVote(owner, vote);
	}

	public synchronized void addBreak() {
		breaks++;
	}

	public int getBreaks() {
		return breaks;
	}

	public void addYawn(Finding eventFinding) {
		getFindingStatus(eventFinding).addYawn();
	}

	public synchronized boolean isVotingComplete(Finding eventFinding) {
		return getFindingStatus(eventFinding).isVotingComplete(maxVotes);
	}

	private void resetBreak() {
		breaks = 0;
	}

	public int getContinue() {
		return getFindingStatus().getYawn();
	}

	public int getVotings() {
		return getFindingStatus().getVotings();
	}

	public int getFindingTime() {
		return getFindingStatus().getFindingTime();
	}

	public String getBreakText() {
		return "" + breaks;
	}

	public void rumble() {
		controllerManager.rumble();
	}

	private void resetTime() {
		resetBreak();
		for (FindingStatus findingStatus : findingStatuses.values()) {
			findingStatus.resetFindingTime();
		}
	}

	private FindingStatus getFindingStatus() {
		return getFindingStatus(finding);
	}

	private FindingStatus getFindingStatus(Finding finding) {
		return findingStatuses.getOrDefault(finding, new FindingStatus());
	}

}
