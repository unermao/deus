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

	public void parseReferencedProcesses() {
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

	public void insertIntoEventsList(Event e) {
		eventsList.add(e);
		Collections.sort(eventsList);
	}

	public ArrayList<Node> getConfigNodes() {
		return configNodes;
	}

	public ArrayList<Event> getConfigEvents() {
		return configEvents;
	}

	public Event createEvent(Class<?> eventClass, float triggeringTime) {
		for (Iterator<Event> it = configEvents.iterator(); it.hasNext(); ) {
			Event e = it.next();
			if (e.getClass().equals(eventClass))
				return e.createInstance(triggeringTime);
		}
		return null;
	}
	
	public Event createEvent(String eventId, float triggeringTime) {
		for (Iterator<Event> it = configEvents.iterator(); it.hasNext(); ) {
			Event e = it.next();
			if (e.getId().equals(eventId))
				return e.createInstance(triggeringTime);
		}
		return null;
	}
	
	public ArrayList<Process> getConfigProcesses() {
		return configProcesses;
	}

	public float getVirtualTime() {
		return virtualTime;
	}

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
				}
			}
		}

		getLogger().info(
				"Simulation ended at virtualTime = " + virtualTime
						+ " (maxVirtualTime=" + maxVirtualTime
						+ ") with numOfQueueEvents=" + eventsList.size());
	}

	public static Engine getDefault() {
		return engine;
	}

	public Random getSimulationRandom() {
		return simulationRandom;
	}

	public String generateUUID() {
		// TODO Verificare che non sia gi stato generato?
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

	private String bytesToHex(byte hash[]) {
		char buf[] = new char[hash.length * 2];
		for (int i = 0, x = 0; i < hash.length; i++) {
			buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
			buf[x++] = HEX_CHARS[hash[i] & 0xf];
		}
		return new String(buf);
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

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
}
