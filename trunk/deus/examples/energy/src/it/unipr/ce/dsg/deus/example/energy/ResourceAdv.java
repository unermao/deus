package it.unipr.ce.dsg.deus.example.energy;

import it.unipr.ce.dsg.deus.p2p.node.Peer;

/**
 * <p>
 * This class represents a resource advertisement, i.e. a document that
 * describes a ConsumableResource (with a name and an amount), and the
 * interested Peer.
 * </p>
 * <p>
 * Once the resource described by a ResourceAdv has been discovered,
 * the owner of the resource should be registered into the ResourceAdv,
 * and the found flag set to true. This flag is checked, for example, 
 * at the beginning of each RevolDiscoveryEvent in order to avoid propagation
 * of discovery events associated to an already discovered and consumed resource.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
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
