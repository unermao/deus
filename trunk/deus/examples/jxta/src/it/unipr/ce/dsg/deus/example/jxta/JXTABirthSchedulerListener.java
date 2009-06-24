package it.unipr.ce.dsg.deus.example.jxta;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * <p>
 * This class is used to initialize the events associated to JXTABirthEvent.
 * </p>
 * 
 * @author  Stefano Sebastio
 */

public class JXTABirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		// TODO Auto-generated method stub
		JXTABirthEvent be = (JXTABirthEvent) parentEvent;
		if(newEvent instanceof JXTACreateAdvEvent) {
			((JXTACreateAdvEvent) newEvent).setAssociatedNode((JXTAEdgePeer) be.getAssociatedNode());
			System.out.println("scheduler listener, ass node: " + ((JXTACreateAdvEvent) newEvent).getAssociatedNode());
			System.out.println("scheduler listener, ass to: " + newEvent);
		}
		System.out.println("ASS: " + be.getAssociatedNode());
	}

}
