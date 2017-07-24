package org.revager.gamecontroller;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FindingStatus extends Observable {

	private final ConcurrentHashMap<Integer, Classification> classifications = new ConcurrentHashMap<>();
	private AtomicInteger focuses = new AtomicInteger(0);
	private AtomicInteger findingTime = new AtomicInteger(0);

	public String buildClassificationCountString() {
		EnumMap<Classification, Integer> classificationCountMap = createClassificationCountMap();
		int maxCount = -1;
		for (Entry<Classification, Integer> entry : classificationCountMap.entrySet()) {
			maxCount = Math.max(maxCount, entry.getValue());
		}

		if (maxCount == 1) {
			maxCount = -1;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");

		for (Entry<Classification, Integer> entry : classificationCountMap.entrySet()) {
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

	private EnumMap<Classification, Integer> createClassificationCountMap() {
		EnumMap<Classification, Integer> map = new EnumMap<>(Classification.class);
		for (Classification classification : getClassifications()) {
			int count = map.getOrDefault(classification, 0);
			count++;
			map.put(classification, count);
		}
		return map;
	}

	public void addClassification(int owner, Classification classification) {
		classifications.put(owner, classification);
		setChanged();
		notifyObservers();
	}

	public void addFocus() {
		focuses.getAndIncrement();
	}

	public int getFocusNumber() {
		return focuses.intValue();
	}

	public Collection<Classification> getClassifications() {
		return classifications.values();
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

	public Classification getClassificationForOwner(int owner) {
		return classifications.getOrDefault(owner, null);
	}

}
