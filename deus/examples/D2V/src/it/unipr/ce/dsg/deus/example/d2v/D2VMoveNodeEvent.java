package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;

public class D2VMoveNodeEvent extends NodeEvent {
	
	public D2VMoveNodeEvent(String id, Properties params,
			Process parentProcess)
			throws InvalidParamsException {
		
		super(id, params, parentProcess);
	
	}

	public Object clone() {
		D2VMoveNodeEvent clone = (D2VMoveNodeEvent) super.clone();
		return clone;
	}

	public void run() throws RunException {
		
		D2VPeer currentNode = (D2VPeer) getAssociatedNode();
		
		//DebugLog log = new DebugLog();
		//log.printStart(currentNode.getKey(),this.getClass().getName(),triggeringTime);
		
		currentNode.move(triggeringTime);
		
		//log.printEnd(currentNode.getKey(),this.getClass().getName(),triggeringTime);
	}

}
