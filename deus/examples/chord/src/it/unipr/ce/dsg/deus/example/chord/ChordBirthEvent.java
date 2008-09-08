package it.unipr.ce.dsg.deus.example.chord;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * This event represents the birth of a simulation node. During its execution an
 * instance of the node associated to the event will be created.
 * 
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordBirthEvent extends NodeEvent {
	
	public ChordBirthEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		// TODO implement params to establish the policy of node type initialization
		// (now choose one type only - randomly - from the parent process)
	}

	public void run() throws RunException {
//		 System.out.println("birth -- N = " +
//		 Engine.getDefault().getNodes().size());
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
		Collections.sort(Engine.getDefault().getNodes());
//		for(int i = 0; i<Engine.getDefault().getNodes().size(); i++)
//		System.out.println("nodo " + i + " " + Engine.getDefault().getNodes().get(i).getId());
		
	}


}