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
package org.revager.app.model.appdata;

import static org.revager.app.model.Data._;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.schema.Attendee;

/**
 * Instances of this class represent attendees in the database.
 */
public class AppAttendee {

	/**
	 * Contact information of the attendee.
	 */
	private String contact = null;

	/**
	 * The name of the attendee.
	 */
	private String name = null;

	/**
	 * This is exception is thrown if an attendee could not be found.
	 */
	private DataException notFoundExc = new DataException(
			_("Attendee does not exist!") + " [NAME = " + this.name
					+ ", CONTACT = " + this.contact + "]");

	/**
	 * Internally used contructor to create a new instance of this class by the
	 * name of the attendee.
	 * 
	 * @param name
	 *            name of the attendee
	 * @param contact
	 *            contact information of the attendee
	 */
	protected AppAttendee(String name, String contact) {
		super();
		this.name = name;
		this.contact = contact;
	}

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		String contact = this.contact;

		if (contact.length() > 20) {
			contact = contact.substring(0, 20) + "...";
		}

		if (!contact.trim().equals("")) {
			contact = " (" + contact + ")";
		}

		return this.name + contact;
	}

	/**
	 * If the name of two AppAttendee instances is equal, then they are declared
	 * as equal by this method.
	 * 
	 * @param obj
	 *            the attendee for comparation
	 * 
	 * @return true, if the attendees are equal
	 */
	@Override
	public boolean equals(Object obj) {
		AppAttendee otherAtt = (AppAttendee) obj;
		boolean isEqual = false;

		try {
			if (this.name.equals(otherAtt.getName())
					&& this.contact.equals(otherAtt.getContact())) {
				isEqual = true;
			}
		} catch (DataException e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			isEqual = false;
		}

		return isEqual;
	}

	/**
	 * Checks if the attendee with given name and contact exists.
	 * 
	 * @return true, if the attendee exists
	 * 
	 * @throws DataException
	 *             If an error occurs while checking the existence
	 */
	public boolean exists() throws DataException {
		return Data.getInstance().getAppData()
				.isAttendee(this.name, this.contact);
	}

	/**
	 * Creates a new instance of this class by the attendee name.
	 * 
	 * @param name
	 *            name of the attendee
	 * @param contact
	 *            contact information of the attendee
	 * 
	 * @return the attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while creating a new instance
	 */
	public static AppAttendee newInstance(String name, String contact)
			throws DataException {
		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name,contact FROM Attendees "
							+ "WHERE name = ? AND contact = ?");

			ps.setString(1, name);
			ps.setString(2, contact);

			ResultSet res = ps.executeQuery();

			/*
			 * If an attendee with the given name does not exist
			 */
			if (!res.next()) {
				ps = conn.prepareStatement("INSERT INTO Attendees "
						+ "(name, contact) VALUES (?, ?)");

				ps.setString(1, name);
				ps.setString(2, contact);

				ps.executeUpdate();

				Data.getInstance().getAppData().fireDataChanged();
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot add or get attendee.")
					+ " [NAME = " + name + "] " + e.getMessage());
		}

		return new AppAttendee(name, contact);
	}

	/**
	 * Returns the name of the attendee.
	 * 
	 * @return name of attendee
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the contact information of the attendee.
	 * 
	 * @return attendee's contact information
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the contact information
	 */
	public String getContact() throws DataException {
		return this.contact;
	}

	/**
	 * Sets name of the attendee.
	 * 
	 * @param name
	 *            name of the attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while setting attendee's name
	 */
	public void setName(String name) throws DataException {
		setNameAndContact(name, this.contact);
	}

	/**
	 * Sets contact of the attendee.
	 * 
	 * @param contact
	 *            contact information of the attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while setting attendee's contact
	 */
	public void setContact(String contact) throws DataException {
		setNameAndContact(this.name, contact);
	}

	/**
	 * Sets name and contact of the attendee.
	 * 
	 * @param name
	 *            name of the attendee
	 * @param contact
	 *            contact information of the attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while setting attendee's name and contact
	 */
	public void setNameAndContact(String name, String contact)
			throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		if (name.equals(this.name) && contact.equals(this.contact)) {
			return;
		}

		int result = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT COUNT(*) FROM Attendees WHERE name=? AND contact=?");
			ps.setString(1, name);
			ps.setString(2, contact);

			ResultSet res = ps.executeQuery();

			res.next();

			result = res.getInt(1);

			res.close();

			/*
			 * If there is no attendee with the given name and contact
			 */
			if (result == 0) {
				ps = conn.prepareStatement("UPDATE Attendees "
						+ "SET name=?, contact=? WHERE name=? AND contact=?");

				ps.setString(1, name);
				ps.setString(2, contact);
				ps.setString(3, this.name);
				ps.setString(4, this.contact);

				ps.executeUpdate();

				ps.close();

				/*
				 * If strengths for this attendee exist, update the attendee
				 * strengths table
				 */
				ps = conn
						.prepareStatement("UPDATE AttendeesStrengths "
								+ "SET attendeeName=?, attendeeContact=? WHERE attendeeName=? AND attendeeContact=?");

				ps.setString(1, name);
				ps.setString(2, contact);
				ps.setString(3, this.name);
				ps.setString(4, this.contact);

				ps.executeUpdate();

				ps.close();

				conn.close();

				this.name = name;
				this.contact = contact;

				Data.getInstance().getAppData().fireDataChanged();
			} else {
				ps.close();
				conn.close();

				throw new DataException();
			}
		} catch (Exception e) {
			throw new DataException(
					_("Cannot store attendee! There may be an attendee with the given name and contact information already existing.")
							+ " [NAME = "
							+ this.name
							+ ", CONTACT = "
							+ this.contact + "] " + e.getMessage());
		}
	}

	/**
	 * Returns the strengths of this attendee.
	 * 
	 * @return strengths of this attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while getting attendee's strengths
	 */
	public List<String> getStrengths() throws DataException {
		List<String> contact = new ArrayList<String>();

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT categoryName FROM AttendeesStrengths "
							+ "WHERE attendeeName=? AND attendeeContact=? ORDER BY categoryName ASC");

			ps.setString(1, this.name);
			ps.setString(2, this.contact);

			ResultSet res = ps.executeQuery();

			while (res.next()) {
				contact.add(res.getString("categoryName"));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(
					_("Cannot get strengths of the selected attendee.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return contact;
	}

	/**
	 * Check if this attendee has got the given strength.
	 * 
	 * @param str
	 *            strength to check
	 * 
	 * @return true if the attendee has got the given strength, otherwise false
	 * 
	 * @throws DataException
	 *             If an error occurs while checking the given strength
	 */
	public boolean isStrength(String str) throws DataException {
		boolean isStr = false;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM AttendeesStrengths "
							+ "WHERE attendeeName=? AND attendeeContact=? AND categoryName=?");
			ps.setString(1, this.name);
			ps.setString(2, this.contact);
			ps.setString(3, str);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				isStr = true;
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(
					_("Cannot detect wether the given category is a strength of the attendee or not.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return isStr;
	}

	/**
	 * Adds the given strength to attendee's strengths.
	 * 
	 * @param str
	 *            strength to add
	 * 
	 * @throws DataException
	 *             If an error occurs while adding the given strength
	 */
	public void addStrength(String str) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		if (!isStrength(str)) {
			try {
				Connection conn = Data.getInstance().getAppData()
						.openConnection();

				PreparedStatement ps = conn
						.prepareStatement("INSERT INTO AttendeesStrengths "
								+ "(attendeeName, attendeeContact, categoryName) VALUES (?, ?, ?)");

				ps.setString(1, this.name);
				ps.setString(2, this.contact);
				ps.setString(3, str);

				ps.executeUpdate();

				ps.close();
				conn.close();

				Data.getInstance().getAppData().fireDataChanged();
			} catch (Exception e) {
				/*
				 * Not part of the unit testing, because this exception is only
				 * thrown if there occurs an internal error.
				 */
				throw new DataException(
						_("Cannot assign strength to the attendee.")
								+ " [NAME = " + this.name + "] "
								+ e.getMessage());
			}
		}
	}

	/**
	 * Replaces the given old strength with the given new strength.
	 * 
	 * @param oldStr
	 *            the old strength
	 * @param newStr
	 *            the new strength
	 * 
	 * @throws DataException
	 *             If an error occurs while replacing the strength
	 */
	public void editStrength(String oldStr, String newStr) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("UPDATE AttendeesStrengths "
							+ "SET categoryName=? WHERE attendeeName=? AND attendeeContact=? AND categoryName=?");

			ps.setString(1, newStr);
			ps.setString(2, this.name);
			ps.setString(3, this.contact);
			ps.setString(4, oldStr);

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException();
		}
	}

	/**
	 * Removes the given strength from attendee's strengths.
	 * 
	 * @param str
	 *            strength to remove
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the strength
	 */
	public void removeStrength(String str) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		if (isStrength(str)) {
			try {
				Connection conn = Data.getInstance().getAppData()
						.openConnection();

				PreparedStatement ps = conn
						.prepareStatement("DELETE FROM AttendeesStrengths "
								+ "WHERE attendeeName=? AND attendeeContact=? AND categoryName=?");

				ps.setString(1, this.name);
				ps.setString(2, this.contact);
				ps.setString(3, str);

				ps.executeUpdate();

				ps.close();
				conn.close();

				Data.getInstance().getAppData().fireDataChanged();
			} catch (Exception e) {
				/*
				 * Not part of the unit testing, because this exception is only
				 * thrown if there occurs an internal error.
				 */
				throw new DataException(
						_("Cannot remove strength of the attendee.")
								+ " [NAME = " + this.name + "] "
								+ e.getMessage());
			}
		}
	}

	/**
	 * Returns this attendee as Attendee object (Resi XML schema).
	 * 
	 * @return Attendee object
	 * 
	 * @throws DataException
	 *             If an error occurs while converting the attendee to an
	 *             Attendee object
	 */
	public Attendee getAsResiAttendee() throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		Attendee att = new Attendee();

		att.setContact(this.contact);
		att.setAspects(null);
		att.setId(null);
		att.setName(this.name);
		att.setRole(null);

		return att;
	}

}
