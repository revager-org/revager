package neos.resi.test.DIRTY;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import neos.resi.app.Application;
import neos.resi.app.ResiFileFilter;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.gui.MainFrame;
import neos.resi.gui.UI;
import neos.resi.gui.helpers.FileChooser;
import neos.resi.tools.FileTools;
import neos.resi.tools.GUITools;
import neos.resi.tools.PDFTools;

public class GeneralTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {

		

		if (args.length >= 2) {
			if (args[0].equals("-data".trim())) {
				Data.getInstance().getAppData().setCustomAppDataDirectory(
						args[1].trim());
				System.out.println(args[1].trim());
			}
		}
		
		/*
		try {
			List<File> list = FileTools.getListOfFiles(new File("/home/jojo"));
		
			FileTools.writeToZip(list, new File("/home/jojo/TEST.zip"));
		
			new File("/home/jojo/TTT---").mkdir();
		
			FileTools.extractZipFile(new File("/home/jojo/TEST.zip"), new File("/home/jojo/TTT---"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		try {
			Data.getInstance().getAppData().initialize();
			
			Application.getInstance().getApplicationCtl().loadReview("/home/jojo/TestReviewNEW.zip");
			
			Application.getInstance().getFindingMgmt().addExtReference(new File("/home/jojo/dpkg.list"), Application.getInstance().getMeetingMgmt().getMeetings().get(0).getProtocol().getFindings().get(0));
			Application.getInstance().getFindingMgmt().addExtReference(new File("/home/jojo/dpkg.list"), Application.getInstance().getMeetingMgmt().getMeetings().get(0).getProtocol().getFindings().get(0));
			Application.getInstance().getFindingMgmt().addExtReference(new File("/home/jojo/review.xml"), Application.getInstance().getMeetingMgmt().getMeetings().get(0).getProtocol().getFindings().get(0));
			
			Application.getInstance().getApplicationCtl().storeReview("/home/jojo/TestReviewNEW.zip");
			
			Application.getInstance().getApplicationCtl().clearReview();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		try {
			URI abs = new URI("file", null, "/home/jojo/", null);
			URI file = new URI("file:///home/jojo/rev%20Ätest.xml");
			URI rel = new URI(null, null,"rev ätest.xml", null);
			
			
			
			System.out.println(abs.resolve(rel));
			new File(abs.resolve(rel)).createNewFile();
			
			System.out.println(file);
			System.out.println(file.toURL());
			new File(file).createNewFile();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		try {
			Data.getInstance().getAppData().initialize();
			
			
			
			JFileChooser chooser = new JFileChooser();
			chooser.showOpenDialog(UI.getInstance().getMainFrame());
			System.out.println(chooser.getSelectedFile().getAbsolutePath());
		
			Application.getInstance().getApplicationCtl().loadReview(chooser.getSelectedFile().getAbsolutePath());
			Application.getInstance().getApplicationCtl().storeReview(chooser.getSelectedFile().getAbsolutePath());

			System.out.println(Data.getInstance().getResiData().getReview().getName() + "\n");
			
			
			
			FileDialog dialog = new FileDialog(UI.getInstance().getMainFrame());
			dialog.setVisible(true);
			System.out.println(dialog.getDirectory() + dialog.getFile());
			
			Application.getInstance().getApplicationCtl().clearReview();
			
			Application.getInstance().getApplicationCtl().loadReview(dialog.getDirectory() + dialog.getFile());
			Application.getInstance().getApplicationCtl().storeReview(dialog.getDirectory() + dialog.getFile());
			
			System.out.println(Data.getInstance().getResiData().getReview().getName() + "\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		MainFrame mf = UI.getInstance().getMainFrame();
		FileChooser fc = UI.getInstance().getFileChooser();
		
		int dRet;
		
		dRet = fc.showDialog(mf, FileChooser.MODE_OPEN_FILE, ResiFileFilter.TYPE_REVIEW);
		System.out.println("BUTTON: " + dRet);
		System.out.println("VERZ: " + fc.getDir());
		System.out.println("DATEI: " + fc.getFile());
		
		dRet = fc.showDialog(mf, FileChooser.MODE_OPEN_FILE, ResiFileFilter.TYPE_REVIEW);
		System.out.println("BUTTON: " + dRet);
		System.out.println("VERZ: " + fc.getDir());
		System.out.println("DATEI: " + fc.getFile());
		
		fc.clearFile();
		dRet = fc.showDialog(mf, FileChooser.MODE_SAVE_FILE, ResiFileFilter.TYPE_REVIEW);
		System.out.println("BUTTON: " + dRet);
		System.out.println("VERZ: " + fc.getDir());
		System.out.println("DATEI: " + fc.getFile());
		
		dRet = fc.showDialog(mf, FileChooser.MODE_SELECT_DIRECTORY, ResiFileFilter.TYPE_REVIEW);
		System.out.println("BUTTON: " + dRet);
		System.out.println("VERZ: " + fc.getDir());
		System.out.println("DATEI: " + fc.getFile());
		
		dRet = fc.showDialog(mf, FileChooser.MODE_OPEN_FILE, ResiFileFilter.TYPE_CSV);
		System.out.println("BUTTON: " + dRet);
		System.out.println("VERZ: " + fc.getDir());
		System.out.println("DATEI: " + fc.getFile());
		
		dRet = fc.showDialog(mf, FileChooser.MODE_SAVE_FILE, ResiFileFilter.TYPE_ASPECTS);
		System.out.println("BUTTON: " + dRet);
		System.out.println("VERZ: " + fc.getDir());
		System.out.println("DATEI: " + fc.getFile());
		*/
		
		/*
		System.out.println(Application.getInstance().getReviewMgmt().getExtRefFileName("reviewfile:///T%C3%A4st%20Datei.txt"));

		System.out.println(Application.getInstance().getReviewMgmt().getExtRefURI("Täst Datei.txt"));
		*/
		
		
		int aspects = 85;
		
		int attendees = 6;
		
		int aspPerAtt = (int) ((aspects / attendees) * 1.6);
		System.out.println(aspPerAtt);
		
	}
}
