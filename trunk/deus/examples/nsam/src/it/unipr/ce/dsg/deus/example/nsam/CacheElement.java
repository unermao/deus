package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Engine;

import java.util.ArrayList;

public class CacheElement {

	//private NsamService cachedService = null;
	private String cachedService = null;
	private ArrayList<CompositionElement> cachedComp = new ArrayList<CompositionElement>();
	private float executionTime = 0;
	
	public CacheElement(String serv, ArrayList<CompositionElement> comp){
		this.cachedService=serv;
		this.cachedComp=comp;
		this.executionTime= Engine.getDefault().getVirtualTime();
	}

	public String getCachedService() {
		return cachedService;
	}

	public void setCachedService(String cachedService) {
		this.cachedService = cachedService;
	}

	public ArrayList<CompositionElement> getCachedComp() {
		return cachedComp;
	}

	public void setCachedComp(ArrayList<CompositionElement> cachedComp) {
		this.cachedComp = cachedComp;
	}

	public float getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(float executionTime) {
		this.executionTime = executionTime;
	}
	
	
}
