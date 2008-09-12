package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class ChordStabilizeEvent extends NodeEvent {

	public ChordStabilizeEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		
		ChordPeer currentNode = (ChordPeer) getAssociatedNode();
		//System.out.println("prova " + currentNode);
		//ystem.out.println("prova " + currentNode.getFingerTable()[0]);
		ChordPeer successorNode = currentNode.getFingerTable()[0];
		
		if (successorNode == null)
			return;

		if (successorNode.getPredecessor() != null) {
			ChordPeer predecessorNode = successorNode.getPredecessor();
			if (predecessorNode.getId().compareTo(currentNode.getId()) > 0
					&& predecessorNode.getId().compareTo(successorNode.getId()) < 0)
				successorNode = predecessorNode;
		}

		ChordPeer notifyResult = successorNode.notify(currentNode);
		getLogger().fine(
				"Current: " + currentNode + "\tSuccessor: " + successorNode
						+ "\t Successor Predecessor after Notify: "
						+ notifyResult);
	}

}
