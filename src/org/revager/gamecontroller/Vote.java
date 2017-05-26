package org.revager.gamecontroller;

import static org.revager.app.model.Data.translate;

public enum Vote {
	// @formatter:off
	// Order has impact on sorting.
	CRITICAL_ERROR("Critical error"),
	MAIN_ERROR("Main error"),
	MINOR_ERROR("Minor error"),
	POSSIBLE_NO_ERROR("(Possible) no error"),
	GOOD("Good");
	// @formatter:on

	private String i18nKey;

	private Vote(String i18nKey) {
		this.i18nKey = i18nKey;
	}

	@Override
	public String toString() {
		return translate(i18nKey);
	}

}
