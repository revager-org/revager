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
	private float internOpacity = 1.0f;
	private transient Thread blinker = null;

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
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, internOpacity));
		g2d.drawImage(image, getWidth() - sideLength, getHeight() - sideLength, sideLength, sideLength, this);
	}

	public void setInternImageOpacity(float internOpacity) {
		this.internOpacity = Math.max(0, Math.min(1.0f, internOpacity));
		repaint();
	}

	public synchronized void setImageOpacity(float opacity) {
		if (opacity < 0.0f) {
			throw new IllegalArgumentException("Opacity must not be smaller than 0.");
		}
		if (this.opacity < 0.0f && opacity < 1.0f) {
			stopBlinking();
			this.opacity = opacity;
			setInternImageOpacity(opacity);
		} else if (1.0f < opacity) {
			startBlinking();
			this.opacity = -1.0f;
		} else {
			this.opacity = opacity;
			setInternImageOpacity(opacity);
		}
	}

	public void startBlinking() {
		if (blinker != null) {
			return;
		}
		blinker = new Thread(() -> {
			while (blinker != null) {
				try {
					setInternImageOpacity(0f);
					Thread.sleep(800);
					setInternImageOpacity(0.8f);
					Thread.sleep(800);
				} catch (InterruptedException e) {
					setInternImageOpacity(0f);
				}
			}
		});
		blinker.start();
	}

	public void stopBlinking() {
		blinker = null;
	}

}
