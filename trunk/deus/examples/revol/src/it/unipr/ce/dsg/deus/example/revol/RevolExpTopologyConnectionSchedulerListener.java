package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.p2p.event.DisconnectionEvent;
import it.unipr.ce.dsg.deus.p2p.event.ExpTopologyConnectionEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

/**
 * <p>
 * This class is used to initialize the events associated to ExpTopologyConnectionEvent.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class RevolExpTopologyConnectionSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		ExpTopologyConnectionEvent ce = (ExpTopologyConnectionEvent) parentEvent; 
		if (newEvent instanceof DisconnectionEvent) {
			((DisconnectionEvent) newEvent).setAssociatedNode((Peer) ce.getAssociatedNode());
			((DisconnectionEvent) newEvent).setNodeToDisconnectFrom(null);
		} 
	}

}
