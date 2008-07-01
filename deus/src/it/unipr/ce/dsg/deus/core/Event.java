package it.unipr.ce.dsg.deus.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * <p>
 * The Event class represents the simulation object being scheduled by the
 * Engine.
 * </p>
 * <p>
 * Each event is identified by the configuration id, a set of properties, a flag
 * indicating if the event should be executed only once, a set of referenced
 * events, a parent process, the triggering time and a listener to handle the
 * execution of referenced events.
 * </p>
 * 
 * <p>
 * Each implementing class should provide the code for cloning the event
 * ensuring that its internal state is consistent. In order to keep the
 * simulation memory area as small as possible, each event is created by cloning
 * the original event obtained from the simulation configuration parser. Be sure
 * to re-initialize the event members that you do not want to be cloned.
 * </p>
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (amoretti@ce.unipr.it)
 * 
 */
public abstract class Event extends SimulationObject implements
		Comparable<Event>, Cloneable {
	protected String id = null;
	protected Properties params = null;
	protected boolean isOneShot = false;
	protected ArrayList<Event> referencedEvents = null;
	protected float triggeringTime = 0;
	protected Process parentProcess = null;
	private SchedulerListener schedulerListener = null;

	/**
	 * Class constructor that builds the event with its minimal set of
	 * properties. Each implementing class should call the super constructor and
	 * immediately after invoke the initialize method to check that the event
	 * parameters are correct.
	 * 
	 * @param id
	 *            the identifier of the event as specified in the configuration
	 *            file.
	 * @param params
	 *            the properties that will be handled by the event.
	 * @param parentProcess
	 *            the parent process of the event.
	 */
	public Event(String id, Properties params, Process parentProcess) {
		this.id = id;
		this.params = params;
		this.parentProcess = parentProcess;
		this.referencedEvents = new ArrayList<Event>();
	}

	/**
	 * Returns the event parameters.
	 * 
	 * @return the event parameters.
	 */
	public Properties getParams() {
		return params;
	}

	/**
	 * Clone the event.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	/**
	 * Create an instance of the current event by cloning it and updating the
	 * triggering time.
	 * 
	 * @param triggeringTime
	 *            the triggering time of the newly created event instance.
	 * @return the newly created event instance.
	 */
	public Event createInstance(float triggeringTime) {
		Event clone = (Event) clone();
		if (schedulerListener != null)
			try {
				clone.schedulerListener = schedulerListener.getClass()
						.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		clone.triggeringTime = triggeringTime;
		return clone;
	}

	/**
	 * Provides the initialization of the event according to the given
	 * parameters. This method should also perform a check on the parameters
	 * values.
	 * 
	 * @throws InvalidParamsException
	 *             if the parameters passed to the event are wrong.
	 */
	public abstract void initialize() throws InvalidParamsException;

	/**
	 * Performs the standard Object.equals comparison by using the event id as
	 * the criteria.
	 */
	public boolean equals(Object o) {
		return id.equals(((Event) o).getId());
	}

	/**
	 * Returns the event id.
	 * 
	 * @return the event id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the list of referenced events of the current event.
	 * 
	 * @return the list of referenced events of the current event.
	 */
	public ArrayList<Event> getReferencedEvents() {
		return referencedEvents;
	}

	/**
	 * Returns the event triggering time.
	 * 
	 * @return the event triggering time.
	 */
	public float getTriggeringTime() {
		return triggeringTime;
	}

	/**
	 * Implementing class should provide in this method the code used for the
	 * event execution.
	 * 
	 * @throws RunException
	 *             if the event execution fails.
	 */
	public abstract void run() throws RunException;

	/**
	 * Add to the simulation event queue all the referenced events of the
	 * current event, by scheduling them according to their parent process
	 * strategy. Each time an event is scheduled, the scheduling listener is
	 * invoked so that the event itself is notified of that operation.
	 */
	public void scheduleReferencedEvents() {
		float nextTriggeringTime = triggeringTime;

		for (Iterator<Event> it = referencedEvents.iterator(); it.hasNext();) {
			Event event = (Event) it.next();
			if (event.getParentProcess() == null)
				continue;
			nextTriggeringTime = event.getParentProcess()
					.getNextTriggeringTime(nextTriggeringTime);
			Event eventToSchedule = event.createInstance(nextTriggeringTime);
			schedulerListener.newEventScheduled(this, eventToSchedule);
			Engine.getDefault().insertIntoEventsList(eventToSchedule);
		}
	}

	/**
	 * Standard implementation of the compareTo method that uses the triggering
	 * time as sorting criteria.
	 */
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

	/**
	 * Set the parent process of the event.
	 * 
	 * @param parentProcess
	 *            the parent process of the event.
	 */
	public void setParentProcess(Process parentProcess) {
		this.parentProcess = parentProcess;
	}

	/**
	 * Returns the parent process of the event.
	 * 
	 * @return the parent process of the event.
	 */
	public Process getParentProcess() {
		return parentProcess;
	}

	/**
	 * Sets the scheduler listener for the event's referenced events.
	 * 
	 * @param l
	 *            the scheduler listener.
	 */
	public void setSchedulerListener(SchedulerListener l) {
		schedulerListener = l;
	}

	/**
	 * Sets if the event is one shot. If <code>true</code> the event will be
	 * executed only once, if <code>false</code> the event will be scheduled
	 * immediately after its execution using a triggering time determined by its
	 * parent process strategy.
	 * 
	 * @param isOneShot
	 *            whether the node is one shot.
	 */
	public void setOneShot(boolean isOneShot) {
		this.isOneShot = isOneShot;
	}

	/**
	 * Returns <code>true</code> if the node is one shot (will be scheduled only
	 * once), <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the node is one shot (will be scheduled only
	 *         once), <code>false</code> otherwise.
	 */
	public boolean isOneShot() {
		return isOneShot;
	}
}