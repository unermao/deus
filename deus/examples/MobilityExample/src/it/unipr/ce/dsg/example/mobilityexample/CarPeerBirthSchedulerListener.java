package it.unipr.ce.dsg.example.mobilityexample;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.SchedulerListener;

/**
 * 
 * @author Marco Picone picone@ce.unipr.it
 *
 */
public class CarPeerBirthSchedulerListener implements SchedulerListener {
	public void newEventScheduled(Event parentEvent, Event newEvent) {
		CarPeerBirthEvent be = (CarPeerBirthEvent) parentEvent; 
	}
}
