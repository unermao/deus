package it.unipr.ce.dsg.deus.example.geokad;

import java.util.ArrayList;
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
		connectingNode.setConnected(true);
		
		//GeoKadPeer destinationNode = null;
		
		GeoKadBootStrapPeer bootStrap = null;
		bootStrap = (GeoKadBootStrapPeer)Engine.getDefault().getNodeByKey(GeoKadBootStrapPeer.BOOTSTRAP_KEY);
		bootStrap.addIncomingNode(connectingNode);
		
		ArrayList<GeoKadPeerInfo> peerInfoList = bootStrap.getInitialPeerList(connectingNode);
		
		//System.out.println("Boot List: " + peerInfoList.size());
		
		if(peerInfoList.size() > 0)
			for(int index=0; index<peerInfoList.size();index++)
				connectingNode.insertPeer((GeoKadPeer)Engine.getDefault().getNodeByKey(peerInfoList.get(index).getKey()));
				
		
		/*
		// If this is the very first node in the network
		if (Engine.getDefault().getNodes().size() <= 1) {			
			
			System.out.println("BOOTSTRAP NODE KEY: " + connectingNode);
			
			connectingNode.setConnected(true);
			return;
		}
		else
		{
			//Add node to the bootstrap list
			bootStrap = ((GeoKadBootStrapPeer)Engine.getDefault().getNodes().get(0));
			bootStrap.insertPeer(connectingNode);
			bootStrap.getInitialPeerList(connectingNode);
			//bootStrap.insertPeer(connectingNode);
		}
		*/
		/*
		// Find a Random connected node
		do {
			destinationNode = (GeoKadPeer) Engine.getDefault().getNodes()
					.get(
							Engine.getDefault().getSimulationRandom().nextInt(
									Engine.getDefault().getNodes().size()));
		} while (destinationNode.equals(connectingNode)
				|| !destinationNode.isConnected());
		
		connectingNode.insertPeer(destinationNode);
		*/
		
		/*
		//Receive a list of available peers
		if(Engine.getDefault().getNodes().size() <= 20)
		{
			
			for(int i=0; i < Engine.getDefault().getNodes().size(); i++)
			{
				GeoKadPeer peer = (GeoKadPeer) Engine.getDefault().getNodes().get(i);
				connectingNode.insertPeer(peer);
			}
		}
		else
		{
			
			
			ArrayList<GeoKadPeer> appList = bootStrap.find_node(connectingNode);
			
//			if(connectingNode.getKey() == 9322)
//				System.out.println("BootStrap List Size: " + appList.size());
//			
			
			//System.out.println("BootStrap List Size: " + appList.size());
			for(int k=0; k < appList.size(); k++)
			{
				GeoKadPeer peerFromBoot = appList.get(k);
				
				ArrayList<GeoKadPeer> appList2 = peerFromBoot.find_node(connectingNode);
				
//				if(connectingNode.getKey() == 9322)
//					System.out.println("App List Size: " + appList2.size());
//				
				for(int z=0; z < appList2.size(); z++)
					if(appList2.get(z).getKey() != connectingNode.getKey())
						connectingNode.insertPeer(appList2.get(z));
			}
		}
		*/	
		
		//First Discovery
		try
		{
			if(Engine.getDefault().getSimulationRandom().nextBoolean() == true)
			{
				GeoKadNodeLookUpEvent fn = (GeoKadNodeLookUpEvent) new GeoKadNodeLookUpEvent("find_node", new Properties(), null).createInstance(triggeringTime + 1);
				fn.setOneShot(true);
				fn.setAssociatedNode(connectingNode);
				Engine.getDefault().insertIntoEventsList(fn);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
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
		*/
		//First Move
		//System.out.println("FIRT Move: " + connectingNode);
		connectingNode.move(triggeringTime);
	}
}
