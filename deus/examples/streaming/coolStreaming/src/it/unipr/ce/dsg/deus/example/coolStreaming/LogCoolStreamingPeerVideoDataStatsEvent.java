package it.unipr.ce.dsg.deus.example.coolStreaming;

import java.util.Iterator;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * <p>
 * This Event logs a number of statistics related to the current "snapshot" of
 * the StreamingPeer network:
 * <ol>
 * <li>mean value and variance of the chromosomes</li>
 * <li>initial and mean Streaming value</li>
 * <li>number of searchers (i.e. peers with at least 1 query sent)</li>
 * <li>average QHR (query hit ratio)</li>
 * </ol>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * 
 */
public class LogCoolStreamingPeerVideoDataStatsEvent extends Event {

	public LogCoolStreamingPeerVideoDataStatsEvent(String id, Properties params,
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
			CoolStreamingPeer peer = (CoolStreamingPeer) Engine.getDefault().getNodes().get(index);
			
			if(peer.isConnected())
			{
				String listaRisorse = "";
				
				//getLogger().info("Connessioni attive("+ peer.getId() + "): " + peer.getActiveConnection() + "/" + peer.getMaxAcceptedConnection());
				
				if(peer.isInit_bool() == false)
						peer.init();
				
				//for(int i=0;i<peer.getK_value();i++)
				//for(int j = 0 ; j < peer.getK_buffer().get(i).size(); j++)
					//listaRisorse = listaRisorse + " " + peer.getK_buffer().get(i).get(j).getChunkIndex(); 
				
				
				for(int j = 0 ; j < peer.getPlayer().size(); j++)
					listaRisorse = listaRisorse + " " + peer.getPlayer().get(j).getChunkIndex();
				
				String sourceId = "";
				String sourceKey = "";
				
//				if(peer.getServerNode() != null){
//					
//					sourceId = peer.getServerNode().getId();	
//					sourceKey = peer.getServerNode().getKey();
//				}	
//				else
//					if(peer.getSourceStreamingNode() != null)
//					{
//						sourceId = peer.getSourceStreamingNode().getId();
//						sourceKey = peer.getSourceStreamingNode().getKey();
//					}
				for(int i =0; i<peer.getK_value();i++)
				if(peer.getServerByServer().get(i) != null){
					
					sourceId = sourceId + " | " + peer.getServerByServer().get(i).getId();	
					sourceKey = sourceKey + " | " +peer.getServerByServer().get(i).getKey();
				}	
				
				for(int i =0; i<peer.getK_value();i++)
					if(peer.getServerByPeer().get(i) != null)
					{
						sourceId = sourceId + " | " + peer.getServerByPeer().get(i).getId();
						sourceKey = sourceKey + " | " +peer.getServerByPeer().get(i).getKey();
						//sourceId = peer.getSourceStreamingNode().getId();
						//sourceKey = peer.getSourceStreamingNode().getKey();
					}

				
				if(peer.getId().equals("pcNode"))
					getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + " - " + peer.getNodeDepth() + " -> " + sourceId+"("+ sourceKey +")"+"):       " + listaRisorse);				
				else
					getLogger().info("Nodo ("+ peer.getId() + " - " + peer.getKey() + " - " + peer.getNodeDepth() + " -> " + sourceId+"("+ sourceKey +")"+"):       " + listaRisorse);				
				
			}
		}
		
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime());
		getLogger().info("Nodi Totali = " + Engine.getDefault().getNodes().size());
		getLogger().info("###########################");
		getLogger().info("\n");
	}

}
