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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.revager.app.model.Data;
import org.revager.gui.UI;


/**
 * The Class ProgressGlassPane.
 */
@SuppressWarnings("serial")
public class ProgressGlassPane extends JComponent implements KeyListener {

	private final static Border MESSAGE_BORDER = new EmptyBorder(20, 20, 20, 20);

	private final ImageIcon ICON_WAIT = Data.getInstance().getIcon(
			"wait_32x32.gif");
	private JLabel message = new JLabel(ICON_WAIT, SwingConstants.CENTER);

	/**
	 * Instantiates a new progress glass pane.
	 */
	public ProgressGlassPane() {
		// Set glass pane properties

		setOpaque(false);

		setBackground(UI.GLASSPANE_COLOR);

		setLayout(new GridBagLayout());

		// Add a message label to the glass pane

		add(message, new GridBagConstraints());
		message.setOpaque(true);
		message.setBorder(MESSAGE_BORDER);

		// Disable Mouse, Key and Focus events for the glass pane

		addMouseListener(new MouseAdapter() {
		});
		addMouseMotionListener(new MouseMotionAdapter() {
		});

		addKeyListener(this);

		setFocusTraversalKeysEnabled(false);
	}

	/*
	 * The component is transparent but we want to paint the background to give
	 * it the disabled look.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getSize().width, getSize().height);
	}

	/*
	 * The background color of the message label will be the same as the
	 * background of the glass pane without the alpha value
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color background) {
		super.setBackground(background);

		Color messageBackground = new Color(background.getRGB());
		message.setBackground(messageBackground);
	}

	//
	// Implement the KeyListener to consume events
	//
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		e.consume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		e.consume();
	}

	/*
	 * Make the glass pane visible and change the cursor to the wait cursor
	 * 
	 * A message can be displayed and it will be centered on the frame.
	 */
	/**
	 * Activate.
	 */
	public void activate() {
		activate(null);
	}

	/**
	 * Activate.
	 * 
	 * @param text
	 *            the text
	 */
	public void activate(String text) {
		if (text != null && text.length() > 0) {
			message.setVisible(true);
			message.setText(text);
			message.setForeground(getForeground());

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			message.setVisible(false);
		}

		setVisible(true);
		requestFocusInWindow();
	}

	/*
	 * Hide the glass pane and restore the cursor
	 */
	/**
	 * Deactivate.
	 */
	public void deactivate() {
		setCursor(null);
		setVisible(false);
	}
}
