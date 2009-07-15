package it.unipr.ce.dsg.deus.example.jxta;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * This event represents the refresh of Rendezvous Peer View (RPV) of a Rendezvous 
 * super peer.
 * 
 * @author Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 * 
 */

public class JXTARefreshRPVEvent extends NodeEvent {

	public JXTARefreshRPVEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

	}

	@Override
	public void run() throws RunException {

		if( getAssociatedNode() != null && ((JXTARendezvousSuperPeer) getAssociatedNode()).isConnected() )
			( (JXTARendezvousSuperPeer)getAssociatedNode()).testRPV();

	}

}
