package org.revager.gui.presentationView;

import static org.revager.app.model.Data._;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.revager.app.Application;
import org.revager.app.ProtocolManagement;
import org.revager.app.ReviewManagement;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

public class PresentationProtocolTab extends JPanel {

	private static final long serialVersionUID = 1377208308219097378L;

	private GridBagLayout gridBagLayout ;

	private Protocol currentProt;
	private JComboBox recBx;
	private Meeting currentMeet = null;
	private JTextArea meetCommTxtArea;
	private JTextArea protCommTxtArea;
	private JScrollPane meetCommScrllPn;
	private JScrollPane protCommScrllPn;

	private ProtocolManagement protMgmt = Application.getInstance().getProtocolMgmt();
	private ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();


	public PresentationProtocolTab() {
		super();
		gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);

		JLabel recLbl = new JLabel(_("Final recommendation for the product:"));
		recLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel meetCommLbl = new JLabel(_("Comments on the meeting:"));
		meetCommLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		JLabel protCommLbl = new JLabel(_("Comments on the list of findings:"));
		protCommLbl.setFont(UI.PROTOCOL_FONT_BOLD);

		meetCommTxtArea = new JTextArea();
		meetCommTxtArea.setRows(4);
		meetCommTxtArea.setFont(UI.PROTOCOL_FONT);

		protCommTxtArea = new JTextArea();
		protCommTxtArea.setRows(4);
		protCommTxtArea.setFont(UI.PROTOCOL_FONT);

		recBx = new JComboBox();
		recBx.setEditable(true);
		recBx.setFont(UI.PROTOCOL_FONT);

//		recBx.setSelectedIndex(0);
		recBx.setSelectedItem(revMgmt.getRecommendation().trim());

		meetCommScrllPn = GUITools.setIntoScrllPn(meetCommTxtArea);
		meetCommScrllPn.setMinimumSize(meetCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(meetCommScrllPn);

		protCommScrllPn = GUITools.setIntoScrllPn(protCommTxtArea);
		protCommScrllPn.setMinimumSize(protCommScrllPn.getPreferredSize());
		GUITools.scrollToTop(protCommScrllPn);

		GUITools.addComponent(this, gridBagLayout, recLbl, 0, 3, 2, 1, 0.0, 0.0, 20, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, recBx, 0, 4, 2, 1, 1.0, 0.0, 5, 10, 0, 10,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, meetCommLbl, 0, 5, 1, 1, 1.0, 0.0, 25, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, meetCommScrllPn, 0, 6, 1, 1, 1.0, 1.0, 5, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, protCommLbl, 1, 5, 1, 1, 1.0, 0.0, 25, 10, 0, 10,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gridBagLayout, protCommScrllPn, 1, 6, 1, 1, 1.0, 1.0, 5, 10, 0, 10,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);

		this.setBorder(new EmptyBorder(0, 10, 20, 10));
	}
}
