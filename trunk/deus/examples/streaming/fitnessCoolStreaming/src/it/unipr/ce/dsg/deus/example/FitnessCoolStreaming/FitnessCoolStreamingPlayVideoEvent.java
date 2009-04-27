package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;

public class FitnessCoolStreamingPlayVideoEvent extends NodeEvent {

	
	public FitnessCoolStreamingPlayVideoEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("StreamingUpdateParentsEvent");
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		FitnessCoolStreamingPlayVideoEvent clone = (FitnessCoolStreamingPlayVideoEvent) super.clone();
	
		
		return clone;
	}

	public void run() throws RunException {

		getLogger().fine("## New play Video Event ! ");
	
		//Aggiorno le liste di tutti i nodi presenti
		for(int i = 1; i < Engine.getDefault().getNodes().size(); i++){
			
			FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)Engine.getDefault().getNodes().get(i);
			
			if(peer.isConnected())
			{
				//Simulo la riproduzione del video da parte del peer
				peer.playVideoBufferCoolStreaming();		
			}
		}
			
			
		getLogger().fine("end New play Video Event ##");
	}
}
