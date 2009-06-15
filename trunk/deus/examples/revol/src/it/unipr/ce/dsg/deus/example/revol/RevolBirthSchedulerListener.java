package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.impl.event.BirthEvent;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.event.ExpTopologyConnectionEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;


/**
 * <p>
 * This class is used to initialize the events associated to RevolBirthEvent.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class RevolBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		BirthEvent be = (BirthEvent) parentEvent; 
		if (newEvent instanceof MultipleRandomConnectionsEvent) {
			((MultipleRandomConnectionsEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		} else if (newEvent instanceof RevolAdaptationEvent) {
			((RevolAdaptationEvent) newEvent).setAssociatedNode((RevolPeer) be.getAssociatedNode());
		} else if (newEvent instanceof RevolDiscoveryEvent) {
			((RevolDiscoveryEvent) newEvent).setAssociatedNode((RevolPeer) be.getAssociatedNode());
		} else if (newEvent instanceof ExpTopologyConnectionEvent) {
			((ExpTopologyConnectionEvent) newEvent).setAssociatedNode((RevolPeer) be.getAssociatedNode());
		}
	}

}
