package it.unipr.ce.dsg.deus.example.nsam;


import java.util.ArrayList;

public class ServiceDiscoveryStructure {

	
		
	private NsamService requestService = null;
	private float requestTime;
	private NsamService localComponent = null;
	private NsamService associatedSearch = null;
	private ArrayList<ArrayList<CompositionElement>> compositionAlternatives = new ArrayList<ArrayList<CompositionElement>>();
	
	
	public ServiceDiscoveryStructure() {	
	}
	
	public ServiceDiscoveryStructure(NsamService service, float time){
		this.requestService= service;
		this.requestTime = time;
	}
	public ServiceDiscoveryStructure(NsamService service, float time, NsamService local, NsamService associated){
		this.requestService= service;
		this.requestTime = time;
		this.associatedSearch= associated;
		this.localComponent= local;
	}
	
	public NsamService getLocalComponent() {
		return localComponent;
	}


	public NsamService getAssociatedSearch() {
		return associatedSearch;
	}

	public void setAssociatedSearch(NsamService associatedSearch) {
		this.associatedSearch = associatedSearch;
	}

	public NsamService getRequestService() {
		return requestService;
	}
	public void setRequestService(NsamService requestService) {
		this.requestService = requestService;
	}
	public float getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(float requestTime) {
		this.requestTime = requestTime;
	}

	public ArrayList<ArrayList<CompositionElement>> getCompositionAlternatives() {
		return compositionAlternatives;
	}

	


}
