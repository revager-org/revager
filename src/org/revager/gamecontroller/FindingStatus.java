package org.revager.gamecontroller;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FindingStatus extends Observable {

	private final ConcurrentHashMap<Integer, Vote> votes = new ConcurrentHashMap<>();
	private AtomicInteger yawns = new AtomicInteger(0);
	private AtomicInteger findingTime = new AtomicInteger(0);

	public String buildVoteCountString() {
		EnumMap<Vote, Integer> voteCountMap = createVoteCountMap();
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
		if (!votes.remove(owner, vote)) {
			votes.put(owner, vote);
		}
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