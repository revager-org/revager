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
package neos.resi.gui.actions.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.DefaultTreeModel;

import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.gui.HelpBrowserFrame;

/**
 * The Class ResetHelpAction.
 */
@SuppressWarnings("serial")
public class ResetHelpAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ResetHelp) {
		HelpBrowserFrame.getTree().setModel(
				new DefaultTreeModel(HelpBrowserFrame.getStandardRoot()));
		HelpBrowserFrame.getSrchTxtFld().setText("");
		int selectedRows[] = HelpBrowserFrame.getTree().getSelectionRows();
		HelpBrowserFrame.getTree().removeSelectionRows(selectedRows);
		HelpBrowserFrame.getTree().setSelectionRow(0);

		try {
			HelpBrowserFrame.getBodyPane().setText(
					"<H1>"
							+ Data.getInstance().getHelpData().getChapterTitle(
									"start")
							+ "</H1>"
							+ Data.getInstance().getHelpData()
									.getChapterContent("start"));
		} catch (DataException e) {
			/*
			 * do nothing
			 */
		}
	}

}
