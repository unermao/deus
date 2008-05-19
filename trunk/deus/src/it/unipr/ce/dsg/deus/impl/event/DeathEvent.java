package it.unipr.ce.dsg.deus.impl.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class DeathEvent extends Event {

	private Node nodeToKill = null;

	public DeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	@Override
	public void initialize() throws InvalidParamsException {

	}

	public Object clone() {
		DeathEvent clone = (DeathEvent) super.clone();
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
				Engine.getDefault().getNodes().remove(Engine.getDefault().getSimulationRandom().nextInt(
						Engine.getDefault().getNodes().size()));
		} else {
			int n = 
				Engine.getDefault().getNodes().indexOf(nodeToKill);
			if (n > -1)
				Engine.getDefault().getNodes().remove(n);
		}

	}

}
