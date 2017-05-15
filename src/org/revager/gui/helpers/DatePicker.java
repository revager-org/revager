/*
 * This class is inspired by the Java Swing Date Picker (version 0.99)
 * developed by javadao aka Mark. The Java Swing Date Picker is licensed
 * under the Academic Free License (AFL).
 * 
 * URL: https://sourceforge.net/projects/datepicker/
 */
package org.revager.gui.helpers;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

/**
 * This class is inspirated by.
 */
public class DatePicker extends Observable implements Runnable, WindowFocusListener {

	/**
	 * Internally used font with plain style
	 */
	protected static final Font FONT_PLAIN = new Font(Font.DIALOG, Font.PLAIN, 12);

	/**
	 * Internally used font with bold style
	 */
	protected static final Font FONT_BOLD = new Font(Font.DIALOG, Font.BOLD, 12);

	/**
	 * This sub-class represents a label for a single day.
	 */
	@SuppressWarnings("serial")
	public static class DayLabel extends JLabel implements MouseInputListener, MouseMotionListener {
		private DatePicker parent;

		private Border oldBorder;

		/**
		 * Instantiates a new day label.
		 * 
		 * @param parent
		 *            the parent
		 * @param day
		 *            the day
		 */
		public DayLabel(DatePicker parent, int day) {
			super(Integer.toString(day));

			this.parent = parent;

			setHorizontalAlignment(SwingConstants.CENTER);
			setFont(FONT_PLAIN);

			this.addMouseListener(this);
		}

		/**
		 * Sets the current day style.
		 */
		public void setCurrentDayStyle() {
			setFont(FONT_BOLD);
			setForeground(new Color(0, 148, 28)); // dark green
			setBorder(null);
		}

		/**
		 * Sets the selected day style.
		 */
		public void setSelectedDayStyle() {
			setFont(FONT_BOLD);
			setForeground(UI.LINK_COLOR);
			setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		}

		/**
		 * Sets the weekend style.
		 */
		public void setWeekendStyle() {
			setFont(FONT_PLAIN);
			setForeground(Color.GRAY);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			parent.dayPicked(Integer.parseInt(getText()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			oldBorder = this.getBorder();
			Border b = BorderFactory.createLineBorder(Color.GRAY);
			this.setBorder(b);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			this.setBorder(oldBorder);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
		 * MouseEvent )
		 */
		@Override
		public void mouseDragged(MouseEvent e) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.
		 * MouseEvent )
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}

	/**
	 * This sub-class represents the field of day labels for the currently
	 * displayed month.
	 */
	@SuppressWarnings("serial")
	public static class MonthPanel extends JPanel {
		private DatePicker parent;

		/**
		 * Instantiates a new month panel.
		 * 
		 * @param parent
		 *            the parent
		 * @param c
		 *            the c
		 */
		public MonthPanel(DatePicker parent, Calendar c) {
			this.parent = parent;
			GridLayout g = new GridLayout(0, 7);
			this.setLayout(g);

			for (int i = 0; i < 7; i++) {
				JLabel wd = new JLabel(parent.getString("week." + i));
				wd.setHorizontalAlignment(SwingConstants.CENTER);

				if (i == 5 || i == 6) {
					wd.setForeground(Color.GRAY);
				}

				this.add(wd);
			}

			setDaysOfMonth(c);

			this.setPreferredSize(new Dimension(245, 140));

			this.setBackground(Color.WHITE);
		}

		/**
		 * Sets the days of month.
		 * 
		 * @param c
		 *            the new days of month
		 */
		private void setDaysOfMonth(Calendar c) {
			Calendar curr = new GregorianCalendar();

			int currdate = curr.get(Calendar.DAY_OF_MONTH);
			int currmon = curr.get(Calendar.MONTH);
			int curryear = curr.get(Calendar.YEAR);

			int seldate = -1;
			int selmon = -1;
			int selyear = -1;

			if (parent.selectedDate != null) {
				seldate = parent.selectedDate.get(Calendar.DAY_OF_MONTH);
				selmon = parent.selectedDate.get(Calendar.MONTH);
				selyear = parent.selectedDate.get(Calendar.YEAR);
			}

			int date = c.get(Calendar.DAY_OF_MONTH);
			int mon = c.get(Calendar.MONTH);
			int year = c.get(Calendar.YEAR);
			int day = c.get(Calendar.DAY_OF_WEEK);
			int start = (6 - (date - day) % 7) % 7;
			int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);

			for (int i = 0; i < start; i++) {
				JLabel lbl = new JLabel();
				add(lbl);
			}

			int pos = start;

			for (int i = 1; i <= days; i++) {
				pos++;

				DayLabel lbl = new DayLabel(parent, i);

				if (pos % 7 == 6 || pos % 7 == 0) {
					lbl.setWeekendStyle();
				}

				if (currdate == i && currmon == mon && curryear == year) {
					lbl.setCurrentDayStyle();
				}

				if (seldate == i && selmon == mon && selyear == year) {
					lbl.setSelectedDayStyle();
				}

				add(lbl);
			}

			for (int i = pos; i <= 36; i++) {
				add(new JLabel());
			}
		}
	}

	/**
	 * This sub-class represents the navigation to navigate in the Date Picker
	 * throug the months and years.
	 */
	@SuppressWarnings("serial")
	public static class NavigatePanel extends JPanel implements ActionListener {
		private DatePicker parent;

		private JButton premon;

		private JButton preyear;

		private JButton nextmon;

		private JButton nextyear;

		private JLabel lbl;

		/**
		 * Instantiates a new navigate panel.
		 * 
		 * @param parent
		 *            the parent
		 */
		public NavigatePanel(DatePicker parent) {
			this.parent = parent;

			setLayout(new BorderLayout());
			setBackground(UI.POPUP_BACKGROUND);

			Dimension d = new Dimension(20, 20);
			Box box = new Box(BoxLayout.X_AXIS);

			ImageIcon icon = Data.getInstance().getIcon("datePrevYear_16x16_0.png");

			preyear = GUITools.newImageButton();
			preyear.setToolTipText(parent.getString("prevYear"));
			preyear.setIcon(icon);
			preyear.setRolloverIcon(Data.getInstance().getIcon("datePrevYear_16x16.png"));
			preyear.addActionListener(this);
			preyear.setPreferredSize(d);
			preyear.setBorder(null);
			preyear.setContentAreaFilled(false);

			box.add(preyear);

			box.add(Box.createHorizontalStrut(3));

			icon = Data.getInstance().getIcon("datePrevMonth_16x16_0.png");

			premon = GUITools.newImageButton();
			premon.setToolTipText(parent.getString("prevMonth"));
			premon.setIcon(icon);
			premon.setRolloverIcon(Data.getInstance().getIcon("datePrevMonth_16x16.png"));
			premon.addActionListener(this);
			premon.setPreferredSize(d);
			premon.setBorder(null);
			premon.setContentAreaFilled(false);

			box.add(premon);

			add(box, BorderLayout.WEST);

			box = new Box(BoxLayout.X_AXIS);

			icon = Data.getInstance().getIcon("dateNextMonth_16x16_0.png");

			nextmon = GUITools.newImageButton();
			nextmon.setToolTipText(parent.getString("nextMonth"));
			nextmon.setIcon(icon);
			nextmon.setRolloverIcon(Data.getInstance().getIcon("dateNextMonth_16x16.png"));
			nextmon.setPreferredSize(d);
			nextmon.addActionListener(this);
			nextmon.setBorder(null);
			nextmon.setContentAreaFilled(false);

			box.add(nextmon);

			box.add(Box.createHorizontalStrut(3));

			icon = Data.getInstance().getIcon("dateNextYear_16x16_0.png");

			nextyear = GUITools.newImageButton();
			nextyear.setToolTipText(parent.getString("nextYear"));
			nextyear.setIcon(icon);
			nextyear.setRolloverIcon(Data.getInstance().getIcon("dateNextYear_16x16.png"));
			nextyear.setPreferredSize(d);
			nextyear.addActionListener(this);
			nextyear.setBorder(null);
			nextyear.setContentAreaFilled(false);

			box.add(nextyear);

			add(box, BorderLayout.EAST);

			setCurrentMonth(parent.calendar);
		}

		private JComboBox monthBox;

		private JComboBox yearBox;

		private String[] months;

		private Integer[] years;

		private Box box;

		/**
		 * Sets the current month.
		 * 
		 * @param c
		 *            the new current month
		 */
		public void setCurrentMonth(Calendar c) {
			setMonthComboBox(c);
			setYearComboBox(c);

			if (box == null) {
				box = new Box(BoxLayout.X_AXIS);
				box.add(monthBox);
				box.add(yearBox);
				add(box, BorderLayout.CENTER);
			}

		}

		/**
		 * Sets the month combo box.
		 * 
		 * @param c
		 *            the new month combo box
		 */
		private void setMonthComboBox(Calendar c) {
			if (months == null) {
				months = new String[12];
				for (int i = 0; i < 12; i++) {
					String m = parent.getString("month." + i);
					months[i] = m;
				}
			}

			if (monthBox == null) {
				monthBox = new JComboBox();
				monthBox.addActionListener(this);
				monthBox.setFont(DatePicker.FONT_PLAIN);
			}

			monthBox.setModel(new DefaultComboBoxModel(months));
			monthBox.setSelectedIndex(c.get(Calendar.MONTH));
		}

		/**
		 * Sets the year combo box.
		 * 
		 * @param c
		 *            the new year combo box
		 */
		private void setYearComboBox(Calendar c) {
			int y = c.get(Calendar.YEAR);

			years = new Integer[11];

			for (int i = y - 5, j = 0; i <= y + 5; i++, j++) {
				years[j] = new Integer(i);
			}

			if (yearBox == null) {
				yearBox = new JComboBox();
				yearBox.addActionListener(this);
				yearBox.setFont(DatePicker.FONT_PLAIN);
			}

			yearBox.setModel(new DefaultComboBoxModel(years));
			yearBox.setSelectedItem(years[5]);
		}

		/**
		 * Sets the label.
		 * 
		 * @param c
		 *            the new label
		 */
		public void setLabel(Calendar c) {
			if (lbl != null) {
				remove(lbl);
			}

			lbl = new JLabel(parent.getString("month." + c.get(Calendar.MONTH)) + ", " + c.get(Calendar.YEAR));

			lbl.setHorizontalAlignment(SwingConstants.CENTER);

			lbl.setFocusable(false);

			add(lbl, BorderLayout.CENTER);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			Calendar c = new GregorianCalendar();
			c.setTime(parent.getCalendar().getTime());

			if (src instanceof JButton) {
				if (e.getSource() == premon) {
					c.add(Calendar.MONTH, -1);
				} else if (e.getSource() == nextmon) {
					c.add(Calendar.MONTH, 1);
				} else if (e.getSource() == nextyear) {
					c.add(Calendar.YEAR, 1);
				}

				if (e.getSource() == preyear) {
					c.add(Calendar.YEAR, -1);
				}

				parent.updateScreen(c);
			} else if (src instanceof JComboBox) {
				JComboBox jcb = (JComboBox) src;

				if (src == monthBox) {
					c.set(Calendar.MONTH, jcb.getSelectedIndex());
				} else if (e.getSource() == yearBox) {
					c.set(Calendar.YEAR, years[jcb.getSelectedIndex()].intValue());
					setYearComboBox(c);
				}

				parent.setMonthPanel(c);
				parent.screen.pack();
			}
		}

	}

	/**
	 * This sub-class handles the close action.
	 */
	@SuppressWarnings("serial")
	public class DatePickerCloseAction extends AbstractAction {

		private Window datePicker = null;

		/**
		 * Instantiates a new date picker close action.
		 * 
		 * @param datePicker
		 *            the date picker
		 */
		public DatePickerCloseAction(Window datePicker) {
			this.datePicker = datePicker;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			datePicker.dispose();
		}

	}

	/*
	 * Attributes for the internally used panels and Calendar objects.
	 */
	private MonthPanel monthPanel;

	private NavigatePanel navPanel;

	protected Calendar calendar;

	private Calendar selectedDate;

	private boolean closeOnSelect = true;

	private DateFormat sdf;

	private JDialog screen;

	private JPanel panelBase;

	/**
	 * Constructor to create a new Date Picker.
	 * 
	 * @param parent
	 *            the modal window for this picker
	 * @param observer
	 *            the observer
	 */
	public DatePicker(Window parent, Observer observer) {
		this(parent, observer, new Date());
	}

	/**
	 * Constructor to create a new Date Picker.
	 * 
	 * @param parent
	 *            the modal window for this picker
	 * @param observer
	 *            observing textfield for this picker
	 * @param selDate
	 *            date to be selected
	 */
	public DatePicker(Window parent, Observer observer, Date selDate) {
		super();

		register(observer);

		panelBase = GUITools.newPopupBasePanel();

		screen = new JDialog(parent);
		screen.addWindowFocusListener(this);
		screen.setSize(200, 200);
		screen.setModal(true);

		// screen.setUndecorated(true);
		screen.setResizable(false);
		screen.setTitle(_("RevAger"));

		screen.getContentPane().setLayout(new BorderLayout());
		screen.getContentPane().add(panelBase, BorderLayout.CENTER);

		calendar = new GregorianCalendar();
		setSelectedDate(selDate);
		Calendar c = calendar;

		if (selectedDate != null) {
			c = selectedDate;
		}

		updateScreen(c);

		panelBase.add(navPanel, BorderLayout.NORTH);

		/*
		 * Close button
		 */
		JPanel panelBottom = new JPanel(new BorderLayout());

		JButton buttonClose = GUITools.newImageButton();
		buttonClose.setIcon(Data.getInstance().getIcon("datePickerClose_18x18_0.png"));
		buttonClose.setRolloverIcon(Data.getInstance().getIcon("datePickerClose_18x18.png"));
		buttonClose.setToolTipText(getString("close"));
		buttonClose.setBorder(BorderFactory.createLineBorder(screen.getContentPane().getBackground(), 4));
		buttonClose.addActionListener(new DatePickerCloseAction(screen));

		/*
		 * Hint for the user that he/she schould pick a date
		 */
		JTextField hint = new JTextField();
		hint.setBorder(BorderFactory.createLineBorder(UI.POPUP_BACKGROUND, 5));
		hint.setText(getString("hint"));
		hint.setFont(FONT_PLAIN.deriveFont(Font.BOLD));
		hint.setFocusable(false);
		hint.setEditable(false);
		hint.setSelectionColor(UI.POPUP_BACKGROUND);
		hint.setBackground(UI.POPUP_BACKGROUND);

		/*
		 * Add close button and hint to the bottom of the picker
		 */
		panelBottom.add(buttonClose, BorderLayout.EAST);
		panelBottom.add(hint, BorderLayout.WEST);
		panelBottom.setBackground(UI.POPUP_BACKGROUND);

		panelBase.add(panelBottom, BorderLayout.SOUTH);
	}

	/**
	 * Initializes the Date Picker.
	 * 
	 * @param comp
	 *            Swing Component object
	 */
	public void start(Component comp) {
		/*
		 * if (comp != null) { Component p = comp.getParent();
		 * 
		 * int x = comp.getX() + comp.getWidth(), y = comp.getY() +
		 * comp.getHeight();
		 * 
		 * while (p != null) { x += p.getX(); y += p.getY(); p = p.getParent();
		 * }
		 * 
		 * screen.setLocation(x, y); } else { Dimension dim =
		 * Toolkit.getDefaultToolkit().getScreenSize();
		 * 
		 * screen.setLocation((int) (dim.getWidth() - screen.getWidth()) / 2,
		 * (int) (dim.getHeight() - screen.getHeight()) / 2); }
		 */
		GUITools.setLocationToCursorPos(screen);

		SwingUtilities.invokeLater(this);
	}

	/**
	 * Displays the Date Picker.
	 */
	@Override
	public void run() {
		screen.pack();
		screen.setVisible(true);
	}

	/**
	 * Parses the given string and returns a Date object.
	 * 
	 * @param date
	 *            string to parse
	 * 
	 * @return Date object
	 */
	public Date parseDate(String date) {
		if (sdf == null) {
			sdf = DateFormat.getDateInstance(DateFormat.LONG);
		}

		try {
			return sdf.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Formats the given Date object and returns a string.
	 * 
	 * @param date
	 *            Date object to format
	 * 
	 * @return string
	 */
	public String formatDate(Date date) {
		if (date == null) {
			return "";
		}

		if (sdf == null) {
			sdf = DateFormat.getDateInstance(DateFormat.LONG);
		}

		return sdf.format(date);
	}

	/**
	 * Format date.
	 * 
	 * @param date
	 *            the date
	 * @param pattern
	 *            the pattern
	 * 
	 * @return the string
	 */
	public String formatDate(Date date, String pattern) {
		if (date == null) {
			return "";
		}

		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * Format date.
	 * 
	 * @param date
	 *            the date
	 * 
	 * @return the string
	 */
	public String formatDate(Calendar date) {
		if (date == null) {
			return "";
		}

		return formatDate(date.getTime());
	}

	/**
	 * Format date.
	 * 
	 * @param date
	 *            the date
	 * @param pattern
	 *            the pattern
	 * 
	 * @return the string
	 */
	public String formatDate(Calendar date, String pattern) {
		if (date == null) {
			return "";
		}

		return new SimpleDateFormat(pattern).format(date.getTime());
	}

	/**
	 * Register.
	 * 
	 * @param observer
	 *            the observer
	 */
	public void register(Observer observer) {
		if (observer != null) {
			this.addObserver(observer);
		}
	}

	/**
	 * Unregister.
	 * 
	 * @param observer
	 *            the observer
	 */
	public void unregister(Observer observer) {
		if (observer != null) {
			this.deleteObserver(observer);
		}
	}

	/**
	 * Gets the calendar.
	 * 
	 * @return the calendar
	 */
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * Sets the selected date.
	 * 
	 * @param d
	 *            the d
	 */
	public void setSelectedDate(Date d) {
		if (d != null) {
			if (selectedDate == null) {
				selectedDate = new GregorianCalendar();
			}

			this.selectedDate.setTime(d);

			updateScreen(selectedDate);
		}
	}

	/**
	 * Update screen.
	 * 
	 * @param c
	 *            the c
	 */
	protected void updateScreen(Calendar c) {
		if (navPanel == null) {
			navPanel = new NavigatePanel(this);
		}

		navPanel.setCurrentMonth(c);

		setMonthPanel(c);

		screen.pack();
	}

	/**
	 * Sets the month panel.
	 * 
	 * @param calendar
	 *            the calendar
	 */
	protected void setMonthPanel(Calendar calendar) {
		if (calendar != null) {
			this.calendar.setTime(calendar.getTime());
		}

		if (monthPanel != null) {
			panelBase.remove(monthPanel);
		}

		monthPanel = new MonthPanel(this, calendar);

		panelBase.add(monthPanel, BorderLayout.CENTER);
	}

	/**
	 * Day picked.
	 * 
	 * @param day
	 *            the day
	 */
	protected void dayPicked(int day) {
		calendar.set(Calendar.DAY_OF_MONTH, day);
		setSelectedDate(calendar.getTime());

		this.setChanged();
		this.notifyObservers(selectedDate);

		if (closeOnSelect) {
			screen.dispose();
			screen.setVisible(false);
		}
	}

	/**
	 * Gets the string.
	 * 
	 * @param key
	 *            the key
	 * 
	 * @return the string
	 */
	public String getString(String key) {
		if (key.equals("month.0")) {
			return _("January");
		} else if (key.equals("month.1")) {
			return _("February");
		} else if (key.equals("month.2")) {
			return _("March");
		} else if (key.equals("month.3")) {
			return _("April");
		} else if (key.equals("month.4")) {
			return _("May");
		} else if (key.equals("month.5")) {
			return _("June");
		} else if (key.equals("month.6")) {
			return _("July");
		} else if (key.equals("month.7")) {
			return _("August");
		} else if (key.equals("month.8")) {
			return _("September");
		} else if (key.equals("month.9")) {
			return _("October");
		} else if (key.equals("month.10")) {
			return _("November");
		} else if (key.equals("month.11")) {
			return _("December");
		} else if (key.equals("week.0")) {
			return _("Mon");
		} else if (key.equals("week.1")) {
			return _("Tue");
		} else if (key.equals("week.2")) {
			return _("Wed");
		} else if (key.equals("week.3")) {
			return _("Thu");
		} else if (key.equals("week.4")) {
			return _("Fri");
		} else if (key.equals("week.5")) {
			return _("Sat");
		} else if (key.equals("week.6")) {
			return _("Sun");
		} else if (key.equals("prevMonth")) {
			return _("Previous month");
		} else if (key.equals("nextMonth")) {
			return _("Next month");
		} else if (key.equals("prevYear")) {
			return _("Previous year");
		} else if (key.equals("nextYear")) {
			return _("Next year");
		} else if (key.equals("close")) {
			return _("Close");
		} else if (key.equals("hint")) {
			return _("Please choose a date...");
		} else if (key.equals("tooltip")) {
			return _("Calendar");
		}

		return _(key);
	}

	/**
	 * Checks if is close on select.
	 * 
	 * @return true, if is close on select
	 */
	public boolean isCloseOnSelect() {
		return closeOnSelect;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.awt.event.WindowFocusListener#windowGainedFocus(java.awt.event.
	 * WindowEvent)
	 */
	@Override
	public void windowGainedFocus(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowFocusListener#windowLostFocus(java.awt.event.
	 * WindowEvent )
	 */
	@Override
	public void windowLostFocus(WindowEvent e) {
		screen.toFront();
	}

	/**
	 * Gets the screen.
	 * 
	 * @return the screen
	 */
	public JDialog getScreen() {
		return this.screen;
	}

	/**
	 * Sets the close on select.
	 * 
	 * @param closeOnSelect
	 *            the close on select
	 */
	public void setCloseOnSelect(boolean closeOnSelect) {
		this.closeOnSelect = closeOnSelect;
	}
}
