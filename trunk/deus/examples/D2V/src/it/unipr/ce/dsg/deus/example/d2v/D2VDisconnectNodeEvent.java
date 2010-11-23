package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.example.d2v.util.DebugLog;

public class D2VDisconnectNodeEvent extends NodeEvent {
	
	public D2VDisconnectNodeEvent(String id, Properties params,
			Process parentProcess)
			throws InvalidParamsException {
		
		super(id, params, parentProcess);
	
	}

	public Object clone() {
		D2VDisconnectNodeEvent clone = (D2VDisconnectNodeEvent) super.clone();
		return clone;
	}

	public void run() throws RunException {
		D2VPeer currentNode = (D2VPeer) getAssociatedNode();
		currentNode.disconnectNode();
	}

}
