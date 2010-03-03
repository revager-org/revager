package org.revager.gui.helpers;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.LineMetrics;

import javax.swing.JLabel;

public class LinkLabel extends JLabel{
	private String localText;
	public LinkLabel(String text){
		super();
		localText=text;
		setText(localText);
	}
	  @Override
	  public void paint(Graphics g)
	  {
		drawUnderlinedString(g, 0, 0,localText);
	  }
	public void drawUnderlinedString( Graphics g, int x, int y, String s ) 
	{ 
	  g.drawString( s, x, y ); 
	 
	  FontMetrics fm = g.getFontMetrics(); 
	  LineMetrics lm = fm.getLineMetrics( s, g ); 
	 
	  g.fillRect( x, y + (int) lm.getUnderlineOffset(), 
	              fm.stringWidth(s), (int) lm.getUnderlineThickness() ); 
	}
}