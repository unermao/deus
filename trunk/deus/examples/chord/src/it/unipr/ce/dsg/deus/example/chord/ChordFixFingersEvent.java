package it.unipr.ce.dsg.deus.example.chord;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class ChordFixFingersEvent extends NodeEvent {

	public ChordFixFingersEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		ChordPeer currentNode = (ChordPeer) getAssociatedNode();
		currentNode.fixFingers();
		try {
			getLogger().fine(
					"Current: "
							+ currentNode.getId()
							+ "\tLast Fixed Finger Entry: "
							+ (currentNode.getLastFixedFinger() - 1)
							+ "\tFinger entry: "
							+ currentNode.getNeighbors().get(
									currentNode.getLastFixedFinger() - 1).getId());
		} catch (Exception e) {
			/*getLogger().fine(
					"Current: "
							+ currentNode.getId()
							+ "\tLast Fixed Finger Entry: "
							+ (currentNode.getLastFixedFinger() - 1)
							+ "\tFinger entry: NULL");*/
		}

	}

}
