package org.revager.gui.helpers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class LinkGroup {

	//TODO private List<VLink> vLinksList= new ArrayList<VLink>();
	private List<HLink> hLinksList= new ArrayList<HLink>();
	
	public void addLink(HLink link){
		hLinksList.add(link);
		link.setLocalRolloverIcon(link.getLocalIcon());
	}
	
	public void resetAllLinks(){
		for(HLink link:hLinksList){
			link.setBold(false);
			link.setColor(Color.BLACK);
			link.setLocalRolloverIcon(link.getLocalDisIcon());
			link.setLocalIcon(link.getLocalDisIcon());
			//link.getLocalBttn().setSelected(false);
			
			
		}
	}

	public void selectLink(HLink selLink) {
		for(HLink link:hLinksList){
			if(link==selLink){
				link.setBold(true);
				link.setColor(Color.BLUE);
				link.setLocalRolloverIcon(link.getLocalSelIcon());
				link.setLocalIcon(link.getLocalSelIcon());
				//link.getLocalBttn().setSelected(true);
				link.setSelected(true);
			}else{
				link.setBold(false);
				link.setColor(Color.BLACK);
				link.setLocalRolloverIcon(link.getLocalDisIcon());
				link.setLocalIcon(link.getLocalDisIcon());
				//link.getLocalBttn().setSelected(false);
				link.setSelected(false);
			}
		}
	}
	
	public String getSelectedLinkText(){
		for(HLink link:hLinksList){
			if(link.getSelected())
				return link.getLocalLbl().getText();
		}
		return null;
	}
	
	public int getSelectedLinkIndex(){
		for(int index=0;index<hLinksList.size();index++){
			if(hLinksList.get(index).getSelected())
				return index;
		}
		return -1;
	}

	public void deselectAllLinks() {
		for(HLink link:hLinksList){
			link.setSelected(false);
		}
		
	}

}
