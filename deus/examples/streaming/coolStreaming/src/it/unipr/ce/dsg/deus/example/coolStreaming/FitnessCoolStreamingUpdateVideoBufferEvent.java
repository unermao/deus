package it.unipr.ce.dsg.deus.example.FitnessCoolStreaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.Properties;


/**
 * <p>
 * This event is related to the release of a previously 
 * consumed resource, by updating the corresponding value 
 * on the resource owner.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 *
 */
public class FitnessCoolStreamingUpdateVideoBufferEvent extends NodeEvent {
	
	private int maxPartnersNumber = 20;
	
	public FitnessCoolStreamingUpdateVideoBufferEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("StreamingUpdateParentsEvent");
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		FitnessCoolStreamingUpdateVideoBufferEvent clone = (FitnessCoolStreamingUpdateVideoBufferEvent) super.clone();
	
		clone.maxPartnersNumber = this.maxPartnersNumber;
		
		return clone;
	}

	public void run() throws RunException {

		getLogger().fine("## Update Video Buffer Event ! ");
	
		System.out.println("Tempo : " + Engine.getDefault().getVirtualTime());
		
		//Aggiorno le liste di tutti i nodi presenti
		for(int i = 1; i < Engine.getDefault().getNodes().size(); i++){
			
			FitnessCoolStreamingPeer peer = (FitnessCoolStreamingPeer)Engine.getDefault().getNodes().get(i);
			
			if(peer.isConnected())
				//peer.updateVideoBufferList(this.triggeringTime);
				peer.updateVideoBufferListCoolStreaming(this.triggeringTime);
		}
			
			
		getLogger().fine("end Update Parents List Event ##");
	}

	public int getMaxPartnersNumber() {
		return maxPartnersNumber;
	}

	public void setMaxPartnersNumber(int maxPartnersNumber) {
		this.maxPartnersNumber = maxPartnersNumber;
	}

}
