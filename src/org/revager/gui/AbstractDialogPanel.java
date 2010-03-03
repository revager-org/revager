package org.revager.gui;

import java.awt.Panel;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AbstractDialogPanel extends Panel{
	
	private AbstractDialog parent;

	public AbstractDialog getParent() {
		return parent;
	}

	public AbstractDialogPanel(AbstractDialog parent) {
		
		super();
		
		this.parent = parent; 
		
	}

	public void setHint(String hintText) {
		
		parent.setMessage(hintText); 
		
	}
	
}
