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
import java.util.Collections;
import java.util.List;

import org.revager.app.comparators.AspectComparator;
import org.revager.app.comparators.AttendeeComparator;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.ResiData;
import org.revager.app.model.appdata.AppAttendee;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.AspectsIds;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.AttendeeReference;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Role;
import org.revager.tools.AppTools;


/**
 * This class manages the attendees.
 */
public class AttendeeManagement {

	/**
	 * Instantiates a new attendee management.
	 */
	AttendeeManagement() {
		super();
	}

	/**
	 * The resi data.
	 */
	private ResiData resiData = Data.getInstance().getResiData();

	/**
	 * Gets the last id of all attendee ids.
	 * 
	 * @return the last id
	 */
	private int getLastId() {
		int lastId = 0;

		for (Attendee a : resiData.getReview().getAttendees()) {
			if (Integer.parseInt(a.getId()) > lastId) {
				lastId = Integer.parseInt(a.getId());
			}
		}

		return lastId;
	}

	/**
	 * Checks if the given attendee id exists.
	 * 
	 * @param id
	 *            the attendee id
	 * 
	 * @return true, if the given attendee id exists
	 */
	private boolean isId(String id) {
		for (Attendee a : resiData.getReview().getAttendees()) {
			if (a.getId().equals(id)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Renames the id of the given attendee.
	 * 
	 * @param att
	 *            the attendee
	 * @param newId
	 *            the new id for the attendee
	 * 
	 * @return true, if renaming was successful
	 */
	private boolean renameId(Attendee att, String newId) {
		if (isId(newId)) {
			/*
			 * The following line of code depends on a random event, so it is
			 * not part of unit testing.
			 */
			return false;
		} else {
			/*
			 * Rename attendee references
			 */
			for (Meeting m : resiData.getReview().getMeetings()) {
				Protocol prot = m.getProtocol();

				if (prot != null) {
					for (AttendeeReference ar : prot.getAttendeeReferences()) {
						if (ar.getAttendee().equals(att.getId())) {
							ar.setAttendee(newId);
						}
					}
				}
			}

			/*
			 * Rename attendee id
			 */
			att.setId(newId);

			return true;
		}
	}

	/**
	 * Refactor all attendee ids, so that they are all integer.
	 */
	public void refactorIds() {
		/*
		 * Remove duplicate references
		 */
		for (Meeting m : resiData.getReview().getMeetings()) {
			List<String> idList = new ArrayList<String>();
			Protocol prot = m.getProtocol();

			if (prot != null) {
				int i = 0;

				while (prot.getAttendeeReferences().size() > i) {
					AttendeeReference ar = prot.getAttendeeReferences().get(i);

					i++;

					if (idList.contains(ar.getAttendee())) {
						prot.getAttendeeReferences().remove(ar);
						i--;
					} else {
						idList.add(ar.getAttendee());
					}
				}
			}
		}

		/*
		 * Remove wrong references
		 */
		for (Meeting m : resiData.getReview().getMeetings()) {
			Protocol prot = m.getProtocol();

			if (prot != null) {
				int i = 0;

				while (prot.getAttendeeReferences().size() > i) {
					AttendeeReference ar = prot.getAttendeeReferences().get(i);

					i++;

					if (!isId(ar.getAttendee())) {
						prot.getAttendeeReferences().remove(ar);
						i--;
					}
				}
			}
		}

		/*
		 * Rename all current ids so that there aren't any number ids
		 */
		for (Attendee a : resiData.getReview().getAttendees()) {
			String newId = a.getId() + AppTools.getRandomString();

			/*
			 * The following three lines of code depend on a random event, so
			 * they are not part of unit testing.
			 */
			while (!renameId(a, newId)) {
				newId += AppTools.getRandomString();
			}
		}

		/*
		 * Give attendees new systematic ids (numbers only)
		 */
		int id = 1;

		for (Attendee a : resiData.getReview().getAttendees()) {
			renameId(a, Integer.toString(id));

			id++;
		}

		resiData.fireDataChanged();
	}

	/**
	 * Updates the internal review-independent attendees directory.
	 */
	public void updateAttendeesDirectory() {
		ApplicationData appData = Data.getInstance().getAppData();

		for (Attendee att : getAttendees()) {
			try {
				appData.newAttendee(att.getName(), att.getContact());
			} catch (DataException e) {
				/*
				 * do nothing and continue with the next attendee.
				 */
			}
		}
	}

	/**
	 * Returns the list of attendees of the review.
	 * 
	 * @return attendees of the review
	 */
	public List<Attendee> getAttendees() {
		return resiData.getReview().getAttendees();
	}

	/**
	 * Returns an attendees of the review by using his ID.
	 * 
	 * @param id
	 *            the id of the attendee
	 * 
	 * @return attendee with the given id
	 */
	public Attendee getAttendee(int id) {
		Attendee att = null;

		for (Attendee a : resiData.getReview().getAttendees()) {
			if (a.getId().equals(Integer.toString(id))) {
				att = a;
			}
		}

		return att;
	}

	/**
	 * Returns the number of attendees of the review.
	 * 
	 * @return number of attendees
	 */
	public int getNumberOfAttendees() {
		return resiData.getReview().getAttendees().size();
	}

	/**
	 * Checks if the given attendee exists
	 * 
	 * @param attendee
	 *            the attendee to check
	 * 
	 * @return true, if the given attendee exists
	 */
	public boolean isAttendee(Attendee att) {
		AttendeeComparator comp = Application.getInstance().getAttendeeComp();

		if (att == null) {
			return false;
		}

		for (Attendee a : getAttendees()) {
			if (comp.compare(att, a) == 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds the given attendee to the review.
	 * 
	 * @param name
	 *            the name of the attendee
	 * @param contact
	 *            the contact of the attendee
	 * @param role
	 *            the role of the attendee
	 * @param aspects
	 *            the aspects of the attendee
	 * 
	 * @return the added attendee
	 */
	public Attendee addAttendee(String name, String contact, Role role,
			List<Aspect> aspects) {
		Attendee attendee = new Attendee();

		attendee.setName(name);
		attendee.setContact(contact);
		attendee.setRole(role);

		if (aspects != null) {
			AspectsIds aspIds = new AspectsIds();

			for (Aspect a : aspects) {
				aspIds.getAspectIds().add(a.getId());
			}

			attendee.setAspects(aspIds);
		}

		return addAttendee(attendee);
	}

	/**
	 * Adds the given attendee to the review.
	 * 
	 * @param appAtt
	 *            the attendee to add
	 * @param role
	 *            the role of the attendee
	 * @param aspects
	 *            the aspects of the attendee
	 * 
	 * @return the added attendee
	 */
	public Attendee addAttendee(AppAttendee appAtt, Role role,
			List<Aspect> aspects) {
		String contact;
		try {
			contact = appAtt.getContact();
		} catch (DataException e) {
			/*
			 * The following statement is not part of unit testing because it is
			 * only reached when an internal error occurs.
			 */
			contact = "";
		}

		return addAttendee(appAtt.getName(), contact, role, aspects);
	}

	/**
	 * Adds the given attendee to the review.
	 * 
	 * @param att
	 *            the attendee to add
	 * 
	 * @return the added attendee
	 */
	public Attendee addAttendee(Attendee att) {
		Attendee attendee = null;

		if (!getAttendees().contains(att) && !isAttendee(att)) {
			att.setId(Integer.toString((getLastId() + 1)));

			getAttendees().add(att);

			Collections.sort(resiData.getReview().getAttendees(), Application
					.getInstance().getAttendeeComp());

			resiData.fireDataChanged();

			attendee = att;
		}

		return attendee;
	}

	/**
	 * Checks if the given attendee is removable.
	 * 
	 * @param att
	 *            the attendee to remove
	 * 
	 * @return true, if the given attendee is removable
	 */
	public boolean isAttendeeRemovable(Attendee att) {
		if (getMeetings(att).size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes the given attendee from the review.
	 * 
	 * @param att
	 *            attendee which should be removed from the review
	 */
	public void removeAttendee(Attendee att) {
		if (isAttendeeRemovable(att)) {
			resiData.getReview().getAttendees().remove(att);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Edits an attendee of the review.
	 * 
	 * @param oldAtt
	 *            the old attendee
	 * @param newAtt
	 *            the new attendee
	 * 
	 * @return true, if editing of the given old attendee was succesful
	 */
	public boolean editAttendee(Attendee oldAtt, Attendee newAtt) {
		if (resiData.getReview().getAttendees().contains(oldAtt)
				&& !isAttendee(newAtt)) {
			String id = oldAtt.getId();
			int index = resiData.getReview().getAttendees().indexOf(oldAtt);

			resiData.getReview().getAttendees().remove(oldAtt);

			newAtt.setId(id);
			resiData.getReview().getAttendees().add(index, newAtt);

			resiData.fireDataChanged();

			return true;
		}

		return false;
	}

	/**
	 * Gets the meetings in which the given attendee is involved.
	 * 
	 * @param att
	 *            the attendee to check
	 * 
	 * @return meetings of the attendee
	 */
	public List<Meeting> getMeetings(Attendee att) {
		List<Meeting> meetingList = new ArrayList<Meeting>();

		for (Meeting m : resiData.getReview().getMeetings()) {
			Protocol prot = m.getProtocol();

			if (prot != null) {
				for (AttendeeReference ar : prot.getAttendeeReferences()) {
					if (ar.getAttendee().equals(att.getId())
							&& !meetingList.contains(m)) {
						meetingList.add(m);
					}
				}
			}
		}

		return meetingList;
	}

	/**
	 * Returns the list of aspects of the attendee.
	 * 
	 * @param att
	 *            the attendee to check
	 * 
	 * @return aspects of the attendee
	 */
	public List<Aspect> getAspects(Attendee att) {
		List<Aspect> aspectList = new ArrayList<Aspect>();

		for (Attendee a : resiData.getReview().getAttendees()) {
			if (a == att && a.getAspects() != null) {
				for (String aspId : a.getAspects().getAspectIds()) {
					if (Application.getInstance().getAspectMgmt().getAspect(
							aspId) != null) {
						aspectList.add(Application.getInstance()
								.getAspectMgmt().getAspect(aspId));
					}
				}
			}
		}

		return aspectList;
	}

	/**
	 * Checks if the given attendee has the given aspect.
	 * 
	 * @param asp
	 *            the aspect to check
	 * @param att
	 *            the attendee to check
	 * 
	 * @return true, if the given attendee has the given aspect
	 */
	public boolean hasAspect(Aspect asp, Attendee att) {
		if (asp.getId() != null && getAspects(att).contains(asp)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds the given aspect to the list of aspects of the given attendee.
	 * 
	 * @param asp
	 *            the aspect to add
	 * @param att
	 *            the attendee
	 */
	public void addAspect(Aspect asp, Attendee att) {
		AspectComparator comp = Application.getInstance().getAspectComp();
		AspectManagement aspMgmt = Application.getInstance().getAspectMgmt();
		Aspect aspect = asp;

		/*
		 * If the aspect is not part of the review
		 */
		if (!aspMgmt.isAspect(asp)) {
			aspect = aspMgmt.addAspect(asp);
		}

		/*
		 * If the aspect was a duplicate to an aspect which already is part of
		 * the review
		 */
		if (aspect.getId() == null) {
			for (Aspect a : aspMgmt.getAspects()) {
				if (comp.compare(aspect, a) == 0) {
					aspect = a;
				}
			}
		}

		if (att.getAspects() == null) {
			att.setAspects(new AspectsIds());
		}

		if (!att.getAspects().getAspectIds().contains(aspect.getId())) {
			att.getAspects().getAspectIds().add(aspect.getId());

			resiData.fireDataChanged();
		}
	}

	/**
	 * Adds the given aspect from the list of aspects of the given attendee.
	 * 
	 * @param asp
	 *            the aspect to remove
	 * @param att
	 *            the attendee
	 */
	public void removeAspect(Aspect asp, Attendee att) {
		AspectManagement aspMgmt = Application.getInstance().getAspectMgmt();

		if (att.getAspects() != null) {
			att.getAspects().getAspectIds().remove(asp.getId());

			if (att.getAspects().getAspectIds().isEmpty()) {
				att.setAspects(null);
			}

			/*
			 * If no other attendee has the removed aspect, then remove the
			 * aspect completely from the review
			 */
			if (getAttendeesWithAspect(asp).isEmpty()) {
				aspMgmt.removeAspect(asp);
			}

			resiData.fireDataChanged();
		}
	}

	/**
	 * Returns the number of aspects that are assigned to the given attendee.
	 * 
	 * @param att
	 *            the attendee to check
	 * 
	 * @return the number of aspects of the given attendee
	 */
	public int getNumberOfAspects(Attendee att) {
		if (att.getAspects() != null) {
			return att.getAspects().getAspectIds().size();
		} else {
			return 0;
		}
	}

	/**
	 * Pushs up a aspect in the list of severities of the current review.
	 * 
	 * @param att
	 *            the attendee
	 * @param asp
	 *            the aspect to push
	 */
	public void pushUpAspect(Attendee att, Aspect asp) {
		if (!isTopAspect(att, asp)) {
			int index = getAspects(att).indexOf(asp);

			att.getAspects().getAspectIds().remove(index);
			att.getAspects().getAspectIds().add(index - 1, asp.getId());

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs down a Aspect in the list of Aspects of the current review.
	 * 
	 * @param att
	 *            the attendee
	 * @param asp
	 *            the aspect to push
	 */
	public void pushDownAspect(Attendee att, Aspect asp) {
		if (!isBottomAspect(att, asp)) {
			int index = getAspects(att).indexOf(asp);

			att.getAspects().getAspectIds().remove(index);
			att.getAspects().getAspectIds().add(index + 1, asp.getId());

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs up a Aspect to the top of the list of Aspects of the current
	 * review.
	 * 
	 * @param att
	 *            the attendee
	 * @param asp
	 *            the aspect to push
	 */
	public void pushTopAspect(Attendee att, Aspect asp) {
		if (!isTopAspect(att, asp)) {
			int index = getAspects(att).indexOf(asp);

			att.getAspects().getAspectIds().remove(index);
			att.getAspects().getAspectIds().add(0, asp.getId());

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs up a Aspect to the bottom of the list of Aspects of the current
	 * review.
	 * 
	 * @param att
	 *            the attendee
	 * @param asp
	 *            the aspect to push
	 */
	public void pushBottomAspect(Attendee att, Aspect asp) {
		if (!isBottomAspect(att, asp)) {
			int index = getAspects(att).indexOf(asp);

			att.getAspects().getAspectIds().remove(index);
			att.getAspects().getAspectIds().add(getAspects(att).size(),
					asp.getId());

			resiData.fireDataChanged();
		}
	}

	/**
	 * Returns a true if the Aspect is the element at the top of the list;
	 * otherwise false.
	 * 
	 * @param att
	 *            the attendee
	 * @param asp
	 *            the aspect to check
	 * 
	 * @return true, if checks if is top aspect
	 */
	public boolean isTopAspect(Attendee att, Aspect asp) {
		int index = getAspects(att).indexOf(asp);

		if (index == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns a true if the Aspect is the element at the bottom of the list;
	 * otherwise false.
	 * 
	 * @param att
	 *            the attendee
	 * @param asp
	 *            the aspect to check
	 * 
	 * @return true, if checks if is bottom aspect
	 */
	public boolean isBottomAspect(Attendee att, Aspect asp) {
		int index = getAspects(att).indexOf(asp);

		if (index == getAspects(att).size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the reviewers of this review.
	 * 
	 * @return the reviewers
	 */
	public List<Attendee> getReviewers() {
		List<Attendee> attList = new ArrayList<Attendee>();

		for (Attendee att : getAttendees()) {
			if (att.getRole() == Role.REVIEWER) {
				attList.add(att);
			}
		}

		return attList;
	}

	/**
	 * Gets the attendees with the given aspect.
	 * 
	 * @param asp
	 *            the aspect
	 * 
	 * @return the attendees with the given aspect
	 */
	public List<Attendee> getAttendeesWithAspect(Aspect asp) {
		List<Attendee> attList = new ArrayList<Attendee>();

		for (Attendee att : getAttendees()) {
			if (att.getAspects() != null
					&& att.getAspects().getAspectIds().contains(asp.getId())) {
				attList.add(att);
			}
		}

		return attList;
	}

	/**
	 * Gets the strengths of the given attendee.
	 * 
	 * @param att
	 *            the attendee
	 * 
	 * @return the attendee's strengths
	 */
	public List<String> getAttendeeStrengths(Attendee att) {
		List<String> strengths = new ArrayList<String>();

		try {
			for (AppAttendee appAtt : Data.getInstance().getAppData()
					.getAttendees()) {
				if (att.getName().equals(appAtt.getName())
						&& att.getContact().equals(appAtt.getContact())) {
					strengths = appAtt.getStrengths();
				}
			}
		} catch (DataException e) {
			/*
			 * do nothing and return the possibly empty list.
			 */
		}

		return strengths;
	}
	
}
