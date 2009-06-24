package it.unipr.ce.dsg.deus.example.life;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

public class LifeBirthSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		LifeBirthEvent be = (LifeBirthEvent) parentEvent;
		if (newEvent instanceof LifeUpdateWorldEvent) {
			((LifeUpdateWorldEvent) newEvent).setAssociatedNode((LifeRegion) be
					.getAssociatedNode());
		}
	}
}
