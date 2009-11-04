package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;


public class GeoKadPingEvent extends NodeEvent {
	private GeoKadPeer peer = null;
	
	public GeoKadPingEvent(String id, Properties params, Process parentProcess, GeoKadPeer target) 
	throws InvalidParamsException {
		super(id,params,parentProcess);
		peer = target;
		initialize();
	}

	public void initialize() {
		
	}
	
	public void run() throws RunException {
		GeoKadPeer currPeer = (GeoKadPeer) getAssociatedNode();
		currPeer.ping(peer);
	}
	
	public Object clone() {
		GeoKadPingEvent clone = (GeoKadPingEvent) super.clone();
		clone.peer = null;
		return clone;
	}
}