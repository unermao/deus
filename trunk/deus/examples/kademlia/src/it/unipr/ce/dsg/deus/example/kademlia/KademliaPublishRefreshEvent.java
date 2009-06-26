package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;



public class KademliaPublishRefreshEvent extends NodeEvent {
	
	public KademliaPublishRefreshEvent (String id, Properties params, Process parentProcess)
	throws InvalidParamsException {
		super(id,params,parentProcess);
		initialize();
	}
	
	public void initialize() { }

	
	public void run() throws RunException {
		KademliaPeer currNode = (KademliaPeer) getAssociatedNode();
		for (KademliaResourceType res : currNode.kademliaResources ) {
			for (KademliaPeer peer : currNode.find_node(res.getResourceKey())) {
				peer.insertPeer(currNode);
				peer.store(res);
			}
		}

	}

}
