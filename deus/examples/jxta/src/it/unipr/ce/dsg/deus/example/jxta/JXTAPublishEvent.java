package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * This event represents the publish of all not
 * just published Advertisement of the Edge Peer.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */

public class JXTAPublishEvent extends NodeEvent {

	public JXTAPublishEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {


		JXTAEdgePeer publishingNode = (JXTAEdgePeer) getAssociatedNode();
		if(publishingNode != null && publishingNode.isConnected())
			publishingNode.publishAdvertisement();
	}

}
