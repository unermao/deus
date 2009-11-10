package it.unipr.ce.dsg.deus.example.geokad;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

public class GeoKadJoinEvent extends NodeEvent {

	public GeoKadJoinEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() {
	}

	public void run() throws RunException {
		
		//System.out.println("JOIN");
		
		// Identify current node
		GeoKadPeer connectingNode = (GeoKadPeer) this.getAssociatedNode();
		GeoKadPeer destinationNode = null;
		
		if (connectingNode == null) {
			// Find a random not-connected node
			do {
				connectingNode = (GeoKadPeer) Engine.getDefault().getNodes()
						.get(
								Engine.getDefault().getSimulationRandom()
										.nextInt(
												Engine.getDefault().getNodes()
														.size()));
			} while (connectingNode.isConnected());
		}

//		for (int i = 0; i < connectingNode.getResourcesNode(); i++) {
//			connectingNode.kademliaResources.get(i).setOwner(connectingNode);
//		}

		// If this is the very first node in the network
		if (Engine.getDefault().getNodes().size() <= 1) {
			connectingNode.setConnected(true);
			return;
		}

		// Find a Random connected node
		do {
			destinationNode = (GeoKadPeer) Engine.getDefault().getNodes()
					.get(
							Engine.getDefault().getSimulationRandom().nextInt(
									Engine.getDefault().getNodes().size()));
		} while (destinationNode.equals(connectingNode)
				|| !destinationNode.isConnected());
		
		connectingNode.insertPeer(destinationNode);
		connectingNode.setConnected(true);
		
		// Populates the connectingNode's kbuckets by searching its own key
		try {
			 GeoKadNodeLookUpEvent nlk = (GeoKadNodeLookUpEvent)
			 new GeoKadNodeLookUpEvent("node_lookup", params, null )
			 .createInstance(triggeringTime);

			nlk.setOneShot(true);
			nlk.setAssociatedNode(connectingNode);
			//nlk.setResourceKey(connectingNode.getKey());
			Engine.getDefault().insertIntoEventsList(nlk);
		} catch (InvalidParamsException e1) {
			e1.printStackTrace();
		}
	}
}
