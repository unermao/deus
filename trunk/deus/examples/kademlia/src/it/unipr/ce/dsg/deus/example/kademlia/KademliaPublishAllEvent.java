package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class KademliaPublishAllEvent extends NodeEvent {

	public KademliaPublishAllEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		KademliaPeer currNode = (KademliaPeer) getAssociatedNode();
		for (KademliaResourceType res : currNode.kademliaResources) {
			try {
				KademliaNodeLookUpEvent nlk = (KademliaNodeLookUpEvent) new KademliaNodeLookUpEvent(
						"node_lookup", params, null)
						.createInstance(triggeringTime);
				nlk.setRes(res);
				nlk.setOneShot(true);
				nlk.setAssociatedNode(currNode);
				nlk.setResourceKey(res.getResourceKey());
				Engine.getDefault().insertIntoEventsList(nlk);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
