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
package neos.resi.app.model.appdata;

/**
 * This enumeration class defines the keys of the application
 * settings.
 */
public enum AppSettingKey {
	DATABASE_VERSION,
	
	/* Application setting keys */
	APP_LAST_REVIEW_PATH,
	APP_LAST_MODE,
	APP_DO_AUTO_SAVE,
	APP_AUTO_SAVE_INTERVAL,
	APP_FONT_SIZE,
	APP_CHECK_VERSION,
	APP_LANGUAGE,
	APP_SHOW_HINTS,
	APP_HIGHLIGHT_FIELDS,
	APP_SHOW_PROTOCOL_WARNING,
	APP_PROTOCOL_WARNING_TIME,
	
	/* PDF protocol keys */
	PDF_PROTOCOL_FOOT_TEXT,
	PDF_PROTOCOL_LOGO,
	PDF_PROTOCOL_SHOW_SIGN_FIELDS,
	
	/* PDF invitation keys */
	PDF_INVITATION_TEXT,
	PDF_INVITATION_FOOT_TEXT,
	PDF_INVITATION_LOGO;
}
