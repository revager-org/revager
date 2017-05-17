package org.revager.gui.presentationView;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

import org.revager.app.model.Data;

public class HurryUpImage extends JPanel {

	private static final long serialVersionUID = 6404510395755481196L;

	private transient Image image;
	private float opacity = 1.0f;

	public HurryUpImage() {
		super();
		setOpaque(false);
		// https://pixabay.com/en/clock-time-hour-minute-face-hands-308938/
		image = Data.getInstance().getIcon("hurryUpRed.png").getImage();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final int sideLength = Math.min(getWidth(), getHeight());
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2d.drawImage(image, 0, 0, sideLength, sideLength, this);
	}

	public void setImageOpacity(float opacity) {
		if (opacity > 1.0f) {
			throw new IllegalArgumentException("Opacity must not be larger than 1.");
		}
		if (opacity < 0.0f) {
			throw new IllegalArgumentException("Opacity must not be smaller than 1.");
		}
		this.opacity = opacity;
	}

}
