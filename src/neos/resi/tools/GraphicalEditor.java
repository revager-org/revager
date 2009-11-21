package neos.resi.tools;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import neos.resi.app.model.Data;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.AbstractFrame;

@SuppressWarnings("serial")
public class GraphicalEditor extends AbstractFrame {
	
	private BorderLayout bl=new BorderLayout();
	private GridBagLayout gbl=new GridBagLayout();
	
	private JPanel imagePanel;
	private JPanel interactionPanel;
	
	private JToggleButton ellipseButton;
	private JToggleButton quadrateButton;
	private JToggleButton arrowButton;
	private ButtonGroup buttonGr;
	
	private int b_h;
	
	private JNumberSlider sizeSliderPanel;
	private JNumberSlider thiknessSliderPanel;
	
	private Color color=Color.RED;
	private BasicStroke stroke;
	
	
	private JButton colorBttn;
	
	private JLabel sizeLabel=new JLabel("size:");
	private JLabel thiknessLabel=new JLabel("thikness:");
	private JLabel colorLabel=new JLabel("color:");
	
	private BufferedImage image = null;
	
	
	public GraphicalEditor() {
		setLayout(bl);
		imagePanel=new JPanel() {
			Stack<Color> colors = new Stack<Color>();
			Stack<RectangularShape> shapes = new Stack<RectangularShape>();
			Stack<BasicStroke> thiknesses = new Stack<BasicStroke>();
			
			
			{
				try {
					image = ImageIO.read(new File("/home/jojo/Files/Media/Bilder/GGD Workshop-Tag/Ballons.jpg"));
				} catch (IOException e) {
				}

				addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						b_h=sizeSliderPanel.getValue();
						stroke=new BasicStroke(thiknessSliderPanel.getValue());
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
						}
						
						//revalidate();
						repaint(); 
					}

				});

				addMouseMotionListener(new MouseAdapter() {
					public void mouseDragged(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
								RectangularShape shape =shapes.peek();
								shape.setFrame(shape.getX(), shape.getY(), Math.abs(shape.getX() - e.getX()), Math
										.abs(shape.getY() - e.getY()));
								//revalidate();
								//repaint(); 
							}
						}
				});

			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(image.getWidth(), image.getHeight());
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(image, 50, 50, null);
				for (int index=0;index<shapes.size();index++) {
					g2.setColor(colors.get(index));
					g2.draw(shapes.get(index));
					g2.setStroke(thiknesses.get(index));
					if(shapes.get(index) instanceof Arrow2D)
						g2.fill(shapes.get(index));
				}
			}

		};
		
		add(imagePanel,BorderLayout.CENTER);
		createToolbar();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(image.getWidth()+150, image.getHeight()+300));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		pack();
		setVisible(true);

		
	}

	
	private void createToolbar(){
		interactionPanel=new JPanel(gbl);
		buttonGr = new ButtonGroup();
		
		quadrateButton=GUITools.newImageToggleButton(Data.getInstance().getIcon("quadrate_50x50_0.png"),Data.getInstance().getIcon("quadrate_50x50.png"), null);
		ellipseButton=GUITools.newImageToggleButton(Data.getInstance().getIcon("ellipse_50x50_0.png"),Data.getInstance().getIcon("ellipse_50x50.png"),null);
		arrowButton=GUITools.newImageToggleButton(Data.getInstance().getIcon("arrow_50x50_0.png"), Data.getInstance().getIcon("arrow_50x50.png"), null);
		ellipseButton.setSelected(true);
		
		buttonGr.add(ellipseButton);
		buttonGr.add(quadrateButton);
		buttonGr.add(arrowButton);
		
		sizeSliderPanel=new JNumberSlider(50,1,500,2);
		thiknessSliderPanel=new JNumberSlider(5,1,10,1);
		
		colorBttn = new JButton();
		colorBttn.setSize(25,25);
		colorBttn.setBackground(Color.RED);
	    colorBttn.addActionListener( new ActionListener()
	    {
	      public void actionPerformed( ActionEvent e )
	      {
	        Color newColor = JColorChooser.showDialog(
	          null, "Wähle neue Farbe", Color.RED );
	        colorBttn.setBackground( newColor );
	        color=newColor;
	        
	      }
	    } );
		
		GUITools.addComponent(interactionPanel, gbl, quadrateButton,      1, 1, 1, 2, 1.0, 1.0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, ellipseButton,       2, 1, 1, 2, 1.0, 1.0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, arrowButton,         3, 1, 1, 2, 1.0, 1.0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, sizeLabel,           4, 1, 1, 2,   0, 1.0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER);
		GUITools.addComponent(interactionPanel, gbl, sizeSliderPanel,     5, 1, 1, 2, 1.0,   0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, thiknessLabel,       6, 1, 1, 2,   0, 1.0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER);
		GUITools.addComponent(interactionPanel, gbl, thiknessSliderPanel, 7, 1, 1, 2, 1.0, 1.0, 0, 0, 10, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, colorLabel,          8, 1, 1, 1,   0,   0, 0, 0,  0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(interactionPanel, gbl, colorBttn,           8, 2, 1, 1, 1.0, 1.0, 0, 0, 20, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		
		
		add(interactionPanel, BorderLayout.NORTH);
		
		
		}

}
