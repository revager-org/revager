package org.revager.gamecontroller;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FindingStatus {

	private final ConcurrentHashMap<Integer, Vote> votes = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Integer, Vote> votesInQueue = new ConcurrentHashMap<>();
	private AtomicInteger yawns = new AtomicInteger(0);
	private AtomicInteger findingTime = new AtomicInteger(0);

	public void addVoteToQueue(int owner, Vote vote) {
		votesInQueue.put(owner, vote);
	}

	public void addOrRemoveVote(int owner, Vote vote) {
		if (!votes.remove(owner, vote)) {
			votes.put(owner, vote);
			votesInQueue.remove(owner, vote);
		}
	}

	public void addYawn() {
		yawns.getAndIncrement();
	}

	public boolean isVotingComplete(int maxVotes) {
		return votesInQueue.size() + votes.size() == maxVotes;
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