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

import java.util.List;

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

	/**
	 * Checks the severity of all findings and corrects incorrect values.
	 */
	public void validateSeverities() {
		String highestSev = getSeverities().get(0);

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
		if (getSeverities().contains(sev)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns a list of severities of the current review.
	 * 
	 * @return Severities
	 */
	public List<String> getSeverities() {
		return resiData.getReview().getSeverities().getSeverities();
	}

	/**
	 * Adds a severity to the list of severities of the current review.
	 * 
	 * @param sev
	 *            the severity to add
	 */
	public void addSeverity(String sev) {
		sev = sev.trim();

		if (!isSeverity(sev)) {
			getSeverities().add(sev);

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
	public String getReplaceSeverity(String sev) {
		String replaceSeverity = null;

		if (isSeverity(sev)) {
			if (getSeverities().indexOf(sev) == 0 && getSeverities().size() > 1) {
				replaceSeverity = getSeverities().get(1);
			} else {
				replaceSeverity = getSeverities().get(
						getSeverities().indexOf(sev) - 1);
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
		if (isSeverity(sev) && getSeverities().size() <= 1) {
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

			getSeverities().remove(sev);

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
		newSev = newSev.trim();

		if (getSeverities().contains(oldSev)) {
			int index = getSeverities().indexOf(oldSev);

			getSeverities().remove(oldSev);

			if (!isSeverity(newSev)) {
				getSeverities().add(index, newSev);
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
		if (!isTopSeverity(sev)) {
			int index = getSeverities().indexOf(sev);

			getSeverities().remove(index);
			getSeverities().add(index - 1, sev);

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
		if (!isBottomSeverity(sev)) {
			int index = getSeverities().indexOf(sev);

			getSeverities().remove(index);
			getSeverities().add(index + 1, sev);

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
		if (!isTopSeverity(sev)) {
			int index = getSeverities().indexOf(sev);

			getSeverities().remove(index);
			getSeverities().add(0, sev);

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
		if (!isBottomSeverity(sev)) {
			int index = getSeverities().indexOf(sev);

			getSeverities().add(getSeverities().size(), sev);
			getSeverities().remove(index);

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
		int index = getSeverities().indexOf(sev);

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
		int index = getSeverities().indexOf(sev);

		if (index == getSeverities().size() - 1) {
			return true;
		} else {
			return false;
		}
	}

}
