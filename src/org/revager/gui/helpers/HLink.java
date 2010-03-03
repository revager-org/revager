package org.revager.gui.helpers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.revager.gui.AbstractDialogPanel;
import org.revager.tools.GUITools;

@SuppressWarnings("serial")
public class HLink extends JPanel{
	
	private String localTextStrng;
	private JLabel localLbl;
	private ImageIcon localIcon;
	private ImageIcon localSelIcon;
	private ImageIcon localDisIcon;
	private ImageIcon localRolloverIcon;
	private JButton localBttn;
	private GridBagLayout gbl=new GridBagLayout();
	private Font localFont=new Font(Font.DIALOG,Font.PLAIN, 11);
	private JPanel localPnl= this;
	private ActionListener localAction=null;
	private LinkGroup localGroup;
	private Boolean selected=false;
	private AbstractDialogPanel father=null;
	private String rollOverText="";
	
	public AbstractDialogPanel getFather() {
		return father;
	}

	public void setFather(AbstractDialogPanel parent) {
		this.father = parent;
	}
	
	public Boolean getSelected() {
		return selected;
	}

	private MouseListener selectionListener=new MouseListener(){

		@Override
		public void mouseClicked(MouseEvent e) {
			if(localGroup!=null)
				localGroup.deselectAllLinks();
			if(!selected)
				selected=true;
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
			
		};};
		
	private MouseListener localListener=new MouseListener(){

		@Override
		public void mouseClicked(MouseEvent e) {
			if(localGroup==null){
				localLbl.setForeground(Color.BLUE);
				localBttn.setSelected(true);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setBold(true);
			}else{
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
			if(localGroup==null){
				localLbl.setForeground(Color.BLUE);
				localBttn.setSelected(true);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setBold(true);
			}else{
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setUnderlined(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(localGroup==null){
				localLbl.setForeground(Color.BLACK);
				localBttn.setSelected(false);
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setBold(false);
			}else{
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
			
		}};
		
		public ImageIcon getLocalDisIcon() {
			return localDisIcon;
		}

		public void setLocalDisIcon(ImageIcon localDisIcon) {
			this.localDisIcon = localDisIcon;
		}
	
		public JButton getLocalBttn() {
			return localBttn;
		}
	/**
	 * Sets the text of the link
	 * 
	 * @param localTextStrng
	 */
	public void setLocalTextStrng(String localTextStrng) {
		this.localTextStrng = localTextStrng;
	}

	/**
	 * Sets the icon of the link
	 * 
	 * @param localIcon
	 */
	public void setLocalIcon(ImageIcon localIcon) {
		this.localIcon = localIcon;
		localBttn.setIcon(localIcon);
	}

	/**
	 * sets the local rollover icon of the link
	 * 
	 * @param localRolloverIcon
	 */
	public void setLocalRolloverIcon(ImageIcon localRolloverIcon) {
		this.localRolloverIcon = localRolloverIcon;
		localBttn.setRolloverIcon(localRolloverIcon);
	}
	
	public ImageIcon getLocalIcon() {
		return localIcon;
	}

	public ImageIcon getLocalRolloverIcon() {
		return localRolloverIcon;
	}
	
	/**
	 * sets the text underlined if isUnderlined is true
	 * 
	 * @param isUnderlined
	 */
	public void setUnderlined(Boolean isUnderlined) {
		if(isUnderlined)
			localLbl.setText("<html><U>"+localTextStrng+"</html>");
		else
			localLbl.setText(localTextStrng);
	}
	
	/**
	 * sets the text italic if isItalic is true
	 * 
	 * @param isItalic
	 */
	public void setItalic(Boolean isItalic) {
		if(isItalic)
			localFont=new Font(Font.DIALOG,Font.ITALIC, 11);
		else
			localFont=new Font(Font.DIALOG,Font.PLAIN, 11);
	
	}
	
	/**
	 * sets the text bold if isBold is true
	 * 
	 * @param isBold
	 */
	public void setBold(Boolean isBold) {
		if(isBold){
			localFont=new Font(Font.DIALOG,Font.BOLD, 11);
			localLbl.setFont(localFont);
			localLbl.validate();
			localLbl.repaint();
			this.validate();
			this.repaint();
		}
		else{
			localFont=new Font(Font.DIALOG,Font.PLAIN, 11);
			localLbl.setFont(localFont);
			localLbl.validate();
			localLbl.repaint();
			this.validate();
			this.repaint();
			
			}
		}
	
	/**
	 * sets the color of the text
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		localLbl.setForeground(color);
	}

	/**
	 * constructor
	 * 
	 * @param text
	 * @param icon
	 * @param rolloverIcon
	 */
	public HLink(String text,ImageIcon icon,ImageIcon rolloverIcon, LinkGroup group){
		super();
		this.setLayout(gbl);
		this.localGroup=group;
		localIcon=icon;
		localRolloverIcon=rolloverIcon;
		localSelIcon=rolloverIcon;
		localDisIcon=icon;
		if(group==null)
			localBttn=GUITools.newImageButton(localIcon, localRolloverIcon);
		else
			localBttn=GUITools.newImageButton(localIcon, localIcon);
		
		localTextStrng=text;
		localLbl= new JLabel(localTextStrng);
		
		
		
		this.addMouseListener(localListener);
		localLbl.addMouseListener(localListener);
		localBttn.addMouseListener(localListener);
		localBttn.addMouseListener(selectionListener);
		localLbl.addMouseListener(selectionListener);
		
		GUITools.addComponent(localPnl, gbl, localBttn, 0, 0, 1, 1, 0.0, 0.0, 0, 0, 0, 0, GridBagConstraints.NONE,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(localPnl, gbl, localLbl, 1, 0, 1, 1, 1.0, 0.0, 0, 5, 0, 0, GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST);
	
	}

	public void addActionListener(ActionListener action) {
		localAction=action;
		localBttn.addActionListener(action);
		
		localLbl.addMouseListener(new MouseListener(){

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
				
			}});
	}

	public void setSelected(boolean b) {
		selected=b;
	}

	public ImageIcon getLocalSelIcon() {
		// TODO Auto-generated method stub
		return localSelIcon;
	}
	
	public void addRolloverText(String rolloverText){
		if(father!=null){
			this.rollOverText=rolloverText;
			localLbl.removeMouseListener(rolloverListener);
			localBttn.removeMouseListener(rolloverListener);
			
			localLbl.addMouseListener(rolloverListener);
			localBttn.addMouseListener(rolloverListener);
		}
	}
	
	private MouseListener rolloverListener=new MouseListener(){

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if(father!=null)
				father.setHint(rollOverText);
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(father!=null)
				father.setHint(null);
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}};

	public JLabel getLocalLbl() {
		// TODO Auto-generated method stub
		return localLbl;
	}
	
}	