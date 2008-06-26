package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.p2p.node.Peer;


public class ResourceAdv {

	private Peer owner = null;
	private Peer interestedNode = null;
	private String name = null;
	private int amount = 0;
	private boolean found = false;

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public Peer getInterestedNode() {
		return interestedNode;
	}

	public void setInterestedNode(Peer interestedNode) {
		this.interestedNode = interestedNode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Peer getOwner() {
		return owner;
	}

	public void setOwner(Peer owner) {
		this.owner = owner;
	}
	
}
