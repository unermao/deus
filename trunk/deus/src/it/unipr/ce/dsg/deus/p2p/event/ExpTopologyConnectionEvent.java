package it.unipr.ce.dsg.deus.p2p.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Properties;

/**
 * <p>
 * This NodeEvent connects the associatedNode (which must be a Peer) 
 * to "1..numInitialConnections" other Peers, randomly chosen.
 * If this event is executed by the Engine after the initiator Peer
 * has been the target of connections from other Peers, new connections 
 * are created only if the initiator has less than "numInitialConnections". 
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */

public class ExpTopologyConnectionEvent extends NodeEvent {
	private static final String IS_BIDIRECTIONAL = "isBidirectional";
	private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
	
	private boolean isBidirectional = false;
	private int numInitialConnections = 0;
	
	public ExpTopologyConnectionEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		if (params.containsKey(IS_BIDIRECTIONAL))
			isBidirectional = Boolean.parseBoolean(params.getProperty(IS_BIDIRECTIONAL)); 
		if (params.containsKey(NUM_INITIAL_CONNECTIONS))
			numInitialConnections = Integer.parseInt(params.getProperty(NUM_INITIAL_CONNECTIONS));
	}

	public Object clone() {
		ExpTopologyConnectionEvent clone = (ExpTopologyConnectionEvent) super.clone();
		return clone;
	}
 
	public void run() throws RunException {
		if (!(associatedNode instanceof Peer))
			throw new RunException("The associated node is not a Peer!");
		//System.out.println("numInitialConnections = " + numInitialConnections);
		numInitialConnections = Engine.getDefault().getSimulationRandom().nextInt(numInitialConnections+1);
		//System.out.println("numInitialConnections = " + numInitialConnections);
		int n = Engine.getDefault().getNodes().size();
		if (n == 1)
			return;
		int m = 0;
		if (n <= numInitialConnections)
			m = Engine.getDefault().getNodes().size() - 1;
		else
			m = numInitialConnections;
		do {
			Peer target = null;			
			do {			
				int randomInt = Engine.getDefault().getSimulationRandom().nextInt(n);
				Node randomNode = Engine.getDefault().getNodes().get(randomInt);
				if (!(randomNode instanceof Peer)) {
					target = null;					
					continue;
				}
				target = (Peer) randomNode; 
			} while ((target == null) || (target.getKey() == ((Peer) associatedNode).getKey()));
			if (((Peer) associatedNode).addNeighbor(target)) {
				if (isBidirectional)
					target.addNeighbor(((Peer) associatedNode));
			}
		} while (((Peer) associatedNode).getNeighbors().size() < m);
	}

	public int getNumInitialConnections() {
		return numInitialConnections;
	}

	public void setNumInitialConnections(int numInitialConnections) {
		this.numInitialConnections = numInitialConnections;
	}

}
