package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;

public class GeoKadGossipEvent extends NodeEvent {
	//private int resourceKey = -1;

	private GeoKadGossipMessage gossipMessage = null;
	
	public GeoKadGossipEvent(String id, Properties params,
			Process parentProcess)
			throws InvalidParamsException {
		
		super(id, params, parentProcess);
		this.gossipMessage = gossipMessage;
	}

	public Object clone() {
		
		GeoKadGossipEvent clone = (GeoKadGossipEvent) super.clone();
		clone.gossipMessage = null;
		
		return clone;
	}

	public void run() throws RunException {
		
		GeoKadPeer currentNode = (GeoKadPeer) getAssociatedNode();
		
		currentNode.setSentMessages(currentNode.getSentMessages() + 1);
		
		//System.out.println("Move Node: " + currentNode);

		currentNode.routeGossipMessage(triggeringTime, gossipMessage);
		
		/*
		try
		{
			if(Engine.getDefault().getSimulationRandom().nextBoolean() == true)
			{
				GeoKadNodeLookUpEvent fn = (GeoKadNodeLookUpEvent) new GeoKadNodeLookUpEvent("find_node", new Properties(), null).createInstance(triggeringTime + 1);
				fn.setOneShot(true);
				fn.setAssociatedNode(currentNode);
				Engine.getDefault().insertIntoEventsList(fn);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public GeoKadGossipMessage getGossipMessage() {
		return gossipMessage;
	}

	public void setGossipMessage(GeoKadGossipMessage gossipMessage) {
		this.gossipMessage = gossipMessage;
	}

}
