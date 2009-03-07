package it.unipr.ce.dsg.deus.example.simpleDataDriven;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Properties;


/**
 * 
 * @author Picone Marco
 * 
 */
public class StreamingConnectionEvent extends NodeEvent {
	
	public StreamingConnectionEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		
		System.out.println("Connection Event !");
		
		initialize();
		
		
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		
		StreamingConnectionEvent clone = (StreamingConnectionEvent) super.clone();
		
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## Connection Event of: " +  associatedNode.getId() +" (" + associatedNode.getKey()  + ")");
	
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
		associatedStreamingNode.setConnected(true);
		
		//Se ci sono almeno un numero di peer superiore al massimo 
		if( Engine.getDefault().getNodes().size() - 1 > associatedStreamingNode.getMaxPartnersNumber())
		{
			getLogger().fine("Ci sono : " + Engine.getDefault().getNodes().size() +" elementi");
		
			int index = 0;
			StreamingPeer peer = null;
			
			int size = (Engine.getDefault().getNodes().size() - 1 );
			
			index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
			peer = (StreamingPeer)Engine.getDefault().getNodes().get(index);
			
			//Associo Casualmente i Vicini fino al massimo consentito!
			while( associatedStreamingNode.getNeighbors().size() < associatedStreamingNode.getMaxPartnersNumber() )
			{

				if(		peer.isConnected() 
						&& !associatedStreamingNode.getNeighbors().contains((Peer)peer) 
						&& !associatedStreamingNode.equals(Engine.getDefault().getNodes().get(index))
				  )
				{	
					associatedStreamingNode.addNeighbor((Peer)peer);
					getLogger().fine("Nodo:" + peer.getKey() + " (" + peer.isConnected() + ")");
				}
				
				index = Engine.getDefault().getSimulationRandom().nextInt(size) + 1;
				peer = (StreamingPeer)Engine.getDefault().getNodes().get(index);
			}
			
				
		}
		else //Se i peer presenti sono meno del massimo consentito li inserisco tutti come vicini
		{
			for(int i = 1; i<Engine.getDefault().getNodes().size(); i++)
			{
					
				StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(i);
				
				if(!associatedStreamingNode.equals(Engine.getDefault().getNodes().get(i)) && peer.isConnected()){
					getLogger().fine("Nodo:" + Engine.getDefault().getNodes().get(i).getKey() + " (" + peer.isConnected() + ")");
					associatedStreamingNode.addNeighbor((Peer) Engine.getDefault().getNodes().get(i));
				}
				
			}
		}
		
		getLogger().fine("end Connection ##");
	}

}
