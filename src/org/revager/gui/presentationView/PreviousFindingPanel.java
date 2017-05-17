package org.revager.gui.presentationView;

import javax.swing.JPanel;

import org.revager.app.model.schema.Finding;

public class PreviousFindingPanel extends JPanel {

	private static final long serialVersionUID = -2211779692487550473L;

	private Finding finding;

	public PreviousFindingPanel() {
		// TODO: build GUI.
	}

	public void setFiding(Finding finding) {
		this.finding = finding;
		updateDisplay();
	}

	private void updateDisplay() {
		// TODO: update Display.
	}

}
