package org.revager.gui.presentationView;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static org.revager.app.model.Data.translate;

import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.revager.app.model.Data;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Review;
import org.revager.tools.GUITools;

public class PresentationProtocolTab extends JPanel {

	private static final long serialVersionUID = 1377208308219097378L;

	public static final Font LABLE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 25);
	public static final Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 19);

	private GridBagLayout layout = new GridBagLayout();
	private JTextArea impressionTextArea;
	private JTextArea recommendationField;
	private JTextArea meetingCommentTextArea;
	private JTextArea protocolCommentTextArea;
	private JScrollPane impressionField;
	private JScrollPane meetingCommentField;
	private JScrollPane protocolCommentField;

	public PresentationProtocolTab() {
		super();
		this.setLayout(layout);

		JLabel impressionLabel = new JLabel(translate("General impression of the product:"));
		impressionLabel.setFont(LABLE_FONT);
		impressionTextArea = new JTextArea();
		impressionTextArea.setEditable(false);
		impressionTextArea.setLineWrap(true);
		impressionTextArea.setWrapStyleWord(true);
		impressionTextArea.setFont(TEXT_FONT);

		JLabel recommendationLabel = new JLabel(translate("Final recommendation for the product:"));
		recommendationLabel.setFont(LABLE_FONT);
		recommendationField = new JTextArea();
		recommendationField.setRows(1);
		recommendationField.setEditable(false);
		recommendationField.setFont(TEXT_FONT);

		JLabel meetingCommentLabel = new JLabel(translate("Comments on the meeting:"));
		meetingCommentLabel.setFont(LABLE_FONT);
		meetingCommentTextArea = new JTextArea();
		meetingCommentTextArea.setEditable(false);
		meetingCommentTextArea.setLineWrap(true);
		meetingCommentTextArea.setWrapStyleWord(true);
		meetingCommentTextArea.setFont(TEXT_FONT);

		JLabel protocalCommentLabel = new JLabel(translate("Comments on the list of findings:"));
		protocalCommentLabel.setFont(LABLE_FONT);
		protocolCommentTextArea = new JTextArea();
		protocolCommentTextArea.setEditable(false);
		protocolCommentTextArea.setLineWrap(true);
		protocolCommentTextArea.setWrapStyleWord(true);
		protocolCommentTextArea.setFont(TEXT_FONT);

		impressionField = new JScrollPane(impressionTextArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		impressionField.setMinimumSize(impressionField.getPreferredSize());
		impressionField.setMaximumSize(impressionField.getSize());
		impressionField.setPreferredSize(impressionField.getSize());
		GUITools.scrollToTop(impressionField);

		meetingCommentField = new JScrollPane(meetingCommentTextArea, VERTICAL_SCROLLBAR_AS_NEEDED,
				HORIZONTAL_SCROLLBAR_NEVER);
		meetingCommentField.setMinimumSize(meetingCommentField.getPreferredSize());
		GUITools.scrollToTop(meetingCommentField);

		protocolCommentField = new JScrollPane(protocolCommentTextArea, VERTICAL_SCROLLBAR_AS_NEEDED,
				HORIZONTAL_SCROLLBAR_NEVER);
		protocolCommentField.setMinimumSize(protocolCommentField.getPreferredSize());
		GUITools.scrollToTop(protocolCommentField);

		GUITools.addComponent(this, layout, impressionLabel, 0, 1, 1, 1, 1, 0, 15, 10, 0, 10, NONE, NORTHWEST);
		GUITools.addComponent(this, layout, impressionField, 0, 2, 1, 1, 1, 1, 5, 10, 0, 10, BOTH, NORTHWEST);
		GUITools.addComponent(this, layout, recommendationLabel, 0, 3, 1, 1, 1, 0, 15, 10, 0, 10, NONE, NORTHWEST);
		GUITools.addComponent(this, layout, recommendationField, 0, 4, 1, 1, 1, 0, 5, 10, 0, 10, NONE, NORTHWEST);
		GUITools.addComponent(this, layout, meetingCommentLabel, 0, 5, 1, 1, 1, 0, 15, 10, 0, 10, NONE, NORTHWEST);
		GUITools.addComponent(this, layout, meetingCommentField, 0, 6, 1, 1, 1, 1, 5, 10, 0, 10, BOTH, NORTHWEST);
		GUITools.addComponent(this, layout, protocalCommentLabel, 0, 7, 1, 1, 1, 0, 15, 10, 0, 10, NONE, NORTHWEST);
		GUITools.addComponent(this, layout, protocolCommentField, 0, 8, 1, 1, 1, 1, 5, 10, 0, 10, BOTH, NORTHWEST);

		this.setBorder(new EmptyBorder(0, 10, 20, 10));
	}

	public void updateTabData(Review review) {
		impressionTextArea.setText(review.getImpression());
		recommendationField.setText(Data.translate(review.getRecommendation()));
		updateFontSizes(impressionTextArea, impressionField);
	}

	public void updateTabData(Protocol protocol) {
		protocolCommentTextArea.setText(protocol.getComments());
		updateFontSizes(protocolCommentTextArea, protocolCommentField);
	}

	public void updateTabData(Meeting meeting) {
		meetingCommentTextArea.setText(meeting.getComments());
		updateFontSizes(meetingCommentTextArea, meetingCommentField);
	}

	private void updateFontSizes(JTextArea area, JScrollPane scrollPane) {
		// TODO: check what to do.
		// SwingUtilities.invokeLater(() -> {
		// area.setFont(TEXT_FONT);
		// Thread thread = new Thread(() -> {
		// int size = area.getFont().getSize();
		// while (scrollPane.getVerticalScrollBar().isVisible() && size > 0) {
		// size = Math.max(1, size - 5);
		// area.setText(area.getText());
		// area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
		// try {
		// Thread.sleep(5);
		// } catch (InterruptedException e) {
		// }
		// }
		//
		// });
		// thread.start();
		// });
	}
}
