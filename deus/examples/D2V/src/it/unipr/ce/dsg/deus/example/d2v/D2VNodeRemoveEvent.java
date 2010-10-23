package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;
import it.unipr.ce.dsg.deus.p2p.node.Peer;


public class D2VNodeRemoveEvent extends NodeEvent {

	private D2VPeerDescriptor peerInfo = null;
	
	public D2VNodeRemoveEvent(String id, Properties params, Process parentProcess) 
	throws InvalidParamsException {
		super(id,params,parentProcess);
	}

	
	public void run() throws RunException {
		
		D2VPeer currPeer = (D2VPeer) getAssociatedNode();
		
		//currPeer.setSentMessages(currPeer.getSentMessages() + 1);
		currPeer.incrementSentMessages();
		
		double kbValue = 0.0;
		String messageString = "REMOVE_NODE#";
		kbValue = (double)messageString.getBytes().length / 1000.0;
		currPeer.addSentKbAmountForDGT(kbValue+D2VPeerDescriptor.getStructureKbLenght());
		
		currPeer.getGb().removePeer(peerInfo, triggeringTime);
		
		//Remove the peer from original neighbor list
		currPeer.removeNeighbor( (Peer) Engine.getDefault().getNodeByKey(peerInfo.getKey()));
		
	}
	
	public Object clone() {
		D2VNodeRemoveEvent clone = (D2VNodeRemoveEvent) super.clone();
		clone.peerInfo = null;
		return clone;
	}


	public D2VPeerDescriptor getPeerInfo() {
		return peerInfo;
	}


	public void setPeerInfo(D2VPeerDescriptor peerInfo) {
		this.peerInfo = peerInfo;
	}
}