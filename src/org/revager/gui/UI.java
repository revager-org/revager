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

import static org.revager.app.model.Data.translate;

import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.FontUIResource;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.gui.aspects_manager.AspectsManagerFrame;
import org.revager.gui.dialogs.AboutDialog;
import org.revager.gui.dialogs.AttendeeDialog;
import org.revager.gui.dialogs.CSVProfilesDialog;
import org.revager.gui.dialogs.CreateInvitationsDialog;
import org.revager.gui.dialogs.EditProductDialog;
import org.revager.gui.dialogs.ExportCSVDialog;
import org.revager.gui.dialogs.ExportPDFProtocolDialog;
import org.revager.gui.dialogs.ManageSeveritiesDialog;
import org.revager.gui.dialogs.MeetingDialog;
import org.revager.gui.dialogs.SettingsDialog;
import org.revager.gui.dialogs.assistant.AssistantDialog;
import org.revager.gui.findings_list.FindingsListFrame;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.presentationView.PresentationFrame;
import org.revager.gui.workers.AutoBackupWorker;
import org.revager.gui.workers.AutoSaveWorker;
import org.revager.gui.workers.CheckForNewVersionWorker;
import org.revager.gui.workers.ProtocolClockWorker;
import org.revager.gui.workers.RestoreReviewWorker;
import org.revager.tools.GUITools;

/**
 * This class is the interface of the graphical user interface.
 */
public class UI implements Observer {

	/*
	 * Some static constants which are used in the UI
	 */

	/**
	 * The Constant BLUE_BACKGROUND_COLOR.
	 */
	public static final Color BLUE_BACKGROUND_COLOR = new Color(220, 231, 255);

	/**
	 * The Constant SEPARATOR_COLOR.
	 */
	public static final Color SEPARATOR_COLOR = new Color(170, 170, 170);

	/**
	 * The Constant POPUP_BACKGROUND.
	 */
	public static final Color POPUP_BACKGROUND = UI.BLUE_BACKGROUND_COLOR;

	/**
	 * The Constant MARKED_COLOR.
	 */
	public static final Color MARKED_COLOR = new Color(210, 115, 0);

	/**
	 * The Constant LINK_COLOR.
	 */
	public static final Color LINK_COLOR = new Color(0, 23, 195);

	/**
	 * The Constant GLASSPANE_COLOR.
	 */
	public static final Color GLASSPANE_COLOR = new Color(255, 255, 255, 160);

	/**
	 * The Constant TABLE_ALT_COLOR.
	 */
	public static final Color TABLE_ALT_COLOR = new Color(246, 246, 246);

	/**
	 * The Constant TABLE_ALT_COLOR.
	 */
	public static final Color DARK_BLUE_COLOR = new Color(37, 64, 153);

	/**
	 * The Constant INLINE_BORDER.
	 */
	private static final Border INLINE_BORDER = new MatteBorder(2, 2, 2, 2, Color.WHITE);

	/**
	 * The Constant POPUP_BORDER.
	 */
	public static final Border POPUP_BORDER = BorderFactory.createLineBorder(Color.GRAY, 2);

	/**
	 * The Constant STANDARD_BORDER.
	 */
	public static final Border STANDARD_BORDER = BorderFactory.createLineBorder(SEPARATOR_COLOR, 1);

	/**
	 * The Constant MARKED_BORDER.
	 */
	public static final Border MARKED_BORDER = BorderFactory.createLineBorder(MARKED_COLOR, 1);

	/**
	 * The Constant STANDARD_BORDER_INLINE.
	 */
	public static final Border STANDARD_BORDER_INLINE = new CompoundBorder(
			BorderFactory.createLineBorder(SEPARATOR_COLOR, 1), INLINE_BORDER);

	/**
	 * The Constant MARKED_BORDER_INLINE.
	 */
	public static final Border MARKED_BORDER_INLINE = new CompoundBorder(
			BorderFactory.createLineBorder(MARKED_COLOR, 1), INLINE_BORDER);

	/**
	 * The Constant STANDARD_FONT.
	 */
	public static final Font STANDARD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	/**
	 * The Constant LARGE_FONT.
	 */
	public static final Font LARGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

	/**
	 * The Constant PROTOCOL_FONT.
	 */
	public static final Font PROTOCOL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);

	/**
	 * The Constant PROTOCOL_FONT_BOLD.
	 */
	public static final Font PROTOCOL_FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 18);

	/**
	 * The Constant PROTOCOL_TITLE_FONT.
	 */
	public static final Font PROTOCOL_TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);

	/**
	 * The Constant TABLE_ROW_HEIGHT.
	 */
	public static final int TABLE_ROW_HEIGHT = 22;

	/**
	 * The main frame.
	 */
	private MainFrame mainFrame = null;
	/**
	 * The help browser frame.
	 */
	private HelpBrowserFrame helpBrowserFrame = null;

	/**
	 * The assistant dialog.
	 */
	private AssistantDialog assistantDialog = null;

	/**
	 * The edit product dialog.
	 */
	private EditProductDialog editProductDialog = null;

	/**
	 * The manage severities dialog.
	 */
	private ManageSeveritiesDialog manageSeveritiesDialog = null;

	/**
	 * The meeting dialog.
	 */
	private MeetingDialog meetingDialog = null;

	/**
	 * The attendee dialog.
	 */
	private AttendeeDialog attendeeDialog = null;

	/**
	 * The aspects manager frame.
	 */
	private AspectsManagerFrame aspectsManagerFrame = null;

	/**
	 * The protocol frame.
	 */
	private FindingsListFrame protocolFrame = null;

	/**
	 * The protocol fullscreen.
	 */
	private FindingsListFrame protocolFullscreen = null;

	/**
	 * The export csv dialog.
	 */
	private ExportCSVDialog exportCSVDialog = null;

	/**
	 * The export pdf protocol dialog.
	 */
	private ExportPDFProtocolDialog exportPDFProtocolDialog = null;

	/**
	 * The create invitations dialog.
	 */
	private CreateInvitationsDialog createInvitationsDialog = null;

	/**
	 * The settings dialog.
	 */
	private SettingsDialog settingsDialog = null;

	/**
	 * The csv profiles dialog.
	 */
	private CSVProfilesDialog csvProfilesDialog = null;

	/**
	 * The file chooser.
	 */
	private FileChooser fileChooser = null;

	/**
	 * The protocol clock worker.
	 */
	private ProtocolClockWorker protocolClockWorker = null;

	/**
	 * The about dialog.
	 */
	private AboutDialog aboutDialog = null;

	/**
	 * The worker for backup.
	 */
	private AutoBackupWorker autoBackupWorker = null;

	/**
	 * The worker for auto saving the current review.
	 */
	private AutoSaveWorker autoSaveWorker = null;

	/**
	 * The protocol frame fullscreen.
	 */
	private boolean protocolFrameFullscreen = false;

	/**
	 * The Enum Status.
	 */
	public static enum Status {
		NO_FILE_LOADED, UNSAVED_CHANGES, DATA_SAVED;
	}

	/**
	 * The Enum Platform.
	 */
	public static enum Platform {
		WINDOWS, MAC, OTHER;
	}

	/**
	 * The status.
	 */
	private Status status = Status.NO_FILE_LOADED;

	/**
	 * The platform.
	 */
	private Platform platform = Platform.OTHER;

	private PresentationFrame presentationFrame;

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public synchronized void setStatus(Status status) {
		this.status = status;

		mainFrame.updateTitle();
	}

	/**
	 * Gets the platform.
	 * 
	 * @return the platform
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * The instance.
	 */
	private static UI instance = new UI();

	/**
	 * Gets the single instance of UI.
	 * 
	 * @return single instance of UI
	 */
	public static UI getInstance() {
		if (instance == null) {
			instance = new UI();
		}

		return instance;
	};

	/**
	 * Returns the auto backup worker.
	 * 
	 * @return the auto backup worker
	 */
	public AutoBackupWorker getAutoBackupWorker() {
		if (autoBackupWorker == null) {
			autoBackupWorker = new AutoBackupWorker();
			GUITools.executeSwingWorker(autoBackupWorker);
		}

		return autoBackupWorker;
	}

	/**
	 * Returns the auto save worker.
	 * 
	 * @return the auto save worker
	 */
	public AutoSaveWorker getAutoSaveWorker() {
		if (autoSaveWorker == null) {
			autoSaveWorker = new AutoSaveWorker();
			GUITools.executeSwingWorker(autoSaveWorker);
		}

		return autoSaveWorker;
	}

	/**
	 * Gets the about dialog.
	 * 
	 * @return the about dialog
	 */
	public AboutDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(mainFrame);
		}

		return aboutDialog;
	}

	/**
	 * Gets the main frame.
	 * 
	 * @return the main frame
	 */
	public MainFrame getMainFrame() {
		if (mainFrame == null) {
			mainFrame = new MainFrame();
		}

		return mainFrame;
	};

	/**
	 * Gets the help browser frame.
	 * 
	 * @return the help browser frame
	 */
	public HelpBrowserFrame getHelpBrowserFrame() {
		if (helpBrowserFrame == null) {
			helpBrowserFrame = new HelpBrowserFrame();
		}

		return helpBrowserFrame;
	};

	/**
	 * Gets the assistant dialog.
	 * 
	 * @return the assistant dialog
	 */
	public AssistantDialog getAssistantDialog() {
		if (assistantDialog == null) {
			assistantDialog = new AssistantDialog(UI.getInstance().getMainFrame());
		}

		return assistantDialog;
	};

	/**
	 * Gets the edits the product dialog.
	 * 
	 * @return the edits the product dialog
	 */
	public EditProductDialog getEditProductDialog() {
		if (editProductDialog == null) {
			editProductDialog = new EditProductDialog(UI.getInstance().getMainFrame());
		}

		return editProductDialog;
	};

	/**
	 * Gets the manage severities dialog.
	 * 
	 * @return the manage severities dialog
	 */
	public ManageSeveritiesDialog getManageSeveritiesDialog() {
		if (manageSeveritiesDialog == null) {
			manageSeveritiesDialog = new ManageSeveritiesDialog(UI.getInstance().getMainFrame());
		}

		return manageSeveritiesDialog;
	};

	/**
	 * Gets the meeting dialog.
	 * 
	 * @return the meeting dialog
	 */
	public MeetingDialog getMeetingDialog() {
		if (meetingDialog == null) {
			meetingDialog = new MeetingDialog(UI.getInstance().getMainFrame());
		}

		return meetingDialog;
	};

	/**
	 * Gets the attendee dialog.
	 * 
	 * @return the attendee dialog
	 */
	public AttendeeDialog getAttendeeDialog() {
		if (attendeeDialog == null) {
			attendeeDialog = new AttendeeDialog(UI.getInstance().getMainFrame());
		}

		return attendeeDialog;
	};

	/**
	 * Resets the attendee dialog.
	 * 
	 * @return the new attendee dialog
	 */
	public synchronized AttendeeDialog resetAttendeeDialog() {
		attendeeDialog = new AttendeeDialog(UI.getInstance().getMainFrame());

		return attendeeDialog;
	};

	/**
	 * Gets the aspects manager frame.
	 * 
	 * @return the aspects manager frame
	 */
	public AspectsManagerFrame getAspectsManagerFrame() {
		if (aspectsManagerFrame == null) {
			aspectsManagerFrame = new AspectsManagerFrame(UI.getInstance().getMainFrame());
		}

		return aspectsManagerFrame;
	};

	/**
	 * Gets the protocol frame.
	 * 
	 * @return the protocol frame
	 */
	public FindingsListFrame getProtocolFrame() {
		return getProtocolFrame(protocolFrameFullscreen);
	}

	/**
	 * Gets the protocol frame.
	 * 
	 * @param fullscreen
	 *            the fullscreen
	 * 
	 * @return the protocol frame
	 */
	public FindingsListFrame getProtocolFrame(boolean fullscreen) {
		FindingsListFrame returnFrame = null;

		if (fullscreen) {
			if (protocolFullscreen == null) {
				protocolFullscreen = new FindingsListFrame(true);
			}

			if (protocolFrame != null && fullscreen != protocolFrameFullscreen) {
				protocolFullscreen.setMeeting(protocolFrame.getMeeting());
				protocolFrame.setVisible(false);
			}

			returnFrame = protocolFullscreen;
		} else {
			if (protocolFrame == null) {
				protocolFrame = new FindingsListFrame(false);
			}

			if (protocolFullscreen != null && fullscreen != protocolFrameFullscreen) {
				protocolFrame.setMeeting(protocolFullscreen.getMeeting());
				protocolFullscreen.setVisible(false);
			}

			returnFrame = protocolFrame;
		}

		protocolFrameFullscreen = fullscreen;

		return returnFrame;
	};

	/**
	 * Gets the export csv dialog.
	 * 
	 * @return the export csv dialog
	 */
	public ExportCSVDialog getExportCSVDialog() {
		if (exportCSVDialog == null) {
			exportCSVDialog = new ExportCSVDialog(UI.getInstance().getMainFrame());
		}

		return exportCSVDialog;
	};
	
	public PresentationFrame getPresentationFrame() {
		if (presentationFrame == null) {
			presentationFrame = new PresentationFrame();
		}
		return presentationFrame;
	}

	/**
	 * Gets the export pdf protocol dialog.
	 * 
	 * @return the export pdf protocol dialog
	 */
	public ExportPDFProtocolDialog getExportPDFProtocolDialog() {
		if (exportPDFProtocolDialog == null) {
			exportPDFProtocolDialog = new ExportPDFProtocolDialog(UI.getInstance().getMainFrame());
		}

		return exportPDFProtocolDialog;
	};

	/**
	 * Gets the creates the invitations dialog.
	 * 
	 * @return the creates the invitations dialog
	 */
	public CreateInvitationsDialog getCreateInvitationsDialog() {
		if (createInvitationsDialog == null) {
			createInvitationsDialog = new CreateInvitationsDialog(UI.getInstance().getMainFrame());
		}

		return createInvitationsDialog;
	};

	/**
	 * Gets the settings dialog.
	 * 
	 * @return the settings dialog
	 */
	public SettingsDialog getSettingsDialog() {
		if (settingsDialog == null) {
			settingsDialog = new SettingsDialog(UI.getInstance().getMainFrame());
		}

		return settingsDialog;
	};

	/**
	 * Gets the cSV profiles dialog.
	 * 
	 * @return the cSV profiles dialog
	 */
	public CSVProfilesDialog getCSVProfilesDialog() {
		if (csvProfilesDialog == null) {
			csvProfilesDialog = new CSVProfilesDialog(UI.getInstance().getMainFrame());
		}

		return csvProfilesDialog;
	}

	/**
	 * Gets the file chooser.
	 * 
	 * @return the file chooser
	 */
	public FileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();
		}

		return fileChooser;
	}

	/**
	 * Gets the protocol clock worker.
	 * 
	 * @return the protocol clock worker
	 */
	public ProtocolClockWorker getProtocolClockWorker() {
		if (protocolClockWorker == null) {
			protocolClockWorker = new ProtocolClockWorker();
		}

		GUITools.executeSwingWorker(protocolClockWorker);

		return protocolClockWorker;
	}

	/**
	 * Instantiates a new uI.
	 */
	private UI() {
		super();

		/*
		 * Add this class as observer to the data model
		 */
		Data.getInstance().getResiData().addObserver(this);

		/*
		 * Determine on which platform the application is running
		 */
		String osName = System.getProperty("os.name").toLowerCase();

		if (osName.contains("mac")) {
			platform = Platform.MAC;
		} else if (osName.contains("windows")) {
			platform = Platform.WINDOWS;
		} else {
			platform = Platform.OTHER;
		}

		/*
		 * Set some properties for Mac OS X
		 */
		if (platform == Platform.MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");

			System.setProperty("com.apple.mrj.application.apple.menu.about.name",
					Data.getInstance().getResource("appName"));

			/*
			 * Register a hook to save the window position when quit via the app
			 * menu.
			 */
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					String path = Data.getInstance().getResiData().getReviewPath();

					try {
						if (path != null) {
							Application.getInstance().getApplicationCtl().storeReview(path);

							Application.getInstance().getApplicationCtl().clearReview();
						} else {
							Application.getInstance().getApplicationCtl().backupReview();
						}
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			};

			Runtime.getRuntime().addShutdownHook(new Thread(runner, "Window Prefs Hook"));
		}

		/*
		 * Set the GUI look-and-feel for the current platform
		 */
		try {
			if (platform == Platform.MAC || platform == Platform.WINDOWS) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		/*
		 * Set some standard values of the UI
		 */
		UIManager.put("TextArea.border", INLINE_BORDER);
		UIManager.put("TextField.border", STANDARD_BORDER_INLINE);
		UIManager.put("ScrollPane.border", STANDARD_BORDER);
		UIManager.put("ScrollBar.border", null);
		UIManager.put("Table.alternateRowColor", TABLE_ALT_COLOR);
		UIManager.put("Table.gridColor", SEPARATOR_COLOR);
		UIManager.put("Table.cellNoFocusBorder", new EmptyBorder(3, 3, 3, 3));
		UIManager.put("Table.focusSelectedCellHighlightBorder", new EmptyBorder(3, 3, 3, 3));
		UIManager.put("Separator.foreground", SEPARATOR_COLOR);
		UIManager.put("TextArea.font", new FontUIResource(STANDARD_FONT));
		UIManager.put("TextField.font", new FontUIResource(STANDARD_FONT));

		if (platform != Platform.MAC) {
			UIManager.put("Spinner.border", STANDARD_BORDER_INLINE);
		}
		/*
		 * UIManager.put("TextField.border", STANDARD_BORDER);
		 * UIManager.put("TextField.margin", new Insets(3, 3, 3, 3));
		 * UIManager.put("ScrollPane.border", STANDARD_BORDER);
		 * UIManager.put("ScrollPane.background", Color.WHITE);
		 * UIManager.put("Table.background", Color.WHITE);
		 * UIManager.put("Table.opaque", true);
		 */
	}

	/**
	 * This method starts the graphical user interface.
	 */
	public synchronized void run() {
		/*
		 * Initialize the frames
		 */
		// getHelpBrowserFrame();
		getAspectsManagerFrame();
		getCSVProfilesDialog();
		getFileChooser();

		/*
		 * If a review backup is present, try to load it; otherwise show the
		 * assistant dialog
		 */
		if (Application.getInstance().getApplicationCtl().isReviewRestorable()) {
			getMainFrame().setVisible(true);

			// getMainFrame().setEnabled(false);

			Object[] options = { translate("Restore"), translate("Discard") };

			if (JOptionPane.showOptionDialog(mainFrame,
					GUITools.getMessagePane(
							translate("There is a review which hasn't been stored properly. Would you like to restore and load the review?")),
					translate("Question"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
					options[0]) == JOptionPane.YES_OPTION) {
				// getMainFrame().setEnabled(true);

				GUITools.executeSwingWorker(new RestoreReviewWorker());
			} else {
				Application.getInstance().getApplicationCtl().clearReview();

				setStatus(Status.NO_FILE_LOADED);

				getAssistantDialog().setVisible(true);
			}
		} else {
			getAssistantDialog().setVisible(true);
		}

		/*
		 * Check for new version of RevAger
		 */
		GUITools.executeSwingWorker(new CheckForNewVersionWorker());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		setStatus(Status.UNSAVED_CHANGES);
	}

}