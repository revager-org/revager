package org.revager.gamecontroller;

import static org.revager.app.model.Data.translate;

public enum Classification {
	// @formatter:off
	// Order has impact on sorting.
	CRITICAL_ERROR("Critical error"),
	MAIN_ERROR("Main error"),
	MINOR_ERROR("Minor error"),
	RATHER_NO_ERROR("Rather no error"),
	GOOD("Good");
	// @formatter:on

	private String i18nKey;

	private Classification(String i18nKey) {
		this.i18nKey = i18nKey;
	}

	@Override
	public String toString() {
		return translate(i18nKey);
	}

}
