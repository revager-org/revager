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
import java.util.List;

import org.revager.app.comparators.AspectComparator;
import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.tools.AppTools;

/**
 * This class is for management of aspects.
 */
public class AspectManagement {

	/**
	 * Dummy aspect
	 */
	public final Aspect DUMMY_ASPECT = new Aspect();

	/**
	 * The Resi data.
	 */
	private ResiData resiData = Data.getInstance().getResiData();

	/**
	 * Instantiates a new aspect management.
	 */
	AspectManagement() {
		super();

		DUMMY_ASPECT.setId("");
		DUMMY_ASPECT.setDirective("");
		DUMMY_ASPECT.setDescription("");
		DUMMY_ASPECT.setCategory("");
	}

	/**
	 * Gets the last id.
	 * 
	 * @return the last id
	 */
	private int getLastId() {
		int lastId = 0;

		for (Aspect asp : resiData.getReview().getAspects()) {
			if (asp != DUMMY_ASPECT && Integer.parseInt(asp.getId()) > lastId) {
				lastId = Integer.parseInt(asp.getId());
			}
		}

		return lastId;
	}

	/**
	 * Checks if the given id exists.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return true, if checks if is id
	 */
	private boolean isId(String id) {
		for (Aspect a : resiData.getReview().getAspects()) {
			if (a.getId().equals(id)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Renames the id of the given aspect.
	 * 
	 * @param asp
	 *            the asp
	 * @param newId
	 *            the new id
	 * 
	 * @return true, if rename id
	 */
	private boolean renameId(Aspect asp, String newId) {
		if (isId(newId)) {
			/*
			 * The following line of code depends on a random event, so it is
			 * not part of unit testing.
			 */
			return false;
		} else {
			/*
			 * Rename aspect references
			 */
			for (Attendee att : Application.getInstance().getAttendeeMgmt().getAttendees()) {
				int index = -1;

				if (att.getAspects() != null) {
					index = att.getAspects().getAspectIds().indexOf(asp.getId());
				}

				if (index != -1) {
					att.getAspects().getAspectIds().remove(index);
					att.getAspects().getAspectIds().add(index, newId);
				}
			}

			/*
			 * Rename aspect id
			 */
			asp.setId(newId);

			return true;
		}
	}

	/**
	 * Refactors the ids of all aspects, so that they are all integer.
	 */
	public void refactorIds() {
		/*
		 * Remove dummy and empty aspects
		 */
		int i = 0;

		while (resiData.getReview().getAspects().size() > i) {
			Aspect asp = resiData.getReview().getAspects().get(i);

			if (asp.getId().trim().equals("")) {
				resiData.getReview().getAspects().remove(asp);
			}

			i++;
		}

		/*
		 * Remove duplicate references
		 */
		for (Attendee a : Application.getInstance().getAttendeeMgmt().getAttendees()) {
			List<String> idList = new ArrayList<String>();

			i = 0;

			if (a.getAspects() != null) {
				while (a.getAspects().getAspectIds().size() > i) {
					String aspId = a.getAspects().getAspectIds().get(i);

					i++;

					if (idList.contains(aspId)) {
						a.getAspects().getAspectIds().remove(aspId);
						i--;
					} else {
						idList.add(aspId);
					}
				}
			}
		}

		/*
		 * Remove wrong references
		 */
		for (Attendee a : Application.getInstance().getAttendeeMgmt().getAttendees()) {
			i = 0;

			if (a.getAspects() != null) {
				while (a.getAspects().getAspectIds().size() > i) {
					String aspId = a.getAspects().getAspectIds().get(i);

					i++;

					if (!isId(aspId)) {
						a.getAspects().getAspectIds().remove(aspId);
						i--;
					}
				}
			}
		}

		/*
		 * Rename all current ids so that there aren't any number ids
		 */
		for (Aspect a : resiData.getReview().getAspects()) {
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

		for (Aspect a : resiData.getReview().getAspects()) {
			renameId(a, Integer.toString(id));

			id++;
		}

		/*
		 * Add dummy aspect
		 */
		resiData.getReview().getAspects().add(DUMMY_ASPECT);

		resiData.fireDataChanged();
	}

	/**
	 * Add dummy aspect
	 */
	public void addDummyAspect() {
		if (!resiData.getReview().getAspects().contains(DUMMY_ASPECT)) {
			resiData.getReview().getAspects().add(DUMMY_ASPECT);
		}
	}

	/**
	 * Returns a list of aspects of the current review (without the dummy
	 * aspect).
	 * 
	 * @return aspects
	 */
	public List<Aspect> getAspects() {
		List<Aspect> aspects = new ArrayList<Aspect>();

		for (Aspect asp : resiData.getReview().getAspects()) {
			if (asp != DUMMY_ASPECT) {
				aspects.add(asp);
			}
		}

		return aspects;
	}

	/**
	 * Gets all aspects of the review.
	 * 
	 * @param filter
	 *            the filter
	 * 
	 * @return the aspects
	 */
	public List<Aspect> getAspects(String filter) {
		List<Aspect> aspects = new ArrayList<Aspect>();
		filter = filter.toLowerCase();

		for (Aspect a : getAspects()) {
			if (a.getDirective().toLowerCase().contains(filter) || a.getDescription().toLowerCase().contains(filter)) {
				aspects.add(a);
			}
		}

		return aspects;
	}

	/**
	 * Returns an aspect by using its id.
	 * 
	 * @param id
	 *            the id of the aspect
	 * 
	 * @return the aspect
	 */
	public Aspect getAspect(String id) {
		Aspect asp = null;

		for (int i = 0; i < resiData.getReview().getAspects().size(); i++) {
			Aspect aspect = resiData.getReview().getAspects().get(i);

			if (aspect.getId().equals(id)) {
				asp = aspect;
			}
		}

		return asp;
	}

	/**
	 * Returns an aspect by using its id.
	 * 
	 * @param id
	 *            the id of the aspect
	 * 
	 * @return the aspect
	 */
	public Aspect getAspect(int id) {
		return getAspect(Integer.toString(id));
	}

	/**
	 * Checks if the aspect exists.
	 * 
	 * @param asp
	 *            the aspect
	 * 
	 * @return true, if the given aspect exists
	 */
	public boolean isAspect(Aspect asp) {
		AspectComparator comp = Application.getInstance().getAspectComp();

		for (Aspect a : getAspects()) {
			if (comp.compare(asp, a) == 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds the given aspect to the review.
	 * 
	 * @param directive
	 *            the directive of this aspect
	 * @param description
	 *            the description of this aspect
	 * @param category
	 *            the category of this aspect
	 * 
	 * @return the aspect
	 */
	public Aspect addAspect(String directive, String description, String category) {
		Aspect aspect = new Aspect();

		aspect.setDirective(directive);
		aspect.setDescription(description);
		aspect.setCategory(category);

		addAspect(aspect);

		return aspect;
	}

	/**
	 * Adds the given aspect to the review.
	 * 
	 * @param asp
	 *            the aspect to add
	 * 
	 * @return the aspect added to the review
	 */
	public Aspect addAspect(Aspect asp) {
		Aspect aspect = null;

		if (!resiData.getReview().getAspects().contains(asp) && !isAspect(asp)) {
			asp.setId(Integer.toString(getLastId() + 1));

			resiData.getReview().getAspects().add(asp);

			resiData.fireDataChanged();

			aspect = asp;
		}

		return aspect;
	}

	/**
	 * Removes an aspect form the list of aspects of the current review.
	 * 
	 * @param asp
	 *            the aspect to remove
	 */
	public void removeAspect(Aspect asp) {
		AttendeeManagement attMgmt = Application.getInstance().getAttendeeMgmt();

		if (asp.getId() != null) {
			for (Attendee att : attMgmt.getAttendees()) {
				if (attMgmt.hasAspect(asp, att)) {
					attMgmt.removeAspect(asp, att);
				}
			}

			resiData.getReview().getAspects().remove(asp);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Replaces an aspect from the list of aspects of the current review by
	 * another one.
	 * 
	 * @param oldAsp
	 *            the old aspect
	 * @param newAsp
	 *            the new aspect
	 * 
	 * @return true, if the aspect was edited successfully
	 */
	public boolean editAspect(Aspect oldAsp, Aspect newAsp) {
		if (resiData.getReview().getAspects().contains(oldAsp) && !isAspect(newAsp)) {
			String id = oldAsp.getId();
			int index = resiData.getReview().getAspects().indexOf(oldAsp);

			resiData.getReview().getAspects().remove(oldAsp);

			newAsp.setId(id);
			resiData.getReview().getAspects().add(index, newAsp);

			resiData.fireDataChanged();

			return true;
		}

		return false;
	}

	/**
	 * Pushs up an aspect in the list of aspects of the current review.
	 * 
	 * @param asp
	 *            the aspect to push
	 */
	public void pushUpAspect(Aspect asp) {
		if (!isTopAspect(asp)) {
			int index = resiData.getReview().getAspects().indexOf(asp);

			resiData.getReview().getAspects().remove(asp);
			resiData.getReview().getAspects().add(index - 1, asp);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs down an aspect in the list of aspects of the current review.
	 * 
	 * @param asp
	 *            the aspect to push
	 */
	public void pushDownAspect(Aspect asp) {
		if (!isBottomAspect(asp)) {
			int index = resiData.getReview().getAspects().indexOf(asp);

			resiData.getReview().getAspects().remove(index);
			resiData.getReview().getAspects().add(index + 1, asp);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs up an aspect to the top of the list of aspects of the current
	 * review.
	 * 
	 * @param asp
	 *            the aspect to push
	 */
	public void pushTopAspect(Aspect asp) {
		if (!isTopAspect(asp)) {
			int index = resiData.getReview().getAspects().indexOf(asp);

			resiData.getReview().getAspects().remove(index);
			resiData.getReview().getAspects().add(0, asp);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Pushs up an aspect to the bottom of the list of aspects of the current
	 * review.
	 * 
	 * @param asp
	 *            the aspect to push
	 */
	public void pushBottomAspect(Aspect asp) {
		if (!isBottomAspect(asp)) {
			int index = resiData.getReview().getAspects().indexOf(asp);

			resiData.getReview().getAspects().remove(index);
			resiData.getReview().getAspects().add(resiData.getReview().getAspects().size(), asp);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Returns an boolean whether the aspect is the element at the top of the
	 * list or not.
	 * 
	 * @param asp
	 *            the aspect to check
	 * 
	 * @return true, if the given aspect is the top aspect
	 */
	public boolean isTopAspect(Aspect asp) {
		int index = resiData.getReview().getAspects().indexOf(asp);

		if (index == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns an boolean whether the aspect is the element at the bottom of the
	 * list or not.
	 * 
	 * @param asp
	 *            the aspect to check
	 * 
	 * @return true, if the given aspect is the bottom aspect
	 */
	public boolean isBottomAspect(Aspect asp) {
		int index = resiData.getReview().getAspects().indexOf(asp);

		if (index == resiData.getReview().getAspects().size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the categories of all aspects.
	 * 
	 * @return the categories
	 */
	public List<String> getCategories() {
		List<String> categories = new ArrayList<String>();

		for (Aspect asp : getAspects()) {
			String cat = asp.getCategory().trim();

			if (!categories.contains(cat)) {
				categories.add(cat);
			}
		}

		return categories;
	}

}
