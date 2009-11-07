package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

/**
 * 
 * @author Picone Marco
 * 
 */
public class StreamingPlayVideoEvent extends NodeEvent {
	
	public StreamingPlayVideoEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		
		StreamingPlayVideoEvent clone = (StreamingPlayVideoEvent) super.clone();
		
		return clone;
	}

	public void run() throws RunException {
		
		//getLogger().fine("## new play video event");
		System.out.println("Time: " + Engine.getDefault().getVirtualTime() );
		
		//Aggiorno le liste di tutti i nodi presenti
		for(int i = 1; i < Engine.getDefault().getNodes().size(); i++){
			
			StreamingPeer peer = (StreamingPeer)Engine.getDefault().getNodes().get(i);
			
			if(peer.isConnected())
			{
				//Simulo la riproduzione del video da parte del peer
				peer.playVideoBuffer();
			}
		}	
			
		//getLogger().fine("new play video event ##");
	}

}