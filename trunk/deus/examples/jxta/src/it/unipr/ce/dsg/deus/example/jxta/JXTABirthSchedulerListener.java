package it.unipr.ce.dsg.deus.example.jxta;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * <p>
 * This class is used to initialize the events associated to JXTABirthEvent.
 * </p>
 * 
 * @author  Stefano Sebastio (stefano.sebastio@studenti.unipr.it)
 */

public class JXTABirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {

		JXTABirthEvent be = (JXTABirthEvent) parentEvent;
		if(newEvent instanceof JXTACreateAdvEvent) {
			((JXTACreateAdvEvent) newEvent).setAssociatedNode((JXTAEdgePeer) be.getAssociatedNode());
			//System.out.println("ASS CREATE_ADV " + be.getAssociatedNode());
		} else if (newEvent instanceof JXTAJoinEvent) {
			((JXTAJoinEvent) newEvent).setAssociatedNode((JXTAEdgePeer) be.getAssociatedNode());
			//System.out.println("ASS JOIN: " +  ((JXTAEdgePeer) be.getAssociatedNode()).JXTAID);
		} else if (newEvent instanceof JXTAPublishEvent) {
			((JXTAPublishEvent) newEvent).setAssociatedNode( (JXTAEdgePeer) be.getAssociatedNode());
			//System.out.println("ASS PUB: " + ((JXTAEdgePeer)be.getAssociatedNode()).JXTAID);
		} else if (newEvent instanceof JXTADiscoveryEvent) {
			( (JXTADiscoveryEvent) newEvent).setAssociatedNode( (JXTAEdgePeer) be.getAssociatedNode());
			//System.out.println("ASS RICERCA: " + ((JXTAEdgePeer)be.getAssociatedNode()).JXTAID);
		} else if (newEvent instanceof JXTARefreshRPVEvent) {
			( (JXTARefreshRPVEvent) newEvent).setAssociatedNode( (JXTARendezvousSuperPeer) be.getAssociatedNode());
			//System.out.println("ASS STABIL: " + ((JXTARendezvousSuperPeer)be.getAssociatedNode()).JXTAID);
		}
		
		//System.out.println("ASS: " + be.getAssociatedNode());
		
	}

}
