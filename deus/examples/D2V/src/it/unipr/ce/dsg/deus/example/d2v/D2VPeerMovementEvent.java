package it.unipr.ce.dsg.deus.example.d2v;

import java.util.Properties;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VPeerMovementEvent extends NodeEvent {

	public D2VPeerMovementEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		D2VPeer connectingNode = (D2VPeer) this.getAssociatedNode();
		System.out.println("VT:"+triggeringTime+" First Movement Event ---> Peer Key: " + connectingNode.getKey());
	}

}
