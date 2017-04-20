/* 
 * Copyright 2009 Davide Casciato, Sandra Reich, Johannes Wettinger
 * 
 * This file is part of Resi.
 *
 * Resi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Resi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resi. If not, see <http://www.gnu.org/licenses/>.
 */
package org.revager.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;

/**
 * This class manages the severities of the current review.
 */
public class SeverityManagement {

	/**
	 * Instantiates the severity management.
	 */
	SeverityManagement() {
		super();
	}

	/**
	 * The resi data to access the current review.
	 */
	private ResiData resiData = Data.getInstance().getResiData();

	public void updateSeverities() {
		Map<String, String> updates = new HashMap<String, String>();

		for (String sev : getDefLangSeverities()) {
			if (!sev.equals(Data.getDefLangSeverity(sev.trim()))) {
				updates.put(sev, Data.getDefLangSeverity(sev.trim()));
			}
		}

		for (Entry<String, String> entry : updates.entrySet()) {
			String currSev = entry.getKey();
			String updatedSev = entry.getValue();

			int index = getDefLangSeverities().indexOf(currSev);

			getDefLangSeverities().remove(currSev);

			if (!isSeverity(updatedSev)) {
				getDefLangSeverities().add(index, updatedSev);
			}

			for (Meeting m : Application.getInstance().getMeetingMgmt()
					.getMeetings()) {
				Protocol prot = m.getProtocol();

				if (prot != null) {
					for (Finding f : prot.getFindings()) {
						if (currSev.equals(f.getSeverity())) {
							f.setSeverity(updatedSev);
						}
					}
				}
			}
		}
	}

	/**
	 * Checks the severity of all findings and corrects incorrect values.
	 */
	public void validateSeverities() {
		String highestSev = getDefLangSeverities().get(0);

		for (Meeting m : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			Protocol prot = m.getProtocol();

			if (prot != null) {
				for (Finding f : prot.getFindings()) {
					if (!isSeverity(f.getSeverity())) {
						f.setSeverity(highestSev);
					}
				}
			}
		}
	}

	/**
	 * Checks if the given severity exists.
	 * 
	 * @param sev
	 *            the severity
	 * 
	 * @return true, if the given severity exists
	 */
	public boolean isSeverity(String sev) {
		if (getDefLangSeverities().contains(Data.getDefLangSeverity(sev))) {
			return true;
		} else {
			return false;
		}
	}

	private List<String> getDefLangSeverities() {
		return resiData.getReview().getSeverities().getSeverities();
	}

	/**
	 * Returns a list of severities of the current review.
	 * 
	 * @return the severities
	 */
	public List<String> getSeverities() {
		List<String> list = new ArrayList<String>();

		for (String sev : getDefLangSeverities()) {
			list.add(Data._(sev));
		}

		return list;
	}

	/**
	 * Adds a severity to the list of severities of the current review.
	 * 
	 * @param sev
	 *            the severity to add
	 */
	public void addSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev.trim());

		if (!isSeverity(sev)) {
			getDefLangSeverities().add(sev);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Gets the replace severity of the given severity
	 * 
	 * @param sev
	 *            the severity
	 * 
	 * @return the replace severity
	 */
	private String getReplaceSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		String replaceSeverity = null;

		if (isSeverity(sev)) {
			if (getDefLangSeverities().indexOf(sev) == 0
					&& getDefLangSeverities().size() > 1) {
				replaceSeverity = getDefLangSeverities().get(1);
			} else {
				replaceSeverity = getDefLangSeverities().get(
						getDefLangSeverities().indexOf(sev) - 1);
			}
		}

		return replaceSeverity;
	}

	/**
	 * Checks if the given severity is removable.
	 * 
	 * @param sev
	 *            the severity
	 * 
	 * @return true, if the given severity is removable
	 */
	public boolean isSeverityRemovable(String sev) {
		sev = Data.getDefLangSeverity(sev);

		if (isSeverity(sev) && getDefLangSeverities().size() <= 1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Removes the given severity form the list of severities of the current
	 * review.
	 * 
	 * @param sev
	 *            the severity
	 */
	public void removeSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		if (isSeverityRemovable(sev)) {
			String replaceSeverity = getReplaceSeverity(sev);

			for (Meeting m : Application.getInstance().getMeetingMgmt()
					.getMeetings()) {
				Protocol prot = m.getProtocol();

				if (prot != null) {
					for (Finding f : prot.getFindings()) {
						if (sev.equals(f.getSeverity())) {
							f.setSeverity(replaceSeverity);
						}
					}
				}
			}

			getDefLangSeverities().remove(sev);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Replaces the given severity from the list of severities of the current
	 * review by another one.
	 * 
	 * @param oldSev
	 *            the old severity
	 * @param newSev
	 *            the new severity
	 */
	public void editSeverity(String oldSev, String newSev) {
		newSev = Data.getDefLangSeverity(newSev.trim());
		oldSev = Data.getDefLangSeverity(oldSev);

		if (getDefLangSeverities().contains(oldSev)) {
			int index = getDefLangSeverities().indexOf(oldSev);

			getDefLangSeverities().remove(oldSev);

			if (!isSeverity(newSev)) {
				getDefLangSeverities().add(index, newSev);
			}

			for (Meeting m : Application.getInstance().getMeetingMgmt()
					.getMeetings()) {
				Protocol prot = m.getProtocol();

				if (prot != null) {
					for (Finding f : prot.getFindings()) {
						if (oldSev.equals(f.getSeverity())) {
							f.setSeverity(newSev);
						}
					}
				}
			}

			resiData.fireDataChanged();
		}
	}

	/**
	 * Returns the number of severities in this review.
	 * 
	 * @return the number of severities
	 */
	public int getNumberOfSeverities() {
		return Application.getInstance().getReviewMgmt()
				.getNumberOfSeverities();
	}

	/**
	 * Pushs up a severity in the list of severities of the current review.
	 * 
	 * @param sev
	 *            the severity to push
	 */
	public void pushUpSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		if (!isTopSeverity(sev)) {
			int index = getDefLangSeverities().indexOf(sev);

			getDefLangSeverities().remove(index);
			getDefLangSeverities().add(index - 1, sev);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs down a severity in the list of severities of the current review.
	 * 
	 * @param sev
	 *            the severity to push
	 */
	public void pushDownSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		if (!isBottomSeverity(sev)) {
			int index = getDefLangSeverities().indexOf(sev);

			getDefLangSeverities().remove(index);
			getDefLangSeverities().add(index + 1, sev);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs up a severity to the top of the list of severities of the current
	 * review.
	 * 
	 * @param sev
	 *            the severity to push
	 */
	public void pushTopSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		if (!isTopSeverity(sev)) {
			int index = getDefLangSeverities().indexOf(sev);

			getDefLangSeverities().remove(index);
			getDefLangSeverities().add(0, sev);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs up a severity to the bottom of the list of severities of the
	 * current review.
	 * 
	 * @param sev
	 *            the severity to push
	 */
	public void pushBottomSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		if (!isBottomSeverity(sev)) {
			int index = getDefLangSeverities().indexOf(sev);

			getDefLangSeverities().add(getDefLangSeverities().size(), sev);
			getDefLangSeverities().remove(index);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Returns true if the severity is the element at the top of the list;
	 * otherwise false.
	 * 
	 * @param sev
	 *            the severity to check
	 * 
	 * @return true, if the severity is at the top
	 */
	public boolean isTopSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		int index = getDefLangSeverities().indexOf(sev);

		if (index == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true if the severity is the element at the bottom of the list;
	 * otherwise false.
	 * 
	 * @param sev
	 *            the severity
	 * 
	 * @return true, if the severity is at the bottom
	 */
	public boolean isBottomSeverity(String sev) {
		sev = Data.getDefLangSeverity(sev);

		int index = getDefLangSeverities().indexOf(sev);

		if (index == getDefLangSeverities().size() - 1) {
			return true;
		} else {
			return false;
		}
	}

}
