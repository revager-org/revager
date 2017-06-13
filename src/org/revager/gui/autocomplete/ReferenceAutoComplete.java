package org.revager.gui.autocomplete;

import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;

public class ReferenceAutoComplete extends Java2sAutoTextField {

	private static final long serialVersionUID = -2520614166432107742L;

	public ReferenceAutoComplete() {
		super(Finding.class);
	}

	protected void loadData() {
		Protocol protocol = UI.getInstance().getProtocolFrame().getMeeting().getProtocol();
		for (Finding finding : protocol.getFindings()) {
			for (String reference : finding.getReferences()) {
				addSuggestion(reference);
			}
		}
	}
}
