package neos.resi.test.DIRTY;

import neos.resi.app.model.Data;

public class TestSearch {
	
	public static String getLocalString(String searchString,String chapterContent){
		
		String localString="";
		
			if(chapterContent.indexOf("<")>0){
				String local=chapterContent.substring(0, chapterContent.indexOf("<"));

				String localLow=local.toLowerCase();
				String searchLow=searchString.toLowerCase();
				if(localLow.indexOf(searchLow)!=-1){
					String replacement="";
					replacement=local.substring(0,localLow.indexOf(searchLow));
					replacement=replacement.concat(Data.getInstance().getLocaleStr("help.firstReplacement"));
					replacement=replacement.concat(local.substring(localLow.indexOf(searchLow),localLow.indexOf(searchLow)+searchString.length()));
					replacement=replacement.concat(Data.getInstance().getLocaleStr("help.lastReplacement"));
					localString=localString.concat(replacement);
					chapterContent=chapterContent.substring(localLow.indexOf(searchLow)+searchString.length());
					}else {
						localString=localString.concat(local);
						chapterContent=chapterContent.substring(chapterContent.indexOf("<"));
					}
			}
			if(chapterContent.indexOf("<")==0){
				localString=localString.concat(chapterContent.substring(0, chapterContent.indexOf(">")+1));
				chapterContent=chapterContent.substring(chapterContent.indexOf(">")+1);
			}
			if(chapterContent.indexOf("<")!=-1){
				localString=localString.concat(getLocalString(searchString,chapterContent));
				}
			
			
		return localString;
	}
	
}

