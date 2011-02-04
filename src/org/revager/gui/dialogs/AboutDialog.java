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
package org.revager.gui.dialogs;

import static org.revager.app.model.Data._;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.MatteBorder;

import org.revager.app.model.Data;
import org.revager.gui.AbstractDialog;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

/**
 * The Class AboutDialog.
 */
@SuppressWarnings("serial")
public class AboutDialog extends AbstractDialog {

	private GridBagLayout gbl = new GridBagLayout();

	/**
	 * Instantiates a new about dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public AboutDialog(Frame parent) {
		super(parent);
		setTitle(_("About"));
		setDescription(null);
		setIcon(Data.getInstance().getIcon("RevAger_300x74.png"));
		setLayout(gbl);

		Font fontTitle = new Font(Font.SANS_SERIF, Font.BOLD, 15);
		Font fontNormal = UI.STANDARD_FONT;

		JLabel appNameLbl = new JLabel(Data.getInstance()
				.getResource("appName"));
		appNameLbl.setFont(fontTitle);

		JLabel versionLbl = new JLabel(_("Version:"));
		versionLbl.setFont(fontNormal);
		JLabel buildLbl = new JLabel(_("Build:"));
		buildLbl.setFont(fontNormal);
		JLabel releaseLbl = new JLabel(_("Date of Release:"));
		releaseLbl.setFont(fontNormal);
		JLabel homepageLbl = new JLabel(_("Homepage:"));
		homepageLbl.setFont(fontNormal);
		JLabel emailLbl = new JLabel(_("E-mail:"));
		emailLbl.setFont(fontNormal);

		JLabel appVersionLbl = new JLabel(Data.getInstance().getResource(
				"appVersion"));
		appVersionLbl.setFont(fontNormal);

		JLabel appReleaseLbl = new JLabel(Data.getInstance().getResource(
				"appRelease"));
		appReleaseLbl.setFont(fontNormal);

		JLabel appBuildLbl = new JLabel(Data.getInstance().getResource(
				"appBuild"));
		appBuildLbl.setFont(fontNormal);

		JLabel authorInternetLbl = new JLabel(Data.getInstance().getResource(
				"authorInternet"));
		authorInternetLbl.setFont(fontNormal);
		authorInternetLbl.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		authorInternetLbl.setBorder(new MatteBorder(0, 0, 1, 0, UI.LINK_COLOR));
		authorInternetLbl.setForeground(UI.LINK_COLOR);
		authorInternetLbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(
							new URI(Data.getInstance().getResource(
									"authorInternet")));
				} catch (Exception exc) {
					/*
					 * do nothing
					 */
				}
			}
		});

		JLabel authorEmailLbl = new JLabel(Data.getInstance().getResource(
				"authorEmail"));
		authorEmailLbl.setFont(fontNormal);
		authorEmailLbl
				.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		authorEmailLbl.setBorder(new MatteBorder(0, 0, 1, 0, UI.LINK_COLOR));
		authorEmailLbl.setForeground(UI.LINK_COLOR);
		authorEmailLbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					Desktop.getDesktop().mail(
							new URI("mailto:"
									+ Data.getInstance().getResource(
											"authorEmail")
									+ "?subject=RevAger%20Feedback"));
				} catch (Exception exc) {
					/*
					 * do nothing
					 */
				}
			}
		});

		GUITools.addComponent(this, gbl, appNameLbl, 0, 0, 1, 1, 2, 1, 0, 25,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, versionLbl, 0, 1, 1, 1, 1, 1, 0, 25,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, appVersionLbl, 1, 1, 1, 1, 1, 1, 0, 0,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, buildLbl, 0, 2, 1, 1, 1, 1, 0, 25, 0,
				0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, appBuildLbl, 1, 2, 1, 1, 1, 1, 0, 0,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, releaseLbl, 0, 3, 1, 1, 1, 1, 0, 25,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, appReleaseLbl, 1, 3, 1, 1, 1, 1, 0, 0,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, homepageLbl, 0, 4, 1, 1, 1, 1, 0, 25,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, authorInternetLbl, 1, 4, 1, 1, 1, 1,
				0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, emailLbl, 0, 5, 1, 1, 1, 1, 0, 25, 0,
				0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, authorEmailLbl, 1, 5, 1, 1, 1, 1, 0,
				0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);

		setMinimumSize(new Dimension(350, 400));
		setSize(350, 400);
		setResizable(false);

		JButton cancel = new JButton(_("Close"), Data.getInstance().getIcon(
				"buttonClose_16x16.png"));
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		addButton(cancel);

		setLocationToCenter();
	}

}
