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
public class StreamingServerNewVideoResourceEvent extends NodeEvent {

	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	
	public StreamingServerNewVideoResourceEvent(String id, Properties params,
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
		
		StreamingServerNewVideoResourceEvent clone = (StreamingServerNewVideoResourceEvent) super.clone();
	
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new video resource");	
		
		ServerPeer serverNode = (ServerPeer)Engine.getDefault().getNodes().get(0);
		
		//System.out.println("New SERVER Video Resource Event" + " (" + serverNode.getId() + ")" +" : " + serverNode.getServedPeers().size() + " (" + Engine.getDefault().getNodes().size() + " )");
		
		
		int resourceValue = 1;
		
		//Aggiungo la nuova porzione video al Server
		if(serverNode.getVideoResource().size() != 0)
			resourceValue = serverNode.getVideoResource().get(serverNode.getVideoResource().size() -1) + 1;
		
		serverNode.addNewVideoResource(resourceValue);
		
		/*
		float time = triggeringTime + expRandom(meanArrivalTriggeredDiscovery)
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		for(int index = 0 ; index < serverNode.getServedPeers().size(); index++)
		{			
				StreamingPeerNewVideoResourceEvent newPeerResEvent = (StreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(StreamingPeerNewVideoResourceEvent.class,triggeringTime + expRandom(meanArrivalTriggeredDiscovery));
				newPeerResEvent.setOneShot(true);
				newPeerResEvent.setAssociatedNode(serverNode.getServedPeers().get(index));
				newPeerResEvent.setResourceValue(resourceValue);
				Engine.getDefault().insertIntoEventsList(newPeerResEvent);
		}
		*/
		getLogger().fine("end new video resource ##");
	}
	
	private float expRandom(float meanValue) {
		float myRandom = (float) (-Math.log(Engine.getDefault()
				.getSimulationRandom().nextFloat()) * meanValue);
		return myRandom;
	}

}
