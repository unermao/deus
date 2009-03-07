package it.unipr.ce.dsg.deus.example.simpleDataDriven;

import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * @author Picone Marco
 * 
 */
public class LogStreamingPeerVideoDataStatsEvent extends Event {

	public LogStreamingPeerVideoDataStatsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}

	public void run() throws RunException {

		getLogger().info("###########################");
		getLogger().info("Node Video Data Stats:");
		
		//Stampo la lista delle risorse di ogni nodo
		for(int index = 1; index < Engine.getDefault().getNodes().size(); index++ ){
			StreamingPeer peer = (StreamingPeer) Engine.getDefault().getNodes().get(index);
			
			if(peer.isConnected())
			{
				String listaRisorse = "";
				
				for(int j = 0 ; j < peer.getVideoResource().size(); j++)
					listaRisorse = listaRisorse + " " + peer.getVideoResource().get(j).getChunkIndex(); 
				
				String sourceId = "";
				int sourceKey = -1;
				
				if(peer.getServerNode() != null){
					
					sourceId = peer.getServerNode().getId();	
					sourceKey = peer.getServerNode().getKey();
				}	
				else
					if(peer.getSourceStreamingNode() != null)
					{
						sourceId = peer.getSourceStreamingNode().getId();
						sourceKey = peer.getSourceStreamingNode().getKey();
					}
				
				if(peer.getId().equals("pcNode"))
					getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + " - " + peer.getNodeDepth() +  " -> " + sourceId+"("+ sourceKey +")"+"):       " + listaRisorse);
				if(peer.getId().equals("mobileNode"))
					getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + " - " + peer.getNodeDepth() +  " -> " + sourceId+"("+ sourceKey +")"+"):   " + listaRisorse);
				if(peer.getId().equals("mobile3GNode"))
					getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + " - " + peer.getNodeDepth() +  " -> " + sourceId+"("+ sourceKey +")"+"): " + listaRisorse);
				
			}
		}
		
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime());
		getLogger().info("Nodi Totali = " + Engine.getDefault().getNodes().size());
		getLogger().info("###########################");
		getLogger().info("\n");
	}

}
