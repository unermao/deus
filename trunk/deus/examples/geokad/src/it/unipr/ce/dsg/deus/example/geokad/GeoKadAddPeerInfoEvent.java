package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;


public class GeoKadAddPeerInfoEvent extends NodeEvent {

	private GeoKadPeerInfo peerInfo = null;
	
	public GeoKadAddPeerInfoEvent(String id, Properties params, Process parentProcess) 
	throws InvalidParamsException {
		super(id,params,parentProcess);
	}

	
	public void run() throws RunException {
		GeoKadPeer currPeer = (GeoKadPeer) getAssociatedNode();
		currPeer.setSentMessages(currPeer.getSentMessages() + 1);
		currPeer.insertPeer(peerInfo);
	}
	
	public Object clone() {
		GeoKadAddPeerInfoEvent clone = (GeoKadAddPeerInfoEvent) super.clone();
		clone.peerInfo = null;
		return clone;
	}


	public GeoKadPeerInfo getPeerInfo() {
		return peerInfo;
	}


	public void setPeerInfo(GeoKadPeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}
}