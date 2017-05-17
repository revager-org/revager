package org.revager.gui.presentationView;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.appdata.AppSettingValue;
import org.revager.app.model.schema.Finding;
import org.revager.gui.UI;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 4044468994799470896L;

	private Finding finding;
	private JProgressBar totalDurationProgress;
	private JLabel findingTimeField;
	private JPanel hurryUpPanel;
	private JLabel continueDiscussionField;
	private JLabel votingsField;

	public StatusPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] { 0.225, 0.1, 0.225, 0.225, 0.225 };
		gridBagLayout.columnWeights = new double[] { 0.03, 0.17, 0.8 };
		setLayout(gridBagLayout);
		setBackground(UI.BLUE_BACKGROUND_COLOR);

		addTitle();
		addTotalTime();
		addFindingTime();
		addHurryUp();
		addContinueDiscussion();
		addVotings();

		revalidate();
		repaint();
	}

	public void setFinding(Finding finding) {
		this.finding = finding;
		updateDisplay();
	}

	private void updateDisplay()  {
		ApplicationData appData = Data.getInstance().getAppData();
		// TODO APR: update display.
		totalDurationProgress.setMaximum(120);
		totalDurationProgress.setValue(100);
		int maxReviewTime = 0;
		try {
			maxReviewTime = Integer
					.parseInt(appData.getSetting(AppSettingKey.APP_PROTOCOL_WARNING_TIME));
		} catch (DataException e) {
		}
		System.out.println(maxReviewTime);
		totalDurationProgress
				.setString(maxReviewTime * totalDurationProgress.getValue() / totalDurationProgress.getMaximum() + "min");
		findingTimeField.setText("sdf");
		// hurryUpPanel.
		continueDiscussionField.setText("2/3");
		votingsField.setText("1/2");
	}

	private void addTitle() {
		JLabel title = new JLabel("title");
		title.setBackground(UI.EDIT_VIEW_BG);
		title.setOpaque(true);
		title.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT_BOLD);
		title.setHorizontalTextPosition(SwingConstants.LEADING);
		GridBagConstraints titleGridConstraints = new GridBagConstraints();
		titleGridConstraints.gridwidth = GridBagConstraints.REMAINDER;
		titleGridConstraints.insets = new Insets(0, 0, 5, 0);
		titleGridConstraints.gridx = 0;
		titleGridConstraints.gridy = 0;
		add(title, titleGridConstraints);
	}

	private void addTotalTime() {
		JLabel totalDurationLabel = new JLabel("Gesamtdauer:");
		totalDurationLabel.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		GridBagConstraints totalDurationGridConstraints = new GridBagConstraints();
		totalDurationGridConstraints.insets = new Insets(0, 0, 0, 5);
		totalDurationGridConstraints.gridx = 0;
		totalDurationGridConstraints.gridy = 1;
		totalDurationGridConstraints.anchor = GridBagConstraints.EAST;
		add(totalDurationLabel, totalDurationGridConstraints);

		totalDurationProgress = new JProgressBar();
		totalDurationProgress.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		totalDurationProgress.setStringPainted(true);
		GridBagConstraints totalDurationProgressGridConstraints = new GridBagConstraints();
		totalDurationProgressGridConstraints.fill = GridBagConstraints.BOTH;
		totalDurationProgressGridConstraints.gridwidth = 2;
		totalDurationProgressGridConstraints.gridx = 1;
		totalDurationProgressGridConstraints.gridy = 1;
		totalDurationProgressGridConstraints.anchor = GridBagConstraints.EAST;
		add(totalDurationProgress, totalDurationProgressGridConstraints);
	}

	private void addFindingTime() {
		JLabel findingTimeLabel = new JLabel("Befunddauer:");
		findingTimeLabel.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		GridBagConstraints findingTimeLabelGridConstraints = new GridBagConstraints();
		findingTimeLabelGridConstraints.insets = new Insets(0, 0, 0, 5);
		findingTimeLabelGridConstraints.gridx = 0;
		findingTimeLabelGridConstraints.gridy = 2;
		findingTimeLabelGridConstraints.anchor = GridBagConstraints.EAST;
		add(findingTimeLabel, findingTimeLabelGridConstraints);

		findingTimeField = new JLabel();
		findingTimeField.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		GridBagConstraints findingTimeFieldGridConstraints = new GridBagConstraints();
		findingTimeFieldGridConstraints.gridx = 1;
		findingTimeFieldGridConstraints.gridy = 2;
		findingTimeFieldGridConstraints.anchor = GridBagConstraints.WEST;
		add(findingTimeField, findingTimeFieldGridConstraints);
	}

	private void addHurryUp() {
		hurryUpPanel = new HurryUpImage();
		GridBagConstraints hurryUpPanelGridConstraints = new GridBagConstraints();
		hurryUpPanelGridConstraints.fill = GridBagConstraints.BOTH;
		hurryUpPanelGridConstraints.gridheight = GridBagConstraints.REMAINDER;
		hurryUpPanelGridConstraints.gridwidth = GridBagConstraints.REMAINDER;
		hurryUpPanelGridConstraints.gridx = 2;
		hurryUpPanelGridConstraints.gridy = 2;
		hurryUpPanelGridConstraints.anchor = GridBagConstraints.SOUTHEAST;
		add(hurryUpPanel, hurryUpPanelGridConstraints);
	}

	private void addContinueDiscussion() {
		JLabel continueDiscussionLabel = new JLabel("Mehr Fokus auf die Diskussion:");
		continueDiscussionLabel.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		GridBagConstraints continueDiscussionLabelGridConstraints = new GridBagConstraints();
		continueDiscussionLabelGridConstraints.insets = new Insets(0, 0, 0, 5);
		continueDiscussionLabelGridConstraints.gridx = 0;
		continueDiscussionLabelGridConstraints.gridy = 3;
		continueDiscussionLabelGridConstraints.anchor = GridBagConstraints.EAST;
		add(continueDiscussionLabel, continueDiscussionLabelGridConstraints);

		continueDiscussionField = new JLabel();
		continueDiscussionField.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		GridBagConstraints continueDiscussionFieldGridConstraints = new GridBagConstraints();
		continueDiscussionFieldGridConstraints.gridx = 1;
		continueDiscussionFieldGridConstraints.gridy = 3;
		continueDiscussionFieldGridConstraints.anchor = GridBagConstraints.WEST;
		add(continueDiscussionField, continueDiscussionFieldGridConstraints);
	}

	private void addVotings() {
		JLabel votingsLabel = new JLabel("Abgestimmt:");
		votingsLabel.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		GridBagConstraints votingLabelGridConstraints = new GridBagConstraints();
		votingLabelGridConstraints.insets = new Insets(0, 0, 0, 5);
		votingLabelGridConstraints.gridx = 0;
		votingLabelGridConstraints.gridy = 4;
		votingLabelGridConstraints.anchor = GridBagConstraints.EAST;
		add(votingsLabel, votingLabelGridConstraints);

		votingsField = new JLabel();
		votingsField.setFont(PresentationFindingsTab.STANDARD_STATUS_FONT);
		GridBagConstraints votingsFieldGridConstraints = new GridBagConstraints();
		votingsFieldGridConstraints.gridx = 1;
		votingsFieldGridConstraints.gridy = 4;
		votingsFieldGridConstraints.anchor = GridBagConstraints.WEST;
		add(votingsField, votingsFieldGridConstraints);
	}

}
