package neos.resi.test.DIRTY;

import neos.resi.gui.UI;
import neos.resi.tools.AppTools;
import neos.resi.tools.GraphicalEditor;

public class TestGraphicalEditor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GraphicalEditor test=new GraphicalEditor(UI.getInstance().getMainFrame(), AppTools.getImageFromClipboard());
	}

}
