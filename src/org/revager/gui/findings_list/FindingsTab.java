package org.revager.gui.findings_list;

import static org.revager.app.model.Data.translate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.revager.app.Application;
import org.revager.app.FindingManagement;
import org.revager.app.SeverityManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

public class FindingsTab extends JPanel {

	private static final long serialVersionUID = 1L;

	private FindingManagement findMgmt = Application.getInstance().getFindingMgmt();
	private SeverityManagement sevMgmt = Application.getInstance().getSeverityMgmt();

	private Protocol protocol = null;

	private FindingPanel currentFindingPanel = null;

	private GridBagLayout layoutFindingsList = new GridBagLayout();
	private JPanel panelFindingsList = new JPanel(layoutFindingsList);
	private JScrollPane scrollFindingsList = new JScrollPane(panelFindingsList);

	private int gblAlignment = GridBagConstraints.BOTH;

	private JPanel panelStrut = new JPanel();

	private JButton buttonAddFinding = new JButton(translate("Add Finding"));
	private JLabel labelNumOfFindings = new JLabel();

	private Map<Finding, Integer> gridBagPositions = new HashMap<Finding, Integer>();
	private Map<Finding, FindingPanel> findingPanels = new HashMap<Finding, FindingPanel>();

	private int currentGridBagPosition = 0;

	public FindingsTab(final Protocol protocol) {
		super();

		this.protocol = protocol;

		this.setLayout(new BorderLayout());

		panelFindingsList.setBackground(Color.WHITE);
		panelStrut.setBackground(Color.WHITE);

		scrollFindingsList.getVerticalScrollBar().setUnitIncrement(12);
		scrollFindingsList.getHorizontalScrollBar().setUnitIncrement(12);

		/*
		 * Prepare footer
		 */
		JPanel panelFoot = new JPanel(new BorderLayout());

		labelNumOfFindings.setFont(UI.VERY_LARGE_FONT);

		buttonAddFinding.setIcon(Data.getInstance().getIcon("add_25x25.png"));
		buttonAddFinding.addActionListener(e -> {
			if (currentFindingPanel != null) {
				currentFindingPanel.storeFindingData();
			}
			Finding newFind = new Finding();
			findMgmt.setLocalizedSeverity(newFind, sevMgmt.getSeverities().get(0));
			newFind = findMgmt.addFinding(newFind, protocol);

			addFinding(newFind);
		});

		panelFoot.setBorder(BorderFactory.createMatteBorder(5, 35, 5, 35, panelFoot.getBackground()));

		panelFoot.add(buttonAddFinding, BorderLayout.WEST);
		panelFoot.add(labelNumOfFindings, BorderLayout.EAST);

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

	public void updateTab() {
		if (currentFindingPanel != null) {
			currentFindingPanel.storeFindingData();
		}

		labelNumOfFindings.setText(findMgmt.getNumberOfFindings(protocol) + " " + translate("Findings"));

		/*
		 * Update the tooltip
		 */
		Map<String, Integer> findingsSev = new HashMap<String, Integer>();

		for (Finding f : findMgmt.getFindings(protocol)) {
			String severity = findMgmt.getLocalizedSeverity(f);

			if (findingsSev.get(severity) == null) {
				findingsSev.put(severity, 1);
			} else {
				findingsSev.put(severity, findingsSev.get(severity) + 1);
			}
		}

		String sevOverview = "";
		String separator = "";

		for (String s : sevMgmt.getSeverities()) {
			if (findingsSev.get(s) != null) {
				sevOverview = sevOverview + separator + findingsSev.get(s) + " x " + s;

				separator = "; ";
			}
		}

		labelNumOfFindings.setToolTipText(GUITools.getTextAsHtml(sevOverview));

		/*
		 * Update the button state
		 */
		boolean isComplete = true;

		for (Finding find : findMgmt.getFindings(protocol)) {
			if (findMgmt.isFindingNotComplete(find)) {
				isComplete = false;
			}
		}

		buttonAddFinding.setEnabled(isComplete);

		UI.getInstance().getProtocolFrame().update(null, null);
	}

	public void addFinding(Finding finding) {
		findMgmt.addFinding(finding, protocol);

		if (currentFindingPanel != null) {
			currentFindingPanel.switchView();
		}

		currentFindingPanel = new FindingPanel(finding, this);

		gridBagPositions.put(finding, currentGridBagPosition);
		findingPanels.put(finding, currentFindingPanel);

		panelFindingsList.remove(panelStrut);

		GUITools.addComponent(panelFindingsList, layoutFindingsList, currentFindingPanel, 0, currentGridBagPosition, 1,
				1, 1.0, 0.0, 5, 5, 5, 5, gblAlignment, GridBagConstraints.NORTHWEST);

		/*
		 * Update position of strut panel
		 */
		GUITools.addComponent(panelFindingsList, layoutFindingsList, panelStrut, 0, currentGridBagPosition + 1, 1, 1,
				1.0, 1.0, 0, 0, 0, 0, gblAlignment, GridBagConstraints.NORTHWEST);

		currentGridBagPosition++;

		GUITools.scrollToBottom(scrollFindingsList);
		// panelFindingsList.scrollRectToVisible(currentFindingPanel.getBounds());

		panelFindingsList.revalidate();

		updateTab();
	}

	public void editFinding(Finding finding) {
		if (currentFindingPanel != null && currentFindingPanel.getFinding() == finding) {
			return;
		}

		if (currentFindingPanel != null) {
			currentFindingPanel.switchView();
		}

		currentFindingPanel = findingPanels.get(finding);

		currentFindingPanel.switchView();

		panelFindingsList.revalidate();
	}

	public void closeCurrentFinding() {
		if (currentFindingPanel != null) {
			currentFindingPanel.switchView();

			currentFindingPanel = null;

			panelFindingsList.revalidate();
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

			panelFindingsList.revalidate();
		}

		/*
		 * Add new finding, if there are no findings present
		 */
		if (findMgmt.getNumberOfFindings(protocol) == 0) {
			Finding newFind = new Finding();
			findMgmt.setLocalizedSeverity(newFind, sevMgmt.getSeverities().get(0));
			newFind = findMgmt.addFinding(newFind, protocol);

			addFinding(newFind);
		}

		updateTab();
	}

	public void pushUpCurrentFinding() {
		if (currentFindingPanel != null) {
			FindingPanel predecFindPanel = null;

			int predecPos = -1;
			int currPos = gridBagPositions.get(currentFindingPanel.getFinding());

			/*
			 * Find predecessor
			 */
			for (Finding find : gridBagPositions.keySet()) {
				if (gridBagPositions.get(find) < currPos && gridBagPositions.get(find) > predecPos) {
					predecPos = gridBagPositions.get(find);
					predecFindPanel = findingPanels.get(find);
				}
			}

			gridBagPositions.put(currentFindingPanel.getFinding(), predecPos);
			gridBagPositions.put(predecFindPanel.getFinding(), currPos);

			panelFindingsList.remove(currentFindingPanel);
			panelFindingsList.remove(predecFindPanel);

			GUITools.addComponent(panelFindingsList, layoutFindingsList, currentFindingPanel, 0, predecPos, 1, 1, 1.0,
					0.0, 5, 5, 5, 5, gblAlignment, GridBagConstraints.NORTHWEST);
			GUITools.addComponent(panelFindingsList, layoutFindingsList, predecFindPanel, 0, currPos, 1, 1, 1.0, 0.0, 5,
					5, 5, 5, gblAlignment, GridBagConstraints.NORTHWEST);

			findMgmt.pushUpFinding(currentFindingPanel.getFinding(), protocol);

			panelFindingsList.revalidate();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollFindingsList.getVerticalScrollBar()
							.setValue(scrollFindingsList.getVerticalScrollBar().getValue()
									- (FindingPanel.COMPACT_VIEW_SIZE.height + 10));
				}
			});

			currentFindingPanel.updateFindingButtons();
		}
	}

	public void pushDownCurrentFinding() {
		if (currentFindingPanel != null) {
			FindingPanel succFindPanel = null;

			int succPos = currentGridBagPosition + 1;
			int currPos = gridBagPositions.get(currentFindingPanel.getFinding());

			/*
			 * Find successor
			 */
			for (Finding find : gridBagPositions.keySet()) {
				if (gridBagPositions.get(find) > currPos && gridBagPositions.get(find) < succPos) {
					succPos = gridBagPositions.get(find);
					succFindPanel = findingPanels.get(find);
				}
			}

			gridBagPositions.put(currentFindingPanel.getFinding(), succPos);
			gridBagPositions.put(succFindPanel.getFinding(), currPos);

			panelFindingsList.remove(currentFindingPanel);
			panelFindingsList.remove(succFindPanel);

			GUITools.addComponent(panelFindingsList, layoutFindingsList, currentFindingPanel, 0, succPos, 1, 1, 1.0,
					0.0, 5, 5, 5, 5, gblAlignment, GridBagConstraints.NORTHWEST);
			GUITools.addComponent(panelFindingsList, layoutFindingsList, succFindPanel, 0, currPos, 1, 1, 1.0, 0.0, 5,
					5, 5, 5, gblAlignment, GridBagConstraints.NORTHWEST);

			findMgmt.pushDownFinding(currentFindingPanel.getFinding(), protocol);

			panelFindingsList.revalidate();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollFindingsList.getVerticalScrollBar()
							.setValue(scrollFindingsList.getVerticalScrollBar().getValue()
									+ (FindingPanel.COMPACT_VIEW_SIZE.height + 10));
				}
			});

			currentFindingPanel.updateFindingButtons();
		}
	}

	public void pushTopCurrentFinding() {
		if (currentFindingPanel != null) {
			while (!findMgmt.isTopFinding(currentFindingPanel.getFinding(), protocol)) {
				pushUpCurrentFinding();
			}
			/*
			 * FindingPanel firstFindPanel = null;
			 * 
			 * int firstPos = currentGridBagPosition + 1; int currPos =
			 * gridBagPositions .get(currentFindingPanel.getFinding());
			 * 
			 * 
			 * 
			 * for (Finding find : gridBagPositions.keySet()) { if
			 * (gridBagPositions.get(find) < firstPos) { firstPos =
			 * gridBagPositions.get(find); firstFindPanel =
			 * findingPanels.get(find); } }
			 * 
			 * gridBagPositions.put(currentFindingPanel.getFinding(), firstPos);
			 * gridBagPositions.put(firstFindPanel.getFinding(), currPos);
			 * 
			 * panelFindingsList.remove(currentFindingPanel);
			 * panelFindingsList.remove(firstFindPanel);
			 * 
			 * GUITools.addComponent(panelFindingsList, layoutFindingsList,
			 * currentFindingPanel, 0, firstPos, 1, 1, 1.0, 0.0, 5, 5, 5, 5,
			 * GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
			 * GUITools.addComponent(panelFindingsList, layoutFindingsList,
			 * firstFindPanel, 0, currPos, 1, 1, 1.0, 0.0, 5, 5, 5, 5,
			 * GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
			 * 
			 * findMgmt.pushTopFinding(currentFindingPanel.getFinding(),
			 * protocol);
			 * 
			 * panelFindingsList.revalidate();
			 * 
			 * GUITools.scrollToTop(scrollFindingsList);
			 * 
			 * currentFindingPanel.updateFindingButtons();
			 */
		}
	}

	public void pushBottomCurrentFinding() {
		if (currentFindingPanel != null) {
			while (!findMgmt.isBottomFinding(currentFindingPanel.getFinding(), protocol)) {
				pushDownCurrentFinding();
			}
		}
	}

}
