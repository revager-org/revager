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

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.revager.app.ResiFileFilter;
import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.workers.LoadReviewWorker;
import org.revager.tools.GUITools;

/**
 * The Class LoadReviewAction.
 */
@SuppressWarnings("serial")
public class LoadReviewAction extends AbstractAction {

	/**
	 * Instantiates a new load review action.
	 */
	public LoadReviewAction() {
		super();

		putValue(SMALL_ICON, Data.getInstance().getIcon("menuOpen_16x16.png"));
		putValue(NAME, Data.getInstance().getLocaleStr("menu.file.openReview"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit
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
		FileChooser fileChooser = UI.getInstance().getFileChooser();

		fileChooser.setFile(null);

		if (fileChooser.showDialog(UI.getInstance().getMainFrame(),
				FileChooser.MODE_OPEN_FILE, ResiFileFilter.TYPE_REVIEW) == FileChooser.SELECTED_APPROVE) {
			String reviewPath = fileChooser.getFile().getAbsolutePath();

			GUITools.executeSwingWorker(new LoadReviewWorker(reviewPath));
		}
	}

}
