package it.unipr.ce.dsg.deus.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public abstract class Event  extends SimulationObject implements Comparable<Event>, Cloneable {
	protected String id = null;
	protected Properties params = null;
	protected boolean isOneShot = false; 
	protected ArrayList<Event> referencedEvents = null;
	protected float triggeringTime = 0;
	protected Process parentProcess = null;
	private SchedulerListener schedulerListener = null;
	
	public Event(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		this.id = id;
		this.params = params;
		this.parentProcess = parentProcess;
		this.referencedEvents = new ArrayList<Event>();
	}

	public Properties getParams() {
		return params;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	public Event createInstance(float triggeringTime) {
		Event clone = (Event) clone();
		clone.schedulerListener = schedulerListener;
		clone.triggeringTime = triggeringTime;
		return clone;
	}

	public abstract void initialize() throws InvalidParamsException;

	public boolean equals(Object o) {
		return id.equals(((Event) o).getId());
	}

	public String getId() {
		return id;
	}

	public ArrayList<Event> getReferencedEvents() {
		return referencedEvents;
	}

	public float getTriggeringTime() {
		return triggeringTime;
	}

	public abstract void run() throws RunException;

	public void scheduleReferencedEvents() {
		float nextTriggeringTime = triggeringTime;
		
		for (Iterator<Event> it = referencedEvents.iterator(); it.hasNext();) {
			Event event = (Event) it.next();
			if (event.getParentProcess() == null)
				continue;
			nextTriggeringTime = event.getParentProcess().getNextTriggeringTime(nextTriggeringTime);
			Event eventToSchedule = event.createInstance(nextTriggeringTime);
			schedulerListener.newEventScheduled(this, eventToSchedule);
			Engine.getDefault().insertIntoEventsList(eventToSchedule);
		}
	}

	public int compareTo(Event e) {
		int result = 0;
		if (this.triggeringTime < e.triggeringTime)
			result = -1;
		else if (this.triggeringTime == e.triggeringTime)
			result = 0;
		else if (this.triggeringTime > e.triggeringTime)
			result = 1;
		return result;
	}

	public void setParentProcess(Process parentProcess) {
		this.parentProcess = parentProcess;
	}
	
	public Process getParentProcess() {
		return parentProcess;
	}

	public void setSchedulerListener(SchedulerListener l) {
		schedulerListener = l;
	}
	
	public void setOneShot(boolean isOneShot) {
		this.isOneShot = isOneShot;
	}
	
	public boolean isOneShot() {
		return isOneShot;
	}
}