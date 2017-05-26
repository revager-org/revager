package org.revager.gamecontroller;

import static org.revager.app.model.Data.translate;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

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

	public Vote getVoteForOwner(Finding eventFinding, int owner) {
		return getFindingStatus(eventFinding).getVoteForOwner(owner);
	}

	public void addVoteToQueue(Finding eventFinding, int owner, Vote vote) {
		getFindingStatus(eventFinding).addVoteToQueue(owner, vote);
	}

	public synchronized void addOrRemoveVote(Finding eventFinding, int owner, Vote vote) {
		getFindingStatus(eventFinding).addOrRemoveVote(owner, vote);
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

	public String getVotingsDetails() {
		Collection<Vote> votings = getFindingStatus().getVotings();
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
		return buildVoteCountString(votings).toString();
	}

	public int getFindingTime() {
		return getFindingStatus().getFindingTime();
	}

	public String getBreakText() {
		return Integer.toString(breaks);
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

	private StringBuilder buildVoteCountString(Collection<Vote> votings) {
		EnumMap<Vote, Integer> voteCountMap = createVoteCountMap(votings);
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");
		float sizeLevel = 1.5f;
		int lastCount = Integer.MAX_VALUE;
		while (!voteCountMap.isEmpty()) {
			Entry<Vote, Integer> max = new SimpleEntry<>(null, 0);
			for (Entry<Vote, Integer> entry : voteCountMap.entrySet()) {
				if (entry.getValue() > max.getValue()) {
					max = entry;
				}
			}
			builder.append("<span style=\"font-size:");
			if (lastCount != max.getValue()) {
				lastCount = Math.min(lastCount, max.getValue());
				sizeLevel = 2 * sizeLevel / 3;
			}
			builder.append(sizeLevel);
			builder.append("em;\">");
			builder.append(max.getValue());
			builder.append("x");
			builder.append(max.getKey());
			builder.append("; ");
			builder.append("</span>");
			voteCountMap.remove(max.getKey());
		}
		builder.append("</html>");
		return builder;
	}

	private EnumMap<Vote, Integer> createVoteCountMap(Collection<Vote> votings) {
		EnumMap<Vote, Integer> map = new EnumMap<>(Vote.class);
		for (Vote vote : votings) {
			int count = map.getOrDefault(vote, 0);
			count++;
			map.put(vote, count);
		}
		return map;
	}

	private FindingStatus getFindingStatus() {
		return getFindingStatus(finding);
	}

	private FindingStatus getFindingStatus(Finding finding) {
		return findingStatuses.getOrDefault(finding, new FindingStatus());
	}

}
