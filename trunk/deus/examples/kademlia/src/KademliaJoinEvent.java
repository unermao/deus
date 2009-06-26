package it.unipr.ce.dsg.deus.example.kademlia;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class KademliaJoinEvent extends NodeEvent {

	public KademliaJoinEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		// Identify current node
		KademliaPeer connectingNode = (KademliaPeer) this.getAssociatedNode();
		KademliaPeer destinationNode = null;

		for (int i = 0; i < connectingNode.getResourcesNode(); i++) {
			connectingNode.kademliaResources.get(i).setOwner(connectingNode);
		}

		// If this is the very first node in the network
		if (Engine.getDefault().getNodes().size() <= 1) {
			connectingNode.setConnected(true);
			return;
		}

		// Find a Random connected node
		do {
			destinationNode = (KademliaPeer) Engine.getDefault().getNodes()
					.get(
							Engine.getDefault().getSimulationRandom().nextInt(
									Engine.getDefault().getNodes().size()));
		} while (destinationNode.equals(connectingNode)
				|| !destinationNode.isConnected());
		
		connectingNode.insertPeer(destinationNode);
		connectingNode.setConnected(true);
		

		// Populates the connectingNode's kbuckets by searching its own key
		try {
			 KademliaNodeLookUpEvent nlk = (KademliaNodeLookUpEvent)
			 new KademliaNodeLookUpEvent("node_lookup", params, null )
			 .createInstance(triggeringTime);

			nlk.setOneShot(true);
			nlk.setAssociatedNode(connectingNode);
			nlk.setResourceKey(connectingNode.getKey());
			Engine.getDefault().insertIntoEventsList(nlk);
		} catch (InvalidParamsException e1) {
			e1.printStackTrace();
		}
	}
}
