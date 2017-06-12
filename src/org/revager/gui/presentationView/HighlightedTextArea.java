package org.revager.gui.presentationView;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

public class HighlightedTextArea extends JTextArea {

	private static final long serialVersionUID = 1971998633005799712L;
	private static final Color VERY_BRIGHT_YELLOW = new Color(252, 242, 199);

	private AtomicInteger keepHighlightedCounter = new AtomicInteger(0);
	private boolean firstTime = true;
	private String placeholder;
	private Font textFont;
	private Font placeholderFont;

	public HighlightedTextArea(Font textFont, String placeholder) {
		this.textFont = textFont;
		this.placeholder = placeholder;
		placeholderFont = new Font(textFont.getFontName(), Font.BOLD | Font.ITALIC, textFont.getSize());
	}

	@Override
	public void setText(String newText) {
		if (StringUtils.isEmpty(newText)) {
			setFont(placeholderFont);
			setForeground(new Color(135, 206, 250));
			super.setText(placeholder);
		} else {
			setFont(textFont);
			setForeground(Color.BLACK);
			highlightIfTextChanged(newText);
			super.setText(newText);
		}
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
