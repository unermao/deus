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
public class StreamingUpdateVideoBufferEvent extends NodeEvent {

	private int maxPartnersNumber = 20;
	
	public StreamingUpdateVideoBufferEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("StreamingUpdateVideoBufferEvent");
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		StreamingUpdateVideoBufferEvent clone = (StreamingUpdateVideoBufferEvent) super.clone();
	
		clone.maxPartnersNumber = this.maxPartnersNumber;
		
		return clone;
	}

	public void run() throws RunException {

		//getLogger().fine("## Update Video Buffer Event ! ");
	
		//Aggiorno le liste di tutti i nodi presenti
		for(int i = 1; i < Engine.getDefault().getNodes().size(); i++){
			
			StreamingPeer peer = (StreamingPeer)Engine.getDefault().getNodes().get(i);
			
			if(peer.isConnected())
				peer.updateVideoBufferList(this.triggeringTime);
		}
			
			
		//getLogger().fine("end Update Video Buffer Event ##");
	}

	public int getMaxPartnersNumber() {
		return maxPartnersNumber;
	}

	public void setMaxPartnersNumber(int maxPartnersNumber) {
		this.maxPartnersNumber = maxPartnersNumber;
	}

}
