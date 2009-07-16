package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.p2p.event.DisconnectionEvent;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

/**
 * <p>
 * This class is used to initialize the events associated to MultipleRandomConnectionsEvent.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class NsamMultipleRandomConnectionsSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		MultipleRandomConnectionsEvent ce = (MultipleRandomConnectionsEvent) parentEvent; 
		if (newEvent instanceof DisconnectionEvent) {
			((DisconnectionEvent) newEvent).setAssociatedNode((Peer) ce.getAssociatedNode());
			((DisconnectionEvent) newEvent).setNodeToDisconnectFrom(null);
		} 
	}

}
