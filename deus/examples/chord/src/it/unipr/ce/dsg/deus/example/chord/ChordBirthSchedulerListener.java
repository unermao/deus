package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;
import it.unipr.ce.dsg.deus.impl.event.BirthEvent;

/**
 * <p>
 * This class is used to initialize the events associated to RevolBirthEvent.
 * </p>
 * 
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		BirthEvent be = (BirthEvent) parentEvent;
		if (newEvent instanceof ChordJoinEvent) {
			((ChordJoinEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		} else if (newEvent instanceof ChordStabilizeEvent) {
			((ChordStabilizeEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		} else if (newEvent instanceof ChordFixFingersEvent) {
			((ChordFixFingersEvent) newEvent).setAssociatedNode((ChordPeer) be
					.getAssociatedNode());
		}
	}

}
