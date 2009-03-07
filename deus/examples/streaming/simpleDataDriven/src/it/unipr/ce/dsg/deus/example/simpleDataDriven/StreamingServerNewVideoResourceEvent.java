package it.unipr.ce.dsg.deus.example.simpleDataDriven;
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
		
		
		VideoChunk newResource = null;
		
		//Creo la nuova risorsa video
	    if(serverNode.getVideoResource().size() == 0)
	    	newResource = new VideoChunk(0,serverNode.getChunkSize());
	    else 
	    	newResource = new VideoChunk(serverNode.getLastChunk().getChunkIndex()+1,serverNode.getChunkSize());
		
	    //Imposto nel chunk le informazioni sul sorgente
	    newResource.setSourceNode(serverNode);
	    newResource.setOriginalTime(this.triggeringTime);
	    
    	//Aggiungo la nuova porzione video al Server
	    serverNode.addNewVideoResource(newResource);
		
		float time = 0;
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		for(int index = 0 ; index < serverNode.getServedPeers().size(); index++){	
			
			//if(!serverNode.getServedPeers().get(index).getNeededChunk().contains(newResource.getChunkIndex()))
			if( newResource.getChunkIndex() >  serverNode.getServedPeers().get(index).getInitChunk())	
				serverNode.sendVideoChunk(serverNode.getServedPeers().get(index), newResource, this.triggeringTime);
		}
			
		getLogger().fine("end new video resource ##");
	}
	
}
