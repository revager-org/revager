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
package org.revager.gui.dialogs.assistant;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.revager.app.model.Data;
import org.revager.gui.AbstractDialog;
import org.revager.gui.UI;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.InitializeNewReviewAction;
import org.revager.gui.actions.assistant.GoToFirstScreenPnlAction;

/**
 * The class AssistantDialog.
 * 
 * @author D.Casciato
 * 
 */
@SuppressWarnings("serial")
public class AssistantDialog extends AbstractDialog {

	private Container currentPnl;
	private FirstScreenPanel firstScreenPanel = new FirstScreenPanel(this);
	private OpenReviewPanel openReviewPanel = new OpenReviewPanel(this);
	private Container addAttendeePanel = UI.getInstance().getAttendeeDialog()
			.getContentPane();

	private ImageIcon revagerIcon = Data.getInstance().getIcon(
			"revager_50x50.png");

	/**
	 * Action to shut down the application.
	 */
	private ActionListener exitApp = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}

	};

	/**
	 * Action to initialize a new review.
	 */
	private ActionListener initQuickRev = ActionRegistry.getInstance().get(
			InitializeNewReviewAction.class.getName());

	/*
	 * Strings
	 */
	private String firstScreenDescStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.firstDesc");

	private String openRevDescStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.openDesc");

	private String addAttDescStrng = Data.getInstance().getLocaleStr(
			"addYourself.description");
	private String localMode = "moderator";

	/*
	 * 
	 * Wizard buttons
	 */
	private JButton backBttn = new JButton();
	private JButton finishBttn = new JButton();
	private ImageIcon confirmIcon = Data.getInstance().getIcon(
			"buttonOk_16x16.png");
	private String confirmString = Data.getInstance().getLocaleStr("confirm");

	/**
	 * Sets the localMode parameter
	 * 
	 * @param string
	 */
	public void setLocalMode(String string) {
		this.localMode = string;

	}

	/**
	 * Returns the currentPanel.
	 * 
	 * @return
	 */
	public Container getCurrentPnl() {
		return currentPnl;
	}

	/**
	 * Sets the currentPanel.
	 * 
	 * @param currentPnl
	 */
	public void setCurrentPnl(Container currentPnl) {

		this.currentPnl = currentPnl;

	}

	/**
	 * Returns the FirstScreenPanel.
	 * 
	 * @return
	 */
	public FirstScreenPanel getFirstScreenPanel() {
		return firstScreenPanel;
	}

	/**
	 * Returns the OpenReviewPanel.
	 * 
	 * @return
	 */
	public OpenReviewPanel getOpenReviewPanel() {
		return openReviewPanel;
	}

	/**
	 * Returns the AddAttendeePanel.
	 * 
	 * @return
	 */
	public Container getAddAttendeePanel() {
		return addAttendeePanel;
	}

	/**
	 * Returns the BackButton.
	 * 
	 * @return
	 */
	public JButton getBackBttn() {
		return backBttn;
	}

	/**
	 * constructor
	 * 
	 * @param parent
	 */
	public AssistantDialog(Frame parent) {
		super(parent);

		defineWizardBttns();
		Data.getInstance().setMode("moderator");
		setIcon(revagerIcon);
		setCurrentPnl(firstScreenPanel);
		getContentPane().setLayout(new BorderLayout());
		backBttn.addActionListener(ActionRegistry.getInstance().get(
				GoToFirstScreenPnlAction.class.getName()));

		updateContents();

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setMinimumSize(new Dimension(650, 500));

		pack();
	}

	/**
	 * Defines the wizard buttons ; should be invoked in the constructor once.
	 */
	private void defineWizardBttns() {

		backBttn.setText(Data.getInstance().getLocaleStr("back"));
		backBttn.setIcon(Data.getInstance().getIcon("buttonBack_16x16.png"));

		finishBttn.setText(confirmString);
		finishBttn.setIcon(confirmIcon);

		this.addButton(backBttn);
		this.addButton(finishBttn);

		backBttn.setEnabled(false);
		finishBttn.setEnabled(false);
	}

	/**
	 * Updates the wizard buttons and should be invoked after the content has
	 * changed.
	 */
	public void updateWizardBttns() {
		finishBttn.removeActionListener(exitApp);
		finishBttn.removeActionListener(openReviewPanel.openExistRev);
		finishBttn.removeActionListener(initQuickRev);

		if (currentPnl == firstScreenPanel) {

			backBttn.setEnabled(false);
			finishBttn.setEnabled(false);

		} else if (currentPnl == addAttendeePanel) {

			backBttn.setEnabled(true);
			finishBttn.setEnabled(true);
			finishBttn.addActionListener(initQuickRev);

		} else if (currentPnl == openReviewPanel) {

			backBttn.setEnabled(true);
			finishBttn.setEnabled(true);
			finishBttn.addActionListener(openReviewPanel.openExistRev);
			if (openReviewPanel.getLastRevsVector().size() == 0)
				finishBttn.setEnabled(false);

		}
	}

	/**
	 * Updates the content area.
	 */
	public void updateContents() {

		this.getContentPane().removeAll();

		updateMessage();

		if (currentPnl == firstScreenPanel) {
			this.getContentPane().add(firstScreenPanel, BorderLayout.CENTER);
			Data.getInstance().setMode("moderator");
		} else if (currentPnl == addAttendeePanel) {
			this.getContentPane().add(addAttendeePanel, BorderLayout.CENTER);
			Data.getInstance().setMode("instant");

			UI.getInstance().getAttendeeDialog().setCurrentAttendee(null);
			UI.getInstance().getAttendeeDialog().getNameTxtFld().setText(
					System.getProperty("user.name"));
		} else if (currentPnl == openReviewPanel) {
			this.getContentPane().add(openReviewPanel, BorderLayout.CENTER);
			Data.getInstance().setMode(localMode);
		}

		this.getContentPane().validate();
		this.getContentPane().repaint();

	}

	/**
	 * Updates the description of the assistant, depending from the
	 * currentPanel.
	 */
	public void updateMessage() {

		if (currentPnl == firstScreenPanel) {
			setDescription(firstScreenDescStrng);
		} else if (currentPnl == addAttendeePanel) {
			setDescription(addAttDescStrng);
		} else if (currentPnl == openReviewPanel) {
			setDescription(openRevDescStrng);
		}

	}

}
