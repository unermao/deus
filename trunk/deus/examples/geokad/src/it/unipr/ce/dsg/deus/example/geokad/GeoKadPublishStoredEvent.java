package it.unipr.ce.dsg.deus.example.geokad;

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

public class GeoKadPublishStoredEvent extends NodeEvent {
	
	public GeoKadPublishStoredEvent (String id, Properties params, Process parentProcess)
	throws InvalidParamsException {
		super (id, params, parentProcess);
		initialize();
	}
	
	public void initialize() { }

	public void run() throws RunException {
		GeoKadPeer currNode = (GeoKadPeer) getAssociatedNode();
		for (GeoKadResourceType res: currNode.storedResources) {
			for (GeoKadPeer peer : currNode.find_node(res.getResourceKey())) {
				peer.insertPeer(currNode);
				peer.store(res);
			}
		}

	}

}
