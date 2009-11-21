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
package neos.resi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.MatteBorder;

import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.appdata.AppSettingKey;
import neos.resi.app.model.appdata.AppSettingValue;
import neos.resi.gui.helpers.HintItem;
import neos.resi.gui.helpers.ProgressGlassPane;
import neos.resi.gui.workers.LoadHelpWorker;
import neos.resi.tools.GUITools;

/**
 * This class is the superclass for all frames in Resi.
 */
@SuppressWarnings("serial")
public class AbstractFrame extends JFrame {

	/**
	 * The ICO n_ frame.
	 */
	private final Image ICON_FRAME = Data.getInstance().getIcon(
			"frameIcon_128x128.png").getImage();

	/**
	 * The HINT s_ background.
	 */
	private final Color HINTS_BACKGROUND = UI.BLUE_BACKGROUND_COLOR;

	/**
	 * The FON t_ text.
	 */
	public final Font FONT_TEXT = UI.STANDARD_FONT;

	/**
	 * The ICO n_ help.
	 */
	private final ImageIcon ICON_HELP = Data.getInstance().getIcon(
			"hintHelp_22x22_0.png");

	/**
	 * The ICO n_ hel p_ rollover.
	 */
	private final ImageIcon ICON_HELP_ROLLOVER = Data.getInstance().getIcon(
			"hintHelp_22x22.png");

	/**
	 * The ICO n_ ope n_ hints.
	 */
	private final ImageIcon ICON_OPEN_HINTS = Data.getInstance().getIcon(
			"openHints_16x16_0.png");

	/**
	 * The ICO n_ clos e_ hints.
	 */
	private final ImageIcon ICON_CLOSE_HINTS = Data.getInstance().getIcon(
			"closeHints_16x16_0.png");

	/**
	 * The ICO n_ ope n_ hint s_ rollover.
	 */
	private final ImageIcon ICON_OPEN_HINTS_ROLLOVER = Data.getInstance()
			.getIcon("openHints_16x16.png");

	/**
	 * The ICO n_ clos e_ hint s_ rollover.
	 */
	private final ImageIcon ICON_CLOSE_HINTS_ROLLOVER = Data.getInstance()
			.getIcon("closeHints_16x16.png");

	/**
	 * The ICO n_ wai t_ small.
	 */
	private final ImageIcon ICON_WAIT_SMALL = Data.getInstance().getIcon(
			"wait_16x16.gif");

	/**
	 * The ICO n_ blank.
	 */
	private final ImageIcon ICON_BLANK = Data.getInstance().getIcon(
			"blank_16x16.png");

	/**
	 * The progress pane.
	 */
	private ProgressGlassPane progressPane = new ProgressGlassPane();

	/**
	 * The disable pane.
	 */
	private ProgressGlassPane disablePane = new ProgressGlassPane();

	/**
	 * The panel base.
	 */
	private JPanel panelBase = new JPanel();

	/**
	 * The panel top.
	 */
	private JPanel panelTop = new JPanel();

	/**
	 * The panel content.
	 */
	private JPanel panelContent = new JPanel();

	/**
	 * The panel bottom.
	 */
	private JPanel panelBottom = new JPanel();

	/**
	 * The panel tool bar.
	 */
	private JPanel panelToolBar = new JPanel();

	/**
	 * The panel top right.
	 */
	private JPanel panelTopRight = new JPanel();

	/**
	 * The panel grid content.
	 */
	private JPanel panelGridContent = new JPanel();

	/**
	 * The panel border hints.
	 */
	private JPanel panelBorderHints = new JPanel();

	/**
	 * The panel hints.
	 */
	private JPanel panelHints;

	/**
	 * The panel border strut.
	 */
	private JPanel panelBorderStrut = new JPanel();

	/**
	 * The button hints.
	 */
	private JToggleButton buttonHints = GUITools.newImageToggleButton();

	/**
	 * The label icon.
	 */
	private JLabel labelIcon = new JLabel();

	/**
	 * The status message.
	 */
	private JLabel statusMessage = new JLabel();

	/**
	 * The status in progress.
	 */
	private JLabel statusInProgress = new JLabel(ICON_BLANK);

	/**
	 * The format time.
	 */
	private DateFormat formatTime = new SimpleDateFormat(Data.getInstance()
			.getLocaleStr("format.time"));

	/**
	 * The number of hints.
	 */
	private int numberOfHints = 3;

	/**
	 * The current hints.
	 */
	private List<HintItem> currentHints = null;

	/**
	 * The hints opened.
	 */
	private boolean hintsOpened = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JFrame#getContentPane()
	 */
	@Override
	public Container getContentPane() {
		return panelContent;
	}

	/**
	 * Sets the icon.
	 * 
	 * @param icon
	 *            the new icon
	 */
	public void setIcon(ImageIcon icon) {
		this.labelIcon.setIcon(icon);

		this.panelTopRight.removeAll();

		this.panelTopRight.add(labelIcon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#add(java.awt.Component)
	 */
	@Override
	public Component add(Component comp) {
		return this.panelContent.add(comp);
	}

	/**
	 * Adds the top right comp.
	 * 
	 * @param component
	 *            the component
	 */
	public void addTopRightComp(Component component) {
		this.panelTopRight.add(component);
		this.panelTopRight.revalidate();
	}

	/**
	 * Clear top right comps.
	 */
	public void clearTopRightComps() {
		this.panelTopRight.removeAll();
		this.panelTopRight.revalidate();
	}

	/**
	 * Sets the top right comps visible.
	 * 
	 * @param visible
	 *            the new top right comps visible
	 */
	public void setTopRightCompsVisible(boolean visible) {
		this.panelTopRight.setVisible(visible);
	}

	/**
	 * Sets the top right comps enabled.
	 * 
	 * @param enabled
	 *            the new top right comps enabled
	 */
	public void setTopRightCompsEnabled(boolean enabled) {
		for (Component c : this.panelTopRight.getComponents()) {
			c.setEnabled(enabled);
		}
	}

	/**
	 * Adds the top component.
	 * 
	 * @param component
	 *            the component
	 */
	public void addTopComponent(Component component) {
		this.panelToolBar.add(component);
		this.panelToolBar.revalidate();
	}

	/**
	 * Clear top components.
	 */
	public void clearTopComponents() {
		this.panelToolBar.removeAll();
		this.panelToolBar.revalidate();
	}

	/**
	 * Sets the top components visible.
	 * 
	 * @param visible
	 *            the new top components visible
	 */
	public void setTopComponentsVisible(boolean visible) {
		this.panelToolBar.setVisible(visible);
	}

	/**
	 * Sets the top components enabled.
	 * 
	 * @param enabled
	 *            the new top components enabled
	 */
	public void setTopComponentsEnabled(boolean enabled) {
		for (Component c : this.panelToolBar.getComponents()) {
			c.setEnabled(enabled);
		}
	}

	/**
	 * Sets the hints opened.
	 * 
	 * @param hintsOpened
	 *            the new hints opened
	 */
	public void setHintsOpened(boolean hintsOpened) {
		this.hintsOpened = hintsOpened;
	}

	/**
	 * Sets the status message.
	 * 
	 * @param message
	 *            the message
	 * @param inProgress
	 *            the in progress
	 */
	synchronized public void setStatusMessage(String message, boolean inProgress) {
		this.statusMessage.setText(formatTime.format(new Date().getTime())
				+ " | " + message);

		if (inProgress == true) {
			this.statusInProgress.setIcon(ICON_WAIT_SMALL);
		} else {
			this.statusInProgress.setIcon(ICON_BLANK);
		}
	}

	/**
	 * Sets the status bar visible.
	 * 
	 * @param statusBar
	 *            the new status bar visible
	 */
	public void setStatusBarVisible(boolean statusBar) {
		this.panelBottom.setVisible(statusBar);
	}

	/**
	 * Sets the location to center.
	 */
	public void setLocationToCenter() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int posX = (int) ((int) (screenSize.getWidth() / 2) - (this.getSize()
				.getWidth() / 2));
		int posY = (int) ((int) (screenSize.getHeight() / 2) - (this.getSize()
				.getHeight() / 2));

		setLocation(posX, posY);
	}

	/**
	 * Helper method to add a component to a GridBagLayout.
	 * 
	 * @param gbl
	 *            the layout to add the component
	 * @param container
	 *            the container object in which the layout is
	 * @param component
	 *            the component to add
	 * @param posx
	 *            the vertical position
	 * @param posy
	 *            the horizontal position
	 * @param width
	 *            the width of the component
	 * @param height
	 *            the height of the component
	 * @param insets
	 *            the padding of the component
	 * @param weightx
	 *            the vertical weight
	 * @param weighty
	 *            the horizontal weight
	 * @param fill
	 *            the fill
	 * @param anchor
	 *            the anchor
	 */
	protected void gblAdd(GridBagLayout gbl, Container container,
			Component component, int posx, int posy, int width, int height,
			Insets insets, int fill, int anchor, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = posx;
		gbc.gridy = posy;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = insets;
		gbc.fill = fill;
		gbc.anchor = anchor;
		gbl.setConstraints(component, gbc);
		container.add(component);
	}

	/**
	 * Gbl add.
	 * 
	 * @param gbl
	 *            the gbl
	 * @param container
	 *            the container
	 * @param component
	 *            the component
	 * @param posx
	 *            the posx
	 * @param posy
	 *            the posy
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param insets
	 *            the insets
	 * @param weightx
	 *            the weightx
	 * @param weighty
	 *            the weighty
	 */
	protected void gblAdd(GridBagLayout gbl, Container container,
			Component component, int posx, int posy, int width, int height,
			Insets insets, double weightx, double weighty) {
		gblAdd(gbl, container, component, posx, posy, width, height, insets,
				GridBagConstraints.BOTH, GridBagConstraints.BASELINE, weightx,
				weighty);
	}

	/**
	 * Instantiates a new abstract frame.
	 */
	public AbstractFrame() {
		super();

		setLayout(new BorderLayout());

		setIconImage(ICON_FRAME);

		setGlassPane(disablePane);

		panelBase.setLayout(new BorderLayout());
		panelTop.setLayout(new BorderLayout());

		panelBorderHints.setLayout(new BorderLayout());
		panelBorderStrut = new JPanel();
		panelBorderStrut.setBackground(HINTS_BACKGROUND);

		/*
		 * Build the top panel
		 */
		FlowLayout flowToolBar = new FlowLayout(FlowLayout.LEFT);
		FlowLayout flowTopRight = new FlowLayout(FlowLayout.RIGHT);

		flowToolBar.setVgap(12);
		flowToolBar.setHgap(14);

		flowTopRight.setVgap(12);
		flowTopRight.setHgap(14);

		panelToolBar.setLayout(flowToolBar);
		panelToolBar.setBackground(Color.WHITE);

		panelTopRight.setLayout(flowTopRight);
		panelTopRight.setBackground(Color.WHITE);

		JPanel panelBorderTop = new JPanel();
		panelBorderTop.setLayout(new BorderLayout());
		panelBorderTop.setBackground(Color.WHITE);

		panelBorderTop.add(panelToolBar, BorderLayout.WEST);
		panelBorderTop.add(panelTopRight, BorderLayout.EAST);

		GridBagLayout gblTop = new GridBagLayout();
		JPanel panelGridTop = new JPanel();
		panelGridTop.setLayout(gblTop);
		panelGridTop.setBackground(Color.WHITE);
		gblAdd(gblTop, panelGridTop, panelBorderTop, 0, 0, 1, 1, new Insets(0,
				0, 0, 10), 1.0, 1.0);

		panelTop.add(panelGridTop, BorderLayout.CENTER);
		panelTop.setBorder(new MatteBorder(0, 0, 1, 0, UI.SEPARATOR_COLOR));

		/*
		 * Construct the button to toggle the hints
		 */
		buttonHints
				.setToolTipText(Data.getInstance().getLocaleStr("showHints"));
		buttonHints.setIcon(ICON_CLOSE_HINTS);
		buttonHints.setRolloverIcon(ICON_CLOSE_HINTS_ROLLOVER);
		buttonHints.setRolloverSelectedIcon(ICON_OPEN_HINTS_ROLLOVER);

		buttonHints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleHints();
			}
		});

		/*
		 * Build the bottom panel
		 */
		statusMessage.setFont(FONT_TEXT);
		statusMessage.setForeground(Color.DARK_GRAY);
		setStatusMessage(Data.getInstance().getLocaleStr(
				"status.noReviewInProcess"), false);

		GridBagLayout gblBottom = new GridBagLayout();
		panelBottom.setLayout(gblBottom);

		panelBottom.setBorder(new MatteBorder(1, 0, 0, 0, UI.SEPARATOR_COLOR));

		/*
		 * Minor margin for Mac
		 */
		int ins = 6;

		gblAdd(gblBottom, panelBottom, panelBorderHints, 0, 0, 3, 1,
				new Insets(0, 0, 0, 0), 1.0, 1.0);
		gblAdd(gblBottom, panelBottom, statusMessage, 0, 1, 1, 1, new Insets(0,
				7, 0, 7), GridBagConstraints.WEST, GridBagConstraints.WEST,
				0.0, 1.0);
		gblAdd(gblBottom, panelBottom, statusInProgress, 1, 1, 1, 1,
				new Insets(6, 6, 6, 6), GridBagConstraints.WEST,
				GridBagConstraints.WEST, 1.0, 1.0);
		gblAdd(gblBottom, panelBottom, buttonHints, 2, 1, 1, 1, new Insets(ins,
				ins, ins, ins), GridBagConstraints.EAST,
				GridBagConstraints.EAST, 1.0, 1.0);

		/*
		 * Build content pane with padding
		 */
		GridBagLayout gblContent = new GridBagLayout();

		panelGridContent.setLayout(gblContent);

		gblAdd(gblContent, panelGridContent, panelContent, 0, 0, 1, 1,
				new Insets(15, 15, 15, 15), 1.0, 1.0);

		panelContent.setLayout(null);

		/*
		 * Construct the dialog
		 */
		panelBase.add(panelTop, BorderLayout.NORTH);
		panelBase.add(panelGridContent, BorderLayout.CENTER);
		panelBase.add(panelBottom, BorderLayout.SOUTH);

		/*
		 * General properties for the window
		 */
		setContentPane(panelBase);

		setHints(null);

		// pack();

		setMinimumSize(new Dimension(800, 650));

		/*
		 * Behaviour when closing this dialog.
		 */
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		/*
		 * addWindowListener(new WindowAdapter() {
		 * 
		 * @Override public void windowClosing(WindowEvent e) { // buttonClicked
		 * = ButtonClicked.CANCEL; setVisible(false); } });
		 */
	}

	/**
	 * Sets the number of hints.
	 * 
	 * @param numberOfHints
	 *            the numberOfHints to set
	 */
	public void setNumberOfHints(int numberOfHints) {
		this.numberOfHints = numberOfHints;
	}

	/**
	 * Sets the hints.
	 * 
	 * @param hints
	 *            the new hints
	 */
	public void setHints(List<HintItem> hints) {
		if (hints != null) {
			if (hints.isEmpty()) {
				hints = null;
			}
		}

		/*
		 * Check if the hints are the same
		 */
		boolean hintsAreTheSame = false;

		if (currentHints != null) {
			hintsAreTheSame = true;

			Iterator<HintItem> iterCurHints = currentHints.iterator();

			for (HintItem hint : hints) {
				if (hint != iterCurHints.next()) {
					hintsAreTheSame = false;

					break;
				}
			}
		}

		if (hintsAreTheSame == true) {
			return;
		}

		if (hints == null) {
			buttonHints.setIcon(ICON_BLANK);
			buttonHints.setSelectedIcon(ICON_BLANK);
			buttonHints.setEnabled(false);

			if (hintsOpened == true) {
				toggleHints();
			}
		} else {
			buttonHints.setIcon(ICON_CLOSE_HINTS);
			buttonHints.setSelectedIcon(ICON_OPEN_HINTS);
			buttonHints.setEnabled(true);

			GridBagLayout gblHints = new GridBagLayout();

			panelHints = new JPanel();
			panelHints.setLayout(gblHints);
			panelHints.setBackground(HINTS_BACKGROUND);

			/*
			 * Add vertical strut
			 */
			gblAdd(gblHints, panelHints, new JLabel(), 0, 0, 1, 1, new Insets(
					10, 0, 0, 0), 0.0, 1.0);

			Iterator<HintItem> iter = hints.iterator();
			int i = 1;

			while (iter.hasNext() && i <= numberOfHints) {
				String icon;

				final HintItem HINT = iter.next();

				switch (HINT.getType()) {
				case HintItem.ERROR:
					icon = "hintError_22x22.png";
					break;

				case HintItem.WARNING:
					icon = "hintWarning_22x22.png";
					break;

				case HintItem.OK:
					icon = "hintOk_22x22.png";
					break;

				case HintItem.INFO:
					icon = "hintInfo_22x22.png";
					break;

				default:
					icon = "hintInfo_22x22.png";
					break;
				}

				JLabel labelIcon = new JLabel(Data.getInstance().getIcon(icon));

				JTextArea text = new JTextArea(HINT.getText());
				text.setFont(FONT_TEXT);
				text.setRows(2);
				text.setEditable(false);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				text.setBackground(HINTS_BACKGROUND);
				text.setSelectionColor(HINTS_BACKGROUND);
				text.setBorder(null);

				gblAdd(gblHints, panelHints, labelIcon, 0, 2 * i, 1, 1,
						new Insets(0, 15, 5, 0), GridBagConstraints.WEST,
						GridBagConstraints.WEST, 0.0, 1.0);
				gblAdd(gblHints, panelHints, text, 1, 2 * i, 1, 1, new Insets(
						10, 15, 5, 15), GridBagConstraints.BOTH,
						GridBagConstraints.WEST, 1.0, 1.0);

				if (HINT.getHelpChapter() != null) {
					JButton helpButton = GUITools.newImageButton();
					helpButton.setIcon(ICON_HELP);
					helpButton.setRolloverIcon(ICON_HELP_ROLLOVER);
					helpButton.setBackground(Color.WHITE);
					helpButton.setFocusable(false);

					helpButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							new LoadHelpWorker(HINT.getHelpChapter(), HINT
									.getHelpChapterAnchor()).execute();
						}

					});

					try {
						helpButton
								.setToolTipText(Data.getInstance()
										.getLocaleStr("showHelpChapter")
										+ " "
										+ Data.getInstance().getHelpData()
												.getChapterTitle(
														HINT.getHelpChapter()));
					} catch (DataException e) {
						helpButton.setVisible(false);
					}

					/*
					 * Minor margin for Mac
					 */
					int ins = 15;

					gblAdd(gblHints, panelHints, helpButton, 2, 2 * i, 1, 1,
							new Insets(ins / 2, 0, ins, 8),
							GridBagConstraints.EAST, GridBagConstraints.EAST,
							0.0, 1.0);
				}

				i++;
			}

			/*
			 * Show hints
			 */
			if (!hintsOpened && currentHints == null) {
				try {
					if (Data.getInstance().getAppData().getSettingValue(
							AppSettingKey.APP_SHOW_HINTS) == AppSettingValue.TRUE) {
						toggleHints();
					} else {
						buttonHints.setSelected(true);
					}
				} catch (DataException e) {
					toggleHints();
				}
			}

			/*
			 * Refresh hints view
			 */
			if (hintsOpened) {
				panelBorderHints.removeAll();
				panelBorderHints.add(panelHints, BorderLayout.CENTER);
				panelBorderHints.add(panelBorderStrut, BorderLayout.SOUTH);
				panelBorderHints.revalidate();
			}
		}

		/*
		 * Save the new hints as current hints
		 */
		currentHints = hints;
	}

	/**
	 * Switch to edit mode.
	 */
	public void switchToEditMode() {
		// panelBase.remove(panelGridContent);
		// panelBase.add(panelGridContent, BorderLayout.CENTER);

		// panelGridContent.revalidate();
		// panelGridContent.repaint();

		// panelBase.revalidate();
		// panelBase.repaint();

		progressPane.deactivate();
		setGlassPane(disablePane);
	}

	/**
	 * Switch to progress mode.
	 */
	public void switchToProgressMode() {
		switchToProgressMode(Data.getInstance().getLocaleStr(
				"message.inProgress"));
	}

	/**
	 * Switch to progress mode.
	 * 
	 * @param text
	 *            the text
	 */
	public void switchToProgressMode(String text) {
		// panelBase.remove(panelGridContent);

		// panelBase.revalidate();
		// panelBase.repaint();

		setGlassPane(progressPane);
		progressPane.activate(text);
	}

	/**
	 * Switch to clear mode.
	 */
	public void switchToClearMode() {
		// panelBase.remove(panelGridContent);

		// panelBase.revalidate();
		// panelBase.repaint();

		progressPane.deactivate();
		setGlassPane(disablePane);
	}

	/**
	 * Toggle hints.
	 */
	public void toggleHints() {
		if (panelHints != null) {
			if (buttonHints.isSelected()) {
				buttonHints.setPressedIcon(ICON_OPEN_HINTS);
				buttonHints.setSelected(true);
			} else {
				buttonHints.setPressedIcon(ICON_CLOSE_HINTS);
				buttonHints.setSelected(false);
			}

			if (hintsOpened == false) {
				hintsOpened = true;

				panelBorderHints.setBorder(new MatteBorder(0, 0, 1, 0,
						UI.SEPARATOR_COLOR));
				panelBorderHints.add(panelHints, BorderLayout.CENTER);
				panelBorderHints.add(panelBorderStrut, BorderLayout.SOUTH);
				panelBorderHints.revalidate();
			} else {
				hintsOpened = false;

				panelBorderHints.setBorder(null);
				panelBorderHints.removeAll();
				panelBorderHints.revalidate();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		if (enabled) {
			disablePane.deactivate();
		} else {
			disablePane.activate();
		}

		super.setEnabled(enabled);
	}
}
