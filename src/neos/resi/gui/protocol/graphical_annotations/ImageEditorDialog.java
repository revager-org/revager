package neos.resi.gui.protocol.graphical_annotations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import neos.resi.app.model.Data;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.TextPopupWindow;
import neos.resi.gui.UI;
import neos.resi.gui.TextPopupWindow.ButtonClicked;
import neos.resi.tools.AppTools;
import neos.resi.tools.GUITools;

public class ImageEditorDialog extends AbstractDialog {
	private static final long serialVersionUID = 1L;

	private File fileImage = null;

	private ImageEditorPanel panelImage = null;

	private final int DEFAULT_THICKNESS = 5;
	private final int MAX_THICKNESS = 14;

	private JPanel panelThicknessPreview = new JPanel();
	private JSlider sliderThickness = null;

	private GridBagLayout gblThickness = new GridBagLayout();
	private JPanel panelThickness = new JPanel(gblThickness);

	private JButton buttonUndo;
	private JButton buttonRedo;

	private JToggleButton buttonEllipse;
	private JToggleButton buttonRectangle;
	private JToggleButton buttonArrow;
	private JToggleButton buttonText;
	private ButtonGroup buttonGroup;

	private String currentText = "";
	private Color currentColor = Color.RED;
	private int currentThickness = DEFAULT_THICKNESS;

	private JPanel buttonColor;
	private JButton buttonCancel;
	private JButton buttonConfirm;

	public ImageEditorDialog(Frame parent, File fileImage) {
		super(parent);

		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);

				panelImage.restore();

				updateUndoRedoButtons();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
		});

		setTitle(Data.getInstance().getLocaleStr("graphicalEditor.title"));

		ImageAnnotation annotation = ImageAnnotation.newEllipseAnnotation(
				currentColor, DEFAULT_THICKNESS);

		this.fileImage = fileImage;

		BufferedImage image = null;

		try {
			image = ImageIO.read(fileImage);
		} catch (IOException e) {
			// TODO Handle this!
		}

		panelImage = new ImageEditorPanel(image);
		panelImage.setCurrentAnnotation(annotation);
		panelImage.setBorder(new MatteBorder(1, 1, 1, 1, UI.SEPARATOR_COLOR));
		panelImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				updateUndoRedoButtons();
			}
		});

		createToolbar();

		updateUndoRedoButtons();

		run();
	}

	public void run() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		GridBagLayout gbl = new GridBagLayout();
		JPanel panelGbl = new JPanel(gbl);
		panelGbl.setBackground(Color.WHITE);

		ComponentStrut compStrut = new ComponentStrut();

		GUITools.addComponent(panelGbl, gbl, panelImage, 2, 2, 1, 1, 0.0, 0.0,
				0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

		GUITools.addComponent(panelGbl, gbl, compStrut, 1, 1, 3, 1, 1.0, 1.0,
				0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		GUITools.addComponent(panelGbl, gbl, compStrut, 1, 3, 3, 1, 1.0, 1.0,
				0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		GUITools.addComponent(panelGbl, gbl, compStrut, 1, 2, 1, 1, 1.0, 0.0,
				0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		GUITools.addComponent(panelGbl, gbl, compStrut, 3, 2, 1, 1, 1.0, 0.0,
				0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

		JScrollPane scrollImage = new JScrollPane(panelGbl);
		scrollImage.getVerticalScrollBar().setUnitIncrement(12);
		scrollImage.getHorizontalScrollBar().setUnitIncrement(12);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollImage);

		pack();

		setSize(new Dimension(600, 500));

		updateThicknessInfo();

		/*
		 * SwingUtilities.invokeLater(new Runnable() { public void run() {
		 * setVisible(true); } });
		 */
	}

	public void writeImageToFile() {
		AppTools.writeBufferedImageToFile(panelImage.getImage(), fileImage);
	}

	private void createToolbar() {
		buttonUndo = GUITools.newImageButton(Data.getInstance().getIcon(
				"undo_50x50_0.png"), Data.getInstance().getIcon(
				"undo_50x50.png"));
		buttonUndo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelImage.undo();
				updateUndoRedoButtons();
			}
		});
		buttonUndo.setToolTipText(Data.getInstance().getLocaleStr(
				"graphicalEditor.undo"));

		buttonRedo = GUITools.newImageButton(Data.getInstance().getIcon(
				"redo_50x50_0.png"), Data.getInstance().getIcon(
				"redo_50x50.png"));
		buttonRedo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelImage.redo();
				updateUndoRedoButtons();
			}
		});
		buttonRedo.setToolTipText(Data.getInstance().getLocaleStr(
				"graphicalEditor.redo"));

		buttonGroup = new ButtonGroup();

		buttonRectangle = GUITools.newImageToggleButton(Data.getInstance()
				.getIcon("rectangle_50x50_0.png"), Data.getInstance().getIcon(
				"rectangle_50x50.png"), null);
		buttonEllipse = GUITools.newImageToggleButton(Data.getInstance()
				.getIcon("ellipse_50x50_0.png"), Data.getInstance().getIcon(
				"ellipse_50x50.png"), null);
		buttonArrow = GUITools.newImageToggleButton(Data.getInstance().getIcon(
				"arrow_50x50_0.png"), Data.getInstance().getIcon(
				"arrow_50x50.png"), null);
		buttonText = GUITools.newImageToggleButton(Data.getInstance().getIcon(
				"text_50x50_0.png"), Data.getInstance().getIcon(
				"text_50x50.png"), null);

		ActionListener thiknessEnActionL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableThickness(true);
			}
		};

		ActionListener thiknessDisActionL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableThickness(false);
			}
		};

		buttonRectangle.addActionListener(thiknessEnActionL);
		buttonEllipse.addActionListener(thiknessEnActionL);
		buttonArrow.addActionListener(thiknessDisActionL);
		buttonText.addActionListener(thiknessDisActionL);

		buttonRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelImage
						.setCurrentAnnotation(ImageAnnotation
								.newRectangleAnnotation(currentColor,
										currentThickness));
			}
		});

		buttonEllipse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelImage.setCurrentAnnotation(ImageAnnotation
						.newEllipseAnnotation(currentColor, currentThickness));
			}
		});

		buttonArrow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelImage.setCurrentAnnotation(ImageAnnotation
						.newArrowAnnotation(currentColor));
			}
		});

		buttonText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextPopupWindow popup = new TextPopupWindow(
						getImageEditorDialog(), Data.getInstance()
								.getLocaleStr("graphicalEditor.enterText"),
						currentText, false);

				popup.setVisible(true);

				if (popup.getButtonClicked() == ButtonClicked.OK) {
					currentText = popup.getInput();
				}

				panelImage.setCurrentAnnotation(ImageAnnotation
						.newTextAnnotation(currentColor, currentText));
			}
		});

		buttonEllipse.setSelected(true);

		buttonGroup.add(buttonEllipse);
		buttonGroup.add(buttonRectangle);
		buttonGroup.add(buttonArrow);
		buttonGroup.add(buttonText);

		panelThicknessPreview.setPreferredSize(new Dimension(80,
				DEFAULT_THICKNESS));
		panelThicknessPreview.setBackground(currentColor);

		sliderThickness = new JSlider(JSlider.VERTICAL, 1, MAX_THICKNESS,
				DEFAULT_THICKNESS);
		registerInToolTipManager(sliderThickness);

		sliderThickness.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				currentThickness = sliderThickness.getValue();

				panelImage.getCurrentAnnotation()
						.setThickness(currentThickness);

				updateThicknessInfo();
			}
		});
		sliderThickness.setPreferredSize(new Dimension((int) sliderThickness
				.getPreferredSize().getWidth(), 50));
		sliderThickness.setBackground(Color.WHITE);

		GUITools.addComponent(panelThickness, gblThickness,
				panelThicknessPreview, 2, 2, 1, 1, 0.0, 0.0, 0, 5, 0, 5,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER);

		GUITools.addComponent(panelThickness, gblThickness,
				new ComponentStrut(), 1, 1, 3, 1, 1.0, 1.0, 0, 0, 0, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		GUITools.addComponent(panelThickness, gblThickness,
				new ComponentStrut(), 1, 3, 3, 1, 1.0, 1.0, 0, 0, 0, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		GUITools.addComponent(panelThickness, gblThickness,
				new ComponentStrut(), 1, 2, 1, 1, 1.0, 0.0, 0, 0, 0, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		GUITools.addComponent(panelThickness, gblThickness,
				new ComponentStrut(), 3, 2, 1, 1, 1.0, 0.0, 0, 0, 0, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER);

		panelThickness.setBackground(Color.WHITE);
		registerInToolTipManager(panelThickness);

		buttonColor = new JPanel();
		buttonColor.setToolTipText(Data.getInstance().getLocaleStr(
				"graphicalEditor.color"));
		buttonColor.setPreferredSize(new Dimension(32, 32));
		buttonColor.setBackground(currentColor);
		buttonColor.setBorder(new MatteBorder(2, 2, 2, 2, Color.GRAY));
		buttonColor.setCursor(new Cursor(Cursor.HAND_CURSOR));
		buttonColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color selectedColor = JColorChooser.showDialog(
						getImageEditorDialog(), Data.getInstance()
								.getLocaleStr("graphicalEditor.chooseColor"),
						currentColor);

				if (selectedColor != null) {
					buttonColor.setBackground(selectedColor);
					currentColor = selectedColor;

					if (panelThicknessPreview.isEnabled()) {
						if (currentColor.equals(Color.WHITE)) {
							panelThicknessPreview
									.setBackground(Color.LIGHT_GRAY);
						} else {
							panelThicknessPreview.setBackground(currentColor);
						}
					}

					panelImage.getCurrentAnnotation().setColor(currentColor);
				}
			}
		});

		JPanel panelTop = new JPanel(new BorderLayout());

		JPanel panelTopLeft = new JPanel(new FlowLayout());
		panelTopLeft.setBackground(Color.WHITE);

		JPanel panelTopCenter = new JPanel(new FlowLayout());
		panelTopCenter.setBackground(Color.WHITE);

		JPanel panelTopRight = new JPanel(new FlowLayout());
		panelTopRight.setBackground(Color.WHITE);

		panelTopLeft.add(buttonUndo);
		panelTopLeft.add(buttonRedo);
		panelTopCenter.add(buttonEllipse);
		panelTopCenter.add(buttonRectangle);
		panelTopCenter.add(buttonArrow);
		panelTopCenter.add(buttonText);
		panelTopRight.add(buttonColor);
		panelTopRight.add(new ToolBarSeparator());
		panelTopRight.add(sliderThickness);
		panelTopRight.add(panelThickness);

		panelTop.add(panelTopLeft, BorderLayout.WEST);
		panelTop.add(panelTopCenter, BorderLayout.CENTER);
		panelTop.add(panelTopRight, BorderLayout.EAST);
		setTopPanel(panelTop);

		buttonCancel = new JButton(Data.getInstance().getLocaleStr("abort"),
				Data.getInstance().getIcon("buttonCancel_16x16.png"));
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);

				panelImage.restore();

				updateUndoRedoButtons();
			}
		});

		addButton(buttonCancel);

		buttonConfirm = new JButton(Data.getInstance().getLocaleStr("confirm"),
				Data.getInstance().getIcon("buttonOk_16x16.png"));
		buttonConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		addButton(buttonConfirm);
	}

	private void enableThickness(boolean enable) {
		sliderThickness.setEnabled(enable);

		panelThicknessPreview.setEnabled(enable);

		if (enable) {
			panelThicknessPreview.setBackground(currentColor);
		} else {
			panelThicknessPreview.setBackground(Color.LIGHT_GRAY);
		}

		updateThicknessInfo();
	}

	private void updateThicknessInfo() {
		panelThicknessPreview.setPreferredSize(new Dimension(
				(int) panelThicknessPreview.getSize().getWidth(),
				sliderThickness.getValue()));
		panelThicknessPreview.revalidate();

		String tooltip = Data.getInstance().getLocaleStr(
				"graphicalEditor.thickness")
				+ ": " + Integer.toString(sliderThickness.getValue()) + " px";

		panelThickness.setToolTipText(tooltip);
		sliderThickness.setToolTipText(tooltip);
	}

	private void updateUndoRedoButtons() {
		buttonUndo.setEnabled(panelImage.isUndoPossible());
		buttonRedo.setEnabled(panelImage.isRedoPossible());
	}

	private ImageEditorDialog getImageEditorDialog() {
		return this;
	}

	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			panelImage.backup();
			setLocationToCenter();
		}

		super.setVisible(vis);
	}

	private static void registerInToolTipManager(JComponent c) {
		// ensure InputMap and ActionMap are created
		InputMap imap = c.getInputMap();
		/* ActionMap amap = c.getActionMap(); */
		// put dummy KeyStroke into InputMap if is empty:
		boolean removeKeyStroke = false;
		KeyStroke[] ks = imap.keys();
		if (ks == null || ks.length == 0) {
			imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0),
					"backSlash");
			removeKeyStroke = true;
		}
		// now we can register by ToolTipManager
		ToolTipManager.sharedInstance().registerComponent(c);
		// and remove dummy KeyStroke
		if (removeKeyStroke) {
			imap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0));
		}
		// now last part - add appropriate MouseListener and
		// hear to mouseEntered events
		c.addMouseListener(MOUSE_HANDLER);
		c.addMouseMotionListener(MOUSE_HANDLER);
	}

	private static MouseHandler MOUSE_HANDLER = new MouseHandler();

	// implementation of MouseHandler
	private static class MouseHandler extends MouseAdapter implements
			MouseMotionListener {
		private void performAction(MouseEvent e) {
			JComponent c = (JComponent) e.getComponent();
			Action action = c.getActionMap().get("postTip");
			// it is also possible to use own Timer to display
			// ToolTip with custom delay, but here we just
			// display it immediately
			if (action != null) {
				action.actionPerformed(new ActionEvent(c,
						ActionEvent.ACTION_PERFORMED, "postTip"));
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			performAction(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			performAction(e);
		}
	}

	private class ComponentStrut extends JComponent {
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getSize() {
			return new Dimension(0, 0);
		}

		@Override
		public Dimension getMinimumSize() {
			return getSize();
		}

		@Override
		public Dimension getPreferredSize() {
			return getSize();
		}

		@Override
		public Dimension getMaximumSize() {
			return getSize();
		}
	};

	private class ToolBarSeparator extends JPanel {
		private static final long serialVersionUID = 1L;

		public ToolBarSeparator() {
			super();

			setBackground(Color.WHITE);

			setLayout(new BorderLayout());

			JPanel strutLeft = new JPanel();
			strutLeft.setPreferredSize(new Dimension(15, 50));
			strutLeft.setBackground(Color.WHITE);

			JPanel strutRight = new JPanel();
			strutRight.setPreferredSize(new Dimension(15, 50));
			strutRight.setBackground(Color.WHITE);

			JPanel separator = new JPanel();
			separator.setPreferredSize(new Dimension(1, 50));
			separator.setBackground(Color.GRAY);

			add(strutLeft, BorderLayout.WEST);
			add(separator, BorderLayout.CENTER);
			add(strutRight, BorderLayout.EAST);
		}
	}

}
