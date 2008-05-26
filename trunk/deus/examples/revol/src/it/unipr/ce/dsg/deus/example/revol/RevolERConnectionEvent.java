package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class RevolERConnectionEvent extends Event {
	private static final String IS_BIDIRECTIONAL = "isBidirectional";
	private static final String NUM_INITIAL_CONNECTIONS = "numInitialConnections";
	
	private boolean isBidirectional = false;
	private int numInitialConnections = 0;
	private RevolNode initiator = null;
	
	public RevolERConnectionEvent(String id, Properties params, Process parentProcess)
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

	public void setNodeToConnect(RevolNode initiator) {
		this.initiator = initiator;
	}

	public Object clone() {
		RevolERConnectionEvent clone = (RevolERConnectionEvent) super.clone();
		clone.initiator = null;
		return clone;
	}
 
	public void run() throws RunException {
		if (Engine.getDefault().getNodes().size() > numInitialConnections) {
			//System.out.println("N = " + Engine.getDefault().getNodes().size());
			//System.out.println("initiator: " + initiator);
			int m = 0;
			if (Engine.getDefault().getNodes().size() < numInitialConnections)
				m = Engine.getDefault().getNodes().size();
			else
				m = numInitialConnections;
			//System.out.println("m = " + m);
			int numConnectedNodes = 0;
			do {
				RevolNode target = null;			
				do {
					int randomInt = Engine.getDefault().getSimulationRandom().nextInt(
					Engine.getDefault().getNodes().size());
					Node randomNode = (RevolNode) Engine.getDefault().getNodes().get(randomInt);
					if (randomNode instanceof RevolNode)
						target = (RevolNode) randomNode; 
					} while ((target == null) || (target.getId().equals(initiator.getId())));
					//System.out.println("target: " + target);
					if (initiator.addNeighbor(target)) {
						initiator.setReachable(true);
					if (isBidirectional) {
						target.addNeighbor(initiator);
						target.setReachable(true);
					}
					numConnectedNodes++;
				}
				//System.out.println("numConnectedNodes: " + numConnectedNodes);
			} while (numConnectedNodes < m);
		}
		else
			return;	
	}

}
