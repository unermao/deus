package it.unipr.ce.dsg.deus.example.geokad;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Properties;
import java.util.Random;


/**
 * <p>
 * This NodeEvent disconnects the associatedNode (which must be a Peer) 
 * from a specified target Peer (if target is null, the associatedNode 
 * is disconnected from all its neighbors). 
 * </p>
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class GeoKadDisconnectionEvent extends NodeEvent {    

	private Peer target = null;

	public GeoKadDisconnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {

	}

	public void setNodeToDisconnectFrom(Peer target) {
		this.target = target;
	}
	
	public Object clone() {
		GeoKadDisconnectionEvent clone = (GeoKadDisconnectionEvent) super.clone();
		clone.target = null;
		return clone;
	}

	public void run() throws RunException {
		if (associatedNode == null) {
			Random random = new Random();

			int initialized_nodes = Engine.getDefault().getNodes().size();
			int random_node = random.nextInt(initialized_nodes);
			associatedNode = (GeoKadPeer) Engine.getDefault().getNodes().get(random_node);
		}

		if (!(associatedNode instanceof GeoKadPeer))
			throw new RunException("The associated node is not a KademliaPeer!");
		if (target != null)
			((Peer) associatedNode).removeNeighbor(this.target);
		else { // disconnect from all neighbors
			((Peer) associatedNode).resetNeighbors();
		}
	}

}
