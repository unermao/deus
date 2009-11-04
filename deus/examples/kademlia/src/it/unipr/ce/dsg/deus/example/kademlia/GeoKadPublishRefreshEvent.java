package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.RunException;



public class GeoKadPublishRefreshEvent extends NodeEvent {
	
	public GeoKadPublishRefreshEvent (String id, Properties params, Process parentProcess)
	throws InvalidParamsException {
		super(id,params,parentProcess);
		initialize();
	}
	
	public void initialize() { }

	
	public void run() throws RunException {
		GeoKadPeer currNode = (GeoKadPeer) getAssociatedNode();
		for (GeoKadResourceType res : currNode.kademliaResources ) {
			for (GeoKadPeer peer : currNode.find_node(res.getResourceKey())) {
				peer.insertPeer(currNode);
				peer.store(res);
			}
		}

	}

}
