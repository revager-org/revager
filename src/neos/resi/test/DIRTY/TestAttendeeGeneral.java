package neos.resi.test.DIRTY;

import javax.swing.JComboBox;

import neos.resi.app.model.schema.Role;
import neos.resi.gui.dialogs.AttendeeDialog;

public class TestAttendeeGeneral {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JComboBox roleBox = new JComboBox();
		roleBox.addItem("Moderator");
		roleBox.setSelectedItem("Moderator");
		System.out.print(roleBox.getSelectedItem().toString());
		Role roll=Role.fromValue("moderator");
	}

}
