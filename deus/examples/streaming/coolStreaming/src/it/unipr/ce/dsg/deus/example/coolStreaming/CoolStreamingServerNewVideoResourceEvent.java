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
public class CoolStreamingServerNewVideoResourceEvent extends NodeEvent {

	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	
	public CoolStreamingServerNewVideoResourceEvent(String id, Properties params,
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
		
		CoolStreamingServerNewVideoResourceEvent clone = (CoolStreamingServerNewVideoResourceEvent) super.clone();
	
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new video resource");	
		
		CoolStreamingServerPeer serverNode = (CoolStreamingServerPeer)Engine.getDefault().getNodes().get(0);
		
		//System.out.println("New SERVER Video Resource Event" + " (" + serverNode.getId() + ")" +" : " + serverNode.getServedPeers().size() + " (" + Engine.getDefault().getNodes().size() + " )");
		
		CoolStreamingVideoChunk newResource = null;
		
		//Creo la nuova risorsa video
	    if(serverNode.getVideoResource().size() == 0)
	    	newResource = new CoolStreamingVideoChunk(0,serverNode.getChunkSize());
	    else 
	    	newResource = new CoolStreamingVideoChunk(serverNode.getLastChunk().getChunkIndex()+1,serverNode.getChunkSize());
		
    	//Aggiungo la nuova porzione video al Server
	    serverNode.addNewVideoResource(newResource);
		
		
		
		float time = 0;
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		for(int index = 0 ; index < serverNode.getServedPeers().size(); index++)
		{		

		        time = triggeringTime + nextChunkArrivalTime(serverNode.getUploadSpeed(),serverNode.getServedPeers().get(index).getDownloadSpeed(),newResource);
			
				CoolStreamingPeerNewVideoResourceEvent newPeerResEvent = (CoolStreamingPeerNewVideoResourceEvent)Engine.getDefault().createEvent(CoolStreamingPeerNewVideoResourceEvent.class,time);
				newPeerResEvent.setOneShot(true);
				newPeerResEvent.setAssociatedNode(serverNode.getServedPeers().get(index));
				newPeerResEvent.setResourceValue(newResource);
				Engine.getDefault().insertIntoEventsList(newPeerResEvent);
		}
		
		getLogger().fine("end new video resource ##");
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
		
		System.out.println("Server New Chunk Time :" + time);
		
		return (float)time;
	}

}
