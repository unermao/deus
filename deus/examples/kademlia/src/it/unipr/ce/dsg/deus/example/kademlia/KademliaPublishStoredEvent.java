package it.unipr.ce.dsg.deus.example.kademlia;

import it.unipr.ce.dsg.deus.core.NodeEvent;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * Like KademliaPublishAllEvent but republishing STORE <key, value> couples
 * This event is to be triggered every hour
 * 
 * @author Vittorio
 *
 */

public class KademliaPublishStoredEvent extends NodeEvent {
	
	public KademliaPublishStoredEvent (String id, Properties params, Process parentProcess)
	throws InvalidParamsException {
		super (id, params, parentProcess);
		initialize();
	}
	
	public void initialize() { }

	public void run() throws RunException {
		KademliaPeer currNode = (KademliaPeer) getAssociatedNode();
		for (KademliaResourceType res: currNode.storedResources) {
			for (KademliaPeer peer : currNode.find_node(res.getResourceKey())) {
				peer.insertPeer(currNode);
				peer.store(res);
			}
		}

	}

}
