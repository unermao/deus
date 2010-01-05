package it.unipr.ce.dsg.deus.example.nsam;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.impl.event.BirthEvent;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;


/**
 * <p>
 * This class is used to initialize the events associated to NsamBirthEvent.
 * </p>
 * 
 * @author Maria Chiara Laghi (laghi@ce.unipr.it)
 *
 */
public class NsamBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		BirthEvent be = (BirthEvent) parentEvent; 
		if (newEvent instanceof MultipleRandomConnectionsEvent) {
			((MultipleRandomConnectionsEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
				} else if (newEvent instanceof NsamDiscoveryEvent) {
			((NsamDiscoveryEvent) newEvent).setAssociatedNode((NsamPeer) be.getAssociatedNode());
			System.out.println("beschlistener: be.associatedNode " + be.getAssociatedNode());
				}
	}

}
