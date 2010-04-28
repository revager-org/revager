package org.revager.gui.protocol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.revager.app.Application;
import org.revager.app.FindingManagement;
import org.revager.app.SeverityManagement;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Protocol;
import org.revager.tools.GUITools;

public class FindingsTab extends JPanel {

	private static final long serialVersionUID = 1L;

	private FindingManagement findMgmt = Application.getInstance()
			.getFindingMgmt();
	private SeverityManagement sevMgmt = Application.getInstance()
			.getSeverityMgmt();

	private Protocol protocol = null;

	private FindingPanel currentFindingPanel = null;

	private GridBagLayout layoutFindingsList = new GridBagLayout();
	private JPanel panelFindingsList = new JPanel(layoutFindingsList);
	private JScrollPane scrollFindingsList = new JScrollPane(panelFindingsList);

	private Map<Finding, Integer> gridBagPositions = new HashMap<Finding, Integer>();
	private Map<Finding, FindingPanel> findingPanels = new HashMap<Finding, FindingPanel>();

	private int currentGridBagPosition = 0;

	public FindingsTab(final Protocol protocol) {
		super();

		this.protocol = protocol;

		this.setLayout(new BorderLayout());

		panelFindingsList.setBackground(Color.WHITE);

		/*
		 * Prepare footer
		 */
		JPanel panelFoot = new JPanel(new BorderLayout());

		JButton buttonAddFinding = new JButton("Add new finding");
		buttonAddFinding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int listIdLastFinding = findMgmt.getNumberOfFindings(protocol) - 1;

				if (!findMgmt.isFindingEmpty(findMgmt.getFindings(protocol)
						.get(listIdLastFinding))) {
					Finding newFind = new Finding();
					newFind.setSeverity(sevMgmt.getSeverities().get(0));
					newFind = findMgmt.addFinding(newFind, protocol);

					addFinding(newFind);
				}
			}
		});

		panelFoot.add(buttonAddFinding, BorderLayout.WEST);
		panelFoot.add(new JLabel("Total: 7 findings"), BorderLayout.EAST);

		this.add(scrollFindingsList, BorderLayout.CENTER);
		this.add(panelFoot, BorderLayout.SOUTH);

		/*
		 * Load all findings
		 */
		for (Finding find : findMgmt.getFindings(protocol)) {
			addFinding(find);
		}
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void addFinding(Finding finding) {
		findMgmt.addFinding(finding, protocol);

		if (currentFindingPanel != null) {
			currentFindingPanel.switchView();
		}

		currentFindingPanel = new FindingPanel(finding, this);

		gridBagPositions.put(finding, currentGridBagPosition);
		findingPanels.put(finding, currentFindingPanel);

		GUITools.addComponent(panelFindingsList, layoutFindingsList,
				currentFindingPanel, 0, currentGridBagPosition, 1, 1, 1.0, 0.0,
				5, 5, 5, 5, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		currentGridBagPosition++;

		GUITools.scrollToBottom(scrollFindingsList);
	}

	public void editFinding(Finding finding) {
		if (currentFindingPanel != null
				&& currentFindingPanel.getFinding() == finding) {
			return;
		}

		if (currentFindingPanel != null) {
			currentFindingPanel.switchView();
		}

		currentFindingPanel = findingPanels.get(finding);

		currentFindingPanel.switchView();
	}

	public void closeCurrentFinding() {
		if (currentFindingPanel != null) {
			currentFindingPanel.switchView();

			currentFindingPanel = null;
		}
	}

	public void removeCurrentFinding() {
		if (currentFindingPanel != null) {
			Finding currentFinding = currentFindingPanel.getFinding();

			panelFindingsList.remove(currentFindingPanel);

			currentFindingPanel = null;

			gridBagPositions.remove(currentFinding);

			findingPanels.remove(currentFinding);

			findMgmt.removeFinding(currentFinding, protocol);
		}
	}

	public void pushUpCurrentFinding() {
		if (currentFindingPanel != null) {

		}
	}
}
