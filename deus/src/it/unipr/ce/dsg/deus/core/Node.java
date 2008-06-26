package it.unipr.ce.dsg.deus.core;

import java.util.ArrayList;
import java.util.Properties;

public abstract class Node extends SimulationObject implements Comparable<Node>, Cloneable {
	protected String id = null;

	protected ArrayList<Resource> resources = null;

	protected boolean isReachable = false;
	
	protected Properties params = null;

	// TODO generic node should not have neighbors
	public Node(String id, Properties params, ArrayList<Resource> resources) throws InvalidParamsException {
		this.id = id;
		this.resources = resources;
		this.params = params;
	}

	public abstract void initialize() throws InvalidParamsException;

	public boolean equals(Object o) {
		return id.equals(((Node) o).getId());
	}

	public int compareTo(Node n) {
		int result = 0;
		if (this.id.compareTo(n.id) < 0)
			result = -1;
		else if (this.id.equals(n.id))
			result = 0;
		else if (this.id.compareTo(n.id) > 0)
			result = 1;
		return result;
	}
	



	
	public ArrayList<Resource> getResources() {
		return resources;
	}
	
	public String getId() {
		return id;
	}

	public Node createInstance(String id) {
		Node clone = (Node) clone();
		clone.id = id;
		return clone;
	}

	public Object clone() {
		try {
			Node clone = (Node) super.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	public boolean isReachable() {
		return isReachable;
	}

	public void setReachable(boolean isReachable) {
		this.isReachable = isReachable;
	}

}
