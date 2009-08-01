package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;


public class KademliaPingEvent extends NodeEvent {
	private KademliaPeer peer = null;
	
	public KademliaPingEvent(String id, Properties params, Process parentProcess, KademliaPeer target) 
	throws InvalidParamsException {
		super(id,params,parentProcess);
		peer = target;
		initialize();
	}

	public void initialize() {
		
	}
	
	public void run() throws RunException {
		KademliaPeer currPeer = (KademliaPeer) getAssociatedNode();
		currPeer.ping(peer);
	}
	
	public Object clone() {
		KademliaPingEvent clone = (KademliaPingEvent) super.clone();
		clone.peer = null;
		return clone;
	}
}