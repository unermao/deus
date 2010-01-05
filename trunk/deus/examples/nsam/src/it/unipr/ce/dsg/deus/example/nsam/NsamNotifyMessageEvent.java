package it.unipr.ce.dsg.deus.example.nsam;



import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;


public class NsamNotifyMessageEvent extends NodeEvent {

	private NotifyMessage notif = null;
	
	
	public NsamNotifyMessageEvent(String id, Properties params,
			Process parentProcess, NotifyMessage incomingMsg) throws InvalidParamsException {
		super(id, params, parentProcess);	
		this.notif= incomingMsg;
		
	}
	
	
	public NotifyMessage getNotifyMsg() {
		return notif;
	}


	public void setNotifyMsg(NotifyMessage notif) {
		this.notif = notif;
	}


	public Object clone(){
		NsamNotifyMessageEvent clone = (NsamNotifyMessageEvent) super.clone();
		return clone;
	}
	
	
	public void run() throws RunException {
	
		NsamPeer currentNode = (NsamPeer)this.getAssociatedNode();
		currentNode.manageNotification(notif.getDiscoveredService(), notif.getDiscoveredComp());
	}
	
}
