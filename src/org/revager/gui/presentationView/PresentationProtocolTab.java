package org.revager.gui.presentationView;

import static org.revager.app.model.Data.translate;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Review;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

public class PresentationProtocolTab extends JPanel {

	private static final long serialVersionUID = 1377208308219097378L;

	private GridBagLayout gridBagLayout;

	private JTextArea recommendationTextField;
	private JTextArea generalImpressionTextArea;
	private JTextArea meetingCommentTextArea;
	private JTextArea protocolCommentTextArea;
	private JScrollPane meetCommScrllPn;
	private JScrollPane protCommScrllPn;

	public PresentationProtocolTab() {
		super();
		gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);

		JLabel impressionLabel = new JLabel(translate("General impression of the product:"));
		impressionLabel.setFont(UI.VERY_LARGE_FONT_BOLD);
		generalImpressionTextArea = new JTextArea();
		generalImpressionTextArea.setEditable(false);
		generalImpressionTextArea.setRows(4);
		generalImpressionTextArea.setFont(UI.VERY_LARGE_FONT);

		JLabel recLbl = new JLabel(translate("Final recommendation for the product:"));
		recLbl.setFont(UI.VERY_LARGE_FONT_BOLD);
		recommendationTextField = new JTextArea();
		recommendationTextField.setRows(1);
		recommendationTextField.setEditable(false);
		recommendationTextField.setFont(UI.VERY_LARGE_FONT);

		JLabel meetCommLbl = new JLabel(translate("Comments on the meeting:"));
		meetCommLbl.setFont(UI.VERY_LARGE_FONT_BOLD);
		meetingCommentTextArea = new JTextArea();
		meetingCommentTextArea.setEditable(false);
		meetingCommentTextArea.setRows(4);
		meetingCommentTextArea.setFont(UI.VERY_LARGE_FONT);

		JLabel protCommLbl = new JLabel(translate("Comments on the list of findings:"));
		protCommLbl.setFont(UI.VERY_LARGE_FONT_BOLD);
		protocolCommentTextArea = new JTextArea();
		protocolCommentTextArea.setEditable(false);
		protocolCommentTextArea.setRows(4);
		protocolCommentTextArea.setFont(UI.VERY_LARGE_FONT);

		meetCommScrllPn = GUITools.setIntoScrllPn(meetingCommentTextArea);
		meetCommScrllPn.setMinimumSize(meetCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(meetCommScrllPn);

		protCommScrllPn = GUITools.setIntoScrllPn(protocolCommentTextArea);
		protCommScrllPn.setMinimumSize(protCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(protCommScrllPn);

		GUITools.addComponent(this, gridBagLayout, impressionLabel, 0, 1, 2, 1, 0.0, 0.0, 20, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, generalImpressionTextArea, 0, 2, 2, 1, 1.0, 0.0, 5, 10, 0, 10,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);

		GUITools.addComponent(this, gridBagLayout, recLbl, 0, 3, 2, 1, 0.0, 0.0, 20, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, recommendationTextField, 0, 4, 2, 1, 1.0, 0.0, 5, 10, 0, 10,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, meetCommLbl, 0, 5, 1, 1, 1.0, 0.0, 15, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, meetCommScrllPn, 0, 6, 1, 1, 1.0, 1.0, 5, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, protCommLbl, 1, 5, 1, 1, 1.0, 0.0, 15, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, protCommScrllPn, 1, 6, 1, 1, 1.0, 1.0, 5, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		this.setBorder(new EmptyBorder(0, 10, 20, 10));
	}

	public void updateTabData(Review review) {
		generalImpressionTextArea.setText(review.getImpression());
		recommendationTextField.setText(review.getRecommendation());
	}

	public void updateTabData(Protocol protocol) {
		protocolCommentTextArea.setText(protocol.getComments());
	}

	public void updateTabData(Meeting meeting) {
		meetingCommentTextArea.setText(meeting.getComments());
	}
}
