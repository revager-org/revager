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

import static org.revager.app.model.Data.translate;

import java.awt.BorderLayout;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
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
import org.revager.gamecontroller.Dashboard;
import org.revager.gui.AbstractFrame;
import org.revager.gui.UI;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.ExitAction;
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
import org.revager.gui.presentationView.PresentationFrame;
import org.revager.gui.workers.ImageEditorWriteWorker;
import org.revager.gui.workers.ProtocolClockWorker;
import org.revager.tools.GUITools;

/**
 * The Class ProtocolFrame.
 */
@SuppressWarnings("serial")
public class FindingsListFrame extends AbstractFrame implements Observer {

	private final ImageIcon ICON_TAB_OK = Data.getInstance().getIcon("tabOk_24x24.png");
	private final ImageIcon ICON_TAB_WARN = Data.getInstance().getIcon("tabWarning_24x24.png");

	private Map<String, ImageEditorDialog> imageEditors = new HashMap<>();

	private boolean componentMarked = false;
	private boolean fullscreen = false;
	private boolean nativeFullscrSupported = false;
	private boolean bodyCreated = false;

	private transient GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private transient FindingManagement findMgmt = Application.getInstance().getFindingMgmt();
	private transient ApplicationData appData = Data.getInstance().getAppData();

	private GridBagLayout gbl = new GridBagLayout();
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel tabPanelOrg = new JPanel(gbl);
	private JPanel tabGenImp = new JPanel(gbl);
	private JPanel bottomOrgPanel = new JPanel(gbl);

	private JButton tbConfirmProt;
	private JButton tbPdfExport;
	private JButton tbCsvExport;
	private JButton tbPresenationView;
	private JButton tbFullscreen;

	private JPanel attPanel = new JPanel(gbl);
	private JPanel tabPanelCommAndRec = new JPanel(gbl);
	private JTextField locationTxtFld;
	private JScrollPane scrllP;

	/*
	 * Hint items
	 */
	private transient HintItem hintAtt;
	private transient HintItem hintImpr;
	private transient HintItem hintRec;
	private transient HintItem hintFind;
	private transient HintItem hintOk;
	private transient HintItem hintInfoNewFinding;

	/*
	 * things for finding tab
	 */
	private transient Protocol currentProt;

	private FindingsTab tabPanelFindings;
	private SimpleDateFormat sdfCurrentTime = new SimpleDateFormat("d. MMMM yyyy | HH:mm");
	private transient ProtocolClockWorker clockWorker = UI.getInstance().getProtocolClockWorker();
	private JLabel clockLabel = new JLabel();
	private JLabel clockCurrentTime = new JLabel();
	private JButton clockButtonStart;
	private JButton clockButtonReset;

	private PresentAttendeesTableModel patm;
	private JTable presentAttTable;

	private transient List<Attendee> presentAttList;
	private transient Meeting currentMeet = null;

	private JSpinner beginMSpinner;
	private JSpinner beginHSpinner;
	private JSpinner endMSpinner;
	private JSpinner endHSpinner;
	private ObservingTextField dateTxtFld;
	private JComboBox<String> recBx;
	private JTextArea impTxtArea;
	private JTextArea meetCommTxtArea;
	private JTextArea protCommTxtArea;
	private JScrollPane impScrllPn;
	private JScrollPane meetCommScrllPn;
	private JScrollPane protCommScrllPn;

	private transient ProtocolManagement protMgmt = Application.getInstance().getProtocolMgmt();
	private transient ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();
	/*
	 * attendee buttons
	 */
	private JButton addResiAtt;
	private JButton addAttendee;
	private JButton removeAttendee;
	private JButton editAttendee;

	private DateFormat dateF = SimpleDateFormat.getDateInstance(DateFormat.LONG);

	private transient ChangeListener tabChangeListener = e -> SwingUtilities.invokeLater(this::updateHints);

	public PresentAttendeesTableModel getPatm() {
		return patm;
	}

	public synchronized Meeting getMeeting() {
		return currentMeet;
	}

	public Protocol getCurrentProt() {
		return currentProt;
	}

	public synchronized void setMeeting(Meeting meet) {
		currentMeet = meet;
		currentProt = meet.getProtocol();

		patm = new PresentAttendeesTableModel(currentProt);
		presentAttTable = GUITools.newStandardTable(patm, false);

		createBody();
	}

	public JTable getPresentAttTable() {
		return presentAttTable;
	}

	public List<Attendee> getPresentAttList() {
		return presentAttList;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public ObservingTextField getDateTxtFld() {
		return dateTxtFld;
	}

	private void createBody() {
		SwingUtilities.invokeLater(() -> {
			try {
				attPanel.removeAll();
				createAttPanel();
				attPanel.validate();

				bottomOrgPanel.removeAll();
				createBottomOrgPanel();
				bottomOrgPanel.validate();

				tabGenImp.removeAll();
				createImpPanel();
				tabGenImp.validate();

				tabPanelCommAndRec.removeAll();
				createCommAndRatePanel();
				tabPanelCommAndRec.validate();
			} catch (Exception exc) {
				// Workaround for a threading problem
				exc.printStackTrace();

				JOptionPane.showMessageDialog(UI.getInstance().getProtocolFrame(),
						GUITools.getMessagePane(translate("Severe problem occurred! RevAger has to be restarted.")),
						translate("Problem occurred"), JOptionPane.ERROR_MESSAGE);

				ExitAction exitAction = ((ExitAction) ActionRegistry.getInstance().get(ExitAction.class.getName()));

				exitAction.setRestartAgain(true);
				exitAction.actionPerformed(null);
			}
		});

		SwingUtilities.invokeLater(() -> {
			tabPanelFindings = new FindingsTab(currentProt);
			tabbedPane.removeAll();
			tabbedPane.removeChangeListener(tabChangeListener);
			tabbedPane.setTabPlacement(SwingConstants.TOP);
			tabbedPane.add(translate("Organizational"), tabPanelOrg);
			tabbedPane.add(translate("Impression"), tabGenImp);
			tabbedPane.add(translate("Findings"), tabPanelFindings);
			tabbedPane.add(translate("Comments & Recommendation"), tabPanelCommAndRec);
			tabbedPane.addChangeListener(tabChangeListener);

			add(tabbedPane);
			bodyCreated = true;
		});
	}

	private void createToolBar() {

		tbConfirmProt = GUITools.newImageButton(Data.getInstance().getIcon("confirmProtocol_50x50_0.png"),
				Data.getInstance().getIcon("confirmProtocol_50x50.png"));
		tbConfirmProt.setToolTipText(translate("Confirm and close list of findings"));
		tbConfirmProt.addActionListener(e -> {
			if (StringUtils.isBlank(protCommTxtArea.getText())) {
				currentProt.setComments("");
			}

			GUITools.executeSwingWorker(new ImageEditorWriteWorker(currentProt));

			setVisible(false);
			UI.getInstance().getPresentationFrame().setVisible(false);
		});

		addTopComponent(tbConfirmProt);

		JButton sepBttn = GUITools.newImageButton();
		sepBttn.setIcon(Data.getInstance().getIcon("sep_50x50.png"));
		sepBttn.setEnabled(false);
		addTopComponent(sepBttn);

		tbPdfExport = GUITools.newImageButton(Data.getInstance().getIcon("PDFExport_50x50_0.png"),
				Data.getInstance().getIcon("PDFExport_50x50.png"));
		tbPdfExport.setToolTipText(translate("Export List of Findings as PDF File"));
		tbPdfExport.addActionListener(e -> UI.getInstance().getExportPDFProtocolDialog().setVisible(true));

		addTopComponent(tbPdfExport);

		tbCsvExport = GUITools.newImageButton(Data.getInstance().getIcon("CSVExport_50x50_0.png"),
				Data.getInstance().getIcon("CSVExport_50x50.png"));
		tbCsvExport.setToolTipText(translate("Export List of Findings as CSV File"));
		tbCsvExport.addActionListener(e -> UI.getInstance().getExportCSVDialog().setVisible(true));

		addTopComponent(tbCsvExport);

		tbPresenationView = GUITools.newImageButton(Data.getInstance().getIcon("tv_50x50_0.png"),
				Data.getInstance().getIcon("tv_50x50.png"));
		tbPresenationView.setToolTipText(translate("Open Presentation View"));
		tbPresenationView.addActionListener((ActionEvent e) -> {
			PresentationFrame presentationFrame = UI.getInstance().getPresentationFrame();
			presentationFrame.setVisible(!presentationFrame.isVisible());
		});
		addTopComponent(tbPresenationView);

		/*
		 * Fullscreen
		 */
		tbFullscreen = GUITools.newImageButton();
		if (fullscreen) {
			tbFullscreen.setIcon(Data.getInstance().getIcon("fullscreenClose_50x50_0.png"));
			tbFullscreen.setRolloverIcon(Data.getInstance().getIcon("fullscreenClose_50x50.png"));
			tbFullscreen.setToolTipText(translate("Exit Fullscreen"));
		} else {
			tbFullscreen.setIcon(Data.getInstance().getIcon("fullscreen_50x50_0.png"));
			tbFullscreen.setRolloverIcon(Data.getInstance().getIcon("fullscreen_50x50.png"));
			tbFullscreen.setToolTipText(translate("Change to Fullscreen mode"));
		}
		tbFullscreen.addActionListener(e -> UI.getInstance().getProtocolFrame(!isFullscreen()).setVisible(true));

		addTopComponent(tbFullscreen);

		/*
		 * current time
		 */
		clockCurrentTime.setFont(UI.VERY_LARGE_FONT);

		addTopRightComp(clockCurrentTime);

		/*
		 * The clock
		 */
		addTopRightComp(new JLabel(Data.getInstance().getIcon("blank_50x50.png")));

		clockLabel.setFont(UI.VERY_LARGE_FONT);

		clockButtonStart = GUITools.newImageButton();

		clockButtonStart.addActionListener(e -> {
			if (clockWorker.isClockRunning()) {
				clockWorker.stopClock();
			} else {
				clockWorker.startClock();
			}
			updateClockButtons();
		});

		clockButtonReset = GUITools.newImageButton(Data.getInstance().getIcon("clockReset_24x24_0.png"),
				Data.getInstance().getIcon("clockReset_24x24.png"));
		clockButtonReset.setToolTipText(translate("Reset Stop Watch"));
		clockButtonReset.addActionListener(e -> {
			clockWorker.resetClock();
			updateClockButtons();
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

	private void updateClockButtons() {
		if (clockWorker.isClockRunning()) {
			clockButtonStart.setIcon(Data.getInstance().getIcon("clockPause_24x24_0.png"));
			clockButtonStart.setRolloverIcon(Data.getInstance().getIcon("clockPause_24x24.png"));
			clockButtonStart.setToolTipText(translate("Pause Stop Watch"));
		} else {
			clockButtonStart.setIcon(Data.getInstance().getIcon("clockStart_24x24_0.png"));
			clockButtonStart.setRolloverIcon(Data.getInstance().getIcon("clockStart_24x24.png"));
			clockButtonStart.setToolTipText(translate("Start Stop Watch"));
		}
	}

	private void createAttPanel() {
		GridLayout grid = new GridLayout(4, 1);
		grid.setVgap(8);

		JPanel attendeeButtons = new JPanel(grid);

		addResiAtt = GUITools.newImageButton();
		addResiAtt.setIcon(Data.getInstance().getIcon("addResiAtt_25x25_0.png"));
		addResiAtt.setRolloverIcon(Data.getInstance().getIcon("addResiAtt_25x25.png"));
		addResiAtt.setToolTipText(translate("Add Attendee from the Attendee Pool"));
		addResiAtt.addActionListener(ActionRegistry.getInstance().get(AddResiAttToProtAction.class.getName()));
		attendeeButtons.add(addResiAtt);

		addAttendee = GUITools.newImageButton();
		addAttendee.setIcon(Data.getInstance().getIcon("addAttendee_25x25_0.png"));
		addAttendee.setRolloverIcon(Data.getInstance().getIcon("addAttendee_25x25.png"));
		addAttendee.setToolTipText(translate("Add Attendee"));
		addAttendee.addActionListener(ActionRegistry.getInstance().get(AddAttToProtAction.class.getName()));
		attendeeButtons.add(addAttendee);

		removeAttendee = GUITools.newImageButton();
		removeAttendee.setIcon(Data.getInstance().getIcon("removeAttendee_25x25_0.png"));
		removeAttendee.setRolloverIcon(Data.getInstance().getIcon("removeAttendee_25x25.png"));
		removeAttendee.setToolTipText(translate("Remove Attendee"));
		removeAttendee.addActionListener(ActionRegistry.getInstance().get(RemAttFromProtAction.class.getName()));
		attendeeButtons.add(removeAttendee);

		editAttendee = GUITools.newImageButton();
		editAttendee.setIcon(Data.getInstance().getIcon("editAttendee_25x25_0.png"));
		editAttendee.setRolloverIcon(Data.getInstance().getIcon("editAttendee_25x25.png"));
		editAttendee.setToolTipText(translate("Edit Attendee"));
		editAttendee.addActionListener(ActionRegistry.getInstance().get(EditAttFromProtAction.class.getName()));
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
					ActionRegistry.getInstance().get(EditAttFromProtAction.class.getName()).actionPerformed(null);
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

		TableCellRenderer renderer = (table, value, isSelected, hasFocus, row, column) -> {
			JLabel label = new JLabel((String) value);
			label.setOpaque(true);
			label.setBorder(new EmptyBorder(5, 5, 5, 5));

			label.setFont(UI.VERY_LARGE_FONT);

			if (isSelected) {
				label.setBackground(presentAttTable.getSelectionBackground());
			} else {
				if (row % 2 == 0) {
					label.setBackground(UI.TABLE_ALT_COLOR);
				} else {
					label.setBackground(presentAttTable.getBackground());
				}
			}
			return label;
		};

		for (int i = 1; i <= 4; i++) {
			presentAttTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}

		TableColumn col = presentAttTable.getColumnModel().getColumn(0);
		col.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
			JPanel localPnl = new JPanel();
			localPnl.add(new JLabel(Data.getInstance().getIcon("attendee_40x40.png")));
			if (isSelected) {
				localPnl.setBackground(presentAttTable.getSelectionBackground());
			} else {
				if (row % 2 == 0) {
					localPnl.setBackground(UI.TABLE_ALT_COLOR);
				} else {
					localPnl.setBackground(presentAttTable.getBackground());
				}
			}
			return localPnl;
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
		scrllP.setToolTipText(translate("Add Attendee to Meeting"));
		scrllP.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isAddResiAttPossible()) {
					ActionRegistry.getInstance().get(AddResiAttToProtAction.class.getName()).actionPerformed(null);
				} else {
					ActionRegistry.getInstance().get(AddAttToProtAction.class.getName()).actionPerformed(null);
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

		JLabel labelAttendees = new JLabel(translate("Attendees of the current meeting:"));
		labelAttendees.setFont(UI.HUGE_FONT_BOLD);

		GUITools.addComponent(attPanel, gbl, labelAttendees, 0, 0, 2, 1, 1.0, 0.0, 20, 20, 0, 20,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(attPanel, gbl, scrllP, 0, 1, 1, 1, 1.0, 1.0, 20, 20, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(attPanel, gbl, attendeeButtons, 1, 1, 1, 1, 0, 0, 20, 0, 20, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
	}

	private void createImpPanel() {
		JLabel impLbl = new JLabel(translate("General impression of the product:"));
		impLbl.setFont(UI.VERY_LARGE_FONT_BOLD);

		impTxtArea = new JTextArea();
		impTxtArea.setFont(UI.VERY_LARGE_FONT);

		impTxtArea.addKeyListener(updateListener);
		impTxtArea.addKeyListener(tabKeyListener);

		impTxtArea.setText(revMgmt.getImpression().trim());

		impScrllPn = GUITools.setIntoScrllPn(impTxtArea);
		GUITools.scrollToTop(impScrllPn);

		GUITools.addComponent(tabGenImp, gbl, impLbl, 0, 1, 2, 1, 0, 0, 20, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);

		GUITools.addComponent(tabGenImp, gbl, impScrllPn, 0, 2, 2, 1, 1.0, 1.0, 5, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		tabGenImp.setBorder(new EmptyBorder(0, 10, 20, 10));
	}

	private void createBottomOrgPanel() {
		JLabel locationLbl = new JLabel(translate("Location:"));
		locationLbl.setFont(UI.VERY_LARGE_FONT_BOLD);

		JLabel dateLbl = new JLabel(translate("Date:"));
		dateLbl.setFont(UI.VERY_LARGE_FONT_BOLD);

		JLabel beginLbl = new JLabel(translate("Period of time:"));
		beginLbl.setFont(UI.VERY_LARGE_FONT_BOLD);

		JLabel tillLabel = new JLabel(translate("to"));
		tillLabel.setFont(UI.VERY_LARGE_FONT_BOLD);

		clockLabel.setFont(UI.VERY_LARGE_FONT_BOLD);

		dateTxtFld = new ObservingTextField();
		dateTxtFld.setFont(UI.VERY_LARGE_FONT);

		dateTxtFld.setFocusable(false);
		dateTxtFld.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		dateTxtFld.setPreferredSize(new Dimension(190, (int) dateTxtFld.getPreferredSize().getHeight()));
		dateTxtFld.setMinimumSize(dateTxtFld.getPreferredSize());
		dateTxtFld.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// instantiate the DatePicker
				DatePicker dp = new DatePicker(UI.getInstance().getProtocolFrame(),
						UI.getInstance().getProtocolFrame().getDateTxtFld());

				// previously selected date
				Date selectedDate = dp.parseDate(UI.getInstance().getProtocolFrame().getDateTxtFld().getText());
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
		beginMSpinner = new JSpinner(new RotateSpinnerNumberModel(00, 00, 59, 1));
		beginHSpinner = new JSpinner(new RotateSpinnerNumberModel(00, 00, 23, 1));

		endMSpinner = new JSpinner(new RotateSpinnerNumberModel(00, 00, 59, 1));
		endHSpinner = new JSpinner(new RotateSpinnerNumberModel(00, 00, 23, 1));

		beginMSpinner.setFont(UI.VERY_LARGE_FONT);
		beginHSpinner.setFont(UI.VERY_LARGE_FONT);
		endHSpinner.setFont(UI.VERY_LARGE_FONT);
		endMSpinner.setFont(UI.VERY_LARGE_FONT);

		beginMSpinner.addChangeListener(spinnerChangeListener);
		beginHSpinner.addChangeListener(spinnerChangeListener);
		endHSpinner.addChangeListener(spinnerChangeListener);
		endMSpinner.addChangeListener(spinnerChangeListener);

		locationTxtFld = new JTextField();
		locationTxtFld.setFont(UI.VERY_LARGE_FONT);

		/*
		 * Hide border if the application runs on Mac OS X
		 */
		boolean hideBorder = UI.getInstance().getPlatform() == UI.Platform.MAC;

		GUITools.formatSpinner(endHSpinner, hideBorder);
		GUITools.formatSpinner(endMSpinner, hideBorder);
		GUITools.formatSpinner(beginHSpinner, hideBorder);
		GUITools.formatSpinner(beginMSpinner, hideBorder);

		// TODO: In some cases 'currentProt.getDate()' returns null.
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
			((NumberEditor) beginMSpinner.getEditor()).getTextField().setText("00");
		}

		if ((Integer) beginHSpinner.getValue() == 0) {
			((NumberEditor) beginHSpinner.getEditor()).getTextField().setText("00");
		}

		if ((Integer) endMSpinner.getValue() == 0) {
			((NumberEditor) endMSpinner.getEditor()).getTextField().setText("00");
		}

		if ((Integer) endHSpinner.getValue() == 0) {
			((NumberEditor) endHSpinner.getEditor()).getTextField().setText("00");
		}

		locationTxtFld.setText(currentProt.getLocation().trim());

		JPanel spinnerPanel = new JPanel(gbl);
		spinnerPanel.setOpaque(false);

		JLabel labelDoubleDot1 = new JLabel(":");
		labelDoubleDot1.setFont(UI.VERY_LARGE_FONT_BOLD);

		JLabel labelDoubleDot2 = new JLabel(":");
		labelDoubleDot2.setFont(UI.VERY_LARGE_FONT_BOLD);

		GUITools.addComponent(spinnerPanel, gbl, beginHSpinner, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0,
				GridBagConstraints.VERTICAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(spinnerPanel, gbl, labelDoubleDot1, 1, 0, 1, 1, 0, 0, 0, 5, 0, 0,
				GridBagConstraints.VERTICAL, GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, beginMSpinner, 2, 0, 1, 1, 0, 0, 0, 5, 0, 0,
				GridBagConstraints.VERTICAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(spinnerPanel, gbl, tillLabel, 3, 0, 1, 1, 1.0, 0, 0, 10, 0, 10,
				GridBagConstraints.VERTICAL, GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, endHSpinner, 4, 0, 1, 1, 0, 0, 0, 0, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTHEAST);
		GUITools.addComponent(spinnerPanel, gbl, labelDoubleDot2, 5, 0, 1, 1, 0, 0, 0, 5, 0, 0,
				GridBagConstraints.VERTICAL, GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, endMSpinner, 6, 0, 1, 1, 0, 0, 0, 5, 0, 0, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTHEAST);

		/*
		 * adding created components to orgpanel
		 */
		GUITools.addComponent(bottomOrgPanel, gbl, dateLbl, 2, 0, 1, 1, 0.0, 1.0, 10, 20, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(bottomOrgPanel, gbl, dateTxtFld, 3, 0, 1, 1, 0.0, 1.0, 10, 5, 0, 0,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);

		GUITools.addComponent(bottomOrgPanel, gbl, locationLbl, 0, 0, 1, 1, 0.0, 1.0, 10, 20, 0, 0,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(bottomOrgPanel, gbl, locationTxtFld, 1, 0, 1, 1, 1.0, 1.0, 10, 5, 0, 10,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);

		GUITools.addComponent(bottomOrgPanel, gbl, beginLbl, 5, 0, 1, 1, 0.0, 1.0, 10, 30, 0, 0,
				GridBagConstraints.NONE, GridBagConstraints.EAST);
		GUITools.addComponent(bottomOrgPanel, gbl, spinnerPanel, 6, 0, 1, 1, 0.0, 1.0, 10, 5, 0, 25,
				GridBagConstraints.VERTICAL, GridBagConstraints.WEST);

		updateAttButtons();
	}

	private void createCommAndRatePanel() {

		JLabel recLbl = new JLabel(translate("Final recommendation for the product:"));
		recLbl.setFont(UI.VERY_LARGE_FONT_BOLD);

		JLabel meetCommLbl = new JLabel(translate("Comments on the meeting:"));
		meetCommLbl.setFont(UI.VERY_LARGE_FONT_BOLD);

		JLabel protCommLbl = new JLabel(translate("Comments on the list of findings:"));
		protCommLbl.setFont(UI.VERY_LARGE_FONT_BOLD);

		meetCommTxtArea = new JTextArea();
		meetCommTxtArea.setRows(4);
		meetCommTxtArea.setFont(UI.VERY_LARGE_FONT);

		protCommTxtArea = new JTextArea();
		protCommTxtArea.setRows(4);
		protCommTxtArea.setFont(UI.VERY_LARGE_FONT);

		recBx = new JComboBox<>();
		recBx.setEditable(true);
		recBx.setFont(UI.VERY_LARGE_FONT);

		/*
		 * adding focus and tab listeners to TextAreas
		 */

		meetCommTxtArea.addKeyListener(updateListener);
		meetCommTxtArea.addKeyListener(tabKeyListener);

		protCommTxtArea.addKeyListener(updateListener);
		protCommTxtArea.addKeyListener(tabKeyListener);

		for (String rec : Data.getDefaultRecommendations()) {
			recBx.addItem(rec);
		}
		recBx.setSelectedIndex(0);
		recBx.addItemListener(itemListener);
		recBx.setSelectedItem(revMgmt.getRecommendation().trim());

		meetCommTxtArea.setText(Application.getInstance().getMeetingMgmt().getMeetingComment(currentMeet).trim());
		protCommTxtArea.setText(protMgmt.getProtocolComment(currentProt).trim());

		meetCommScrllPn = GUITools.setIntoScrllPn(meetCommTxtArea);
		meetCommScrllPn.setMinimumSize(meetCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(meetCommScrllPn);

		protCommScrllPn = GUITools.setIntoScrllPn(protCommTxtArea);
		protCommScrllPn.setMinimumSize(protCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(protCommScrllPn);

		GUITools.addComponent(tabPanelCommAndRec, gbl, recLbl, 0, 3, 2, 1, 0.0, 0.0, 20, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, recBx, 0, 4, 2, 1, 1.0, 0.0, 5, 10, 0, 10,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, meetCommLbl, 0, 5, 1, 1, 1.0, 0.0, 25, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, meetCommScrllPn, 0, 6, 1, 1, 1.0, 1.0, 5, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, protCommLbl, 1, 5, 1, 1, 1.0, 0.0, 25, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelCommAndRec, gbl, protCommScrllPn, 1, 6, 1, 1, 1.0, 1.0, 5, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		tabPanelCommAndRec.setBorder(new EmptyBorder(0, 10, 20, 10));
	}

	public void resetClock() {
		clockWorker.resetClock();
		updateClockButtons();
	}

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

		try {
			boolean showWarning = Data.getInstance().getAppData()
					.getSettingValue(AppSettingKey.APP_SHOW_PROTOCOL_WARNING) == AppSettingValue.TRUE;

			int warningTime = Integer
					.parseInt(Data.getInstance().getAppData().getSetting(AppSettingKey.APP_PROTOCOL_WARNING_TIME));

			if (seconds > warningTime * 60 && showWarning && !clockWorker.isWarningDisplayed()) {
				clockWorker.setWarningDisplayed(true);
				String message = MessageFormat.format(
						translate(
								"This review meeting is running for {0} minutes already. Therefore it is recommended to finalize the meeting now and continue the review at a later point in time."),
						Integer.toString(warningTime));

				JOptionPane.showMessageDialog(UI.getInstance().getProtocolFrame(), GUITools.getMessagePane(message),
						translate("Information"), JOptionPane.INFORMATION_MESSAGE);

			}
		} catch (Exception e) {
		}
	}

	public void updateCurrentTime() {
		clockCurrentTime.setText(sdfCurrentTime.format(new Date().getTime()));
	}

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

		UI.getInstance().getProtocolClockWorker().addPropertyChangeListener(evt -> {
			Object value = evt.getNewValue();
			if (value instanceof Integer) {
				updateClock((int) value);
				updateCurrentTime();
			}
		});

		setTitle(translate("List of Findings"));
		setStatusMessage(translate("List of findings successfully loaded."), false);

		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		/*
		 * Format bottom panel in org tab
		 */
		bottomOrgPanel.setBackground(UI.TABLE_ALT_COLOR);
		bottomOrgPanel.setBorder(new EmptyBorder(20, 0, 30, 0));

		GUITools.addComponent(tabPanelOrg, gbl, attPanel, 0, 0, 1, 1, 1.0, 1.0, 0, 0, 20, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelOrg, gbl, new JSeparator(), 0, 1, 1, 1, 1.0, 0.0, 0, 0, 0, 0,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(tabPanelOrg, gbl, bottomOrgPanel, 0, 2, 1, 1, 1.0, 0.0, 0, 0, 0, 0,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

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
				if (StringUtils.isBlank(protCommTxtArea.getText())) {
					currentProt.setComments("");
				}
				GUITools.executeSwingWorker(new ImageEditorWriteWorker(currentProt));
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

	@Override
	public void update(Observable o, Object arg) {
		SwingUtilities.invokeLater(() -> updateHints());
	}

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

		// Show fullscreen button in toolbar
		try {
			if (appData.getSettingValue(AppSettingKey.APP_ALLOW_FULLSCREEN) == AppSettingValue.TRUE) {
				tbFullscreen.setVisible(true);
			} else {
				tbFullscreen.setVisible(false);
			}
		} catch (DataException exc) {
			// Ignore
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

		boolean protFrameVisible = UI.getInstance().getProtocolFrame().isVisible();

		UI.getInstance().getMainFrame().setVisible(!protFrameVisible);
		Dashboard.getInstance();
	}

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
		for (Attendee att : Application.getInstance().getAttendeeMgmt().getAttendees()) {
			if (!Application.getInstance().getProtocolMgmt().isAttendee(att, currentProt)) {
				return true;
			}
		}
		return false;
	}

	private long updateTime = System.currentTimeMillis();
	private transient KeyListener updateListener = new KeyListener() {
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

	private transient KeyListener tabKeyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
			Object evSrc = e.getSource();

			if (evSrc instanceof JTextArea && e.getKeyCode() == KeyEvent.VK_TAB) {
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

	private transient ChangeListener spinnerChangeListener = e -> updateTime = System.currentTimeMillis();

	private transient ItemListener itemListener = e -> updateTime = System.currentTimeMillis();

	private transient SwingWorker<Void, Void> updateWorker = new SwingWorker<Void, Void>() {
		@Override
		protected Void doInBackground() throws Exception {
			long change = Long.parseLong(Data.getInstance().getResource("keyTypeChangeInMillis"));

			while (true) {
				try {
					Thread.sleep(change);
					long diff = System.currentTimeMillis() - updateTime;
					if (diff >= change && diff < change * 3) {
						updateResiData();
						SwingUtilities.invokeLater(() -> updateHints());
					}
				} catch (Exception e) {
				}
			}
		}
	};

	/**
	 * Update resi data.
	 */
	public void updateResiData() {

		try {
			currentProt.setDate(GUITools.dateString2Calendar(dateTxtFld.getText(), dateF));
		} catch (ParseException e) {

			currentProt.setDate(null);
		}

		currentProt.setLocation(locationTxtFld.getText());

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, Integer.parseInt(beginMSpinner.getValue().toString()));
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(beginHSpinner.getValue().toString()));
		currentProt.setStart(cal);

		Calendar calEnd = Calendar.getInstance();
		calEnd.set(Calendar.MINUTE, Integer.parseInt(endMSpinner.getValue().toString()));
		calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHSpinner.getValue().toString()));
		currentProt.setEnd(calEnd);

		revMgmt.setImpression(impTxtArea.getText());

		String comm = meetCommTxtArea.getText();
		Application.getInstance().getMeetingMgmt().setMeetingComment(comm, currentMeet);

		revMgmt.setRecommendation((String) recBx.getSelectedItem());

		protMgmt.setProtocolComment(protCommTxtArea.getText(), currentProt);

	}

	private void unmarkAllComponents() {
		dateTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		scrllP.setBorder(UI.STANDARD_BORDER);
		recBx.setBorder(UI.STANDARD_BORDER);
		impScrllPn.setBorder(UI.STANDARD_BORDER);
		protCommScrllPn.setBorder(UI.STANDARD_BORDER);

		componentMarked = false;
	}

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

	private void createHints() {
		setNumberOfHints(2);

		hintAtt = new HintItem(
				translate(
						"Please add at least one attendee to the meeting by choosing one from the attendees pool or create a new one (Tab 'Organizational')."),
				HintItem.WARNING);

		hintImpr = new HintItem(
				translate(
						"Please enter the general impression for the product into the provided text field (Tab 'Impression')."),
				HintItem.WARNING);

		hintRec = new HintItem(
				translate(
						"Please enter the final recommendation for the product into the provided text field (Tab 'Comments & Recommendation')."),
				HintItem.WARNING);

		hintFind = new HintItem(translate("For every finding enter at least a description (Tab 'Findings')."),
				HintItem.WARNING);

		hintOk = new HintItem(translate("The meeting data and its list of findings is complete."), HintItem.OK);

		hintInfoNewFinding = new HintItem(
				translate(
						"In order to add a new finding to the list of findings use the 'Add Finding' button (Tab 'Findings')."),
				HintItem.INFO);
	}

	private void updateHints() {
		if (!bodyCreated) {
			return;
		}

		boolean warningErrorHints = false;
		List<HintItem> hints = new ArrayList<>();
		unmarkAllComponents();
		if (protMgmt.getAttendees(currentProt).isEmpty()) {
			hints.add(hintAtt);
			if (tabPanelOrg.isVisible()) {
				markComponent(scrllP);
			}
			warningErrorHints = true;
			tabbedPane.setIconAt(0, ICON_TAB_WARN);
		} else {
			tabbedPane.setIconAt(0, ICON_TAB_OK);
		}
		if (StringUtils.isBlank(revMgmt.getImpression())) {
			hints.add(hintImpr);
			if (tabGenImp.isVisible() && StringUtils.isBlank(revMgmt.getImpression())) {
				markComponent(impScrllPn);
			}
			warningErrorHints = true;
			tabbedPane.setIconAt(1, ICON_TAB_WARN);
		} else {
			tabbedPane.setIconAt(1, ICON_TAB_OK);
		}

		if (!findMgmt.areAllFindingsComplete(currentProt)) {
			hints.add(hintFind);
			warningErrorHints = true;
			tabbedPane.setIconAt(2, ICON_TAB_WARN);
		} else {
			tabbedPane.setIconAt(2, ICON_TAB_OK);
		}

		if (StringUtils.isBlank(revMgmt.getRecommendation())) {
			hints.add(hintRec);
			if (tabPanelCommAndRec.isVisible() && StringUtils.isBlank(revMgmt.getRecommendation())) {
				markComponent(recBx);
			}

			warningErrorHints = true;

			tabbedPane.setIconAt(3, ICON_TAB_WARN);
		} else {
			tabbedPane.setIconAt(3, ICON_TAB_OK);
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
			editor = new ImageEditorDialog(UI.getInstance().getProtocolFrame(), image);
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
