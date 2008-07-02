package it.unipr.ce.dsg.deus.p2p.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Properties;

public class MultipleRandomConnectionsEvent extends Event {
	private static final String IS_BIDIRECTIONAL = "isBidirectional";
	private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
	
	private boolean isBidirectional = false;
	private int numInitialConnections = 0;
	private Peer initiator = null;
	
	public MultipleRandomConnectionsEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {
		if (params.containsKey(IS_BIDIRECTIONAL))
			isBidirectional = Boolean.parseBoolean(params.getProperty(IS_BIDIRECTIONAL)); 
		if (params.containsKey(NUM_INITIAL_CONNECTIONS))
			numInitialConnections = Integer.parseInt(params.getProperty(NUM_INITIAL_CONNECTIONS));
	}

	public void setNodeToConnect(Peer initiator) {
		this.initiator = initiator;
	}

	public Object clone() {
		MultipleRandomConnectionsEvent clone = (MultipleRandomConnectionsEvent) super.clone();
		clone.initiator = null;
		return clone;
	}
 
	public void run() throws RunException {
		int n = Engine.getDefault().getNodes().size();
		//System.out.println("N = " + n);
		if (n == 1)
			return;
		//System.out.println("initiator: " + initiator);
		int m = 0;
		if (n <= numInitialConnections)
			m = Engine.getDefault().getNodes().size() - 1;
		else
			m = numInitialConnections;
		//System.out.println("m = " + m);
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
			} while ((target == null) || (target.getId().equals(initiator.getId())));
			//System.out.println("target: " + target);
			if (initiator.addNeighbor(target)) {
				if (isBidirectional)
					target.addNeighbor(initiator);
			}
			//System.out.println("num neighbors: " + initiator.getNeighbors().size());
		} while (initiator.getNeighbors().size() < m);
	}

}
