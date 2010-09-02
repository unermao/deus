package it.unipr.ce.dsg.deus.example.d2v;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class D2VTrafficElementBirthScheduler implements SchedulerListener {
	public void newEventScheduled(Event parentEvent, Event newEvent) {
		D2VTrafficElementBirthEvent be = (D2VTrafficElementBirthEvent) parentEvent;
	}
}
