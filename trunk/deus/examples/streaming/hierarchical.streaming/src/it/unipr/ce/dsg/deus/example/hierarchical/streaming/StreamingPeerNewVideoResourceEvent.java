package it.unipr.ce.dsg.deus.example.hierarchical.streaming;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

import java.util.Properties;


/**
 * @author Picone Marco
 * 
 */
public class StreamingPeerNewVideoResourceEvent extends NodeEvent {

	private ChunkLayer chunkLayer = null;
	
	public StreamingPeerNewVideoResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		
		StreamingPeerNewVideoResourceEvent clone = (StreamingPeerNewVideoResourceEvent) super.clone();
		clone.chunkLayer = this.chunkLayer;
		return clone;
	}

	public void run() throws RunException {
		
		
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
		
		
		
		Peer sourcePeer = (Peer) chunkLayer.getSourceNode();
			
		getLogger().fine("Sono " + associatedNode.getKey() + " ricevuto layer: " + chunkLayer.getLayerIndex() + " Chunk: " + chunkLayer.getChunkIndex());
		
		/**
		 * Se il nodo che mi ha inviato il pacchetto e' ancora connesso
		 * effettuo le operazioni di invio ai miei forniti.
		 * Altrimenti il pacchetto non viene inoltrato dato che si suppone che a causa
		 * della disconnessione della sorgente non sia arrivato.
		 * 
		 */
		if( Engine.getDefault().getNodes().contains(sourcePeer) )
		{
			
			//Aggiungo la nuova porzione video al nodo
			
			ChunkLayer newChunkLayer = new ChunkLayer(chunkLayer.getLayerIndex(),chunkLayer.getLayerSize(),chunkLayer.getChunkIndex());
		
			
			//Imposto il nuovo nodo sorgente sulla porzione video
			newChunkLayer.setSourceNode(associatedNode);
			newChunkLayer.setOriginalTime(triggeringTime);
			
			
			associatedStreamingNode.addNewVideoResource(chunkLayer,this.triggeringTime);
			
			//Innesca per i nodi forniti l'evento di aggiornamento risorsa
			//devi reinviare la risorsa che ho ottenuto a tutti i peer che sto servendo (tenendo conto del layer)
			
			for(int index = 0 ; index < associatedStreamingNode.getServedPeers().size(); index++)
			{
				
				
				if (associatedStreamingNode.getServedPeers().get(index).getLayer()== chunkLayer.getLayerIndex()){
				
					//getLogger().fine("Sono: " + associatedStreamingNode.getKey() + " Invio a: " + associatedStreamingNode.getServedPeers().get(index).getPeer().getKey() + " Layer: " + chunkLayer.getLayerIndex() + " chunk: " + chunkLayer.getChunkIndex());
					//Commentato anche nel codice originale
				  //if (!associatedStreamingNode.getServedPeers().get(index).getPeer().isLayerNeeded(chunkLayer.getChunkIndex(),chunkLayer.getLayerIndex() ) )
				
					
				  //TODO: da rivedere la condizione	
			   	  if( chunkLayer.getChunkIndex() >  associatedStreamingNode.getServedPeers().get(index).getPeer().getInitChunk())		
				//if (!associatedStreamingNode.getServedPeers().get(index).getPeer().isLayerNeeded(chunkLayer.getChunkIndex(),chunkLayer.getLayerIndex() ) && chunkLayer.getChunkIndex() >  associatedStreamingNode.getServedPeers().get(index).getPeer().getInitChunk())		
					associatedStreamingNode.sendChunkLayer(associatedStreamingNode.getServedPeers().get(index).getPeer(), chunkLayer, this.triggeringTime);
				}				
				
			}
		}
	}

	public ChunkLayer getResourceValue() {
		return this.chunkLayer;
	}

	public void setResourceValue(ChunkLayer resourceValue) {
		this.chunkLayer = resourceValue;
	}


}
