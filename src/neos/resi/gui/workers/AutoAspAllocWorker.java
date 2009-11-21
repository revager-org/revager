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
package neos.resi.gui.workers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.AttendeeManagement;
import neos.resi.app.model.Data;
import neos.resi.app.model.appdata.AppAspect;
import neos.resi.app.model.schema.Attendee;
import neos.resi.app.model.schema.Role;
import neos.resi.gui.UI;

/**
 * Worker for auto allocation of aspects.
 */
public class AutoAspAllocWorker extends SwingWorker<Void, Void> {

	/**
	 * Reference to attendee management
	 */
	private AttendeeManagement attMgmt = Application.getInstance()
			.getAttendeeMgmt();

	/**
	 * The aspects to allocate.
	 */
	private List<AppAspect> aspects = null;

	/**
	 * Instantiates a new allocation worker.
	 * 
	 * @param aspects
	 *            the aspects to allocate
	 */
	public AutoAspAllocWorker(List<AppAspect> aspects) {
		super();

		this.aspects = aspects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		UI.getInstance().getAspectsManagerFrame().switchToProgressMode(
				Data.getInstance().getLocaleStr("status.allocatingAspects"));

		UI.getInstance().getAspectsManagerFrame().observeResiData(false);

		List<Attendee> reviewers = new ArrayList<Attendee>();

		for (Attendee att : attMgmt.getAttendees()) {
			if (att.getRole().equals(Role.REVIEWER)) {
				reviewers.add(att);
			}
		}

		if (reviewers.size() > 0 && aspects.size() > 0) {
			int aspectsPerAttendee = (int) ((aspects.size() / reviewers.size()) * 1.5);

			for (int i = 0; reviewers.size() > i; i++) {
				Attendee rev = reviewers.get(i);

				int aspectsAddCount = aspectsPerAttendee
						- attMgmt.getNumberOfAspects(rev);

				if (aspectsAddCount < 0) {
					aspectsAddCount = 0;
				}

				int index = 0;

				/*
				 * Allocating aspects by strengths
				 */
				while (aspectsAddCount > 0 && index < aspects.size()) {
					AppAspect asp = aspects.get(index);

					if (attMgmt.getAttendeeStrengths(rev).contains(
							asp.getCategory())) {
						attMgmt.addAspect(asp.getAsResiAspect(), rev);

						aspectsAddCount--;
					}

					index++;
				}

				index = aspects.size() - 1;

				/*
				 * Allocating the rest of aspects
				 */
				while (aspectsAddCount > 0 && index >= 0) {
					AppAspect asp = aspects.get(index);

					attMgmt.addAspect(asp.getAsResiAspect(), rev);
					aspectsAddCount--;

					index--;
				}
			}

			UI.getInstance().getAspectsManagerFrame().setStatusMessage(
					Data.getInstance().getLocaleStr("status.aspectsAllocated"),
					false);
		} else {
			UI.getInstance().getAspectsManagerFrame().setStatusMessage(
					Data.getInstance().getLocaleStr(
							"status.allocatingAspectsFailed"), false);
		}

		UI.getInstance().getAspectsManagerFrame().observeResiData(true);

		UI.getInstance().getAspectsManagerFrame().switchToEditMode();

		return null;
	}

}
