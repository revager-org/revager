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
package org.revager.gui.findings_list;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.revager.app.Application;
import org.revager.app.AspectManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Aspect;
import org.revager.gui.UI;
import org.revager.gui.actions.popup.AddAspToFindPopupWindowAction;
import org.revager.tools.GUITools;

/**
 * The Class AddAspToFindPopupWindow.
 */
@SuppressWarnings("serial")
public class AddAspToFindPopupWindow extends JDialog {

	private final int NUMBER_OF_ASPECTS = 7;

	private GridBagLayout gbl = new GridBagLayout();
	private JPanel panelContent = new JPanel(gbl);

	private List<Aspect> resiAspList;
	private List<Aspect> selAspList;

	private AspectManagement aspectMgmt = Application.getInstance()
			.getAspectMgmt();
	private JTextField filterTxtFld;

	private JPanel aspPnl = new JPanel(gbl);
	private JPanel selAspPnl = new JPanel(gbl);
	private JPanel allAspPnl = new JPanel(gbl);

	/**
	 * Gets the sel asp list.
	 * 
	 * @return the sel asp list
	 */
	public List<Aspect> getSelAspList() {
		return selAspList;
	}

	/**
	 * The Enum ButtonClicked.
	 */
	public static enum ButtonClicked {
		OK, ABORT;
	};

	private ButtonClicked buttonClicked = null;

	/**
	 * Instantiates a new adds the asp to find popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param titleText
	 *            the title text
	 * @param editing
	 *            the editing
	 */
	public AddAspToFindPopupWindow(Window parent) {
		super(parent);

		setLayout(new BorderLayout());

		setUndecorated(true);

		setModal(true);

		JPanel panelBase = GUITools.newPopupBasePanel();

		JTextArea textTitle = GUITools
				.newPopupTitleArea(_("Please select the aspects which should be added to the finding:"));

		panelBase.add(textTitle, BorderLayout.NORTH);

		panelContent.setBackground(Color.WHITE);

		/*
		 * 
		 * creating the inputPanel
		 */
		resiAspList = aspectMgmt.getAspects();
		selAspList = new ArrayList<Aspect>();

		JLabel aspFilLbl = new JLabel(_("Filter:"));
		filterTxtFld = new JTextField();

		filterTxtFld.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				resiAspList = aspectMgmt.getAspects(filterTxtFld.getText());
				allAspPnl.removeAll();
				aspPnl.removeAll();
				selAspPnl.removeAll();
				creatingAllAspPnl();
				aspPnl.validate();
				selAspPnl.validate();
				allAspPnl.validate();
				panelContent.repaint();
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		selAspPnl.setBackground(Color.WHITE);
		aspPnl.setBackground(Color.WHITE);
		allAspPnl.setBackground(Color.WHITE);

		creatingAllAspPnl();

		GUITools.addComponent(panelContent, gbl, aspFilLbl, 0, 0, 1, 1, 0, 0,
				5, 5, 0, 5, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gbl, filterTxtFld, 0, 1, 1, 1, 1.0,
				0, 5, 5, 10, 5, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gbl, allAspPnl, 0, 3, 1, 1, 1.0,
				1.0, 5, 5, 10, 5, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		panelBase.add(panelContent, BorderLayout.CENTER);

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(_("Abort"));
		buttonAbort.addActionListener(new AddAspToFindPopupWindowAction(this,
				ButtonClicked.ABORT));

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm.setToolTipText(_("Confirm"));
		buttonConfirm.addActionListener(new AddAspToFindPopupWindowAction(this,
				ButtonClicked.OK));

		JPanel panelButtons = new JPanel(new BorderLayout());
		panelButtons.setBackground(UI.POPUP_BACKGROUND);
		panelButtons.setBorder(BorderFactory.createLineBorder(
				panelButtons.getBackground(), 3));
		panelButtons.add(buttonAbort, BorderLayout.WEST);
		panelButtons.add(buttonConfirm, BorderLayout.EAST);

		/*
		 * Base panel
		 */
		panelBase.add(panelButtons, BorderLayout.SOUTH);

		add(panelBase, BorderLayout.CENTER);

		/*
		 * Set size and location
		 */
		Dimension popupSize = new Dimension(260, 410);

		setPreferredSize(popupSize);

		pack();

		setAlwaysOnTop(true);
		toFront();

		GUITools.setLocationToCursorPos(this);
	}

	/**
	 * Gets the button clicked.
	 * 
	 * @return the buttonClicked
	 */
	public ButtonClicked getButtonClicked() {
		return buttonClicked;
	}

	/**
	 * Sets the button clicked.
	 * 
	 * @param buttonClicked
	 *            the buttonClicked to set
	 */
	public void setButtonClicked(ButtonClicked buttonClicked) {
		this.buttonClicked = buttonClicked;
	}

	/*
	 * 
	 * methode to create the aspectPanel
	 */
	/**
	 * Creating all asp pnl.
	 */
	private void creatingAllAspPnl() {
		int aspectsAdded = 0;

		for (Aspect selAsp : selAspList) {
			if (aspectsAdded >= NUMBER_OF_ASPECTS) {
				break;
			}

			final Aspect localSelAsp = selAsp;
			JCheckBox aspBx = new JCheckBox();

			String aspStr = selAsp.getDirective().trim() + " ("
					+ selAsp.getCategory().trim() + ")";

			aspBx.setSelected(true);
			aspBx.setText(aspStr);
			aspBx.setToolTipText(aspStr);
			aspBx.setBackground(Color.WHITE);

			aspBx.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED
							&& !selAspList.contains(localSelAsp)) {
						selAspList.add(localSelAsp);

					} else if (e.getStateChange() == ItemEvent.DESELECTED
							&& selAspList.contains(localSelAsp)) {

						selAspList.remove(localSelAsp);

						allAspPnl.removeAll();
						aspPnl.removeAll();
						selAspPnl.removeAll();
						creatingAllAspPnl();
						aspPnl.validate();
						selAspPnl.validate();
						allAspPnl.validate();
						panelContent.repaint();
					}
				}
			});

			GUITools.addComponent(selAspPnl, gbl, aspBx, 0, -1, 1, 1, 1.0, 0,
					5, 0, 5, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

			aspectsAdded++;
		}

		if (!selAspList.isEmpty()) {
			GUITools.addComponent(aspPnl, gbl, new JSeparator(), 0, -1, 1, 1,
					1.0, 0, 5, 0, 0, 5, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);
		}

		for (Aspect asp : resiAspList) {
			if (aspectsAdded >= NUMBER_OF_ASPECTS) {
				break;
			}

			if (!selAspList.contains(asp)) {
				final Aspect localAsp = asp;

				String aspStr = asp.getDirective().trim() + " ("
						+ asp.getCategory().trim() + ")";

				JCheckBox aspBx = new JCheckBox();
				aspBx.setText(aspStr);
				aspBx.setToolTipText(aspStr);
				aspBx.setBackground(Color.WHITE);

				aspBx.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED
								&& !selAspList.contains(localAsp)) {
							selAspList.add(localAsp);

							allAspPnl.removeAll();
							aspPnl.removeAll();
							selAspPnl.removeAll();
							creatingAllAspPnl();
							aspPnl.validate();
							selAspPnl.validate();
							allAspPnl.validate();
							panelContent.repaint();
						} else if (e.getStateChange() == ItemEvent.DESELECTED
								&& selAspList.contains(localAsp)) {

							selAspList.remove(localAsp);

						}
					}
				});

				GUITools.addComponent(aspPnl, gbl, aspBx, 0, -1, 1, 1, 1.0, 0,
						5, 0, 5, 0, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.NORTHWEST);

				aspectsAdded++;
			}

		}

		GUITools.addComponent(allAspPnl, gbl, selAspPnl, 0, -1, 1, 1, 1.0, 0,
				5, 0, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(allAspPnl, gbl, aspPnl, 0, -1, 1, 1, 1.0, 1.0, 5,
				0, 10, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);

	}

}
