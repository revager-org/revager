package org.revager.gui.presentationView;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Review;
import org.revager.gui.MainFrame;
import org.revager.gui.UI;

public class PresentationFrame extends JFrame {

	public static final int GENERAL_TAB_ID = 0;
	public static final int FINDINGS_TAB_ID = 1;

	private static final long serialVersionUID = 1459457125329301470L;

	private JTabbedPane tabbedPane = new JTabbedPane();
	private PresentationFindingsTab tabPanelFinding;
	private PresentationProtocolTab tabPanelProtocol;

	public PresentationFrame() {
		super();
		buildGUI();
		Application instance = Application.getInstance();
		MainFrame mainFrame = UI.getInstance().getMainFrame();
		ResiData resiData = Data.getInstance().getResiData();

		Protocol protocol = mainFrame.getSelectedProtocol();
		Meeting meeting = instance.getProtocolMgmt().getMeeting(protocol);
		Review review = resiData.getReview();
		for (Finding finding : protocol.getFindings()) {
			finding.addObserver((o, arg) -> {
				displayFindingsTab();
				tabPanelFinding.updateFinding((Finding) o);
			});
		}

		review.addObserver((o, arg) -> {
			displayGeneralTab();
			tabPanelProtocol.updateTabData((Review) o);
		});

		meeting.addObserver((o, arg) -> {
			displayGeneralTab();
			tabPanelProtocol.updateTabData((Meeting) o);
		});

		protocol.addObserver((o, arg) -> {
			displayGeneralTab();
			tabPanelProtocol.updateTabData((Protocol) o);
			if (arg instanceof Finding) {
				Finding finding = (Finding) arg;
				tabPanelFinding.updateFinding(finding);
				finding.addObserver((o2, arg2) -> {
					displayFindingsTab();
					tabPanelFinding.updateFinding((Finding) o2);
				});
			}
		});

	}

	private void buildGUI() {
		setTitle("tasdfsdf");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		SwingUtilities.invokeLater(() -> {
			tabPanelProtocol = new PresentationProtocolTab();
			tabPanelFinding = new PresentationFindingsTab();
			tabbedPane.add(tabPanelProtocol, GENERAL_TAB_ID);
			tabbedPane.add(tabPanelFinding, FINDINGS_TAB_ID);

			// Hide bar displaying the tabs to select.
			tabbedPane.setUI(new BasicTabbedPaneUI() {
				@Override
				protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
					return 0;
				}
			});

			add(tabbedPane);
			tabbedPane.setSelectedIndex(GENERAL_TAB_ID);
		});
	}

	public void displayGeneralTab() {
		tabbedPane.setSelectedIndex(GENERAL_TAB_ID);
	}

	public void displayFindingsTab() {
		tabbedPane.setSelectedIndex(FINDINGS_TAB_ID);
	}

}
