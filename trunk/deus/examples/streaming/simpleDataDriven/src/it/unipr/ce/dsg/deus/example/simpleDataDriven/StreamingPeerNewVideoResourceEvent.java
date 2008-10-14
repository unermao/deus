package it.unipr.ce.dsg.deus.example.simpleDataDriven;
import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.NodeEvent;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.deus.p2p.node.Peer;

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

	private VideoChunk videoChunk = null;
	private float originalTime = 0;
	
	public StreamingPeerNewVideoResourceEvent(String id, Properties params,
			Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
		initialize();
	}

	public void initialize() throws InvalidParamsException {
	}
	
	public Object clone() {
		
		StreamingPeerNewVideoResourceEvent clone = (StreamingPeerNewVideoResourceEvent) super.clone();
		clone.videoChunk = this.videoChunk;
		clone.originalTime = this.originalTime;
		return clone;
	}

	public void run() throws RunException {
		
		getLogger().fine("## new node video resource");
	
		StreamingPeer associatedStreamingNode = (StreamingPeer) associatedNode;
		
		Peer sourcePeer = (Peer) videoChunk.getSourceNode();
			
		/**
		 * Se il nodo che mi ha inviato il pacchetto e' ancora conneso
		 * effettuo le operazioni di invio ai miei forniti.
		 * Altrimenti il pacchetto non viene inoltrato dato che si suppone che a causa
		 * della disconnessione della sorgente non sia arrivato.
		 * 
		 */
		if( Engine.getDefault().getNodes().contains(sourcePeer) )
		{
		
			VideoChunk newVideoChunk = new VideoChunk(videoChunk.getChunkIndex(),videoChunk.getChunkSize());
			
			//Aggiungo la nuova porzione video al nodo
			associatedStreamingNode.addNewVideoResource(newVideoChunk);
			
			//Imposto il nuovo nodo sorgente sulla porzione video
			newVideoChunk.setSourceNode(associatedNode);
				
			//Innesca per i nodi forniti l'evento di aggiornamento risorsa
			for(int index = 0 ; index < associatedStreamingNode.getServedPeers().size(); index++)
			{
				   //getLogger().fine("Sono : " + associatedStreamingNode.getKey() + " Invio a: " + associatedStreamingNode.getServedPeers().get(index).getKey() + " Chunk: " + videoChunk.getChunkIndex());
				   associatedStreamingNode.sendVideoChunk(associatedStreamingNode.getServedPeers().get(index), newVideoChunk, this.triggeringTime);
			}
		}
			
		getLogger().fine("end new node video resource ##");
	}

	public VideoChunk getResourceValue() {
		return videoChunk;
	}

	public void setResourceValue(VideoChunk resourceValue) {
		this.videoChunk = resourceValue;
	}

	public float getOriginalTime() {
		return originalTime;
	}

	public void setOriginalTime(float originalTime) {
		this.originalTime = originalTime;
	}

}
