package it.unipr.ce.dsg.deus.example.geokad;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

public class GeoKadBirthSchedulerListener implements SchedulerListener {
	public void newEventScheduled(Event parentEvent, Event newEvent) {
		GeoKadBirthEvent be = (GeoKadBirthEvent) parentEvent;
		if (newEvent instanceof GeoKadJoinEvent) {
			((GeoKadJoinEvent) newEvent).setAssociatedNode((GeoKadPeer) be.getAssociatedNode());
		} 
		else if (newEvent instanceof GeoKadBootStrapJoinEvent) {
			((GeoKadBootStrapJoinEvent) newEvent).setAssociatedNode((GeoKadPeer) be.getAssociatedNode());
		} else if (newEvent instanceof GeoKadNodeLookUpEvent)  {
			((GeoKadNodeLookUpEvent) newEvent).setAssociatedNode((GeoKadPeer) be.getAssociatedNode());
		} else if (newEvent instanceof GeoKadDisconnectionEvent) {
			((GeoKadDisconnectionEvent) newEvent).setAssociatedNode((GeoKadPeer) be.getAssociatedNode());
		} else if (newEvent instanceof GeoKadDeathEvent)  {
			((GeoKadDeathEvent) newEvent).setNodeToKill((GeoKadPeer) be.getAssociatedNode());
		}
	}
}
