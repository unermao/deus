package it.unipr.ce.dsg.deus.example.d2v;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VBirthSchedulerListener implements SchedulerListener {
	public void newEventScheduled(Event parentEvent, Event newEvent) {
		D2VBirthEvent be = (D2VBirthEvent) parentEvent;
		if (newEvent instanceof D2VFirstDiscoveryEvent) {
			((D2VFirstDiscoveryEvent) newEvent).setAssociatedNode((D2VPeer) be.getAssociatedNode());
		} 
	}
}
