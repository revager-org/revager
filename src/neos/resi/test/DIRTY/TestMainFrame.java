/**
 * 
 */
package neos.resi.test.DIRTY;

import java.awt.FileDialog;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import neos.resi.gui.UI;
import neos.resi.gui.helpers.FileChooser;
import neos.resi.gui.helpers.HintItem;

/**
 * @author jojo
 *
 */
public class TestMainFrame {

	public static void main(String[] args) {
		//UI.getInstance().setMode(UI.Mode.MODERATOR);

		List<HintItem> test = new ArrayList<HintItem>();

		test.add(new HintItem("Bitte Name des Reviews eintragen! Bitte Name des Reviews eintragen! Bitte Name des Reviews eintragen! Bitte Name des Reviews eintragen! Bitte Name des Reviews eintragen! Bitte Name des Reviews eintragen! Bitte Name des Reviews eintragen! Bitte Name des Reviews eintragen! ",
				HintItem.ERROR, "example", "test"));
		test.add(new HintItem("Ein weiterer Eintrag", HintItem.WARNING, "review_management"));
		test.add(new HintItem("Ein weiterer Eintrag", HintItem.WARNING));
		
		UI.getInstance().getMainFrame().setHints(test);

		UI.getInstance().getMainFrame().toggleHints();

		UI.getInstance().getMainFrame().setStatusMessage("Review-Datei wird geladen...", true);
		
		UI.getInstance().getMainFrame().setVisible(true);
		
		UI.getInstance().getMainFrame().setHints(null);
	}
	
}
