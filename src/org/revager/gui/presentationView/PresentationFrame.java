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
import org.revager.app.ReviewManagement;
import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Protocol;
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
		ResiData resiData = Data.getInstance().getResiData();
		resiData.getReview().addObserver(new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				displayGeneralTab();
			}
		});
		
	
		
		buildGUI();
	}

	private void buildGUI() {
		setTitle("tasdfsdf");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
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
			}
		});
	}

	public void displayGeneralTab() {
		tabbedPane.setSelectedIndex(GENERAL_TAB_ID);
	}

	public void displayFindingsTab() {
		tabbedPane.setSelectedIndex(FINDINGS_TAB_ID);
	}

}