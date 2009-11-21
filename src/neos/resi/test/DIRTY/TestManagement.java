/**
 * 
 */
package neos.resi.test.DIRTY;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.app.model.schema.Review;

/**
 * @author Sandra
 *
 */
public class TestManagement {

	public static void main(String[] args) {
		
		Data.getInstance().getResiData().setReview(new Review());
		
		Application.getInstance().getReviewMgmt().setReviewName("TEST");
		
		System.out.println(Application.getInstance().getReviewMgmt().getReviewName());
		
		//for (String element : List) {
			
		}
	}
	

