package it.unipr.ce.dsg.deus.example.hierarchical.streaming;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
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
				
				for (int i = 0; i<peer.getNumberOfLayer();i++){
					if(peer.getServerNode().get(i) != null){

						sourceId = peer.getServerNode().get(i).getId();	
						sourceKey = peer.getServerNode().get(i).getKey();
					}	
					else
						if(peer.getSourceStreamingNode().get(i) != null)
						{
							sourceId = peer.getSourceStreamingNode().get(i).getId();
							sourceKey = peer.getSourceStreamingNode().get(i).getKey();
						}

					if(peer.getId().equals("pcNode"))
						getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + /*" - " + peer.getNodeDepth() +*/  " -> " + sourceId+"("+ sourceKey +")"+"):       " + listaRisorse);
					if(peer.getId().equals("mobileNode"))
						getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + /*" - " + peer.getNodeDepth() +*/  " -> " + sourceId+"("+ sourceKey +")"+"):   " + listaRisorse);
					if(peer.getId().equals("mobile3GNode"))
						getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + /*" - " + peer.getNodeDepth() +*/ " -> " + sourceId+"("+ sourceKey +")"+"): " + listaRisorse);

				}
				
			}
		}
		
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime());
		getLogger().info("Nodi Totali = " + Engine.getDefault().getNodes().size());
		getLogger().info("###########################");
		getLogger().info("\n");
	}

}
