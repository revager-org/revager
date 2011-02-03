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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.revager.app.Application;
import org.revager.app.FindingManagement;
import org.revager.app.ProtocolManagement;
import org.revager.app.ReviewManagement;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.appdata.AppSettingValue;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.AbstractFrame;
import org.revager.gui.UI;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.attendee.AddAttToProtAction;
import org.revager.gui.actions.attendee.AddResiAttToProtAction;
import org.revager.gui.actions.attendee.EditAttFromProtAction;
import org.revager.gui.actions.attendee.RemAttFromProtAction;
import org.revager.gui.findings_list.graphical_annotations.ImageEditorDialog;
import org.revager.gui.helpers.DatePicker;
import org.revager.gui.helpers.HintItem;
import org.revager.gui.helpers.ObservingTextField;
import org.revager.gui.models.PresentAttendeesTableModel;
import org.revager.gui.models.RotateSpinnerNumberModel;
import org.revager.gui.workers.ImageEditorWriteWorker;
import org.revager.gui.workers.ProtocolClockWorker;
import org.revager.tools.GUITools;

/**
 * The Class ProtocolFrame.
 */
@SuppressWarnings("serial")
public class FindingsListFrame extends AbstractFrame implements Observer {

	private final ImageIcon ICON_TAB_OK = Data.getInstance().getIcon(
			"tabOk_24x24.png");
	private final ImageIcon ICON_TAB_WARN = Data.getInstance().getIcon(
			"tabWarning_24x24.png");

	private Map<String, ImageEditorDialog> imageEditors = new HashMap<String, ImageEditorDialog>();

	private boolean componentMarked = false;

	private boolean fullscreen = false;
	private boolean nativeFullscrSupported = false;

	private boolean bodyCreated = false;

	private GraphicsDevice gd = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice();

	private FindingManagement findMgmt = Application.getInstance()
			.getFindingMgmt();

	private ApplicationData appData = Data.getInstance().getAppData();

	private GridBagLayout gbl = new GridBagLayout();
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel tabPanelOrg = new JPanel(gbl);
	private JPanel bottomOrgPanel = new JPanel(gbl);

	private JButton tbPdfExport;
	private JButton tbCsvExport;
	private JButton tbConfirmProt;
	private JButton tbFullscreen;

	private JPanel attPanel = new JPanel(gbl);
	private JPanel tabPanelCommAndRec = new JPanel(gbl);
	private JTextField locationTxtFld;
	private JScrollPane scrllP;

	/*
	 * Hint items
	 */
	private HintItem hintAtt;
	private HintItem hintImprRec;
	private HintItem hintFind;
	private HintItem hintOk;
	private HintItem hintInfoNewFinding;

	/*
	 * things for finding tab
	 */
	private Protocol currentProt;

	private FindingsTab tabPanelFindings;

	private SimpleDateFormat sdfCurrentTime = new SimpleDateFormat(
			"d. MMMM yyyy | HH:mm");
	private ProtocolClockWorker clockWorker = UI.getInstance()
			.getProtocolClockWorker();
	private JLabel clockLabel = new JLabel();
	private JLabel clockCurrentTime = new JLabel();
	private JButton clockButtonStart;
	private JButton clockButtonReset;

	private PresentAttendeesTableModel patm;

	private JTable presentAttTable;

	private List<Attendee> presentAttList;

	private Meeting currentMeet = null;
	private JSpinner beginMSpinner;
	private JSpinner beginHSpinner;
	private JSpinner endMSpinner;
	private JSpinner endHSpinner;
	private ObservingTextField dateTxtFld;
	private JComboBox recBx;
	private JTextArea impTxtArea;
	private JTextArea meetCommTxtArea;
	private JTextArea protCommTxtArea;
	private JScrollPane impScrllPn;
	private JScrollPane meetCommScrllPn;
	private JScrollPane protCommScrllPn;

	private ProtocolManagement protMgmt = Application.getInstance()
			.getProtocolMgmt();
	private ReviewManagement revMgmt = Application.getInstance()
			.getReviewMgmt();
	/*
	 * attendee buttons
	 */
	private JButton addResiAtt;
	private JButton addAttendee;
	private JButton removeAttendee;
	private JButton editAttendee;

	private DateFormat dateF = SimpleDateFormat
			.getDateInstance(DateFormat.LONG);

	/*
	 * Change listener for the tabbed pane
	 */
	private ChangeListener tabChangeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			updateHints();
		}
	};

	/**
	 * Gets the patm.
	 * 
	 * @return the patm
	 */
	public PresentAttendeesTableModel getPatm() {
		return patm;
	}

	/**
	 * Gets the meeting.
	 * 
	 * @return the meeting
	 */
	public Meeting getMeeting() {
		return currentMeet;
	}

	/**
	 * Gets the current prot.
	 * 
	 * @return the current prot
	 */
	public Protocol getCurrentProt() {
		return currentProt;
	}

	/**
	 * Sets the meeting.
	 * 
	 * @param meet
	 *            the new meeting
	 */
	public void setMeeting(Meeting meet) {
		currentMeet = meet;
		currentProt = meet.getProtocol();

		patm = new PresentAttendeesTableModel(currentProt);
		presentAttTable = GUITools.newStandardTable(patm, false);

		createBody();
	}

	/**
	 * Gets the present att table.
	 * 
	 * @return the present att table
	 */
	public JTable getPresentAttTable() {
		return presentAttTable;
	}

	/**
	 * Gets the present att list.
	 * 
	 * @return the present att list
	 */
	public List<Attendee> getPresentAttList() {
		return presentAttList;
	}

	/**
	 * Checks if is fullscreen.
	 * 
	 * @return true, if is fullscreen
	 */
	public boolean isFullscreen() {
		return fullscreen;
	}

	/**
	 * Gets the date txt fld.
	 * 
	 * @return the date txt fld
	 */
	public ObservingTextField getDateTxtFld() {
		return dateTxtFld;
	}

	// //////////////////////////
	// creating whole component//
	// //////////////////////////

	/**
	 * Creates the body.
	 */
	private void createBody() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				attPanel.removeAll();
				createAttPanel();
				attPanel.validate();

				bottomOrgPanel.removeAll();
				createBottomOrgPanel();
				bottomOrgPanel.validate();

				tabPanelCommAndRec.removeAll();
				createCommAndRatePanel();
				tabPanelCommAndRec.validate();
			}
		});

		tabPanelFindings = new FindingsTab(currentProt);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tabbedPane.removeAll();
				tabbedPane.removeChangeListener(tabChangeListener);

				tabbedPane.setTabPlacement(JTabbedPane.TOP);

				tabbedPane.add(_("Organizational"), tabPanelOrg);
				tabbedPane.add(_("Findings"), tabPanelFindings);
				tabbedPane.add(_("Impression · Comments · Recommendation"),
						tabPanelCommAndRec);

				tabbedPane.addChangeListener(tabChangeListener);

				add(tabbedPane);

				bodyCreated = true;
			}
		});
	}

	/**
	 * Creates the tool bar.
	 */
	private void createToolBar() {

		tbConfirmProt = GUITools.newImageButton(
				Data.getInstance().getIcon("confirmProtocol_50x50_0.png"), Data
						.getInstance().getIcon("confirmProtocol_50x50.png"));
		tbConfirmProt.setToolTipText(_("Confirm and close list of findings"));
		tbConfirmProt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (protCommTxtArea.getText().trim().equals("")) {
					currentProt.setComments("");
				}

				GUITools.executeSwingWorker(new ImageEditorWriteWorker(
						currentProt));

				setVisible(false);
			}
		});

		addTopComponent(tbConfirmProt);

		JButton sepBttn = GUITools.newImageButton();
		sepBttn.setIcon(Data.getInstance().getIcon("sep_50x50.png"));
		sepBttn.setEnabled(false);
		addTopComponent(sepBttn);

		tbPdfExport = GUITools.newImageButton(
				Data.getInstance().getIcon("PDFExport_50x50_0.png"), Data
						.getInstance().getIcon("PDFExport_50x50.png"));
		tbPdfExport.setToolTipText(_("Export List of Findings as PDF File"));
		tbPdfExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				UI.getInstance().getExportPDFProtocolDialog().setVisible(true);

			}
		});

		addTopComponent(tbPdfExport);

		tbCsvExport = GUITools.newImageButton(
				Data.getInstance().getIcon("CSVExport_50x50_0.png"), Data
						.getInstance().getIcon("CSVExport_50x50.png"));
		tbCsvExport.setToolTipText(_("Export List of Findings as CSV File"));
		tbCsvExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getExportCSVDialog().setVisible(true);
			}
		});

		addTopComponent(tbCsvExport);

		/*
		 * Fullscreen
		 */
		tbFullscreen = GUITools.newImageButton();
		if (fullscreen) {
			tbFullscreen.setIcon(Data.getInstance().getIcon(
					"fullscreenClose_50x50_0.png"));
			tbFullscreen.setRolloverIcon(Data.getInstance().getIcon(
					"fullscreenClose_50x50.png"));
			tbFullscreen.setToolTipText(_("Exit Fullscreen"));
		} else {
			tbFullscreen.setIcon(Data.getInstance().getIcon(
					"fullscreen_50x50_0.png"));
			tbFullscreen.setRolloverIcon(Data.getInstance().getIcon(
					"fullscreen_50x50.png"));
			tbFullscreen.setToolTipText(_("Change to Fullscreen mode"));
		}
		tbFullscreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getProtocolFrame(!isFullscreen())
						.setVisible(true);
			}
		});

		addTopComponent(tbFullscreen);

		/*
		 * current time
		 */
		clockCurrentTime.setFont(UI.PROTOCOL_FONT);

		addTopRightComp(clockCurrentTime);

		/*
		 * The clock
		 */
		addTopRightComp(new JLabel(Data.getInstance()
				.getIcon("blank_50x50.png")));

		clockLabel.setFont(UI.PROTOCOL_FONT);

		clockButtonStart = GUITools.newImageButton();

		clockButtonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (clockWorker.isClockRunning()) {
					clockWorker.stopClock();

					updateClockButtons();
				} else {
					clockWorker.startClock();

					updateClockButtons();
				}
			}
		});

		clockButtonReset = GUITools.newImageButton(
				Data.getInstance().getIcon("clockReset_24x24_0.png"), Data
						.getInstance().getIcon("clockReset_24x24.png"));
		clockButtonReset.setToolTipText(_("Reset Stop Watch"));
		clockButtonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clockWorker.resetClock();

				updateClockButtons();
			}
		});

		addTopRightComp(clockButtonReset);
		addTopRightComp(clockLabel);
		addTopRightComp(clockButtonStart);

		/*
		 * Update clock elements
		 */
		updateClock(0);
		updateClockButtons();
		updateCurrentTime();
	}

	/**
	 * Update clock buttons.
	 */
	private void updateClockButtons() {
		if (clockWorker.isClockRunning()) {
			clockButtonStart.setIcon(Data.getInstance().getIcon(
					"clockPause_24x24_0.png"));
			clockButtonStart.setRolloverIcon(Data.getInstance().getIcon(
					"clockPause_24x24.png"));
			clockButtonStart.setToolTipText(_("Pause Stop Watch"));
		} else {
			clockButtonStart.setIcon(Data.getInstance().getIcon(
					"clockStart_24x24_0.png"));
			clockButtonStart.setRolloverIcon(Data.getInstance().getIcon(
					"clockStart_24x24.png"));
			clockButtonStart.setToolTipText(_("Start Stop Watch"));
		}
	}

	/**
	 * Creates the att panel.
	 */
	private void createAttPanel() {
		GridLayout grid = new GridLayout(4, 1);
		grid.setVgap(8);

		JPanel attendeeButtons = new JPanel(grid);

		addResiAtt = GUITools.newImageButton();
		addResiAtt
				.setIcon(Data.getInstance().getIcon("addResiAtt_25x25_0.png"));
		addResiAtt.setRolloverIcon(Data.getInstance().getIcon(
				"addResiAtt_25x25.png"));
		addResiAtt.setToolTipText(_("Add Attendee from the Attendee Pool"));
		addResiAtt.addActionListener(ActionRegistry.getInstance().get(
				AddResiAttToProtAction.class.getName()));
		attendeeButtons.add(addResiAtt);

		addAttendee = GUITools.newImageButton();
		addAttendee.setIcon(Data.getInstance().getIcon(
				"addAttendee_25x25_0.png"));
		addAttendee.setRolloverIcon(Data.getInstance().getIcon(
				"addAttendee_25x25.png"));
		addAttendee.setToolTipText(_("Add Attendee"));
		addAttendee.addActionListener(ActionRegistry.getInstance().get(
				AddAttToProtAction.class.getName()));
		attendeeButtons.add(addAttendee);

		removeAttendee = GUITools.newImageButton();
		removeAttendee.setIcon(Data.getInstance().getIcon(
				"removeAttendee_25x25_0.png"));
		removeAttendee.setRolloverIcon(Data.getInstance().getIcon(
				"removeAttendee_25x25.png"));
		removeAttendee.setToolTipText(_("Remove Attendee"));
		removeAttendee.addActionListener(ActionRegistry.getInstance().get(
				RemAttFromProtAction.class.getName()));
		attendeeButtons.add(removeAttendee);

		editAttendee = GUITools.newImageButton();
		editAttendee.setIcon(Data.getInstance().getIcon(
				"editAttendee_25x25_0.png"));
		editAttendee.setRolloverIcon(Data.getInstance().getIcon(
				"editAttendee_25x25.png"));
		editAttendee.setToolTipText(_("Edit Attendee"));
		editAttendee.addActionListener(ActionRegistry.getInstance().get(
				EditAttFromProtAction.class.getName()));
		attendeeButtons.add(editAttendee);

		editAttendee.setEnabled(false);
		removeAttendee.setEnabled(false);

		presentAttTable.setRowHeight(55);
		presentAttTable.getColumnModel().getColumn(0).setMaxWidth(55);
		presentAttTable.setShowHorizontalLines(false);
		presentAttTable.setShowVerticalLines(true);
		presentAttTable.setShowGrid(true);
		presentAttTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ActionRegistry.getInstance()
							.get(EditAttFromProtAction.class.getName())
							.actionPerformed(null);
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

		TableCellRenderer renderer = new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				JLabel label = new JLabel((String) value);
				label.setOpaque(true);
				label.setBorder(new EmptyBorder(5, 5, 5, 5));

				label.setFont(UI.PROTOCOL_FONT);

				if (isSelected) {
					label.setBackground(presentAttTable
							.getSelectionBackground());
				} else {
					int localRow = row;

					while (localRow > 0) {
						localRow = localRow - 2;
					}

					if (localRow == 0) {
						label.setBackground(UI.TABLE_ALT_COLOR);
					} else {
						label.setBackground(presentAttTable.getBackground());
					}
				}

				return label;
			}
		};

		for (int i = 1; i <= 4; i++) {
			presentAttTable.getColumnModel().getColumn(i)
					.setCellRenderer(renderer);
		}

		TableColumn col = presentAttTable.getColumnModel().getColumn(0);
		col.setCellRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				JPanel localPnl = new JPanel();

				localPnl.add(new JLabel(Data.getInstance().getIcon(
						"attendee_40x40.png")));

				if (isSelected) {
					localPnl.setBackground(presentAttTable
							.getSelectionBackground());
				} else {
					int localRow = row;

					while (localRow > 0) {
						localRow = localRow - 2;
					}

					if (localRow == 0) {
						localPnl.setBackground(UI.TABLE_ALT_COLOR);
					} else {
						localPnl.setBackground(presentAttTable.getBackground());
					}
				}

				return localPnl;
			}
		});
		presentAttTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateAttButtons();
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

		scrllP = GUITools.setIntoScrollPane(presentAttTable);
		scrllP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		scrllP.setToolTipText(_("Add Attendee to Meeting"));
		scrllP.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isAddResiAttPossible()) {
					ActionRegistry.getInstance()
							.get(AddResiAttToProtAction.class.getName())
							.actionPerformed(null);
				} else {
					ActionRegistry.getInstance()
							.get(AddAttToProtAction.class.getName())
							.actionPerformed(null);
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

		JLabel labelAttendees = new JLabel(
				_("Attendees of the current meeting:"));
		labelAttendees.setFont(UI.PROTOCOL_TITLE_FONT);

		GUITools.addComponent(attPanel, gbl, labelAttendees, 0, 0, 2, 1, 1.0,
				0.0, 20, 20, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(attPanel, gbl, scrllP, 0, 1, 1, 1, 1.0, 1.0, 20,
				20, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(attPanel, gbl, attendeeButtons, 1, 1, 1, 1, 0, 0,
				20, 0, 20, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
	}

	/**
	 * Creates the bottom org panel.
	 */
	private void createBottomOrgPanel() {
		JLabel locationLbl = new JLabel(_("Location:"));
		locationLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel dateLbl = new JLabel(_("Date:"));
		dateLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel beginLbl = new JLabel(_("Period of time:"));
		beginLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel tillLabel = new JLabel(_("to"));
		tillLabel.setFont(UI.PROTOCOL_FONT_BOLD);

		clockLabel.setFont(UI.PROTOCOL_FONT_BOLD);

		dateTxtFld = new ObservingTextField();
		dateTxtFld.setFont(UI.PROTOCOL_FONT);

		dateTxtFld.setFocusable(false);
		dateTxtFld.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		dateTxtFld.setPreferredSize(new Dimension(190, (int) dateTxtFld
				.getPreferredSize().getHeight()));
		dateTxtFld.setMinimumSize(dateTxtFld.getPreferredSize());
		dateTxtFld.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// instantiate the DatePicker
				DatePicker dp = new DatePicker(UI.getInstance()
						.getProtocolFrame(), UI.getInstance()
						.getProtocolFrame().getDateTxtFld());

				// previously selected date
				Date selectedDate = dp.parseDate(UI.getInstance()
						.getProtocolFrame().getDateTxtFld().getText());
				dp.setSelectedDate(selectedDate);
				dp.start(UI.getInstance().getProtocolFrame().getDateTxtFld());
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

		dateTxtFld.addKeyListener(updateListener);

		/*
		 * creating spinner panel
		 */
		beginMSpinner = new JSpinner(
				new RotateSpinnerNumberModel(00, 00, 59, 1));
		beginHSpinner = new JSpinner(
				new RotateSpinnerNumberModel(00, 00, 23, 1));

		endMSpinner = new JSpinner(new RotateSpinnerNumberModel(00, 00, 59, 1));
		endHSpinner = new JSpinner(new RotateSpinnerNumberModel(00, 00, 23, 1));

		beginMSpinner.setFont(UI.PROTOCOL_FONT);
		beginHSpinner.setFont(UI.PROTOCOL_FONT);

		endHSpinner.setFont(UI.PROTOCOL_FONT);
		endMSpinner.setFont(UI.PROTOCOL_FONT);

		beginMSpinner.addChangeListener(spinnerChangeListener);
		beginHSpinner.addChangeListener(spinnerChangeListener);

		endHSpinner.addChangeListener(spinnerChangeListener);
		endMSpinner.addChangeListener(spinnerChangeListener);

		locationTxtFld = new JTextField();
		locationTxtFld.setFont(UI.PROTOCOL_FONT);

		GUITools.formatSpinner(endHSpinner);
		GUITools.formatSpinner(endMSpinner);
		GUITools.formatSpinner(beginHSpinner);
		GUITools.formatSpinner(beginMSpinner);

		dateF.setTimeZone(currentProt.getDate().getTimeZone());
		dateTxtFld.setText(dateF.format(currentProt.getDate().getTime()));

		int beginHours = currentProt.getStart().get(Calendar.HOUR_OF_DAY);
		beginMSpinner.setValue(currentProt.getStart().get(Calendar.MINUTE));
		beginHSpinner.setValue(beginHours);

		int endHours = currentProt.getEnd().get(Calendar.HOUR_OF_DAY);
		endMSpinner.setValue(currentProt.getEnd().get(Calendar.MINUTE));
		endHSpinner.setValue(endHours);

		/*
		 * Correct the leading zero's
		 */
		if ((Integer) beginMSpinner.getValue() == 0) {
			((NumberEditor) beginMSpinner.getEditor()).getTextField().setText(
					"00");
		}

		if ((Integer) beginHSpinner.getValue() == 0) {
			((NumberEditor) beginHSpinner.getEditor()).getTextField().setText(
					"00");
		}

		if ((Integer) endMSpinner.getValue() == 0) {
			((NumberEditor) endMSpinner.getEditor()).getTextField().setText(
					"00");
		}

		if ((Integer) endHSpinner.getValue() == 0) {
			((NumberEditor) endHSpinner.getEditor()).getTextField().setText(
					"00");
		}

		locationTxtFld.setText(currentProt.getLocation().trim());

		JPanel spinnerPanel = new JPanel(gbl);
		spinnerPanel.setOpaque(false);

		JLabel labelDoubleDot1 = new JLabel(":");
		labelDoubleDot1.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel labelDoubleDot2 = new JLabel(":");
		labelDoubleDot2.setFont(UI.PROTOCOL_FONT_BOLD);

		GUITools.addComponent(spinnerPanel, gbl, beginHSpinner, 0, 0, 1, 1, 0,
				0, 0, 0, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(spinnerPanel, gbl, labelDoubleDot1, 1, 0, 1, 1,
				0, 0, 0, 5, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, beginMSpinner, 2, 0, 1, 1, 0,
				0, 0, 5, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(spinnerPanel, gbl, tillLabel, 3, 0, 1, 1, 1.0, 0,
				0, 10, 0, 10, GridBagConstraints.VERTICAL,
				GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, endHSpinner, 4, 0, 1, 1, 0, 0,
				0, 0, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTHEAST);
		GUITools.addComponent(spinnerPanel, gbl, labelDoubleDot2, 5, 0, 1, 1,
				0, 0, 0, 5, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, endMSpinner, 6, 0, 1, 1, 0, 0,
				0, 5, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTHEAST);

		/*
		 * adding created components to orgpanel
		 */
		GUITools.addComponent(bottomOrgPanel, gbl, dateLbl, 2, 0, 1, 1, 0.0,
				1.0, 10, 20, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(bottomOrgPanel, gbl, dateTxtFld, 3, 0, 1, 1, 0.0,
				1.0, 10, 5, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);

		GUITools.addComponent(bottomOrgPanel, gbl, locationLbl, 0, 0, 1, 1,
				0.0, 1.0, 10, 20, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(bottomOrgPanel, gbl, locationTxtFld, 1, 0, 1, 1,
				1.0, 1.0, 10, 5, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);

		GUITools.addComponent(bottomOrgPanel, gbl, beginLbl, 5, 0, 1, 1, 0.0,
				1.0, 10, 30, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.EAST);
		GUITools.addComponent(bottomOrgPanel, gbl, spinnerPanel, 6, 0, 1, 1,
				0.0, 1.0, 10, 5, 0, 25, GridBagConstraints.VERTICAL,
				GridBagConstraints.WEST);

		updateAttButtons();
	}

	/**
	 * Creates the comm and rate panel.
	 */
	private void createCommAndRatePanel() {
		JLabel impLbl = new JLabel(_("General impression of the product:"));
		impLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel recLbl = new JLabel(_("Final recommendation for the product:"));
		recLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel meetCommLbl = new JLabel(_("Comments on the meeting:"));
		meetCommLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel protCommLbl = new JLabel(_("Comments on the list of findings:"));
		protCommLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		KeyListener tabKeyListener = new KeyListener() {
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
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		};

		impTxtArea = new JTextArea();
		impTxtArea.setFont(UI.PROTOCOL_FONT);

		meetCommTxtArea = new JTextArea();
		meetCommTxtArea.setRows(4);
		meetCommTxtArea.setFont(UI.PROTOCOL_FONT);

		protCommTxtArea = new JTextArea();
		protCommTxtArea.setRows(4);
		protCommTxtArea.setFont(UI.PROTOCOL_FONT);

		recBx = new JComboBox();
		recBx.setEditable(true);
		recBx.setFont(UI.PROTOCOL_FONT);

		/*
		 * adding focus and tab listeners to TextAreas
		 */
		impTxtArea.addKeyListener(updateListener);
		impTxtArea.addKeyListener(tabKeyListener);

		meetCommTxtArea.addKeyListener(updateListener);
		meetCommTxtArea.addKeyListener(tabKeyListener);

		protCommTxtArea.addKeyListener(updateListener);
		protCommTxtArea.addKeyListener(tabKeyListener);

		for (String rec : Data.getStandardRecommendations()) {
			recBx.addItem(rec);
		}
		recBx.setSelectedIndex(0);
		recBx.addItemListener(itemListener);
		recBx.setSelectedItem(revMgmt.getRecommendation().trim());

		impTxtArea.setText(revMgmt.getImpression().trim());
		meetCommTxtArea.setText(Application.getInstance().getMeetingMgmt()
				.getMeetingComment(currentMeet).trim());
		protCommTxtArea
				.setText(protMgmt.getProtocolComment(currentProt).trim());

		impScrllPn = GUITools.setIntoScrllPn(impTxtArea);
		GUITools.scrollToTop(impScrllPn);

		meetCommScrllPn = GUITools.setIntoScrllPn(meetCommTxtArea);
		meetCommScrllPn.setMinimumSize(meetCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(meetCommScrllPn);

		protCommScrllPn = GUITools.setIntoScrllPn(protCommTxtArea);
		protCommScrllPn.setMinimumSize(protCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(protCommScrllPn);

		GUITools.addComponent(tabPanelCommAndRec, gbl, impLbl, 0, 1, 2, 1, 0,
				0, 20, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, impScrllPn, 0, 2, 2, 1,
				1.0, 1.0, 5, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, recLbl, 0, 3, 2, 1, 0.0,
				0.0, 25, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, recBx, 0, 4, 2, 1, 1.0,
				0.0, 5, 10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, meetCommLbl, 0, 5, 1, 1,
				1.0, 0.0, 25, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, meetCommScrllPn, 0, 6,
				1, 1, 1.0, 0.0, 5, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, protCommLbl, 1, 5, 1, 1,
				1.0, 0.0, 25, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, protCommScrllPn, 1, 6,
				1, 1, 1.0, 0.0, 5, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		tabPanelCommAndRec.setBorder(new EmptyBorder(0, 10, 20, 10));
	}

	/**
	 * Resets the clock.
	 */
	public void resetClock() {
		clockWorker.resetClock();
		updateClockButtons();
	}

	/**
	 * Update clock.
	 * 
	 * @param seconds
	 *            the seconds
	 */
	public void updateClock(int seconds) {
		String hrs = Integer.toString(seconds / (60 * 60));
		String min = Integer.toString((seconds / 60) % 60);
		String sec = Integer.toString(seconds % 60);

		if (hrs.length() == 1) {
			hrs = "0" + hrs;
		}

		if (min.length() == 1) {
			min = "0" + min;
		}

		if (sec.length() == 1) {
			sec = "0" + sec;
		}

		clockLabel.setText(hrs + ":" + min + ":" + sec);

		/*
		 * Show warning message
		 */
		try {
			boolean showWarning = Data.getInstance().getAppData()
					.getSettingValue(AppSettingKey.APP_SHOW_PROTOCOL_WARNING) == AppSettingValue.TRUE;

			int warningTime = Integer.parseInt(Data.getInstance().getAppData()
					.getSetting(AppSettingKey.APP_PROTOCOL_WARNING_TIME));

			if (seconds > warningTime * 60 && showWarning
					&& !clockWorker.isWarningDisplayed()) {
				String message = MessageFormat
						.format(_("This review meeting is running for {0} minutes already. Therefore it is recommended to finalize the meeting now and continue the review at a later point in time."),
								Integer.toString(warningTime));

				JOptionPane.showMessageDialog(UI.getInstance()
						.getProtocolFrame(), GUITools.getMessagePane(message),
						_("Information"), JOptionPane.INFORMATION_MESSAGE);

				clockWorker.setWarningDisplayed(true);
			}
		} catch (Exception e) {
			/*
			 * do nothing
			 */
		}
	}

	/**
	 * Update current time.
	 */
	public void updateCurrentTime() {
		clockCurrentTime.setText(sdfCurrentTime.format(new Date().getTime())
				+ " Uhr");
	}

	/**
	 * Instantiates a new protocol frame.
	 * 
	 * @param fullscreen
	 *            the fullscreen
	 */
	public FindingsListFrame(boolean fullscreen) {
		super();

		this.fullscreen = fullscreen;
		this.nativeFullscrSupported = gd.isFullScreenSupported();

		/*
		 * Because of some problems do not use the native fullscreen
		 * functionality
		 */
		if (UI.getInstance().getPlatform() == UI.Platform.WINDOWS
				|| UI.getInstance().getPlatform() == UI.Platform.MAC) {
			nativeFullscrSupported = false;
		}

		UI.getInstance().getProtocolClockWorker().addObserverFrame(this);

		setTitle(_("List of Findings"));
		setStatusMessage(_("List of findings successfully loaded."), false);

		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		/*
		 * Format bottom panel in org tab
		 */
		bottomOrgPanel.setBackground(UI.TABLE_ALT_COLOR);
		bottomOrgPanel.setBorder(new EmptyBorder(20, 0, 30, 0));

		GUITools.addComponent(tabPanelOrg, gbl, attPanel, 0, 0, 1, 1, 1.0, 1.0,
				0, 0, 20, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelOrg, gbl, new JSeparator(), 0, 1, 1, 1,
				1.0, 0.0, 0, 0, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelOrg, gbl, bottomOrgPanel, 0, 2, 1, 1,
				1.0, 0.0, 0, 0, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		createToolBar();

		setLocationToCenter();

		if (fullscreen) {
			setUndecorated(true);

			if (!nativeFullscrSupported) {
				setMinimumSize(Toolkit.getDefaultToolkit().getScreenSize());
				setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
				setSize(Toolkit.getDefaultToolkit().getScreenSize());

				setLocation(0, 0);

				setResizable(false);
				setAlwaysOnTop(true);
			}

			toFront();

			// Dimension screenSize =
			// Toolkit.getDefaultToolkit().getScreenSize();
			// setPreferredSize(screenSize);
		} else {
			setMinimumSize(new Dimension(900, 710));
			setPreferredSize(new Dimension(900, 710));

			pack();
		}

		setExtendedState(Frame.MAXIMIZED_BOTH);

		createHints();

		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (protCommTxtArea.getText().trim().equals("")) {
					currentProt.setComments("");
				}

				GUITools.executeSwingWorker(new ImageEditorWriteWorker(
						currentProt));

				setVisible(false);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
				if (isFullscreen()) {
					setExtendedState(MAXIMIZED_BOTH);
				}
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
		});

		GUITools.executeSwingWorker(updateWorker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (bodyCreated) {
					updateHints();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		updateClockButtons();

		setExtendedState(Frame.MAXIMIZED_BOTH);

		if (fullscreen && nativeFullscrSupported) {
			if (vis) {
				gd.setFullScreenWindow(this);
			} else {
				gd.setFullScreenWindow(null);
			}
		}

		/*
		 * Show fullscreen button in toolbar
		 */
		try {
			if (appData.getSettingValue(AppSettingKey.APP_ALLOW_FULLSCREEN) == AppSettingValue.TRUE) {
				tbFullscreen.setVisible(true);
			} else {
				tbFullscreen.setVisible(false);
			}
		} catch (DataException exc) {
			/*
			 * Ignore
			 */
		}

		if (vis) {
			clockWorker.startClock();
			updateClockButtons();

			Data.getInstance().getResiData().addObserver(this);

			UI.getInstance().getAutoBackupWorker().addObserverFrame(this);
			UI.getInstance().getAutoSaveWorker().addObserverFrame(this);

			update(null, null);
		} else {
			clockWorker.stopClock();
			updateClockButtons();

			Data.getInstance().getResiData().deleteObserver(this);

			UI.getInstance().getAutoBackupWorker().removeObserverFrame(this);
			UI.getInstance().getAutoSaveWorker().removeObserverFrame(this);
		}

		super.setVisible(vis);

		boolean protFrameVisible = UI.getInstance().getProtocolFrame()
				.isVisible();

		UI.getInstance().getMainFrame().setVisible(!protFrameVisible);
	}

	/**
	 * Update att buttons.
	 */
	public void updateAttButtons() {
		if (presentAttTable.getSelectedRow() == -1) {
			removeAttendee.setEnabled(false);
			editAttendee.setEnabled(false);
		} else {
			removeAttendee.setEnabled(true);
			editAttendee.setEnabled(true);
		}

		addResiAtt.setEnabled(isAddResiAttPossible());
	}

	public boolean isAddResiAttPossible() {
		for (Attendee att : Application.getInstance().getAttendeeMgmt()
				.getAttendees()) {
			if (!Application.getInstance().getProtocolMgmt()
					.isAttendee(att, currentProt)) {
				return true;
			}
		}

		return false;
	}

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

	private ChangeListener spinnerChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			updateTime = System.currentTimeMillis();
		}
	};

	private ItemListener itemListener = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			updateTime = System.currentTimeMillis();
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
						updateResiData();

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								updateHints();
							}
						});
					}
				} catch (Exception e) {
					/*
					 * do nothing and wait for the next run of this worker
					 */
				}
			}
		}
	};

	/**
	 * Update resi data.
	 */
	public void updateResiData() {

		try {
			currentProt.setDate(GUITools.dateString2Calendar(
					dateTxtFld.getText(), dateF));
		} catch (ParseException e) {

			currentProt.setDate(null);
		}

		currentProt.setLocation(locationTxtFld.getText());

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE,
				Integer.parseInt(beginMSpinner.getValue().toString()));
		cal.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(beginHSpinner.getValue().toString()));
		currentProt.setStart(cal);

		Calendar calEnd = Calendar.getInstance();
		calEnd.set(Calendar.MINUTE,
				Integer.parseInt(endMSpinner.getValue().toString()));
		calEnd.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(endHSpinner.getValue().toString()));
		currentProt.setEnd(calEnd);

		revMgmt.setImpression(impTxtArea.getText());

		String comm = meetCommTxtArea.getText();
		Application.getInstance().getMeetingMgmt()
				.setMeetingComment(comm, currentMeet);

		revMgmt.setRecommendation((String) recBx.getSelectedItem());

		protMgmt.setProtocolComment(protCommTxtArea.getText(), currentProt);

	}

	/**
	 * Unmark all components.
	 */
	private void unmarkAllComponents() {
		dateTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		beginHSpinner.setBorder(UI.STANDARD_BORDER);
		beginMSpinner.setBorder(UI.STANDARD_BORDER);
		endHSpinner.setBorder(UI.STANDARD_BORDER);
		endMSpinner.setBorder(UI.STANDARD_BORDER);
		scrllP.setBorder(UI.STANDARD_BORDER);
		recBx.setBorder(UI.STANDARD_BORDER);
		impScrllPn.setBorder(UI.STANDARD_BORDER);
		protCommScrllPn.setBorder(UI.STANDARD_BORDER);

		componentMarked = false;
	}

	/**
	 * Mark component.
	 * 
	 * @param comp
	 *            the component to mark
	 */
	private void markComponent(JComponent comp) {
		boolean mark = false;

		try {
			if (Data.getInstance().getAppData()
					.getSettingValue(AppSettingKey.APP_HIGHLIGHT_FIELDS) == AppSettingValue.TRUE) {
				mark = true;
			}
		} catch (DataException e) {
			mark = true;
		}

		if (!componentMarked && mark) {
			if (comp instanceof JTextField) {
				comp.setBorder(UI.MARKED_BORDER_INLINE);
			} else {
				comp.setBorder(UI.MARKED_BORDER);
			}

			componentMarked = true;
		}
	}

	/**
	 * Creates the hints.
	 */
	private void createHints() {
		setNumberOfHints(2);

		hintAtt = new HintItem(
				_("Please add at least one attendee to the meeting by choosing one from the attendees pool or create a new one (Tab 'Organizational')."),
				HintItem.WARNING);

		hintImprRec = new HintItem(
				_("Please enter the general impression and the final recommendation for the product into the provided text field (Tab 'Impression · Comments · Recommendation')."),
				HintItem.WARNING);

		hintFind = new HintItem(
				_("For every finding enter at least a description (Tab 'Findings')."),
				HintItem.WARNING);

		hintOk = new HintItem(
				_("The meeting data and its list of findings is complete."),
				HintItem.OK);

		hintInfoNewFinding = new HintItem(
				_("In order to add a new finding to the list of findings use the 'Add Finding' button (Tab 'Findings')."),
				HintItem.INFO);
	}

	/**
	 * Update hints.
	 */
	private void updateHints() {
		boolean warningErrorHints = false;

		List<HintItem> hints = new ArrayList<HintItem>();

		unmarkAllComponents();

		if (protMgmt.getAttendees(currentProt).size() == 0) {
			hints.add(hintAtt);

			if (tabPanelOrg.isVisible()) {
				markComponent(scrllP);
			}

			warningErrorHints = true;

			tabbedPane.setIconAt(0, ICON_TAB_WARN);
		} else {
			tabbedPane.setIconAt(0, ICON_TAB_OK);
		}

		if (!findMgmt.areAllFindingsComplete(currentProt)) {
			hints.add(hintFind);

			warningErrorHints = true;

			tabbedPane.setIconAt(1, ICON_TAB_WARN);
		} else {
			tabbedPane.setIconAt(1, ICON_TAB_OK);
		}

		if (revMgmt.getImpression().trim().equals("")
				|| revMgmt.getRecommendation().trim().equals("")) {
			hints.add(hintImprRec);

			if (tabPanelCommAndRec.isVisible()) {
				if (revMgmt.getImpression().trim().equals("")) {
					markComponent(impScrllPn);
				}

				if (revMgmt.getRecommendation().trim().equals("")) {
					markComponent(recBx);
				}
			}

			warningErrorHints = true;

			tabbedPane.setIconAt(2, ICON_TAB_WARN);
		} else {
			tabbedPane.setIconAt(2, ICON_TAB_OK);
		}

		if (!warningErrorHints) {
			hints.add(hintOk);
		}

		hints.add(hintInfoNewFinding);

		setHints(hints);
	}

	public ImageEditorDialog getImageEditor(File image) {
		String imagePath = image.getAbsolutePath();

		ImageEditorDialog editor = imageEditors.get(imagePath);

		if (editor == null) {
			editor = new ImageEditorDialog(UI.getInstance().getProtocolFrame(),
					image);

			imageEditors.put(imagePath, editor);
		}

		return editor;
	}

	public Map<String, ImageEditorDialog> getImageEditors() {
		return imageEditors;
	}

	public void activateFindingsTab() {
		tabbedPane.setSelectedIndex(1);
	}

}
