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
package org.revager.gui;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppCatalog;
import org.revager.gui.actions.popup.StrengthPopupWindowAction;
import org.revager.tools.GUITools;

/**
 * The popup window provides the possibility to give strengths to an attendee.
 */
@SuppressWarnings("serial")
public class StrengthPopupWindow extends JDialog {

	private final int NUMBER_OF_CATEGORIES = 7;

	private GridBagLayout gbl = new GridBagLayout();

	private JPanel panelContent = new JPanel(gbl);

	private ArrayList<String> cateList;

	private List<String> selCateList;

	private JTextField filterTxtFld;

	private JPanel catePnl = new JPanel(gbl);

	private JPanel selCatePnl = new JPanel(gbl);

	private JPanel panelCateList = new JPanel(gbl);

	public static enum ButtonClicked {
		OK, ABORT;
	};

	private ButtonClicked buttonClicked = null;

	/**
	 * Gets the sel cate list.
	 * 
	 * @return the sel cate list
	 */
	public List<String> getSelCateList() {
		return selCateList;
	}

	/**
	 * Instantiates a new strength popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param titleText
	 *            the title text
	 */
	public StrengthPopupWindow(Window parent, String titleText) {
		super(parent);

		setLayout(new BorderLayout());

		setUndecorated(true);

		setModal(true);

		JPanel panelBase = GUITools.newPopupBasePanel();

		JTextArea textTitle = GUITools.newPopupTitleArea(titleText);

		panelBase.add(textTitle, BorderLayout.NORTH);

		/*
		 * Creating the content panel
		 */
		panelContent.setBackground(Color.WHITE);

		cateList = new ArrayList<String>();

		try {
			for (AppCatalog appCat : Data.getInstance().getAppData()
					.getCatalogs()) {
				for (String cate : appCat.getCategories()) {
					if (!cateList.contains(cate))
						cateList.add(cate);
				}
			}
		} catch (DataException e) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

		selCateList = new ArrayList<String>();

		JLabel labelFilter = new JLabel(_("Filter:"));

		filterTxtFld = new JTextField();
		filterTxtFld.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				updateListView();
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		JButton buttonClear = GUITools.newImageButton(Data.getInstance()
				.getIcon("clear_22x22_0.png"),
				Data.getInstance().getIcon("clear_22x22.png"));
		buttonClear.setToolTipText(_("Reset filter"));
		buttonClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterTxtFld.setText("");

				updateListView();
			}
		});

		selCatePnl.setBackground(Color.WHITE);
		catePnl.setBackground(Color.WHITE);
		panelCateList.setBackground(Color.WHITE);

		creatingAllCatePnl();

		GUITools.addComponent(panelContent, gbl, labelFilter, 0, 0, 2, 1, 0, 0,
				10, 5, 0, 5, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gbl, filterTxtFld, 0, 1, 1, 1, 1.0,
				0, 5, 5, 10, 5, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gbl, buttonClear, 1, 1, 1, 1, 0.0,
				0, 5, 0, 10, 5, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gbl, panelCateList, 0, 2, 2, 1,
				1.0, 1.0, 5, 5, 10, 5, GridBagConstraints.BOTH,
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
		buttonAbort.addActionListener(new StrengthPopupWindowAction(this,
				ButtonClicked.ABORT));

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm.setToolTipText(_("Confirm"));
		buttonConfirm.addActionListener(new StrengthPopupWindowAction(this,
				ButtonClicked.OK));

		JPanel panelButtons = new JPanel(new BorderLayout());
		panelButtons.setBackground(UI.POPUP_BACKGROUND);
		panelButtons.setBorder(new EmptyBorder(3, 3, 3, 3));
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
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			updateListView();
		}

		super.setVisible(vis);
	}

	/**
	 * Update list view.
	 */
	private void updateListView() {
		cateList.clear();

		try {
			for (AppCatalog appCat : Data.getInstance().getAppData()
					.getCatalogs()) {
				for (String cate : appCat.getCategories(filterTxtFld.getText())) {
					if (!cateList.contains(cate))
						cateList.add(cate);
				}
			}
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(exc.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

		panelCateList.removeAll();
		catePnl.removeAll();
		selCatePnl.removeAll();
		creatingAllCatePnl();
		catePnl.validate();
		selCatePnl.validate();
		panelCateList.validate();
		panelContent.repaint();
	}

	/*
	 * method to create the categoryPanel
	 */
	/**
	 * Creating all cate pnl.
	 */
	private void creatingAllCatePnl() {
		int categoriesAdded = 0;

		for (String selCate : selCateList) {
			if (categoriesAdded >= NUMBER_OF_CATEGORIES) {
				break;
			}

			final String localSelCate = selCate;
			JCheckBox cateBx = new JCheckBox();
			cateBx.setSelected(true);
			cateBx.setText(selCate);
			cateBx.setToolTipText(selCate);
			cateBx.setBackground(Color.WHITE);
			cateBx.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED
							&& !selCateList.contains(localSelCate)) {
						selCateList.add(localSelCate);

					} else if (e.getStateChange() == ItemEvent.DESELECTED
							&& selCateList.contains(localSelCate)) {

						selCateList.remove(localSelCate);

						panelCateList.removeAll();
						catePnl.removeAll();
						selCatePnl.removeAll();
						creatingAllCatePnl();
						catePnl.validate();
						selCatePnl.validate();
						panelCateList.validate();
						panelContent.repaint();
					}
				}
			});

			GUITools.addComponent(selCatePnl, gbl, cateBx, 0, -1, 1, 1, 1.0, 0,
					5, 0, 5, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

			categoriesAdded++;
		}

		if (!selCateList.isEmpty()) {
			GUITools.addComponent(catePnl, gbl, new JSeparator(), 0, -1, 1, 1,
					1.0, 0, 5, 0, 5, 5, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);
		}

		for (String cate : cateList) {
			if (categoriesAdded >= NUMBER_OF_CATEGORIES) {
				break;
			}

			if (!selCateList.contains(cate)) {
				final String localCate = cate;
				JCheckBox cateBx = new JCheckBox();
				cateBx.setText(cate);
				cateBx.setToolTipText(cate);
				cateBx.setBackground(Color.WHITE);

				cateBx.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED
								&& !selCateList.contains(localCate)) {
							selCateList.add(localCate);

							panelCateList.removeAll();
							catePnl.removeAll();
							selCatePnl.removeAll();
							creatingAllCatePnl();
							catePnl.validate();
							selCatePnl.validate();
							panelCateList.validate();
							panelContent.repaint();
						} else if (e.getStateChange() == ItemEvent.DESELECTED
								&& selCateList.contains(localCate)) {

							selCateList.remove(localCate);

						}
					}
				});

				GUITools.addComponent(catePnl, gbl, cateBx, 0, -1, 1, 1, 1.0,
						0, 5, 0, 5, 0, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.NORTHWEST);

				categoriesAdded++;
			}

		}

		GUITools.addComponent(panelCateList, gbl, selCatePnl, 0, -1, 1, 1, 1.0,
				0, 5, 0, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelCateList, gbl, catePnl, 0, -1, 1, 1, 1.0,
				1.0, 5, 0, 10, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
	}

}
