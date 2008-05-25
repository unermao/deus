package it.unipr.ce.dsg.deus.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

public abstract class Node extends SimulationObject implements Comparable<Node>, Cloneable {
	protected String id = null;

	protected ArrayList<Node> neighbors = null;

	protected boolean isReachable = false;
	
	protected Properties params = null;

	public Node(String id, Properties params) throws InvalidParamsException {
		this.id = id;
		this.neighbors = new ArrayList<Node>();
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
	
	public void addNeighbor(Node newNeighbor) {
		// check if newNeighbor is already in the neighbors list
		boolean isAlreadyNeighbor = false;
		for (Iterator<Node> it = neighbors.iterator(); it.hasNext(); ) 
			if (((Node)it.next()).id.equals(newNeighbor.id))
				isAlreadyNeighbor = true;
		if (!isAlreadyNeighbor) {	
			neighbors.add(newNeighbor);
			Collections.sort(neighbors); // sort by node id
		}
	}

	public void removeNeighbor(Node neighbor) {
		// We can't use the remove function of the arraylist because it will
		// destroy the object (sets it to null) so we basically need to copy the
		// whole arraylist into a new one avoiding the node to be removed
		ArrayList<Node> newNeighbors = new ArrayList<Node>();
		for (Iterator<Node> it = neighbors.iterator(); it.hasNext();) {
			Node n = it.next();
			if (!n.equals(neighbor))
				newNeighbors.add(n);
		}
		neighbors = newNeighbors;
	}

	public ArrayList<Node> getNeighbors() {
		return neighbors;
	}

	public void resetNeighbors() {
		neighbors = new ArrayList<Node>();
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
			clone.neighbors = new ArrayList<Node>();
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