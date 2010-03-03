package org.revager.gui.dialogs.assistant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.gui.UI;
import org.revager.gui.helpers.HLink;
import org.revager.gui.helpers.LinkGroup;
import org.revager.tools.GUITools;

/**
 * The Class LanguagePopupWindow.
 */
@SuppressWarnings("serial")
public class LanguagePopupWindow extends JDialog {

	
	/**
	 * The Enum ButtonClicked.
	 */
	public static enum ButtonClicked {
		OK, ABORT;
	};

	private ApplicationData appData = Data.getInstance().getAppData();
	
	private ButtonClicked buttonClicked = null;
	
	private GridBagLayout gbl= new GridBagLayout();
	private JPanel inputPnl= new JPanel(gbl);
	private JPanel panelBase = GUITools.newPopupBasePanel();
	
	private String germanStrng=Data.getInstance().getLocaleStr("popup.language.german");
	private String englishStrng=Data.getInstance().getLocaleStr("popup.language.english");
	
	private ImageIcon germanIcon=Data.getInstance().getIcon("german_31x20_0.png");
	private ImageIcon germanRolloverIcon=Data.getInstance().getIcon("german_31x20.png");
	private ImageIcon englishIcon=Data.getInstance().getIcon("english_31x20_0.png");
	private ImageIcon englishRolloverIcon=Data.getInstance().getIcon("english_31x20.png");
	
	private LinkGroup languageGrp=new LinkGroup();
	private HLink germanHLnk = new HLink(germanStrng,germanIcon,germanRolloverIcon,languageGrp);
	private HLink englishHLnk = new HLink(englishStrng,englishIcon,englishRolloverIcon,languageGrp);
	private String currentLang;

	
	public String getSelectedLanguage(){
		if(languageGrp.getSelectedLinkText().equals(Data.getInstance().getLocaleStr("popup.language.german")))
			return "de";
		else if (languageGrp.getSelectedLinkText().equals(Data.getInstance().getLocaleStr("popup.language.english")))
			return "en";
		else
			return null;
	}
	
	public LanguagePopupWindow(Window parent, String titleText) {
		super(parent);

		setLayout(new BorderLayout());

		setUndecorated(true);

		setModal(true);
		
		JTextArea textTitle = GUITools.newPopupTitleArea(titleText);

		panelBase.add(textTitle, BorderLayout.NORTH);

		try {
			currentLang= appData.getSetting(AppSettingKey.APP_LANGUAGE);
		} catch (DataException e) {
			currentLang = null;
		}
		languageGrp.addLink(germanHLnk);
		languageGrp.addLink(englishHLnk);
		
		if(currentLang.equals("de"))
			languageGrp.selectLink(germanHLnk);
		else if(currentLang.equals("en"))
			languageGrp.selectLink(englishHLnk);
		
		GUITools.addComponent(inputPnl, gbl, germanHLnk, 0, 0, 1, 1, 1.0, 1.0, 10, 10, 0, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
		GUITools.addComponent(inputPnl, gbl, englishHLnk, 0, 1, 1, 1, 1.0, 1.0, 10, 10, 10, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
		
		panelBase.add(inputPnl, BorderLayout.CENTER);

		Dimension popupSize;

		popupSize = new Dimension(110, 160);

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(Data.getInstance().getLocaleStr("abort"));
		buttonAbort.addActionListener(new LanguagePopupWindowAction(
				this, ButtonClicked.ABORT));

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm
				.setToolTipText(Data.getInstance().getLocaleStr("confirm"));
		buttonConfirm.addActionListener(new LanguagePopupWindowAction(
				this, ButtonClicked.OK));

		JPanel panelButtons = new JPanel(new BorderLayout());
		panelButtons.setBackground(UI.POPUP_BACKGROUND);
		panelButtons.setBorder(BorderFactory.createLineBorder(panelButtons
				.getBackground(), 3));
		panelButtons.add(buttonAbort, BorderLayout.WEST);
		panelButtons.add(buttonConfirm, BorderLayout.EAST);

		/*
		 * Base panel
		 */
		panelBase.add(panelButtons, BorderLayout.SOUTH);

		add(panelBase, BorderLayout.CENTER);

		/*
		 * Set size and location
		 */
		setMinimumSize(popupSize);
		setSize(popupSize);

		pack();

		setAlwaysOnTop(true);
		toFront();

		GUITools.setLocationToCursorPos(this);
	}

	
	/**
	 * Gets the button clicked.
	 * 
	 * @return the buttonClicked
	 */
	public ButtonClicked getButtonClicked() {
		return buttonClicked;
	}

	/**
	 * Sets the button clicked.
	 * 
	 * @param buttonClicked
	 *            the buttonClicked to set
	 */
	public void setButtonClicked(ButtonClicked buttonClicked) {
		this.buttonClicked = buttonClicked;
	}

	
}


