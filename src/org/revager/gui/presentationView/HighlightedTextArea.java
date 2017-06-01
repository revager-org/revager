package org.revager.gui.presentationView;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class HighlightedTextArea extends JTextArea {

	private static final long serialVersionUID = 1971998633005799712L;
	private static final Color VERY_BRIGHT_YELLOW = new Color(252, 242, 199);

	private AtomicInteger keepHighlightedCounter = new AtomicInteger(0);
	private boolean firstTime = true;

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
		setBackground(VERY_BRIGHT_YELLOW);
		keepHighlightedCounter.incrementAndGet();
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			int counter = keepHighlightedCounter.decrementAndGet();
			if (counter == 0) {
				SwingUtilities.invokeLater(() -> setBackground(Color.WHITE));
			}
		});
		thread.start();
	}

}
