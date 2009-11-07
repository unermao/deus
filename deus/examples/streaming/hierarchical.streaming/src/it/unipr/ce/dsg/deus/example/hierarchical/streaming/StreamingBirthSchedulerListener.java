package it.unipr.ce.dsg.deus.example.hierarchical.streaming;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.impl.event.BirthEvent;
import it.unipr.ce.dsg.deus.impl.event.DeathEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;


/**
 * <p>
 * This class is used to initialize the events associated to RevolBirthEvent.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class StreamingBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		
		BirthEvent be = (BirthEvent) parentEvent;
		
		if (newEvent instanceof StreamingConnectionEvent) {
			((StreamingConnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		} else if (newEvent instanceof StreamingDiscoveryEvent) {
			((StreamingDiscoveryEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		} 
		else if (newEvent instanceof StreamingDisconnectionEvent) {
			((StreamingDisconnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}else if (newEvent instanceof DeathEvent) {
			((DeathEvent) newEvent).setNodeToKill(be.getAssociatedNode());
		}
		
		else if (newEvent instanceof Streaming3GTo2GEvent) {
			((Streaming3GTo2GEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}
		else if (newEvent instanceof Streaming2GTo3GEvent) {
			((Streaming2GTo3GEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}
		else if (newEvent instanceof StreamingServerNewVideoResourceEvent) {
			((StreamingServerNewVideoResourceEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}
	}

}
