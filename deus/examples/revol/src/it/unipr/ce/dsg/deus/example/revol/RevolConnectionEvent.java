package it.unipr.ce.dsg.deus.example.revol;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class RevolConnectionEvent extends Event {
	private static final String IS_BIDIRECTIONAL = "isBidirectional";
	
	private boolean isBidirectional = false;
	private RevolNode initiator = null;
	private RevolNode target = null;
	
	public RevolConnectionEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {
		if (params.containsKey(IS_BIDIRECTIONAL))
			isBidirectional = Boolean.parseBoolean(params.getProperty(IS_BIDIRECTIONAL)); 
	}

	public void setNodesToConnect(RevolNode initiator, RevolNode target) {
		this.initiator = initiator;
		this.target = target;
	}

	public Object clone() {
		RevolConnectionEvent clone = (RevolConnectionEvent) super.clone();
		clone.initiator = null;
		clone.target = null;
		return clone;
	}

	@Override
	public void run() throws RunException {
		if (initiator.getNeighbors().size() < initiator.getKMax()) {
			if (target == null) {
				if (Engine.getDefault().getNodes().size() > 1) {
					//System.out.println("target is null and nodes are " + Engine.getDefault().getNodes().size());			
					do {
						int randomInt = Engine.getDefault().getSimulationRandom().nextInt(
								Engine.getDefault().getNodes().size());
						Node randomNode = (RevolNode) Engine.getDefault().getNodes().get(randomInt);
						// qui sotto dovrei fare un controllo: non è detto che tutti i nodi siano di tipo RevolNode
						if (randomNode instanceof RevolNode)
							target = (RevolNode) randomNode; 
					} while ((target == null) || (target.getId().equals(initiator.getId())));
				}
				else
					return;
			}
			initiator.addNeighbor(target);
			initiator.setReachable(true);
			if (isBidirectional)
				target.addNeighbor(initiator);
		}
	}

}
