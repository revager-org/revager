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
package org.revager.gui.aspects_manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.revager.app.Application;
import org.revager.app.AspectManagement;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAspect;
import org.revager.app.model.appdata.AppCatalog;
import org.revager.app.model.schema.Aspect;
import org.revager.gui.UI;
import org.revager.tools.GUITools;


/**
 * The Class EditAspectPopupWindow.
 */
@SuppressWarnings("serial")
public class EditAspectPopupWindow extends JDialog {

	/**
	 * The Enum ButtonClicked.
	 */
	public static enum ButtonClicked {
		OK, ABORT;
	};

	/**
	 * The Enum Mode.
	 */
	private static enum Mode {
		RESI_ASPECT, APP_ASPECT, APP_CATALOG;
	}

	private static final String STANDARD_CATEGORY = Data.getInstance()
			.getLocaleStr("aspectsManager.stdCategory");

	private AspectManagement aspMgmt = Application.getInstance()
			.getAspectMgmt();
	private ApplicationData appData = Data.getInstance().getAppData();

	private Mode mode = null;

	private ButtonClicked buttonClicked = null;

	private Aspect aspect = null;
	private AppCatalog appCatalog = null;
	private AppAspect appAspect = null;

	/**
	 * Instantiates a new edits the aspect popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param aspect
	 *            the aspect
	 */
	public EditAspectPopupWindow(Window parent, Aspect aspect) {
		this(parent);

		this.aspect = aspect;
		this.mode = Mode.RESI_ASPECT;

		createPopupContent();
	}

	/**
	 * Instantiates a new edits the aspect popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param aspect
	 *            the aspect
	 */
	public EditAspectPopupWindow(Window parent, AppAspect aspect) {
		this(parent);

		this.appAspect = aspect;
		this.mode = Mode.APP_ASPECT;

		createPopupContent();
	}

	/**
	 * Instantiates a new edits the aspect popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param catalog
	 *            the catalog
	 */
	public EditAspectPopupWindow(Window parent, AppCatalog catalog) {
		this(parent);

		this.appCatalog = catalog;
		this.mode = Mode.APP_CATALOG;

		createPopupContent();
	}

	/**
	 * Instantiates a new edits the aspect popup window.
	 * 
	 * @param parent
	 *            the parent
	 */
	private EditAspectPopupWindow(Window parent) {
		super(parent);

		setLayout(new BorderLayout());

		setUndecorated(true);

		setModal(true);
	}

	/**
	 * Creates the popup content.
	 */
	private void createPopupContent() {
		JPanel panelBase = GUITools.newPopupBasePanel();

		GridBagLayout gblContent = new GridBagLayout();
		JPanel panelContent = new JPanel(gblContent);
		panelContent.setBackground(Color.WHITE);
		panelContent.setBorder(new EmptyBorder(0, 0, 7, 0));

		String title = null;

		JLabel labelTitle = new JLabel();

		final JTextField textFieldTitle = new JTextField();
		final JTextArea textAreaDescription = new JTextArea();
		textAreaDescription.setLineWrap(true);
		textAreaDescription.setWrapStyleWord(true);
		JScrollPane scrollDescription = new JScrollPane(textAreaDescription,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final JComboBox boxCatalog = new JComboBox();
		boxCatalog.setEditable(false);
		final JComboBox boxCategory = new JComboBox();
		boxCategory.setEditable(true);

		try {
			switch (mode) {
			case RESI_ASPECT:
				title = Data.getInstance().getLocaleStr(
						"popup.editAspect.titleAspect");
				labelTitle.setText(Data.getInstance().getLocaleStr(
						"popup.editAspect.directive"));

				textFieldTitle.setText(aspect.getDirective());
				textAreaDescription.setText(aspect.getDescription());

				for (String cat : aspMgmt.getCategories()) {
					boxCategory.addItem(cat);
				}

				boxCategory.setSelectedItem(aspect.getCategory());

				break;
			case APP_ASPECT:
				title = Data.getInstance().getLocaleStr(
						"popup.editAspect.titleAspect");
				labelTitle.setText(Data.getInstance().getLocaleStr(
						"popup.editAspect.directive"));

				textFieldTitle.setText(appAspect.getDirective());
				textAreaDescription.setText(appAspect.getDescription());

				for (String cat : appAspect.getCatalog().getCategories()) {
					boxCategory.addItem(cat);
				}

				boxCategory.setSelectedIndex(0);

				String selCategory = UI.getInstance().getAspectsManagerFrame()
						.getSelectedCategory();
				if (selCategory != null
						&& appAspect.getCategory().equals(STANDARD_CATEGORY)) {
					appAspect.setCategory(selCategory);
				}

				boxCategory.setSelectedItem(appAspect.getCategory());

				for (AppCatalog appCat : appData.getCatalogs()) {
					boxCatalog.addItem(appCat);
				}

				boxCatalog.setSelectedItem(appAspect.getCatalog());

				break;
			case APP_CATALOG:
				title = Data.getInstance().getLocaleStr(
						"popup.editAspect.titleCatalog");
				labelTitle.setText(Data.getInstance().getLocaleStr(
						"popup.editAspect.catalogName"));

				textFieldTitle.setText(appCatalog.getName());
				textAreaDescription.setText(appCatalog.getDescription());

				boxCategory.setVisible(false);

				break;

			default:
				break;
			}
		} catch (DataException e) {
			JOptionPane.showMessageDialog(this, GUITools.getMessagePane(e
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		textFieldTitle.setCaretPosition(0);
		textAreaDescription.setCaretPosition(0);
		((JTextField) boxCategory.getEditor().getEditorComponent())
				.setCaretPosition(0);

		JTextArea textTitle = GUITools.newPopupTitleArea(title);

		panelBase.add(textTitle, BorderLayout.NORTH);

		JLabel labelDescription = new JLabel(Data.getInstance().getLocaleStr(
				"popup.editAspect.description"));
		JLabel labelCatalog = new JLabel(Data.getInstance().getLocaleStr(
				"popup.editAspect.catalog"));
		JLabel labelCategory = new JLabel(Data.getInstance().getLocaleStr(
				"popup.editAspect.category"));

		int ins = 5;

		GUITools.addComponent(panelContent, gblContent, labelTitle, 0, 0, 1, 1,
				1.0, 0.0, ins * 2, ins, ins, ins,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gblContent, textFieldTitle, 0, 1,
				1, 1, 1.0, 0.0, 0, ins, ins, ins,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gblContent, labelDescription, 0, 2,
				1, 1, 1.0, 0.0, ins * 2, ins, ins, ins,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelContent, gblContent, scrollDescription, 0,
				3, 1, 1, 1.0, 1.0, 0, ins, ins, ins, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		if (mode == Mode.APP_ASPECT || mode == Mode.RESI_ASPECT) {
			GUITools
					.addComponent(panelContent, gblContent, labelCategory, 0,
							4, 1, 1, 1.0, 0.0, ins * 2, ins, ins, ins,
							GridBagConstraints.HORIZONTAL,
							GridBagConstraints.NORTHWEST);
			GUITools
					.addComponent(panelContent, gblContent, boxCategory, 0, 5,
							1, 1, 1.0, 0.0, 0, ins, ins, ins,
							GridBagConstraints.HORIZONTAL,
							GridBagConstraints.NORTHWEST);
		}

		if (mode == Mode.APP_ASPECT) {
			GUITools
					.addComponent(panelContent, gblContent, labelCatalog, 0, 6,
							1, 1, 1.0, 0.0, ins * 2, ins, ins, ins,
							GridBagConstraints.HORIZONTAL,
							GridBagConstraints.NORTHWEST);
			GUITools
					.addComponent(panelContent, gblContent, boxCatalog, 0, 7,
							1, 1, 1.0, 0.0, 0, ins, ins * 2, ins,
							GridBagConstraints.HORIZONTAL,
							GridBagConstraints.NORTHWEST);
		}

		panelBase.add(panelContent, BorderLayout.CENTER);

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(Data.getInstance().getLocaleStr("abort"));
		buttonAbort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonClicked = ButtonClicked.ABORT;

				setVisible(false);
			}
		});

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm
				.setToolTipText(Data.getInstance().getLocaleStr("confirm"));
		buttonConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField boxCatTextField = (JTextField) boxCategory
						.getEditor().getEditorComponent();

				textFieldTitle.setBorder(UI.STANDARD_BORDER_INLINE);
				boxCategory.setBorder(null);

				if (textFieldTitle.getText().trim().equals("")) {
					textFieldTitle.setBorder(UI.MARKED_BORDER_INLINE);

					return;
				}

				if (boxCategory.isVisible()
						&& boxCatTextField.getText().trim().equals("")) {
					boxCategory.setBorder(UI.MARKED_BORDER);

					return;
				}

				buttonClicked = ButtonClicked.OK;

				try {
					String category = boxCatTextField.getText().trim();

					if (category.trim().equals("")) {
						category = STANDARD_CATEGORY;
					}

					String directive = textFieldTitle.getText().trim();
					String description = textAreaDescription.getText().trim();

					switch (mode) {
					case RESI_ASPECT:
						aspect.setCategory(category);
						aspect.setDescription(description);
						aspect.setDirective(directive);

						aspMgmt.editAspect(aspect, aspect);

						break;
					case APP_ASPECT:
						AppCatalog selCatalog = (AppCatalog) boxCatalog
								.getSelectedItem();

						if (selCatalog.equals(appAspect.getCatalog())) {
							appAspect.setDirective(directive);
							appAspect.setCategory(category);
							appAspect.setDescription(description);
						} else {
							appAspect.getCatalog().removeAspect(appAspect);

							appAspect = selCatalog.newAspect(directive,
									description, category);
						}

						UI.getInstance().getAspectsManagerFrame().updateTree(
								null, null, appAspect);

						break;
					case APP_CATALOG:
						appCatalog.setName(textFieldTitle.getText().trim());
						appCatalog.setDescription(textAreaDescription.getText()
								.trim());

						UI.getInstance().getAspectsManagerFrame().updateTree(
								appCatalog, null, null);

						break;

					default:
						break;
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(((JButton) e.getSource())
							.getParent().getParent(), GUITools
							.getMessagePane(exc.getMessage()), Data
							.getInstance().getLocaleStr("error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				}

				setVisible(false);
			}
		});

		JPanel panelButtons = new JPanel(new BorderLayout());
		panelButtons.setBackground(UI.POPUP_BACKGROUND);
		panelButtons.setBorder(BorderFactory.createLineBorder(panelButtons
				.getBackground(), 3));
		panelButtons.add(buttonAbort, BorderLayout.WEST);
		panelButtons.add(buttonConfirm, BorderLayout.EAST);

		/*
		 * Base panel
		 */
		panelBase.add(panelButtons, BorderLayout.SOUTH);

		add(panelBase, BorderLayout.CENTER);

		pack();

		/*
		 * Set size and location
		 */
		Dimension popupSize = new Dimension(260, 400);

		setMinimumSize(popupSize);
		setSize(popupSize);
		setPreferredSize(popupSize);

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
	 * Gets the aspect.
	 * 
	 * @return the aspect
	 */
	public Aspect getAspect() {
		return aspect;
	}

	/**
	 * Gets the app catalog.
	 * 
	 * @return the appCatalog
	 */
	public AppCatalog getAppCatalog() {
		return appCatalog;
	}

	/**
	 * Gets the app aspect.
	 * 
	 * @return the appAspect
	 */
	public AppAspect getAppAspect() {
		return appAspect;
	}

}
