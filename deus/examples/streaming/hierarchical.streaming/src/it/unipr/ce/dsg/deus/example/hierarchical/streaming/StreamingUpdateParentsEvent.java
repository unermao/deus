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
public class StreamingUpdateParentsEvent extends NodeEvent {

	private int maxPartnersNumber = 20;
	
	public StreamingUpdateParentsEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
		
		System.out.println("StreamingUpdateParentsEvent");
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		StreamingUpdateParentsEvent clone = (StreamingUpdateParentsEvent) super.clone();
	
		clone.maxPartnersNumber = this.maxPartnersNumber;
		
		return clone;
	}

	public void run() throws RunException {

		//getLogger().fine("## Update Parents List Event ! ");
		
		//Aggiorno le liste di tutti i nodi presenti
		for(int i = 1; i < Engine.getDefault().getNodes().size(); i++){
			
			StreamingPeer peer = (StreamingPeer)Engine.getDefault().getNodes().get(i);
			
			if(peer.isConnected())
			{
				for (int layer=0; layer<peer.getNumberOfLayer();layer++){
				  peer.updateParentsList(this.triggeringTime,layer);
				}
			}
		}	
			
		//getLogger().fine("end Update Parents List Event ##");
	}

	public int getMaxPartnersNumber() {
		return maxPartnersNumber;
	}

	public void setMaxPartnersNumber(int maxPartnersNumber) {
		this.maxPartnersNumber = maxPartnersNumber;
	}

}
