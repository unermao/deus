package it.unipr.ce.dsg.deus.example.d2v;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VFirstDiscoverySchedulerListener implements SchedulerListener {
	public void newEventScheduled(Event parentEvent, Event newEvent) {
		
		D2VFirstDiscoveryEvent be = (D2VFirstDiscoveryEvent) parentEvent;
		
		if (newEvent instanceof D2VPeerMovementEvent) {
			((D2VPeerMovementEvent) newEvent).setAssociatedNode((D2VPeer) be.getAssociatedNode());
		} else if (newEvent instanceof D2VDiscoveryEvent)  {
			((D2VDiscoveryEvent) newEvent).setAssociatedNode((D2VPeer) be.getAssociatedNode());
		}
	}
}
