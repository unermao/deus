package it.unipr.ce.dsg.deus.example.chordStreaming;

import java.util.Properties;
import java.util.Random;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This class is used to run the join methods that provide to connect the new peer
 * to an another existing Peer in the Network e takes from him informations
 * about his position in the Chord Ring
 * </p>
 * 
 * @author  Matteo Agosti (matteo.agosti@unipr.it)
 * @author  Marco Muro (marco.muro@studenti.unipr.it)
 */
public class ChordJoinEvent extends NodeEvent {

	public ChordJoinEvent(String id, Properties params, Process parentProcess)
			throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {		
		ChordPeer connectingNode = (ChordPeer) getAssociatedNode();
		ChordPeer gatewayNode = null;
		ChordResourceType prova = null;

		for (int c = 0; c <connectingNode.chordResources.size(); c++) {
			prova = connectingNode.chordResources.get(c);			
			prova.addOwners(connectingNode);
		}		
	// i'am the first Peer in the Network: I'am server
		if (Engine.getDefault().getNodes().size() <= 1) {
			connectingNode.initFirstFingerTable();
			connectingNode.setPredecessor(connectingNode);
			connectingNode.setConnected(true);
			connectingNode.setServerId(true);
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
		Random rand = new Random();
		int randomVideo = rand.nextInt(3);
		
		if(randomVideo==1)
			{
			connectingNode.setVideoName("KillBill");
			connectingNode.setCountFirstVideo();
			}
		else if(randomVideo==2)
		{
			connectingNode.setVideoName("Matrix");
			connectingNode.setCountSecondVideo();
		}
		else
		{	
			connectingNode.setVideoName("Armaggeddon");
			connectingNode.setCountThirdVideo();
		}
		
		randomVideo = rand.nextInt(3);
		
		if(randomVideo==1)
		{
			connectingNode.setTypePeer(1);
			connectingNode.setCountFastPeer();
		}
		else if(randomVideo==2)
		{
			connectingNode.setTypePeer(2);
			connectingNode.setCountMediumPeer();
		}
		else
		{	
			connectingNode.setTypePeer(3);
			connectingNode.setCountSlowPeer();
		}
		
		getLogger().fine("Current: " + connectingNode.getKey() + "\tGateway: " + gatewayNode.getKey() + "\tSuccessor: " + connectingNode.getSuccessor().getKey() + "\tPredecessor; " + connectingNode.getPredecessor().getKey());
	}

}
