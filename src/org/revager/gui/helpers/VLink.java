package org.revager.gui.helpers;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.revager.gui.AbstractDialogPanel;
import org.revager.tools.GUITools;

@SuppressWarnings("serial")
public class VLink extends JPanel{
	
	private String localTextStrng;
	private JLabel localLbl;
	private ImageIcon localIcon;
	private ImageIcon localRolloverIcon;
	private JButton localBttn;
	private GridBagLayout gbl=new GridBagLayout();
	private Font localFont=new Font(Font.DIALOG,Font.PLAIN, 12);
	private Font boldFont=new Font(Font.DIALOG,Font.BOLD, 12);
	private JPanel localPnl=this;
	private ActionListener localAction=null;
	private AbstractDialogPanel father=null;
	private String rollOverText="";
	
	
	public AbstractDialogPanel getFather() {
		return father;
	}

	
	public void setFather(AbstractDialogPanel parent) {
		this.father = parent;
	}
	
	private MouseListener localListener=new MouseListener(){

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			localLbl.setForeground(Color.BLUE);
			localBttn.setSelected(true);
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			setBold(true);
			localLbl.revalidate();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			localLbl.setForeground(Color.BLACK);
			localBttn.setSelected(false);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			setBold(false);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}};
	
	/**
	 * sets the text of the link
	 * 
	 * @param localTextStrng
	 */
	public void setLocalTextStrng(String localTextStrng) {
		this.localTextStrng = localTextStrng;
	}

	/**
	 * sets the icon of the link
	 * 
	 * @param localIcon
	 */
	public void setLocalIcon(ImageIcon localIcon) {
		this.localIcon = localIcon;
	}

	/**
	 * sets the rollover icon of the link
	 * 
	 * @param localRolloverIcon
	 */
	public void setLocalRolloverIcon(ImageIcon localRolloverIcon) {
		this.localRolloverIcon = localRolloverIcon;
	}
	
	/**
	 * sets the link underlined if isUnderlined is true
	 * 
	 * @param isUnderlined
	 */
	public void setUnderlined(Boolean isUnderlined) {
		//TODO
	}
	
	public void setItalic(Boolean isItalic) {
		if(isItalic)
			localFont=new Font(Font.DIALOG,Font.ITALIC, 12);
		else
			localFont=new Font(Font.DIALOG,Font.PLAIN, 12);
	
	}
	
	public void setBold(Boolean isBold) {
		if(isBold){
			localFont=new Font(Font.DIALOG,Font.BOLD, 12);
			localLbl.setFont(localFont);
			this.validate();
			localLbl.repaint();
		}
		else{
			localFont=new Font(Font.DIALOG,Font.PLAIN, 12);
			localLbl.setFont(localFont);
			this.validate();
			localLbl.repaint();
			}
		}
	
	public void setColor(Color color) {
		localLbl.setForeground(color);
	}

	public VLink(String text,ImageIcon icon,ImageIcon rolloverIcon){
		super();
		this.setLayout(gbl);
		localIcon=icon;
		localRolloverIcon=rolloverIcon;
		localBttn=GUITools.newImageButton(localIcon, localRolloverIcon);
		localTextStrng=text;
		localLbl=new JLabel("<html><U>"+localTextStrng+"</html>");
		localLbl.setFont(boldFont);
		Dimension prefSize=localLbl.getPreferredSize();
		localLbl.setFont(localFont);
		localLbl.setMinimumSize(prefSize);
		
		
		this.addMouseListener(localListener);
		localLbl.addMouseListener(localListener);
		localBttn.addMouseListener(localListener);

		
		GUITools.addComponent(localPnl, gbl, localBttn, 0, 0, 1, 1, 0.0, 0.0, 0, 0, 0, 0, GridBagConstraints.NONE,GridBagConstraints.NORTH);
		GUITools.addComponent(localPnl, gbl, localLbl, 0, 1, 1, 1, 0.0, 0.0, 5, 0, 0, 0, GridBagConstraints.NONE,GridBagConstraints.NORTH);
		
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
	
	public void addRolloverText(String rolloverText){
		if(father!=null){
			this.rollOverText=rolloverText;
			this.removeMouseListener(rolloverListener);
			localLbl.removeMouseListener(rolloverListener);
			localBttn.removeMouseListener(rolloverListener);
			this.addMouseListener(rolloverListener);
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
	
}
