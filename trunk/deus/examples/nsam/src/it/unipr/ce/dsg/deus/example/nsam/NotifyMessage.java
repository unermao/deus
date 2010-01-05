package it.unipr.ce.dsg.deus.example.nsam;

import java.util.ArrayList;

public class NotifyMessage {

	private String discoveredService = null;
	private ArrayList<CompositionElement> discoveredComp = null;
	
	
	public NotifyMessage(String serv, ArrayList<CompositionElement> comp){
		this.discoveredService= serv;
		this.discoveredComp= comp;
		
	}
	
	public String getDiscoveredService() {
		return discoveredService;
	}

	public void setDiscoveredService(String discoveredService) {
		this.discoveredService = discoveredService;
	}

	public ArrayList<CompositionElement> getDiscoveredComp() {
		return discoveredComp;
	}


}
