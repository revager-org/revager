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
package org.revager.gui.workers;

import static org.revager.app.model.Data._;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.AttendeeManagement;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAspect;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Role;
import org.revager.gui.UI;

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
	 * The reviewers
	 */
	private List<Attendee> reviewers = null;

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
		UI.getInstance().getAspectsManagerFrame()
				.switchToProgressMode(_("Allocating aspects ..."));

		UI.getInstance().getAspectsManagerFrame().observeResiData(false);

		reviewers = new ArrayList<Attendee>();

		for (Attendee att : attMgmt.getAttendees()) {
			if (att.getRole().equals(Role.REVIEWER)) {
				reviewers.add(att);
			}
		}

		if (reviewers.size() > 0 && aspects.size() > 0) {
			allocateAspects();

			UI.getInstance().getAspectsManagerFrame()
					.setStatusMessage(_("Aspects allocated."), false);
		} else {
			UI.getInstance().getAspectsManagerFrame()
					.setStatusMessage(_("Cannot allocate aspects!"), false);
		}

		UI.getInstance().getAspectsManagerFrame().updateViews();

		UI.getInstance().getAspectsManagerFrame().observeResiData(true);

		UI.getInstance().getAspectsManagerFrame().switchToEditMode();

		return null;
	}

	/**
	 * Allocate the aspects in a way, so that each aspect is allocated to at
	 * least one attendee.
	 * 
	 * @throws DataException
	 */
	private void allocateAspects() throws DataException {
		for (AppAspect asp : aspects) {
			Attendee rev1 = getReviewerWithLeastAspects(null);

			attMgmt.addAspect(asp.getAsResiAspect(), rev1);

			Attendee rev2 = getReviewerWithLeastAspects(rev1);

			attMgmt.addAspect(asp.getAsResiAspect(), rev2);
		}
	}

	private Attendee getReviewerWithLeastAspects(Attendee exception) {
		Attendee reviewer = reviewers.get(0);

		if (reviewers.size() > 1 && reviewer == exception) {
			reviewer = reviewers.get(1);
		}

		for (Attendee rev : reviewers) {
			if (attMgmt.getNumberOfAspects(rev) < attMgmt
					.getNumberOfAspects(reviewer) && rev != exception) {
				reviewer = rev;
			}
		}

		return reviewer;
	}
}
