package it.unipr.ce.dsg.deus.core;

import it.unipr.ce.dsg.deus.util.LogEntryFormatter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * This class represents the simulation engine of DEUS. After the configuration
 * file is parsed, the obtained configured simulation objects (Nodes, Events and
 * Processes) are passed to the Engine that will properly initialize the queue
 * of events to be run.
 * </p>
 * 
 * <p>
 * The simulation is a standard discrete event simulation where each event has
 * an associated triggering time, used as a sorting criteria. The events
 * inserted into the simulation queue are processed singularly one after each
 * other, each time updating the current simulation virtual time.
 * </p>
 * 
 * <p>
 * The run method of the engine will process each event in the events queue
 * until a maximum virtual time is reached or the queue is empty. In each cycle
 * the first event of the queue is removed (the one with the lowest triggering
 * time), the virtual time of the simulation is updated and the event is
 * executed. If the event has some referenced events, those will be scheduled
 * right after the event execution, in the same order used to define them in the
 * configuration file. If the event is not oneshot and it has a parent process,
 * than it will be scheduled for a next execution with a triggering time
 * calculated according to the parent process strategy.
 * </p>
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public final class Engine extends SimulationObject {
	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

	public static final Level DEFAULT_LOGGER_LEVEL = Level.INFO;

	public static final String DEFAULT_LOGGER_PATH_PREFIX = "log";

	private ArrayList<Node> configNodes = null;

	private ArrayList<Event> configEvents = null;

	private ArrayList<Process> configProcesses = null;

	private ArrayList<Process> referencedProcesses = null;

	private float maxVirtualTime = 0;

	private float virtualTime = 0;

	private LinkedList<Event> eventsList = null;

	private Random simulationRandom = null;

	private Random uuidRandom = null;

	private static Engine engine = null;

	private ArrayList<Node> nodes = null;

	/**
	 * Class constructor that initializes the simulation engine according to the
	 * parameters extracted from the configuration file.
	 * 
	 * @param maxVirtualTime
	 *            the maximum virtual time of the simulation.
	 * @param seed
	 *            the seed used as the only source of randomness in all the
	 *            simulation.
	 * @param configNodes
	 *            the list of Node classes defined in the configuration file.
	 * @param configEvents
	 *            the list of Event classes defined in the configuration file.
	 * @param configProcesses
	 *            the list of Process classes defined in the configuration file.
	 * @param referencedProcesses
	 *            the list of Process classes associated to the simulation's
	 *            main cycle.
	 * 
	 * @see it.unipr.ce.dsg.deus.core.AutomatorParser
	 */
	public Engine(float maxVirtualTime, int seed, ArrayList<Node> configNodes,
			ArrayList<Event> configEvents, ArrayList<Process> configProcesses,
			ArrayList<Process> referencedProcesses) {
		engine = this;
		this.maxVirtualTime = maxVirtualTime;
		simulationRandom = new Random(seed);
		uuidRandom = new Random(seed);
		this.configNodes = configNodes;
		this.configEvents = configEvents;
		this.configProcesses = configProcesses;
		this.referencedProcesses = referencedProcesses;
		eventsList = new LinkedList<Event>();
		nodes = new ArrayList<Node>();
		parseReferencedProcesses();
	}

	/**
	 * Insert into the events queue all the events in each process associated to
	 * the simulation's main cycle.
	 */
	private void parseReferencedProcesses() {
		for (Iterator<Process> it = referencedProcesses.iterator(); it
				.hasNext();) {
			Process p = it.next();

			for (Iterator<Event> it2 = p.getReferencedEvents().iterator(); it2
					.hasNext();) {
				Event e = it2.next();
				insertIntoEventsList(e.createInstance(p
						.getNextTriggeringTime(virtualTime)));

			}
		}
	}

	/**
	 * Insert an event into the simulation events queue. This method must be
	 * called all the time an event should be inserted into a queue, since it
	 * provides the automatically sorting of the queue by using the events'
	 * triggering time as criteria.
	 * 
	 * @param e
	 *            the event to insert into the queue.
	 */
	public void insertIntoEventsList(Event e) {
		eventsList.add(e);
		Collections.sort(eventsList);
	}

	/**
	 * Create a new instance of the event with the given class extracted from
	 * the list of events defined into the simulation configuration. The event
	 * triggering time will be set according to the given parameter.
	 * 
	 * @param eventClass
	 *            the class of the event whose instance should be created.
	 * @param triggeringTime
	 *            the triggering time of the event.
	 * @return a new instance of the event with the given class, obtained
	 *         through cloning.
	 */
	public Event createEvent(Class<?> eventClass, float triggeringTime) {
		for (Iterator<Event> it = configEvents.iterator(); it.hasNext();) {
			Event e = it.next();
			if (e.getClass().equals(eventClass))
				return e.createInstance(triggeringTime);
		}
		return null;
	}

	/**
	 * Create a new instance of the event with the given id extracted from the
	 * list of events defined into the simulation configuration. The event
	 * triggering time will be set according to the given parameter.
	 * 
	 * @param eventId
	 *            the id of the event whose instance should be created.
	 * @param triggeringTime
	 *            the triggering time of the event.
	 * @return a new instance of the event with the given id, obtained through
	 *         cloning.
	 */
	public Event createEvent(String eventId, float triggeringTime) {
		for (Iterator<Event> it = configEvents.iterator(); it.hasNext();) {
			Event e = it.next();
			if (e.getId().equals(eventId))
				return e.createInstance(triggeringTime);
		}
		return null;
	}

	/**
	 * Returns the current simulation virtual time.
	 * 
	 * @return the current simulation virtual time.
	 */
	public float getVirtualTime() {
		return virtualTime;
	}

	/**
	 * Runs the simulation.
	 * 
	 * @throws SimulationException
	 *             if the event fails during its execution.
	 */
	public void run() throws SimulationException {
		getLogger().info(
				"Starting simulation with maxVirtualTime = " + maxVirtualTime);

		while (virtualTime < maxVirtualTime && eventsList.size() > 0) {
			getLogger().fine(
					"virtualTime=" + virtualTime + " numOfQueueEvents="
							+ eventsList.size());
			Event e = eventsList.removeFirst();
			virtualTime = e.getTriggeringTime();
			if (virtualTime <= maxVirtualTime) {
				try {
					e.run();
					e.scheduleReferencedEvents();
					if (e.getParentProcess() != null && !e.isOneShot())
						insertIntoEventsList(e.createInstance(e
								.getParentProcess().getNextTriggeringTime(
										virtualTime)));
				} catch (RunException ex) {
					getLogger().severe(ex.getMessage());
					throw new SimulationException(ex.getMessage());
				}
			}
		}

		getLogger().info(
				"Simulation ended at virtualTime = " + virtualTime
						+ " (maxVirtualTime=" + maxVirtualTime
						+ ") with numOfQueueEvents=" + eventsList.size());
	}

	/**
	 * Returns the current instance of the simulation engine class.
	 * 
	 * @return the current instance of the simulation engine class.
	 */
	public static Engine getDefault() {
		return engine;
	}

	/**
	 * Returns the source of randomness used in the simulation.
	 * 
	 * @return the source of randomness used in the simulation.
	 */
	public Random getSimulationRandom() {
		return simulationRandom;
	}

	/**
	 * Generate a random Universally Unique Identifier (UUID) using the MD5
	 * hash.
	 * 
	 * @return a random UUID based on MD5 hash.
	 */
	public String generateUUID() {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(Double.toHexString(uuidRandom.nextDouble())
					.getBytes());
			return bytesToHex(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Generate a random Universally Unique Identifier (UUID)using the given
	 * hashing algorithm.
	 * 
	 * @param algorithm
	 *            the hashing algorithm to be used in order to produce the UUID.
	 * @return a random UUID based on the given hashing algorithm.
	 * @throws NoSuchAlgorithmException
	 *             if the given algorithm is not supported by Java Security.
	 */
	public String generateUUID(String algorithm)
			throws NoSuchAlgorithmException {
		MessageDigest digest = java.security.MessageDigest
				.getInstance(algorithm);
		digest.update(Double.toHexString(uuidRandom.nextDouble()).getBytes());
		return bytesToHex(digest.digest());
	}

	/**
	 * Convert a byte array into an hex string.
	 * 
	 * @param hash
	 *            the byte array to convert.
	 * @return the hex string representation of the given byte array.
	 */
	private String bytesToHex(byte hash[]) {
		char buf[] = new char[hash.length * 2];
		for (int i = 0, x = 0; i < hash.length; i++) {
			buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
			buf[x++] = HEX_CHARS[hash[i] & 0xf];
		}
		return new String(buf);
	}

	/**
	 * Returns the list of all instantiated simulation nodes. This should not be
	 * mixed-up with the list of nodes defined into the simulation configuration
	 * file; this list contains the nodes effectively instantiated by starting
	 * from the configuration file.
	 * 
	 * @return the list of instantiated simulation nodes.
	 */
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	/**
	 * Initialize a java file logger that will store log messages, with the
	 * given logging level, into the given logger path prefix and using as log
	 * file name the class name of the given object.
	 * 
	 * @param o
	 *            the object whose class name will be used to create the log
	 *            file name.
	 * @param loggerPathPrefix
	 *            the base path in which log file will be stored.
	 * @param loggerLevel
	 *            the logging level used for filtering.
	 * @return the instance of the java file logger.
	 */
	public Logger getLogger(Object o, String loggerPathPrefix, Level loggerLevel) {
		Logger l = Logger.getLogger(o.getClass().getCanonicalName());
		l.setLevel(loggerLevel);
		if (l.getHandlers().length == 0) {
			FileHandler fh;
			try {
				fh = new FileHandler(loggerPathPrefix + "/"
						+ o.getClass().getCanonicalName() + ".log");
				fh.setFormatter(new LogEntryFormatter());
				l.addHandler(fh);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return l;
	}

	/**
	 * Return the list of Node classes defined in the configuration file.
	 * 
	 * @return the list of Node classes defined in the configuration file.
	 */
	public ArrayList<Node> getConfigNodes() {
		return configNodes;
	}

	/**
	 * Return the list of Event classes defined in the configuration file.
	 * 
	 * @return the list of Event classes defined in the configuration file.
	 */
	public ArrayList<Event> getConfigEvents() {
		return configEvents;
	}

	/**
	 * Return the list of Process classes defined in the configuration file.
	 * 
	 * @return the list of Process classes defined in the configuration file.
	 */
	public ArrayList<Process> getConfigProcesses() {
		return configProcesses;
	}

}
