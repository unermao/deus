package it.unipr.ce.dsg.deus.p2p.node;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

public class Peer extends Node  {

	protected ArrayList<Peer> neighbors = null;
	
	public Peer(String id, Properties params, ArrayList<Resource> resources)
			throws InvalidParamsException {
		super(id, params, resources);
		this.neighbors = new ArrayList<Peer>();
		initialize();
	}

	
	public void initialize() throws InvalidParamsException {

	}
	
	public Object clone() {
		Peer clone = (Peer) super.clone();
		clone.neighbors = new ArrayList<Peer>();
		return clone;
	}

	public boolean addNeighbor(Peer newNeighbor) {
		// check if newNeighbor is already in the neighbors list
		boolean isAlreadyNeighbor = false;
		for (Iterator<Peer> it = neighbors.iterator(); it.hasNext(); ) 
			if (((Peer)it.next()).id.equals(newNeighbor.id))
				isAlreadyNeighbor = true;
		if (!isAlreadyNeighbor) {	
			neighbors.add(newNeighbor);
			Collections.sort(neighbors); // sort by node id
			return true;
		}
		else
			return false;
	}
	
	public void removeNeighbor(Peer neighbor) {
		// We can't use the remove function of the arraylist because it will
		// destroy the object (sets it to null) so we basically need to copy the
		// whole arraylist into a new one avoiding the node to be removed
		ArrayList<Peer> newNeighbors = new ArrayList<Peer>();
		for (Iterator<Peer> it = neighbors.iterator(); it.hasNext();) {
			Peer n = it.next();
			if (!n.equals(neighbor))
				newNeighbors.add(n);
		}
		neighbors = newNeighbors;
	}

	public ArrayList<Peer> getNeighbors() {
		return neighbors;
	}

	public void resetNeighbors() {
		neighbors = new ArrayList<Peer>();
	}
}
