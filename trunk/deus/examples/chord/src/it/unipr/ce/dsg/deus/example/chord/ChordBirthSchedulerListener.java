package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * <p>
 * This class is used to initialize the events associated to ChordBirthEvent.
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author  Marco Muro (marco.muro@studenti.unipr.it)
 */
public class ChordBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		ChordBirthEvent be = (ChordBirthEvent) parentEvent;
		if (newEvent instanceof ChordJoinEvent) {
			((ChordJoinEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		} else if (newEvent instanceof ChordStabilizeEvent) {
			((ChordStabilizeEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		} else if (newEvent instanceof ChordFixFingersEvent) {
			((ChordFixFingersEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		} else if (newEvent instanceof ChordPublishEvent) {
			((ChordPublishEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		}	else if (newEvent instanceof ChordRefreshPublishEvent) {
				((ChordRefreshPublishEvent) newEvent).setAssociatedNode((ChordPeer) be
						.getAssociatedNode());
		}	else if (newEvent instanceof ChordDiscoveryEvent) {
			((ChordDiscoveryEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		}	else if (newEvent instanceof ChordDisconnectionEvent) {
			((ChordDisconnectionEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		}   else if (newEvent instanceof ChordDeathEvent) {
			((ChordDeathEvent) newEvent).setNodeToKill(be.getAssociatedNode());
		}
	}

}
