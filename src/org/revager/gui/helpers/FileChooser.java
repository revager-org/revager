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
package org.revager.gui.helpers;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.revager.app.ResiFileFilter;
import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.tools.GUITools;


/**
 * The Class FileChooser.
 */
public class FileChooser {

	public static final int MODE_OPEN_FILE = 1;
	public static final int MODE_SAVE_FILE = 2;
	public static final int MODE_SELECT_DIRECTORY = 3;

	public static final int SELECTED_APPROVE = 101;
	public static final int SELECTED_CANCEL = 102;
	public static final int SELECTED_ERROR = 103;

	private File dir = null;
	private File file = null;

	/**
	 * Gets the dir.
	 * 
	 * @return the dir
	 */
	public File getDir() {
		return dir;
	}

	/**
	 * Sets the dir.
	 * 
	 * @param dir
	 *            the dir to set
	 */
	public void setDir(File dir) {
		if (dir == null) {
			resetDir();
		} else {
			this.dir = dir;
		}
	}

	/**
	 * Reset dir.
	 */
	public void resetDir() {
		this.dir = new File(System.getProperty("user.home"));
	}

	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		if (file != null) {
			int index = file.getAbsolutePath().lastIndexOf("/");

			if (index < file.getAbsolutePath().lastIndexOf("\\")) {
				index = file.getAbsolutePath().lastIndexOf("\\");
			}

			File directory = new File(file.getAbsolutePath()
					.substring(0, index));

			setDir(directory);
		}

		this.file = file;
	}

	/**
	 * Clear file.
	 */
	public void clearFile() {
		this.file = null;
	}

	/**
	 * Instantiates a new file chooser.
	 */
	public FileChooser() {
		super();

		resetDir();
	}

	/**
	 * Show dialog.
	 * 
	 * @param parent
	 *            the parent
	 * @param chooserMode
	 *            the chooser mode
	 * @param resiFileFilterType
	 *            the resi file filter type
	 * 
	 * @return the int
	 */
	@SuppressWarnings("serial")
	public int showDialog(Window parent, int chooserMode, int resiFileFilterType) {
		int selected = SELECTED_APPROVE;

		final ResiFileFilter filter = new ResiFileFilter(resiFileFilterType);

		/*
		 * For the Apple Mac platform use the native file chooser (AWT
		 * FileDialog); otherwise use the Swing standard file chooser
		 * (JFileChooser).
		 */
		if (UI.getInstance().getPlatform() == UI.Platform.MAC) {
			final FileDialog dialog;

			if (parent instanceof Frame) {
				dialog = new FileDialog((Frame) parent);
			} else if (parent instanceof Dialog) {
				dialog = new FileDialog((Dialog) parent);
			} else {
				dialog = new FileDialog(UI.getInstance().getMainFrame());
			}

			dialog.setModal(true);

			dialog.setFilenameFilter(filter);

			switch (chooserMode) {
			case MODE_OPEN_FILE:
				dialog.setTitle(Data.getInstance().getLocaleStr(
						"fileChooser.titleOpen"));

				dialog.setDirectory(this.dir.getAbsolutePath());
				if (this.file != null) {
					dialog.setFile(this.file.getName());
				}

				dialog.setMode(FileDialog.LOAD);
				dialog.setVisible(true);

				if (dialog.getFile() != null) {
					setFile(new File(dialog.getDirectory() + dialog.getFile()));
					selected = SELECTED_APPROVE;
				} else {
					selected = SELECTED_CANCEL;
				}
				break;

			case MODE_SAVE_FILE:
				dialog.setTitle(Data.getInstance().getLocaleStr(
						"fileChooser.titleSave"));

				dialog.setDirectory(this.dir.getAbsolutePath());
				if (this.file != null) {
					dialog.setFile(this.file.getName());
				}

				dialog.setMode(FileDialog.SAVE);
				dialog.setVisible(true);

				if (dialog.getFile() != null) {
					setFile(new File(dialog.getDirectory() + dialog.getFile()));
					selected = SELECTED_APPROVE;
				} else {
					selected = SELECTED_CANCEL;
				}
				break;

			case MODE_SELECT_DIRECTORY:
				dialog.setTitle(Data.getInstance().getLocaleStr(
						"fileChooser.titleSelectDir"));

				dialog.setFilenameFilter(new ResiFileFilter(
						ResiFileFilter.TYPE_DIRECTORY));

				dialog.setMode(FileDialog.LOAD);

				System
						.setProperty("apple.awt.fileDialogForDirectories",
								"true");
				dialog.setVisible(true);
				System.setProperty("apple.awt.fileDialogForDirectories",
						"false");

				if (dialog.getDirectory() != null) {
					setFile(null);
					setDir(new File(dialog.getDirectory() + dialog.getFile()));

					selected = SELECTED_APPROVE;
				} else {
					selected = SELECTED_CANCEL;
				}
				break;

			default:
				break;
			}
		} else {
			JFileChooser dialog = new JFileChooser() {
				@Override
				public void approveSelection() {
					File f = getSelectedFile();

					/*
					 * Show error if selected file does not exist
					 */
					if (getDialogType() == OPEN_DIALOG) {
						if (!f.exists()) {
							String errMsg = Data.getInstance().getLocaleStr(
									"fileChooser.fileNotFound");

							JOptionPane.showMessageDialog(
									getTopLevelAncestor(), GUITools
											.getMessagePane(errMsg), Data
											.getInstance()
											.getLocaleStr("error"),
									JOptionPane.ERROR_MESSAGE);

							return;
						}
					}

					/*
					 * Show error if file name is invalid
					 */
					if (getDialogType() == SAVE_DIALOG) {
						/*
						 * Show warning if file exists
						 */
						if (f.exists()) {
							String questMsg = Data.getInstance().getLocaleStr(
									"fileChooser.warningFileExists")
									+ "\n\n"
									+ this.getSelectedFile().getAbsolutePath();

							int result = JOptionPane.showConfirmDialog(
									getTopLevelAncestor(), GUITools
											.getMessagePane(questMsg), Data
											.getInstance().getLocaleStr(
													"question"),
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
							switch (result) {
							case JOptionPane.YES_OPTION:
								super.approveSelection();
								return;
							case JOptionPane.NO_OPTION:
								return;
							}
						}
					}

					super.approveSelection();
				}
			};

			dialog.setMultiSelectionEnabled(false);

			dialog.setCurrentDirectory(this.dir);
			dialog.setSelectedFile(this.file);

			/*
			 * Remove standard file filters
			 */
			FileFilter[] filters = dialog.getChoosableFileFilters();
			for (int i = 0; i < filters.length; i++) {
				dialog.removeChoosableFileFilter(filters[i]);
			}

			switch (chooserMode) {
			case MODE_OPEN_FILE:
				dialog.setDialogTitle(Data.getInstance().getLocaleStr(
						"fileChooser.titleOpen"));

				dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

				/*
				 * Add choosable file filter to show all files
				 */
				dialog.addChoosableFileFilter(new ResiFileFilter(
						ResiFileFilter.TYPE_ALL));

				if (resiFileFilterType != ResiFileFilter.TYPE_ALL) {
					dialog.setFileFilter(filter);
				}

				selected = dialog.showOpenDialog(parent);
				break;

			case MODE_SAVE_FILE:
				dialog.setDialogTitle(Data.getInstance().getLocaleStr(
						"fileChooser.titleSave"));

				dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
				dialog.setFileFilter(filter);

				selected = dialog.showSaveDialog(parent);
				break;

			case MODE_SELECT_DIRECTORY:
				dialog.setDialogTitle(Data.getInstance().getLocaleStr(
						"fileChooser.titleSelectDir"));
				dialog.setApproveButtonText(Data.getInstance().getLocaleStr(
						"select"));

				dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				dialog.setFileFilter(new ResiFileFilter(
						ResiFileFilter.TYPE_DIRECTORY));

				selected = dialog.showOpenDialog(parent);
				break;

			default:
				break;
			}

			if (selected == JFileChooser.APPROVE_OPTION) {
				if (chooserMode == MODE_SELECT_DIRECTORY) {
					setFile(null);
					setDir(dialog.getSelectedFile());
				} else {
					setFile(dialog.getSelectedFile());
					setDir(dialog.getCurrentDirectory());
				}

				selected = SELECTED_APPROVE;
			} else if (selected == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(parent, GUITools
						.getMessagePane(Data.getInstance().getLocaleStr(
								"fileChooser.error")), Data.getInstance()
						.getResource("appName"), JOptionPane.ERROR_MESSAGE);

				selected = SELECTED_ERROR;
			} else {
				selected = SELECTED_CANCEL;
			}
		}

		return selected;
	}

}
