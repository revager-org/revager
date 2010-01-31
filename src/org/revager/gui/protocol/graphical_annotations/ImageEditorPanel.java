package org.revager.gui.protocol.graphical_annotations;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JPanel;

public class ImageEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private BufferedImage image = null;

	private Stack<ImageAnnotation> annotations = new Stack<ImageAnnotation>();
	private Stack<ImageAnnotation> annotationsReverse = new Stack<ImageAnnotation>();

	private Stack<ImageAnnotation> annotationsBackup = new Stack<ImageAnnotation>();
	private Stack<ImageAnnotation> annotationsReverseBackup = new Stack<ImageAnnotation>();

	private ImageAnnotation currentAnnotation = null;

	private Point originPosition = null;

	private MouseMotionListener motionListener = new MouseMotionListener() {
		public void mouseMoved(MouseEvent e) {
			updateOriginPosition(e);

			currentAnnotation.setPosition(originPosition);

			repaint();
		}

		public void mouseDragged(MouseEvent e) {
			int width = (int) (e.getX() - originPosition.getX());
			int height = (int) (e.getY() - originPosition.getY());

			currentAnnotation.setSize(new Dimension(width, height));

			repaint();
		}
	};

	private MouseAdapter mouseListener = new MouseAdapter() {
		private boolean mouseInsidePanel = false;

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && mouseInsidePanel) {
				/*
				 * Clear redo stack
				 */
				annotationsReverse.clear();

				/*
				 * Remove currentAnnotation from stack
				 */
				annotations.pop();

				/*
				 * Push cloned instance of current annotation to stack
				 */
				annotations.push(currentAnnotation.cloneInstance());

				currentAnnotation.resetSize();

				updateOriginPosition(e);
				currentAnnotation.setPosition(originPosition);

				annotations.push(currentAnnotation);
			}

			repaint();
		};

		@Override
		public void mouseEntered(MouseEvent e) {
			mouseInsidePanel = true;

			ImageEditorPanel panelEditor = (ImageEditorPanel) e.getSource();

			panelEditor.addMouseMotionListener(motionListener);

			annotations.push(currentAnnotation);

			/*
			 * Set empty cursor
			 */
			// GraphicsEnvironment ge = GraphicsEnvironment
			// .getLocalGraphicsEnvironment();
			// GraphicsDevice gs = ge.getDefaultScreenDevice();
			// GraphicsConfiguration gc = gs.getDefaultConfiguration();
			//
			// BufferedImage imageCursor = gc.createCompatibleImage(1, 1,
			// Transparency.BITMASK);
			//
			// Toolkit toolkit = Toolkit.getDefaultToolkit();
			//
			// Cursor cur = toolkit.createCustomCursor(imageCursor,
			// new Point(0, 0), "img");
			//
			// setCursor(cur);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseInsidePanel = false;

			// currentAnnotation.resetSize();

			ImageEditorPanel panelEditor = (ImageEditorPanel) e.getSource();

			panelEditor.removeMouseMotionListener(motionListener);

			if (!annotations.isEmpty()) {
				annotations.pop();
			}

			repaint();
		}
	};

	public ImageEditorPanel(BufferedImage image) {
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		this.image = image;

		addMouseListener(mouseListener);
	}

	private void updateOriginPosition(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int width = (int) currentAnnotation.getSize().getWidth();
		int height = (int) currentAnnotation.getSize().getHeight();

		originPosition = new Point(x - width, y - height);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		paintImage(g2);
	}

	private void paintImage(Graphics2D g2) {
		/*
		 * Enable anti-aliasing
		 */
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		/*
		 * Draw the image
		 */
		g2.drawImage(image, 0, 0, null);

		/*
		 * Draw all the annotations
		 */
		for (ImageAnnotation annotation : annotations) {
			if (annotation == currentAnnotation) {
				annotation.draw(g2, true);
			} else {
				annotation.draw(g2, false);
			}
		}
	}

	public BufferedImage getImage() {
		BufferedImage imageOutput = new BufferedImage(image.getWidth(), image
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2Output = imageOutput.createGraphics();

		paintImage(g2Output);

		return imageOutput;
	}

	public void undo() {
		if (isUndoPossible()) {
			annotationsReverse.push(annotations.pop());
		}

		repaint();
	}

	public void redo() {
		if (isRedoPossible()) {
			annotations.push(annotationsReverse.pop());
		}

		repaint();
	}

	public boolean isUndoPossible() {
		return !annotations.isEmpty()
				&& !(annotations.size() == 1 && annotations.lastElement() == currentAnnotation);
	}

	public boolean isRedoPossible() {
		return !annotationsReverse.isEmpty();
	}

	public void backup() {
		annotationsBackup.clear();

		for (ImageAnnotation ann : annotations) {
			annotationsBackup.add(ann);
		}

		annotationsReverseBackup.clear();

		for (ImageAnnotation ann : annotationsReverse) {
			annotationsReverseBackup.add(ann);
		}
	}

	public void restore() {
		annotations.clear();

		for (ImageAnnotation ann : annotationsBackup) {
			annotations.add(ann);
		}

		annotationsReverse.clear();

		for (ImageAnnotation ann : annotationsReverseBackup) {
			annotationsReverse.add(ann);
		}
		
		repaint();
	}

	public ImageAnnotation getCurrentAnnotation() {
		return currentAnnotation;
	}

	public void setCurrentAnnotation(ImageAnnotation annotation) {
		currentAnnotation = annotation;
	}
}
