package neos.resi.test.DIRTY;

import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;

public class TestHelpData {

	public static void main(String[] args) {
		
		try {
			for (String chapterId: Data.getInstance().getHelpData().getChapters()) {
				System.out.println(Data.getInstance().getHelpData().getChapterTitle(chapterId));
				System.out.println("=========================================");
				System.out.println(Data.getInstance().getHelpData().getChapterContent(chapterId));
				System.out.println("...\n...\n...\n");
				System.out.println(System.getProperty("file.encoding"));
			}
		} catch (DataException e) {
			System.err.println("FEHLER: " + e.getMessage());
		}
		
	}
	
}
