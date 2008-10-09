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
				
				
				for(int j = 0 ; j < peer.getVideoResource().size(); j++)
					listaRisorse = listaRisorse + " " + peer.getVideoResource().get(j); 
				
				if(peer.getId().equals("pcNode"))
					getLogger().info("Nodo ("+ peer.getId() +"):       " + listaRisorse);
				if(peer.getId().equals("mobileNode"))
					getLogger().info("Nodo ("+ peer.getId() +"):   " + listaRisorse);
				if(peer.getId().equals("mobile3GNode"))
					getLogger().info("Nodo ("+ peer.getId() +"): " + listaRisorse);
				
			}
		}
		
		getLogger().info("VT = " + Engine.getDefault().getVirtualTime());
		getLogger().info("Nodi Totali = " + Engine.getDefault().getNodes().size());
		getLogger().info("###########################");
		getLogger().info("\n");
	}

}
