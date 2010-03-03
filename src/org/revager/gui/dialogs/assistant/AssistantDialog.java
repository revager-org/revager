package org.revager.gui.dialogs.assistant;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.plaf.SeparatorUI;

import org.revager.app.ResiFileFilter;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAttendee;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Role;
import org.revager.gui.AbstractDialog;
import org.revager.gui.AbstractDialogPanel;
import org.revager.gui.StrengthPopupWindow;
import org.revager.gui.UI;
import org.revager.gui.StrengthPopupWindow.ButtonClicked;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.InitializeNewReviewAction;
import org.revager.gui.actions.OpenAspectsManagerAction;
import org.revager.gui.actions.attendee.SelectAttOutOfDirAction;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.helpers.HLink;
import org.revager.gui.helpers.LinkGroup;
import org.revager.gui.helpers.VLink;
import org.revager.gui.models.StrengthTableModel;
import org.revager.gui.workers.LoadReviewWorker;
import org.revager.gui.workers.LoadStdCatalogsWorker;
import org.revager.tools.GUITools;

@SuppressWarnings("serial")
public class AssistantDialog extends AbstractDialog{
	
	public static enum VisiblePnls {FIRST_SCREEN,OPEN_REVIEW,ADD_ATTENDEE}; 
	
	private VisiblePnls currentPnl;
	private AbstractDialogPanel firstScreenPanel=new AbstractDialogPanel(this);
	private AbstractDialogPanel openReviewPanel=new AbstractDialogPanel(this);
	private AbstractDialogPanel addAttendeePanel=new AbstractDialogPanel(this);
	
	private ImageIcon revagerIcon=Data.getInstance().getIcon("revager_50x50.png");
	private AppAttendee currentAppAttendee;
	private List<String> strengthList;
	
	/*
	 * 
	 * Actions
	 *
	 *
	 */
	private Action browseAction=ActionRegistry.getInstance().get(SelectAttOutOfDirAction.class.getName());
	
	private ActionListener addStrengthAction=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ev) {
			final String title = Data.getInstance().getLocaleStr("popup.addStrength.title");

			SwingWorker<Void, Void> showPopupWorker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					StrengthPopupWindow popup = new StrengthPopupWindow(UI.getInstance().getAssistantDialog(), title);

					/*
			 		* Import the standard catalogs, if no catalogs exist in
			 		* the database
					*/
					try {
						if (Data.getInstance().getAppData().getNumberOfCatalogs() == 0) {
							switchToProgressMode(Data.getInstance().getLocaleStr("status.importingCatalog"));

							LoadStdCatalogsWorker catalogWorker = new LoadStdCatalogsWorker();

							catalogWorker.execute();

							while (!catalogWorker.isDone()
									&& !catalogWorker.isCancelled()) {
								Thread.sleep(500);
							}

							switchToEditMode();
						}
					} catch (Exception exc) {
					/*
				 	* do nothing
				 	*/
					}

					/*
					 * Show the popup
					 */
					popup.setVisible(true);

					if (popup.getButtonClicked() == ButtonClicked.OK) {
						for (String cat : popup.getSelCateList()) {
							if (!strengthList.contains(cat)) {
							strengthList.add(cat);
							}
						}

					stm.fireTableDataChanged();

					updateStrengthButtons();
				}

				return null;
				}
			};
		
		showPopupWorker.execute();
		}
	};
	
	private ActionListener removeStrengthAction=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ev) {
			int selRow = strengthTbl.getSelectedRow();

			String str = (String) stm.getValueAt(selRow, 1);

			strengthList.remove(str);

			stm.fireTableDataChanged();

			updateStrengthButtons();
		}
	};
	
	private ActionListener goToQuickmode= new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPnl=VisiblePnls.ADD_ATTENDEE;
			updateMessage();
			updateContents();
			updateWizardBttns();
		}
		
	};
	
	private ActionListener goToFirstScreen= new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPnl=VisiblePnls.FIRST_SCREEN;
			updateMessage();
			updateContents();
			updateWizardBttns();
		}
		
	};
	
	private ActionListener goToOpenReview= new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPnl=VisiblePnls.OPEN_REVIEW;
			updateMessage();
			updateContents();
			updateWizardBttns();
		}
		
	};
	
	private ActionListener exitApp= new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
		
	};
	
	private ActionListener initQuickRev=ActionRegistry.getInstance().get(InitializeNewReviewAction.class.getName()); 
	private ActionListener selectLanguageAction = ActionRegistry.getInstance().get(SelectLanguageAction.class.getName());
	private ActionListener openAspMngrAction = ActionRegistry.getInstance().get(OpenAspectsManagerAction.class.getName());

	
	private ActionListener openExistRev= new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				List<String> lastRevs = Data.getInstance().getAppData().getLastReviews();
				int index=lastRevsGrp.getSelectedLinkIndex();
				String revPath=lastRevs.get(index);
				new LoadReviewWorker(revPath).execute();
			} catch (DataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
	};
	
	private ActionListener openAnotherRev= new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			FileChooser fileChooser = UI.getInstance().getFileChooser();

			if (fileChooser.showDialog(UI.getInstance().getAssistantDialog(), FileChooser.MODE_OPEN_FILE,ResiFileFilter.TYPE_REVIEW) == FileChooser.SELECTED_APPROVE) {
				String reviewPath = fileChooser.getFile().getAbsolutePath();
				new LoadReviewWorker(reviewPath).execute();
			}
		}};
	
	
	/*
	 * 
	 * TableModels
	 *
	 *
	 */
	private StrengthTableModel stm = null;
	
	/*
	 * 
	 * Components of the firstScreenPanel
	 *
	 *
	 */
	
	private String quickRevTooltipStrng=Data.getInstance().getLocaleStr("assistantDialog.quickRev.rollover");
	private String newRevTooltipStrng=Data.getInstance().getLocaleStr("assistantDialog.newRev.rollover");
	private String openRevTooltipStrng=Data.getInstance().getLocaleStr("assistantDialog.openRev.rollover");
	private String selectLanguageTooltipStrng=Data.getInstance().getLocaleStr("assistantDialog.selectLanguage.rollover");
	private String openAspectsMngrTooltipStrng=Data.getInstance().getLocaleStr("assistantDialog.openAspectsMngr.rollover");

	private GridBagLayout gbl1 = new GridBagLayout();
	
	private String firstScreenDescStrng=Data.getInstance().getLocaleStr("assistantDialog.firstDesc");
	
	private String newReviewStrng=Data.getInstance().getLocaleStr("assistantDialog.newReview");
	private String quickstartStrng=Data.getInstance().getLocaleStr("assistantDialog.quickstart");
	private String openRevStrng=Data.getInstance().getLocaleStr("assistantDialog.openReview");
	private String languageStrng=Data.getInstance().getLocaleStr("assistantDialog.selectLanguage");
	private String aspectsManagerStrng=Data.getInstance().getLocaleStr("assistantDialog.aspectsManager");
	
	private ImageIcon moderatorIcon=Data.getInstance().getIcon("moderator_128x128_0.png");
	private ImageIcon moderatorRolloverIcon=Data.getInstance().getIcon("moderator_128x128.png");
	private ImageIcon openRevIcon=Data.getInstance().getIcon("scribe_128x128_0.png");
	private ImageIcon openRevRolloverIcon=Data.getInstance().getIcon("scribe_128x128.png");
	private ImageIcon quickstartIcon=Data.getInstance().getIcon("instantReview_128x128_0.png");
	private ImageIcon quickstartRolloverIcon=Data.getInstance().getIcon("instantReview_128x128.png");
	private ImageIcon languageIcon=Data.getInstance().getIcon("language_31x20_0.png");
	private ImageIcon languageRolloverIcon=Data.getInstance().getIcon("language_31x20.png");
	private ImageIcon aspectsManagerIcon=Data.getInstance().getIcon("aspectsManager_25x25_0.png");
	private ImageIcon aspectsManagerRolloverIcon=Data.getInstance().getIcon("aspectsManager_25x25.png");
	
	private JSeparator dottedSprtr= new JSeparator(SwingConstants.HORIZONTAL);
	
	private VLink newReviewLnk=new VLink(newReviewStrng,moderatorIcon,moderatorRolloverIcon);
	private VLink quickstartLnk=new VLink(quickstartStrng,quickstartIcon,quickstartRolloverIcon);
	private VLink openReviewLnk=new VLink(openRevStrng,openRevIcon,openRevRolloverIcon);
	private HLink selectLanguageLnk=new HLink(languageStrng,languageIcon,languageRolloverIcon,null);
	private HLink openAspManagerLnk=new HLink(aspectsManagerStrng,aspectsManagerIcon,aspectsManagerRolloverIcon,null);
	
	/*
	 * 
	 * Components of the openReviewPanel
	 *
	 *
	 */
	
	private GridBagLayout gbl2 = new GridBagLayout();
	
	private String openRevDescStrng=Data.getInstance().getLocaleStr("assistantDialog.openDesc");
	private String moderatorStrng=Data.getInstance().getLocaleStr("mode.moderator");
	private String scribeStrng=Data.getInstance().getLocaleStr("mode.scribeOrSingle");
	private String anotherRevStrng=Data.getInstance().getLocaleStr("assistantDialog.selectAnotherRev");
	private String noRevsStrng=Data.getInstance().getLocaleStr("assistantDialog.noRevs");;
	private String firstRevStrng;
	private String secondRevStrng;
	private String thirdRevStrng;
	private String fourthRevStrng;
	
	
	private ImageIcon smallModeratorIcon=Data.getInstance().getIcon("moderator_50x50_0.png");
	private ImageIcon smallModeratorRolloverIcon=Data.getInstance().getIcon("moderator_50x50.png");
	private ImageIcon scribeIcon=Data.getInstance().getIcon("scribe_50x50_0.png");
	private ImageIcon scribeRolloverIcon=Data.getInstance().getIcon("scribe_50x50.png");
	private ImageIcon reviewIcon=Data.getInstance().getIcon("review_40x40_0.png");
	private ImageIcon reviewRolloverIcon=Data.getInstance().getIcon("review_40x40.png");
	private ImageIcon browseIcon=Data.getInstance().getIcon("open_40x40_0.png");
	private ImageIcon browseRolloverIcon=Data.getInstance().getIcon("open_40x40.png");
	
	private LinkGroup modeGrp= new LinkGroup(); 
	private HLink moderatorLnk = new HLink(moderatorStrng, smallModeratorIcon, smallModeratorRolloverIcon,modeGrp);
	private HLink scribeSingleRevLnk= new HLink(scribeStrng, scribeIcon, scribeRolloverIcon,modeGrp);
	
	private LinkGroup lastRevsGrp= new LinkGroup(); 
	private HLink firstReviewLnk;
	private HLink secondReviewLnk;
	private HLink thirdReviewLnk;
	private HLink fourthReviewLnk;
	private HLink anotherReviewLnk;
	
	private JLabel noRevsLbl=new JLabel(noRevsStrng);
	
	private Vector<String>lastRevsVector=getLastReviews();

	
	/*
	 * 
	 * Components of the addAttendeePanel
	 *
	 *
	 */
	
	private GridBagLayout gbl3 = new GridBagLayout();
	
	private boolean nameMissing;
	
	private String addAttDescStrng=Data.getInstance().getLocaleStr("addYourself.description");
	
	
	private String reviewerStrng=Data.getInstance().getLocaleStr("role.reviewer");
	private String nameStrng=Data.getInstance().getLocaleStr("attendee.name");
	private String contactStrng=Data.getInstance().getLocaleStr("attendee.contact");
	private String roleStrng=Data.getInstance().getLocaleStr("attendee.role");
	private String strengthStrng=Data.getInstance().getLocaleStr("attendee.priorities");
	private String directoryTooltipStrng=Data.getInstance().getLocaleStr("attendee.directory");
	private String addStrengthTooltipStrng=Data.getInstance().getLocaleStr("attendeeDialog.addStrength");
	private String removeStrengthTooltipStrng=Data.getInstance().getLocaleStr("attendeeDialog.remStrength");
	
	private ImageIcon directoryIcon=Data.getInstance().getIcon("directory_25x25_0.png");
	private ImageIcon directoryRolloverIcon=Data.getInstance().getIcon("directory_25x25.png");
	private ImageIcon addStrengthIcon=Data.getInstance().getIcon("add_25x25_0.png");
	private ImageIcon addStrengthRolloverIcon=Data.getInstance().getIcon("add_25x25.png");
	private ImageIcon removeStrengthIcon=Data.getInstance().getIcon("remove_25x25_0.png");
	private ImageIcon removeStrengthRolloverIcon=Data.getInstance().getIcon("remove_25x25.png");
	
	private JLabel nameLbl=new JLabel(nameStrng);
	private JLabel contactLbl=new JLabel(contactStrng);
	private JLabel roleLbl=new JLabel(roleStrng);
	private JLabel strengthLbl=new JLabel(strengthStrng);
	
	private JButton directoryBttn;
	private JButton addStrengthBttn;
	private JButton removeStrengthBttn;
	
	public JTextField nameTxtFld;
	private JTextArea contactTxtArea;
	private JComboBox roleCmbBx;
	private JTable strengthTbl;
	private JScrollPane contactScrllPn;
	private JPanel buttonPnl = null;
	
	private FocusListener roleCmbBxListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() != strengthTbl) {
				if (strengthTbl.getRowCount() > 0) {
					strengthTbl.removeRowSelectionInterval(0, strengthTbl
							.getRowCount() - 1);

					updateStrengthButtons();
				}
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	};

	
	//TODO TableModel, BackBttn,ConfirmBttn
	
	/*
	 * 
	 * Wizard buttons
	 *
	 *
	 */
	private JButton backBttn = new JButton();
	private JButton finishBttn = new JButton();
	private ImageIcon confirmIcon = Data.getInstance().getIcon("buttonOk_16x16.png");
	private String confirmString = Data.getInstance().getLocaleStr("confirm");
	private String finishString = Data.getInstance().getLocaleStr("closeApplication");
	private ImageIcon finishIcon = Data.getInstance().getIcon("buttonExit_16x16.png");
	private String localMode;
	
	

	
	/*
	 * 
	 * Getter and Setter
	 *
	 *
	 */
	public VisiblePnls getCurrentPnl() {
		return currentPnl;
	}

	public void setCurrentPnl(VisiblePnls currentPnl) {
		this.currentPnl = currentPnl;
	}
	
	
	/*
	 * 
	 * Constructor
	 * 
	 * 
	 */
	public AssistantDialog(Frame parent) {
		super(parent);

		createFirstScreenPnl();
		createAddAttendeePnl();
		createOpenReviewPnl();
		defineWizardBttns();
		
		setIcon(revagerIcon);
		getContentPane().setLayout(new BorderLayout());
		updateContents();
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setMinimumSize(new Dimension(600, 500));

		pack();
	}
	
	private void createFirstScreenPnl(){
		currentPnl=VisiblePnls.FIRST_SCREEN;
		
		quickstartLnk.setFather(firstScreenPanel);
		newReviewLnk.setFather(firstScreenPanel);
		openReviewLnk.setFather(firstScreenPanel);
		selectLanguageLnk.setFather(firstScreenPanel);
		openAspManagerLnk.setFather(firstScreenPanel);
		
		newReviewLnk.addActionListener(ActionRegistry.getInstance().get(InitializeNewReviewAction.class.getName()));
		
		backBttn.addActionListener(goToFirstScreen);
		openReviewLnk.addActionListener(goToOpenReview);
		quickstartLnk.addActionListener(goToQuickmode);
		selectLanguageLnk.addActionListener(selectLanguageAction);
		openAspManagerLnk.addActionListener(openAspMngrAction);
		
		selectLanguageLnk.setUnderlined(true);
		openAspManagerLnk.setUnderlined(true);
		
		
		quickstartLnk.addRolloverText(quickRevTooltipStrng);
		
		openReviewLnk.addRolloverText(openRevTooltipStrng);
		newReviewLnk.addRolloverText(newRevTooltipStrng);
		selectLanguageLnk.addRolloverText(selectLanguageTooltipStrng);
		openAspManagerLnk.addRolloverText(openAspectsMngrTooltipStrng);
		
		firstScreenPanel.setLayout(gbl1);
		GUITools.addComponent(firstScreenPanel, gbl1, newReviewLnk, 0, 0, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(firstScreenPanel, gbl1, quickstartLnk, 1, 0, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(firstScreenPanel, gbl1, openReviewLnk, 2, 0, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(firstScreenPanel, gbl1, dottedSprtr, 0, 1, 3, 1, 1.0, 0.0, 20, 20, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		GUITools.addComponent(firstScreenPanel, gbl1, selectLanguageLnk, 0, 2, 1, 1, 1.0, 0.0, 20, 20, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		GUITools.addComponent(firstScreenPanel, gbl1, openAspManagerLnk, 2, 2, 1, 1, 1.0, 0.0, 20, 0, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		
	}
	
	private void createOpenReviewPnl(){
		
		openReviewPanel.setLayout(gbl2);
		localMode="moderator";


		moderatorLnk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				localMode="moderator";
				Data.getInstance().setMode("moderator");
			}});
		
		scribeSingleRevLnk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				localMode="scribe";
				Data.getInstance().setMode("scribe");
			}});
		
		modeGrp.addLink(moderatorLnk);
		modeGrp.addLink(scribeSingleRevLnk);
		modeGrp.selectLink(moderatorLnk);
		
		
		GUITools.addComponent(openReviewPanel, gbl2, moderatorLnk, 0, 0, 1, 1, 1.0, 0.0, 0, 20, 0, 84, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(openReviewPanel, gbl2, scribeSingleRevLnk, 0, 1, 1, 1, 1.0, 0.0, 0, 20, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(openReviewPanel, gbl2, new JSeparator(SwingConstants.VERTICAL), 1, 0, 1, 6, 0.0, 1.0, 0, 0, 0, 0, GridBagConstraints.VERTICAL, GridBagConstraints.NORTHWEST);
			
		
		try{
			firstRevStrng=lastRevsVector.get(0);
			firstReviewLnk=new HLink(firstRevStrng,reviewIcon,reviewRolloverIcon,lastRevsGrp);
			lastRevsGrp.addLink(firstReviewLnk);
			lastRevsGrp.selectLink(firstReviewLnk);
			GUITools.addComponent(openReviewPanel, gbl2, firstReviewLnk, 2, 0, 1, 1, 1.0, 0.0, 0, 40, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
			
		}catch(Exception e){
			noRevsLbl.setFont(new Font(Font.SANS_SERIF,Font.ITALIC, 12));
			GUITools.addComponent(openReviewPanel, gbl2, noRevsLbl, 2, 0, 1, 1, 1.0, 0.0, 15, 80, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		}
		try{
			secondRevStrng=lastRevsVector.get(1);
			secondReviewLnk=new HLink(secondRevStrng,reviewIcon,reviewRolloverIcon,lastRevsGrp);
			lastRevsGrp.addLink(secondReviewLnk);
			GUITools.addComponent(openReviewPanel, gbl2, secondReviewLnk, 2, 1, 1, 1, 1.0, 0.0, 0, 40, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
			
		}catch(Exception e){
			
		}
		try{
			thirdRevStrng=lastRevsVector.get(2);
			thirdReviewLnk=new HLink(thirdRevStrng,reviewIcon,reviewRolloverIcon,lastRevsGrp);
			lastRevsGrp.addLink(thirdReviewLnk);
			GUITools.addComponent(openReviewPanel, gbl2, thirdReviewLnk, 2, 2, 1, 1, 1.0, 0.0, 0, 40, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
			
		}catch(Exception e){
		
		}
		
		try{
			fourthRevStrng=lastRevsVector.get(3);
			fourthReviewLnk=new HLink(fourthRevStrng,reviewIcon,reviewRolloverIcon,lastRevsGrp);
			lastRevsGrp.addLink(fourthReviewLnk);
			GUITools.addComponent(openReviewPanel, gbl2, fourthReviewLnk, 2, 3, 1, 1, 1.0, 0.0, 10, 40, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
			
		}catch(Exception e){
			
		}
		anotherReviewLnk=new HLink(anotherRevStrng,browseIcon,browseRolloverIcon,null);
		anotherReviewLnk.addActionListener(openAnotherRev);
		GUITools.addComponent(openReviewPanel, gbl2, anotherReviewLnk, 2, 4, 1, 1, 1.0, 1.0, 30, 30, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTHWEST);
		
	}
	
	private void createAddAttendeePnl(){
		addAttendeePanel.setLayout(gbl3);
		
		getStrengthList();

		nameLbl = new JLabel(nameStrng);
		contactLbl = new JLabel(contactStrng);
		roleLbl = new JLabel(roleStrng);
		strengthLbl = new JLabel(strengthStrng);

		nameTxtFld = new JTextField();
		contactTxtArea = new JTextArea();
		contactTxtArea.addFocusListener(roleCmbBxListener);
		contactScrllPn = GUITools.setIntoScrllPn(contactTxtArea);
		roleCmbBx = new JComboBox();
		roleCmbBx.addFocusListener(roleCmbBxListener);

		for (Role x : Role.values()) {
			String roleString = "role.".concat(x.value());
			roleCmbBx.addItem(Data.getInstance().getLocaleStr(roleString));
		}

		roleCmbBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				updateStrengthTable();
			}
		});

		strengthTbl = GUITools.newStandardTable(null, false);

		directoryBttn = GUITools.newImageButton(directoryIcon, directoryRolloverIcon);
		directoryBttn.setToolTipText(directoryTooltipStrng);
		directoryBttn.addActionListener(browseAction);
		buttonPnl = new JPanel(new GridLayout(3, 1));

		strengthTbl.addFocusListener(roleCmbBxListener);
		strengthTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateStrengthButtons();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		addStrengthBttn = GUITools.newImageButton();
		addStrengthBttn.setIcon(addStrengthIcon);
		addStrengthBttn.setRolloverIcon(addStrengthRolloverIcon);
		addStrengthBttn.setToolTipText(addStrengthTooltipStrng);

		addStrengthBttn.addActionListener(addStrengthAction);

		buttonPnl.add(addStrengthBttn);

		removeStrengthBttn = GUITools.newImageButton();
		removeStrengthBttn.setIcon(removeStrengthIcon);
		removeStrengthBttn.setRolloverIcon(removeStrengthRolloverIcon);
		removeStrengthBttn.setToolTipText(removeStrengthTooltipStrng);
		removeStrengthBttn.addActionListener(removeStrengthAction);

		buttonPnl.add(removeStrengthBttn);

		JScrollPane strScrllPn = GUITools.setIntoScrollPane(strengthTbl);

		GUITools.addComponent(addAttendeePanel, gbl3, nameLbl, 0, 0, 1, 1, 0, 0, 0,
				20, 0, 20, GridBagConstraints.NONE,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, nameTxtFld, 1, 0, 3, 1, 1.0, 0,
				0, 20, 0, 0, GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, directoryBttn, 4, 0, 1, 1, 0, 0,
			0, 5, 0, 20, GridBagConstraints.NONE,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, contactLbl, 0, 1, 1, 1, 0, 0,
				5, 20, 0, 20, GridBagConstraints.NONE,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, contactScrllPn, 1, 1, 3, 3, 1.0,
				0.5, 5, 20, 0, 0, GridBagConstraints.BOTH,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, roleLbl, 0, 4, 1, 1, 0, 0, 10,
				20, 0, 20, GridBagConstraints.NONE,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, roleCmbBx, 1, 4, 3, 1, 1.0, 0,
				10, 20, 0, 0, GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, strengthLbl, 0, 5, 1, 1, 0, 0,
				17, 20, 0, 20, GridBagConstraints.NONE,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, strScrllPn, 1, 5, 3, 2, 1.0, 0.5,
				15, 20, 0, 0, GridBagConstraints.BOTH,GridBagConstraints.NORTHWEST);
		GUITools.addComponent(addAttendeePanel, gbl3, buttonPnl, 4, 5, 1, 2, 0, 0,
				17, 5, 0, 0, GridBagConstraints.NONE,GridBagConstraints.NORTHWEST);

		setCurrentAttendee(null);

		
	}
	
	/**
	 * defines the wizard buttons ; should be invoked in the constructor once
	 */
	private void defineWizardBttns(){
		
		backBttn.setText(Data.getInstance().getLocaleStr("back"));
		backBttn.setIcon(Data.getInstance().getIcon("buttonBack_16x16.png"));

		finishBttn.setText(finishString);
		finishBttn.setIcon(finishIcon);
		finishBttn.addActionListener(exitApp);

		
		this.addButton(backBttn);
		this.addButton(finishBttn);
		
		//TODO add actions to the buttons
		
		backBttn.setEnabled(false);
	}
	
	
	
	/**
	 * Sets the current attendee.
	 * 
	 * @param att
	 *            the new current attendee
	 */
	public void setCurrentAttendee(Attendee att) {
		//currentAttendee = att;
		currentAppAttendee = null;

		nameTxtFld.setText(null);
		contactTxtArea.setText(null);
		roleCmbBx.setSelectedItem(Data.getInstance().getLocaleStr(
				"role." + Role.REVIEWER.toString().toLowerCase()));

		updateStrengthTable();
	}

	/**
	 * Gets the strength list.
	 * 
	 * @return the strengthList
	 */
	public List<String> getStrengthList() {
		if (strengthList == null) {
			strengthList = new ArrayList<String>();
		}

		return strengthList;
	}

	/**
	 * Sets the current app attendee.
	 * 
	 * @param appAtt
	 *            the new current app attendee
	 */
	public void setCurrentAppAttendee(AppAttendee appAtt) {
		this.currentAppAttendee = appAtt;

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		nameTxtFld.setText(currentAppAttendee.getName());

		try {
			contactTxtArea.setText(currentAppAttendee.getContact());
		} catch (DataException e) {
			JOptionPane.showMessageDialog(this, GUITools.getMessagePane(e
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);
		}

		updateStrengthTable();
	}
	
	/**
	 * updates the wizard buttons and should be invoked after 
	 * the content has changed
	 */
	public void updateWizardBttns(){
		finishBttn.removeActionListener(exitApp);
		finishBttn.removeActionListener(openExistRev);
		finishBttn.removeActionListener(initQuickRev);
		
		if(currentPnl==VisiblePnls.FIRST_SCREEN){
			
			backBttn.setEnabled(false);
			finishBttn.setEnabled(true);
			finishBttn.setText(finishString);
			finishBttn.setIcon(finishIcon);
			finishBttn.addActionListener(exitApp);
			
		}else if(currentPnl==VisiblePnls.ADD_ATTENDEE){
			
			backBttn.setEnabled(true);
			finishBttn.setEnabled(true);
			finishBttn.setText(confirmString);
			finishBttn.setIcon(confirmIcon);
			finishBttn.addActionListener(initQuickRev);
			
			
			if(!nameTxtFld.getText().trim().equals(""))
				
				finishBttn.setEnabled(true);
		
		}else if(currentPnl==VisiblePnls.OPEN_REVIEW){
			
			backBttn.setEnabled(true);
			finishBttn.setEnabled(true);
			finishBttn.setText(openRevStrng);
			finishBttn.setIcon(finishIcon);
			finishBttn.addActionListener(openExistRev);
			if(lastRevsVector.size()==0)
				finishBttn.setEnabled(false);
			
		}
	}
	
	/**
	 * updates the content area
	 */
	public void updateContents(){
		
		this.getContentPane().removeAll();
		
		updateMessage();
		
		if(currentPnl==VisiblePnls.FIRST_SCREEN){
			this.getContentPane().add(firstScreenPanel,BorderLayout.CENTER);
			Data.getInstance().setMode("moderator");
		}else if(currentPnl==VisiblePnls.ADD_ATTENDEE){
			Data.getInstance().setMode("instant");
			this.getContentPane().add(addAttendeePanel,BorderLayout.CENTER);
		}else if(currentPnl==VisiblePnls.OPEN_REVIEW){
			Data.getInstance().setMode(localMode);
			this.getContentPane().add(openReviewPanel,BorderLayout.CENTER);
		}
		this.getContentPane().validate();
		this.getContentPane().repaint();
		
	}
	
	/**
	 * Adds instant reviewer
	 * 
	 */
	public void updateInstantAtt() {

		Role[] roles = Role.values();
		String attContact;

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		String attName = nameTxtFld.getText();
		if (contactTxtArea.getText() != null)
			attContact = contactTxtArea.getText();
		else
			attContact = "";

		Role attRole = roles[roleCmbBx.getSelectedIndex()];

		nameMissing = false;

		String message = "";

		if (attName.trim().equals("")) {
			nameMissing = true;
		}

		if (nameMissing) {
			message = Data.getInstance().getLocaleStr(
					"attendeeDialog.message.noName");

			setMessage(message);
			nameTxtFld.setBorder(UI.MARKED_BORDER_INLINE);
		} else {

			/*
			 * Update the app attendee in the database
			 */
			try {
				if (currentAppAttendee == null) {
					currentAppAttendee = Data.getInstance().getAppData()
							.getAttendee(attName, attContact);

					if (currentAppAttendee == null) {
						currentAppAttendee = Data.getInstance().getAppData()
								.newAttendee(attName, attContact);
					}
				} else {
					currentAppAttendee.setNameAndContact(attName, attContact);
				}

				for (String str : currentAppAttendee.getStrengths()) {
					currentAppAttendee.removeStrength(str);
				}

				for (String str : strengthList) {
					currentAppAttendee.addStrength(str);
				}
			} catch (DataException e1) {
				JOptionPane.showMessageDialog(UI.getInstance()
						.getAssistantDialog(), GUITools.getMessagePane(e1
						.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);
			}

			/*
			 * update the review attendee
			 */
			Attendee newAtt = new Attendee();

			newAtt.setName(attName);
			newAtt.setContact(attContact);
			newAtt.setRole(attRole);

			org.revager.app.Application.getInstance().getAttendeeMgmt().addAttendee(attName,
					attContact, attRole, null);

			setVisible(false);

			UI.getInstance().getAspectsManagerFrame().updateViews();
		}

	}
	
	private void updateMessage(){

		if(currentPnl==VisiblePnls.FIRST_SCREEN){
			setDescription(firstScreenDescStrng);
		}else if(currentPnl==VisiblePnls.ADD_ATTENDEE){
			setDescription(addAttDescStrng);
		}else if(currentPnl==VisiblePnls.OPEN_REVIEW){
			setDescription(openRevDescStrng);
		}

	}
	
	/**
	 * Update strength buttons.
	 */
	private void updateStrengthButtons() {
		if (strengthTbl.getSelectedRow() != -1 && strengthTbl.isEnabled()) {
			removeStrengthBttn.setEnabled(true);
		} else {
			removeStrengthBttn.setEnabled(false);
		}
	}
	
	/**
	 * Update strength table.
	 */
	private void updateStrengthTable() {

		try {
			strengthList = currentAppAttendee.getStrengths();
		} catch (Exception e) {
			strengthList = new ArrayList<String>();
		}

		if (stm == null) {
			stm = new StrengthTableModel();
			strengthTbl.setModel(stm);
		}

		stm.fireTableDataChanged();

		/*
		 * View of strengths
		 */
		boolean enable = false;

		if (((String) roleCmbBx.getSelectedItem()).equals(reviewerStrng)) {
			enable = true;
		}

		addStrengthBttn.setEnabled(enable);
		removeStrengthBttn.setEnabled(false);
		strengthTbl.setEnabled(enable);
		strengthLbl.setEnabled(enable);

		if (enable) {
			strengthTbl.setForeground(Color.BLACK);
		} else {
			strengthTbl.setForeground(Color.GRAY);
		}
	}
	
	/**
	 * Gets the last reviews.
	 * 
	 * @return the last reviews
	 */
	private Vector<String> getLastReviews() {
		Vector<String> vecLastReviews = new Vector<String>();

		try {
			for (String rev : Data.getInstance().getAppData().getLastReviews()) {
				vecLastReviews.add(new File(rev).getName());
			}
		} catch (DataException exc) {
			JOptionPane.showMessageDialog(null, GUITools.getMessagePane(exc
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);
		}

		return vecLastReviews;
	}


}
