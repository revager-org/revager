package org.revager.gui.presentationView;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static org.revager.app.model.Data.translate;

import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Review;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

public class PresentationProtocolTab extends JPanel {

	private static final long serialVersionUID = 1377208308219097378L;

	private GridBagLayout layout = new GridBagLayout();
	private HighlightedTextArea impressionTextArea;
	private HighlightedTextArea recommendationField;
	private HighlightedTextArea meetingCommentTextArea;
	private HighlightedTextArea protocolCommentTextArea;
	private JScrollPane impressionField;
	private JScrollPane meetingCommentField;
	private JScrollPane protocolCommentField;

	public PresentationProtocolTab() {
		super();
		this.setLayout(layout);

		JLabel impressionLabel = new JLabel(translate("General impression of the product:"));
		impressionLabel.setFont(UI.HUGE_HUGE_FONT_BOLD);
		impressionTextArea = createTextArea(
				translate("What is your general impression of the prodcut? Write this down before documenting "
						+ "findings. When you finished with documenting findings, continue with "
						+ "the recommendation and the comments of the meeting and of the list of findings."));

		JLabel recommendationLabel = new JLabel(translate("Final recommendation for the product:"));
		recommendationLabel.setFont(UI.HUGE_HUGE_FONT_BOLD);
		recommendationField = createTextArea(StringUtils.join(Data.getDefaultRecommendations(), ", "));
		recommendationField.setRows(2);

		JLabel meetingCommentLabel = new JLabel(translate("Comments on the meeting:"));
		meetingCommentLabel.setFont(UI.HUGE_HUGE_FONT_BOLD);
		meetingCommentTextArea = createTextArea(translate("Is there anything to say about the meeting?"));

		JLabel protocalCommentLabel = new JLabel(translate("Comments on the list of findings:"));
		protocalCommentLabel.setFont(UI.HUGE_HUGE_FONT_BOLD);
		protocolCommentTextArea = createTextArea(translate("Is there anything to say about the findings?"));

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
		GUITools.addComponent(this, layout, recommendationField, 0, 4, 1, 1, 1, 0, 5, 10, 0, 10, BOTH, NORTHWEST);
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

	private HighlightedTextArea createTextArea(String placeholderText) {
		HighlightedTextArea textArea = new HighlightedTextArea(UI.HUGE_HUGE_FONT, placeholderText);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		return textArea;
	}

	private void updateFontSizes(JTextArea area, JScrollPane scrollPane) {
		// TODO: check what to do.
		// try {
		// SwingUtilities.invokeAndWait(() -> {
		// area.setFont(TEXT_FONT);
		// Thread thread = new Thread(() -> {
		// int size = area.getFont().getSize();
		// while (scrollPane.getVerticalScrollBar().isVisible() && size > 0) {
		// size = Math.max(1, size - 1);
		// // area.setText(area.getText());
		// area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
		// try {
		// Thread.sleep(5);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// });
		// thread.start();
		// });
		// } catch (InvocationTargetException | InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
