package neos.resi.test.DIRTY;

import java.util.ArrayList;
import java.util.List;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.app.model.schema.Review;
import neos.resi.app.model.schema.Severities;

public class TestSeveritiesManagement {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		List<String> test =new ArrayList<String>();

		Data.getInstance().getResiData().setReview(new Review());
		Data.getInstance().getResiData().getReview().setSeverities(new Severities());
		for(int index=0;index<10;index++){
			
			Application.getInstance().getSeverityMgmt().addSeverity(String.valueOf(index));
		}
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		Application.getInstance().getSeverityMgmt().removeSeverity(String.valueOf(3));
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		Application.getInstance().getSeverityMgmt().editSeverity(String.valueOf(5),String.valueOf(10));
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		Application.getInstance().getSeverityMgmt().pushUpSeverity(String.valueOf(1));
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		Application.getInstance().getSeverityMgmt().pushDownSeverity(String.valueOf(7));
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		Application.getInstance().getSeverityMgmt().pushDownSeverity(String.valueOf(7));
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		Application.getInstance().getSeverityMgmt().pushTopSeverity(String.valueOf(8));
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		Application.getInstance().getSeverityMgmt().pushBottomSeverity(String.valueOf(0));
		System.out.println(Application.getInstance().getSeverityMgmt().getSeverities());
		System.out.println(Application.getInstance().getSeverityMgmt().isBottomSeverity(String.valueOf(0)));
		System.out.println(Application.getInstance().getSeverityMgmt().isBottomSeverity(String.valueOf(7)));
		System.out.println(Application.getInstance().getSeverityMgmt().isTopSeverity(String.valueOf(1)));
		System.out.println(Application.getInstance().getSeverityMgmt().isTopSeverity(String.valueOf(8)));
		
		
	}

}
