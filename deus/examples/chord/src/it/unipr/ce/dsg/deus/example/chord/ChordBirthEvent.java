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
//		 Engine.getDefault().getNodes().size() );
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
		getLogger().fine("id " + associatedNode.getId());
		Collections.sort(Engine.getDefault().getNodes());
		
		int initializedNode =  Engine.getDefault().getNodes().size();
		int associateNode_index = Engine.getDefault().getNodes().indexOf(this.getAssociatedNode());
		
		stabilize(initializedNode,associateNode_index);

		for(int i = 0; i<Engine.getDefault().getNodes().size(); i++)
			getLogger().fine("nodo " + i + " " + Engine.getDefault().getNodes().get(i).getId());
		
	}

	private void stabilize(int initializedNode, int associateNode_index)
	{
		ChordPeer peer = null;
		//se il nodo arrivato  il primo della lista devo inserirlo come successore dell'ultimo nodo della rete
		if (associateNode_index == 0)
		{
			peer = ((ChordPeer) Engine.getDefault().getNodes().get(initializedNode-1));
			peer.setSuccessor(associatedNode.getId());
		}
		else
		{
			//inserisco il nodo arrivato come successore del nodo precedente
			peer = ((ChordPeer) Engine.getDefault().getNodes().get((associateNode_index-1)%initializedNode));
			peer.setSuccessor(associatedNode.getId());
			//lo inserisco all'inizio della fingerTable del nodo precedente
			peer.setFingerTableAtFirst(associatedNode.getId());
		}

		//il nodo arrivato  anche il predecessore del nodo successivo nella rete
		peer = ((ChordPeer) Engine.getDefault().getNodes().get((associateNode_index+1)%initializedNode));
		peer.setPredecessor(associatedNode.getId());
	}

}