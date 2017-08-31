package org.revager.gui.presentationView;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.revager.app.model.schema.Finding;
import org.revager.tools.GUITools;

/**
 * Gui tab element which displays the findings and the dashboard.
 * 
 * @see PresentationProtocolTab
 */
public class PresentationFindingsTab extends JPanel {

	private static final long serialVersionUID = -7499170906423144396L;

	private PreviousFindingPanel previousFindingPanel = new PreviousFindingPanel();
	private CurrentFindingPanel currentFindingPanel = new CurrentFindingPanel();
	private StatusPanel statusPanel = new StatusPanel();
	private transient Finding lastFinding;

	public PresentationFindingsTab() {
		super();
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		setBackground(Color.WHITE);

		GUITools.addComponent(this, gridBagLayout, previousFindingPanel, 0, 0, 1, 1, 1.0, 0.5, 10, 10, 5, 10, BOTH,
				WEST);
		GUITools.addComponent(this, gridBagLayout, currentFindingPanel, 0, 1, 1, 1, 1.0, 1.0, 5, 10, 5, 10, BOTH, WEST);
		GUITools.addComponent(this, gridBagLayout, statusPanel, 0, 2, 1, 1, 1.0, 0.0, 5, 0, 0, 0, HORIZONTAL, WEST);
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
