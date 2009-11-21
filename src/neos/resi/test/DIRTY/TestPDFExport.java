/**
 * 
 */
package neos.resi.test.DIRTY;

import java.awt.Desktop;
import java.io.File;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.app.model.appdata.AppSettingKey;
import neos.resi.export.InvitationDirExporter;
import neos.resi.export.InvitationPDFExporter;
import neos.resi.export.InvitationZIPExporter;
import neos.resi.export.MeetingProtocolPDFExporter;
import neos.resi.export.ProtocolPDFExporter;
import neos.resi.export.ReviewProtocolPDFExporter;
import neos.resi.io.ResiIO;
import neos.resi.io.ResiIOFactory;

/**
 * @author jojo
 * 
 */
public class TestPDFExport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ResiIO io = ResiIOFactory.getInstance().getIOProvider();
		String testReview = "/home/jojo/TestReview (guter Test).rev";

		try {
			Data.getInstance().getAppData().initialize();

			Application.getInstance().getApplicationCtl()
					.loadReview(testReview);
			
			Data.getInstance().getAppData().setSetting(
					AppSettingKey.PDF_PROTOCOL_FOOT_TEXT,
					"(c) 2009 by Universität Stuttgart, Abteilung SE");
			Data.getInstance().getAppData().setSetting(
					AppSettingKey.PDF_PROTOCOL_LOGO,
					"/home/jojo/se_logo_mit_uni.gif.jpeg");
			
			Data.getInstance().getAppData().setSetting(
					AppSettingKey.PDF_INVITATION_FOOT_TEXT,
					"(c) 2009 by Universität Stuttgart, Abteilung SE");
			Data.getInstance().getAppData().setSetting(
					AppSettingKey.PDF_INVITATION_LOGO,
					"/home/jojo/se_logo_mit_uni.gif.jpeg");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			/*
			ProtocolPDFExporter exporter = new ReviewProtocolPDFExporter(
					"/home/jojo/REVIEW_PROT.pdf", true, true, true);
			exporter.writeToFile();

			exporter = new MeetingProtocolPDFExporter(
					"/home/jojo/MEETING_PROT.pdf", Data.getInstance().getResiData()
							.getReview().getMeetings().get(0), true, true, true);
			exporter.writeToFile();

			Desktop.getDesktop().open(new File("/home/jojo/REVIEW_PROT.pdf"));
			Desktop.getDesktop().open(new File("/home/jojo/MEETING_PROT.pdf"));
			*/
			
			/*
			InvitationPDFExporter invExp = new InvitationPDFExporter("/home/jojo/INVITATION.pdf", Data.getInstance().getResiData()
					.getReview().getMeetings().get(1), Data.getInstance().getResiData()
					.getReview().getAttendees().get(2), true);
			invExp.writeToFile();

			Desktop.getDesktop().open(new File("/home/jojo/INVITATION.pdf"));
			*/
			
			/*
			InvitationZIPExporter invExp = new InvitationZIPExporter("/home/jojo/INVITATION.zip", Data.getInstance().getResiData()
					.getReview().getMeetings().get(1), Data.getInstance().getResiData()
					.getReview().getAttendees().get(2), true);
			invExp.writeToFile();
			*/
			
			InvitationDirExporter invExp = new InvitationDirExporter("/home/jojo/INVITATION_DIR", Data.getInstance().getResiData()
					.getReview().getMeetings().get(1), Data.getInstance().getResiData()
					.getReview().getAttendees().get(2), true);
			invExp.writeDir();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
