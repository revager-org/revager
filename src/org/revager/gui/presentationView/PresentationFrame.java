package org.revager.gui.presentationView;

import static org.revager.app.model.Data.translate;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Review;
import org.revager.gui.UI;

public class PresentationFrame extends JFrame {

	public static final int FINDINGS_TAB_ID = 0;
	public static final int GENERAL_TAB_ID = 1;

	private static final long serialVersionUID = 1459457125329301470L;

	private JTabbedPane tabbedPane = new JTabbedPane();
	private PresentationFindingsTab tabPanelFinding;
	private PresentationProtocolTab tabPanelProtocol;
	/**
	 * Always use the same listener, to prevent that we register, with different
	 * listener instances, multiple times.
	 */
	private final transient Observer findingListener = (Observable o, Object arg) -> {
		displayFindingsTab();
		tabPanelFinding.updateFinding((Finding) o);
	};

	/**
	 * Always use the same listener, to prevent that we register, with different
	 * listener instances, multiple times.
	 */
	private final transient Observer reviewListener = (Observable o, Object arg) -> {
		displayGeneralTab();
		tabPanelProtocol.updateTabData((Review) o);
	};
	/**
	 * Always use the same listener, to prevent that we register, with different
	 * listener instances, multiple times.
	 */
	private final transient Observer meetingListener = (Observable o, Object arg) -> {
		displayGeneralTab();
		tabPanelProtocol.updateTabData((Meeting) o);
	};
	/**
	 * Always use the same listener, to prevent that we register, with different
	 * listener instances, multiple times.
	 */
	private final transient Observer protocolListener = (Observable o, Object arg) -> {
		if (arg instanceof Finding) {
			// A finding was added to the protocol. We don't care, that the
			// protocol "changed".
			Finding finding = (Finding) arg;
			finding.addObserver(findingListener);
		} else {
			displayGeneralTab();
			tabPanelProtocol.updateTabData((Protocol) o);
		}
	};

	@Override
	public void setVisible(boolean b) {
		addObjectListeners();
		super.setVisible(b);
	}

	public PresentationFrame() {
		super();
		buildGUI();
	}

	private void addObjectListeners() {
		Review review = Data.getInstance().getResiData().getReview();
		Meeting meeting = UI.getInstance().getProtocolFrame().getMeeting();
		Protocol protocol = UI.getInstance().getProtocolFrame().getMeeting().getProtocol();
		review.addObserver(reviewListener);
		meeting.addObserver(meetingListener);
		protocol.addObserver(protocolListener);
		for (Finding finding : protocol.getFindings()) {
			finding.addObserver(findingListener);
		}
	}

	private void buildGUI() {
		// TODO: Translate string.
		setTitle(translate("Presentation View"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		tabPanelProtocol = new PresentationProtocolTab();
		tabPanelFinding = new PresentationFindingsTab();
		tabbedPane.add(tabPanelFinding, FINDINGS_TAB_ID);
		tabbedPane.add(tabPanelProtocol, GENERAL_TAB_ID);
		displayGeneralTab();

		// Hide bar displaying the tabs to select.
		tabbedPane.setUI(new BasicTabbedPaneUI() {
			@Override
			protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
				return 0;
			}
		});
		add(tabbedPane);
	}

	public void displayGeneralTab() {
		tabbedPane.setSelectedIndex(GENERAL_TAB_ID);
	}

	public void displayFindingsTab() {
		tabbedPane.setSelectedIndex(FINDINGS_TAB_ID);
	}

}
