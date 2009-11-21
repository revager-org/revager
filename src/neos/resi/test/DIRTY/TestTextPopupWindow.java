/**
 * 
 */
package neos.resi.test.DIRTY;

import neos.resi.gui.TextPopupWindow;
import neos.resi.gui.UI;

/**
 * @author jojo
 * 
 */
public class TestTextPopupWindow {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TextPopupWindow popup = new TextPopupWindow(UI.getInstance()
				.getMainFrame(), "Bitte Name des Reviews eingeben:",
				"Review der Spezi ABC", true);

		popup.setVisible(true);

		System.out.println("EINGABE: " + popup.getInput());
		System.out.println("BUTTON: " + popup.getButtonClicked().toString());

	}

}
