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
package neos.resi.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.ResiFileFilter;
import neos.resi.app.ReviewManagement;
import neos.resi.app.model.Data;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.UI;
import neos.resi.gui.helpers.FileChooser;
import neos.resi.gui.models.ExRefTableModel;
import neos.resi.gui.models.ReferenceTableModel;
import neos.resi.tools.GUITools;

/**
 * The Class EditProductDialog.
 */
@SuppressWarnings("serial")
public class EditProductDialog extends AbstractDialog {

	private GridBagLayout gbl = new GridBagLayout();
	private JPanel basePanel = new JPanel();

	private JTextField nameTxtFld;
	private JTextField versionTxtFld;

	private JTable referenceTbl;
	private JTable dataTbl;

	private ExRefTableModel ertm = new ExRefTableModel();
	private JScrollPane refScrllPn;
	private JScrollPane dataScrllPn;

	private JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
	private JPanel buttonPanelD = new JPanel(new GridLayout(4, 1));

	private JLabel referenceLbl = new JLabel(Data.getInstance().getLocaleStr(
			"editProduct.reference"));
	private JLabel dataLbl = new JLabel(Data.getInstance().getLocaleStr(
			"editProduct.data"));
	private JLabel nameLbl = new JLabel(Data.getInstance().getLocaleStr(
			"editProduct.name"));
	private JLabel versionLbl = new JLabel(Data.getInstance().getLocaleStr(
			"editProduct.version"));

	private ReferenceTableModel rtm = new ReferenceTableModel();
	private ReviewManagement reviewMgmt = Application.getInstance()
			.getReviewMgmt();

	private JButton addReference;
	private JButton removeReference;
	private JButton editReference;

	private JButton addData;
	private JButton removeData;

	private JButton close;

	/*
	 * For usability
	 */
	private long updateTime = System.currentTimeMillis();
	private KeyListener updateListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			updateTime = System.currentTimeMillis();
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	};
	private SwingWorker<Void, Void> updateWorker = new SwingWorker<Void, Void>() {
		@Override
		protected Void doInBackground() throws Exception {
			long change = Long.parseLong(Data.getInstance().getResource(
					"keyTypeChangeInMillis"));

			while (true) {
				try {
					Thread.sleep(change);

					long diff = System.currentTimeMillis() - updateTime;

					if (diff >= change && diff < change * 3) {
						updateMessages();
					}
				} catch (Exception e) {
					/*
					 * do nothing and wait for the next run of this worker
					 */
				}
			}
		}
	};

	private FocusListener focusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			updateFocus(e.getSource());
			updateButtons();
			updateMessages();
		}

		@Override
		public void focusLost(FocusEvent e) {
			updateButtons();
			updateMessages();

			/*
			 * Set Resi data
			 */
			reviewMgmt.setProductName(nameTxtFld.getText());
			reviewMgmt.setProductVersion(versionTxtFld.getText());
		}
	};

	/**
	 * Creates the dialog.
	 */
	private void createDialog() {
		nameTxtFld = new JTextField();
		nameTxtFld.addKeyListener(updateListener);
		nameTxtFld.addFocusListener(focusListener);

		versionTxtFld = new JTextField();
		versionTxtFld.addKeyListener(updateListener);
		versionTxtFld.addFocusListener(focusListener);

		referenceTbl = GUITools.newStandardTable(rtm, false);
		referenceTbl.addFocusListener(focusListener);
		referenceTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateButtons();
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
			}
		});

		addReference = GUITools.newImageButton();
		addReference.setIcon(Data.getInstance().getIcon("add_25x25_0.png"));
		addReference.setRolloverIcon(Data.getInstance()
				.getIcon("add_25x25.png"));
		addReference.setToolTipText(Data.getInstance().getLocaleStr(
				"editProduct.addReference"));
		addReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (referenceTbl.getCellEditor() != null) {
					referenceTbl.getCellEditor().stopCellEditing();
				}

				String placeHolder = Data.getInstance().getLocaleStr(
						"editProduct.placeHolder");

				reviewMgmt.removeProductReference(placeHolder);
				reviewMgmt.addProductReference(placeHolder);

				rtm.fireTableDataChanged();

				updateMessages();

				int row = referenceTbl.getRowCount() - 1;

				referenceTbl.scrollRectToVisible(referenceTbl.getCellRect(row,
						0, false));
				referenceTbl.editCellAt(row, 0);
			}
		});
		buttonPanel.add(addReference);

		removeReference = GUITools.newImageButton();
		removeReference.setIcon(Data.getInstance()
				.getIcon("remove_25x25_0.png"));
		removeReference.setRolloverIcon(Data.getInstance().getIcon(
				"remove_25x25.png"));
		removeReference.setToolTipText(Data.getInstance().getLocaleStr(
				"editProduct.removeReference"));
		removeReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = referenceTbl.getSelectedRow();

				String localRef = reviewMgmt.getProductReferences().get(
						selectedRow);

				if (reviewMgmt.isProductReferenceRemovable(localRef)) {
					reviewMgmt.removeProductReference(localRef);
					rtm.fireTableDataChanged();
				} else {
					setMessage(Data.getInstance().getLocaleStr(
							"editProduct.message.remRef"));
				}
			}
		});
		buttonPanel.add(removeReference);

		editReference = GUITools.newImageButton();
		editReference.setIcon(Data.getInstance().getIcon("edit_25x25_0.png"));
		editReference.setRolloverIcon(Data.getInstance().getIcon(
				"edit_25x25.png"));
		editReference.setToolTipText(Data.getInstance().getLocaleStr(
				"editProduct.editReference"));
		editReference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = referenceTbl.getSelectedRow();

				if (selectedRow != -1) {
					referenceTbl.editCellAt(selectedRow, 0);
				}

				updateButtons();
			}
		});
		buttonPanel.add(editReference);

		dataTbl = GUITools.newStandardTable(ertm, false);
		dataTbl.addFocusListener(focusListener);
		dataTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateButtons();

				if (e.getClickCount() == 2) {
					File ref = reviewMgmt.getExtProdRefByName((String) ertm
							.getValueAt(dataTbl.getSelectedRow(), 0));

					try {
						Desktop.getDesktop().open(ref);
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(UI.getInstance()
								.getEditProductDialog(), GUITools
								.getMessagePane(exc.getMessage()), Data
								.getInstance().getLocaleStr("error"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
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
			}
		});

		addData = GUITools.newImageButton();
		addData.setIcon(Data.getInstance().getIcon("add_25x25_0.png"));
		addData.setRolloverIcon(Data.getInstance().getIcon("add_25x25.png"));
		addData.setToolTipText(Data.getInstance().getLocaleStr(
				"editProduct.addFile"));
		addData.addFocusListener(focusListener);
		addData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> addExtRefWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						FileChooser fileChooser = UI.getInstance()
								.getFileChooser();

						fileChooser.setFile(null);

						if (fileChooser.showDialog(UI.getInstance()
								.getEditProductDialog(),
								FileChooser.MODE_OPEN_FILE,
								ResiFileFilter.TYPE_ALL) == FileChooser.SELECTED_APPROVE) {

							switchToProgressMode(Data.getInstance()
									.getLocaleStr(
											"editProduct.message.addExtRef"));

							File file = fileChooser.getFile();
							reviewMgmt.addExtProdReference(file);
							updateMessages();
							ertm.fireTableDataChanged();

							Thread.sleep(200);

							GUITools.scrollToBottom(dataScrllPn);
						}

						switchToEditMode();

						return null;
					}
				};

				addExtRefWorker.execute();
			}
		});
		buttonPanelD.add(addData);

		removeData = GUITools.newImageButton();
		removeData.setIcon(Data.getInstance().getIcon("remove_25x25_0.png"));
		removeData.setRolloverIcon(Data.getInstance().getIcon(
				"remove_25x25.png"));
		removeData.setToolTipText(Data.getInstance().getLocaleStr(
				"editProduct.removeFile"));
		removeData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int selRow = dataTbl.getSelectedRow();

				final File ref = reviewMgmt.getExtProdReferences().get(selRow);

				SwingWorker<Void, Void> removeExtRefWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						switchToProgressMode(Data.getInstance().getLocaleStr(
								"editProduct.message.removeExtRef"));

						reviewMgmt.removeExtProdReference(ref);
						ertm.fireTableDataChanged();

						switchToEditMode();

						return null;
					}
				};

				if (selRow != -1) {
					if (reviewMgmt.isExtProdReferenceRemovable(ref)) {
						removeExtRefWorker.execute();
					} else {
						setMessage(Data.getInstance().getLocaleStr(
								"editProduct.message.remRef"));
					}
				}
			}
		});
		buttonPanelD.add(removeData);

		refScrllPn = GUITools.setIntoScrollPane(referenceTbl);
		dataScrllPn = GUITools.setIntoScrollPane(dataTbl);

		GUITools.addComponent(basePanel, gbl, nameLbl, 0, 0, 1, 1, 0.0, 0.0, 0,
				5, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, nameTxtFld, 1, 0, 5, 1, 1.0, 0.0,
				0, 5, 0, 5, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, versionLbl, 0, 1, 1, 1, 0.0, 0.0,
				10, 5, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, versionTxtFld, 1, 1, 5, 1, 1.0,
				0.0, 10, 5, 0, 5, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, referenceLbl, 0, 2, 2, 1, 0.0,
				0.0, 20, 5, 0, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, refScrllPn, 0, 3, 2, 1, 1.0, 1.0,
				10, 5, 0, 5, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, buttonPanel, 3, 3, 1, 1, 0.0,
				0.0, 10, 5, 0, 20, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTH);
		GUITools.addComponent(basePanel, gbl, dataLbl, 4, 2, 2, 1, 0.0, 0.0,
				20, 5, 0, 5, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, dataScrllPn, 4, 3, 2, 1, 1.0,
				1.0, 10, 5, 0, 5, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, buttonPanelD, 6, 3, 1, 1, 0.0,
				0.0, 10, 5, 0, 5, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTH);

		/*
		 * Run the update worker
		 */
		updateWorker.execute();
	}

	/*
	 * 
	 * method which clears warning message and border markings
	 */
	/**
	 * Update messages.
	 */
	public void updateMessages() {
		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		versionTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		refScrllPn.setBorder(UI.STANDARD_BORDER);
		dataScrllPn.setBorder(UI.STANDARD_BORDER);

		setMessage(null);

		int size = reviewMgmt.getNumberOfProdRefs();
		String name = nameTxtFld.getText();
		String version = versionTxtFld.getText();

		if (referenceTbl.isEditing()) {
			setMessage(Data.getInstance().getLocaleStr(
					"editProduct.message.confirmRef"));

			return;
		}

		if (name.trim().equals("")) {
			setMessage(Data.getInstance().getLocaleStr(
					"editProduct.message.name"));
			nameTxtFld.setBorder(UI.MARKED_BORDER_INLINE);
			return;
		}

		if (version.trim().equals("")) {
			setMessage(Data.getInstance().getLocaleStr(
					"editProduct.message.ver"));
			versionTxtFld.setBorder(UI.MARKED_BORDER_INLINE);
			return;
		}

		if (size < 1) {
			setMessage(Data.getInstance().getLocaleStr(
					"editProduct.message.ref"));
			refScrllPn.setBorder(UI.MARKED_BORDER);
			dataScrllPn.setBorder(UI.MARKED_BORDER);
			return;
		}
	}

	/*
	 * 
	 * constructor
	 */
	/**
	 * Instantiates a new edits the product dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public EditProductDialog(Frame parent) {
		super(parent);

		setTitle(Data.getInstance().getLocaleStr("editProduct.title"));
		setDescription(Data.getInstance().getLocaleStr(
				"editProduct.description"));
		setIcon(Data.getInstance().getIcon("edit_64x64.png"));

		basePanel.setLayout(gbl);

		getContentPane().setLayout(new BorderLayout());

		add(basePanel, BorderLayout.CENTER);

		createDialog();

		close = new JButton(Data.getInstance().getLocaleStr("close"), Data
				.getInstance().getIcon("buttonClose_16x16.png"));
		close.addFocusListener(focusListener);
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		addButton(close);

		setLocationToCenter();

		setMinimumSize(new Dimension(500, 450));
		setPreferredSize(new Dimension(600, 450));

		pack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see neos.resi.gui.AbstractDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (referenceTbl.getEditingRow() != -1) {
			return;
		}

		if (vis) {
			nameTxtFld.setText(reviewMgmt.getProductName());
			versionTxtFld.setText(reviewMgmt.getProductVersion());
		}

		updateMessages();

		updateFocus(null);

		updateButtons();

		super.setVisible(vis);
	}

	/**
	 * Update focus.
	 * 
	 * @param evSource
	 *            the ev source
	 */
	private void updateFocus(Object evSource) {
		if (referenceTbl.getCellEditor() != null) {
			referenceTbl.getCellEditor().stopCellEditing();
		}

		if (evSource != referenceTbl) {
			if (referenceTbl.getRowCount() > 0) {
				referenceTbl.removeRowSelectionInterval(0, referenceTbl
						.getRowCount() - 1);
			}
		}

		if (evSource != dataTbl) {
			if (dataTbl.getRowCount() > 0) {
				dataTbl
						.removeRowSelectionInterval(0,
								dataTbl.getRowCount() - 1);
			}
		}
	}

	/**
	 * Update buttons.
	 */
	public void updateButtons() {
		if (referenceTbl.getSelectedRow() == -1 || referenceTbl.isEditing()) {
			removeReference.setEnabled(false);
			editReference.setEnabled(false);
		} else {
			removeReference.setEnabled(true);
			editReference.setEnabled(true);
		}

		if (dataTbl.getSelectedRow() == -1) {
			removeData.setEnabled(false);
		} else {
			removeData.setEnabled(true);
		}
	}

}
