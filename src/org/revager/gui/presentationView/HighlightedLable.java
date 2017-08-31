package org.revager.gui.presentationView;

import java.awt.Font;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Subclass of an {@link JLabel} which changes the font to <em>bold</em> for a
 * few seconds when the text was changed.
 * 
 * @see #setText(String)
 */
public class HighlightedLable extends JLabel {

	private static final long serialVersionUID = 1971998633005799712L;

	private AtomicInteger keepHighlightedCounter = new AtomicInteger(0);
	private boolean firstTime = true;
	private Font highlightFont;
	private Font defaultFont;

	public HighlightedLable(Font defaultFont) {
		super();
		setFont(defaultFont);
		this.defaultFont = defaultFont;
		highlightFont = new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize());
	}

	@Override
	public void setText(String newText) {
		highlightIfTextChanged(newText);
		super.setText(newText);
	}

	private void highlightIfTextChanged(String newText) {
		if (getText().equals(newText)) {
			return;
		}
		if (firstTime) {
			firstTime = false;
			return;
		}
		setFont(highlightFont);
		keepHighlightedCounter.incrementAndGet();
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			int counter = keepHighlightedCounter.decrementAndGet();
			if (counter == 0) {
				SwingUtilities.invokeLater(() -> setFont(defaultFont));
			}
		});
		thread.start();
	}

}
