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

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.tools.AppTools;

/**
 * This class manages the findings.
 */
public class FindingManagement {

	/**
	 * Instantiates a new finding management.
	 */
	FindingManagement() {
		super();
	}

	/**
	 * The resi data.
	 */
	private ResiData resiData = Data.getInstance().getResiData();

	/**
	 * The directory in which the external references are stored.
	 */
	private final String EXTREF_DIRECTORY = Data.getInstance().getAppData().getAppDataPath()
			+ Data.getInstance().getResource("extRefsDirectoryName");

	/**
	 * The work directory for temporary files.
	 */
	private final String WORK_DIRECTORY = Data.getInstance().getAppData().getAppDataPath()
			+ Data.getInstance().getResource("workDirectoryName");

	/**
	 * Gets the id of the last finding.
	 * 
	 * @return the last id
	 */
	public int getLastId() {
		int lastId = 0;
		for (Meeting m : Application.getInstance().getMeetingMgmt().getMeetings()) {
			Protocol prot = m.getProtocol();
			if (prot == null) {
				continue;
			}
			for (Finding f : prot.getFindings()) {
				Integer id = f.getId();
				if (id == null) {
					continue;
				}
				if (id > lastId) {
					lastId = id;
				}
			}
		}
		return lastId;
	}

	/**
	 * Returns the list of findings of the given protocol.
	 * 
	 * @param prot
	 *            the prot
	 * 
	 * @return list of findings
	 */
	public List<Finding> getFindings(Protocol prot) {
		return prot.getFindings();
	}

	/**
	 * Returns the finding with the given id; otherwise null
	 * 
	 * @param finding
	 *            id
	 * 
	 * @return finding
	 */
	public Finding getFinding(int findId, Protocol prot) {
		for (Finding f : getFindings(prot)) {
			if (f.getId() == findId) {
				return f;
			}
		}

		return null;
	}

	/**
	 * Returns the number of findings of the given protocol.
	 * 
	 * @param prot
	 *            the prot
	 * 
	 * @return number of findings
	 */
	public int getNumberOfFindings(Protocol prot) {
		return prot.getFindings().size();
	}

	/**
	 * Adds the given finding to the review.
	 * 
	 * @param description
	 *            the description
	 * @param severity
	 *            the severity
	 * @param prot
	 *            the protocol
	 * 
	 * @return the added finding
	 */
	public Finding addFinding(String description, String severity, Protocol prot) {
		Finding finding = new Finding();
		if (description == null) {
			description = "";
		}
		if (severity == null) {
			severity = "";
		}
		finding.setDescription(description);
		setLocalizedSeverity(finding, severity);
		return addFinding(finding, prot);
	}

	/**
	 * Adds the finding to the given protocol.
	 * 
	 * @param find
	 *            the finding
	 * @param prot
	 *            the protocol
	 * 
	 * @return the added finding
	 */
	public Finding addFinding(Finding find, Protocol prot) {
		if (prot.addFinding(find)) {
			find.setId(getLastId() + 1);
			resiData.fireDataChanged();
			return find;
		}
		return null;
	}

	/**
	 * Removes the finding from the given protocol.
	 * 
	 * @param find
	 *            the finding to remove
	 * @param prot
	 *            the protocol
	 */
	public void removeFinding(Finding find, Protocol prot) {
		prot.removeFinding(find);
		resiData.fireDataChanged();
	}

	// /**
	// * Replaces a finding in the given protocol by another one.
	// *
	// * @param oldFind
	// * the old finding
	// * @param newFind
	// * the new finding
	// * @param prot
	// * the protocol
	// */
	// public void editFinding(Finding oldFind, Finding newFind, Protocol prot)
	// {
	// if (prot.getFindings().contains(oldFind)) {
	// // TODO: continue
	// int id = oldFind.getId();
	// int index = prot.getFindings().indexOf(oldFind);
	//
	// prot.removeFinding(oldFind);
	//
	// newFind.setId(id);
	// prot.getFindings().add(index, newFind);
	//
	// resiData.fireDataChanged();
	// }
	// }

	/**
	 * Checks if the given finding is empty.
	 * 
	 * @param find
	 *            the finding to check
	 * 
	 * @return true, if the given finding is empty
	 */
	public boolean isFindingEmpty(Finding find) {
		boolean noAspects = find.getAspects().isEmpty();
		boolean noDesc = find.getDescription() == null || find.getDescription().trim().equals("");
		boolean noExtRefs = find.getExternalReferences().isEmpty();
		boolean noRefs = find.getReferences().isEmpty();

		if (noAspects && noDesc && noExtRefs && noRefs) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the given finding is not complete.
	 * 
	 * @param find
	 *            the finding to check
	 * 
	 * @return true, if the given finding is not complete
	 */
	public boolean isFindingNotComplete(Finding find) {
		boolean noDesc = find.getDescription() == null || find.getDescription().trim().equals("");

		if (noDesc) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the localized severity
	 * 
	 * @param find
	 *            The finding
	 * @return The localized severity
	 */
	public String getLocalizedSeverity(Finding find) {
		return Data.translate(find.getSeverity());
	}

	/**
	 * Sets the localized severity
	 * 
	 * @param find
	 *            The finding
	 * @param severity
	 *            The localized severity
	 */
	public void setLocalizedSeverity(Finding find, String severity) {
		String defLangSev = Data.getDefLangSeverity(severity);

		find.setSeverity(defLangSev);
	}

	/**
	 * Returns the list of references of the given finding.
	 * 
	 * @param find
	 *            the finding
	 * 
	 * @return the references if the given finding
	 */
	public List<String> getReferences(Finding find) {
		return find.getReferences();
	}

	/**
	 * Adds a reference to the list of references of the given finding.
	 * 
	 * @param ref
	 *            the reference to add
	 * @param find
	 *            the finding
	 */
	public void addReference(String ref, Finding find) {
		if (find.addReference(ref)) {
			resiData.fireDataChanged();
		}
	}

	/**
	 * Removes a reference from the list of references of the given finding.
	 * 
	 * @param ref
	 *            the reference to remove
	 * @param find
	 *            the finding
	 */
	public void removeReference(String ref, Finding find) {
		find.removeReference(ref);

		resiData.fireDataChanged();
	}

	/**
	 * Edits the given old reference.
	 * 
	 * @param find
	 *            the finding
	 * @param oldRef
	 *            the old reference
	 * @param newRef
	 *            the new reference
	 */
	public void editReference(String oldRef, String newRef, Finding find) {
		if (find.updateReference(oldRef, newRef)) {
			resiData.fireDataChanged();
		}
	}

	/**
	 * Returns the list of external references of the given finding.
	 * 
	 * @param find
	 *            the finding
	 * 
	 * @return the list of files
	 */
	public List<File> getExtReferences(Finding find) {
		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();
		List<File> list = new ArrayList<File>();
		for (String ref : find.getExternalReferences()) {
			File extRefFile = new File(EXTREF_DIRECTORY + revMgmt.getExtRefFileName(ref));
			list.add(extRefFile);
		}
		return list;
	}

	/**
	 * Adds a external reference by its file name to the list of references of
	 * the given finding.
	 * 
	 * @param file
	 *            the file
	 * @param find
	 *            the finding
	 * 
	 * @return true if adding the external reference was succesful; otherwise
	 *         false
	 */
	public boolean addExtReference(File file, Finding find) {
		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();

		String extRefFile = revMgmt.addExtRefFile(file);

		if (extRefFile != null) {

			find.addExternalReferences(extRefFile);

			resiData.fireDataChanged();

			return true;
		} else {
			/*
			 * Not part of unit testing because false is only returned if an
			 * internal error occured while adding the external reference.
			 */
			return false;
		}
	}

	/**
	 * Adds an image to the list of references of the given finding.
	 * 
	 * @param img
	 *            the image
	 * @param fileName
	 *            the file name
	 * @param find
	 *            the finding
	 * 
	 * @return true if adding the external reference was succesful; otherwise
	 *         false
	 */
	public boolean addExtReference(Image img, String fileName, Finding find) {
		File workDir = new File(WORK_DIRECTORY);
		File imgFile = new File(WORK_DIRECTORY, fileName);

		workDir.mkdir();

		try {
			imgFile = AppTools.writeImageToPNG(img, imgFile.getAbsolutePath());
		} catch (IOException e) {
			return false;
		}

		return addExtReference(imgFile, find);
	}

	/**
	 * Removes an external reference by its file name from the list of
	 * references of the given finding.
	 * 
	 * @param find
	 *            the finding
	 * @param file
	 *            the file
	 */
	public void removeExtReference(File file, Finding find) {
		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();

		String extRef = revMgmt.getExtRefURI(file.getName());
		file.delete();
		find.addExternalReferences(extRef);
		resiData.fireDataChanged();
	}

	/**
	 * Returns the list of aspects of the given finding.
	 * 
	 * @param find
	 *            the finding
	 * 
	 * @return the aspects of the given finding
	 */
	public List<String> getAspects(Finding find) {
		return find.getAspects();
	}

	/**
	 * Adds an aspect to the given finding.
	 * 
	 * @param asp
	 *            the aspects
	 * @param find
	 *            the finding
	 */
	public void addAspect(Aspect asp, Finding find) {
		String aspect = asp.getDirective() + " (" + asp.getCategory() + ")";
		if (find.addAspect(aspect)) {
			resiData.fireDataChanged();
		}
	}

	/**
	 * Adds an aspect to the given finding.
	 * 
	 * @param asp
	 *            the aspects
	 * @param find
	 *            the finding
	 */
	public void addAspect(String asp, Finding find) {
		if (find.addAspect(asp)) {
			resiData.fireDataChanged();
		}
	}

	/**
	 * Removes the aspect.
	 * 
	 * @param asp
	 *            the aspect
	 * @param find
	 *            the finding
	 */
	public void removeAspect(String asp, Finding find) {
		find.removeAspect(asp);
		resiData.fireDataChanged();
	}

	/**
	 * Checks if all findings of the chosen protocol are complete.
	 * 
	 * @param prot
	 *            the protocol
	 */
	public boolean areAllFindingsComplete(Protocol prot) {
		boolean comp = true;
		for (Finding find : prot.getFindings()) {
			if (find.getDescription() == null || find.getDescription().trim().equals("")) {
				comp = false;
			}
		}
		return comp;
	}

	/**
	 * Push up the given finding.
	 * 
	 * @param find
	 *            the finding
	 * @param prot
	 *            the protocol
	 */
	public void pushUpFinding(Finding find, Protocol prot) {
		if (!isTopFinding(find, prot)) {
			// TODO: continue
			int index = prot.getFindings().indexOf(find);

			prot.getFindings().remove(index);
			prot.getFindings().add(index - 1, find);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Push down the given finding.
	 * 
	 * @param find
	 *            the finding
	 * @param prot
	 *            the protocol
	 */
	public void pushDownFinding(Finding find, Protocol prot) {
		if (!isBottomFinding(find, prot)) {
			// TODO: continue
			int index = prot.getFindings().indexOf(find);

			prot.getFindings().remove(index);
			prot.getFindings().add(index + 1, find);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Push top the given finding.
	 * 
	 * @param find
	 *            the finding
	 * @param prot
	 *            the protocol
	 */
	public void pushTopFinding(Finding find, Protocol prot) {
		if (!isTopFinding(find, prot)) {
			// TODO: continue
			int index = prot.getFindings().indexOf(find);

			prot.getFindings().remove(index);
			prot.getFindings().add(0, find);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Push bottom the given finding.
	 * 
	 * @param find
	 *            the finding
	 * @param prot
	 *            the protocol
	 */
	public void pushBottomFinding(Finding find, Protocol prot) {
		if (!isTopFinding(find, prot)) {
			// TODO: continue
			int index = prot.getFindings().indexOf(find);

			prot.getFindings().remove(index);
			prot.getFindings().add(prot.getFindings().size(), find);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Checks if the given finding is at the top.
	 * 
	 * @param find
	 *            the finding
	 * @param prot
	 *            the protocol
	 * 
	 * @return true, if the finding is at the top
	 */
	public boolean isTopFinding(Finding find, Protocol prot) {
		int index = prot.getFindings().indexOf(find);

		if (index == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the given finding is at the bottom.
	 * 
	 * @param find
	 *            the finding
	 * @param prot
	 *            the protocol
	 * 
	 * @return true, if the finding is at the bottom
	 */
	public boolean isBottomFinding(Finding find, Protocol prot) {
		int index = prot.getFindings().indexOf(find);

		if (index == prot.getFindings().size() - 1) {
			return true;
		} else {
			return false;
		}
	}

}
