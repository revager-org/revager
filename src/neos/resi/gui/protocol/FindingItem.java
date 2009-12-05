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
package neos.resi.gui.protocol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import neos.resi.app.Application;
import neos.resi.app.FindingManagement;
import neos.resi.app.ResiFileFilter;
import neos.resi.app.SeverityManagement;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.ResiData;
import neos.resi.app.model.appdata.AppSettingKey;
import neos.resi.app.model.appdata.AppSettingValue;
import neos.resi.app.model.schema.Aspect;
import neos.resi.app.model.schema.Finding;
import neos.resi.app.model.schema.Protocol;
import neos.resi.gui.UI;
import neos.resi.gui.helpers.DefaultTableHeaderCellRenderer;
import neos.resi.gui.helpers.FileChooser;
import neos.resi.gui.models.FindAspTableModel;
import neos.resi.gui.models.FindExRefTableModel;
import neos.resi.gui.models.FindRefTableModel;
import neos.resi.gui.protocol.AddAspToFindPopupWindow.ButtonClicked;
import neos.resi.tools.AppTools;
import neos.resi.tools.GUITools;

/**
 * The Class FindingItem.
 */
@SuppressWarnings("serial")
public class FindingItem extends JPanel implements Observer {

	private FindingManagement findMgmt = Application.getInstance()
			.getFindingMgmt();

	private JComboBox sevBx;
	private JTextArea descTxtArea;

	// private JLabel sevLbl = new JLabel(Data.getInstance().getLocaleStr(
	// "editProtocol.finding.sev"));
	// private JLabel descLbl = new JLabel(Data.getInstance().getLocaleStr(
	// "editProtocol.finding.desc"));
	private JLabel idLbl;

	private GridBagLayout gbl = new GridBagLayout();
	private GridLayout gl = new GridLayout(3, 1);

	private FindRefTableModel frtm;
	private FindExRefTableModel fertm;
	private FindAspTableModel fatm;

	private JTable refTbl;
	private JTable exRefTbl;
	private JTable aspTbl;

	private JScrollPane descScrllPn;
	private JScrollPane refScrllPn;
	private JScrollPane exRefScrllPn;
	private JScrollPane aspScrllPn;

	private JButton addRefBttn;
	private JButton ediRefBttn;
	private JButton remRefBttn;

	private JButton addExRefBttn;
	private JButton remExRefBttn;

	private JButton addAspBttn;
	private JButton remAspBttn;

	private JButton pushUpBttn;
	private JButton pushDownBttn;

	private JButton remFindBttn;

	private JButton pasteExRefBttn;

	private Finding currentFinding;
	private Protocol currentProtocol;

	private ImageIcon addIcon = Data.getInstance().getIcon("add_25x25_0.png");
	private ImageIcon remIcon = Data.getInstance()
			.getIcon("remove_25x25_0.png");
	private ImageIcon editIcon = Data.getInstance().getIcon("edit_25x25_0.png");
	private ImageIcon pushUpIcon = Data.getInstance().getIcon(
			"upArrow_25x25_0.png");
	private ImageIcon pushDownIcon = Data.getInstance().getIcon(
			"downArrow_25x25_0.png");
	private ImageIcon remFindIcon = Data.getInstance().getIcon(
			"clear_22x22_0.png");
	private ImageIcon showExRefIcon = Data.getInstance().getIcon(
			"show_25x25_0.png");

	private ImageIcon rolloverAddIcon = Data.getInstance().getIcon(
			"add_25x25.png");
	private ImageIcon rolloverRemIcon = Data.getInstance().getIcon(
			"remove_25x25.png");
	private ImageIcon rolloverEditIcon = Data.getInstance().getIcon(
			"edit_25x25.png");
	private ImageIcon rolloverPushUpIcon = Data.getInstance().getIcon(
			"upArrow_25x25.png");
	private ImageIcon rolloverPushDownIcon = Data.getInstance().getIcon(
			"downArrow_25x25.png");
	private ImageIcon rolloverRemFindIcon = Data.getInstance().getIcon(
			"clear_22x22.png");
	private ImageIcon rolloverShowExRefIcon = Data.getInstance().getIcon(
			"show_25x25.png");

	private FindingManagement findingMgmt = Application.getInstance()
			.getFindingMgmt();
	private SeverityManagement sevMgmt = Application.getInstance()
			.getSeverityMgmt();
	private ResiData resiData = Data.getInstance().getResiData();

	private FocusListener focusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			UI.getInstance().getProtocolFrame().updateFocus(currentFinding,
					e.getSource());

			updateTableButtons();
		}

		@Override
		public void focusLost(FocusEvent e) {
			updateTableButtons();
		}
	};

	/**
	 * Instantiates a new finding item.
	 * 
	 * @param find
	 *            the find
	 * @param prot
	 *            the prot
	 */
	public FindingItem(Finding find, Protocol prot) {
		super();

		Data.getInstance().getResiData().addObserver(this);

		currentFinding = find;

		currentProtocol = prot;

		setLayout(gbl);
		String nr = Data.getInstance().getLocaleStr(
				"editProtocol.finding.number")
				+ " ";
		idLbl = new JLabel(nr.concat(currentFinding.getId().toString()));
		idLbl.setFont(UI.PROTOCOL_TITLE_FONT);
		idLbl.setForeground(Color.DARK_GRAY);

		/*
		 * creating buttons and their panels
		 */
		JPanel refBttnPnl = new JPanel(gl);
		JPanel exRefBttnPnl = new JPanel(gl);
		JPanel aspBttnPnl = new JPanel(gl);

		refBttnPnl.setOpaque(false);
		exRefBttnPnl.setOpaque(false);
		aspBttnPnl.setOpaque(false);

		/*
		 * creating refButtons and adding them to their panel
		 */

		addRefBttn = GUITools.newImageButton(addIcon, rolloverAddIcon);
		addRefBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getProtocolFrame().updateFocus(currentFinding,
						refTbl);

				if (refTbl.getCellEditor() != null) {
					refTbl.getCellEditor().stopCellEditing();
				}

				String ref = Data.getInstance().getLocaleStr(
						"editProtocol.finding.newRef");
				findingMgmt.addReference(ref, currentFinding);
				frtm.fireTableDataChanged();

				int row = refTbl.getRowCount() - 1;

				refTbl.scrollRectToVisible(refTbl.getCellRect(row, 0, false));
				refTbl.editCellAt(row, 0);
			}
		});

		remRefBttn = GUITools.newImageButton(remIcon, rolloverRemIcon);
		remRefBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = refTbl.getSelectedRow();

				if (refTbl.getEditingRow() != -1) {
					selRow = refTbl.getEditingRow();

					refTbl.getCellEditor().stopCellEditing();
				}

				if (selRow != -1) {
					String ref = findingMgmt.getReferences(currentFinding).get(
							selRow);
					findingMgmt.removeReference(ref, currentFinding);
					frtm.fireTableDataChanged();
				}

				updateTableButtons();
			}
		});

		ediRefBttn = GUITools.newImageButton(editIcon, rolloverEditIcon);
		ediRefBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = refTbl.getSelectedRow();
				if (selRow != -1) {
					refTbl.editCellAt(selRow, 0);
					frtm.fireTableDataChanged();
				}
			}
		});

		addRefBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.addRef"));
		remRefBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.remRef"));
		ediRefBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.ediRef"));

		refBttnPnl.add(addRefBttn);
		refBttnPnl.add(remRefBttn);
		refBttnPnl.add(ediRefBttn);

		/*
		 * creating exRefButton and adding them to their panels
		 */

		addExRefBttn = GUITools.newImageButton(addIcon, rolloverAddIcon);
		addExRefBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final ProtocolFrame protFrame = UI.getInstance()
						.getProtocolFrame();

				UI.getInstance().getProtocolFrame().updateFocus(currentFinding,
						exRefTbl);

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

							protFrame.switchToProgressMode();

							File file = fileChooser.getFile();
							findingMgmt.addExtReference(file, currentFinding);
							fertm.fireTableDataChanged();

							Thread.sleep(200);

							GUITools.scrollToBottom(exRefScrllPn);

							updateTableButtons();
						}

						protFrame.switchToEditMode();

						return null;
					}
				};

				addExtRefWorker.execute();
			}
		});

		remExRefBttn = GUITools.newImageButton(remIcon, rolloverRemIcon);
		remExRefBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = exRefTbl.getSelectedRow();
				if (selRow != -1) {
					File file = findingMgmt.getExtReferences(currentFinding)
							.get(selRow);
					findingMgmt.removeExtReference(file, currentFinding);
					fertm.fireTableDataChanged();
					updateTableButtons();
				}
			}
		});

		/*
		 * 
		 * creating show external reference button
		 */
		pasteExRefBttn = GUITools.newImageButton(showExRefIcon,
				rolloverShowExRefIcon);
		pasteExRefBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getProtocolFrame().updateFocus(currentFinding,
						exRefTbl);

				SwingWorker<Void, Void> addImgFromCbWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						UI
								.getInstance()
								.getProtocolFrame()
								.switchToProgressMode(
										Data
												.getInstance()
												.getLocaleStr(
														"editProtocol.message.imgLoading"));

						Image img = AppTools.getImageFromClipboard();

						if (img == null) {
							JOptionPane
									.showMessageDialog(
											UI.getInstance().getProtocolFrame(),
											GUITools
													.getMessagePane(Data
															.getInstance()
															.getLocaleStr(
																	"editProtocol.message.noImgInCb")),
											Data.getInstance().getLocaleStr(
													"info"),
											JOptionPane.INFORMATION_MESSAGE);
						} else {
							Image scalImg = img.getScaledInstance(-1, 250,
									Image.SCALE_SMOOTH);

							JLabel labelImg = new JLabel(new ImageIcon(scalImg));
							labelImg.setBorder(new CompoundBorder(
									new EmptyBorder(0, 0, 15, 0),
									new MatteBorder(1, 1, 1, 1,
											UI.SEPARATOR_COLOR)));

							JTextField inputField = new JTextField();
							inputField.setText(Data.getInstance().getLocaleStr(
									"editProtocol.finding.stdFileName"));

							JPanel messagePane = new JPanel(new BorderLayout());
							messagePane.add(labelImg, BorderLayout.NORTH);
							messagePane
									.add(
											new JLabel(
													Data
															.getInstance()
															.getLocaleStr(
																	"editProtocol.message.askForFileName")),
											BorderLayout.CENTER);
							messagePane.add(inputField, BorderLayout.SOUTH);

							// String fileName = (String) JOptionPane
							// .showInputDialog(
							// UI.getInstance().getProtocolFrame(),
							// messagePane,
							// Data.getInstance().getLocaleStr(
							// "confirm"),
							// JOptionPane.DEFAULT_OPTION,
							// null,
							// null,
							// Data
							// .getInstance()
							// .getLocaleStr(
							// "editProtocol.finding.stdFileName"));

							Object[] options = { "Speichern", "Bearbeiten",
									"Abbrechen" };

							int action = JOptionPane.showOptionDialog(UI
									.getInstance().getProtocolFrame(),
									messagePane, Data.getInstance()
											.getLocaleStr("confirm"),
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, options,
									options[0]);

							String fileName = null;

							if (action != 2) {
								fileName = inputField.getText();
							}

							if (fileName != null && !fileName.trim().equals("")) {
								findingMgmt.addExtReference(img, fileName,
										currentFinding);

								fertm.fireTableDataChanged();

								updateTableButtons();
							}

							if (action == 1) {
								int numberOfExtRefs = findingMgmt
										.getExtReferences(currentFinding)
										.size();

								File extRef = findingMgmt.getExtReferences(
										currentFinding)
										.get(numberOfExtRefs - 1);

								UI.getInstance().getProtocolFrame()
										.getImageEditor(extRef)
										.setVisible(true);
							}
						}

						UI.getInstance().getProtocolFrame().switchToEditMode();

						return null;
					}
				};

				addImgFromCbWorker.execute();
			}
		});

		addExRefBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.addExRef"));
		remExRefBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.remExRef"));
		pasteExRefBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.pasteExRef"));

		exRefBttnPnl.add(addExRefBttn);
		exRefBttnPnl.add(remExRefBttn);
		exRefBttnPnl.add(pasteExRefBttn);

		/*
		 * creating aspButtons and adding them to their panel
		 */
		addAspBttn = GUITools.newImageButton(addIcon, rolloverAddIcon);
		addAspBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getProtocolFrame().updateFocus(currentFinding,
						aspTbl);

				AddAspToFindPopupWindow popup = new AddAspToFindPopupWindow(UI
						.getInstance().getProtocolFrame());

				popup.setVisible(true);

				if (popup.getButtonClicked() == ButtonClicked.OK) {
					List<Aspect> aspList = popup.getSelAspList();

					for (Aspect asp : aspList) {
						findingMgmt.addAspect(asp, currentFinding);
					}

					fatm.fireTableDataChanged();

					GUITools.scrollToBottom(aspScrllPn);
				}

				updateTableButtons();
			}
		});

		remAspBttn = GUITools.newImageButton(remIcon, rolloverRemIcon);
		remAspBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = aspTbl.getSelectedRow();
				if (selRow != -1) {
					String asp = findingMgmt.getAspects(currentFinding).get(
							selRow);
					findingMgmt.removeAspect(asp, currentFinding);
					fatm.fireTableDataChanged();
				}
				updateTableButtons();
			}
		});

		addAspBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.addAsp"));
		remAspBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.remAsp"));

		aspBttnPnl.add(addAspBttn);
		aspBttnPnl.add(remAspBttn);

		/*
		 * 
		 * creating pushbuttons;
		 */
		pushUpBttn = GUITools.newImageButton(pushUpIcon, rolloverPushUpIcon);
		pushUpBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.pushUp"));
		pushUpBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getProtocolFrame().updateFocus(currentFinding,
						null);

				findingMgmt.pushUpFinding(currentFinding, currentProtocol);
				int index = UI.getInstance().getProtocolFrame()
						.getFirstVisibleFinding();
				UI.getInstance().getProtocolFrame().setFirstVisibleFinding(
						index - 1);
				UI.getInstance().getProtocolFrame().updatePanelFindings();
				UI.getInstance().getProtocolFrame().getFtm()
						.fireTableDataChanged();
				UI.getInstance().getProtocolFrame().setSelectionRow();
				UI.getInstance().getProtocolFrame().updateTable();
			}
		});

		pushDownBttn = GUITools.newImageButton(pushDownIcon,
				rolloverPushDownIcon);
		pushDownBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.pushDown"));
		pushDownBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getProtocolFrame().updateFocus(currentFinding,
						null);

				findingMgmt.pushDownFinding(currentFinding, currentProtocol);
				int index = UI.getInstance().getProtocolFrame()
						.getFirstVisibleFinding();
				UI.getInstance().getProtocolFrame().setFirstVisibleFinding(
						index + 1);
				UI.getInstance().getProtocolFrame().updatePanelFindings();

				UI.getInstance().getProtocolFrame().getFtm()
						.fireTableDataChanged();
				UI.getInstance().getProtocolFrame().setSelectionRow();
				UI.getInstance().getProtocolFrame().updateTable();
			}
		});

		/*
		 * 
		 * creating remove finding button
		 */
		remFindBttn = GUITools.newImageButton(remFindIcon, rolloverRemFindIcon);
		remFindBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"findingsItem.delFind"));
		remFindBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				findingMgmt.removeFinding(currentFinding, currentProtocol);
				UI.getInstance().getProtocolFrame().updatePanelFindings();

				UI.getInstance().getProtocolFrame().getFtm()
						.fireTableDataChanged();
				UI.getInstance().getProtocolFrame().setSelectionRow();
				UI.getInstance().getProtocolFrame().updateTable();
			}
		});

		updateFindBttns();

		/*
		 * 
		 * creating and setting desc,ref,exRef,asp,frtm,fertm,fatm
		 */

		descTxtArea = new JTextArea(currentFinding.getDescription());

		descScrllPn = GUITools.setIntoScrllPn(descTxtArea);
		GUITools.scrollToTop(descScrllPn);

		descTxtArea.addFocusListener(focusListener);
		descTxtArea.setFont(UI.PROTOCOL_FONT);
		descTxtArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				Object evSrc = e.getSource();

				if (evSrc instanceof JTextArea
						&& e.getKeyCode() == KeyEvent.VK_TAB) {
					JTextArea txtArea = (JTextArea) evSrc;

					if (e.getModifiers() > 0) {
						txtArea.transferFocusBackward();
					} else {
						txtArea.transferFocus();
						e.consume();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				boolean mark = false;

				try {
					if (Data.getInstance().getAppData().getSettingValue(
							AppSettingKey.APP_HIGHLIGHT_FIELDS) == AppSettingValue.TRUE) {
						mark = true;
					}
				} catch (DataException exc) {
					mark = true;
				}

				if (descTxtArea.getText().trim().equals("") && mark) {
					descScrllPn.setBorder(UI.MARKED_BORDER);
				} else {
					descScrllPn.setBorder(UI.STANDARD_BORDER);
				}

				currentFinding.setDescription(descTxtArea.getText());
				UI.getInstance().getProtocolFrame().getFtm()
						.fireTableDataChanged();
				resiData.fireDataChanged();
				UI.getInstance().getProtocolFrame().setSelectionRow();
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		for (KeyListener kl : descTxtArea.getKeyListeners()) {
			kl.keyReleased(null);
		}

		/*
		 * disabeling buttons
		 */
		remAspBttn.setEnabled(false);
		remExRefBttn.setEnabled(false);
		remRefBttn.setEnabled(false);

		ediRefBttn.setEnabled(false);

		/*
		 * creating table models
		 */
		fatm = new FindAspTableModel(currentFinding);
		frtm = new FindRefTableModel(currentFinding);
		fertm = new FindExRefTableModel(currentFinding);

		DefaultTableCellRenderer cellRend = new DefaultTableCellRenderer() {
			@Override
			public Font getFont() {
				return UI.PROTOCOL_FONT;
			}

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				setToolTipText(GUITools.getTextAsHtml("<font size=\"5\">"
						+ (String) value + "</font>"));

				return super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
			}
		};

		DefaultTableHeaderCellRenderer headRend = new DefaultTableHeaderCellRenderer() {
			@Override
			public Font getFont() {
				return UI.PROTOCOL_FONT;
			}
		};

		aspTbl = GUITools.newStandardTable(fatm, true);
		refTbl = GUITools.newStandardTable(frtm, true);
		exRefTbl = GUITools.newStandardTable(fertm, true);

		aspTbl.getColumnModel().getColumn(0).setHeaderRenderer(headRend);
		refTbl.getColumnModel().getColumn(0).setHeaderRenderer(headRend);
		exRefTbl.getColumnModel().getColumn(0).setHeaderRenderer(headRend);

		aspTbl.getColumnModel().getColumn(0).setCellRenderer(cellRend);
		refTbl.getColumnModel().getColumn(0).setCellRenderer(cellRend);
		exRefTbl.getColumnModel().getColumn(0).setCellRenderer(cellRend);

		aspTbl.setRowHeight(29);
		refTbl.setRowHeight(29);
		exRefTbl.setRowHeight(29);

		aspTbl.addFocusListener(focusListener);
		refTbl.addFocusListener(focusListener);
		exRefTbl.addFocusListener(focusListener);

		aspTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateTableButtons();
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

		refTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateTableButtons();
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

		exRefTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateTableButtons();

				if (e.getClickCount() == 2) {
					int selRow = exRefTbl.getSelectedRow();

					if (selRow != -1) {
						File ref = findMgmt.getExtReferences(currentFinding)
								.get(exRefTbl.getSelectedRow());

						if (AppTools.isReadableWritableImageFile(ref)) {
							UI.getInstance().getProtocolFrame().getImageEditor(
									ref).setVisible(true);
						} else {
							try {
								Desktop.getDesktop().open(ref);
							} catch (Exception exc) {
								JOptionPane.showMessageDialog(UI.getInstance()
										.getProtocolFrame(), GUITools
										.getMessagePane(exc.getMessage()), Data
										.getInstance().getLocaleStr("error"),
										JOptionPane.ERROR_MESSAGE);
							}
						}
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

		aspScrllPn = new JScrollPane(aspTbl);
		refScrllPn = new JScrollPane(refTbl);
		exRefScrllPn = new JScrollPane(exRefTbl);

		aspScrllPn.getViewport().setBackground(Color.WHITE);
		refScrllPn.getViewport().setBackground(Color.WHITE);
		exRefScrllPn.getViewport().setBackground(Color.WHITE);

		sevBx = new JComboBox();
		sevBx.setFont(UI.PROTOCOL_FONT_BOLD);

		for (String sev : sevMgmt.getSeverities()) {
			sevBx.addItem(sev);
		}

		sevBx.addFocusListener(focusListener);

		sevBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					currentFinding.setSeverity(sevBx.getSelectedItem()
							.toString());
					resiData.fireDataChanged();
				}
			}
		});
		sevBx.setSelectedItem(currentFinding.getSeverity());

		/*
		 * adding components to the content panel
		 */
		GUITools.addComponent(this, gbl, idLbl, 0, 0, 1, 1, 0.0, 0.0, 5, 10, 5,
				0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(this, gbl, remFindBttn, 1, 0, 1, 1, 0.0, 0.0, 5,
				15, 5, 3, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(this, gbl, pushUpBttn, 2, 0, 1, 1, 0.0, 0.0, 5,
				5, 5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(this, gbl, pushDownBttn, 3, 0, 1, 1, 0.0, 0.0, 5,
				5, 5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);

		// GUITools.addComponent(this, gbl, descLbl, 0, 1, 5, 1, 0, 0, 15, 10,
		// 0,
		// 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools
				.addComponent(this, gbl, descScrllPn, 0, 2, 5, 3, 1.0, 1.0, 5,
						10, 0, 0, GridBagConstraints.BOTH,
						GridBagConstraints.NORTHWEST);

		GUITools.addComponent(this, gbl, refScrllPn, 0, 5, 5, 1, 1.0, 1.0, 5,
				10, 10, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, refBttnPnl, 5, 5, 1, 1, 0, 0, 5, 10,
				20, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);

		// GUITools.addComponent(this, gbl, sevLbl, 6, 1, 1, 1, 0, 0, 15, 30, 0,
		// 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, sevBx, 6, 2, 1, 1, 1.0, 0, 5, 30, 0,
				0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);

		GUITools
				.addComponent(this, gbl, aspScrllPn, 6, 3, 1, 2, 1.0, 1.0, 5,
						30, 0, 0, GridBagConstraints.BOTH,
						GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, aspBttnPnl, 7, 3, 1, 2, 0, 0, 5, 10,
				0, 10, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);

		GUITools.addComponent(this, gbl, exRefScrllPn, 6, 5, 1, 1, 1.0, 1.0, 5,
				30, 10, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, exRefBttnPnl, 7, 5, 1, 1, 0, 0, 5, 10,
				20, 10, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);

		/*
		 * change for saving
		 */
		if (findingMgmt.isFindingEmpty(currentFinding)) {
			currentFinding.setDescription("");
			currentFinding.setSeverity(sevMgmt.getSeverities().get(0));
			resiData.fireDataChanged();
		}
	}

	/**
	 * Updates the buttons of the tables.
	 */
	private void updateTableButtons() {
		if (aspTbl.getSelectedRow() == -1) {
			remAspBttn.setEnabled(false);
		} else {
			remAspBttn.setEnabled(true);
		}

		if (refTbl.getSelectedRow() == -1) {
			remRefBttn.setEnabled(false);
			ediRefBttn.setEnabled(false);
		} else {
			remRefBttn.setEnabled(true);
			ediRefBttn.setEnabled(true);
		}

		if (exRefTbl.getSelectedRow() == -1) {
			remExRefBttn.setEnabled(false);
		} else {
			remExRefBttn.setEnabled(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		updateFindBttns();
	}

	/**
	 * Update find bttns.
	 */
	public void updateFindBttns() {
		if (findingMgmt.getNumberOfFindings(currentProtocol) == 1) {
			remFindBttn.setEnabled(false);
			pushUpBttn.setEnabled(false);
			pushDownBttn.setEnabled(false);

		} else {
			remFindBttn.setEnabled(true);

			if (findingMgmt.isTopFinding(currentFinding, currentProtocol)) {
				pushUpBttn.setEnabled(false);
			} else {
				pushUpBttn.setEnabled(true);
			}

			if (findingMgmt.isBottomFinding(currentFinding, currentProtocol)) {
				pushDownBttn.setEnabled(false);
			} else {
				pushDownBttn.setEnabled(true);
			}
		}
	}

	/**
	 * Updates the focus.
	 * 
	 * @param evSource
	 *            the event source
	 */
	public void updateFocus(Object evSource) {
		if (evSource != descTxtArea) {
			descTxtArea.select(0, 0);
		}

		if (evSource != refTbl) {
			if (refTbl.getCellEditor() != null) {
				refTbl.getCellEditor().stopCellEditing();
			}

			if (refTbl.getRowCount() > 0) {
				refTbl.removeRowSelectionInterval(0, refTbl.getRowCount() - 1);
			}
		}

		if (evSource != exRefTbl) {
			if (exRefTbl.getRowCount() > 0) {
				exRefTbl.removeRowSelectionInterval(0,
						exRefTbl.getRowCount() - 1);
			}
		}

		if (evSource != aspTbl) {
			if (aspTbl.getRowCount() > 0) {
				aspTbl.removeRowSelectionInterval(0, aspTbl.getRowCount() - 1);
			}
		}
	}

	/**
	 * Returns the current finding
	 * 
	 * @return the current finding
	 */
	public Finding getCurrentFinding() {
		return currentFinding;
	}
}
