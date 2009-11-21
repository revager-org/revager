/**
 * 
 */
package neos.resi.test.DIRTY;

import java.util.HashMap;
import java.util.Map;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.export.ExportException;
import neos.resi.export.FindingsCSVExporter;

/**
 * @author jojo
 * 
 */
public class TestCSVExport {

	/**
	 * @param args
	 * @throws DataException 
	 * @throws ExportException 
	 */
	public static void main(String[] args) throws DataException, ExportException {
		String testReview = "/home/jojo/TestReview (guter Test).rev";

		try {
			Data.getInstance().getAppData().initialize();

			Application.getInstance().getApplicationCtl()
					.loadReview(testReview);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> sevMap = new HashMap<String, String>();
		sevMap.put("Hauptfehler", "major");

		FindingsCSVExporter exp = new FindingsCSVExporter(Data.getInstance()
				.getAppData().getCSVProfile("Trac"), sevMap, Data.getInstance()
				.getResiData().getReview().getMeetings().get(0).getProtocol()
				.getFindings(), "Johannes Wettinger");
		
		exp.writeToFile("/home/jojo/FINDINGS_TEST.csv");
	}

}
