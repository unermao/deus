package it.unipr.ce.dsg.deus.example.nsam;


import java.util.ArrayList;

public class ServiceCompositionStructure {

	
	private String serviceId = new String();
	private ArrayList<String> requestingPeers = new ArrayList<String>();
	private ArrayList<String> localComponents = new ArrayList<String>();
	private ArrayList<String> remoteNeededComponents = new ArrayList<String>();
	

	
	
	public ServiceCompositionStructure(String serviceId) {
		this.serviceId= serviceId;
	}
	
	public ArrayList<String> updateLocalComponents(String serviceId){
		localComponents.add(serviceId);
		return localComponents;
	}
	
	public ArrayList<String> getLocalComponents(){
		return localComponents;
	}
	
}
