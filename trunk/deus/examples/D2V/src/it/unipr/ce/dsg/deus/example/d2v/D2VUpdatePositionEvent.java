package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.example.d2v.peer.D2VPeerDescriptor;


public class D2VUpdatePositionEvent extends NodeEvent {

	private D2VPeerDescriptor peerInfo = null;
	
	public D2VUpdatePositionEvent(String id, Properties params, Process parentProcess) 
	throws InvalidParamsException {
		super(id,params,parentProcess);
	}

	
	public void run() throws RunException {
		D2VPeer currPeer = (D2VPeer) getAssociatedNode();
		//System.out.println("VT:"+triggeringTime+" UPDATE_POSITION_EVENT");
		//currPeer.setSentMessages(currPeer.getSentMessages() + 1);
		currPeer.incrementSentMessages();
		
		double kbValue = 0.0;
		String messageString = "UPDATE_POS#";
		kbValue = (double)messageString.getBytes().length / 1000.0;
		currPeer.addSentKbAmount(kbValue+D2VPeerDescriptor.getStructureKbLenght());
		
		
		currPeer.insertPeer("D2VUpdatePositionEvent",peerInfo);
	}
	
	public Object clone() {
		D2VUpdatePositionEvent clone = (D2VUpdatePositionEvent) super.clone();
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