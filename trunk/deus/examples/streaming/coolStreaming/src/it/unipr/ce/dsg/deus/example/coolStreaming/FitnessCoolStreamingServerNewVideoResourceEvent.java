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
public class FitnessCoolStreamingServerNewVideoResourceEvent extends NodeEvent {

	private static final String MEAN_ARRIVAL_TRIGGERED_DISCOVERY = "meanArrivalTriggeredDiscovery";
	private float meanArrivalTriggeredDiscovery = 0;
	
	public FitnessCoolStreamingServerNewVideoResourceEvent(String id, Properties params,
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
		
		FitnessCoolStreamingServerNewVideoResourceEvent clone = (FitnessCoolStreamingServerNewVideoResourceEvent) super.clone();
	
		return clone;
	}

	public void run() throws RunException {
		
getLogger().fine("## new video resource");	
		
FitnessCoolStreamingServerPeer serverNode = (FitnessCoolStreamingServerPeer)Engine.getDefault().getNodes().get(0);
		
		
FitnessCoolStreamingVideoChunk newResource = null;
		
		//Creo la nuova risorsa video
	    if(serverNode.getVideoResource().size() == 0)
	    	newResource = new FitnessCoolStreamingVideoChunk(0,serverNode.getChunkSize());
	    else 
	    	newResource = new FitnessCoolStreamingVideoChunk(serverNode.getLastChunk().getChunkIndex()+1,serverNode.getChunkSize());
		
	    //Imposto nel chunk le informazioni sul sorgente
	    newResource.setSourceNode(serverNode);
	    newResource.setOriginalTime(this.triggeringTime);
	    
    	//Aggiungo la nuova porzione video al Server
	    serverNode.addNewVideoResource(newResource);
		
		float time = 0;

		//Invia solo a quelli relativi al k-esimo buffer trovato grazie all'indice della risorsa
	
		int i = serverNode.calculate_buffer_index(newResource);
		
		//serverNode.init();
		
		for(int index = 0 ; index < serverNode.getServedPeers2().get(i).size(); index++)
		{
			
			FitnessCoolStreamingVideoChunk newResource2 = new FitnessCoolStreamingVideoChunk(newResource.getChunkIndex(),newResource.getChunkSize());
			
			//Imposto nel chunk le informazioni sul sorgente
		    newResource2.setSourceNode(serverNode);
		    newResource2.setOriginalTime(this.triggeringTime);
			
			newResource2.setDestNode(serverNode.getServedPeers2().get(i).get(index));
			
			//serverNode.getServedPeers2().get(i).get(index).getRequestChunkBuffer().get(i).add(newResource2);
			
			//TODO AGGIUNGERE CHE è STATO RICHIESTO
			
//			if(serverNode.getServedPeers2().get(i).get(index).getKey() == 531129312)
//				System.out.println("INVIO " + newResource2.getChunkIndex());
			
			
			//TODO TOGLIERE
			if(newResource2.getChunkIndex() > serverNode.getServedPeers2().get(i).get(index).getInitChunk())
			serverNode.getSendBuffer().get(i).add(newResource2);
			//System.out.println("Sono " + serverNode.getKey() + " Invio " + newResource2.getChunkIndex() + " a " + serverNode.getServedPeers2().get(i).get(index).getKey());
		//	if(serverNode.getServedPeers2().get(i).get(index).getKey() == 1602282472 )
			//	 System.out.println("Sono " + serverNode.getKey() + " Invio " + newResource.getChunkIndex() + " a " + serverNode.getServedPeers2().get(i).get(index).getKey());
			//serverNode.sendVideoChunk(serverNode.getServedPeers2().get(i).get(index), newResource, this.triggeringTime);
		}
		
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
//		for(int index = 0 ; index < serverNode.getServedPeers().size(); index++){	
//			
//			//if(!serverNode.getServedPeers().get(index).getNeededChunk().contains(newResource.getChunkIndex()))
//				serverNode.sendVideoChunk(serverNode.getServedPeers().get(index), newResource, this.triggeringTime);
//		}
//			
		getLogger().fine("end new video resource ##");
	}

}
