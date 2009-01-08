package it.unipr.ce.dsg.deus.example.coolStreaming;

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
public class CoolStreamingBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		BirthEvent be = (BirthEvent) parentEvent; 
		if (newEvent instanceof CoolStreamingConnectionEvent) {
			((CoolStreamingConnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		} else if (newEvent instanceof CoolStreamingDiscoveryEvent) {
			((CoolStreamingDiscoveryEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		} 
		else if (newEvent instanceof CoolStreamingDisconnectionEvent) {
			((CoolStreamingDisconnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}else if (newEvent instanceof DeathEvent) {
			((DeathEvent) newEvent).setNodeToKill(be.getAssociatedNode());
		}		
		else if (newEvent instanceof CoolStreamingServerNewVideoResourceEvent) {
			((CoolStreamingServerNewVideoResourceEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}
	}

}
