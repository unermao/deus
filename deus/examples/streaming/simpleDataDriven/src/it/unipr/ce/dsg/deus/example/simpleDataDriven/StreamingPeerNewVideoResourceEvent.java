package it.unipr.ce.dsg.deus.example.simpleDataDriven;
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
public class StreamingPeerNewVideoResourceEvent extends NodeEvent {

	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	private VideoChunk videoChunk = null;
	
	public StreamingPeerNewVideoResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
		
		if (params.containsKey(MEAN_ARRIVAL_TRIGGERED_DISCOVERY)) {
			try {
				meanArrivalTriggeredDiscovery  = Float.parseFloat(params
						.getProperty(MEAN_ARRIVAL_TRIGGERED_DISCOVERY));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(
						MEAN_ARRIVAL_TRIGGERED_DISCOVERY
								+ " must be a valid float value.");
			}
		}
	}
	
	public Object clone() {
		
		StreamingPeerNewVideoResourceEvent clone = (StreamingPeerNewVideoResourceEvent) super.clone();
		clone.videoChunk = this.videoChunk;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new node video resource");
	
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
	
		//Aggiungo la nuova porzione video al nodo
		associatedStreamingNode.addNewVideoResource(videoChunk);
		
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		for(int index = 0 ; index < associatedStreamingNode.getServedPeers().size(); index++)
			   associatedStreamingNode.sendVideoChunk(associatedStreamingNode.getServedPeers().get(index), videoChunk, this.triggeringTime);
			
		getLogger().fine("end new node video resource ##");
	}
	
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	public VideoChunk getResourceValue() {
		return videoChunk;
	}

	public void setResourceValue(VideoChunk resourceValue) {
		this.videoChunk = resourceValue;
	}

}
