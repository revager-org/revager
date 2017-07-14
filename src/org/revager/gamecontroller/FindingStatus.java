package org.revager.gamecontroller;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.revager.gui.UI;

public class FindingStatus extends Observable {

	private final ConcurrentHashMap<Integer, Vote> votes = new ConcurrentHashMap<>();
	private AtomicInteger yawns = new AtomicInteger(0);
	private AtomicInteger findingTime = new AtomicInteger(0);

	public String buildVoteCountString() {
		EnumMap<Vote, Integer> voteCountMap = createVoteCountMap();
		int maxCount = -1;
		for (Entry<Vote, Integer> entry : voteCountMap.entrySet()) {
			maxCount = Math.max(maxCount, entry.getValue());
		}

		if (maxCount == 1) {
			maxCount = -1;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");

		for (Entry<Vote, Integer> entry : voteCountMap.entrySet()) {
			if (maxCount == entry.getValue()) {
				if (maxCount == 1) {
					builder.append("<span style=\"font-size:1em;\">");
				} else {
					builder.append("<span style=\"font-size:1em;font-weight: bold;\">");
				}
			} else {
				builder.append("<span style=\"font-size:0.8em;\">");
			}
			builder.append(entry.getValue());
			builder.append("x");
			builder.append(entry.getKey());
			builder.append("; ");
			builder.append("</span>");
		}
		builder.append("</html>");
		return builder.toString();
	}

	private EnumMap<Vote, Integer> createVoteCountMap() {
		EnumMap<Vote, Integer> map = new EnumMap<>(Vote.class);
		for (Vote vote : getVotings()) {
			int count = map.getOrDefault(vote, 0);
			count++;
			map.put(vote, count);
		}
		return map;
	}

	public void addOrRemoveVote(int owner, Vote vote) {
        votes.put(owner, vote);
		setChanged();
		notifyObservers();
	}

	public void addYawn() {
		yawns.getAndIncrement();
	}

	public int getYawn() {
		return yawns.intValue();
	}

	public Collection<Vote> getVotings() {
		return votes.values();
	}

	public void addFindingTime(int findingTime) {
		this.findingTime.addAndGet(findingTime);
	}

	public void resetFindingTime() {
		findingTime.set(0);
	}

	public int getFindingTime() {
		return findingTime.intValue();
	}

	public Vote getVoteForOwner(int owner) {
		return votes.getOrDefault(owner, null);
	}

}
