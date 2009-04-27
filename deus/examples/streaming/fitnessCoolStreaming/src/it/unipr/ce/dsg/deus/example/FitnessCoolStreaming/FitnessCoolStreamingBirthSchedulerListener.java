package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;

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
public class FitnessCoolStreamingBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		BirthEvent be = (BirthEvent) parentEvent; 
		if (newEvent instanceof FitnessCoolStreamingConnectionEvent) {
			((FitnessCoolStreamingConnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		} else if (newEvent instanceof FitnessCoolStreamingDiscoveryEvent) {
			((FitnessCoolStreamingDiscoveryEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		} 
		else if (newEvent instanceof FitnessCoolStreamingDisconnectionEvent) {
			((FitnessCoolStreamingDisconnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}else if (newEvent instanceof DeathEvent) {
			((DeathEvent) newEvent).setNodeToKill(be.getAssociatedNode());
		}		
		else if (newEvent instanceof FitnessCoolStreamingServerNewVideoResourceEvent) {
			((FitnessCoolStreamingServerNewVideoResourceEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}
		else if (newEvent instanceof FitnessCoolStreamingServerNewVideoResourceEvent) {
			((FitnessCoolStreamingServerNewVideoResourceEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
		}
	}

}
