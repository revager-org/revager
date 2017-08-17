package org.revager.gamecontroller;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.revager.app.model.schema.Finding;

/**
 * Saves the current state of the classification process for a {@link Finding}.
 * Does not have any semantic without a finding.
 */
public class FindingStatus extends Observable {

	private final ConcurrentHashMap<Integer, Classification> classifications = new ConcurrentHashMap<>();
	private AtomicInteger focuses = new AtomicInteger(0);
	private AtomicInteger findingTime = new AtomicInteger(0);

	/**
	 * Returns a string with the result of the classification process. If this
	 * process is not completed the current result is <em>not</em>masked.
	 */
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

	/**
	 * Record the given classification.
	 * 
	 * @param owner
	 *            A string identifying the controller which caused this request.
	 * @param classification
	 *            The {@link Classification} to record.
	 */
	public void addClassification(int owner, Classification classification) {
		classifications.put(owner, classification);
		setChanged();
		notifyObservers();
	}

	/**
	 * Record, that the focus should be increased.
	 */
	public void addFocus() {
		focuses.getAndIncrement();
	}

	public int getFocusNumber() {
		return focuses.intValue();
	}

	public Collection<Classification> getClassifications() {
		return classifications.values();
	}

	/**
	 * Adds the given seconds to the time the {@link Finding} (this
	 * {@link FindingStatus} belongs to) was discussed.
	 * 
	 * @param findingTime
	 *            The number of seconds to add.
	 */
	public void addFindingTime(int findingTime) {
		this.findingTime.addAndGet(findingTime);
	}

	/**
	 * Resets the time for this {@link Finding} to <code>0s</code>.
	 */
	public void resetFindingTime() {
		findingTime.set(0);
	}

	public int getFindingTime() {
		return findingTime.intValue();
	}

	/**
	 * Builds a map with classifications given and the number how often they
	 * were given.
	 */
	private EnumMap<Classification, Integer> createClassificationCountMap() {
		EnumMap<Classification, Integer> map = new EnumMap<>(Classification.class);
		for (Classification classification : getClassifications()) {
			int count = map.getOrDefault(classification, 0);
			count++;
			map.put(classification, count);
		}
		return map;
	}

}
