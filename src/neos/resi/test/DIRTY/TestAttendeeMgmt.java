package neos.resi.test.DIRTY;

import java.util.ArrayList;
import java.util.List;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.app.model.schema.Attendee;
import neos.resi.app.model.schema.Review;
import neos.resi.app.model.schema.Severities;

public class TestAttendeeMgmt {
	public static void main(String[] args) {
		List<Attendee> test =new ArrayList<Attendee>();

		Data.getInstance().getResiData().setReview(new Review());
		Data.getInstance().getResiData().getReview().getAttendees();
		for(int index=0;index<10;index++){
			Attendee localAttendee=new Attendee();
			localAttendee.setName(String.valueOf(index));
			Application.getInstance().getAttendeeMgmt().addAttendee(localAttendee);
		}
		
		System.out.println(Application.getInstance().getAttendeeMgmt().getAttendees());
		
	/*	Application.getInstance().getAttendeeMgmt().removeAttendee(String.valueOf(3));
		System.out.println(Application.getInstance().getAttendeeMgmt().getSeverities());
		Application.getInstance().getAttendeeMgmt().editAttendee(String.valueOf(5),String.valueOf(10));
		System.out.println(Application.getInstance().getAttendeeMgmt().getSeverities());
		System.out.println(Application.getInstance().getAttendeeMgmt().getNumberOfSevs());
		Application.getInstance().getAttendeeMgmt().pushUpAttendee(String.valueOf(1));
		System.out.println(Application.getInstance().getAttendeeMgmt().getSeverities());
		Application.getInstance().getAttendeeMgmt().pushDownAttendee(String.valueOf(7));
		System.out.println(Application.getInstance().getAttendeeMgmt().getSeverities());
		Application.getInstance().getAttendeeMgmt().pushDownAttendee(String.valueOf(7));
		System.out.println(Application.getInstance().getAttendeeMgmt().getSeverities());
		Application.getInstance().getAttendeeMgmt().pushTopAttendee(String.valueOf(8));
		System.out.println(Application.getInstance().getAttendeeMgmt().getSeverities());
		Application.getInstance().getAttendeeMgmt().pushBottomAttendee(String.valueOf(0));
		System.out.println(Application.getInstance().getAttendeeMgmt().getSeverities());
		System.out.println(Application.getInstance().getAttendeeMgmt().isBottomAttendee(String.valueOf(0)));
		System.out.println(Application.getInstance().getAttendeeMgmt().isBottomAttendee(String.valueOf(7)));
		System.out.println(Application.getInstance().getAttendeeMgmt().isTopAttendee(String.valueOf(1)));
		System.out.println(Application.getInstance().getAttendeeMgmt().isTopAttendee(String.valueOf(8)));
	*/
		
	}
}
