package it.unipr.ce.dsg.deus.example.life;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class LifeCreationEvent extends NodeEvent {

	public LifeCreationEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public void run() throws RunException {
		Node n = null;
		
		if(Engine.getDefault().getNodes().size() != 0) {
			n = (Node) getParentProcess().getReferencedNodes().get(
					Engine.getDefault().getSimulationRandom().nextInt(
							getParentProcess().getReferencedNodes().size()))
					.createInstance(Engine.getDefault().getNodes().get(
							Engine.getDefault().getNodes().size()-1).getKey() + 1);
		} else {
			n = (Node) getParentProcess().getReferencedNodes().get(
					Engine.getDefault().getSimulationRandom().nextInt(
							getParentProcess().getReferencedNodes().size()))
					.createInstance(0);
		}
		Engine.getDefault().getNodes().add(n);
		this.setAssociatedNode(n);
		
		LifeRegion r = (LifeRegion) n;
		r.connect();	
	}

}
