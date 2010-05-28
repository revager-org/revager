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
package org.revager.gui.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.revager.app.Application;
import org.revager.app.ResiFileFilter;
import org.revager.app.ReviewManagement;
import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.workers.SaveReviewWorker;
import org.revager.tools.GUITools;

/**
 * The Class SaveReviewAction.
 */
@SuppressWarnings("serial")
public class SaveReviewAction extends AbstractAction {

	private static final String ENDING_XML = "."
			+ Data.getInstance().getResource("fileEndingReviewXML")
					.toLowerCase();

	private boolean exitApplication = false;

	/**
	 * Instantiates a new save review action.
	 */
	public SaveReviewAction() {
		super();

		putValue(SMALL_ICON, Data.getInstance().getIcon("menuSave_16x16.png"));
		putValue(NAME, Data.getInstance().getLocaleStr("menu.file.saveReview"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();

		boolean exitApp = exitApplication;
		this.exitApplication = false;

		if (!Application.getInstance().getApplicationCtl().isReviewStorable()) {
			String messageText = Data.getInstance().getLocaleStr(
					"message.notStorable")
					+ "\n\n"
					+ Application.getInstance().getApplicationCtl()
							.getReasonForRevNotStorable();

			JOptionPane.showMessageDialog(UI.getInstance().getMainFrame(),
					GUITools.getMessagePane(messageText), Data.getInstance()
							.getLocaleStr("info"),
					JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		FileChooser fileChooser = UI.getInstance().getFileChooser();
		String reviewPath = Data.getInstance().getResiData().getReviewPath();

		if (reviewPath == null) {
			fileChooser.setFile(new File(Application.getInstance()
					.getApplicationCtl().getReviewNameSuggestion()));

			if (fileChooser.showDialog(UI.getInstance().getMainFrame(),
					FileChooser.MODE_SAVE_FILE, ResiFileFilter.TYPE_REVIEW) == FileChooser.SELECTED_APPROVE) {
				reviewPath = fileChooser.getFile().getAbsolutePath();
			}
		}

		if (reviewPath != null) {
			Object[] options = {
					Data.getInstance().getLocaleStr("button.ignore"),
					Data.getInstance().getLocaleStr("button.correct") };

			if (revMgmt.hasExtRefs()
					&& (reviewPath.trim().toLowerCase().endsWith(ENDING_XML) || reviewPath
							.trim().toLowerCase().endsWith(".xml"))
					&& JOptionPane.showOptionDialog(UI.getInstance()
							.getMainFrame(), GUITools.getMessagePane(Data
							.getInstance().getLocaleStr(
									"message.hasExtRefsXMLWarn")), Data
							.getInstance().getLocaleStr("question"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]) == JOptionPane.NO_OPTION) {
				ActionRegistry.getInstance().get(
						SaveReviewAsAction.class.getName()).actionPerformed(e);

				return;
			} else {
				GUITools.executeSwingWorker(new SaveReviewWorker(reviewPath,
						exitApp));
			}
		}
	}

	/**
	 * Sets the property wether the application should be closed or not.
	 * 
	 * @param exitApplication
	 *            true if the application should be closed
	 */
	public void setExitApplication(boolean exitApplication) {
		this.exitApplication = exitApplication;
	}

}
