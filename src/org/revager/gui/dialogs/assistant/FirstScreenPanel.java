/* 
 * Copyright 2009 Davide Casciato, Sandra Reich, Johannes Wettinger
 * 
 * This file is part of Resi.
 *
 * Resi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Resi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resi. If not, see <http://www.gnu.org/licenses/>.
 */
package org.revager.gui.dialogs.assistant;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.revager.app.model.Data;
import org.revager.gui.AbstractDialog;
import org.revager.gui.AbstractDialogPanel;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.InitializeNewReviewAction;
import org.revager.gui.actions.OpenAspectsManagerAction;
import org.revager.gui.actions.assistant.GoToAddAttPnlAction;
import org.revager.gui.actions.assistant.GoToOpenRevPnlAction;
import org.revager.gui.actions.assistant.SelectLanguageAction;
import org.revager.gui.helpers.HLink;
import org.revager.gui.helpers.VLink;
import org.revager.tools.GUITools;

/**
 * The class FirstScreenPanel.
 * 
 * @author D.Casciato
 *
 */
@SuppressWarnings("serial")
public class FirstScreenPanel extends AbstractDialogPanel {

	private JSeparator dottedSprtr = new JSeparator(SwingConstants.HORIZONTAL);

	private GridBagLayout gbl1 = new GridBagLayout();


	/**
	 * Action to select a language.
	 */
	private ActionListener selectLanguageAction = ActionRegistry.getInstance()
			.get(SelectLanguageAction.class.getName());
	/**
	 * Action to open up the Aspects-Manager.
	 */
	private ActionListener openAspMngrAction = ActionRegistry.getInstance()
			.get(OpenAspectsManagerAction.class.getName());

	/*
	 * Strings
	 */
	private String quickRevTooltipStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.quickRev.rollover");
	private String newRevTooltipStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.newRev.rollover");
	private String openRevTooltipStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.openRev.rollover");
	private String selectLanguageTooltipStrng = Data.getInstance()
			.getLocaleStr("assistantDialog.selectLanguage.rollover");
	private String openAspectsMngrTooltipStrng = Data.getInstance()
			.getLocaleStr("assistantDialog.openAspectsMngr.rollover");

	private String openRevStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.openReview");

	private String languageStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.selectLanguage");
	private String aspectsManagerStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.aspectsManager");
	private String newReviewStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.newReview");
	private String quickstartStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.quickstart");

	/*
	 *ImageIcons 
	 */
	private ImageIcon moderatorIcon = Data.getInstance().getIcon(
			"moderator_128x128_0.png");
	private ImageIcon moderatorRolloverIcon = Data.getInstance().getIcon(
			"moderator_128x128.png");
	private ImageIcon openRevIcon = Data.getInstance().getIcon(
			"scribe_128x128_0.png");
	private ImageIcon openRevRolloverIcon = Data.getInstance().getIcon(
			"scribe_128x128.png");
	private ImageIcon quickstartIcon = Data.getInstance().getIcon(
			"instantReview_128x128_0.png");
	private ImageIcon quickstartRolloverIcon = Data.getInstance().getIcon(
			"instantReview_128x128.png");
	private ImageIcon languageIcon = Data.getInstance().getIcon(
			"language_31x20_0.png");
	private ImageIcon languageRolloverIcon = Data.getInstance().getIcon(
			"language_31x20.png");
	private ImageIcon aspectsManagerIcon = Data.getInstance().getIcon(
			"aspectsManager_25x25_0.png");
	private ImageIcon aspectsManagerRolloverIcon = Data.getInstance().getIcon(
			"aspectsManager_25x25.png");

	/*
	 * Links
	 */
	private VLink newReviewLnk = new VLink(newReviewStrng, moderatorIcon,
			moderatorRolloverIcon);
	private VLink quickstartLnk = new VLink(quickstartStrng, quickstartIcon,
			quickstartRolloverIcon);
	private VLink openReviewLnk = new VLink(openRevStrng, openRevIcon,
			openRevRolloverIcon);
	private HLink selectLanguageLnk = new HLink(languageStrng, languageIcon,
			languageRolloverIcon, null);
	private HLink openAspManagerLnk = new HLink(aspectsManagerStrng,
			aspectsManagerIcon, aspectsManagerRolloverIcon, null);

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public FirstScreenPanel(AbstractDialog parent) {
		super(parent);
		createFirstScreenPnl();
	} 
	
	/**
	 * Method which creates and locates the components of the panel.
	 */
	private void createFirstScreenPnl() {

		quickstartLnk.setFather(this);
		newReviewLnk.setFather(this);
		openReviewLnk.setFather(this);
		selectLanguageLnk.setFather(this);
		openAspManagerLnk.setFather(this);

		newReviewLnk.addActionListener(ActionRegistry.getInstance().get(
				InitializeNewReviewAction.class.getName()));

		openReviewLnk.addActionListener(ActionRegistry.getInstance().get(GoToOpenRevPnlAction.class.getName()));
		quickstartLnk.addActionListener(ActionRegistry.getInstance().get(GoToAddAttPnlAction.class.getName()));
		selectLanguageLnk.addActionListener(selectLanguageAction);
		openAspManagerLnk.addActionListener(openAspMngrAction);

		selectLanguageLnk.setUnderlined(true);
		openAspManagerLnk.setUnderlined(true);

		quickstartLnk.addRolloverText(quickRevTooltipStrng);

		openReviewLnk.addRolloverText(openRevTooltipStrng);
		newReviewLnk.addRolloverText(newRevTooltipStrng);
		selectLanguageLnk.addRolloverText(selectLanguageTooltipStrng);
		openAspManagerLnk.addRolloverText(openAspectsMngrTooltipStrng);

		this.setLayout(gbl1);
		GUITools.addComponent(this, gbl1, newReviewLnk, 0, 0, 1, 1,
				1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl1, quickstartLnk, 1, 0, 1,
				1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl1, openReviewLnk, 2, 0, 1,
				1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl1, dottedSprtr, 0, 1, 3, 1,
				1.0, 0.0, 20, 20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER);
		GUITools.addComponent(this, gbl1, selectLanguageLnk, 0, 2,
				1, 1, 1.0, 0.0, 20, 20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER);
		GUITools.addComponent(this, gbl1, openAspManagerLnk, 2, 2,
				1, 1, 1.0, 0.0, 20, 0, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER);

	}

}
