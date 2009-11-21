package neos.resi.test.DIRTY;

import javax.swing.JDialog;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.app.model.schema.Finding;
import neos.resi.app.model.schema.Protocol;
import neos.resi.gui.protocol.FindingItem;

public class TestFindingsItem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Finding testFinding = new Finding();
		Protocol prot = new Protocol();
		FindingItem testItem = new FindingItem(testFinding,prot);
		
		JDialog testDialog = new JDialog();
		testDialog.add(testItem);
		testDialog.setSize(400,500);
		testDialog.setVisible(true);
	}

}
