package it.unipr.ce.dsg.deus.core;

import java.util.ArrayList;
import java.util.Properties;

public abstract class Process extends SimulationObject {
	protected String id = null;
	protected Properties params = null;
	protected ArrayList<Node> referencedNodes = null;
	protected ArrayList<Event> referencedEvents = null;
	
	public Process(String id, Properties params,
			ArrayList<Node> referencedNodes, ArrayList<Event> referencedEvents)
			throws InvalidParamsException {
		this.id = id;
		this.params = params;
		this.referencedNodes = referencedNodes;
		this.referencedEvents = referencedEvents;
	}

	public abstract void initialize() throws InvalidParamsException;

	public abstract float getNextTriggeringTime(float virtualTime);

	public boolean equals(Object o) {
		return id.equals(((Process) o).getId());
	}

	public String getId() {
		return id;
	}

	public ArrayList<Node> getReferencedNodes() {
		return referencedNodes;
	}

	public ArrayList<Event> getReferencedEvents() {
		return referencedEvents;
	}
}
