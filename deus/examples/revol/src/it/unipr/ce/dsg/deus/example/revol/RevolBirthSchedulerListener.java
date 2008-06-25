package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.impl.event.DeathEvent;
import it.unipr.ce.dsg.deus.p2p.event.DisconnectionEvent;
import it.unipr.ce.dsg.deus.p2p.event.MultipleRandomConnectionsEvent;

public class RevolBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		RevolBirthEvent be = (RevolBirthEvent) parentEvent; 
		if (newEvent instanceof MultipleRandomConnectionsEvent) {
			((MultipleRandomConnectionsEvent) newEvent).setNodeToConnect(be.getAssociatedNode());
		} else if (newEvent instanceof DisconnectionEvent) {
			((DisconnectionEvent) newEvent).setNodesToDisconnect(be.getAssociatedNode(), null);
		} else if (newEvent instanceof DeathEvent) {
			((DeathEvent) newEvent).setNodeToKill(be.getAssociatedNode());
		} else if (newEvent instanceof RevolAdaptationEvent) {
			((RevolAdaptationEvent) newEvent).setAssociatedNode((RevolNode) be.getAssociatedNode());
		} else if (newEvent instanceof RevolDiscoveryEvent) {
			((RevolDiscoveryEvent) newEvent).setAssociatedNode((RevolNode) be.getAssociatedNode());
		}
	}

}
