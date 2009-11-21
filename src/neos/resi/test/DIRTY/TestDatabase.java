/**
 * 
 */
package neos.resi.test.DIRTY;

import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.appdata.AppAttendee;
import neos.resi.app.model.appdata.AppCSVColumnName;
import neos.resi.app.model.appdata.AppCSVProfile;

/**
 * @author jojo
 *
 */
public class TestDatabase {

	public static void main(String[] args) throws DataException {
		Data.getInstance().getAppData().openConnection();
		
		/*
		AppAttendee att = Data.getInstance().getAppData().newAttendee("Hans Müller", "Mail-Adresse");
		
		System.out.println(att.getName());
		System.out.println(att.getContact());
		
		att.setName("Hansi");
		
		System.out.println(att.getName());
		System.out.println(att.getContact());
		*/
		
		/*
		 * Test CSV profile
		 */
		
		AppCSVProfile prof = Data.getInstance().getAppData().newCSVProfile("TEST");
		
		System.out.println(prof.getColumnMapping(AppCSVColumnName.DESCRIPTION));
		
		prof.setColumnMapping(AppCSVColumnName.DESCRIPTION, "beschreibung");
		
		System.out.println(prof.getColumnMapping(AppCSVColumnName.DESCRIPTION));
		
		Data.getInstance().getAppData().removeCSVProfile("TEST");
		
		prof = Data.getInstance().getAppData().newCSVProfile("TEST");
		
		System.out.println(prof.getColumnMapping(AppCSVColumnName.DESCRIPTION));
		
	}
	
}
