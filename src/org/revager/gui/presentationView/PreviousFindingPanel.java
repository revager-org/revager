package org.revager.gui.presentationView;

import static org.revager.app.model.Data._;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.revager.app.model.schema.Finding;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

public class PreviousFindingPanel extends JPanel {

	private static final long serialVersionUID = 110372565207228520L;

	private Finding finding = new Finding();

	private GridBagLayout layoutEditView = new GridBagLayout();
	private JPanel panelEditView = new JPanel(layoutEditView);

	private JLabel labelFindingTitle = new JLabel();
	private JLabel labelFindingSeverity = new JLabel();
	private JTextArea textDescription = new JTextArea();

	private JScrollPane scrollDescription;

	public PreviousFindingPanel() {
		super();

		this.setLayout(layoutEditView);
		this.setBackground(Color.WHITE);

		// panelEditView.setPreferredSize(new Dimension(100, 80));
		panelEditView.setBorder(UI.STANDARD_BORDER);
		panelEditView.setBackground(UI.COMPACT_VIEW_BG);

		labelFindingSeverity.setFont(UI.VERY_LARGE_FONT);
		labelFindingSeverity.setForeground(Color.DARK_GRAY);

		// TODO: translage
		labelFindingTitle.setText("Vorheriger Befund: ");
		labelFindingTitle.setFont(UI.VERY_LARGE_FONT_BOLD);

		scrollDescription = GUITools.setIntoScrllPn(textDescription);
		GUITools.scrollToTop(scrollDescription);

		textDescription.setEditable(false);
		textDescription.setFont(UI.VERY_LARGE_FONT);

		JPanel panelStrut = new JPanel();

		GUITools.addComponent(panelEditView, layoutEditView, labelFindingTitle, 0, 0, 2, 1, 0.0, 0.0, 10, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView, scrollDescription, 0, 1, 1, 1, 1.0, 1.0, 10, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(panelEditView, layoutEditView, panelStrut, 1, 1, 1, 1, 0.0, 0.0, 10, 0, 0, 20,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		GUITools.addComponent(this, layoutEditView, panelEditView, 1, 0, 1, 6, 1.0, 0.0, 0, 0, 0, 20,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		this.setVisible(true);
	}

	public void setFiding(Finding finding) {
		this.finding = finding;
		updateDisplay();
	}

	private void updateDisplay() {
		// TODO: update Display.
		// TODO: translatee
		labelFindingTitle.setText("Vorheriger Befund: " + _("Finding") + " " + finding.getId());
		textDescription.setText(finding.getDescription());
	}

}
