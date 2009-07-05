package it.unipr.ce.dsg.deus.example.life;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

public class LifeCreationSchedulerListener implements SchedulerListener {

	public void newEventScheduled(Event parentEvent, Event newEvent) {
		LifeCreationEvent be = (LifeCreationEvent) parentEvent;
		if (newEvent instanceof LifeStartBirthsDeathsEvent) {
			((LifeStartBirthsDeathsEvent) newEvent).setAssociatedNode((LifeRegion) be
					.getAssociatedNode());
		}
	}
}
