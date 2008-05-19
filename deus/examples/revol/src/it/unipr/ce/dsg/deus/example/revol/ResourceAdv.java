package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Node;


public class ResourceAdv {

	private Node owner = null;
	private Node interestedNode = null;
	private String name = null;
	private int amount = 0;
	private boolean found = false;

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public Node getInterestedNode() {
		return interestedNode;
	}

	public void setInterestedNode(Node interestedNode) {
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

	public Node getOwner() {
		return owner;
	}

	public void setOwner(Node owner) {
		this.owner = owner;
	}
	
}
