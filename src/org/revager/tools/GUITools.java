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
package org.revager.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.JTextComponent;

import org.revager.app.model.Data;
import org.revager.gui.UI;

/**
 * The Class GUITools.
 */
public class GUITools {

	/**
	 * Map for storing rollover row indexes for the standard tables
	 */
	private static Map<Integer, Integer> rollOverRowIndex = new HashMap<Integer, Integer>();

	private static int lastRolloverKey = 0;

	/**
	 * Execute the given SwingWorker class.
	 * 
	 * @param worker
	 */
	public static void executeSwingWorker(final SwingWorker<?, ?> worker) {
		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {
		java.util.concurrent.Executors.newCachedThreadPool().execute(worker);
		// }
		// });
	}

	/**
	 * Adds the given component to the given container with GridBagLayout.
	 * 
	 * @param cont
	 *            the container
	 * @param gbl
	 *            the GridBagLayout object
	 * @param c
	 *            the component to add
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param weightx
	 *            the vertical weight
	 * @param weighty
	 *            the horizontal weight
	 * @param t
	 *            the top inset
	 * @param l
	 *            the left inset
	 * @param b
	 *            the bottom inset
	 * @param r
	 *            the right inset
	 * @param i
	 *            a GridBagConstraints constant
	 * @param z
	 *            a GridBagConstraints constant
	 */
	public static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
			double weightx, double weighty, int t, int l, int b, int r, int i, int z) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(t, l, b, r);
		gbc.fill = i;
		gbc.anchor = z;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	/**
	 * Creates a new image button.
	 * 
	 * @param icon
	 *            the normal icon
	 * @param rolloverIcon
	 *            the rollover icon
	 * @param action
	 *            the action
	 * 
	 * @return the newly created image button
	 */
	public static JButton newImageButton(ImageIcon icon, ImageIcon rolloverIcon, Action action) {
		JButton button = new JButton(action);
		button.setToolTipText(button.getText());
		button.setText(null);
		button.setContentAreaFilled(false);
		button.setBorder(new EmptyBorder(0, 0, 0, 0));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorderPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFocusPainted(false);
		button.setFocusable(false);

		button.setIcon(icon);
		button.setRolloverIcon(rolloverIcon);
		button.setRolloverSelectedIcon(rolloverIcon);
		button.setSelectedIcon(rolloverIcon);

		return button;
	}

	/**
	 * Creates a new image toggle button.
	 * 
	 * @param icon
	 *            the normal icon
	 * @param rolloverIcon
	 *            the rollover icon
	 * @param action
	 *            the action
	 * 
	 * @return the newly created image button
	 */
	public static JToggleButton newImageToggleButton(ImageIcon icon, ImageIcon rolloverIcon, Action action) {
		JToggleButton button = new JToggleButton(action);
		button.setToolTipText(button.getText());
		button.setText(null);
		button.setContentAreaFilled(false);
		button.setBorder(new EmptyBorder(0, 0, 0, 0));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorderPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFocusPainted(false);
		button.setFocusable(false);

		button.setIcon(icon);
		button.setRolloverIcon(rolloverIcon);
		button.setRolloverSelectedIcon(rolloverIcon);
		button.setSelectedIcon(rolloverIcon);

		return button;
	}

	/**
	 * Creates a new image button.
	 * 
	 * @param icon
	 *            the normal icon
	 * @param rolloverIcon
	 *            the rollover icon
	 * 
	 * @return the newly created image button
	 */
	public static JButton newImageButton(ImageIcon icon, ImageIcon rolloverIcon) {
		return newImageButton(icon, rolloverIcon, null);
	}

	/**
	 * Creates a new image button.
	 * 
	 * @return the newly created image button
	 */
	public static JButton newImageButton() {
		return newImageButton(null, null, null);
	}

	/**
	 * Creates a new image toggle button.
	 * 
	 * @return the newly created image toggle button
	 */
	public static JToggleButton newImageToggleButton() {
		JToggleButton button = new JToggleButton();

		button.setBorder(new EmptyBorder(2, 2, 2, 8));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setFocusable(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		return button;
	}

	/**
	 * Creates a new invisible strut button.
	 * 
	 * @return the newly created button
	 */
	public static JButton newInvisibleStrutButton() {
		JButton button = new JButton("  ");
		button.setContentAreaFilled(false);
		button.setBorder(null);
		button.setBorderPainted(false);
		button.setFocusable(false);
		button.setEnabled(false);

		return button;
	}

	/**
	 * Creates a new standard table.
	 * 
	 * @param model
	 *            the table model
	 * @param showHeader
	 *            true if the header of the table should be visible
	 * 
	 * @return the newly created table
	 */
	@SuppressWarnings("serial")
	public static JTable newStandardTable(TableModel model, boolean showHeader) {
		/*
		 * Prep. for rollover
		 */
		if (lastRolloverKey == Integer.MAX_VALUE) {
			lastRolloverKey = 0;
		} else {
			lastRolloverKey++;
		}

		final int keyIdx = lastRolloverKey;

		rollOverRowIndex.put(keyIdx, -1);

		/*
		 * The table
		 */
		final JTable table = new JTable(model) {
			@Override
			public void editingStopped(final ChangeEvent e) {
				int selRow = this.getSelectedRow();

				if (selRow == -1) {
					selRow = this.getRowCount() - 1;
				}

				super.editingStopped(e);
				// >> the following statement would be useful, but is still
				// problematic <<
				// this.setRowSelectionInterval(selRow, selRow);
			}

			@Override
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				boolean result = super.editCellAt(row, column, e);
				final Component editor = getEditorComponent();

				TableCellRenderer renderer = this.getColumnModel().getColumn(column).getCellRenderer();

				Font cellFont = null;

				if (renderer instanceof DefaultTableCellRenderer) {
					cellFont = ((DefaultTableCellRenderer) renderer).getFont();
				}

				if (editor != null && editor instanceof JTextComponent) {
					if (e == null) {
						((JTextComponent) editor).selectAll();
					} else {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								((JTextComponent) editor).selectAll();
							}
						});
					}

					((JTextComponent) editor).setBorder(UI.MARKED_BORDER_INLINE);

					if (cellFont != null) {
						((JTextComponent) editor).setFont(cellFont);
					}

					editor.requestFocusInWindow();
				}

				return result;
			}

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				TableCellRenderer renderer = super.getCellRenderer(row, column);

				if (renderer instanceof DefaultTableCellRenderer) {
					((DefaultTableCellRenderer) renderer).setBorder(new EmptyBorder(3, 3, 3, 3));
				}

				return renderer;
			}

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);

				/*
				 * Rollover
				 */
				comp.setBackground(getBackground());

				comp = super.prepareRenderer(renderer, row, col);

				if (!isRowSelected(row) && row == rollOverRowIndex.get(keyIdx)) {
					comp.setForeground(getForeground());
					comp.setBackground(UI.BLUE_BACKGROUND_COLOR);
				}

				/*
				 * Tooltips
				 */
				JComponent jcomp = (JComponent) comp;

				if (comp == jcomp && renderer instanceof DefaultTableCellRenderer) {
					String toolTip = ((DefaultTableCellRenderer) renderer).getToolTipText();

					if (toolTip != null && !toolTip.trim().equals("")) {
						jcomp.setToolTipText(toolTip);
					}
				}

				return comp;
			}
		};

		/*
		 * Table properties
		 */
		table.setRowHeight(UI.TABLE_ROW_HEIGHT);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		/*
		 * Rollover
		 */
		MouseInputAdapter rolloverListener = new MouseInputAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				rollOverRowIndex.put(keyIdx, -1);

				table.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());

				if (row != rollOverRowIndex.get(keyIdx)) {
					rollOverRowIndex.put(keyIdx, row);

					table.repaint();
				}
			}
		};
		table.addMouseMotionListener(rolloverListener);
		table.addMouseListener(rolloverListener);

		/*
		 * Header
		 */
		if (!showHeader) {
			table.setTableHeader(null);
		}

		return table;
	}

	/**
	 * Sets the given table into scroll a pane.
	 * 
	 * @param table
	 *            the table
	 * 
	 * @return the scroll pane
	 */
	public static JScrollPane setIntoScrollPane(JTable table) {
		JScrollPane scrollPn = new JScrollPane(table);
		scrollPn.getViewport().setBackground(Color.WHITE);

		return scrollPn;
	}

	/**
	 * Creates a new base panel for popup windows.
	 * 
	 * @return the newly created base panel
	 */
	public static JPanel newPopupBasePanel() {
		JPanel panelBase = new JPanel();
		panelBase.setLayout(new BorderLayout());
		// panelBase.setBorder(UI.POPUP_BORDER);
		panelBase.setBackground(UI.POPUP_BACKGROUND);

		return panelBase;
	}

	/**
	 * Creates a new title text area for popup windows.
	 * 
	 * @param titleText
	 *            the title text
	 * 
	 * @return the newly created text area
	 */
	public static JTextArea newPopupTitleArea(String titleText) {
		JTextArea textTitle = new JTextArea();
		textTitle.setEditable(false);
		textTitle.setText(titleText);
		textTitle.setBackground(UI.POPUP_BACKGROUND);
		textTitle.setFont(UI.STANDARD_FONT.deriveFont(Font.BOLD));
		textTitle.setLineWrap(true);
		textTitle.setWrapStyleWord(true);
		textTitle.setFocusable(false);
		textTitle.setBorder(new EmptyBorder(5, 5, 5, 5));

		return textTitle;
	}

	/**
	 * Sets the given text area into a scroll pane.
	 * 
	 * @param txt
	 *            the text area
	 * 
	 * @return the scroll pane
	 */
	public static JScrollPane setIntoScrllPn(JTextArea txt) {
		txt.setLineWrap(true);
		txt.setWrapStyleWord(true);
		JScrollPane scrllPn = new JScrollPane(txt, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		return scrllPn;
	}

	/**
	 * Gets a new message pane for JOptionPanes.
	 * 
	 * @param message
	 *            the message
	 * 
	 * @return the message pane to embed
	 */
	public static JLabel getMessagePane(String message) {
		if (message == null) {
			message = "";
		}

		if (message.endsWith("null")) {
			message = message.substring(0, message.length() - 5);
		}

		return new JLabel(getTextAsHtml(message));

		/*
		 * Deprecated implementation
		 */
		// JTextArea textArea = new JTextArea(message);
		// textArea.setLineWrap(true);
		// textArea.setWrapStyleWord(true);
		// textArea.setMargin(new Insets(4, 4, 4, 4));
		// textArea.setEditable(false);
		// textArea.setFocusable(false);
		// textArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		// textArea.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
		// JScrollPane scrollPane = new JScrollPane();
		// scrollPane.setPreferredSize(new Dimension(350, 100));
		// scrollPane.getViewport().setView(textArea);
		// scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
	}

	/**
	 * Gets the given text for a JLabel.
	 * 
	 * @param text
	 *            the text
	 * 
	 * @return the text in simple HTML for JLabel
	 */
	public static String getTextAsHtml(String text) {
		final int CHARS_PER_LINE = 60;

		int currPos = 0;
		int endPos = 0;

		while (text.length() - 1 > endPos) {
			endPos = currPos + CHARS_PER_LINE;

			if (endPos >= text.length()) {
				break;
			}

			String part = text.substring(currPos, endPos);

			if (part.lastIndexOf('\n') != -1) {
				currPos = currPos + part.lastIndexOf('\n') + 1;
			} else {
				if (part.lastIndexOf(' ') == -1) {
					currPos = endPos;
				} else {
					currPos = currPos + part.lastIndexOf(' ');
				}

				text = text.substring(0, currPos) + "\n" + text.substring(currPos + 1);

				currPos++;
			}
		}

		text = text.trim();
		text = text.replace("\n", "<br>");
		text = "<html>" + text + "</html>";

		return text;
	}

	/**
	 * Formats the given spinner.
	 * 
	 * @param sp
	 *            the spinner
	 */
	public static void formatSpinner(JSpinner sp, boolean hideBorder) {
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) sp.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("00");
		DecimalFormatSymbols geSymbols = new DecimalFormatSymbols(Data.getInstance().getLocale());
		decimalFormat.setDecimalFormatSymbols(geSymbols);

		if (hideBorder) {
			sp.setBorder(null);
		}
	}

	/**
	 * Converts the given date string to a Calendar object.
	 * 
	 * @param s
	 *            the date string
	 * @param df
	 *            the date formatter
	 * 
	 * @return the calendar object
	 * 
	 * @throws ParseException
	 *             If an error occurs while parsing the date string
	 */
	public static Calendar dateString2Calendar(String s, DateFormat df) throws ParseException {
		Calendar cal = Calendar.getInstance();
		Date d1 = df.parse(s);
		cal.setTime(d1);
		return cal;
	}

	/**
	 * Sets the location of the given window to cursor position.
	 * 
	 * @param win
	 *            the window for which the location is to be set
	 */
	public static void setLocationToCursorPos(Window win) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		double cursorPosX = MouseInfo.getPointerInfo().getLocation().getX();
		double cursorPosY = MouseInfo.getPointerInfo().getLocation().getY();

		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight() - 40;

		double winWidth = win.getSize().getWidth();
		double winHeight = win.getSize().getHeight();

		int winPosX = (int) cursorPosX;
		int winPosY = (int) cursorPosY;

		/*
		 * If the window would break the screen size
		 */
		if (cursorPosX + winWidth > screenWidth) {
			winPosX = (int) (screenWidth - winWidth);
		}

		if (cursorPosY + winHeight > screenHeight) {
			winPosY = (int) (screenHeight - winHeight);
		}

		win.setLocation(new Point(winPosX, winPosY));
	}

	/**
	 * Scrolls the given scroll pane to top.
	 * 
	 * @param scrollPane
	 */
	public static void scrollToTop(final JScrollPane scrollPane) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
			}
		});
	}

	/**
	 * Scrolls the given scroll pane to bottom.
	 * 
	 * @param scrollPane
	 */
	public static void scrollToBottom(final JScrollPane scrollPane) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			}
		});
	}
}
