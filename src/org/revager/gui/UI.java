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

	public static final Color EDIT_VIEW_BG = new Color(255, 255, 204);
	public static final Color COMPACT_VIEW_BG = new Color(229, 226, 226);
	public static final Color BLUE_BACKGROUND_COLOR = new Color(220, 231, 255);
	public static final Color SEPARATOR_COLOR = new Color(170, 170, 170);
	public static final Color POPUP_BACKGROUND = UI.BLUE_BACKGROUND_COLOR;
	public static final Color MARKED_COLOR = new Color(210, 115, 0);
	public static final Color LINK_COLOR = new Color(0, 23, 195);
	public static final Color GLASSPANE_COLOR = new Color(255, 255, 255, 160);
	public static final Color TABLE_ALT_COLOR = new Color(246, 246, 246);
	public static final Color DARK_BLUE_COLOR = new Color(37, 64, 153);

	private static final Border INLINE_BORDER = new MatteBorder(2, 2, 2, 2, Color.WHITE);
	public static final Border POPUP_BORDER = BorderFactory.createLineBorder(Color.GRAY, 2);
	public static final Border STANDARD_BORDER = BorderFactory.createLineBorder(SEPARATOR_COLOR, 1);
	public static final Border MARKED_BORDER = BorderFactory.createLineBorder(MARKED_COLOR, 1);
	public static final Border STANDARD_BORDER_INLINE = new CompoundBorder(
			BorderFactory.createLineBorder(SEPARATOR_COLOR, 1), INLINE_BORDER);
	public static final Border MARKED_BORDER_INLINE = new CompoundBorder(
			BorderFactory.createLineBorder(MARKED_COLOR, 1), INLINE_BORDER);

	public static final Font STANDARD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	public static final Font LARGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
	public static final Font VERY_LARGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
	public static final Font VERY_LARGE_FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 18);
	public static final Font HUGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 22);
	public static final Font HUGE_FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 22);
	public static final Font VERY_HUGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 25);
	public static final Font VERY_HUGE_FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 25);
	public static final Font HUGE_HUGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 28);
	public static final Font HUGE_HUGE_FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 28);
	public static final Font VERY_HUGE_HUGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 30);
	public static final Font VERY_HUGE_HUGE_FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 30);
	
	

	public static final int TABLE_ROW_HEIGHT = 22;

	private MainFrame mainFrame = null;
	private HelpBrowserFrame helpBrowserFrame = null;
	private AssistantDialog assistantDialog = null;
	private EditProductDialog editProductDialog = null;
	private ManageSeveritiesDialog manageSeveritiesDialog = null;
	private MeetingDialog meetingDialog = null;
	private AttendeeDialog attendeeDialog = null;
	private AspectsManagerFrame aspectsManagerFrame = null;
	private FindingsListFrame protocolFrame = null;
	private FindingsListFrame protocolFullscreen = null;
	private ExportCSVDialog exportCSVDialog = null;
	private ExportPDFProtocolDialog exportPDFProtocolDialog = null;
	private CreateInvitationsDialog createInvitationsDialog = null;
	private SettingsDialog settingsDialog = null;
	private CSVProfilesDialog csvProfilesDialog = null;
	private FileChooser fileChooser = null;

	private ProtocolClockWorker protocolClockWorker = null;
	private AboutDialog aboutDialog = null;
	private AutoBackupWorker autoBackupWorker = null;
	private AutoSaveWorker autoSaveWorker = null;

	private boolean protocolFrameFullscreen = false;

	public static enum Status {
		NO_FILE_LOADED, UNSAVED_CHANGES, DATA_SAVED;
	}

	public static enum Platform {
		WINDOWS, MAC, OTHER;
	}

	private Status status = Status.NO_FILE_LOADED;

	private Platform platform = Platform.OTHER;

	private PresentationFrame presentationFrame;

	public Status getStatus() {
		return status;
	}

	public synchronized void setStatus(Status status) {
		this.status = status;

		mainFrame.updateTitle();
	}

	public Platform getPlatform() {
		return platform;
	}

	private static UI instance = new UI();

	public static UI getInstance() {
		if (instance == null) {
			instance = new UI();
		}
		return instance;
	};

	public AutoBackupWorker getAutoBackupWorker() {
		if (autoBackupWorker == null) {
			autoBackupWorker = new AutoBackupWorker();
			GUITools.executeSwingWorker(autoBackupWorker);
		}
		return autoBackupWorker;
	}

	public AutoSaveWorker getAutoSaveWorker() {
		if (autoSaveWorker == null) {
			autoSaveWorker = new AutoSaveWorker();
			GUITools.executeSwingWorker(autoSaveWorker);
		}
		return autoSaveWorker;
	}

	public AboutDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(mainFrame);
		}
		return aboutDialog;
	}

	public MainFrame getMainFrame() {
		if (mainFrame == null) {
			mainFrame = new MainFrame();
		}
		return mainFrame;
	};

	public HelpBrowserFrame getHelpBrowserFrame() {
		if (helpBrowserFrame == null) {
			helpBrowserFrame = new HelpBrowserFrame();
		}
		return helpBrowserFrame;
	};

	public AssistantDialog getAssistantDialog() {
		if (assistantDialog == null) {
			assistantDialog = new AssistantDialog(UI.getInstance().getMainFrame());
		}
		return assistantDialog;
	};

	public EditProductDialog getEditProductDialog() {
		if (editProductDialog == null) {
			editProductDialog = new EditProductDialog(UI.getInstance().getMainFrame());
		}
		return editProductDialog;
	};

	public ManageSeveritiesDialog getManageSeveritiesDialog() {
		if (manageSeveritiesDialog == null) {
			manageSeveritiesDialog = new ManageSeveritiesDialog(UI.getInstance().getMainFrame());
		}
		return manageSeveritiesDialog;
	};

	public MeetingDialog getMeetingDialog() {
		if (meetingDialog == null) {
			meetingDialog = new MeetingDialog(UI.getInstance().getMainFrame());
		}
		return meetingDialog;
	};

	public AttendeeDialog getAttendeeDialog() {
		if (attendeeDialog == null) {
			attendeeDialog = new AttendeeDialog(UI.getInstance().getMainFrame());
		}
		return attendeeDialog;
	};

	public synchronized AttendeeDialog resetAttendeeDialog() {
		attendeeDialog = new AttendeeDialog(UI.getInstance().getMainFrame());

		return attendeeDialog;
	};

	public AspectsManagerFrame getAspectsManagerFrame() {
		if (aspectsManagerFrame == null) {
			aspectsManagerFrame = new AspectsManagerFrame(UI.getInstance().getMainFrame());
		}

		return aspectsManagerFrame;
	};

	public FindingsListFrame getProtocolFrame() {
		return getProtocolFrame(protocolFrameFullscreen);
	}

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

	public ExportPDFProtocolDialog getExportPDFProtocolDialog() {
		if (exportPDFProtocolDialog == null) {
			exportPDFProtocolDialog = new ExportPDFProtocolDialog(UI.getInstance().getMainFrame());
		}

		return exportPDFProtocolDialog;
	};

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

	public FileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();
		}

		return fileChooser;
	}

	public ProtocolClockWorker getProtocolClockWorker() {
		if (protocolClockWorker == null) {
			protocolClockWorker = new ProtocolClockWorker();
		}

		GUITools.executeSwingWorker(protocolClockWorker);

		return protocolClockWorker;
	}

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

	@Override
	public void update(Observable o, Object arg) {
		setStatus(Status.UNSAVED_CHANGES);
	}

}
