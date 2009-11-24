package neos.resi.tools;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import neos.resi.app.model.Data;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.TextPopupWindow;
import neos.resi.gui.TextPopupWindow.ButtonClicked;

@SuppressWarnings("serial")
public class GraphicalEditor extends AbstractDialog {
	
	private GridBagLayout gbl=new GridBagLayout();
	
	private JPanel imagePanel;
	private JPanel interactionPanel;
	
	private JToggleButton ellipseButton;
	private JToggleButton quadrateButton;
	private JToggleButton arrowButton;
	private JToggleButton textButton;
	private ButtonGroup buttonGr;
	
	private int b_h;
	
	private JSlider sizeSlider;
	private JSlider thiknessSlider;
	
	private Color color=Color.RED;
	private BasicStroke stroke;
	
	
	private JButton colorBttn;
	
	private JButton cancelBttn;

	private JButton confirmBttn;
	
	private String text;
	
	private JLabel sizeLabel=new JLabel(Data.getInstance().getLocaleStr("graphicalEditor.size"));
	private JLabel thiknessLabel=new JLabel(Data.getInstance().getLocaleStr("graphicalEditor.thikness"));
	private JLabel colorLabel=new JLabel(Data.getInstance().getLocaleStr("graphicalEditor.color"));
	private JLabel size;
	private JLabel thikness;
	
	private BufferedImage image = null;

	private JScrollPane imageScrllPn= new JScrollPane();
	
	private Stack<Color> colors = new Stack<Color>();
	private Stack<Color> text_colors = new Stack<Color>();
	private Stack<RectangularShape> shapes = new Stack<RectangularShape>();
	private Stack<BasicStroke> thiknesses = new Stack<BasicStroke>();
	private Stack<Integer> text_size = new Stack<Integer>();
	
	private Stack<String> texts = new Stack<String>();
	private Stack<Integer> x_coordinates= new Stack<Integer>();
	private Stack<Integer> y_coordinates= new Stack<Integer>();

	
	
	private GraphicalEditor getGraphicalEditor() {
		return this;
	} 
	
	public GraphicalEditor(Frame parent, Image img) {
		super(parent);
		image=(BufferedImage)img;
		setLayout(gbl);
		imagePanel=new JPanel() {
			
			

			{
				


				addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						b_h=sizeSlider.getValue();
						stroke=new BasicStroke(thiknessSlider.getValue());
						if(ellipseButton.isSelected()){
							if (e.getButton() == MouseEvent.BUTTON1) {
								Ellipse2D ellipse = new Ellipse2D.Double();
								ellipse.setFrame(e.getX()-b_h/2, e.getY()-b_h/2, b_h, b_h);
								shapes.push(ellipse);
								colors.push(color);
								thiknesses.push(stroke);
							} else {
								if (!shapes.isEmpty())
									shapes.pop();
									colors.pop();
									thiknesses.pop();
							}
						}else if(quadrateButton.isSelected()){
							if (e.getButton() == MouseEvent.BUTTON1) {
								Rectangle2D rectangle = new Rectangle2D.Double();
								rectangle.setFrame(e.getX()-b_h/2, e.getY()-b_h/2, b_h, b_h);
								shapes.push(rectangle);
								colors.push(color);
								thiknesses.push(stroke);
							} else {
								if (!shapes.isEmpty()){
									shapes.pop();
									colors.pop();
									thiknesses.pop();
								}
							}
						}else if(arrowButton.isSelected()){
							if (e.getButton() == MouseEvent.BUTTON1) {
								Arrow2D arrow = new Arrow2D();
								arrow.setFrame(e.getX()-b_h, e.getY()-b_h/2, b_h, b_h);
								shapes.push(arrow);
								colors.push(color);
								thiknesses.push(stroke);
							} else {
								if (!shapes.isEmpty()){
									shapes.pop();
									colors.pop();
									thiknesses.pop();
								}
							}
						}else if(textButton.isSelected()){
							if (e.getButton() == MouseEvent.BUTTON1) {
								x_coordinates.push(e.getX());
								y_coordinates.push(e.getY());
								texts.push(text);
								text_colors.push(color);
								text_size.push(sizeSlider.getValue());
							} else {
								if (!texts.isEmpty()){
									x_coordinates.pop();
									y_coordinates.pop();
									texts.pop();
									text_colors.pop();
									text_size.pop();
								}
							}
						}
						repaint();
					}
					
				}
				
				);

				/*addMouseMotionListener(new MouseAdapter() {
					public void mouseDragged(MouseEvent e) {
						
						if (e.getButton() == MouseEvent.BUTTON1) {
								RectangularShape shape =shapes.peek();
								shape.setFrame(shape.getX(), shape.getY(), Math.abs(shape.getX() - e.getX()), Math
										.abs(shape.getY() - e.getY()));
								//revalidate();
								//repaint(); 
							}
						}
				});*/

			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(image.getWidth(), image.getHeight());
			}

			@Override
			protected void paintComponent(Graphics g) {
				
				Graphics2D g2 = (Graphics2D) g;
				
				g2.drawImage(image, 0, 0, this);
				
				for (int index=0;index<shapes.size();index++) {
					
					g2.setColor(colors.get(index));
					if(shapes.get(index) instanceof Arrow2D)
						g2.fill(shapes.get(index));
					else
						g2.setStroke(thiknesses.get(index));
					g2.draw(shapes.get(index));
					
				}
				
				for(int index=0;index<texts.size();index++){
					g2.setColor(text_colors.get(index));
					int x_coordinate=x_coordinates.get(index);
					int y_coordinate=y_coordinates.get(index);
					g2.setFont(new Font("Dialog", Font.PLAIN, text_size.get(index)));
					g2.drawString(texts.get(index), x_coordinate, y_coordinate);
				}
				getGraphicalEditor().repaint();
			}
			
			

		};
		createToolbar();
		
		imageScrllPn.add(imagePanel);
		setMinimumSize(new Dimension(700, 500));
		
		GUITools.addComponent(this, gbl, imageScrllPn, 1, 1, 1, 1, 1.0, 1.0, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		
		
		if(image.getHeight()+171>Toolkit.getDefaultToolkit().getScreenSize().getHeight()
				&&image.getWidth()+40>Toolkit.getDefaultToolkit().getScreenSize().getWidth()){
			setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
			
		}else{
			setSize(new Dimension(image.getWidth()+40, image.getHeight()+171));
		}
		
		
		setVisible(true);

		
	}

	
	private void createToolbar(){
		interactionPanel=new JPanel(gbl);
		buttonGr = new ButtonGroup();
		
		quadrateButton=GUITools.newImageToggleButton(Data.getInstance().getIcon("quadrate_50x50_0.png"),Data.getInstance().getIcon("quadrate_50x50.png"), null);
		ellipseButton=GUITools.newImageToggleButton(Data.getInstance().getIcon("ellipse_50x50_0.png"),Data.getInstance().getIcon("ellipse_50x50.png"),null);
		arrowButton=GUITools.newImageToggleButton(Data.getInstance().getIcon("arrow_50x50_0.png"), Data.getInstance().getIcon("arrow_50x50.png"), null);
		textButton=GUITools.newImageToggleButton(Data.getInstance().getIcon("text_50x50_0.png"), Data.getInstance().getIcon("text_50x50.png"), null);
		
		ActionListener thiknessEnActionL=new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				enableThikness(true);
			}};
			
		ActionListener thiknessDisActionL=new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				enableThikness(false);
			}};
		quadrateButton.addActionListener(thiknessEnActionL);
		ellipseButton.addActionListener(thiknessEnActionL);
		arrowButton.addActionListener(thiknessDisActionL);
		textButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				enableThikness(false);
				
				TextPopupWindow popup = new TextPopupWindow(getGraphicalEditor(), Data.getInstance().getLocaleStr(
						"graphicalEditor.enterText"), "", true);

				popup.setVisible(true);
				
				
				if (popup.getButtonClicked() == ButtonClicked.OK) {
					text=popup.getInput();
				}
				
			}});
		ellipseButton.setSelected(true);
		
		buttonGr.add(ellipseButton);
		buttonGr.add(quadrateButton);
		buttonGr.add(arrowButton);
		buttonGr.add(textButton);
		
		sizeSlider=new JSlider();
		sizeSlider.setBackground(Color.WHITE);
		sizeSlider.setMinimum(1);
		sizeSlider.setMaximum(100);
		sizeSlider.setValue(50);
		sizeSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				size.setText(Integer.toString(sizeSlider.getValue()));
			}});
		size=new JLabel(Integer.toString(sizeSlider.getValue()));
		

		thiknessSlider=new JSlider();
		thiknessSlider.setBackground(Color.WHITE);
		thiknessSlider.setMinimum(1);
		thiknessSlider.setMaximum(20);
		thiknessSlider.setValue(10);
		thiknessSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				thikness.setText(Integer.toString(thiknessSlider.getValue()));
			}});
		thikness=new JLabel(Integer.toString(thiknessSlider.getValue()));
		
		colorBttn = new JButton();
		colorBttn.setSize(25,25);
		colorBttn.setBackground(Color.RED);
	    colorBttn.addActionListener( new ActionListener()
	    {
	      public void actionPerformed( ActionEvent e )
	      {
	        Color newColor = JColorChooser.showDialog(
	          null, Data.getInstance().getLocaleStr("graphicalEditor.chooseColor"), Color.RED );
	        colorBttn.setBackground( newColor );
	        color=newColor;
	        
	      }
	    } );
		GUITools.addComponent(interactionPanel, gbl, sizeLabel,           1, 1, 1, 1,   0,   0, 0, 0,  5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(interactionPanel, gbl, size,     			  2, 1, 1, 1,   0,   0, 0, 5,  5, 10, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(interactionPanel, gbl, sizeSlider,          1, 2, 2, 1,   0,   0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, thiknessLabel,       4, 1, 1, 1,   0,   0, 0,10,  5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(interactionPanel, gbl, thikness,            5, 1, 1, 1,   0,   0, 0,10,  5, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(interactionPanel, gbl, thiknessSlider,      4, 2, 2, 1,   0,   0, 0,10, 10, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, colorLabel,          6, 1, 1, 1,   0,   0, 0,10,  0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, colorBttn,           6, 2, 1, 1,   0,   0, 0,10, 10, 0, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		interactionPanel.setBackground(Color.WHITE);
		addTopComponent(ellipseButton);
	    addTopComponent(quadrateButton);
	    addTopComponent(arrowButton);
	    addTopComponent(textButton);
	    addTopComponent(interactionPanel);

	    
	    cancelBttn = new JButton(Data.getInstance().getLocaleStr("abort"),
				Data.getInstance().getIcon("buttonCancel_16x16.png"));
		cancelBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});

		addButton(cancelBttn);

		confirmBttn = new JButton(Data.getInstance()
				.getLocaleStr("confirm"), Data.getInstance().getIcon(
				"buttonOk_16x16.png"));
		confirmBttn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);
			}
			
		});

		addButton(confirmBttn);
		
		}
	
	private void enableThikness(boolean enable){
		thiknessSlider.setEnabled(enable);
		thikness.setEnabled(enable);
		thiknessLabel.setEnabled(enable);
	}

	public Image getImage() {
		return image;
	}

}
