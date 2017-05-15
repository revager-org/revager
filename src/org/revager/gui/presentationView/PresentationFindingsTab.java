package org.revager.gui.presentationView;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.revager.app.model.schema.Finding;

public class PresentationFindingsTab extends JPanel {

	private static final long serialVersionUID = -7499170906423144396L;

	private JPanel statusPanel;
	private JPanel currentFindingPanel;

	public PresentationFindingsTab() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		currentFindingPanel = new CurrentFindingPanel(new Finding());
		
		this.add(currentFindingPanel);
		
		createStatusPanel();
		this.add(statusPanel);
		statusPanel.revalidate();

	}

	private void createStatusPanel() {
		statusPanel = new JPanel();
		
		GridBagLayout BagLayout = new GridBagLayout();
		statusPanel.setLayout(BagLayout);
		
		JLabel title = new JLabel("title");
		title.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_title = new GridBagConstraints();
		gbc_title.gridwidth = 3;
		gbc_title.insets = new Insets(0, 0, 5, 0);
		gbc_title.gridx = 0;
		gbc_title.gridy = 0;
		statusPanel.add(title, gbc_title);
		
		JLabel lblNewLabel = new JLabel("Dauer");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		statusPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		JProgressBar progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 1;
		statusPanel.add(progressBar, gbc_progressBar);
		
		JLabel zeitBefund = new JLabel("Zeit Befund");
		zeitBefund.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_zeitBefund = new GridBagConstraints();
		gbc_zeitBefund.insets = new Insets(0, 0, 5, 5);
		gbc_zeitBefund.gridx = 0;
		gbc_zeitBefund.gridy = 2;
		statusPanel.add(zeitBefund, gbc_zeitBefund);
		
	}

	public void updateFinding(Finding finding) {
		System.out.println(finding.getDescription());
		System.out.println(finding.getSeverity());
		System.out.println(finding.getExternalReferences());
		
	}
}
