package it.unipr.ce.dsg.deus.example.chord;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class ChordJoinEvent extends NodeEvent {

	public ChordJoinEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {

	}

	@Override
	public void run() throws RunException {		
		ChordPeer connectingNode = (ChordPeer) getAssociatedNode();
		ChordPeer gatewayNode = null;

		// If there are no nodes in the network the join won't be able to choose
		// the node to which connect
		if (Engine.getDefault().getNodes().size() <= 1) {
			connectingNode.initFirstFingerTable();
			connectingNode.setPredecessor(connectingNode);
			connectingNode.setConnected(true);
			return;
		}
		
		do {
			gatewayNode = (ChordPeer) Engine.getDefault().getNodes().get(
					Engine.getDefault().getSimulationRandom().nextInt(
							Engine.getDefault().getNodes().size()));
		} while (gatewayNode.equals(connectingNode) || !gatewayNode.isConnected());
		
		connectingNode.initFingerTable(gatewayNode);
		connectingNode.updateOthers();
		connectingNode.setConnected(true);
		getLogger().fine("Current: " + connectingNode.getKey() + "\tGateway: " + gatewayNode.getKey() + "\tSuccessor: " + connectingNode.getSuccessor().getKey() + "\tPredecessor; " + connectingNode.getPredecessor().getKey());
	}

}
