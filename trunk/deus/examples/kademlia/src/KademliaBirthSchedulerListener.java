package it.unipr.ce.dsg.deus.example.kademlia;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

public class KademliaBirthSchedulerListener implements SchedulerListener {

	// 090317: Not added Ping and Find* Events....
	public void newEventScheduled(Event parentEvent, Event newEvent) {
		KademliaBirthEvent be = (KademliaBirthEvent) parentEvent;
		if (newEvent instanceof KademliaJoinEvent) {
			((KademliaJoinEvent) newEvent).setAssociatedNode((KademliaPeer) be.getAssociatedNode());
		} else if (newEvent instanceof KademliaPublishAllEvent) {
			((KademliaPublishAllEvent) newEvent).setAssociatedNode((KademliaPeer) be.getAssociatedNode());
		} else if (newEvent instanceof KademliaPublishRefreshEvent) {
			((KademliaPublishRefreshEvent) newEvent).setAssociatedNode((KademliaPeer) be.getAssociatedNode());
		} else if (newEvent instanceof KademliaNodeLookUpEvent)  {
			((KademliaNodeLookUpEvent) newEvent).setAssociatedNode((KademliaPeer) be.getAssociatedNode());
		} else if (newEvent instanceof KademliaValueLookUpEvent)  {
			((KademliaValueLookUpEvent) newEvent).setAssociatedNode((KademliaPeer) be.getAssociatedNode());
		} else if (newEvent instanceof KademliaPublishStoredEvent)  {
			((KademliaPublishStoredEvent) newEvent).setAssociatedNode((KademliaPeer) be.getAssociatedNode());
		} else if (newEvent instanceof KademliaDisconnectionEvent) {
			((KademliaDisconnectionEvent) newEvent).setAssociatedNode((KademliaPeer) be.getAssociatedNode());
		} else if (newEvent instanceof KademliaDeathEvent)  {
			((KademliaDeathEvent) newEvent).setNodeToKill((KademliaPeer) be.getAssociatedNode());
		}
			

	}

}
