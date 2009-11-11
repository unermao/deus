package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;

public class GeoKadMoveNodeEvent extends NodeEvent {
	//private int resourceKey = -1;

	public GeoKadMoveNodeEvent(String id, Properties params,
			Process parentProcess)
			throws InvalidParamsException {
		
		super(id, params, parentProcess);
		
	}

	public Object clone() {
		GeoKadMoveNodeEvent clone = (GeoKadMoveNodeEvent) super.clone();
		return clone;
	}

	public void run() throws RunException {
		
		GeoKadPeer currentNode = (GeoKadPeer) getAssociatedNode();
	
		
		//System.out.println("Move Node: " + currentNode);

		currentNode.move(triggeringTime);
	}

}
