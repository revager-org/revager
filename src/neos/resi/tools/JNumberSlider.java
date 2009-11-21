package neos.resi.tools;

import java.awt.*; 
import java.awt.event.*; 
import java.text.DecimalFormat;

import javax.swing.*; 
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import neos.resi.app.model.Data;
 
@SuppressWarnings("serial")
public class JNumberSlider extends JPanel{
	
	private JFormattedTextField tf = new JFormattedTextField(new DecimalFormat("###"));
	
	private JSlider sb; 
	
	private JButton decreaseBttn;
	private JButton increaseBttn;
	
	private int minLocal;
	private int maxLocal;
	
	private GridBagLayout gbl = new GridBagLayout();;
	
	public JNumberSlider(int value, int min, int max, int caret){
	  super();
	  
	  minLocal=min;
	  maxLocal=max;
	  
	  decreaseBttn = GUITools.newImageButton(Data.getInstance().getIcon("minus_15x15.png"), Data.getInstance().getIcon("minus_15x15.png"));
	  decreaseBttn.setPressedIcon(Data.getInstance().getIcon("minus_15x15_pressed.png"));
	  decreaseBttn.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			if(Integer.parseInt(tf.getText())>minLocal){
			String value=Integer.toString(Integer.parseInt(tf.getText())-1);
			tf.setText(value);
			sb.setValue(Integer.parseInt(value));
			}
		}});
	  increaseBttn = GUITools.newImageButton(Data.getInstance().getIcon("plus_15x15.png"), Data.getInstance().getIcon("plus_15x15.png"));
	  increaseBttn.setPressedIcon(Data.getInstance().getIcon("plus_15x15_pressed.png"));
	  increaseBttn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(maxLocal>Integer.parseInt(tf.getText())){
				String value=Integer.toString(Integer.parseInt(tf.getText())+1);
				tf.setText(value);
				sb.setValue(Integer.parseInt(value));
				}
			}});
	  
	  JPanel buttonPanel = new JPanel(new GridLayout(2,1));
	  buttonPanel.add(increaseBttn);
	  buttonPanel.add(decreaseBttn);
	  
	  
	  sb = new JSlider( min, max, value );
	  sb.setSize(50,15);

	  setLayout( gbl );
	  
	  GUITools.addComponent(this, gbl, tf,          1, 1, 1, 1, 1.0, 1.0, 0, 0, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
	  GUITools.addComponent(this, gbl, buttonPanel, 2, 1, 1, 1,   0,   0, 0, 0, 1, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHEAST);
	  GUITools.addComponent(this, gbl, sb,          1, 2, 2, 1, 1.0, 1.0, 0, 0, 1, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
	  
	  tf.setText(Integer.toString(value));
	  tf.setCaretPosition(caret);
 
	  tf.addActionListener( new ActionListener() { 
		  public void actionPerformed( ActionEvent e ) { 
			  sb.setValue( Integer.parseInt(tf.getText()) ); 
		} 
	  } ); 
 
	  sb.addChangeListener(new ChangeListener() { 
		  
		@Override
		public void stateChanged(ChangeEvent e) {
			tf.setText( "" + sb.getValue() ); 
		} 
	  } ); 
  	} 
  
  public int getValue(){
	  return Integer.parseInt(tf.getText());
  }
}