package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;
import java.util.Random;

/**
 * This event represents the death of a simulation node. During the execution of
 * the event the specified node will be killed or, in case nothing is specified,
 * a random node will be killed.
 * 
 * @author Matteo Agosti (agosti@ce.unipr.it)
 * @author Marco Muro (marco.muro@studenti.unipr.it)
 * 
 */
public class ChordDeathEvent extends Event {

	private Node nodeToKill = null;

	public ChordDeathEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	public Object clone() {
		ChordDeathEvent clone = (ChordDeathEvent) super.clone();
		clone.nodeToKill = null;
		return clone;
	}

	public void setNodeToKill(Node nodeToKill) {
		this.nodeToKill = nodeToKill;
	}

	@Override
	public void run() throws RunException {
		
		ChordPeer disconnectedNode = (ChordPeer) nodeToKill;
		if (disconnectedNode == null)
			{
				Random random = new Random();
				int initialized_nodes = Engine.getDefault().getNodes().size();
				int random_node = random.nextInt(initialized_nodes);
				disconnectedNode = (ChordPeer) Engine.getDefault().getNodes().get(random_node);	
			}
		disconnectedNode.deathChordPeer();
	}

	

}