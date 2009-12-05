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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import neos.resi.app.model.Data;
import neos.resi.gui.helpers.ProgressGlassPane;
import neos.resi.gui.workers.LoadEmbeddedHelpWorker;
import neos.resi.tools.GUITools;

/**
 * This class is the superclass for all dialogs in Resi.
 */
@SuppressWarnings("serial")
public abstract class AbstractDialog extends JDialog {

	/**
	 * The Constant HELP_BACKGROUND.
	 */
	public static final String HELP_BACKGROUND = "#ebf1ff";

	/**
	 * Icon to open the help.
	 */
	private final ImageIcon ICON_OPEN_HELP = Data.getInstance().getIcon(
			"help_26x26.png");

	/**
	 * Icon to close the help.
	 */
	private final ImageIcon ICON_CLOSE_HELP = Data.getInstance().getIcon(
			"closeHelp_26x26.png");

	/**
	 * Rollover open help icon.
	 */
	private final ImageIcon ICON_OPEN_HELP_ROLLOVER = Data.getInstance()
			.getIcon("helpRollover_26x26.png");

	/**
	 * Rollover close help icon.
	 */
	private final ImageIcon ICON_CLOSE_HELP_ROLLOVER = Data.getInstance()
			.getIcon("closeHelpRollover_26x26.png");

	/**
	 * The wait animation.
	 */
	private final ImageIcon ICON_WAIT = Data.getInstance().getIcon(
			"wait_32x32.gif");

	/**
	 * The parent of the dialog.
	 */
	private Frame parent;

	/**
	 * The glass pane.
	 */
	private ProgressGlassPane glassPane = new ProgressGlassPane();

	/**
	 * The split pane.
	 */
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

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
	 * The panel grid content.
	 */
	private JPanel panelGridContent = new JPanel();

	/**
	 * The panel buttons.
	 */
	private JPanel panelButtons = new JPanel();

	/**
	 * The panel help button.
	 */
	private JPanel panelHelpButton = new JPanel();

	/**
	 * The panel help.
	 */
	private JPanel panelHelp = new JPanel();

	/**
	 * The top grid panel.
	 */
	private JPanel panelGridTop = new JPanel();

	/**
	 * The panel description toolbar.
	 */
	private JPanel panelDescTB = new JPanel(new BorderLayout());

	/**
	 * The label title.
	 */
	private JLabel labelTitle = new JLabel();

	/**
	 * The label icon.
	 */
	private JLabel labelIcon = new JLabel();

	/**
	 * The text description.
	 */
	private JTextArea textDescription = new JTextArea();

	/**
	 * The description.
	 */
	private String description = null;

	/**
	 * The help chapter.
	 */
	private String helpChapter = null;

	/**
	 * The help chapter anchor.
	 */
	private String helpChapterAnchor = null;

	/**
	 * The button help.
	 */
	private JToggleButton buttonHelp = null;

	/**
	 * The help opened.
	 */
	private boolean helpOpened = false;

	/**
	 * The original width.
	 */
	private int originalWidth;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JDialog#getContentPane()
	 */
	@Override
	public Container getContentPane() {
		return panelContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Dialog#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		super.setTitle(title);

		this.labelTitle.setText(title);
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;

		this.textDescription.setForeground(Color.BLACK);
		this.textDescription.setText(description);

		panelDescTB.add(textDescription);
	}

	/**
	 * Sets the top panel.
	 * 
	 * @param panel
	 *            the panel
	 */
	public void setTopPanel(JPanel panel) {
		panel.setBackground(Color.WHITE);
		
		panelTop.remove(panelGridTop);
		panelTop.add(panel, BorderLayout.CENTER);
		
		panelTop.revalidate();
	}

	/**
	 * Sets the hint.
	 * 
	 * @param hint
	 *            the new hint
	 */
	public void setHint(String hint) {
		if (hint == null) {
			setDescription(this.description);
		} else {
			this.textDescription.setForeground(Color.RED);
			this.textDescription.setText(hint);
		}
	}

	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		if (message == null) {
			setDescription(this.description);
		} else {
			this.textDescription.setForeground(UI.MARKED_COLOR);
			this.textDescription.setText(message);
		}
	}

	/**
	 * Sets the icon.
	 * 
	 * @param icon
	 *            the new icon
	 */
	public void setIcon(ImageIcon icon) {
		this.labelIcon.setIcon(icon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setMinimumSize(java.awt.Dimension)
	 */
	@Override
	public void setMinimumSize(Dimension minimumSize) {
		super.setMinimumSize(minimumSize);

		panelBase.setMinimumSize(new Dimension(
				(int) minimumSize.getWidth() - 20, (int) minimumSize
						.getHeight()));
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
	 * Adds the button.
	 * 
	 * @param button
	 *            the button
	 */
	public void addButton(JButton button) {
		this.panelButtons.add(button);
	}

	/**
	 * Sets the location to center.
	 */
	public void setLocationToCenter() {
		int posX = (int) ((int) (this.parent.getSize().getWidth() / 2) - (this
				.getSize().getWidth() / 2));
		int posY = (int) ((int) (this.parent.getSize().getHeight() / 2) - (this
				.getSize().getHeight() / 2));

		setLocation(posX, posY);
	}

	/**
	 * Sets the help chapter.
	 * 
	 * @param chapter
	 *            the new help chapter
	 */
	public void setHelpChapter(String chapter) {
		setHelpChapter(chapter, null);
	}

	/**
	 * Sets the help chapter.
	 * 
	 * @param chapter
	 *            the chapter
	 * @param anchor
	 *            the anchor
	 */
	public void setHelpChapter(String chapter, String anchor) {
		this.helpChapter = chapter;
		this.helpChapterAnchor = anchor;

		panelHelpButton.setVisible(true);
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
	 */
	protected void gblAdd(GridBagLayout gbl, Container container,
			Component component, int posx, int posy, int width, int height,
			Insets insets, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = posx;
		gbc.gridy = posy;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = insets;
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(component, gbc);
		container.add(component);
	}

	/**
	 * The Constructor.
	 * 
	 * @param parent
	 *            the parent
	 */
	public AbstractDialog(Frame parent) {
		super(parent);
		this.parent = parent;

		setModal(true);
		// setAlwaysOnTop(true);

		setGlassPane(glassPane);

		panelBase.setLayout(new BorderLayout());
		panelTop.setLayout(new BorderLayout());

		/*
		 * Build the top panel
		 */
		Font fontTitle = new Font(Font.SANS_SERIF, Font.BOLD, 13);
		Font fontText = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

		GridBagLayout gblTop = new GridBagLayout();
		panelGridTop.setLayout(gblTop);
		panelGridTop.setBackground(Color.WHITE);

		labelTitle.setFont(fontTitle);

		textDescription.setRows(3);
		textDescription.setFont(fontText);
		textDescription.setEditable(false);
		textDescription.setLineWrap(true);
		textDescription.setWrapStyleWord(true);
		textDescription.setSelectionColor(Color.WHITE);
		textDescription.setBorder(null);

		gblAdd(gblTop, panelGridTop, labelTitle, 0, 0, 1, 1, new Insets(10, 10,
				10, 10), 0.0, 0.0);
		gblAdd(gblTop, panelGridTop, labelIcon, 1, 0, 1, 2, new Insets(10, 20,
				10, 20), 0.0, 0.0);
		gblAdd(gblTop, panelGridTop, panelDescTB, 0, 1, 1, 1, new Insets(0, 10,
				10, 10), 1.0, 1.0);

		panelTop.add(panelGridTop, BorderLayout.CENTER);
		panelTop.setBorder(new MatteBorder(0, 0, 1, 0, UI.SEPARATOR_COLOR));

		/*
		 * Build the buttons panel and help button
		 */
		FlowLayout buttonLayout = new FlowLayout();
		buttonLayout.setAlignment(FlowLayout.RIGHT);
		buttonLayout.setHgap(10);
		buttonLayout.setVgap(10);

		panelButtons.setLayout(buttonLayout);

		FlowLayout helpButtonLayout = new FlowLayout();
		helpButtonLayout.setAlignment(FlowLayout.LEFT);
		helpButtonLayout.setHgap(10);
		helpButtonLayout.setVgap(10);

		buttonHelp = GUITools.newImageToggleButton();
		buttonHelp.setToolTipText(Data.getInstance().getLocaleStr("showHelp"));
		buttonHelp.setIcon(ICON_OPEN_HELP);
		buttonHelp.setSelectedIcon(ICON_CLOSE_HELP);
		buttonHelp.setRolloverIcon(ICON_OPEN_HELP_ROLLOVER);
		buttonHelp.setRolloverSelectedIcon(ICON_CLOSE_HELP_ROLLOVER);

		buttonHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleHelp();
			}
		});

		panelHelpButton.setLayout(helpButtonLayout);
		panelHelpButton.add(buttonHelp);
		panelHelpButton.setVisible(false);

		/*
		 * Build the bottom panel
		 */
		GridBagLayout gblBottom = new GridBagLayout();
		panelBottom.setLayout(gblBottom);

		panelBottom.setBorder(new MatteBorder(1, 0, 0, 0, UI.SEPARATOR_COLOR));

		gblAdd(gblBottom, panelBottom, panelHelpButton, 0, 0, 1, 1, new Insets(
				5, 5, 5, 5), 1.0, 1.0);
		gblAdd(gblBottom, panelBottom, panelButtons, 1, 0, 1, 1, new Insets(5,
				5, 5, 5), 1.0, 1.0);

		/*
		 * Build content pane with padding
		 */
		GridBagLayout gblContent = new GridBagLayout();

		panelGridContent.setLayout(gblContent);

		gblAdd(gblContent, panelGridContent, panelContent, 0, 0, 1, 1,
				new Insets(25, 15, 30, 15), 1.0, 1.0);

		panelContent.setLayout(null);

		/*
		 * Construct the dialog
		 */
		panelBase.add(panelTop, BorderLayout.NORTH);
		panelBase.add(panelGridContent, BorderLayout.CENTER);
		panelBase.add(panelBottom, BorderLayout.SOUTH);

		setContentPane(panelBase);

		pack();

		setMinimumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(500, 500));
		setMaximumSize(new Dimension(650, 600));

		/*
		 * Behaviour when closing this dialog.
		 */
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// buttonClicked = ButtonClicked.CANCEL;
				setVisible(false);
			}
		});
	}

	/**
	 * Switch to edit mode.
	 */
	public void switchToEditMode() {
		glassPane.deactivate();
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
		glassPane.activate(text);
	}

	/**
	 * Toggle help.
	 */
	public void toggleHelp() {
		if (helpOpened == false) {
			helpOpened = true;

			originalWidth = (int) this.getSize().getWidth();

			/*
			 * Show "wait..." as label
			 */
			JLabel wait = new JLabel(Data.getInstance().getLocaleStr(
					"message.helpIsLoading"), ICON_WAIT, SwingConstants.CENTER);

			panelHelp.setLayout(new BorderLayout());
			panelHelp.add(wait, BorderLayout.CENTER);

			/*
			 * Configure split pane
			 */
			splitPane.setRightComponent(panelHelp);
			splitPane.setLeftComponent(panelBase);
			splitPane.setDividerSize(8);
			splitPane.setEnabled(true);
			splitPane.setBorder(null);
			splitPane.setContinuousLayout(true);

			int expandArea = 400;
			int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize()
					.getWidth();
			int dialogWidth = originalWidth + expandArea;

			if (this.getX() + dialogWidth > screenWidth) {
				dialogWidth = screenWidth - this.getX();
			}

			setSize(new Dimension(dialogWidth, (int) this.getSize().getHeight()));

			setContentPane(splitPane);

			/*
			 * Load help content
			 */
			LoadEmbeddedHelpWorker worker = new LoadEmbeddedHelpWorker(
					panelHelp, helpChapter, helpChapterAnchor);

			worker.execute();
		} else {
			helpOpened = false;

			setSize(new Dimension(originalWidth, (int) this.getSize()
					.getHeight()));

			setContentPane(panelBase);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		this.parent.setEnabled(!vis);

		if (helpOpened && !vis) {
			toggleHelp();

			buttonHelp.setSelected(false);
		}

		if (vis) {
			setLocationToCenter();
		}

		super.setVisible(vis);
	}

}
