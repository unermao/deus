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
	private CoolStreamingVideoChunk videoChunk = null;
	
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
		clone.videoChunk = this.videoChunk;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new node video resource");
	
		CoolStreamingPeer associatedStreamingNode = (CoolStreamingPeer) associatedNode;
		
		//System.out.println("New Peer Video Resource Event" + " (" + associatedStreamingNode.getId() + ")" +" : " + associatedStreamingNode.getServedPeers().size() + " (" + Engine.getDefault().getNodes().size() + " )");
		
		//Aggiungo la nuova porzione video al nodo
		associatedStreamingNode.addNewVideoResource(videoChunk);
		
		
	
		float time = 0;
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		for(int index = 0 ; index < associatedStreamingNode.getServedPeers().size(); index++)
		{		

		        time = triggeringTime + nextChunkArrivalTime(associatedStreamingNode.getUploadSpeed(),associatedStreamingNode.getServedPeers().get(index).getDownloadSpeed(),videoChunk);
			
				CoolStreamingPeerNewVideoResourceEvent newPeerResEvent = (CoolStreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(CoolStreamingPeerNewVideoResourceEvent.class,time);
				newPeerResEvent.setOneShot(true);
				newPeerResEvent.setAssociatedNode(associatedStreamingNode.getServedPeers().get(index));
				newPeerResEvent.setResourceValue(videoChunk);
				Engine.getDefault().insertIntoEventsList(newPeerResEvent);
		}
		
		
		getLogger().fine("end new node video resource ##");
	}
	
	/**
	 * Determina  il tempo in cui dovra' essere schedulato il nuovo arrivo di un chunk al destinatario
	 * in base alla velocità di Upload del fornitore e quella di Downalod del cliente.
	 * @param providerUploadSpeed
	 * @param clientDownloadSpeed
	 * @return
	 */
	private float nextChunkArrivalTime(double providerUploadSpeed, double clientDownloadSpeed, CoolStreamingVideoChunk chunk) {
		
		double time = 0.0;
		double minSpeed = Math.min(providerUploadSpeed, clientDownloadSpeed);
		double chunkMbitSize = (double)( (double) chunk.getChunkSize() / 1024.0 );
		
		time = (chunkMbitSize / minSpeed)*1000.0;
		
		System.out.println("New Chunk Time :" + time);
		
		return (float)time;
	}

	public CoolStreamingVideoChunk getResourceValue() {
		return videoChunk;
	}

	public void setResourceValue(CoolStreamingVideoChunk newResource) {
		this.videoChunk = newResource;
	}

}
