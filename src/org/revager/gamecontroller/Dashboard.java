package org.revager.gamecontroller;

import static org.revager.app.model.Data.translate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.revager.app.model.schema.Finding;
import org.revager.gui.UI;

public class Dashboard {

	private final List<Finding> findings = new ArrayList<>();
	private int breaks = 0;
	private int maxVotes = 0;
	private Finding finding = new Finding();
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
					if (oldSeconds == 0 || newSeconds == 0) {
						resetTime();
					} else if (oldSeconds < newSeconds) {
						finding.getFindingStatus().addFindingTime(newSeconds - oldSeconds);
					}
				}
			}
		});
	}

	public void setNumberControllers(int length) {
		this.maxVotes = length;
	}

	public void setFinding(Finding finding) {
		if (!findings.contains(finding)) {
			findings.add(finding);
		}
		this.finding = finding;
	}

	public Finding getFinding() {
		return finding;
	}

	public Vote getVoteForOwner(Finding eventFinding, int owner) {
		return eventFinding.getFindingStatus().getVoteForOwner(owner);
	}

	public synchronized void addOrRemoveVote(Finding eventFinding, int owner, Vote vote) {
		eventFinding.getFindingStatus().addOrRemoveVote(owner, vote);
	}

	public synchronized void addBreak() {
		breaks++;
	}

	public int getBreaks() {
		return breaks;
	}

	public void addYawn(Finding eventFinding) {
		eventFinding.getFindingStatus().addYawn();
	}

	private void resetBreak() {
		breaks = 0;
	}

	public int getContinue() {
		return finding.getFindingStatus().getYawn();
	}

	public String getVotingsDetails() {
		FindingStatus findingStatus = finding.getFindingStatus();
		Collection<Vote> votings = findingStatus.getVotings();
		int numberOfVotes = votings.size();
		if (numberOfVotes != maxVotes) {
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append(numberOfVotes);
			builder.append("x <em>(");
			builder.append(translate("waiting for all votes..."));
			builder.append(")</em><html>");
			return builder.toString();
		}
		return findingStatus.buildVoteCountString();
	}

	public int getFindingTime() {
		return finding.getFindingStatus().getFindingTime();
	}

	public String getBreakText() {
		return Integer.toString(breaks);
	}

	public void rumble() {
		controllerManager.rumble();
	}

	private void resetTime() {
		resetBreak();
		for (Finding finding : findings) {
			finding.getFindingStatus().resetFindingTime();
		}
	}

}
