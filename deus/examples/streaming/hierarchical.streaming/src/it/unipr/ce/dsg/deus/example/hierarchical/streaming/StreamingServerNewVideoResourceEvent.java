package it.unipr.ce.dsg.deus.example.HierarchicalStreaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

import java.util.ArrayList;
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
				meanArrivalTriggeredDiscovery  = Float.parseFloat(params.getProperty(MEAN_ARRIVAL_TRIGGERED_DISCOVERY));
			} catch (NumberFormatException ex) {
				throw new InvalidParamsException(
						MEAN_ARRIVAL_TRIGGERED_DISCOVERY + " must be a valid float value.");
			}
		}
		
	}
	
	public Object clone() {
		
		StreamingServerNewVideoResourceEvent clone = (StreamingServerNewVideoResourceEvent) super.clone();
		return clone;
	}

	
	//TODO: modificato, ora creo un nuovo videoChunk con tutti i layer
	public void run() throws RunException {
		
			
		
		ServerPeer serverNode = (ServerPeer)Engine.getDefault().getNodes().get(0);
		
		VideoChunk newResource = null;
		
		//mi calcolo l'indice del chunk che sto aggiungendo
		int chunkIndex = 0;
		
	    if(serverNode.getVideoResource().size() == 0)
	    	chunkIndex = 0;
	    else 
	    	chunkIndex = serverNode.getLastChunk().getChunkIndex()+1;
		
		ArrayList<ChunkLayer> listOfLayers = new ArrayList<ChunkLayer>();  
		
		
		getLogger().fine("Server: New video chunk: " + chunkIndex);
		//getLogger().fine("Server: connessioni: " + serverNode.getActiveConnection() + " " + Engine.getDefault().getVirtualTime());
		
		//mi creo i vari layer necessari per il nuovo videochunk
		//TODO> divide i livelli correttamente
		for (int i = 0; i<serverNode.getMaxNumberOfLayer() ; i++){
			//TODO: Qui divido per 4 poi da fare!
			ChunkLayer layer = new ChunkLayer(i, serverNode.getChunkSize()/serverNode.getMaxNumberOfLayer(),chunkIndex);
			layer.setSourceNode(serverNode);
			layer.setOriginalTime(this.triggeringTime);
			listOfLayers.add(layer);
		}
		
	    newResource = new VideoChunk(chunkIndex,serverNode.getChunkSize());
		
	    //Imposto nel chunk le informazioni sul sorgente
	    newResource.setSourceNode(serverNode);
	    newResource.setOriginalTime(this.triggeringTime);
	    
	    //imposto la suddivisione in livelli
	    newResource.setLayers(listOfLayers);
	    
    	//Aggiungo la nuova porzione video al Server
	    
	    //TODO: andare a vedere cosa fa
	    serverNode.addNewVideoResource(newResource);
		
		float time = 0;
		
		//Innesca per i nodi forniti l'evento di aggiornamento risorsa
		
		
		//TODO: TEORICAMENTE A POSTO CONTROLLARE INITCHUNK
		for(int index = 0 ; index < serverNode.getServedPeers().size(); index++){	
			
			//if(!serverNode.getServedPeers().get(index).getNeededChunk().contains(newResource.getChunkIndex()))
			
			if( newResource.getChunkIndex() >  serverNode.getServedPeers().get(index).getPeer().getInitChunk()){	
				
				serverNode.sendChunkLayer(serverNode.getServedPeers().get(index).getPeer(), newResource.extractLayer(serverNode.getServedPeers().get(index).getLayer()), this.triggeringTime);
			}
		}
			
	}	
	
	
}
