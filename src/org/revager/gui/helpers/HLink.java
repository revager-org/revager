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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.revager.gui.AbstractDialogPanel;
import org.revager.tools.GUITools;

/**
 * The class HLink.
 * 
 * @author D.Casciato
 * 
 */
@SuppressWarnings("serial")
public class HLink extends JPanel {

	private String localTextStrng;
	private JLabel localLbl;
	private ImageIcon localIcon;
	private ImageIcon localSelIcon;
	private ImageIcon localDisIcon;
	private ImageIcon localRolloverIcon;
	private JButton localBttn;
	private GridBagLayout gbl = new GridBagLayout();
	private Font localFont = new Font(Font.DIALOG, Font.PLAIN, 11);
	private JPanel localPnl = this;
	private ActionListener localAction = null;
	private LinkGroup localGroup;
	private Boolean selected = false;
	private AbstractDialogPanel father = null;
	private String rollOverText = "";

	/**
	 * Returns the father of the component. Father means the same as parent.
	 * 
	 * @return
	 */
	public AbstractDialogPanel getFather() {
		return father;
	}

	/**
	 * Sets the father of the component.
	 * 
	 * @return
	 */
	public void setFather(AbstractDialogPanel parent) {
		this.father = parent;
	}

	/**
	 * Returns if the link is selected or not. Important when using the link as
	 * RadioButton.
	 * 
	 * @return
	 */
	public Boolean getSelected() {
		return selected;
	}

	/**
	 * Listener which selects the link by mouse click.
	 */
	private MouseListener selectionListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (localGroup != null)
				localGroup.deselectAllLinks();
			if (!selected)
				selected = true;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		};
	};

	/**
	 * Listener which changes the design of the link depending on the use of the
	 * link. If a group exists the link is used as radio button. Else it's used
	 * only as link.
	 */
	private MouseListener localListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (localGroup == null) {
				localLbl.setForeground(Color.BLUE);
				localBttn.setSelected(true);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setBold(true);
			} else {
				localGroup.resetAllLinks();
				localBttn.setIcon(localSelIcon);
				localBttn.setRolloverIcon(localSelIcon);
				localLbl.setForeground(Color.BLUE);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setSelected(true);
				setBold(true);
			}

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (localGroup == null) {
				localLbl.setForeground(Color.BLUE);
				localBttn.setSelected(true);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setBold(true);
			} else {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setUnderlined(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (localGroup == null) {
				localLbl.setForeground(Color.BLACK);
				localBttn.setSelected(false);
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setBold(false);
			} else {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setUnderlined(false);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Returns the icon of the non-selected state.
	 * 
	 * @return
	 */
	public ImageIcon getLocalDisIcon() {
		return localDisIcon;
	}

	/**
	 * Sets the icon of the non-selected state.
	 * 
	 * @return
	 */
	public void setLocalDisIcon(ImageIcon localDisIcon) {
		this.localDisIcon = localDisIcon;
	}

	/**
	 * Returns the button of the link which contains the icon.
	 * 
	 * @return
	 */
	public JButton getLocalBttn() {
		return localBttn;
	}

	/**
	 * Sets the text of the link.
	 * 
	 * @param localTextStrng
	 */
	public void setLocalTextStrng(String localTextStrng) {
		this.localTextStrng = localTextStrng;
	}

	/**
	 * Sets the icon of the link.
	 * 
	 * @param localIcon
	 */
	public void setLocalIcon(ImageIcon localIcon) {
		this.localIcon = localIcon;
		localBttn.setIcon(localIcon);
	}

	/**
	 * Sets the rollover icon of the link.
	 * 
	 * @param localRolloverIcon
	 */
	public void setLocalRolloverIcon(ImageIcon localRolloverIcon) {
		this.localRolloverIcon = localRolloverIcon;
		localBttn.setRolloverIcon(localRolloverIcon);
	}

	/**
	 * Returns the icon of the link.
	 * 
	 * @return
	 */
	public ImageIcon getLocalIcon() {
		return localIcon;
	}

	/**
	 * Returns the rollover icon of the link.
	 * 
	 * @return
	 */
	public ImageIcon getLocalRolloverIcon() {
		return localRolloverIcon;
	}

	/**
	 * Sets the text underlined if isUnderlined is true.
	 * 
	 * @param isUnderlined
	 */
	public void setUnderlined(Boolean isUnderlined) {
		if (isUnderlined)
			localLbl.setText("<html><U>" + localTextStrng + "</html>");
		else
			localLbl.setText(localTextStrng);
	}

	/**
	 * Sets the text italic if isItalic is true.
	 * 
	 * @param isItalic
	 */
	public void setItalic(Boolean isItalic) {
		if (isItalic)
			localFont = new Font(Font.DIALOG, Font.ITALIC, 11);
		else
			localFont = new Font(Font.DIALOG, Font.PLAIN, 11);

	}

	/**
	 * Sets the text bold if isBold is true.
	 * 
	 * @param isBold
	 */
	public void setBold(Boolean isBold) {
		if (isBold) {
			localFont = new Font(Font.DIALOG, Font.BOLD, 11);
			localLbl.setFont(localFont);
			localLbl.validate();
			localLbl.repaint();
			this.validate();
			this.repaint();
		} else {
			localFont = new Font(Font.DIALOG, Font.PLAIN, 11);
			localLbl.setFont(localFont);
			localLbl.validate();
			localLbl.repaint();
			this.validate();
			this.repaint();

		}
	}

	/**
	 * Sets the color of the text.
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		localLbl.setForeground(color);
	}

	/**
	 * Constructor
	 * 
	 * @param text
	 * @param icon
	 * @param rolloverIcon
	 */
	public HLink(String text, ImageIcon icon, ImageIcon rolloverIcon,
			LinkGroup group) {
		super();
		this.setLayout(gbl);
		this.localGroup = group;
		localIcon = icon;
		localRolloverIcon = rolloverIcon;
		localSelIcon = rolloverIcon;
		localDisIcon = icon;
		if (group == null)
			localBttn = GUITools.newImageButton(localIcon, localRolloverIcon);
		else
			localBttn = GUITools.newImageButton(localIcon, localIcon);

		localTextStrng = text;
		localLbl = new JLabel(localTextStrng);

		this.addMouseListener(localListener);
		localLbl.addMouseListener(localListener);
		localBttn.addMouseListener(localListener);
		localBttn.addMouseListener(selectionListener);
		localLbl.addMouseListener(selectionListener);

		GUITools.addComponent(localPnl, gbl, localBttn, 0, 0, 1, 1, 0.0, 0.0,
				0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools
				.addComponent(localPnl, gbl, localLbl, 1, 0, 1, 1, 1.0, 0.0, 0,
						5, 0, 0, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.WEST);

	}

	/**
	 * Method for adding an action to the links subcomponents.
	 * 
	 * @param action
	 */
	public void addActionListener(ActionListener action) {
		localAction = action;
		localBttn.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				localAction.actionPerformed(null);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		localLbl.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				localAction.actionPerformed(null);

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * Sets the link selected or not.
	 * 
	 * @param b
	 */
	public void setSelected(boolean b) {
		selected = b;
	}

	/**
	 * Returns the selection icon of the link.
	 * 
	 * @return
	 */
	public ImageIcon getLocalSelIcon() {
		// TODO Auto-generated method stub
		return localSelIcon;
	}

	/**
	 * Method which adds the rollover text for the messages in AbstractDialog
	 * extended classes.
	 * 
	 * @param rolloverText
	 */
	public void addRolloverText(String rolloverText) {
		if (father != null) {
			this.rollOverText = rolloverText;
			localLbl.removeMouseListener(rolloverListener);
			localBttn.removeMouseListener(rolloverListener);

			localLbl.addMouseListener(rolloverListener);
			localBttn.addMouseListener(rolloverListener);
		}
	}

	/**
	 * Listener which sets the message of the father component.
	 */
	private MouseListener rolloverListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (father != null)
				father.setHint(rollOverText);

		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (father != null)
				father.setHint(null);

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Returns the label of the link.
	 * 
	 * @return
	 */
	public JLabel getLocalLbl() {
		// TODO Auto-generated method stub
		return localLbl;
	}

}