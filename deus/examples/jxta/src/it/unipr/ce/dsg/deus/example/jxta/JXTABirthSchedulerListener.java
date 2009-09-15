package it.unipr.ce.dsg.deus.example.jxta;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * 
 * This class is used to initialize the events associated to JXTABirthEvent.
 * 
 * @author  Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 */

public class JXTABirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {

		JXTABirthEvent be = (JXTABirthEvent) parentEvent;
		if(newEvent instanceof JXTACreateAdvEvent) {
			((JXTACreateAdvEvent) newEvent).setAssociatedNode((JXTAEdgePeer) be.getAssociatedNode());

		} else if (newEvent instanceof JXTAJoinEvent) {
			((JXTAJoinEvent) newEvent).setAssociatedNode((JXTAEdgePeer) be.getAssociatedNode());

		} else if (newEvent instanceof JXTAPublishEvent) {
			((JXTAPublishEvent) newEvent).setAssociatedNode( (JXTAEdgePeer) be.getAssociatedNode());

		} else if (newEvent instanceof JXTADiscoveryEvent) {
			( (JXTADiscoveryEvent) newEvent).setAssociatedNode( (JXTAEdgePeer) be.getAssociatedNode());

		} else if (newEvent instanceof JXTARefreshRPVEvent) {
			( (JXTARefreshRPVEvent) newEvent).setAssociatedNode( (JXTARendezvousSuperPeer) be.getAssociatedNode());
		}
		
	}

}
