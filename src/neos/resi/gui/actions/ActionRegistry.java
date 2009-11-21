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
package neos.resi.gui.actions;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

import neos.resi.gui.actions.attendee.AddAttToProtAction;
import neos.resi.gui.actions.attendee.AddAttendeeAction;
import neos.resi.gui.actions.attendee.AddResiAttToProtAction;
import neos.resi.gui.actions.attendee.ConfirmAttendeeAction;
import neos.resi.gui.actions.attendee.EditAttFromProtAction;
import neos.resi.gui.actions.attendee.EditAttendeeAction;
import neos.resi.gui.actions.attendee.RemAttFromProtAction;
import neos.resi.gui.actions.attendee.RemoveAttendeeAction;
import neos.resi.gui.actions.attendee.SelectAttOutOfDirAction;
import neos.resi.gui.actions.help.OpenHelpAction;
import neos.resi.gui.actions.help.ResetHelpAction;
import neos.resi.gui.actions.help.SearchHelpAction;
import neos.resi.gui.actions.meeting.AddMeetingAction;
import neos.resi.gui.actions.meeting.CommentMeetingAction;
import neos.resi.gui.actions.meeting.ConfirmMeetingAction;
import neos.resi.gui.actions.meeting.EditMeetingAction;
import neos.resi.gui.actions.meeting.RemoveMeetingAction;
import neos.resi.gui.actions.severities.AddSeverityAction;
import neos.resi.gui.actions.severities.EditSeverityAction;
import neos.resi.gui.actions.severities.PushSeverityBottomAction;
import neos.resi.gui.actions.severities.PushSeverityDownAction;
import neos.resi.gui.actions.severities.PushSeverityTopAction;
import neos.resi.gui.actions.severities.PushSeverityUpAction;
import neos.resi.gui.actions.severities.RemoveSeverityAction;

/**
 * The action registry contains all actions of this application, so they can
 * accessed easily.
 */
public class ActionRegistry {

	private static ActionRegistry instance = null;

	private Map<String, Action> actions;

	/**
	 * Gets the single instance of ActionRegistry.
	 * 
	 * @return single instance of ActionRegistry
	 */
	public static ActionRegistry getInstance() {
		if (instance == null) {
			instance = new ActionRegistry();
		}

		return instance;
	};

	/**
	 * Register.
	 * 
	 * @param action
	 *            the action
	 */
	public void register(Action action) {
		getActions().put(action.getClass().getName(), action);
	}

	/**
	 * Gets the actions.
	 * 
	 * @return the actions
	 */
	private Map<String, Action> getActions() {
		// The map for storing the actions is initialized on demand.
		if (actions == null) {
			actions = new HashMap<String, Action>();
		}

		return actions;
	}

	/**
	 * Gets the.
	 * 
	 * @param className
	 *            the class name
	 * 
	 * @return the action
	 */
	public Action get(String className) {
		return getActions().get(className);
	}

	/**
	 * Instantiates a new action registry.
	 */
	private ActionRegistry() {
		super();

		/*
		 * Register all actions
		 */
		register(new ResetHelpAction());
		register(new SearchHelpAction());
		register(new OpenHelpAction());
		register(new ExitAction());
		register(new LoadReviewAction());

		register(new SaveReviewAsAction());
		register(new SaveReviewAction());
		register(new OpenScribeModeAction());
		register(new OpenModeratorModeAction());
		register(new InitializeMainFrameAction());

		register(new ManageSeveritiesAction());
		register(new EditMeetingAction());
		register(new EditAttendeeAction());
		register(new AddMeetingAction());
		register(new AddAttendeeAction());

		register(new DatePickerAction());
		register(new ConfirmAttendeeAction());
		register(new RemoveAttendeeAction());
		register(new ConfirmMeetingAction());
		register(new RemoveMeetingAction());

		register(new NewReviewAction());
		register(new AddSeverityAction());
		register(new RemoveSeverityAction());
		register(new EditSeverityAction());
		register(new PushSeverityUpAction());

		register(new PushSeverityDownAction());
		register(new PushSeverityTopAction());
		register(new PushSeverityBottomAction());
		register(new OpenInvitationsDialogAction());
		register(new OpenAspectsManagerAction());

		register(new CommentMeetingAction());
		register(new SelectModeAction());
		register(new SelectAttOutOfDirAction());
		register(new OpenProtocolFrameAction());
		register(new AddResiAttToProtAction());

		register(new RemAttFromProtAction());
		register(new AddAttToProtAction());
		register(new EditAttFromProtAction());
		register(new OpenExpPDFDialogAction());
		register(new OpenExpCSVDialogAction());
	}
}
