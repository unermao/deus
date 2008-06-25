package it.unipr.ce.dsg.deus.impl.event;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class BirthEvent extends NodeEvent {

	public BirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	// TODO implement params to establish the policy of node type initialization 
	// (now choose one type only - randomly - from the parent process)
	public void initialize() throws InvalidParamsException {
	}

	public void run() throws RunException {
		//System.out.println("birth -- N = " + Engine.getDefault().getNodes().size());
		if (getParentProcess() == null)
			throw new RunException(
					"A parent process must be set in order to run "
							+ getClass().getCanonicalName());
		// create a node (the type is randomly chosen among those which are
		// associated to the process)
		Node n = (Node) getParentProcess().getReferencedNodes().get(
				Engine.getDefault().getSimulationRandom().nextInt(
						getParentProcess().getReferencedNodes().size()))
				.createInstance(Engine.getDefault().generateUUID());
		Engine.getDefault().getNodes().add(n);
		associatedNode = n;
	}

}
