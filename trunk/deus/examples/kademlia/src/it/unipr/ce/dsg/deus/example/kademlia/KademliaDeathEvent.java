package it.unipr.ce.dsg.deus.example.kademlia;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

/**
 * This event represents the death of a simulation node. During the execution of
 * the event the specified node will be killed or, in case nothing is specified,
 * a random node will be killed.
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class KademliaDeathEvent extends Event {

	private Node nodeToKill = null;

	public KademliaDeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public Object clone() {
		KademliaDeathEvent clone = (KademliaDeathEvent) super.clone();
		clone.nodeToKill = null;
		return clone;
	}

	public void setNodeToKill(Node nodeToKill) {
		this.nodeToKill = nodeToKill;
	}

	@Override
	public void run() throws RunException {
		if (nodeToKill == null) {
			if (Engine.getDefault().getNodes().size() > 0)
				Engine.getDefault().getNodes().remove(
						Engine.getDefault().getSimulationRandom().nextInt(
								Engine.getDefault().getNodes().size()));
		} else {
			int n = Engine.getDefault().getNodes().indexOf(nodeToKill);
			if (n > -1)
				Engine.getDefault().getNodes().remove(n);
		}

	}

}
