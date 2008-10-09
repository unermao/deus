package it.unipr.ce.dsg.deus.example.coolStreaming;
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
public class CoolStreamingPeerNewVideoResourceEvent extends NodeEvent {

	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	private int resourceValue = 0;
	
	public CoolStreamingPeerNewVideoResourceEvent(String id, Properties params,
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
		
		CoolStreamingPeerNewVideoResourceEvent clone = (CoolStreamingPeerNewVideoResourceEvent) super.clone();
		clone.resourceValue = this.resourceValue;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new node video resource");
	
		CoolStreamingPeer associatedStreamingNode = (CoolStreamingPeer) associatedNode;
		
		//System.out.println("New Peer Video Resource Event" + " (" + associatedStreamingNode.getId() + ")" +" : " + associatedStreamingNode.getServedPeers().size() + " (" + Engine.getDefault().getNodes().size() + " )");
		
		//Aggiungo la nuova porzione video al nodo
		associatedStreamingNode.addNewVideoResource(resourceValue);
		
		/*
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		for(int index = 0 ; index < associatedStreamingNode.getServedPeers().size(); index++)
		{
			
			System.out.println("RICEVO : " + resourceValue);
			
			if(!associatedStreamingNode.equals( associatedStreamingNode.getServedPeers().get(index)))
			{
				//System.out.println("Sono: " + associatedStreamingNode.getKey() + " Aggiorno: " + associatedStreamingNode.getServedPeers().get(index).getKey());
			
				StreamingPeerNewVideoResourceEvent newPeerResEvent = (StreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(StreamingPeerNewVideoResourceEvent.class,triggeringTime + expRandom(meanArrivalTriggeredDiscovery));
				newPeerResEvent.setOneShot(true);
				newPeerResEvent.setAssociatedNode(associatedStreamingNode.getServedPeers().get(index));
				Engine.getDefault().insertIntoEventsList(newPeerResEvent);
			}	
		}
		*/
		getLogger().fine("end new node video resource ##");
	}
	
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

	public int getResourceValue() {
		return resourceValue;
	}

	public void setResourceValue(int resourceValue) {
		this.resourceValue = resourceValue;
	}

}
