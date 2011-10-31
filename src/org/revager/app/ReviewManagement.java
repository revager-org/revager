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

import static org.revager.app.model.Data._;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.AttendeeReference;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Product;
import org.revager.app.model.schema.Protocol;
import org.revager.tools.FileTools;

/**
 * This class manages the review.
 */
public class ReviewManagement {

	/**
	 * Dummy product reference
	 */
	private final String DUMMY_PROD_REF = "DummyProductReference:xEUcr6cU5lLsiyuW";

	/**
	 * Instantiates the review management.
	 */
	ReviewManagement() {
		super();
	}

	/**
	 * Reference to the instance of the Resi data model to access the current
	 * review.
	 */
	private ResiData resiData = Data.getInstance().getResiData();

	/**
	 * The current review file.
	 */
	private final String REVIEW_FILE = Data.getInstance().getResource(
			"reviewFileName");

	/**
	 * The review info document file name.
	 */
	private final String REVIEW_INFO_DOC = _("Review_Information.pdf");

	/**
	 * The prefix to store external references in XML file.
	 */
	private final String EXTREF_PREFIX = Data.getInstance().getResource(
			"extRefURIPrefix");

	/**
	 * The directory for external references.
	 */
	private final String EXTREF_DIRECTORY = Data.getInstance().getAppData()
			.getAppDataPath()
			+ Data.getInstance().getResource("extRefsDirectoryName");

	/**
	 * Gets the file name of the given external reference.
	 * 
	 * @param extRefURI
	 *            the URI of the external reference
	 * 
	 * @return the file name of the external reference
	 */
	public String getExtRefFileName(String extRefURI) {
		String extRefFileName = null;

		try {
			extRefFileName = new URI(extRefURI).getPath();
		} catch (URISyntaxException e) {
			extRefFileName = extRefURI.substring(EXTREF_PREFIX.length())
					.replace("%20", " ");
		}

		if (extRefFileName.startsWith("/")) {
			extRefFileName = extRefFileName.substring(1);
		}

		return extRefFileName;
	}

	/**
	 * Gets the URI of an external reference by its file name.
	 * 
	 * @param fileName
	 *            the file name
	 * 
	 * @return the external reference as URI
	 */
	public String getExtRefURI(String fileName) {
		return URI.create(EXTREF_PREFIX + fileName.replace(" ", "%20"))
				.toASCIIString();
	}

	/**
	 * Checks if an external reference with the given file name exists.
	 * 
	 * @param fileName
	 *            the file name
	 * 
	 * @return true, if the external reference exists
	 */
	public boolean isExtRef(String fileName) {
		/*
		 * For the review file (review.xml) always return that it is referenced,
		 * because this file name has to be reserved for the review XML file.
		 */
		if (fileName.toLowerCase().equals(REVIEW_FILE.toLowerCase())
				|| fileName.toLowerCase().equals(REVIEW_INFO_DOC.toLowerCase())) {
			return true;
		}

		/*
		 * Check if a reference with this filename exists in any finding of the
		 * currently opened review
		 */
		for (Meeting m : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			Protocol prot = m.getProtocol();

			if (prot != null) {
				for (Finding f : prot.getFindings()) {
					if (f.getExternalReferences().contains(
							getExtRefURI(fileName))) {
						return true;
					}
				}
			}
		}

		/*
		 * Check if a reference with this filename exists in the product
		 * references
		 */
		if (resiData.getReview().getProduct().getReferences()
				.contains(getExtRefURI(fileName))) {
			return true;
		}

		return false;
	}

	/**
	 * Adds the file as external reference to the current review.
	 * 
	 * @param file
	 *            to add to external references
	 * 
	 * @return the internal URI to add to the review XML file; null if an error
	 *         occured while trying to add the file
	 */
	public String addExtRefFile(File file) {
		String extRefFile = file.getName();

		/*
		 * Check if the name for the file is already used and find a new one if
		 * it is so
		 */
		int index = 1;
		String extRefName = extRefFile;

		while (isExtRef(extRefName)) {
			int dotPos;

			if (extRefFile.lastIndexOf('.') != -1) {
				dotPos = extRefFile.lastIndexOf('.');
			} else {
				dotPos = extRefFile.length();
			}

			String fileName = extRefFile.substring(0, dotPos);
			String fileEnding = extRefFile.substring(dotPos);

			extRefName = fileName + index + fileEnding;

			index++;
		}

		File refFile = new File(EXTREF_DIRECTORY + extRefName);

		try {
			FileTools.copyFile(file, refFile);
		} catch (IOException e) {
			/*
			 * Not part of unit testing because this code is only reached if an
			 * internal error occurs.
			 */
			refFile.delete();
			return null;
		}

		return getExtRefURI(extRefName);
	}

	/**
	 * Validate all external references of the current review.
	 */
	public void validateExtRefs() {
		/*
		 * Remove duplicate product file references
		 */
		List<String> prodRefs = resiData.getReview().getProduct()
				.getReferences();
		List<String> refList = new ArrayList<String>();
		int i = 0;

		while (prodRefs.size() > i) {
			String ref = prodRefs.get(i);

			i++;

			if (refList.contains(ref)) {
				prodRefs.remove(ref);
				i--;
			} else {
				refList.add(ref);
			}
		}

		/*
		 * Remove invalid and wrong product file references
		 */
		i = 0;

		while (prodRefs.size() > i) {
			String ref = prodRefs.get(i);

			i++;

			/*
			 * Remove references with no file
			 */
			if (ref.startsWith(EXTREF_PREFIX)) {
				File extRefFile = new File(EXTREF_DIRECTORY
						+ getExtRefFileName(ref));

				if (!extRefFile.exists()) {
					prodRefs.remove(ref);
					i--;
				}
			}
		}

		/*
		 * Remove duplicate references in the findings of the currently opened
		 * review
		 */
		for (Meeting m : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			Protocol prot = m.getProtocol();

			if (prot != null) {

				for (Finding f : prot.getFindings()) {
					refList = new ArrayList<String>();
					i = 0;

					while (f.getExternalReferences().size() > i) {
						String ref = f.getExternalReferences().get(i);

						i++;

						if (refList.contains(ref)) {
							f.getExternalReferences().remove(ref);
							i--;
						} else {
							refList.add(ref);
						}
					}
				}
			}
		}

		/*
		 * Remove invalid and wrong references in the findings of the currently
		 * opened review
		 */
		for (Meeting m : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			Protocol prot = m.getProtocol();

			if (prot != null) {
				for (Finding f : prot.getFindings()) {
					i = 0;

					while (f.getExternalReferences().size() > i) {
						String ref = f.getExternalReferences().get(i);

						i++;

						/*
						 * Remove invalid references
						 */
						if (!ref.startsWith(EXTREF_PREFIX)) {
							f.getExternalReferences().remove(ref);
							i--;
						} else {
							/*
							 * Remove references with no file
							 */
							File extRefFile = new File(EXTREF_DIRECTORY
									+ getExtRefFileName(ref));

							if (!extRefFile.exists()) {
								f.getExternalReferences().remove(ref);
								i--;
							}
						}
					}
				}
			}
		}

		/*
		 * Remove files which are not referenced
		 */
		for (File f : FileTools.getListOfFiles(new File(EXTREF_DIRECTORY))) {
			if (!isExtRef(f.getName())) {
				f.delete();
			}
		}
	}

	/**
	 * Checks if the current review has external references (product and/or
	 * findings).
	 * 
	 * @return true if external references exist
	 */
	public boolean hasExtRefs() {
		if (!getExtProdReferences().isEmpty()) {
			return true;
		}

		for (Meeting m : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			if (m.getProtocol() != null) {
				for (Finding f : m.getProtocol().getFindings()) {
					if (!f.getExternalReferences().isEmpty()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Refactors the whole review (including refactoring the ids and validating
	 * the external references).
	 */
	public void refactorReview() {
		/*
		 * Refactor and validate the ids
		 */
		Application.getInstance().getAspectMgmt().refactorIds();
		Application.getInstance().getAttendeeMgmt().refactorIds();
		Application.getInstance().getFindingMgmt().refactorIds();

		Application.getInstance().getMeetingMgmt().initDummyMeeting();

		Application.getInstance().getSeverityMgmt().validateSeverities();

		validateExtRefs();

		/*
		 * If the review has got a recommendation, then remove all meetings with
		 * no protocol if they are not canceled.
		 */
		if (getRecommendation() != null
				&& !getRecommendation().trim().equals("")) {
			MeetingManagement meetMgmt = Application.getInstance()
					.getMeetingMgmt();

			for (Meeting m : Application.getInstance().getMeetingMgmt()
					.getMeetings()) {
				if (m.getProtocol() == null && !meetMgmt.isMeetingCanceled(m)) {
					meetMgmt.removeMeeting(m);
				}
			}
		}

		/*
		 * Trim the strings of the review properties.
		 */
		setReviewName(getReviewName().trim());
		setReviewDescription(getReviewDescription().trim());
		setReviewComments(getReviewComments().trim());

		setImpression(getImpression().trim());
		setRecommendation(getRecommendation().trim());

		setProductName(getProductName().trim());
		setProductVersion(getProductVersion().trim());

		for (String ref : getProductReferences()) {
			String trimmedRef = ref.trim();

			removeProductReference(ref);
			addProductReference(trimmedRef);
		}

		/*
		 * Trim the strings of the severities.
		 */
		SeverityManagement sevMgmt = Application.getInstance()
				.getSeverityMgmt();

		List<String> sevs = new ArrayList<String>();
		for (String sev : sevMgmt.getSeverities()) {
			sevs.add(sev.trim());
		}
		resiData.getReview().getSeverities().getSeverities().clear();
		resiData.getReview().getSeverities().getSeverities().addAll(sevs);

		/*
		 * Trim the strings of the attendees.
		 */
		for (Attendee att : Application.getInstance().getAttendeeMgmt()
				.getAttendees()) {
			att.setContact(att.getContact().trim());
			att.setName(att.getName().trim());
		}

		/*
		 * Trim the strings of the aspects.
		 */
		for (Aspect asp : Application.getInstance().getAspectMgmt()
				.getAspects()) {
			asp.setCategory(asp.getCategory().trim());
			asp.setDirective(asp.getDirective().trim());
			asp.setDescription(asp.getDescription().trim());
		}

		/*
		 * Trim the strings of the meetings and their protocols.
		 */
		for (Meeting meet : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			if (meet.getCanceled() != null) {
				meet.setCanceled(meet.getCanceled().trim());
			}

			meet.setComments(meet.getComments().trim());
			meet.setPlannedLocation(meet.getPlannedLocation().trim());

			Protocol prot = meet.getProtocol();

			if (prot != null) {
				prot.setComments(prot.getComments().trim());
				prot.setLocation(prot.getLocation().trim());

				// TODO
				/*
				 * workaround
				 */
				for (AttendeeReference att : prot.getAttendeeReferences()) {
					if (att.getPreparationTime() == null) {
						try {
							att.setPreparationTime(DatatypeFactory
									.newInstance().newDuration(0));
						} catch (DatatypeConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				for (Finding find : prot.getFindings()) {
					find.setDescription(find.getDescription().trim());
					find.setSeverity(find.getSeverity().trim());

					List<String> refs = new ArrayList<String>();
					for (String ref : find.getReferences()) {
						refs.add(ref.trim());
					}
					find.getReferences().clear();
					find.getReferences().addAll(refs);

					List<String> asps = new ArrayList<String>();
					for (String asp : find.getAspects()) {
						asps.add(asp.trim());
					}
					find.getAspects().clear();
					find.getAspects().addAll(asps);
				}
			}
		}
	}

	/**
	 * Sets the name of the review.
	 * 
	 * @param name
	 *            name of the review
	 */
	public void setReviewName(String name) {
		name = name.trim();

		resiData.getReview().setName(name);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the name of the review.
	 * 
	 * @return name of the review
	 */
	public String getReviewName() {
		return resiData.getReview().getName();
	}

	/**
	 * Sets the description of the review.
	 * 
	 * @param desc
	 *            description of the review
	 */
	public void setReviewDescription(String desc) {
		desc = desc.trim();

		resiData.getReview().setDescription(desc);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the description of the review.
	 * 
	 * @return description of the review
	 */
	public String getReviewDescription() {
		return resiData.getReview().getDescription();
	}

	/**
	 * Sets the comments of the review.
	 * 
	 * @param com
	 *            the comments
	 */
	public void setReviewComments(String com) {
		com = com.trim();

		resiData.getReview().setComments(com);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the comments of the review.
	 * 
	 * @return comments of the review
	 */
	public String getReviewComments() {
		return resiData.getReview().getComments();
	}

	/**
	 * Sets the name of the reviewed product.
	 * 
	 * @param name
	 *            name of the reviewed product
	 */
	public void setProductName(String name) {
		name = name.trim();

		resiData.getReview().getProduct().setName(name);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the name of the reviewed product.
	 * 
	 * @return name of the reviewed product
	 */
	public String getProductName() {
		if (resiData.getReview().getProduct() == null) {
			resiData.getReview().setProduct(new Product());
			resiData.getReview().getProduct().setName("");
			resiData.getReview().getProduct().setVersion("");
		}

		return resiData.getReview().getProduct().getName();
	}

	/**
	 * Sets the version of the reviewed product.
	 * 
	 * @param version
	 *            of the reviewed product
	 */
	public void setProductVersion(String version) {
		version = version.trim();

		resiData.getReview().getProduct().setVersion(version);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the version of the reviewed product.
	 * 
	 * @return product version of the review
	 */
	public String getProductVersion() {
		if (resiData.getReview().getProduct() == null) {
			resiData.getReview().setProduct(new Product());
			resiData.getReview().getProduct().setName("");
			resiData.getReview().getProduct().setVersion("");
		}

		return resiData.getReview().getProduct().getVersion();
	}

	/**
	 * Adds a dummy product reference.
	 */
	public void addDummyProdReference() {
		addProductReference(DUMMY_PROD_REF);
	}

	/**
	 * Checks if the given product reference exists.
	 * 
	 * @param ref
	 *            the reference
	 * 
	 * @return true, if the product reference exists
	 */
	public boolean isProductReference(String ref) {
		if (getProductReferences().contains(ref)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the product's textual references.
	 * 
	 * @return product references of the review
	 */
	public List<String> getProductReferences() {
		List<String> textRefs = new ArrayList<String>();

		for (String ref : resiData.getReview().getProduct().getReferences()) {
			if (!ref.startsWith(EXTREF_PREFIX) && !ref.equals(DUMMY_PROD_REF)) {
				textRefs.add(ref);
			}
		}

		return textRefs;
	}

	/**
	 * Adds a textual reference to the list of references of the reviewed
	 * product.
	 * 
	 * @param ref
	 *            the reference
	 */
	public void addProductReference(String ref) {
		ref = ref.trim();

		if (!resiData.getReview().getProduct().getReferences().contains(ref)) {
			resiData.getReview().getProduct().getReferences().add(ref);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Removes a textual reference from the list of references of the reviewed
	 * product.
	 * 
	 * @param ref
	 *            the reference
	 */
	public void removeProductReference(String ref) {
		resiData.getReview().getProduct().getReferences().remove(ref);

		resiData.fireDataChanged();
	}

	/**
	 * Edits the given product reference.
	 * 
	 * @param oldRef
	 *            the old reference
	 * @param newRef
	 *            the new reference
	 */
	public void editProductReference(String oldRef, String newRef) {
		int indexOld = resiData.getReview().getProduct().getReferences()
				.indexOf(oldRef);

		if (!newRef.trim().equals("")) {
			resiData.getReview().getProduct().getReferences()
					.set(indexOld, newRef);
		}

		resiData.fireDataChanged();
	}

	/**
	 * Returns the list of external references of the product.
	 * 
	 * @return list of external references
	 */
	public List<File> getExtProdReferences() {
		List<File> fileRefs = new ArrayList<File>();

		for (String ref : resiData.getReview().getProduct().getReferences()) {
			if (ref.startsWith(EXTREF_PREFIX)) {
				File extRefFile = new File(EXTREF_DIRECTORY
						+ getExtRefFileName(ref));

				fileRefs.add(extRefFile);
			}
		}

		return fileRefs;
	}

	/**
	 * Gets the external product reference by its file name.
	 * 
	 * @param fileName
	 *            the file name
	 * 
	 * @return the external product reference by file name
	 */
	public File getExtProdRefByName(String fileName) {
		for (File ref : getExtProdReferences()) {
			if (ref.getName().equals(fileName)) {
				return ref;
			}
		}

		return null;
	}

	/**
	 * Adds the external reference by its file name to the list of references of
	 * the product.
	 * 
	 * @param file
	 *            the file to add
	 * 
	 * @return true if adding the external reference was succesful; otherwise
	 *         false
	 */
	public boolean addExtProdReference(File file) {
		List<String> prodRefs = resiData.getReview().getProduct()
				.getReferences();

		String extRefFile = addExtRefFile(file);

		if (extRefFile != null) {
			prodRefs.add(extRefFile);

			resiData.fireDataChanged();

			return true;
		} else {
			/*
			 * Not part of unit testing because this method only returns false
			 * if an internal error occurs.
			 */
			return false;
		}
	}

	/**
	 * Removes an external reference by its file name from the list of
	 * references of the product.
	 * 
	 * @param file
	 *            the file
	 */
	public void removeExtProdReference(File file) {
		List<String> prodRefs = resiData.getReview().getProduct()
				.getReferences();

		String extRef = getExtRefURI(file.getName());

		file.delete();

		prodRefs.remove(extRef);

		resiData.fireDataChanged();
	}

	/**
	 * Sets the impression of the reviewed product.
	 * 
	 * @param impr
	 *            the impression
	 */
	public void setImpression(String impr) {
		impr = impr.trim();

		resiData.getReview().setImpression(impr);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the impression of the review.
	 * 
	 * @return impression of the review
	 */
	public String getImpression() {
		return resiData.getReview().getImpression();
	}

	/**
	 * Sets the recommendation of the reviewed product.
	 * 
	 * @param rec
	 *            the recommendation
	 */
	public void setRecommendation(String rec) {
		rec = Data.getDefLangRecommendation(rec.trim());

		resiData.getReview().setRecommendation(rec);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the recommendation of the review.
	 * 
	 * @return recommendation of the review
	 */
	public String getRecommendation() {
		return Data._(resiData.getReview().getRecommendation());
	}

	/**
	 * Returns the number of all (textual and external) product references of
	 * the review.
	 * 
	 * @return number of product references of the review
	 */
	public int getNumberOfProdRefs() {
		return getProductReferences().size() + getExtProdReferences().size();
	}

	/**
	 * Returns the number of severities.
	 * 
	 * @return the number of severities
	 */
	public int getNumberOfSeverities() {
		return resiData.getReview().getSeverities().getSeverities().size();
	}

	/**
	 * Gets the number of attendees.
	 * 
	 * @return the number of attendees
	 */
	public int getNumberOfAttendees() {
		return Application.getInstance().getAttendeeMgmt()
				.getNumberOfAttendees();
	}

	/**
	 * Gets the number of aspects.
	 * 
	 * @return the number of aspects
	 */
	public int getNumberOfAspects() {
		return Application.getInstance().getAspectMgmt().getAspects().size();
	}

	/**
	 * Gets the number of meetings.
	 * 
	 * @return the number of meetings
	 */
	public int getNumberOfMeetings() {
		return Application.getInstance().getMeetingMgmt().getMeetings().size();
	}

	/**
	 * Gets the number of findings.
	 * 
	 * @return the number of findings
	 */
	public int getNumberOfFindings() {
		int number = 0;

		for (Meeting m : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			Protocol prot = m.getProtocol();

			if (prot != null) {
				number = number + prot.getFindings().size();
			}
		}

		return number;
	}
}
