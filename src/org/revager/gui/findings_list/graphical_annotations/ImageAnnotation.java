package org.revager.gui.findings_list.graphical_annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class ImageAnnotation {
	private enum Type {
		ELLIPSE, RECTANGLE, ARROW, TEXT;
	}

	private static final Dimension MIN_SIZE = new Dimension(20, 20);

	private Type type = null;

	private int thickness = 1;
	private Dimension size = MIN_SIZE;
	private Color color = Color.RED;

	private RectangularShape shape = null;
	private String text = null;

	private Point position = new Point(0, 0);

	/**
	 * Graphical annotation
	 * 
	 * @param type
	 * @param size
	 * @param color
	 * @param position
	 * @param thickness
	 */
	private ImageAnnotation(Type type) {
		super();

		this.type = type;
	}

	public static ImageAnnotation newTextAnnotation(Color color, String text) {
		ImageAnnotation textAnn = new ImageAnnotation(Type.TEXT);

		textAnn.setColor(color);
		textAnn.setText(text);

		return textAnn;
	}

	public static ImageAnnotation newEllipseAnnotation(Color color, int thickness) {
		ImageAnnotation ellipse = new ImageAnnotation(Type.ELLIPSE);

		ellipse.setColor(color);
		ellipse.setThickness(thickness);

		ellipse.setShape(new Ellipse2D.Double());

		return ellipse;
	}

	public static ImageAnnotation newRectangleAnnotation(Color color, int thickness) {
		ImageAnnotation rectangle = new ImageAnnotation(Type.RECTANGLE);

		rectangle.setColor(color);
		rectangle.setThickness(thickness);

		rectangle.setShape(new Rectangle2D.Double());

		return rectangle;
	}

	public static ImageAnnotation newArrowAnnotation(Color color) {
		ImageAnnotation arrow = new ImageAnnotation(Type.ARROW);

		arrow.setColor(color);

		arrow.setShape(new Arrow2D());
		((Arrow2D) arrow.getShape()).setTailProportion(0.4, 0.3, 0.3);

		return arrow;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the thickness
	 */
	public int getThickness() {
		return thickness;
	}

	/**
	 * @param thickness
	 *            the thickness to set
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/**
	 * @return the size
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Dimension size) {
		/*
		 * Check if the size is smaller than the minimum size
		 */
		if (size.getWidth() < MIN_SIZE.getWidth()) {
			size = new Dimension((int) MIN_SIZE.getWidth(), (int) size.getHeight());
		}

		if (size.getHeight() < MIN_SIZE.getHeight()) {
			size = new Dimension((int) size.getWidth(), (int) MIN_SIZE.getHeight());
		}

		/*
		 * If the current annotation is a shape, set the frame of this shape
		 */
		if (shape != null) {
			shape.setFrame(this.position, size);
		}

		this.size = size;
	}

	/**
	 * Resets the size to the minimum size
	 */
	public void resetSize() {
		setSize(MIN_SIZE);
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the shape
	 */
	private RectangularShape getShape() {
		return shape;
	}

	/**
	 * @param shape
	 *            the shape to set
	 */
	private void setShape(RectangularShape shape) {
		this.shape = shape;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the position
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Point position) {
		/*
		 * If the current annotation is a shape, set the frame of this shape
		 */
		if (shape != null) {
			shape.setFrame(position, this.size);
		}

		this.position = position;
	}

	public ImageAnnotation cloneInstance() {
		ImageAnnotation clone = null;

		switch (type) {
		case TEXT:
			clone = newTextAnnotation(color, text);
			break;
		case ELLIPSE:
			clone = newEllipseAnnotation(color, thickness);
			break;
		case RECTANGLE:
			clone = newRectangleAnnotation(color, thickness);
			break;
		case ARROW:
			clone = newArrowAnnotation(color);
			break;

		default:
			break;
		}

		clone.setSize(size);
		clone.setPosition(position);

		return clone;
	}

	public void draw(Graphics2D g2, boolean tranparent) {
		if (tranparent) {
			g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
		} else {
			g2.setColor(color);
		}

		switch (type) {
		case ELLIPSE:
			g2.setStroke(new BasicStroke(thickness));
			g2.draw(shape);
			break;
		case RECTANGLE:
			g2.setStroke(new BasicStroke(thickness));
			g2.draw(shape);
			break;
		case ARROW:
			g2.fill(shape);
			break;
		case TEXT:
			g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) (size.getWidth() / 1.4)));
			g2.drawString(text, (int) (position.getX() + MIN_SIZE.getWidth() + 2),
					(int) (position.getY() + MIN_SIZE.getHeight() - 2));
			break;

		default:
			break;
		}
	}
}
