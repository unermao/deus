package it.unipr.ce.dsg.deus.mobility;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;

/**
 * 
 * @author Marco Picone (picone@ce.unipr.it)
 *
 */
public class MovePeerEvent extends NodeEvent {
	
	public MovePeerEvent(String id, Properties params,
			Process parentProcess)
			throws InvalidParamsException {
		
		super(id, params, parentProcess);
	
	}

	public Object clone() {
		MovePeerEvent clone = (MovePeerEvent) super.clone();
		return clone;
	}

	public void run() throws RunException {
		MobilePeer currentNode = (MobilePeer) getAssociatedNode();		
		currentNode.move(triggeringTime);
	}

}
