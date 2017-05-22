package org.revager.gui.presentationView;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.revager.app.model.schema.Finding;

public class PresentationFindingsTab extends JPanel {

	private static final long serialVersionUID = -7499170906423144396L;

	private PreviousFindingPanel previousFindingPanel = new PreviousFindingPanel();
	private CurrentFindingPanel currentFindingPanel = new CurrentFindingPanel();
	private StatusPanel statusPanel;
	private transient Finding lastFinding;

	public PresentationFindingsTab() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(previousFindingPanel);
		this.add(currentFindingPanel);
		statusPanel = new StatusPanel();
		this.add(statusPanel);
	}

	public void updateFinding(Finding finding) {
		if (finding == null) {
			return;
		}
		currentFindingPanel.setFinding(finding);
		if (lastFinding != finding) {
			if (lastFinding != null) {
				previousFindingPanel.setFiding(lastFinding);
			}
			lastFinding = finding;
		}
		statusPanel.setFinding(finding);
	}
}
