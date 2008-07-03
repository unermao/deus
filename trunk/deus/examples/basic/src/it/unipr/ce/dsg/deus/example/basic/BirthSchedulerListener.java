package it.unipr.ce.dsg.deus.example.basic;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.impl.event.BirthEvent;
import it.unipr.ce.dsg.deus.impl.event.DeathEvent;
import it.unipr.ce.dsg.deus.p2p.event.DisconnectionEvent;
import it.unipr.ce.dsg.deus.p2p.event.SingleConnectionEvent;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

public class BirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		BirthEvent be = (BirthEvent) parentEvent; 
		if (newEvent instanceof SingleConnectionEvent) {
			((SingleConnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
			((SingleConnectionEvent) newEvent).setNodeToConnectTo(null);
		} else if (newEvent instanceof DisconnectionEvent) {
			((DisconnectionEvent) newEvent).setAssociatedNode((Peer) be.getAssociatedNode());
			((DisconnectionEvent) newEvent).setNodeToDisconnectFrom(null);
		} else if (newEvent instanceof DeathEvent) {
			((DeathEvent) newEvent).setNodeToKill(be.getAssociatedNode());
		}
	}

}
