package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.ArrayList;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.core.Process;

public class KademliaFindValueEvent extends NodeEvent {

	private KademliaPeer reqNode = null;

	private int resourceKey = -1;

	public KademliaFindValueEvent(String id, Properties params,
			Process parentProcess, 
			KademliaPeer requestingNode) throws InvalidParamsException {
		super(id, params, parentProcess);

		reqNode = requestingNode;
		initialize();
	}
	
	public Object clone() {
		KademliaFindValueEvent clone = (KademliaFindValueEvent) super.clone();
		clone.resourceKey = -1;
		clone.reqNode = null;
		return clone;
	}

	public int getResourceKey() {
		return resourceKey;
	}

	public void initialize() {

	}
	@SuppressWarnings("unchecked")
	public void run() throws RunException {
		KademliaPeer currentNode = (KademliaPeer) getAssociatedNode();
		if (resourceKey == -1) {
			throw new RunException("The resourceKey should really be set in "
					+ this);
		}

		Object findv = currentNode.find_value(resourceKey);
		currentNode.insertPeer(reqNode);
		if (findv instanceof ArrayList) {
			reqNode.nlResults.get(resourceKey).addAll((ArrayList<KademliaPeer>) findv);
		} else if (findv instanceof KademliaResourceType) {
			// Resource found!
			reqNode.nlResults.get(resourceKey).setValueFound(true);
			
			//For caching purposes, the initiator must store the key/value pair at the closest node seen which did not return the value.
			for(KademliaPeer p: reqNode.nlResults.get(resourceKey).getFoundNodes()) {
				if (p.getKey() != currentNode.getKey()) {
					p.store(new KademliaResourceType(this.resourceKey));
					return;
				}
			}
			return;
		}
	}
	
	public void setResourceKey(int resourceKey) {
		this.resourceKey = resourceKey;
	}

	public KademliaPeer getRequestingNode() {
		return reqNode;
	}

	public void setRequestingNode(KademliaPeer reqNode) {
		this.reqNode = reqNode;
	}



}
