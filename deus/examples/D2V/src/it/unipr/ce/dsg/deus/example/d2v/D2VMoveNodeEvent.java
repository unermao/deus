package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;

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
		
		//System.out.println("Move Event !");
		
		D2VPeer currentNode = (D2VPeer) getAssociatedNode();
		currentNode.move(triggeringTime);
		
	}

}
